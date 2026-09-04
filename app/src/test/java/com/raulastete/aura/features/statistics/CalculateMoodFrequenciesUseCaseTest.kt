package com.raulastete.aura.features.statistics

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.statistics.CalculateMoodFrequenciesUseCase
import com.raulastete.aura.features.record.recordTemplate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CalculateMoodFrequenciesUseCaseTest : FunSpec({

    val target = CalculateMoodFrequenciesUseCase()

    test("invoke should handle a single record case correctly") {
        val records = listOf(recordTemplate)
        val frequencies = target(records)

        frequencies.first { it.mood == recordTemplate.mood }.percentage shouldBe 100
        frequencies.filter { it.mood != recordTemplate.mood }.forEach { it.percentage shouldBe 0 }
    }

    test("invoke should handle empty records case correctly") {
        val frequencies = target(emptyList())

        frequencies.size shouldBe Mood.entries.size
        frequencies.forEach { it.percentage shouldBe 0 }
    }

    test("invoke should return 100 percentage for single mood when all records have same mood") {
        val records = (1..10).map { recordTemplate.copy() }
        val frequencies = target(records)

        frequencies.first { it.mood == recordTemplate.mood }.percentage shouldBe 100
        frequencies.first { it.mood != recordTemplate.mood }.percentage shouldBe 0
    }

    test("invoke should return all mood entries even if some have 0 percentage") {
        val records = listOf(recordTemplate.copy(mood = Mood.STRESSED))
        val frequencies = target(records)

        frequencies.size shouldBe Mood.entries.size
        Mood.entries.forEach { mood ->
            frequencies.any { it.mood == mood } shouldBe true
        }
    }

    test("invoke should maintain correct raw counts in the output MoodFrequency objects") {
        val records = List(5) { recordTemplate.copy(mood = Mood.STRESSED) } +
                List(3) { recordTemplate.copy(mood = Mood.SAD) }
        val frequencies = target(records)

        frequencies.first { it.mood == Mood.STRESSED }.count shouldBe 5
        frequencies.first { it.mood == Mood.SAD }.count shouldBe 3
        frequencies.sumOf { it.count } shouldBe 8
    }

    test("invoke should return exact percentages when distribution is perfectly divisible (no LRM needed)") {
        val records = List(2) { recordTemplate.copy(mood = Mood.STRESSED) } +
                List(2) { recordTemplate.copy(mood = Mood.SAD) }

        val frequencies = target(records)

        frequencies.first { it.mood == Mood.STRESSED }.percentage shouldBe 50
        frequencies.first { it.mood == Mood.SAD }.percentage shouldBe 50
        frequencies.sumOf { it.percentage } shouldBe 100
    }

    test("invoke should distribute percentages to sum exactly 100 when fractions are periodic") {
        // 3 records, 3 different moods. Each is 33.333...%
        // LRM should give 34%, 33%, 33%
        val records = Mood.entries.take(3).map { recordTemplate.copy(mood = it) }
        val frequencies = target(records)

        frequencies.sumOf { it.percentage } shouldBe 100
        frequencies.count { it.percentage == 34 } shouldBe 1
        frequencies.count { it.percentage == 33 } shouldBe 2
    }

    test("invoke should distribute extra point to the mood with largest residue, not largest absolute value") {
        // Total = 1000
        // STRESSED: 661 (66.1%, floor 66%, residue 0.1)
        // SAD: 339 (33.9%, floor 33%, residue 0.9)
        // Total floor = 99. Extra point should go to SAD.
        val records = List(661) { recordTemplate.copy(mood = Mood.STRESSED) } +
                List(339) { recordTemplate.copy(mood = Mood.SAD) }
        val frequencies = target(records)

        frequencies.first { it.mood == Mood.STRESSED }.percentage shouldBe 66
        frequencies.first { it.mood == Mood.SAD }.percentage shouldBe 34
    }

    test("invoke should use deterministic tie-breaking based on Enum order when residues and base percentages are equal") {
        // 3 records: STRESSED, SAD, NEUTRAL. 
        // All have same residue (33.33%) and same base percentage (33%).
        // The implementation uses stable sort + Enum order (STRESSED is index 0 in Mood.entries).
        val records = listOf(
            recordTemplate.copy(mood = Mood.STRESSED),
            recordTemplate.copy(mood = Mood.SAD),
            recordTemplate.copy(mood = Mood.NEUTRAL)
        )
        val frequencies = target(records)

        // STRESSED comes first in Mood.entries and remains first after stable sorting by descending residue/percentage
        frequencies.first { it.mood == Mood.STRESSED }.percentage shouldBe 34
        frequencies.first { it.mood == Mood.SAD }.percentage shouldBe 33
        frequencies.first { it.mood == Mood.NEUTRAL }.percentage shouldBe 33
    }

    test("invoke should handle large number of records (10,000) and sum to exactly 100") {
        val records = List(10000) { recordTemplate.copy(mood = Mood.entries.random()) }
        val frequencies = target(records)

        frequencies.sumOf { it.percentage } shouldBe 100
    }

    test("invoke should handle cases with very small counts and distribute points correctly") {
        // Total = 200 records.
        // 4 moods with 1 record each (0.5% -> base 0%, residue 100)
        // 1 mood with 196 records (98% -> base 98%, residue 0)
        // Total base = 98%. diff = 2.
        // Residues for 1-record moods: 100. Residue for EXCITED: 0.
        // Point recipients (by Enum order): STRESSED and SAD.
        val records = listOf(Mood.STRESSED, Mood.SAD, Mood.NEUTRAL, Mood.PEACEFUL).map {
            recordTemplate.copy(mood = it)
        } + List(196) { recordTemplate.copy(mood = Mood.EXCITED) }
        val frequencies = target(records)

        frequencies.first { it.mood == Mood.STRESSED }.percentage shouldBe 1
        frequencies.first { it.mood == Mood.SAD }.percentage shouldBe 1
        frequencies.first { it.mood == Mood.NEUTRAL }.percentage shouldBe 0
        frequencies.first { it.mood == Mood.PEACEFUL }.percentage shouldBe 0
        frequencies.first { it.mood == Mood.EXCITED }.percentage shouldBe 98
        frequencies.sumOf { it.percentage } shouldBe 100
    }
})
