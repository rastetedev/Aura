package com.raulastete.aura.screens.statistics

import com.raulastete.aura.core.presentation.model.MoodUi
import java.time.LocalDate

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val heatmapDays: List<HeatmapDayUiModel> = emptyList(),
    val moodFrequencies: List<MoodFrequencyUiModel> = emptyList()
)

data class HeatmapDayUiModel(
    val date: LocalDate,
    val mood: MoodUi?,
)

data class MoodFrequencyUiModel(
    val mood: MoodUi,
    val percentage: Int
)

