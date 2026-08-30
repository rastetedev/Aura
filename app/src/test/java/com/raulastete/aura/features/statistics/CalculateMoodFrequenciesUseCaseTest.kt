package com.raulastete.aura.features.statistics

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.statistics.CalculateMoodFrequenciesUseCase
import com.raulastete.aura.features.record.recordTemplate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CalculateMoodFrequenciesUseCaseTest : FunSpec({

    val target = CalculateMoodFrequenciesUseCase()

    test("invoke should return 100 percentage for mood when all records have same mood") {
        val records = (1..10).map { recordTemplate.copy() }
        val frequencies = target(records)
        val templateMoodFrequency = frequencies.first { it.mood == recordTemplate.mood }

        templateMoodFrequency.percentage shouldBe 1.0
    }

    test("invoke should return 0 percentage for every mood when no records are provided") {
        val frequencies = target(emptyList())
        Mood.entries.forEach { mood ->
            val frequency = frequencies.first { it.mood == mood }
            frequency.percentage shouldBe 0.0
        }
    }

    repeat(5) {
        val recordQuantity = (100..200).random()
        val randomCounts = generateRandomCounts(recordQuantity)

        test("invoke should return correct percentages that sum to 100 percent") {
            val records = Mood.entries.flatMapIndexed { index, mood ->
                List(randomCounts[index]) { recordTemplate.copy(mood = mood) }
            }

            val frequencies = target(records)
            val sum = frequencies.sumOf { it.percentage }
            sum shouldBe 1.0
        }
    }
})

private fun generateRandomCounts(total: Int): List<Int> {
    val counts = mutableListOf<Int>()
    var remaining = total
    repeat(Mood.entries.size - 1) {
        val count = (0..remaining).random()
        counts.add(count)
        remaining -= count
    }

    counts.add(remaining)
    return counts
}