package com.raulastete.aura.screens.record_list

import com.raulastete.aura.R
import com.raulastete.aura.core.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.util.string.UiText
import com.raulastete.aura.screens.record_list.model.AudioCaptureMethod
import com.raulastete.aura.screens.record_list.model.RecordDaySection
import com.raulastete.aura.screens.record_list.model.RecordingState
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration

data class RecordListUiState(
    val records: Map<UiText, List<RecordUi>> = emptyMap(),
    val currentCaptureMethod: AudioCaptureMethod? = null,
    val recordingElapsedDuration: Duration = Duration.ZERO,
    val isLoadingData: Boolean = true,
    val recordingState: RecordingState = RecordingState.NOT_RECORDING,
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
    val formattedRecordDuration: String
        get() {
            val minutes = (recordingElapsedDuration.inWholeMinutes % 60).toInt()
            val seconds = (recordingElapsedDuration.inWholeSeconds % 60).toInt()
            val centiSeconds =
                ((recordingElapsedDuration.inWholeMilliseconds % 1000) / 100.0).roundToInt()

            return String.format(
                locale = Locale.US,
                format = "%02d:%02d:%02d",
                minutes, seconds, centiSeconds
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