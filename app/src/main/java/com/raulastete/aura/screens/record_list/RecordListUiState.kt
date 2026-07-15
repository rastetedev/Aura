package com.raulastete.aura.screens.record_list

import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.util.string.UiText

data class RecordListUiState(
    val hasRecords: Boolean = false,
    val isLoadingData: Boolean = false,
    val moodFilterList: List<Selectable<MoodUi>> = emptyList(),
    val topicFilterList: List<Selectable<String>> = emptyList(),
    val moodChipContent: MoodChipContent = MoodChipContent(),
    val topicChipContent: UiText = UiText.StringResource(R.string.all_topics),
) {
    val isMoodFilterActive = moodFilterList.any { it.selected }
    val isTopicFilterActive = topicFilterList.any { it.selected }
}

enum class RecordFilterDropdown {
    MOOD, TOPIC
}

data class MoodChipContent(
    val moodIcons: List<Int> = emptyList(),
    val title: UiText = UiText.StringResource(R.string.all_moods)
)