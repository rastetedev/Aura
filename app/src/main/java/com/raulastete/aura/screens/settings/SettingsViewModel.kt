package com.raulastete.aura.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.raulastete.aura.core.features.record.Mood
import com.raulastete.aura.core.features.record.RecordDataSource
import com.raulastete.aura.core.features.settings.SettingsPreferences
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.workers.ExportWorker
import com.raulastete.aura.core.workers.ImportWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import kotlin.time.Duration.Companion.milliseconds

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences,
    private val recordDataSource: RecordDataSource,
    private val workManager: WorkManager
) : ViewModel() {

    companion object {
        private const val EXPORT_WORK_NAME = "aura_export_work"
        private const val IMPORT_WORK_NAME = "aura_import_work"
    }

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeSettings()
                observeTopicSearchResults()
                observeExportWork()
                observeImportWork()
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
            SettingsAction.OnRemoveFocusOnInputText -> onHideTextInput()
            SettingsAction.OnExportClick -> onExportClick()
            is SettingsAction.OnImportFileSelected -> onImportFileSelected(action.uri)
        }
    }

    private fun onExportClick() {
        if (_state.value.isExporting) return

        val request = OneTimeWorkRequestBuilder<ExportWorker>()
            .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueueUniqueWork(
            EXPORT_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    private fun onImportFileSelected(uri: Uri) {
        if (_state.value.isImporting) return

        val request = OneTimeWorkRequestBuilder<ImportWorker>()
            .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf(ImportWorker.KEY_ZIP_URI to uri.toString()))
            .build()

        workManager.enqueueUniqueWork(
            IMPORT_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    private fun observeExportWork() {
        workManager.getWorkInfosForUniqueWorkLiveData(EXPORT_WORK_NAME)
            .observeForever { workInfos ->
                val isRunning = workInfos?.any { info ->
                    info.state == WorkInfo.State.RUNNING || info.state == WorkInfo.State.ENQUEUED
                } ?: false
                _state.update { it.copy(isExporting = isRunning) }
            }
    }

    private fun observeImportWork() {
        workManager.getWorkInfosForUniqueWorkLiveData(IMPORT_WORK_NAME)
            .observeForever { workInfos ->
                val isRunning = workInfos?.any { info ->
                    info.state == WorkInfo.State.RUNNING || info.state == WorkInfo.State.ENQUEUED
                } ?: false
                _state.update { it.copy(isImporting = isRunning) }
            }
    }

    private fun onHideTextInput() {
        if (state.value.searchText.isNotBlank()) {
            onSelectTopic(state.value.searchText)
        } else {
            _state.update {
                it.copy(
                    isTopicTextInputVisible = false,
                    searchText = "",
                    isTopicSuggestionsVisible = false,
                )
            }
        }
    }

    private fun observeSettings() {
        combine(
            settingsPreferences.observeDefaultTopics(),
            settingsPreferences.observeDefaultMood(),
        ) { topics, mood ->
            _state.update {
                it.copy(
                    topics = topics,
                    selectedMood = MoodUi.valueOf(mood.name)
                )
            }
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeTopicSearchResults() {
        state
            .map { it.searchText }
            .distinctUntilChanged()
            .transformLatest { query ->
                if (query.isBlank()) {
                    emit(emptyList())
                } else {
                    delay(300.milliseconds)
                    emitAll(recordDataSource.searchTopics(query))
                }
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
            _state.update {
                it.copy(
                    isTopicTextInputVisible = false,
                    isTopicSuggestionsVisible = false,
                    searchText = ""
                )
            }
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
        _state.update { it.copy(searchText = text) }
    }

    private fun onDismissTopicDropDown() {
        _state.update { it.copy(isTopicSuggestionsVisible = false) }
    }

    private fun onAddButtonClick() {
        _state.update { it.copy(isTopicTextInputVisible = true) }
    }
}