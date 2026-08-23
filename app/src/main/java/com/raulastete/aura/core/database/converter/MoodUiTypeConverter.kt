package com.raulastete.aura.core.database.converter

import androidx.room3.ColumnTypeConverter
import com.raulastete.aura.core.presentation.model.MoodUi

class MoodUiTypeConverter {

    @ColumnTypeConverter
    fun fromMood(moodUi: MoodUi): String {
        return moodUi.name
    }

    @ColumnTypeConverter
    fun toMood(moodName: String): MoodUi {
        return MoodUi.valueOf(moodName)
    }
}