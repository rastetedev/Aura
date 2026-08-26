package com.raulastete.aura.screens.create_record

import com.raulastete.aura.core.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.PlaybackState
import kotlin.time.Duration

data class CreateRecordUiState(
    val form: FormUiState = FormUiState(),
    val moodSheerUiState: MoodSheerUiState = MoodSheerUiState(),
    val playerUiState: PlayerUiState = PlayerUiState(),
    val topicPopupUiState: TopicPopupUiState = TopicPopupUiState(),
    val showConfirmLeaveDialog: Boolean = false
)

data class FormUiState(
    val title: String = "",
    val topics: List<String> = listOf(),
    val note: String = "",
    val mood: MoodUi? = null,
) {
    val canSaveRecord = title.isNotBlank() && mood != null
}

data class TopicPopupUiState(
    val showTopicSuggestions: Boolean = false,
    val addTopicText: String = "",
    val searchResults: List<Selectable<String>> = emptyList(),
    val showCreateTopicOption: Boolean = true
)

data class PlayerUiState(
    val playbackAmplitudes: List<Float> = emptyList(),
    val playbackTotalDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val durationPlayed: Duration = Duration.ZERO,
) {
    val durationPlayedRatio = (durationPlayed / playbackTotalDuration).toFloat()
}

data class MoodSheerUiState(
    val selectedMood: MoodUi = MoodUi.NEUTRAL,
    val showMoodSelector: Boolean = true,
)