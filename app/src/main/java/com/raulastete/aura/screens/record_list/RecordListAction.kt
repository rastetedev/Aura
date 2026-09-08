package com.raulastete.aura.screens.record_list

import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.TrackSizeInfo
import java.time.LocalDate

sealed interface RecordListAction {

    data class OnFilterByMoodToggle(val mood: MoodUi) : RecordListAction
    data class OnFilterByTopicToggle(val topic: String) : RecordListAction
    data object OnRecordFabClick : RecordListAction
    data object OnRecordButtonLongClick : RecordListAction
    data object OnRequestPermissionQuickRecording : RecordListAction
    data class OnRemoveFilters(val recordFilterDropdown: RecordFilterDropdown) : RecordListAction
    data class OnPlayClick(val recordId: Int) : RecordListAction
    data object OnPauseRecordingClick : RecordListAction
    data object OnResumeRecordingClick : RecordListAction
    data object OnCompleteRecording : RecordListAction
    data object OnPauseAudioClick : RecordListAction
    data object OnCancelRecording: RecordListAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : RecordListAction
    data object OnAudioPermissionGranted : RecordListAction

    // Search
    data class OnSearchQueryChange(val query: String) : RecordListAction

    // Date range filter
    data class OnDateRangeSelected(val startDate: LocalDate, val endDate: LocalDate) : RecordListAction
    data object OnClearDateRange : RecordListAction
}
