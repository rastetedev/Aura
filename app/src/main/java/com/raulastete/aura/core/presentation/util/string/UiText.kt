package com.raulastete.aura.core.presentation.util.string

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource

@Stable
sealed interface UiText {

    data class Dynamic(val value: String) : UiText

    data class StringResource(
        @StringRes val id: Int,
        val arguments: Array<Any> = arrayOf()
    ) : UiText {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringResource

            if (id != other.id) return false
            if (!arguments.contentEquals(other.arguments)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + arguments.contentHashCode()
            return result
        }
    }

    @Stable
    data class Combined(
        val format: String,
        val uiTexts: Array<UiText>
    ) : UiText {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Combined

            if (format != other.format) return false
            if (!uiTexts.contentEquals(other.uiTexts)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = format.hashCode()
            result = 31 * result + uiTexts.contentHashCode()
            return result
        }
    }

    @Composable
    fun asString(): String {
        return when (this) {
            is Dynamic -> value
            is StringResource -> stringResource(id, *arguments)
            is Combined -> String.format(format, *uiTexts.map { it.asString() }.toTypedArray())
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is Dynamic -> value
            is StringResource -> context.getString(id, *arguments)
            is Combined -> String.format(
                format,
                *uiTexts.map { it.asString(context) }.toTypedArray()
            )
        }
    }
}