package com.raulastete.aura.screens.record_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.chips.MultichoiceChip
import com.raulastete.aura.core.presentation.designsystem.dropdowns.Selectable
import com.raulastete.aura.core.presentation.designsystem.dropdowns.SelectableDropdown
import com.raulastete.aura.core.presentation.designsystem.dropdowns.asUnselectedItems
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme
import com.raulastete.aura.core.presentation.model.MoodUi
import com.raulastete.aura.core.presentation.util.string.UiText
import com.raulastete.aura.screens.record_list.MoodChipContent
import com.raulastete.aura.screens.record_list.RecordFilterDropdown
import com.raulastete.aura.screens.record_list.RecordListAction

@Composable
fun FiltersSection(
    moods: List<Selectable<MoodUi>>,
    topics: List<Selectable<String>>,
    moodChipContent: MoodChipContent,
    topicChipContent: UiText,
    isMoodFilterActive: Boolean,
    isTopicFilterActive: Boolean,
    modifier: Modifier = Modifier,
    onAction: (RecordListAction) -> Unit
) {

    var currentDropdown by remember { mutableStateOf<RecordFilterDropdown?>(null) }

    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MoodFilterChip(
            moods = moods,
            moodChipContent = moodChipContent,
            currentDropdown = currentDropdown,
            isMoodFilterActive = isMoodFilterActive,
            onAction = onAction,
            onClick = { currentDropdown = RecordFilterDropdown.MOOD },
            onDismiss = { currentDropdown = null }
        )

        TopicFilterChip(
            topics = topics,
            topicChipContent = topicChipContent,
            currentDropdown = currentDropdown,
            isTopicFilterActive = isTopicFilterActive,
            onAction = onAction,
            onClick = { currentDropdown = RecordFilterDropdown.TOPIC },
            onDismiss = { currentDropdown = null }
        )
    }
}

@Composable
fun MoodFilterChip(
    moodChipContent: MoodChipContent,
    currentDropdown: RecordFilterDropdown?,
    isMoodFilterActive: Boolean,
    onAction: (RecordListAction) -> Unit,
    moods: List<Selectable<MoodUi>>,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dropdownOffset = remember { mutableIntStateOf(0) }

    MultichoiceChip(
        modifier = Modifier.onGloballyPositioned {
            dropdownOffset.intValue = it.size.height
        },
        text = moodChipContent.title.asString(),
        onClick = onClick,
        isDropdownVisible = currentDropdown == RecordFilterDropdown.MOOD,
        isHighlighted = isMoodFilterActive || currentDropdown == RecordFilterDropdown.MOOD,
        showClearButton = isMoodFilterActive,
        onClearButtonClick = { onAction(RecordListAction.OnRemoveFilters(RecordFilterDropdown.MOOD)) },
        dropdownMenu = {
            SelectableDropdown(
                items = moods,
                textForItem = { it.title.asString(context) },
                itemKey = { it.title.id },
                onDismiss = onDismiss,
                onItemClick = {
                    onAction(RecordListAction.OnFilterByMoodToggle(it.item))
                },
                dropdownOffset = IntOffset(x = 0, y = dropdownOffset.intValue),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(it.iconSet.fill),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            )
        },
        leadingContent = {
            if (moodChipContent.moodIcons.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-4).dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    moodChipContent.moodIcons.forEach {
                        Icon(
                            imageVector = ImageVector.vectorResource(it),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        },
    )
}

@Composable
fun TopicFilterChip(
    topics: List<Selectable<String>>,
    topicChipContent: UiText,
    currentDropdown: RecordFilterDropdown?,
    isTopicFilterActive: Boolean,
    onAction: (RecordListAction) -> Unit,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val dropdownOffset = remember { mutableIntStateOf(0) }

    MultichoiceChip(
        modifier = Modifier.onGloballyPositioned {
            dropdownOffset.intValue = it.size.height
        },
        text = topicChipContent.asString(),
        onClick = onClick,
        isDropdownVisible = currentDropdown == RecordFilterDropdown.TOPIC,
        isHighlighted = isTopicFilterActive || currentDropdown == RecordFilterDropdown.TOPIC,
        showClearButton = isTopicFilterActive,
        onClearButtonClick = { onAction(RecordListAction.OnRemoveFilters(RecordFilterDropdown.TOPIC)) },
        dropdownMenu = {
            SelectableDropdown(
                items = topics,
                textForItem = { it },
                itemKey = { it },
                noItemsText = stringResource(R.string.you_don_t_have_any_topics_yet),
                onDismiss = onDismiss,
                onItemClick = { onAction(RecordListAction.OnFilterByTopicToggle(it.item)) },
                dropdownOffset = IntOffset(x = 0, y = dropdownOffset.intValue),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.hashtag),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            )
        }
    )
}

@Preview
@Composable
private fun FilterSectionPreview() {
    AuraTheme {
        FiltersSection(
            moods = MoodUi.entries.asUnselectedItems(),
            topics = (1..5).map { "Topic $it" }.asUnselectedItems(),
            moodChipContent = MoodChipContent(),
            topicChipContent = UiText.StringResource(R.string.all_topics),
            isMoodFilterActive = true,
            isTopicFilterActive = true,
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}