package com.raulastete.aura.screens.create_record

import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.TrackSizeInfo

sealed interface CreateRecordAction {
    data object OnNavigateBackClick : CreateRecordAction
    data class OnTitleTextChange(val text: String) : CreateRecordAction
    data class OnAddTopicTextChange(val text: String) : CreateRecordAction
    data class OnNoteTextChange(val text: String) : CreateRecordAction
    data object OnSelectMoodClick : CreateRecordAction
    data object OnDismissMoodSelector : CreateRecordAction
    data class OnMoodClick(val moodUi: MoodUi) : CreateRecordAction
    data object OnConfirmMood : CreateRecordAction
    data class OnTopicClick(val topic: String) : CreateRecordAction
    data object OnDismissTopicSuggestions : CreateRecordAction
    data object OnCancelClick : CreateRecordAction
    data object OnSaveClick : CreateRecordAction
    data object OnPlayAudioClick : CreateRecordAction
    data object OnPauseAudioClick : CreateRecordAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : CreateRecordAction
    data class OnRemoveTopicClick(val topic: String) : CreateRecordAction
}