package com.raulastete.aura.screens.record_list

sealed interface RecordListAction {

    data object OnMoodChipClick : RecordListAction
    data object OnTopicChipClick : RecordListAction
    data object OnFabClick : RecordListAction
    data object OnFabLongClick : RecordListAction
    data class OnRemoveFilters(val recordFilter: RecordFilter) : RecordListAction
}