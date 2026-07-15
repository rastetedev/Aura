package com.raulastete.aura.screens.record_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.util.string.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RecordListViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val _state = MutableStateFlow(RecordListUiState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeFilters()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RecordListUiState()
        )

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
            selectedMoodFilters
        ) { selectedTopics, selectedMoods ->
            _state.update {
                it.copy(
                    topicFilterList = it.topicFilterList.map { selectableTopic ->
                        Selectable(
                            item = selectableTopic.item,
                            selected = selectedTopics.contains(selectableTopic.item)
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
}