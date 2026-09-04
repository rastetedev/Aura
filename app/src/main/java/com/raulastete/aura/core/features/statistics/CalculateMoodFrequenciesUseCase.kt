package com.raulastete.aura.core.features.statistics

import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.record.Record

class CalculateMoodFrequenciesUseCase {

    operator fun invoke(records: List<Record>): List<MoodFrequency> {
        val totalRecords = records.size
        val moods = Mood.entries
        if (totalRecords == 0) return moods.map { MoodFrequency.zeroPercentage(it) }

        val moodCounts = IntArray(moods.size)
        for (record in records) {
            moodCounts[record.mood.ordinal]++
        }

        val items = List(moods.size) { index ->
            val mood = moods[index]
            val count = moodCounts[index]
            MoodInfo(
                mood = mood,
                count = count,
                percentage = (count * 100) / totalRecords,
                remainder = (count * 100) % totalRecords
            )
        }

        var currentSum = 0
        for (item in items) {
            currentSum += item.percentage
        }

        val diff = 100 - currentSum
        if (diff > 0) {
            items.sortedWith(
                compareByDescending { it.remainder }
            )
                .take(diff)
                .forEach { it.percentage++ }
        }

        return items.map { MoodFrequency(it.mood, it.count, it.percentage) }
    }
}

private class MoodInfo(
    val mood: Mood,
    val count: Int,
    var percentage: Int,
    val remainder: Int
)