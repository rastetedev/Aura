package com.raulastete.aura.screens.record_list.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme
import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.model.TrackSizeInfo
import com.raulastete.aura.core.presentation.util.string.UiText
import com.raulastete.aura.screens.record_list.model.RecordDaySection
import kotlin.random.Random

@Composable
fun RecordList(
    recordSections: List<RecordDaySection>,
    onPlayClick: (recordId: Int) -> Unit,
    onPauseClick: () -> Unit,
    onTrackSizeAvailable: (trackSizeInfo: TrackSizeInfo) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {
        recordSections.forEachIndexed { sectionIndex, (dateHeader, records) ->
            stickyHeader(key = sectionIndex) {
                Text(
                    text = dateHeader.asString().uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )

                Spacer(Modifier.height(8.dp))
            }

            itemsIndexed(records, key = { _, record -> record.id }) { index, record ->
                RecordTimelineItem(
                    relativePosition = when (index) {
                        0 if records.size == 1 -> RecordPosition.SINGLE_ENTRY
                        0 -> RecordPosition.FIRST
                        records.lastIndex -> RecordPosition.LAST
                        else -> RecordPosition.IN_BETWEEN
                    },
                    onPlayClick = { onPlayClick(record.id) },
                    onPauseClick = { onPauseClick() },
                    onTrackSizeAvailable = onTrackSizeAvailable,
                    recordUi = record,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecordListPreview() {
    AuraTheme {
        RecordList(
            recordSections = listOf(
                RecordDaySection(
                    dateHeader = UiText.StringResource(R.string.today),
                    records = (1..5).map { createMockRecord(it) }
                ),
                RecordDaySection(
                    dateHeader = UiText.StringResource(R.string.yesterday),
                    records = (6..10).map { createMockRecord(it) }
                ),
                RecordDaySection(
                    dateHeader = UiText.Dynamic("17 Agust, 2026"),
                    records = (11..15).map { createMockRecord(it) }
                )
            ),
            onPlayClick = {},
            onPauseClick = {},
            onTrackSizeAvailable = {}
        )
    }
}

private fun createMockRecord(id: Int): RecordUi {
    val moods = com.raulastete.aura.core.presentation.model.MoodUi.entries
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