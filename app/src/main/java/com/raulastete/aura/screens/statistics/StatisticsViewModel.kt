package com.raulastete.aura.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulastete.aura.core.features.record.RecordDataSource
import com.raulastete.aura.core.features.statistics.CalculateMoodFrequenciesUseCase
import com.raulastete.aura.core.features.statistics.CalculateMoodHeatMapUseCase
import com.raulastete.aura.core.presentation.model.MoodUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModel(
    private val recordDataSource: RecordDataSource,
    private val calculateMoodHeatMapUseCase: CalculateMoodHeatMapUseCase,
    private val calculateMoodFrequenciesUseCase: CalculateMoodFrequenciesUseCase
) : ViewModel() {

    private val statisticsPeriod = MutableStateFlow(Period.ofMonths(3))

    val state = statisticsPeriod
        .flatMapLatest { period ->
            val endDate = LocalDate.now()
            val startDate = endDate.minus(period)

            val startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endInstant = endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()

            recordDataSource.getRecordsInRange(startInstant, endInstant)
                .map { records ->
                    val heatmapDays = calculateMoodHeatMapUseCase(records, startDate, endDate)
                    val frequencies = calculateMoodFrequenciesUseCase(records)

                    StatisticsUiState(
                        isLoading = false,
                        heatmapDays = heatmapDays.map { day ->
                            HeatmapDayUiModel(
                                date = day.date,
                                mood = day.mood?.let { MoodUi.valueOf(it.name) }
                            )
                        },
                        moodFrequencies = frequencies.map { freq ->
                            MoodFrequencyUiModel(
                                mood = MoodUi.valueOf(freq.mood.name),
                                percentage = freq.percentage
                            )
                        }
                    )
                }
                .onStart { emit(StatisticsUiState(isLoading = true)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = StatisticsUiState()
        )

    fun onPeriodChange(period: Period) {
        statisticsPeriod.value = period
    }
}
