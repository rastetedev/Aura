package com.raulastete.aura.core.database.entity

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Junction
import androidx.room3.Relation

@Entity(
    primaryKeys = ["recordId", "topic"],
)
data class RecordTopicCrossRef(
    val recordId: Int,
    val topic: String
)

data class RecordWithTopics(
    @Embedded val record: RecordEntity,
    @Relation(
        parentColumns = ["recordId"],
        entity = TopicEntity::class,
        entityColumns = ["topic"],
        associateBy = Junction(RecordTopicCrossRef::class)
    )
    val topics: List<TopicEntity>
)