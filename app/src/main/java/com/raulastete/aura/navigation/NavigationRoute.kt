package com.raulastete.aura.navigation

import com.raulastete.aura.core.domain.recording.RecordingDetails
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds

sealed interface NavigationRoute {
    @Serializable
    data class RecordList(
        val startRecording: Boolean
    ) : NavigationRoute

    @Serializable
    data class CreateRecord(
        val recordingPath: String,
        val duration: Long,
        val amplitudes: String
    ) : NavigationRoute

    @Serializable
    data object Settings : NavigationRoute
}

fun RecordingDetails.toCreateRecordRoute(): NavigationRoute.CreateRecord {
    return NavigationRoute.CreateRecord(
        recordingPath = this.filePath ?: throw IllegalArgumentException(
            "Recording path can't be null."
        ),
        duration = this.duration.inWholeMilliseconds,
        amplitudes = this.amplitudes.joinToString(";")
    )
}

fun NavigationRoute.CreateRecord.toRecordingDetails(): RecordingDetails {
    return RecordingDetails(
        duration = this.duration.milliseconds,
        amplitudes = this.amplitudes.split(";").map { it.toFloat() },
        filePath = recordingPath
    )
}