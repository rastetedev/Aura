package com.raulastete.aura.core.presentation.designsystem.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme
import com.raulastete.aura.core.presentation.model.MoodUi
import kotlin.random.Random

private const val AMPLITUDE_BAR_SPACING_DP = 2
private const val POWER_RATIOS_SIZE = 35

@Composable
fun PlayBar(
    powerRatios: List<Float>,
    trackColor: Color,
    trackFillColor: Color,
    playerProgress: () -> Float,
    modifier: Modifier = Modifier
) {

    Canvas(modifier = modifier) {
        val amplitudeBarSpacingPx = AMPLITUDE_BAR_SPACING_DP.dp.toPx()
        val amplitudeBarWidthPx =
            (this.size.width - (amplitudeBarSpacingPx * POWER_RATIOS_SIZE - 1)) / powerRatios.size

        val clipPath = Path()

        powerRatios.forEachIndexed { i, ratio ->
            val barHeight = ratio.coerceAtLeast(0.1f).coerceAtMost(1f) * size.height

            val barOffsetX = i * (amplitudeBarSpacingPx + amplitudeBarWidthPx)
            val barTopStartPoint = center.y - barHeight / 2f

            val topLeft = Offset(x = barOffsetX, y = barTopStartPoint)

            val barSize = Size(width = amplitudeBarWidthPx, height = barHeight)

            val roundedBar = RoundRect(
                rect = Rect(
                    offset = topLeft,
                    size = barSize
                ),
                cornerRadius = CornerRadius(100f)
            )
            clipPath.addRoundRect(roundedBar)

            drawRoundRect(
                color = trackColor,
                topLeft = topLeft,
                size = barSize,
                cornerRadius = CornerRadius(100f)
            )
        }

        clipPath(clipPath) {
            drawRect(
                color = trackFillColor,
                size = Size(
                    width = size.width * playerProgress(),
                    height = size.height
                )
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PlayBarPreview() {
    AuraTheme {
        val ratios = remember {
            (1..30).map {
                Random.nextFloat()
            }
        }
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayBar(
                powerRatios = ratios,
                trackColor = MoodUi.SAD.colorSet.desaturated,
                trackFillColor = MoodUi.SAD.colorSet.vivid,
                playerProgress = { 0.9f },
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(20.dp)
            )
        }
    }
}
