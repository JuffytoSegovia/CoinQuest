package com.ucsur.coinquest.model

sealed class GameState {
    object NotStarted : GameState()

    data class Playing(
        val level: Int,
        val score: Int,
        val coinsCollected: Int,
        val playerPosition: Position,
        val currentCoinPosition: Position,
        val isPaused: Boolean = false,
        val timeElapsed: Long = 0L,
        val timeLimit: Long = 60000L  // Tiempo límite por defecto de 60 segundos
    ) : GameState()

    data class LevelCompleted(
        val level: Int,
        val finalScore: Int,
        val timeElapsed: Long,
        val stars: Int,
        val nextLevelUnlocked: Boolean = false
    ) : GameState()

    object ExitConfirmation : GameState()
}

data class Position(val x: Float, val y: Float)