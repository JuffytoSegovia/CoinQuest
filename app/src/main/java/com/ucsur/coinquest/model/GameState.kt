package com.ucsur.coinquest.model

sealed class GameState {
    /** Estado inicial del juego */
    data object NotStarted : GameState()

    /** Estado durante el juego activo */
    data class Playing(
        val level: Int,
        val score: Int,
        val coinsCollected: Int,
        val playerPosition: Position,
        val currentCoinPosition: Position,
        // Propiedades para el movimiento de monedas
        val coinInitialPosition: Position? = null,      // Punto de inicio del movimiento
        val coinMovementDirection: Float = 1f,          // 1f = derecha, -1f = izquierda
        // Estados del juego
        val isPaused: Boolean = false,
        val timeElapsed: Long = 0L,                    // Tiempo transcurrido en milisegundos
        val timeLimit: Long                            // Tiempo límite del nivel en milisegundos
    ) : GameState() {
        // Validación de valores
        init {
            require(level > 0) { "El nivel debe ser mayor que 0" }
            require(score >= 0) { "El puntaje no puede ser negativo" }
            require(coinsCollected >= 0) { "Las monedas recolectadas no pueden ser negativas" }
            require(timeElapsed >= 0) { "El tiempo transcurrido no puede ser negativo" }
            require(timeLimit > 0) { "El tiempo límite debe ser mayor que 0" }
        }
    }

    /** Estado cuando se completa un nivel */
    data class LevelCompleted(
        val level: Int,
        val finalScore: Int,
        val timeElapsed: Long,
        val stars: Int,
        val nextLevelUnlocked: Boolean = true,
        val isNewHighScore: Boolean = false,
        val playerName: String,
        val characterName: String
    ) : GameState() {
        init {
            require(level > 0) { "El nivel debe ser mayor que 0" }
            require(finalScore >= 0) { "El puntaje final no puede ser negativo" }
            require(timeElapsed >= 0) { "El tiempo transcurrido no puede ser negativo" }
            require(stars in 1..3) { "Las estrellas deben estar entre 1 y 3" }
        }
    }

    /** Estado de confirmación de salida */
    data object ExitConfirmation : GameState()
}

/**
 * Representa una posición en el área de juego
 * @property x Posición horizontal
 * @property y Posición vertical
 */
data class Position(
    val x: Float,
    val y: Float
) {
    init {
        require(!x.isNaN() && !y.isNaN()) { "Las coordenadas no pueden ser NaN" }
        require(x.isFinite() && y.isFinite()) { "Las coordenadas deben ser finitas" }
    }
}