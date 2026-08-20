package com.raulastete.aura.screens.record_list

import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.TrackSizeInfo

sealed interface RecordListAction {

    data class OnFilterByMoodToggle(val mood: MoodUi) : RecordListAction
    data class OnFilterByTopicToggle(val topic: String) : RecordListAction
    data object OnFabClick : RecordListAction
    data object OnFabLongClick : RecordListAction
    data class OnRemoveFilters(val recordFilterDropdown: RecordFilterDropdown) : RecordListAction
    data class OnPlayClick(val recordId: Int) : RecordListAction
    data object OnPauseClick : RecordListAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : RecordListAction
    data object OnAudioPermissionGranted : RecordListAction
}