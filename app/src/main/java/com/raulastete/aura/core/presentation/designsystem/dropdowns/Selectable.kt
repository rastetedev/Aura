package com.raulastete.aura.core.presentation.designsystem.dropdowns

data class Selectable<T>(
    val item: T,
    val selected: Boolean
)

fun <T> List<T>.asUnselectedItems(): List<Selectable<T>> {
    return map { Selectable(it, false) }
}