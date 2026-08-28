package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.RecordDataSource
import com.raulastete.aura.core.features.record.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek

class GetMoodStatisticsUseCase(
    private val recordDataSource: com.raulastete.aura.core.features.record.RecordDataSource,
    private val calculateMoodHeatMapByRecords: com.raulastete.aura.core.features.statistics.CalculateMoodHeatMapByRecords,
    private val calculateMoodFrequencyByRecords: com.raulastete.aura.core.features.statistics.CalculateMoodFrequencyByRecords
) {
    operator fun invoke(): Flow<com.raulastete.aura.core.features.statistics.MoodStatistics> {
        val now = Instant.now()
        val threeMonthsAgo = now.minus(90, ChronoUnit.DAYS)

        return recordDataSource.getRecordsInRange(threeMonthsAgo, now).map { records ->

            val heatmapDays = calculateMoodHeatMapByRecords(records)
            val frequencies = calculateMoodFrequencyByRecords(records)

            com.raulastete.aura.core.features.statistics.MoodStatistics(
                heatmapDays,
                frequencies
            )
        }
    }
}