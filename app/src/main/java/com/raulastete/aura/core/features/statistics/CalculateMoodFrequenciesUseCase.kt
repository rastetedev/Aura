package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.record.Record

class CalculateMoodFrequenciesUseCase {

    operator fun invoke(records: List<Record>): List<MoodFrequency> {
        val totalRecords = records.size
        val countsByMood = records.groupingBy { it.mood }.eachCount()

        return Mood.entries.map { mood ->
            val count = countsByMood[mood] ?: 0
            val percentage = if (totalRecords > 0) count.toFloat() / totalRecords else 0f
            MoodFrequency(mood, count, percentage)
        }
    }
}