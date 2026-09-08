package com.raulastete.aura.core.workers

import kotlinx.serialization.Serializable

@Serializable
data class BackupRecord(
    val recordId: Int,
    val title: String,
    val mood: String,
    val recordedAt: Long,
    val note: String?,
    val audioFileName: String,
    val audioPlaybackLength: Long,
    val audioAmplitudes: List<Float>,
    val topics: List<String>
)
