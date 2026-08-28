package com.raulastete.aura.core.features.record.data

import com.raulastete.aura.core.database.entity.RecordEntity
import com.raulastete.aura.core.database.entity.RecordWithTopics
import com.raulastete.aura.core.database.entity.TopicEntity
import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.record.Record
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

fun RecordWithTopics.toRecord(): com.raulastete.aura.core.features.record.Record {
    return com.raulastete.aura.core.features.record.Record(
        mood = com.raulastete.aura.core.features.record.Mood.valueOf(record.mood),
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

fun com.raulastete.aura.core.features.record.Record.toEchoWithTopics(): RecordWithTopics {
    return RecordWithTopics(
        record = RecordEntity(
            recordId = id ?: 0,
            title = title,
            mood = mood.name,
            recordedAt = recordedAt.toEpochMilli(),
            note = note,
            audioAmplitudes = audioAmplitudes,
            audioFilePath = audioFilePath,
            audioPlaybackLength = audioPlaybackLength.inWholeMilliseconds
        ),
        topics = topics.map { TopicEntity(it) }
    )
}