package com.raulastete.aura.core.features.recording

import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration.Companion.seconds

interface VoiceRecorder {

    val recordingDetails: StateFlow<RecordingDetails>
    fun start()
    fun pause()
    fun stop()
    fun resume()
    fun cancel()

    companion object {
        val MIN_RECORD_DURATION = 1.5.seconds
    }
}