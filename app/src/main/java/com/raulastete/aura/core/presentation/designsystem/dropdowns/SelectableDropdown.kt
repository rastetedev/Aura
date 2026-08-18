package com.raulastete.aura.core.presentation.designsystem.dropdowns

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme

@Composable
fun <T> SelectableDropdown(
    items: List<Selectable<T>>,
    textForItem: (T) -> String,
    itemKey: (T) -> Any,
    onItemClick: (Selectable<T>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    noItemsText: String? = null,
    leadingIcon: @Composable ((T) -> Unit)? = null,
    dropdownOffset: IntOffset = IntOffset.Zero,
    maxDropdownHeight: Dp = Dp.Unspecified,
    extraDropdownOption: ExtraDropdownOption? = null
) {
    Popup(
        onDismissRequest = onDismiss,
        offset = dropdownOffset
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 4.dp,
            modifier = modifier
                .heightIn(max = maxDropdownHeight)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .animateContentSize()
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    if (items.isEmpty() && noItemsText != null) {
                        Text(
                            text = noItemsText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        )
                    }
                }
                items(items, key = { itemKey(it.item) }) { selectable ->

                    val itemContainerColor = animateColorAsState(
                        if (selectable.selected) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.05f)
                        else MaterialTheme.colorScheme.surface
                    )

                    Row(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = itemContainerColor.value)
                            .clickable {
                                onItemClick(selectable)
                            }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leadingIcon?.invoke(selectable.item)
                        Text(
                            text = textForItem(selectable.item),
                            modifier = Modifier.weight(1f)
                        )
                        if (selectable.selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (extraDropdownOption != null && extraDropdownOption.text.isNotBlank()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable(onClick = extraDropdownOption.onClick),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //TODO: Replace this hardcoded icon with a dynamic option
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(18.dp)
                            )

                            Text(
                                text = stringResource(
                                    R.string.create_entry,
                                    extraDropdownOption.text
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

            }
        }
    }
}

data class ExtraDropdownOption(
    val text: String,
    val onClick: () -> Unit
)

data class Selectable<T>(
    val item: T,
    val selected: Boolean
)

fun <T> List<T>.asUnselectedItems(): List<Selectable<T>> {
    return map { Selectable(it, false) }
}

@Preview
@Composable
private fun SelectableDropdownPreview() {
    AuraTheme {
        SelectableDropdown(
            items = (1..5).map { "Option number $it" }.asUnselectedItems()
                .map { if (it.item == "Option number 3") it.copy(selected = true) else it },
            textForItem = { it },
            itemKey = { it },
            onDismiss = {},
            onItemClick = {},
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.hashtag),
                    contentDescription = null
                )
            },
            extraDropdownOption = ExtraDropdownOption(
                text = "Add a new hashtag",
                onClick = {}
            )
        )
    }
}