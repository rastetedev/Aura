package com.raulastete.aura.features.statistics

import com.raulastete.aura.features.record.RecordDataSource
import com.raulastete.aura.features.record.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek

class GetMoodStatisticsUseCase(
    private val recordDataSource: RecordDataSource,
    private val calculateMoodHeatMapByRecords: CalculateMoodHeatMapByRecords,
    private val calculateMoodFrequencyByRecords: CalculateMoodFrequencyByRecords
) {
    operator fun invoke(): Flow<MoodStatistics> {
        val now = Instant.now()
        val threeMonthsAgo = now.minus(90, ChronoUnit.DAYS)

        return recordDataSource.getRecordsInRange(threeMonthsAgo, now).map { records ->

            val heatmapDays = calculateMoodHeatMapByRecords(records)
            val frequencies = calculateMoodFrequencyByRecords(records)

            MoodStatistics(heatmapDays, frequencies)
        }
    }
}