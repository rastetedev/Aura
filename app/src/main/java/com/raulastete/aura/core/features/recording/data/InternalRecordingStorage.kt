package com.raulastete.aura.core.features.recording.data

import android.content.Context
import com.raulastete.aura.core.features.recording.RecordingStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.time.Instant
import java.time.temporal.ChronoUnit

class InternalRecordingStorage(
    private val context: Context
) : com.raulastete.aura.core.features.recording.RecordingStorage {

    override suspend fun savePersistently(tempFilePath: String): String? {
        val tempFile = File(tempFilePath)
        if(!tempFile.exists()) {
            Timber.e("The temporary file does not exist.")
            return null
        }

        return withContext(Dispatchers.IO) {
            try {
                val savedFile = generateSavedFile()
                tempFile.copyTo(savedFile)

                savedFile.absolutePath
            } catch(e: IOException) {
                Timber.e(e)
                null
            } finally {
                withContext(NonCancellable) {
                    cleanUpTemporaryFiles()
                }
            }
        }
    }

    override suspend fun cleanUpTemporaryFiles() {
        withContext(Dispatchers.IO) {
            context
                .cacheDir
                .listFiles()
                ?.filter { it.name.startsWith(com.raulastete.aura.core.features.recording.RecordingStorage.TEMP_FILE_PREFIX) }
                ?.forEach { file ->
                    file.delete()
                }
        }
    }

    private fun generateSavedFile(): File {
        val timestamp = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        return File(
            context.filesDir,
            "${com.raulastete.aura.core.features.recording.RecordingStorage.PERSISTENT_FILE_PREFIX}_$timestamp.${com.raulastete.aura.core.features.recording.RecordingStorage.RECORDING_FILE_EXTENSION}"
        )
    }
}