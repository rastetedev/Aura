package com.raulastete.aura.core.features.settings.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.settings.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStoreSettings(
    private val context: Context
): com.raulastete.aura.core.features.settings.SettingsPreferences {

    companion object {
        private val Context.settingsDataStore by preferencesDataStore(
            name = "settings_datastore"
        )
    }

    private val topicsKey = stringSetPreferencesKey("topics")
    private val moodKey = stringPreferencesKey("mood")

    override suspend fun saveDefaultTopics(topics: List<String>) {
        context.settingsDataStore.edit { prefs ->
            prefs[topicsKey] = topics.toSet()
        }
    }

    override fun observeDefaultTopics(): Flow<List<String>> {
        return context
            .settingsDataStore
            .data
            .map { prefs ->
                prefs[topicsKey]?.toList() ?: emptyList()
            }
            .distinctUntilChanged()
    }

    override suspend fun saveDefaultMood(mood: com.raulastete.aura.core.features.record.Mood) {
        context.settingsDataStore.edit { prefs ->
            prefs[moodKey] = mood.name
        }
    }

    override fun observeDefaultMood(): Flow<com.raulastete.aura.core.features.record.Mood> {
        return context
            .settingsDataStore
            .data
            .map { prefs ->
                prefs[moodKey]?.let {
                    com.raulastete.aura.core.features.record.Mood.valueOf(it)
                } ?: com.raulastete.aura.core.features.record.Mood.NEUTRAL
            }
            .distinctUntilChanged()
    }
}