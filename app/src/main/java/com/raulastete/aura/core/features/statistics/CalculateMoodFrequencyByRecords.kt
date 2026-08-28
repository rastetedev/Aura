package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.record.Record

class CalculateMoodFrequencyByRecords {

    operator fun invoke(records: List<com.raulastete.aura.core.features.record.Record>): List<com.raulastete.aura.core.features.statistics.MoodFrequency> {

        val allMoods = records.map { it.mood }
        val totalRecords = allMoods.size

        val frequencies = com.raulastete.aura.core.features.record.Mood.entries.map { mood ->
            val count = allMoods.count { it == mood }
            val percentage = if (totalRecords > 0) count.toFloat() / totalRecords else 0f
            com.raulastete.aura.core.features.statistics.MoodFrequency(
                mood,
                count,
                percentage
            )
        }

        return frequencies
    }
}