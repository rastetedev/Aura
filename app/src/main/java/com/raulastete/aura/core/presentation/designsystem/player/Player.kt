package com.raulastete.aura.core.presentation.designsystem.player

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme
import com.raulastete.aura.core.presentation.designsystem.theme.MoodPrimary25
import com.raulastete.aura.core.presentation.designsystem.theme.MoodPrimary35
import com.raulastete.aura.core.presentation.designsystem.theme.MoodPrimary80
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.model.PlaybackState
import com.raulastete.aura.core.presentation.model.TrackSizeInfo
import com.raulastete.aura.core.presentation.util.string.formatMMSS
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun Player(
    moodUi: MoodUi?,
    state: PlaybackState,
    powerRatios: List<Float>,
    progress: () -> Float,
    barSpacing: Dp = 2.dp,
    barWidth: Dp = 5.dp,
    durationPlayed: Duration,
    totalPlaybackDuration : Duration,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    modifier: Modifier = Modifier
) {

    val density = LocalDensity.current

    val iconTint = when (moodUi) {
        null -> MoodPrimary80
        else -> moodUi.colorSet.vivid
    }
    val trackFillColor = when (moodUi) {
        null -> MoodPrimary80
        else -> moodUi.colorSet.vivid
    }
    val backgroundColor = when (moodUi) {
        null -> MoodPrimary25
        else -> moodUi.colorSet.faded
    }
    val trackColor = when (moodUi) {
        null -> MoodPrimary35
        else -> moodUi.colorSet.desaturated
    }
    val formattedDurationText = remember(durationPlayed, totalPlaybackDuration) {
        "${durationPlayed.formatMMSS()}/${totalPlaybackDuration.formatMMSS()}"
    }

    Surface(
        shape = CircleShape,
        modifier = modifier,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaybackButton(
                playbackState = state,
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = iconTint
                )
            )
            PlayBar(
                powerRatios = powerRatios,
                trackColor = trackColor,
                barSpacing = barSpacing,
                barWidth = barWidth,
                trackFillColor = trackFillColor,
                playerProgress = progress,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp, horizontal = 8.dp)
                    .fillMaxHeight()
                    .onSizeChanged {
                        if(it.width > 0) {
                            onTrackSizeAvailable(
                                TrackSizeInfo(
                                    trackWidth = it.width.toFloat(),
                                    barWidth = with(density) { barWidth.toPx() },
                                    spacing = with(density) { barSpacing.toPx() }
                                )
                            )
                        }
                    }
            )

            Text(
                text = formattedDurationText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFeatureSettings = "tnum"
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PlayerPreview() {
    val powerRatios = (1..35).map { Random.nextFloat() }
    AuraTheme {
        Player(
            modifier = Modifier.fillMaxWidth(),
            moodUi = null,
            state = PlaybackState.PLAYING,
            powerRatios = powerRatios,
            progress = { 0.5f },
            onPlayClick = {},
            onPauseClick = {},
            durationPlayed = 125.seconds,
            totalPlaybackDuration = 250.seconds,
            onTrackSizeAvailable = {}
        )
    }
}