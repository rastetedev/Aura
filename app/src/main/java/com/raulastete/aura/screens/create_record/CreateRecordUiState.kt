package com.raulastete.aura.screens.create_record

import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.PlaybackState
import kotlin.time.Duration

data class CreateRecordUiState(
    val title: String = "",
    val addTopicText: String = "",
    val note: String = "",
    val showMoodSelector: Boolean = true,
    val selectedMood: MoodUi = MoodUi.NEUTRAL,
    val showTopicSuggestions: Boolean = false,
    val mood: MoodUi? = null,
    val searchResults: List<String> = emptyList(),
    val showCreateTopicOption: Boolean = false,
    val canSaveRecord: Boolean = false,
    val playbackAmplitudes: List<Float> = List(32) { 0.3f },
    val playbackTotalDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val durationPlayed: Duration = Duration.ZERO,
) {
    val durationPlayedRatio = (durationPlayed / playbackTotalDuration).toFloat()
}