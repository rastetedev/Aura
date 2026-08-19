package com.raulastete.aura.screens.record_list.model

import com.raulastete.aura.core.presentation.model.RecordUi
import com.raulastete.aura.core.presentation.util.string.UiText

data class RecordDaySection(
    val dateHeader: UiText,
    val records: List<RecordUi>
)