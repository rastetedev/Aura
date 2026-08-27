package com.raulastete.aura.features.statistics

import com.raulastete.aura.features.record.Mood
import com.raulastete.aura.features.record.Record

class CalculateMoodFrequencyByRecords {

    operator fun invoke(records: List<Record>): List<MoodFrequency> {

        val allMoods = records.map { it.mood }
        val totalRecords = allMoods.size

        val frequencies = Mood.entries.map { mood ->
            val count = allMoods.count { it == mood }
            val percentage = if (totalRecords > 0) count.toFloat() / totalRecords else 0f
            MoodFrequency(mood, count, percentage)
        }

        return frequencies
    }
}