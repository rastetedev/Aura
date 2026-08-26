package com.raulastete.aura.screens.settings

import com.raulastete.aura.core.presentation.model.MoodUi

sealed interface SettingsAction {
    data class OnSearchTextChange(val text: String): SettingsAction
    data class OnSelectTopicClick(val topic: String): SettingsAction
    data class OnRemoveTopicClick(val topic: String): SettingsAction
    data object OnBackClick: SettingsAction
    data object OnDismissTopicDropDown: SettingsAction
    data object OnAddButtonClick: SettingsAction
    object OnRemoveFocusOnInputText : SettingsAction
    data class OnMoodClick(val mood: MoodUi): SettingsAction
}