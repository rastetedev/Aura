package com.raulastete.aura.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.raulastete.aura.core.domain.record.Mood

@Entity
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val recordId: Int = 0,
    val title: String,
    val mood: Mood,
    val recordedAt: Long,
    val note: String?,
    val audioFilePath: String,
    val audioPlaybackLength: Long,
    val audioAmplitudes: List<Float>
)