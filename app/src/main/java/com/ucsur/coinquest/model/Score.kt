package com.ucsur.coinquest.model

import com.google.firebase.Timestamp

data class Score(
    val playerId: String = "",
    val playerName: String = "",
    val characterName: String = "",
    val score: Int = 0,
    val level: Int = 1,
    val stars: Int = 0,
    val timeElapsed: Long = 0L,
    val timestamp: Timestamp = Timestamp.now()  // Cambiado de Long a Timestamp
)