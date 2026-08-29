package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.Mood
import java.time.LocalDate

data class HeatmapDay(
    val date: LocalDate,
    val mood: Mood?
)

data class MoodFrequency(
    val mood: Mood,
    val count: Int,
    val percentage: Float
)