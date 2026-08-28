package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.Mood
import java.time.LocalDate

data class HeatmapDay(
    val date: LocalDate,
    val mood: com.raulastete.aura.core.features.record.Mood?
)

data class MoodFrequency(
    val mood: com.raulastete.aura.core.features.record.Mood,
    val count: Int,
    val percentage: Float
)

data class MoodStatistics(
    val heatmapDays: List<com.raulastete.aura.core.features.statistics.HeatmapDay>,
    val moodFrequencies: List<com.raulastete.aura.core.features.statistics.MoodFrequency>
)