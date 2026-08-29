package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.Record
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class CalculateMoodHeatMapUseCase {

    operator fun invoke(
        records: List<Record>,
        startDate: LocalDate,
        endDate: LocalDate,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): List<HeatmapDay> {

        val latestMoodByDay = records
            .groupBy { LocalDate.ofInstant(it.recordedAt, zoneId) }
            .mapValues { (_, records) ->
                records.maxBy { it.recordedAt }.mood
            }

        val adjustedStartDate = startDate
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        return adjustedStartDate.datesUntil(endDate.plusDays(1))
            .map { date ->
                HeatmapDay(date, latestMoodByDay[date])
            }
            .toList()
    }
}