package com.raulastete.aura.screens.record_list

data class RecordListUiState(
    val hasRecords: Boolean = false,
    val activeFilter: RecordFilter? = null,
    val isLoadingData: Boolean = false
)

enum class RecordFilter {
    MOOD, TOPIC
}