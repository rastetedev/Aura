package com.raulastete.aura.screens.statistics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raulastete.aura.screens.statistics.HeatmapDayUiModel
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

@Composable
fun MoodHeatmapChart(
    heatmapDays: List<HeatmapDayUiModel>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val labelStyle = TextStyle(
        color = onSurface.copy(alpha = 0.6f),
        fontSize = 10.sp
    )

    Canvas(
        modifier = modifier
    ) {
        if (heatmapDays.isEmpty()) return@Canvas

        val rows = 7
        val columns = (heatmapDays.size + rows - 1) / rows
        val spacing = 4.dp.toPx()
        val labelWidth = 30.dp.toPx()
        val monthLabelHeight = 20.dp.toPx()
        
        val availableWidth = size.width - labelWidth
        val availableHeight = size.height - monthLabelHeight
        
        val cellSize = minOf(
            (availableWidth - (columns - 1) * spacing) / columns,
            (availableHeight - (rows - 1) * spacing) / rows
        )

        // Draw Day Labels (Mon, Wed, Fri)
        val dayLabels = listOf("Mon", "Wed", "Fri")
        dayLabels.forEachIndexed { index, label ->
            val rowIndex = index * 2
            val y = monthLabelHeight + rowIndex * (cellSize + spacing) + cellSize / 2
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                style = labelStyle,
                topLeft = Offset(0f, y - 6.sp.toPx())
            )
        }

        // Draw Cells
        heatmapDays.forEachIndexed { index, day ->
            val col = index / rows
            val row = index % rows
            
            val x = labelWidth + col * (cellSize + spacing)
            val y = monthLabelHeight + row * (cellSize + spacing)
            
            val color = day.mood?.colorSet?.vivid ?: surfaceVariant
            val alpha = if (day.mood == null) 0.3f else 1f
            
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(cellSize, cellSize),
                cornerRadius = CornerRadius(2.dp.toPx()),
                alpha = alpha
            )

            // Draw Month Labels
            if (row == 0 && (col == 0 || (day.date.dayOfMonth <= 7 && day.date.dayOfWeek.value == 1))) {
                val monthLabel = day.date.month.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault()).removeSuffix(".")
                drawText(
                    textMeasurer = textMeasurer,
                    text = monthLabel,
                    style = labelStyle,
                    topLeft = Offset(x, 0f)
                )
            }
        }
    }
}