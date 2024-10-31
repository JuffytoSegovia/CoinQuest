package com.ucsur.coinquest.model

data class Settings(
    val isSoundEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val soundVolume: Float = 1f,
    val musicVolume: Float = 1f
)