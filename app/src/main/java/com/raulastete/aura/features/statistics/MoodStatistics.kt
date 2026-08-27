package com.raulastete.aura.features.statistics

import com.raulastete.aura.features.record.Mood
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

data class MoodStatistics(
    val heatmapDays: List<HeatmapDay>,
    val moodFrequencies: List<MoodFrequency>
)