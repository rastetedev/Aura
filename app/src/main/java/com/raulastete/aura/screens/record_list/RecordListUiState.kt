package com.raulastete.aura.screens.record_list

import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.util.string.UiText
import com.raulastete.aura.screens.record_list.model.AudioCaptureMethod
import com.raulastete.aura.screens.record_list.model.RecordDaySection

data class RecordListUiState(
    val records: Map<UiText, List<RecordUi>> = emptyMap(),
    val currentCaptureMethod: AudioCaptureMethod? = null,
    val isLoadingData: Boolean = false,
    val moodFilterList: List<Selectable<MoodUi>> = emptyList(),
    val topicFilterList: List<Selectable<String>> = emptyList(),
    val moodChipContent: MoodChipContent = MoodChipContent(),
    val topicChipContent: UiText = UiText.StringResource(R.string.all_topics),
) {
    val hasRecords: Boolean = records.isNotEmpty()
    val isMoodFilterActive = moodFilterList.any { it.selected }
    val isTopicFilterActive = topicFilterList.any { it.selected }
    val recordSections = records.map { (dateHeader, records) ->
        RecordDaySection(
            dateHeader,
            records
        )
    }
}

enum class RecordFilterDropdown {
    MOOD, TOPIC
}

data class MoodChipContent(
    val moodIcons: List<Int> = emptyList(),
    val title: UiText = UiText.StringResource(R.string.all_moods)
)