package com.raulastete.aura.screens.create_record

sealed interface CreateRecordEvent {
    data object FailedToSaveFile: CreateRecordEvent
    data object RecordSuccessfullySaved: CreateRecordEvent
}