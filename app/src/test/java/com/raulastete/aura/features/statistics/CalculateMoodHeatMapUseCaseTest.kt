package com.raulastete.aura.features.statistics

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.statistics.CalculateMoodHeatMapUseCase
import com.raulastete.aura.features.record.recordTemplate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class CalculateMoodHeatMapUseCaseTest {

    private lateinit var useCase: CalculateMoodHeatMapUseCase

    @Before
    fun setUp() {
        useCase = CalculateMoodHeatMapUseCase()
    }

    @Test
    fun `invoke should return days starting from Monday of startDate`() {
        val startDate = LocalDate.of(2023, 10, 25) // Wednesday
        val endDate = LocalDate.of(2023, 10, 27) // Friday
        val expectedStartDate = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) // Oct 23
        
        val result = useCase(emptyList(), startDate, endDate)
        
        Assert.assertEquals(expectedStartDate, result.first().date)
        Assert.assertEquals(endDate, result.last().date)
    }

    @Test
    fun `invoke should map records to heatmap days correctly`() {
        val date = LocalDate.of(2023, 10, 25)
        val instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val records = listOf(recordTemplate.copy(recordedAt = instant, mood = Mood.EXCITED))
        
        val result = useCase(records, date, date)
        
        val dayResult = result.find { it.date == date }
        Assert.assertEquals(Mood.EXCITED, dayResult?.mood)
    }
}
