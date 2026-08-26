package com.raulastete.aura.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulastete.aura.features.record.Mood
import com.raulastete.aura.features.record.RecordDataSource
import com.raulastete.aura.features.settings.SettingsPreferences
import com.raulastete.aura.core.presentation.model.MoodUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences,
    private val recordDataSource: RecordDataSource
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeSettings()
                observeTopicSearchResults()
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
            SettingsAction.OnAddButtonClick -> onAddButtonClick()
            SettingsAction.OnBackClick -> {}
            SettingsAction.OnDismissTopicDropDown -> onDismissTopicDropDown()
            is SettingsAction.OnSelectTopicClick -> onSelectTopic(action.topic)
            is SettingsAction.OnMoodClick -> onMoodClick(action.mood)
            is SettingsAction.OnRemoveTopicClick -> onRemoveTopicClick(action.topic)
            is SettingsAction.OnSearchTextChange -> onSearchTextChange(action.text)
        }
    }

    private fun observeSettings() {
        combine(
            settingsPreferences.observeDefaultTopics(),
            settingsPreferences.observeDefaultMood(),
        ) { topics, mood ->
            _state.update { it.copy(
                topics = topics,
                selectedMood = MoodUi.valueOf(mood.name)
            ) }
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeTopicSearchResults() {
        state
            .distinctUntilChangedBy { it.searchText }
            .map { it.searchText }
            .debounce(300.milliseconds)
            .flatMapLatest { query ->
                if(query.isNotBlank()) {
                    recordDataSource.searchTopics(query)
                } else emptyFlow()
            }
            .onEach { filteredResults ->
                _state.update {
                    val filteredNonDefaultResults = filteredResults - it.topics.toSet()
                    val searchText = it.searchText.trim()
                    val isNewTopic = searchText !in filteredNonDefaultResults && searchText !in it.topics
                            && searchText.isNotBlank()
                    it.copy(
                        suggestedTopics = filteredNonDefaultResults,
                        isTopicSuggestionsVisible = filteredResults.isNotEmpty() || isNewTopic,
                        showCreateTopicOption = isNewTopic
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    private fun onMoodClick(mood: MoodUi) {
        viewModelScope.launch {
            settingsPreferences.saveDefaultMood(Mood.valueOf(mood.name))
        }
    }

    private fun onSelectTopic(topic: String) {
        viewModelScope.launch {
            _state.update { it.copy(
                isTopicTextInputVisible = false,
                isTopicSuggestionsVisible = false,
                searchText = ""
            ) }

            val newDefaultTopics = (state.value.topics + topic).distinct()
            settingsPreferences.saveDefaultTopics(newDefaultTopics)
        }
    }

    private fun onRemoveTopicClick(topic: String) {
        viewModelScope.launch {
            val newDefaultTopics = (state.value.topics - topic).distinct()
            settingsPreferences.saveDefaultTopics(newDefaultTopics)
        }
    }

    private fun onSearchTextChange(text: String) {
        _state.update { it.copy(
            searchText = text
        ) }
    }

    private fun onDismissTopicDropDown() {
        _state.update { it.copy(
            isTopicSuggestionsVisible = false
        ) }
    }

    private fun onAddButtonClick() {
        _state.update { it.copy(
            isTopicTextInputVisible = true
        ) }
    }


}