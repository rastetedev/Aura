package com.raulastete.aura.core.designsystem.mood

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.screens.create_record.components.MoodItem

@Composable
fun MoodSelectorRow(
    selectedMood: MoodUi?,
    onMoodClick: (MoodUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MoodUi.entries.forEach { mood ->
            MoodItem(
                selected = mood == selectedMood,
                mood = mood,
                onClick = { onMoodClick(mood) },
            )
        }
    }
}