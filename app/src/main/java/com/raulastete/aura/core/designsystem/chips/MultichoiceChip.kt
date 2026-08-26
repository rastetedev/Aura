package com.raulastete.aura.core.designsystem.chips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raulastete.aura.R
import com.raulastete.aura.core.designsystem.theme.AuraTheme

@Composable
fun MultichoiceChip(
    text: String,
    onClick: () -> Unit,
    onClearButtonClick: () -> Unit,
    showClearButton: Boolean,
    isHighlighted: Boolean,
    dropdownMenu: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    isDropdownVisible: Boolean = false
) {
    val containerColor =
        animateColorAsState(if (isHighlighted) MaterialTheme.colorScheme.surface else Color.Transparent)
    val borderColor =
        animateColorAsState(if (isHighlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outline)

    Box(modifier = modifier) {
        InputChip(
            shape = CircleShape,
            colors = InputChipDefaults.inputChipColors(
                containerColor = containerColor.value,
                selectedContainerColor = containerColor.value
            ),
            border = BorderStroke(width = 0.5.dp, color = borderColor.value),
            onClick = onClick,
            label = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = isHighlighted,
            leadingIcon = { leadingContent?.invoke() },
            trailingIcon = {
                AnimatedVisibility(visible = showClearButton) {
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
        )
        if (isDropdownVisible) {
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
                onClearButtonClick = {},
                showClearButton = true,
                isHighlighted = true,
                dropdownMenu = {},

                leadingContent = {}
            )

            MultichoiceChip(
                text = "All Topics",
                onClick = {},
                onClearButtonClick = {},
                showClearButton = false,
                isHighlighted = false,
                dropdownMenu = {},
                leadingContent = {}
            )
        }
    }
}