package com.raulastete.aura.features.statistics.domain

import com.raulastete.aura.features.record.Mood
import com.raulastete.aura.features.record.Record
import com.raulastete.aura.features.record.RecordDataSource
import com.raulastete.aura.features.statistics.GetMoodStatisticsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes

class GetMoodStatisticsUseCaseTest {

    private lateinit var recordDataSource: RecordDataSource
    private lateinit var getMoodStatisticsUseCase: GetMoodStatisticsUseCase

    @Before
    fun setUp() {
        recordDataSource = mockk()
        getMoodStatisticsUseCase = GetMoodStatisticsUseCase(recordDataSource)
    }

    @Test
    fun `invoke should calculate statistics correctly`() = runTest {
        val now = Instant.now()
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)
        
        val records = listOf(
            Record(
                mood = Mood.EXCITED,
                title = "Happy day",
                note = null,
                topics = emptyList(),
                audioFilePath = "",
                audioPlaybackLength = 1.minutes,
                audioAmplitudes = emptyList(),
                recordedAt = now,
                id = 1
            ),
            Record(
                mood = Mood.SAD,
                title = "Sad day",
                note = null,
                topics = emptyList(),
                audioFilePath = "",
                audioPlaybackLength = 1.minutes,
                audioAmplitudes = emptyList(),
                recordedAt = now.minus(1, ChronoUnit.DAYS),
                id = 2
            )
        )

        every { recordDataSource.getRecordsInRange(any(), any()) } returns flowOf(records)

        getMoodStatisticsUseCase().collect { stats ->
            // Check frequencies
            val excitedFreq = stats.moodFrequencies.find { it.mood == Mood.EXCITED }
            val sadFreq = stats.moodFrequencies.find { it.mood == Mood.SAD }
            
            assertEquals(1, excitedFreq?.count)
            assertEquals(1, sadFreq?.count)
            assertEquals(0.5f, excitedFreq?.percentage)
            assertEquals(0.5f, sadFreq?.percentage)

            // Check heatmap
            val todayHeatmap = stats.heatmapDays.find { it.date == today }
            val yesterdayHeatmap = stats.heatmapDays.find { it.date == today.minusDays(1) }
            
            assertEquals(Mood.EXCITED, todayHeatmap?.mood)
            assertEquals(Mood.SAD, yesterdayHeatmap?.mood)
        }
    }
}