package com.raulastete.aura.core.workers

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.raulastete.aura.R
import com.raulastete.aura.core.database.dao.RecordDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExportWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val recordDao: RecordDao by inject()

    companion object {
        const val KEY_EXPORT_URI = "export_uri"
        private const val BUFFER_SIZE = 8 * 1024
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return buildForegroundInfo(progress = 0, indeterminate = true)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            setForeground(buildForegroundInfo(progress = 0, indeterminate = true))

            // 1. Fetch all records from DB
            val records = recordDao.getAllRecords()
            setForeground(buildForegroundInfo(progress = 10))

            // 2. Serialize records to JSON
            val json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
            val recordsJson = json.encodeToString(
                records.map { recordWithTopics ->
                    BackupRecord(
                        recordId = recordWithTopics.record.recordId,
                        title = recordWithTopics.record.title,
                        mood = recordWithTopics.record.mood,
                        recordedAt = recordWithTopics.record.recordedAt,
                        note = recordWithTopics.record.note,
                        audioFileName = File(recordWithTopics.record.audioFilePath).name,
                        audioPlaybackLength = recordWithTopics.record.audioPlaybackLength,
                        audioAmplitudes = recordWithTopics.record.audioAmplitudes,
                        topics = recordWithTopics.topics.map { it.topic }
                    )
                }
            )
            setForeground(buildForegroundInfo(progress = 25))

            // 3. Create staging directory in cache
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val stagingDir = File(context.cacheDir, "export_staging_$timestamp").also { it.mkdirs() }

            // 4. Write records.json
            File(stagingDir, "records.json").writeText(recordsJson)
            setForeground(buildForegroundInfo(progress = 40))

            // 5. Copy audio files
            val audioDir = File(stagingDir, "audio").also { it.mkdirs() }
            records.forEachIndexed { index, recordWithTopics ->
                val srcFile = File(recordWithTopics.record.audioFilePath)
                if (srcFile.exists()) {
                    srcFile.copyTo(File(audioDir, srcFile.name), overwrite = true)
                }
                val copyProgress = 40 + ((index + 1).toFloat() / records.size * 40).toInt()
                setForeground(buildForegroundInfo(progress = copyProgress))
            }

            // 6. Create ZIP in external Downloads-accessible cache dir
            val exportDir = File(context.getExternalFilesDir(null), "backups").also { it.mkdirs() }
            val zipFile = File(exportDir, "aura_backup_$timestamp.zip")
            zipDirectory(stagingDir, zipFile)
            setForeground(buildForegroundInfo(progress = 95))

            // 7. Cleanup staging
            stagingDir.deleteRecursively()

            // 8. Build shareable URI via FileProvider
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                zipFile
            )

            // 9. Launch share sheet from application context
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(shareIntent, context.getString(R.string.export_share_title))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            // 10. Show success notification + toast
            BackupNotificationHelper.notify(
                context,
                BackupNotificationHelper.EXPORT_NOTIFICATION_ID,
                BackupNotificationHelper.buildSuccessNotification(
                    context,
                    R.string.export_success_title,
                    R.string.export_success_message
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.export_toast_success, Toast.LENGTH_LONG).show()
            }

            Result.success(workDataOf(KEY_EXPORT_URI to uri.toString()))
        } catch (e: Exception) {
            Timber.e(e, "Export failed")
            BackupNotificationHelper.notify(
                context,
                BackupNotificationHelper.EXPORT_NOTIFICATION_ID,
                BackupNotificationHelper.buildErrorNotification(
                    context,
                    R.string.export_error_title,
                    R.string.export_error_message
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.export_toast_error, Toast.LENGTH_LONG).show()
            }
            Result.failure(workDataOf("error" to e.message))
        }
    }

    private fun buildForegroundInfo(progress: Int, indeterminate: Boolean = false): ForegroundInfo {
        BackupNotificationHelper.createChannel(context)
        val notification = BackupNotificationHelper.buildProgressNotification(
            context = context,
            titleRes = R.string.export_notification_title,
            progress = progress,
            indeterminate = indeterminate
        )
        return ForegroundInfo(
            BackupNotificationHelper.EXPORT_NOTIFICATION_ID,
            notification,
            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    private fun zipDirectory(sourceDir: File, zipFile: File) {
        ZipOutputStream(FileOutputStream(zipFile).buffered(BUFFER_SIZE)).use { zos ->
            sourceDir.walkTopDown()
                .filter { it.isFile }
                .forEach { file ->
                    val entryName = file.relativeTo(sourceDir).path
                    zos.putNextEntry(ZipEntry(entryName))
                    FileInputStream(file).buffered(BUFFER_SIZE).use { fis ->
                        fis.copyTo(zos, BUFFER_SIZE)
                    }
                    zos.closeEntry()
                }
        }
    }
}
