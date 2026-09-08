package com.raulastete.aura.screens.record_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.raulastete.aura.R
import com.raulastete.aura.core.designsystem.textfields.TransparentTextField
import com.raulastete.aura.core.designsystem.theme.AuraTheme
import com.raulastete.aura.screens.record_list.RecordListAction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndDateFilterBar(
    searchQuery: String,
    isDateRangeActive: Boolean,
    dateRangeStart: LocalDate?,
    dateRangeEnd: LocalDate?,
    modifier: Modifier = Modifier,
    onAction: (RecordListAction) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search bar
        Row(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
            TransparentTextField(
                text = searchQuery,
                onValueChange = { onAction(RecordListAction.OnSearchQueryChange(it)) },
                hintText = stringResource(R.string.search_hint),
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
            AnimatedVisibility(visible = searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onAction(RecordListAction.OnSearchQueryChange("")) },
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Date range chip
        val chipContainerColor by animateColorAsState(
            if (isDateRangeActive) MaterialTheme.colorScheme.surface else Color.Transparent
        )
        val chipBorderColor by animateColorAsState(
            if (isDateRangeActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outline
        )

        InputChip(
            shape = CircleShape,
            colors = InputChipDefaults.inputChipColors(
                containerColor = chipContainerColor,
                selectedContainerColor = chipContainerColor,
            ),
            border = BorderStroke(width = 0.5.dp, color = chipBorderColor),
            onClick = { showDatePicker = true },
            label = {
                Text(
                    text = if (isDateRangeActive && dateRangeStart != null && dateRangeEnd != null) {
                        formatDateRange(dateRangeStart, dateRangeEnd)
                    } else {
                        stringResource(R.string.date)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            selected = isDateRangeActive,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                AnimatedVisibility(visible = isDateRangeActive) {
                    IconButton(
                        onClick = { onAction(RecordListAction.OnClearDateRange) },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            tint = MaterialTheme.colorScheme.secondaryContainer,
                            contentDescription = stringResource(R.string.clear_date_range)
                        )
                    }
                }
            }
        )
    }

    if (showDatePicker) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val datePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = dateRangeStart
                ?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            initialSelectedEndDateMillis = dateRangeEnd
                ?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )

        ModalBottomSheet(
            onDismissRequest = { showDatePicker = false },
            sheetState = sheetState,
        ) {
            DateRangePicker(
                state = datePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                headline = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.select_range),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                            TextButton(
                                onClick = {
                                    val startMillis = datePickerState.selectedStartDateMillis
                                    val endMillis = datePickerState.selectedEndDateMillis
                                    if (startMillis != null && endMillis != null) {
                                        val start = Instant.ofEpochMilli(startMillis)
                                            .atZone(ZoneId.systemDefault()).toLocalDate()
                                        val end = Instant.ofEpochMilli(endMillis)
                                            .atZone(ZoneId.systemDefault()).toLocalDate()
                                        onAction(RecordListAction.OnDateRangeSelected(start, end))
                                    }
                                    showDatePicker = false
                                },
                                enabled = datePickerState.selectedStartDateMillis != null &&
                                        datePickerState.selectedEndDateMillis != null
                            ) {
                                Text(stringResource(R.string.apply))
                            }
                        }
                    }
                },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

private fun formatDateRange(start: LocalDate, end: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM")
    return "${start.format(formatter)} – ${end.format(formatter)}"
}

@Preview
@Composable
private fun SearchAndDateFilterBarPreview() {
    AuraTheme {
        SearchAndDateFilterBar(
            searchQuery = "",
            isDateRangeActive = false,
            dateRangeStart = null,
            dateRangeEnd = null,
            onAction = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
