package com.raulastete.aura.core.presentation.model

import com.raulastete.aura.core.presentation.util.string.formatHHmm
import com.raulastete.aura.features.record.Record
import java.time.Instant
import kotlin.time.Duration

data class RecordUi(
    val id: Int,
    val title: String,
    val mood: MoodUi,
    val recordedAt: Instant,
    val note: String?,
    val topics: List<String>,
    val amplitudes: List<Float>,
    val playbackTotalDuration : Duration = Duration.ZERO,
    val playbackCurrentDuration : Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val audioFilePath: String,
){
    val formattedRecordedAt = recordedAt.formatHHmm()
    val playbackRatio = playbackCurrentDuration.div(playbackTotalDuration).toFloat()
}

fun Record.toRecordUi(
    currentPlaybackDuration: Duration = Duration.ZERO,
    playbackState: PlaybackState = PlaybackState.IDLE
): RecordUi {
    return RecordUi(
        id = id!!,
        title = title,
        mood = MoodUi.valueOf(mood.name),
        recordedAt = recordedAt,
        note = note,
        topics = topics,
        amplitudes = audioAmplitudes,
        playbackTotalDuration = audioPlaybackLength,
        audioFilePath = audioFilePath,
        playbackCurrentDuration = currentPlaybackDuration,
        playbackState = playbackState
    )
}