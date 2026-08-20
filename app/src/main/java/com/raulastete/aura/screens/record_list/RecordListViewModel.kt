package com.raulastete.aura.screens.record_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulastete.aura.R
import com.raulastete.aura.core.domain.recording.VoiceRecorder
import com.raulastete.aura.core.presentation.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.util.string.UiText
import com.raulastete.aura.screens.record_list.model.AudioCaptureMethod
import com.raulastete.aura.screens.record_list.model.RecordingState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class RecordListViewModel(
    private val voiceRecorder: VoiceRecorder
) : ViewModel() {

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val _state = MutableStateFlow(RecordListUiState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<RecordListEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        observeFilters()
    }

    fun onAction(action: RecordListAction) {
        when (action) {
            RecordListAction.OnRecordFabClick -> {
                requestAudioPermission()
                _state.update {
                    it.copy(currentCaptureMethod = AudioCaptureMethod.STANDARD)
                }
            }

            RecordListAction.OnRequestPermissionQuickRecording -> {
                requestAudioPermission()
                _state.update {
                    it.copy(currentCaptureMethod = AudioCaptureMethod.QUICK)
                }
            }
            RecordListAction.OnRecordButtonLongClick -> {
                startRecording(captureMethod = AudioCaptureMethod.QUICK)
            }

            is RecordListAction.OnFilterByMoodToggle -> toggleMoodFilter(action.mood)
            is RecordListAction.OnFilterByTopicToggle -> toggleTopicFilter(action.topic)
            is RecordListAction.OnRemoveFilters -> removeFilters(action.recordFilterDropdown)

            is RecordListAction.OnPlayClick -> TODO()
            RecordListAction.OnPauseAudioClick -> {}
            is RecordListAction.OnTrackSizeAvailable -> TODO()

            RecordListAction.OnAudioPermissionGranted -> startRecording(captureMethod = AudioCaptureMethod.STANDARD)
            RecordListAction.OnCancelRecording -> cancelRecording()
            RecordListAction.OnCompleteRecording -> stopRecording()
            RecordListAction.OnPauseRecordingClick -> pauseRecording()
            RecordListAction.OnResumeRecordingClick -> resumeRecording()
        }
    }

    private fun removeFilters(recordFilterDropdown: RecordFilterDropdown) {
        when (recordFilterDropdown) {
            RecordFilterDropdown.MOOD -> selectedMoodFilters.update { emptyList() }
            RecordFilterDropdown.TOPIC -> selectedTopicFilters.update { emptyList() }
        }
    }

    private fun observeFilters() {
        combine(
            selectedTopicFilters,
            selectedMoodFilters,
        ) { selectedTopics, selectedMoods ->
            _state.update {
                it.copy(
                    records = mapOf(
                        UiText.StringResource(R.string.today) to (1..5).map { index ->
                            createMockRecord(
                                index
                            )
                        },
                        UiText.StringResource(R.string.yesterday) to (6..10).map { index ->
                            createMockRecord(
                                index
                            )
                        },
                        UiText.Dynamic("17 Agust, 2026") to (11..15).map { index ->
                            createMockRecord(
                                index
                            )
                        }
                    ),
                    topicFilterList = listOf("Topic A", "Topic B", "Topic C").map { topic ->
                        Selectable(
                            item = topic,
                            selected = selectedTopics.contains(topic)
                        )
                    },
                    moodFilterList = MoodUi.entries.map { moodUi ->
                        Selectable(
                            item = moodUi,
                            selected = selectedMoods.contains(moodUi)
                        )
                    },
                    moodChipContent = selectedMoods.asMoodChipContent(),
                    topicChipContent = selectedTopics.asTopicChipContent(),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun toggleMoodFilter(moodUi: MoodUi) {
        selectedMoodFilters.update { selectedMoods ->
            if (moodUi in selectedMoods) {
                selectedMoods - moodUi
            } else {
                selectedMoods + moodUi
            }
        }
    }

    private fun toggleTopicFilter(topic: String) {
        selectedTopicFilters.update { selectedTopics ->
            if (topic in selectedTopics) {
                selectedTopics - topic
            } else {
                selectedTopics + topic
            }
        }
    }

    private fun List<MoodUi>.asMoodChipContent(): MoodChipContent {

        val moodNames = this.map { it.title }

        val title = when (size) {
            0 -> UiText.StringResource(R.string.all_moods)
            1 -> moodNames.first()
            2 -> UiText.Combined(
                format = "%s, %s",
                uiTexts = moodNames.toTypedArray()
            )

            else -> {
                val extraElementCount = size - 2
                UiText.Combined(
                    format = "%s, %s +$extraElementCount",
                    uiTexts = moodNames.take(2).toTypedArray()
                )
            }
        }

        return MoodChipContent(
            moodIcons = this.map { it.iconSet.fill },
            title = title
        )
    }

    private fun startRecording(captureMethod: AudioCaptureMethod) {
        _state.update {
            it.copy(
                recordingState = when (captureMethod) {
                    AudioCaptureMethod.STANDARD -> RecordingState.NORMAL_CAPTURE
                    AudioCaptureMethod.QUICK -> RecordingState.QUICK_CAPTURE
                }
            )
        }
        voiceRecorder.start()

        if (captureMethod == AudioCaptureMethod.STANDARD) {
            voiceRecorder
                .recordingDetails
                .distinctUntilChangedBy { it.duration }
                .map { it.duration }
                .onEach { duration ->
                    _state.update {
                        it.copy(
                            recordingElapsedDuration = duration
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun cancelRecording() {
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING,
                currentCaptureMethod = null
            )
        }
        voiceRecorder.cancel()
    }

    private fun stopRecording() {
        voiceRecorder.stop()
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING
            )
        }

        val recordingDetails = voiceRecorder.recordingDetails.value
        viewModelScope.launch {
            if (recordingDetails.duration < VoiceRecorder.MIN_RECORD_DURATION) {
                eventChannel.send(RecordListEvent.RecordingTooShort)
            } else {
                eventChannel.send(RecordListEvent.OnDoneRecording)
            }
        }
    }

    private fun resumeRecording() {
        voiceRecorder.resume()
        _state.update {
            it.copy(
                recordingState = RecordingState.NORMAL_CAPTURE
            )
        }
    }

    private fun pauseRecording() {
        voiceRecorder.pause()
        _state.update {
            it.copy(
                recordingState = RecordingState.PAUSED
            )
        }
    }

    private fun List<String>.asTopicChipContent(): UiText {
        return when (size) {
            0 -> UiText.StringResource(R.string.all_topics)
            1 -> UiText.Dynamic(this.first())
            2 -> UiText.Dynamic("${this.first()}, ${this.last()}")
            else -> {
                val extraElementCount = size - 2
                UiText.Dynamic("${this.first()}, ${this[1]} +$extraElementCount")
            }
        }
    }

    private fun requestAudioPermission() = viewModelScope.launch {
        eventChannel.send(RecordListEvent.RequestAudioPermission)
    }

    private fun createMockRecord(id: Int): RecordUi {
        val moods = MoodUi.entries
        return RecordUi(
            id = id,
            title = "Record $id",
            mood = moods[id % moods.size],
            recordedAt = java.time.Instant.now(),
            note = "This is a random note for record number $id. " + (1..10).joinToString(" ") { "Hello" },
            topics = listOf("Topic A", "Topic B"),
            amplitudes = (1..35).map { Random.nextFloat() }
        )
    }
}