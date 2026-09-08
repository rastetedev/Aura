package com.raulastete.aura.core.workers

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.raulastete.aura.R
import com.raulastete.aura.core.database.dao.RecordDao
import com.raulastete.aura.core.database.entity.RecordEntity
import com.raulastete.aura.core.database.entity.RecordTopicCrossRef
import com.raulastete.aura.core.database.entity.RecordWithTopics
import com.raulastete.aura.core.database.entity.TopicEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

class ImportWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val recordDao: RecordDao by inject()

    companion object {
        const val KEY_ZIP_URI = "zip_uri"
        private const val BUFFER_SIZE = 8 * 1024
        // Safety limit to prevent zip-slip attacks
        private const val MAX_EXTRACT_SIZE = 500L * 1024 * 1024 // 500 MB
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return buildForegroundInfo(progress = 0, indeterminate = true)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val zipUriString = inputData.getString(KEY_ZIP_URI)
            ?: return@withContext Result.failure(workDataOf("error" to "No ZIP URI provided"))

        try {
            setForeground(buildForegroundInfo(progress = 0, indeterminate = true))

            val zipUri = Uri.parse(zipUriString)

            // 1. Extract ZIP to staging dir
            val stagingDir = File(context.cacheDir, "import_staging_${System.currentTimeMillis()}")
                .also { it.mkdirs() }

            extractZip(zipUri, stagingDir)
            setForeground(buildForegroundInfo(progress = 30))

            // 2. Read records.json
            val jsonFile = File(stagingDir, "records.json")
            if (!jsonFile.exists()) {
                stagingDir.deleteRecursively()
                return@withContext Result.failure(workDataOf("error" to "Invalid backup: records.json not found"))
            }

            val json = Json { ignoreUnknownKeys = true }
            val backupRecords: List<BackupRecord> = json.decodeFromString(jsonFile.readText())
            setForeground(buildForegroundInfo(progress = 50))

            // 3. Copy audio files to internal filesDir
            val audioSrcDir = File(stagingDir, "audio")
            val audioDstDir = context.filesDir
            val audioFileMapping = mutableMapOf<String, String>() // originalName -> newAbsPath

            backupRecords.forEachIndexed { index, backup ->
                val srcAudio = File(audioSrcDir, backup.audioFileName)
                if (srcAudio.exists()) {
                    val destAudio = File(audioDstDir, backup.audioFileName)
                    srcAudio.copyTo(destAudio, overwrite = true)
                    audioFileMapping[backup.audioFileName] = destAudio.absolutePath
                }
                val copyProgress = 50 + ((index + 1).toFloat() / backupRecords.size * 30).toInt()
                setForeground(buildForegroundInfo(progress = copyProgress))
            }

            // 4. Replace all DB data
            val recordsWithTopics = backupRecords.map { backup ->
                val audioPath = audioFileMapping[backup.audioFileName]
                    ?: File(context.filesDir, backup.audioFileName).absolutePath

                RecordWithTopics(
                    record = RecordEntity(
                        recordId = 0, // let Room auto-generate new IDs
                        title = backup.title,
                        mood = backup.mood,
                        recordedAt = backup.recordedAt,
                        note = backup.note,
                        audioFilePath = audioPath,
                        audioPlaybackLength = backup.audioPlaybackLength,
                        audioAmplitudes = backup.audioAmplitudes
                    ),
                    topics = backup.topics.map { TopicEntity(topic = it) }
                )
            }

            recordDao.replaceAllData(recordsWithTopics)
            setForeground(buildForegroundInfo(progress = 95))

            // 5. Cleanup staging
            stagingDir.deleteRecursively()

            // 6. Notify success
            BackupNotificationHelper.notify(
                context,
                BackupNotificationHelper.IMPORT_NOTIFICATION_ID,
                BackupNotificationHelper.buildSuccessNotification(
                    context,
                    R.string.import_success_title,
                    R.string.import_success_message
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.import_toast_success, Toast.LENGTH_LONG).show()
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Import failed")
            BackupNotificationHelper.notify(
                context,
                BackupNotificationHelper.IMPORT_NOTIFICATION_ID,
                BackupNotificationHelper.buildErrorNotification(
                    context,
                    R.string.import_error_title,
                    R.string.import_error_message
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.import_toast_error, Toast.LENGTH_LONG).show()
            }
            Result.failure(workDataOf("error" to e.message))
        }
    }

    private fun buildForegroundInfo(progress: Int, indeterminate: Boolean = false): ForegroundInfo {
        BackupNotificationHelper.createChannel(context)
        val notification = BackupNotificationHelper.buildProgressNotification(
            context = context,
            titleRes = R.string.import_notification_title,
            progress = progress,
            indeterminate = indeterminate
        )
        return ForegroundInfo(
            BackupNotificationHelper.IMPORT_NOTIFICATION_ID,
            notification,
            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    private fun extractZip(zipUri: Uri, destDir: File) {
        val destPath = destDir.canonicalPath
        var totalExtracted = 0L

        context.contentResolver.openInputStream(zipUri)?.use { inputStream ->
            ZipInputStream(inputStream.buffered(BUFFER_SIZE)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    // Zip-slip protection
                    val destFile = File(destDir, entry.name)
                    val destFilePath = destFile.canonicalPath
                    require(destFilePath.startsWith(destPath + File.separator)) {
                        "Zip-slip detected: ${entry.name}"
                    }

                    if (entry.isDirectory) {
                        destFile.mkdirs()
                    } else {
                        destFile.parentFile?.mkdirs()
                        FileOutputStream(destFile).use { fos ->
                            val buffer = ByteArray(BUFFER_SIZE)
                            var len: Int
                            while (zis.read(buffer).also { len = it } > 0) {
                                totalExtracted += len
                                require(totalExtracted <= MAX_EXTRACT_SIZE) {
                                    "ZIP extraction exceeded size limit"
                                }
                                fos.write(buffer, 0, len)
                            }
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } ?: error("Cannot open ZIP stream from URI: $zipUri")
    }
}
