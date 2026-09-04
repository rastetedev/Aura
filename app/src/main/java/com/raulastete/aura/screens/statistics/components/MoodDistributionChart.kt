package com.raulastete.aura.screens.statistics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.raulastete.aura.screens.statistics.MoodFrequencyUiModel

@Composable
fun MoodDistributionChart(
    moodFrequencies: List<MoodFrequencyUiModel>,
    modifier: Modifier = Modifier
) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier) {
        moodFrequencies.forEach { moodFrequency ->
            val (mood, percentage) = moodFrequency

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Icon(
                        painter = painterResource(id = mood.iconSet.fill),
                        tint = Color.Unspecified,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = mood.title.asString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = mood.colorSet.vivid
                            )

                            Text(
                                modifier = Modifier.wrapContentWidth(),
                                text = "${moodFrequency.percentage}%",
                                style = MaterialTheme.typography.bodyLarge,
                                color = mood.colorSet.vivid
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                        ) {
                            val barWidth = size.width
                            val filledWidth = barWidth * percentage / 100

                            drawRoundRect(
                                color = surfaceVariant,
                                size = size,
                                cornerRadius = CornerRadius(6.dp.toPx())
                            )

                            drawRoundRect(
                                color = mood.colorSet.vivid,
                                size = Size(filledWidth, size.height),
                                cornerRadius = CornerRadius(6.dp.toPx())
                            )
                        }
                    }
                }
            }
        }
    }
}