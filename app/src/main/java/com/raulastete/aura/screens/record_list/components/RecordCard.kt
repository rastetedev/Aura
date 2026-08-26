package com.raulastete.aura.screens.record_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.raulastete.aura.core.designsystem.chips.HashtagChip
import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.model.TrackSizeInfo
import com.raulastete.aura.core.designsystem.modifier.defaultShadow
import com.raulastete.aura.core.designsystem.player.Player

@Composable
fun RecordCard(
    recordUi: RecordUi,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
                .defaultShadow(shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recordUi.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = recordUi.formattedRecordedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Player(
                moodUi = recordUi.mood,
                state = recordUi.playbackState,
                progress = { recordUi.playbackRatio },
                durationPlayed = recordUi.playbackCurrentDuration,
                totalPlaybackDuration = recordUi.playbackTotalDuration,
                powerRatios = recordUi.amplitudes,
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                onTrackSizeAvailable = onTrackSizeAvailable
            )

            if(!recordUi.note.isNullOrBlank()) {
                EchoExpandableText(recordUi.note)
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                recordUi.topics.forEach { topic ->
                    HashtagChip(text = topic)
                }
            }
        }
    }
}