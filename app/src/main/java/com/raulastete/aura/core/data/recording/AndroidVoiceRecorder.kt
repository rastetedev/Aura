package com.raulastete.aura.core.data.recording

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.raulastete.aura.core.domain.recording.RecordingDetails
import com.raulastete.aura.core.domain.recording.VoiceRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

class AndroidVoiceRecorder(
    private val context: Context,
    private val applicationScope: CoroutineScope
) : VoiceRecorder {

    companion object {
        private const val MAX_AMPLITUDE_VALUE = 26_000L
    }

    private val singleThreadDispatcher = Dispatchers.Default.limitedParallelism(1)

    private val _recordingDetails = MutableStateFlow(RecordingDetails())
    private var recorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private val amplitudes = mutableListOf<Float>()
    private var isPaused: Boolean = false

    private var durationJob: Job? = null
    private var amplitudeJob: Job? = null

    private lateinit var tempFile: File


    override val recordingDetails = _recordingDetails.asStateFlow()

    override fun start() {
        if (isRecording) return

        try {
            resetSession()
            tempFile = generateTempFile()

            recorder = newMediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128 * 1000)
                setAudioSamplingRate(44100)
                setOutputFile(tempFile.absolutePath)

                prepare()
                start()
            }

            isRecording = true
            isPaused = false
            startTrackingDuration()
            startTrackingAmplitudes()
        } catch (e: IOException) {
            Timber.e(e, "Failed to start recording")
            recorder?.release()
            recorder = null
        }
    }

    override fun pause() {
        if (isRecording.not() || isPaused) return

        isPaused = true
        recorder?.pause()
        durationJob?.cancel()
        amplitudeJob?.cancel()
    }

    override fun stop() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop recording")
        } finally {
            _recordingDetails.update {
                it.copy(
                    amplitudes = amplitudes.toList(),
                    filePath = tempFile.absolutePath
                )
            }
            cleanup()
        }
    }

    override fun resume() {
        if (isRecording.not() || isPaused.not()) return

        recorder?.resume()
        isPaused = false

        startTrackingDuration()
        startTrackingAmplitudes()
    }

    override fun cancel() {
        stop()
        resetSession()
    }

    private fun resetSession() {
        _recordingDetails.update { RecordingDetails() }
        applicationScope.launch(singleThreadDispatcher) {
            amplitudes.clear()
            cleanup()
        }
    }

    private fun cleanup() {
        Timber.d("Cleaning up voice recorder resources")
        recorder = null
        isRecording = false
        isPaused = false
        durationJob?.cancel()
        amplitudeJob?.cancel()
    }

    private fun newMediaRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
    }

    private fun generateTempFile(): File {
        val uniqueId = Uuid.random().toString()
        return File(
            context.cacheDir,
            "temp_recording_$uniqueId.mp4"
        )
    }

    private fun startTrackingDuration() {
        durationJob = applicationScope.launch {
            var lastTime = System.currentTimeMillis()
            while (isRecording && isPaused.not()) {
                delay(10.milliseconds)
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastTime
                _recordingDetails.update {
                    it.copy(duration = it.duration + elapsedTime.milliseconds)
                }
                lastTime = System.currentTimeMillis()
            }
        }
    }

    private fun startTrackingAmplitudes() {
        amplitudeJob = applicationScope.launch {
            while (isRecording) {
                val amplitude = getAmplitude()
                withContext(singleThreadDispatcher){
                    amplitudes.add(amplitude)
                }
                delay(100.milliseconds)
            }
        }
    }

    private fun getAmplitude(): Float {
        return if (isRecording) {
            try {
                val maxAmplitude = recorder?.maxAmplitude ?: 0
                val amplitudeRatio = maxAmplitude.takeIf { it > 0f }?.run {
                    (this / MAX_AMPLITUDE_VALUE.toFloat()).coerceIn(0f, 1f)
                }
                amplitudeRatio ?: 0f
            } catch (e: Exception) {
                Timber.e(e, "Failed to retrieve current amplitude")
                0f
            }
        } else 0f
    }
}