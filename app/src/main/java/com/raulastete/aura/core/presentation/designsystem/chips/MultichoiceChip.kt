package com.raulastete.aura.core.presentation.designsystem.chips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme

@Composable
fun MultichoiceChip(
    text: String,
    onClick: () -> Unit,
    isClearVisible: Boolean,
    onClearButtonClick: () -> Unit,
    isActive: Boolean,
    dropdownMenu: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null
) {
    val containerColor =
        animateColorAsState(if (isActive) MaterialTheme.colorScheme.surface else Color.Transparent)
    val borderColor =
        animateColorAsState(if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outline)

    Box(
        modifier = modifier
            .then(
                if (isActive) Modifier.shadow(elevation = 4.dp, shape = CircleShape)
                else Modifier
            )
            .clip(CircleShape)
            .border(width = 0.5.dp, color = borderColor.value, shape = CircleShape)
            .background(containerColor.value)
            .clickable(onClick = onClick)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center)
        ) {
            leadingContent?.invoke()
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )
            AnimatedVisibility(visible = isClearVisible) {
                IconButton(
                    onClick = onClearButtonClick,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        tint = MaterialTheme.colorScheme.secondaryContainer,
                        contentDescription = stringResource(R.string.clear_selections)
                    )
                }
            }
        }
        if (isActive) {
            dropdownMenu()
        }
    }
}

@Preview
@Composable
private fun MultichoiceChipPreview() {
    AuraTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            MultichoiceChip(
                text = "All Topics",
                onClick = {},
                isClearVisible = true,
                onClearButtonClick = {},
                isActive = true,
                dropdownMenu = {},
                leadingContent = {}
            )

            MultichoiceChip(
                text = "All Topics",
                onClick = {},
                isClearVisible = false,
                onClearButtonClick = {},
                isActive = false,
                dropdownMenu = {},
                leadingContent = {}
            )
        }
    }
}