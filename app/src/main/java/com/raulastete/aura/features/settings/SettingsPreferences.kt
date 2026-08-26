package com.raulastete.aura.features.settings

import com.raulastete.aura.features.record.Mood
import kotlinx.coroutines.flow.Flow

interface SettingsPreferences {
    suspend fun saveDefaultTopics(topics: List<String>)
    fun observeDefaultTopics(): Flow<List<String>>

    suspend fun saveDefaultMood(mood: Mood)
    fun observeDefaultMood(): Flow<Mood>
}