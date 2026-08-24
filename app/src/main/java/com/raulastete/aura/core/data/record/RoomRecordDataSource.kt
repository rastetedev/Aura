package com.raulastete.aura.core.data.record

import com.raulastete.aura.core.database.dao.RecordDao
import com.raulastete.aura.core.domain.record.Record
import com.raulastete.aura.core.domain.record.RecordDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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