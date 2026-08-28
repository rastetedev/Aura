package com.raulastete.aura.screens.record_list

import com.raulastete.aura.core.features.recording.RecordingDetails

sealed interface RecordListEvent {

    data object RequestAudioPermission: RecordListEvent
    data object RecordingTooShort: RecordListEvent
    data class OnDoneRecording(val details: com.raulastete.aura.core.features.recording.RecordingDetails): RecordListEvent
}