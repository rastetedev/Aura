package com.raulastete.aura.core.presentation.util.string

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

    @Composable
    fun asString(): String {
        return when (this) {
            is Dynamic -> value
            is StringResource -> stringResource(id, *arguments)
        }
    }
}