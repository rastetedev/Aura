package com.raulastete.aura.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsUiState()
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnAddButtonClick -> {}
            SettingsAction.OnBackClick -> {}
            SettingsAction.OnCreateTopicClick -> {}
            SettingsAction.OnDismissTopicDropDown -> {}
            is SettingsAction.OnMoodClick -> {}
            is SettingsAction.OnRemoveTopicClick -> {}
            is SettingsAction.OnSearchTextChange -> {}
        }
    }

}