package com.raulastete.aura.screens.record_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.model.TrackSizeInfo
import java.time.Instant
import kotlin.random.Random

private val firstItemTimelineModifier = Modifier.padding(top = 16.dp)
private val lastItemTimelineModifier = Modifier.height(8.dp)

@Composable
fun RecordTimelineItem(
    recordUi: RecordUi,
    relativePosition: RecordPosition,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
            if (relativePosition != RecordPosition.SINGLE_ENTRY) {
                VerticalDivider(
                    modifier = when (relativePosition) {
                        RecordPosition.FIRST -> firstItemTimelineModifier
                        RecordPosition.LAST -> lastItemTimelineModifier
                        RecordPosition.IN_BETWEEN -> Modifier
                    }
                )
            }

            Image(
                imageVector = ImageVector.vectorResource(recordUi.mood.iconSet.fill),
                contentDescription = recordUi.title,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        RecordCard(
            recordUi = recordUi,
            onPlayClick = onPlayClick,
            onPauseClick = onPauseClick,
            onTrackSizeAvailable = onTrackSizeAvailable,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

enum class RecordPosition {
    FIRST,
    LAST,
    SINGLE_ENTRY,
    IN_BETWEEN
}

@Preview
@Composable
private fun RecordTimelineItemPreview() {
    AuraTheme {
        RecordTimelineItem(
            modifier = Modifier.fillMaxWidth(),
            recordUi = RecordUi(
                id = 0,
                title = "Record title",
                mood = MoodUi.EXCITED,
                recordedAt = Instant.now(),
                note = (1..50).joinToString { "Hello" },
                topics = listOf("Hello", "World"),
                amplitudes = (1..35).map { Random.nextFloat() },
                audioFilePath = "file://record.mp3"
            ),
            relativePosition = RecordPosition.FIRST,
            onPlayClick = {},
            onPauseClick = {},
            onTrackSizeAvailable = {}
        )
    }

}