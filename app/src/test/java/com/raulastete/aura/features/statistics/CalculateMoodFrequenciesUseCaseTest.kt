package com.raulastete.aura.features.statistics

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.statistics.CalculateMoodFrequenciesUseCase
import com.raulastete.aura.features.record.recordTemplate
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CalculateMoodFrequenciesUseCaseTest {

    private lateinit var calculateMoodFrequenciesUseCase: CalculateMoodFrequenciesUseCase

    @Before
    fun setUp() {
        calculateMoodFrequenciesUseCase = CalculateMoodFrequenciesUseCase()
    }

    @Test
    fun `invoke should return 100 percentage for mood when all records have same mood`() {
        val records = (1..10).map { recordTemplate.copy() }
        val frequencies = calculateMoodFrequenciesUseCase(records)
        val templateMoodPercentage = frequencies.first { it.mood == recordTemplate.mood }.percentage
        Assert.assertEquals(1f, templateMoodPercentage)
    }

    @Test
    fun `invoke should return 0 percentage for every mood when no records are provided`() {
        val frequencies = calculateMoodFrequenciesUseCase(emptyList())
        Mood.entries.forEach { mood ->
            Assert.assertEquals(0f, frequencies.first { mood == it.mood }.percentage)
        }
    }

    @Test
    fun `invoke should return same percentage for every mood when there are the same quantity of records with each mood`() {
        val moods = Mood.entries
        val recordsPerMood = 10
        val records = moods.flatMap { mood ->
            List(recordsPerMood) { recordTemplate.copy(mood = mood) }
        }
        val expectedPercentage = 1f / moods.size
        val frequencies = calculateMoodFrequenciesUseCase(records)

        frequencies.forEach { frequency ->
            Assert.assertEquals(
                "Mood ${frequency.mood} should have $expectedPercentage percentage",
                expectedPercentage,
                frequency.percentage,
                0.001f // Use a delta for float comparisons
            )
        }
    }
}