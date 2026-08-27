package com.raulastete.aura.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.features.statistics.GetMoodStatisticsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class StatisticsViewModel(
    private val getMoodStatisticsUseCase: GetMoodStatisticsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsUiState())
    val state: StateFlow<StatisticsUiState> = getMoodStatisticsUseCase()
        .map { stats ->
            StatisticsUiState(
                isLoading = false,
                heatmapDays = stats.heatmapDays.map { day ->
                    HeatmapDayUiModel(
                        date = day.date,
                        mood = day.mood?.let { MoodUi.valueOf(it.name) }
                    )
                },
                moodFrequencies = stats.moodFrequencies.map { freq ->
                    MoodFrequencyUiModel(
                        mood = MoodUi.valueOf(freq.mood.name),
                        percentage = freq.percentage
                    )
                }
            )
        }
        .onStart { _state.update { it.copy(isLoading = true) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatisticsUiState()
        )
}