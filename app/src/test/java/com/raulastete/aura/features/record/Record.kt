package com.raulastete.aura.features.record

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.record.Record
import java.time.Instant
import kotlin.time.Duration

val recordTemplate =
    Record(
        mood = Mood.SAD,
        title = "Fake Note",
        note = "Fake Note",
        topics = emptyList(),
        audioFilePath = "",
        audioPlaybackLength = Duration.ZERO,
        audioAmplitudes = emptyList(),
        recordedAt = Instant.now()
    )