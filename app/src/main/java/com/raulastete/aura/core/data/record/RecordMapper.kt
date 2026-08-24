package com.raulastete.aura.core.data.record

import com.raulastete.aura.core.database.entity.RecordEntity
import com.raulastete.aura.core.database.entity.RecordWithTopics
import com.raulastete.aura.core.database.entity.TopicEntity
import com.raulastete.aura.core.domain.record.Record
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

fun RecordWithTopics.toRecord(): Record {
    return Record(
        mood = record.mood,
        title = record.title,
        note = record.note,
        topics = topics.map { it.topic },
        audioFilePath = record.audioFilePath,
        audioPlaybackLength = record.audioPlaybackLength.milliseconds,
        audioAmplitudes = record.audioAmplitudes,
        recordedAt = Instant.ofEpochMilli(record.recordedAt),
        id = record.recordId
    )
}

fun Record.toEchoWithTopics(): RecordWithTopics {
    return RecordWithTopics(
        record = RecordEntity(
            recordId = id ?: 0,
            title = title,
            mood = mood,
            recordedAt = recordedAt.toEpochMilli(),
            note = note,
            audioAmplitudes = audioAmplitudes,
            audioFilePath = audioFilePath,
            audioPlaybackLength = audioPlaybackLength.inWholeMilliseconds
        ),
        topics = topics.map { TopicEntity(it) }
    )
}