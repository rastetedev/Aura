package com.raulastete.aura.core.features.record.data

import com.raulastete.aura.core.database.dao.RecordDao
import com.raulastete.aura.core.features.record.Record
import com.raulastete.aura.core.features.record.RecordDataSource
import com.raulastete.aura.core.features.record.data.toEchoWithTopics
import com.raulastete.aura.core.features.record.data.toRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import kotlin.collections.map

class RoomRecordDataSource(
    private val recordDao: RecordDao
) : com.raulastete.aura.core.features.record.RecordDataSource {
    override fun observeRecords(): Flow<List<com.raulastete.aura.core.features.record.Record>> {
        return recordDao
            .observeRecords()
            .map { recordWithTopics ->
                recordWithTopics.map { recordWithTopic ->
                    recordWithTopic.toRecord()
                }
            }
    }

    override fun getRecordsInRange(start: Instant, end: Instant): Flow<List<com.raulastete.aura.core.features.record.Record>> {
        return recordDao
            .getRecordsInRange(start.toEpochMilli(), end.toEpochMilli())
            .map { recordWithTopics ->
                recordWithTopics.map { it.toRecord() }
            }
    }

    override fun observeTopics(): Flow<List<String>> {
        return recordDao
            .observeTopics()
            .map { topicEntities ->
                topicEntities.map { it.topic }
            }
    }

    override fun searchTopics(query: String): Flow<List<String>> {
        return recordDao
            .searchTopics(query)
            .map { topicEntities ->
                topicEntities.map { it.topic }
            }
    }

    override suspend fun insertRecord(record: com.raulastete.aura.core.features.record.Record) {
        recordDao.insertRecordWithTopics(record.toEchoWithTopics())
    }
}