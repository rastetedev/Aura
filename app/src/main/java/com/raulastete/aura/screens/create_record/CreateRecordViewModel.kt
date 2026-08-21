package com.raulastete.aura.screens.create_record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.raulastete.aura.core.domain.audio.AudioPlayer
import com.raulastete.aura.core.domain.recording.RecordingStorage
import com.raulastete.aura.core.presentation.designsystem.dropdowns.asUnselectedItems
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.PlaybackState
import com.raulastete.aura.core.presentation.model.TrackSizeInfo
import com.raulastete.aura.core.presentation.util.amplitude.AmplitudeNormalizer
import com.raulastete.aura.navigation.NavigationRoute
import com.raulastete.aura.navigation.toRecordingDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class CreateRecordViewModel(
    savedStateHandle: SavedStateHandle,
    private val recordingStorage: RecordingStorage,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var durationJob: Job? = null

    private val recordingDetails =
        savedStateHandle
            .toRoute<NavigationRoute.CreateRecord>()
            .toRecordingDetails()

    private val eventChannel = Channel<CreateRecordEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(
        CreateRecordUiState(playbackTotalDuration = recordingDetails.duration)
    )
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeAddTopicText()
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
            is CreateRecordAction.OnAddTopicTextChange -> onAddTopicTextChange(action.text)
            CreateRecordAction.OnConfirmMood -> onConfirmMood()
            CreateRecordAction.OnDismissMoodSelector -> onDismissMoodSelector()
            CreateRecordAction.OnDismissTopicSuggestions -> onDismissTopicSuggestions()
            is CreateRecordAction.OnMoodClick -> onMoodClick(action.moodUi)
            is CreateRecordAction.OnNoteTextChange -> TODO()
            CreateRecordAction.OnPauseAudioClick -> audioPlayer.pause()
            CreateRecordAction.OnPlayAudioClick -> onPlayAudioClick()
            is CreateRecordAction.OnRemoveTopicClick -> onRemoveTopicClick(action.topic)
            CreateRecordAction.OnSaveClick -> onSaveClick()
            is CreateRecordAction.OnTitleTextChange -> onTitleTextChange(action.text)
            is CreateRecordAction.OnTopicClick -> onTopicClick(action.topic)
            is CreateRecordAction.OnTrackSizeAvailable -> onTrackSizeAvailable(action.trackSizeInfo)
            CreateRecordAction.OnSelectMoodClick -> onSelectMoodClick()
            CreateRecordAction.OnDismissConfirmLeaveDialog -> onDismissConfirmLeaveDialog()
            CreateRecordAction.OnCancelClick,
            CreateRecordAction.OnGoBack,
            CreateRecordAction.OnNavigateBackClick -> onShowConfirmLeaveDialog()
        }
    }

    private fun onTrackSizeAvailable(trackSizeInfo: TrackSizeInfo) {
        viewModelScope.launch(Dispatchers.Default) {
            val finalAmplitudes = AmplitudeNormalizer.normalize(
                sourceAmplitudes = recordingDetails.amplitudes,
                trackWidth = trackSizeInfo.trackWidth,
                barWidth = trackSizeInfo.barWidth,
                spacing = trackSizeInfo.spacing
            )

            _state.update {
                it.copy(
                    playbackAmplitudes = finalAmplitudes
                )
            }
        }
    }


    private fun onConfirmMood() {
        _state.update {
            it.copy(
                mood = it.selectedMood,
                canSaveRecord = it.title.isNotBlank(),
                showMoodSelector = false
            )
        }
    }

    private fun onDismissMoodSelector() {
        _state.update {
            it.copy(showMoodSelector = false)
        }
    }

    private fun onSelectMoodClick() {
        _state.update {
            it.copy(showMoodSelector = true)
        }
    }

    private fun onMoodClick(mood: MoodUi) {
        _state.update {
            it.copy(selectedMood = mood)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeAddTopicText() {
        state
            .map { it.addTopicText }
            .distinctUntilChanged()
            .debounce(300.milliseconds)
            .onEach { query ->
                _state.update {
                    it.copy(
                        showTopicSuggestions = query.isNotBlank() && query.trim() !in it.topics,
                        searchResults = listOf(
                            "hello",
                            "helloworld",
                        ).asUnselectedItems()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onDismissTopicSuggestions() {
        _state.update {
            it.copy(showTopicSuggestions = false)
        }
    }

    private fun onRemoveTopicClick(topic: String) {
        _state.update {
            it.copy(topics = it.topics - topic)
        }
    }

    private fun onTopicClick(topic: String) {
        _state.update {
            it.copy(
                addTopicText = "",
                topics = (it.topics + topic).distinct()
            )
        }
    }

    private fun onAddTopicTextChange(text: String) {
        _state.update {
            it.copy(
                addTopicText = text.filter { char ->
                    char.isLetterOrDigit()
                }
            )
        }
    }

    private fun onShowConfirmLeaveDialog() {
        _state.update {
            it.copy(showConfirmLeaveDialog = true)
        }
    }

    private fun onDismissConfirmLeaveDialog() {
        _state.update {
            it.copy(showConfirmLeaveDialog = false)
        }
    }

    private fun onTitleTextChange(text: String) {
        _state.update {
            it.copy(
                title = text,
                canSaveRecord = text.isNotBlank() && it.mood != null
            )
        }
    }

    private fun onSaveClick() {
        if (recordingDetails.filePath == null) {
            return
        }

        viewModelScope.launch {
            val savedFilePath = recordingStorage.savePersistently(
                tempFilePath = recordingDetails.filePath
            )
            if (savedFilePath == null) {
                eventChannel.send(CreateRecordEvent.FailedToSaveFile)
                return@launch
            }

            // TODO: Echo
        }
    }

    private fun onPlayAudioClick() {
        if(state.value.playbackState == PlaybackState.PAUSED) {
            audioPlayer.resume()
        } else {
            audioPlayer.play(
                filePath = recordingDetails.filePath ?: throw IllegalArgumentException(
                    "File path can't be null"
                ),
                onComplete = {
                    _state.update { it.copy(
                        playbackState = PlaybackState.IDLE,
                        durationPlayed = Duration.ZERO
                    ) }
                }
            )

            durationJob = audioPlayer
                .activeTrack
                .filterNotNull()
                .onEach { track ->
                    _state.update { it.copy(
                        playbackState = if(track.isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED,
                        durationPlayed = track.durationPlayed
                    ) }
                }
                .launchIn(viewModelScope)
        }
    }
}