package com.raulastete.aura.features.player

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val activeTrack: StateFlow<AudioTrack?>
    fun play(filePath: String, onComplete: () -> Unit)
    fun pause()
    fun resume()
    fun stop()
}