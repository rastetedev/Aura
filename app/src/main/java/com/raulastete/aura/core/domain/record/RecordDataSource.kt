package com.raulastete.aura.core.domain.record

import kotlinx.coroutines.flow.Flow

interface RecordDataSource {
    fun observeRecords(): Flow<List<Record>>
    fun observeTopics(): Flow<List<String>>
    fun searchTopics(query: String): Flow<List<String>>
    suspend fun insertRecord(record: Record)
}