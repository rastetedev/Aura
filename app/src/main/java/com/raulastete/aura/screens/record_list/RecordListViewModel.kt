package com.raulastete.aura.screens.record_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulastete.aura.R
import com.raulastete.aura.core.domain.audio.AudioPlayer
import com.raulastete.aura.core.domain.record.RecordDataSource
import com.raulastete.aura.core.domain.recording.VoiceRecorder
import com.raulastete.aura.core.presentation.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.PlaybackState
import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.model.TrackSizeInfo
import com.raulastete.aura.core.presentation.model.toRecordUi
import com.raulastete.aura.core.presentation.util.amplitude.AmplitudeNormalizer
import com.raulastete.aura.core.presentation.util.string.UiText
import com.raulastete.aura.screens.record_list.model.AudioCaptureMethod
import com.raulastete.aura.screens.record_list.model.RecordingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import com.raulastete.aura.core.domain.record.Record

class RecordListViewModel(
    private val voiceRecorder: VoiceRecorder,
    private val audioPlayer: AudioPlayer,
    private val recordDataSource: RecordDataSource,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val playingEchoId = MutableStateFlow<Int?>(null)
    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())
    private val audioTrackSizeInfo = MutableStateFlow<TrackSizeInfo?>(null)

    private val _state = MutableStateFlow(RecordListUiState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeFilters()
                observeRecords()
                fetchNavigationArgs()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RecordListUiState()
        )

    private val eventChannel = Channel<RecordListEvent>()
    val events = eventChannel.receiveAsFlow()

    private val filteredRecords = recordDataSource
        .observeRecords()
        .filterByMoodAndTopics()
        .onEach {
            _state.update {
                it.copy(isLoadingData = false)
            }
        }
        .combine(audioTrackSizeInfo) { records, trackSizeInfo ->
            if (trackSizeInfo != null) {
                records.map { record ->
                    record.copy(
                        audioAmplitudes = AmplitudeNormalizer.normalize(
                            sourceAmplitudes = record.audioAmplitudes,
                            trackWidth = trackSizeInfo.trackWidth,
                            barWidth = trackSizeInfo.barWidth,
                            spacing = trackSizeInfo.spacing
                        )
                    )
                }
            } else records
        }
        .flowOn(Dispatchers.Default)

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

            is RecordListAction.OnPlayClick -> onPlayRecordClick(action.recordId)
            RecordListAction.OnPauseAudioClick -> audioPlayer.pause()
            is RecordListAction.OnTrackSizeAvailable ->
                audioTrackSizeInfo.update {
                    action.trackSizeInfo
                }

            RecordListAction.OnAudioPermissionGranted -> startRecording(captureMethod = AudioCaptureMethod.STANDARD)
            RecordListAction.OnCancelRecording -> cancelRecording()
            RecordListAction.OnCompleteRecording -> stopRecording()
            RecordListAction.OnPauseRecordingClick -> pauseRecording()
            RecordListAction.OnResumeRecordingClick -> resumeRecording()
        }
    }

    private fun fetchNavigationArgs() {
        val startRecording = savedStateHandle.get<Boolean>("startRecording") ?: false
        if (startRecording) {
            _state.update {
                it.copy(
                    currentCaptureMethod = AudioCaptureMethod.STANDARD
                )
            }
            requestAudioPermission()
        }
    }

    private fun observeRecords() {
        combine(
            filteredRecords,
            playingEchoId,
            audioPlayer.activeTrack
        ) { records, playingEchoId, activeTrack ->
            if (playingEchoId == null || activeTrack == null) {
                return@combine records.map { it.toRecordUi() }
            }

            records.map { echo ->
                if (echo.id == playingEchoId) {
                    echo.toRecordUi(
                        currentPlaybackDuration = activeTrack.durationPlayed,
                        playbackState = if (activeTrack.isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED
                    )
                } else echo.toRecordUi()
            }
        }
            .groupByRelativeDate()
            .onEach { groupedRecords ->
                _state.update {
                    it.copy(
                        records = groupedRecords
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private fun Flow<List<RecordUi>>.groupByRelativeDate(): Flow<Map<UiText, List<RecordUi>>> {
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        val today = LocalDate.now()
        return map { recordUis ->
            recordUis
                .groupBy { echo ->
                    LocalDate.ofInstant(
                        echo.recordedAt,
                        ZoneId.systemDefault()
                    )
                }
                .mapValues { (_, echos) ->
                    echos.sortedByDescending { it.recordedAt }
                }
                .toSortedMap(compareByDescending { it })
                .mapKeys { (date, _) ->
                    when (date) {
                        today -> UiText.StringResource(R.string.today)
                        today.minusDays(1) -> UiText.StringResource(R.string.yesterday)
                        else -> UiText.Dynamic(date.format(formatter))
                    }
                }
        }
    }

    private fun Flow<List<Record>>.filterByMoodAndTopics(): Flow<List<Record>> {
        return combine(
            this,
            selectedMoodFilters,
            selectedTopicFilters
        ) { records, moodFilters, topicFilters ->
            records.filter { record ->
                val matchesMoodFilter = moodFilters
                    .takeIf { it.isNotEmpty() }
                    ?.any { it.name == record.mood.name }
                    ?: true

                val matchesTopicFilter = topicFilters
                    .takeIf { it.isNotEmpty() }
                    ?.any { it in record.topics }
                    ?: true

                matchesMoodFilter && matchesTopicFilter
            }

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
            recordDataSource.observeTopics(),
            selectedTopicFilters,
            selectedMoodFilters,
        ) { allTopics, selectedTopics, selectedMoods ->
            _state.update {
                it.copy(
                    topicFilterList = allTopics.map { topic ->
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
                eventChannel.send(RecordListEvent.OnDoneRecording(recordingDetails))
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

    private fun onPlayRecordClick(echoId: Int) {
        val selectedRecord = state.value.records.values.flatten().first { it.id == echoId }
        val activeTrack = audioPlayer.activeTrack.value
        val isNewEcho = playingEchoId.value != echoId
        val isSameEchoIsPlayingFromBeginning = echoId == playingEchoId.value && activeTrack != null
                && activeTrack.durationPlayed == Duration.ZERO

        when {
            isNewEcho || isSameEchoIsPlayingFromBeginning -> {
                playingEchoId.update { echoId }
                audioPlayer.stop()
                audioPlayer.play(
                    filePath = selectedRecord.audioFilePath,
                    onComplete = ::completePlayback
                )
            }

            else -> audioPlayer.resume()
        }
    }

    private fun completePlayback() {
        _state.update {
            it.copy(
                records = it.records.mapValues { (_, echos) ->
                    echos.map { echo ->
                        echo.copy(playbackCurrentDuration = Duration.ZERO)
                    }
                }
            )
        }
        playingEchoId.update { null }
    }
}