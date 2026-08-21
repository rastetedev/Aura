package com.raulastete.aura.screens.create_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class CreateRecordViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CreateRecordUiState())
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
            initialValue = CreateRecordUiState()
        )

    fun onAction(action: CreateRecordAction) {
        when (action) {
            is CreateRecordAction.OnAddTopicTextChange -> TODO()
            CreateRecordAction.OnCancelClick -> TODO()
            CreateRecordAction.OnConfirmMood -> TODO()
            CreateRecordAction.OnCreateNewTopicClick -> TODO()
            CreateRecordAction.OnDismissMoodSelector -> TODO()
            CreateRecordAction.OnDismissTopicSuggestions -> TODO()
            is CreateRecordAction.OnMoodClick -> TODO()
            CreateRecordAction.OnNavigateBackClick -> TODO()
            is CreateRecordAction.OnNoteTextChange -> TODO()
            CreateRecordAction.OnPauseAudioClick -> TODO()
            CreateRecordAction.OnPlayAudioClick -> TODO()
            is CreateRecordAction.OnRemoveTopicClick -> TODO()
            CreateRecordAction.OnSaveClick -> TODO()
            is CreateRecordAction.OnTitleTextChange -> TODO()
            is CreateRecordAction.OnTopicClick -> TODO()
            is CreateRecordAction.OnTrackSizeAvailable -> TODO()
            CreateRecordAction.OnSelectMoodClick -> TODO()
        }
    }


}