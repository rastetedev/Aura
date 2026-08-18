package com.raulastete.aura.core.presentation.util.string

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.formatMMSS(): String {
    val totalSeconds = this.toLong(DurationUnit.SECONDS)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format(
        locale = Locale.getDefault(),
        format = "%02d:%02d",
        minutes,
        seconds
    )
}

fun Instant.formatHHmm(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return  this.atZone(ZoneId.systemDefault()).format(formatter)
}