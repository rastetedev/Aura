package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.Record
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

class CalculateMoodHeatMapByRecords {

    operator fun invoke(records: List<com.raulastete.aura.core.features.record.Record>): List<com.raulastete.aura.core.features.statistics.HeatmapDay> {
        val zoneId = ZoneId.systemDefault()

        val recordsByDate = records.groupBy {
            LocalDate.ofInstant(it.recordedAt, zoneId)
        }

        val endDate = LocalDate.now(zoneId)
        // Start from the Monday of the week that was 3 months ago to keep the grid aligned
        val startDate =
            endDate.minusMonths(3).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val totalDays = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1)).toInt()

        val heatmapDays = (0 until totalDays).map { daysToAdd ->
            val date = startDate.plusDays(daysToAdd.toLong())
            val mood = recordsByDate[date]?.maxByOrNull { it.recordedAt }?.mood
            com.raulastete.aura.core.features.statistics.HeatmapDay(date, mood)
        }

        return heatmapDays
    }
}