package com.raulastete.aura.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class TopicEntity(
    @PrimaryKey(autoGenerate = false)
    val topic: String
)