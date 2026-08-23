package com.raulastete.aura.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.raulastete.aura.core.presentation.model.MoodUi

@Entity
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val recordId: Int = 0,
    val title: String,
    val mood: MoodUi,
    val recordedAt: Long,
    val note: String?,
    val audioFilePath: String,
    val audioPlaybackLength: Long,
    val audioAmplitudes: List<Float>
)