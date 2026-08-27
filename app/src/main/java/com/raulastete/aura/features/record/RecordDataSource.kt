package com.raulastete.aura.features.record

import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface RecordDataSource {
    fun observeRecords(): Flow<List<Record>>
    fun getRecordsInRange(start: Instant, end: Instant): Flow<List<Record>>
    fun observeTopics(): Flow<List<String>>
    fun searchTopics(query: String): Flow<List<String>>
    suspend fun insertRecord(record: Record)
}