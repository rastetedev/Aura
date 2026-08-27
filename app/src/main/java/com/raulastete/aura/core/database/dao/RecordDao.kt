package com.raulastete.aura.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.raulastete.aura.core.database.entity.RecordEntity
import com.raulastete.aura.core.database.entity.RecordTopicCrossRef
import com.raulastete.aura.core.database.entity.RecordWithTopics
import com.raulastete.aura.core.database.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Query("SELECT * FROM RecordEntity ORDER BY recordedAt DESC")
    fun observeRecords(): Flow<List<RecordWithTopics>>

    @Query("SELECT * FROM TopicEntity ORDER BY topic ASC")
    fun observeTopics(): Flow<List<TopicEntity>>

    @Query("""
        SELECT *
        FROM TopicEntity
        WHERE topic LIKE "%" || :query || "%"
        ORDER BY topic ASC
    """)
    fun searchTopics(query: String): Flow<List<TopicEntity>>

    @Insert
    suspend fun insertRecord(recordEntity: RecordEntity): Long

    @Query("SELECT * FROM RecordEntity WHERE recordedAt BETWEEN :start AND :end ORDER BY recordedAt DESC")
    fun getRecordsInRange(start: Long, end: Long): Flow<List<RecordWithTopics>>

    @Upsert
    suspend fun upsertTopic(topicEntity: TopicEntity)

    @Insert
    suspend fun insertRecordTopicCrossRef(crossRef: RecordTopicCrossRef)

    @Transaction
    suspend fun insertRecordWithTopics(recordWithTopics: RecordWithTopics) {
        val echoId = insertRecord(recordWithTopics.record)

        recordWithTopics.topics.forEach { topic ->
            upsertTopic(topic)
            insertRecordTopicCrossRef(
                crossRef = RecordTopicCrossRef(
                    recordId = echoId.toInt(),
                    topic = topic.topic
                )
            )
        }
    }
}