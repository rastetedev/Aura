package com.raulastete.aura.screens.statistics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import com.raulastete.aura.screens.statistics.MoodFrequencyUiModel

@Composable
fun MoodDistributionChartCanvas(
    frequencies: List<MoodFrequencyUiModel>,
    modifier: Modifier = Modifier
) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier.padding(16.dp)) {
        frequencies.forEach { freq ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = freq.mood.iconSet.fill),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = freq.mood.title.asString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = freq.mood.colorSet.vivid
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    ) {
                        val barWidth = size.width
                        val filledWidth = barWidth * freq.percentage
                        
                        drawRoundRect(
                            color = surfaceVariant,
                            size = size,
                            cornerRadius = CornerRadius(6.dp.toPx())
                        )
                        
                        drawRoundRect(
                            color = freq.mood.colorSet.vivid,
                            size = Size(filledWidth, size.height),
                            cornerRadius = CornerRadius(6.dp.toPx())
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(freq.percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}