package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.record.Record

class CalculateMoodFrequenciesUseCase {

    operator fun invoke(records: List<Record>): List<MoodFrequency> {
        if (records.isEmpty()) return Mood.entries.map { MoodFrequency(it, 0, 0.0) }

        val totalRecords = records.size
        val countsByMood = records.groupingBy { it.mood }.eachCount()
        var accumulativePercentage = 0.0

        return Mood.entries.mapIndexed { index, mood ->
            val count = countsByMood[mood] ?: 0
            if (index == Mood.entries.lastIndex) {
                MoodFrequency(mood, count, 1.0 - accumulativePercentage)
            } else {
                val percentage = (count / totalRecords.toDouble() * 10) / 10
                accumulativePercentage += percentage
                println("Percentage $percentage")
                MoodFrequency(mood, count, percentage)
            }
        }
    }
}