package com.raulastete.aura.core.features.record.data

import com.raulastete.aura.core.database.dao.RecordDao
import com.raulastete.aura.core.features.record.Record
import com.raulastete.aura.core.features.record.RecordDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import kotlin.collections.map

class RoomRecordDataSource(
    private val recordDao: RecordDao
) : RecordDataSource {
    override fun observeRecords(): Flow<List<Record>> {
        return recordDao
            .observeRecords()
            .map { recordWithTopics ->
                recordWithTopics.map { recordWithTopic ->
                    recordWithTopic.toRecord()
                }
            }
    }

    override fun getRecordsInRange(start: Instant, end: Instant): Flow<List<Record>> {
        return recordDao
            .getRecordsInRange(start.toEpochMilli(), end.toEpochMilli())
            .map { recordWithTopics ->
                recordWithTopics.map { it.toRecord() }
            }
    }

    override fun searchRecords(query: String): Flow<List<Record>> {
        return recordDao
            .searchRecords(query)
            .map { recordWithTopics ->
                recordWithTopics.map { it.toRecord() }
            }
    }

    override fun searchRecordsInRange(query: String, start: Instant, end: Instant): Flow<List<Record>> {
        return recordDao
            .searchRecordsInRange(query, start.toEpochMilli(), end.toEpochMilli())
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

    override suspend fun insertRecord(record: Record) {
        recordDao.insertRecordWithTopics(record.toEchoWithTopics())
    }
}
