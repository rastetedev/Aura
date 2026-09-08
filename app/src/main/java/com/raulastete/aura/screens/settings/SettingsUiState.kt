package com.raulastete.aura.screens.settings

import com.raulastete.aura.core.presentation.model.MoodUi

data class SettingsUiState(
    val topics: List<String> = emptyList(),
    val selectedMood: MoodUi? = null,
    val searchText: String = "",
    val suggestedTopics: List<String> = emptyList(),
    val isTopicSuggestionsVisible: Boolean = false,
    val showCreateTopicOption: Boolean = false,
    val isTopicTextInputVisible: Boolean = false,

    // Backup
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
)