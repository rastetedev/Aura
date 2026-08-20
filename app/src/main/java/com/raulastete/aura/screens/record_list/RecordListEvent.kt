package com.raulastete.aura.screens.record_list

sealed interface RecordListEvent {

    data object RequestAudioPermission: RecordListEvent
}