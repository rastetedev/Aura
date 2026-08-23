package com.raulastete.aura.core.database.converter

import androidx.room3.ColumnTypeConverter

class FloatListTypeConverter {

    @ColumnTypeConverter
    fun fromList(values: List<Float>): String {
        return values.joinToString(",")
    }

    @ColumnTypeConverter
    fun toList(value: String): List<Float> {
        return value.split(",").map { it.toFloat() }
    }
}