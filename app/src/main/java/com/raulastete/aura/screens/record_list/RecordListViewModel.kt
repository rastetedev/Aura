package com.raulastete.aura.screens.record_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.util.string.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class RecordListViewModel : ViewModel() {

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val _state = MutableStateFlow(RecordListUiState())
    val state = _state.asStateFlow()

    init {
        observeFilters()
    }

    fun onAction(action: RecordListAction) {
        when (action) {
            RecordListAction.OnFabClick -> {}
            RecordListAction.OnFabLongClick -> {}
            is RecordListAction.OnFilterByMoodToggle ->
                toggleMoodFilter(action.mood)

            is RecordListAction.OnFilterByTopicToggle ->
                toggleTopicFilter(action.topic)

            is RecordListAction.OnRemoveFilters ->
                removeFilters(action.recordFilterDropdown)

            RecordListAction.OnPauseClick -> TODO()
            is RecordListAction.OnPlayClick -> TODO()
            is RecordListAction.OnTrackSizeAvailable -> TODO()
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
                        UiText.StringResource(R.string.today) to (1..5).map { index -> createMockRecord(index) },
                        UiText.StringResource(R.string.yesterday) to (6..10).map { index -> createMockRecord(index) },
                        UiText.Dynamic("17 Agust, 2026") to (11..15).map { index -> createMockRecord(index) }
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