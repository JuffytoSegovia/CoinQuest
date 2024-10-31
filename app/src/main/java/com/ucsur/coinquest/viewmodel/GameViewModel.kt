package com.ucsur.coinquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsur.coinquest.model.GameState
import com.ucsur.coinquest.model.GameCharacter
import com.ucsur.coinquest.model.Position
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class GameViewModel : ViewModel() {

    companion object {
        // Definición del área de juego - ajustados para permitir un click más
        const val GAME_AREA_TOP = 0f      // Mantener el borde superior
        const val GAME_AREA_BOTTOM = 430f  // Aumentado para permitir un click más hacia abajo (antes 400f)
        const val GAME_AREA_LEFT = 0f      // Mantener el borde izquierdo
        const val GAME_AREA_RIGHT = 355f   // Aumentado para permitir un click más a la derecha (antes 340f)

        // Tamaños de elementos
        const val PLAYER_SIZE = 40f
        const val COIN_SIZE = 30f

        // Paso de movimiento
        const val MOVEMENT_STEP = 15f
    }

    // Estado del personaje seleccionado
    private val _selectedCharacter = MutableStateFlow<GameCharacter?>(null)
    val selectedCharacter: StateFlow<GameCharacter?> = _selectedCharacter.asStateFlow()

    // Estado del juego
    private val _gameState = MutableStateFlow<GameState>(GameState.NotStarted)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Puntaje más alto
    private val _highScore = MutableStateFlow(0)
    val highScore: StateFlow<Int> = _highScore.asStateFlow()

    // Tiempo de juego
    private val _gameTimer = MutableStateFlow(0L)
    val gameTimer: StateFlow<Long> = _gameTimer.asStateFlow()

    private var timerJob: Job? = null

    // Función para establecer el personaje seleccionado
    fun setSelectedCharacter(character: GameCharacter) {
        if (_gameState.value !is GameState.Playing) {
            _selectedCharacter.value = character
            resetGameState()
        }
    }

    // Función para obtener el personaje actual
    fun getSelectedCharacter(): GameCharacter? {
        return _selectedCharacter.value
    }

    // Función para iniciar el juego
    fun startGame() {
        if (_selectedCharacter.value != null) {
            resetGameState()
            startTimer()
            _gameState.value = GameState.Playing(
                level = 1,
                score = 0,
                coinsCollected = 0,
                playerPosition = Position(
                    x = (GAME_AREA_RIGHT - PLAYER_SIZE) / 2,  // Centro X
                    y = (GAME_AREA_BOTTOM - PLAYER_SIZE) / 2  // Centro Y
                ),
                currentCoinPosition = generateRandomCoinPosition(),
                isPaused = false,
                timeElapsed = 0L
            )
        }
    }

    // Función para reiniciar el juego
    fun restartGame() {
        resetGameState()
        startGame()
    }

    // Control del temporizador
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000) // Actualizar cada segundo
                _gameTimer.value += 1000
                updateGameStateWithTime()
            }
        }
    }

    private fun updateGameStateWithTime() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing && !currentState.isPaused) {
            _gameTimer.value += 1000
            _gameState.value = currentState.copy(
                timeElapsed = _gameTimer.value
            )
        }
    }

    // Función para generar una posición aleatoria para la moneda
    private fun generateRandomCoinPosition(): Position {
        // Mantenemos un pequeño margen para las monedas para que sean siempre visibles
        val coinMargin = 10f
        val safeAreaWidth = GAME_AREA_RIGHT - COIN_SIZE - (coinMargin * 2)
        val safeAreaHeight = GAME_AREA_BOTTOM - COIN_SIZE - (coinMargin * 2)

        return Position(
            x = coinMargin + (Math.random() * safeAreaWidth).toFloat(),
            y = coinMargin + (Math.random() * safeAreaHeight).toFloat()
        )
    }

    // Función para actualizar la posición del jugador
    fun updatePlayerPosition(newPosition: Position) {
        val currentState = _gameState.value
        if (currentState is GameState.Playing && !currentState.isPaused) {
            // Validar los límites con los nuevos valores
            val validX = newPosition.x.coerceIn(
                GAME_AREA_LEFT,
                GAME_AREA_RIGHT - PLAYER_SIZE  // Ajustado para limitar más a la derecha
            )
            val validY = newPosition.y.coerceIn(
                GAME_AREA_TOP,
                GAME_AREA_BOTTOM - PLAYER_SIZE // Ajustado para limitar más abajo
            )

            _gameState.value = currentState.copy(
                playerPosition = Position(validX, validY)
            )

            checkCoinCollection(currentState)
        }
    }

    // Función para verificar la colección de monedas
    private fun checkCoinCollection(currentState: GameState.Playing) {
        val distance = calculateDistance(
            currentState.playerPosition,
            currentState.currentCoinPosition
        )

        if (distance < 30f) {
            val newCoinsCollected = currentState.coinsCollected + 1
            val newScore = currentState.score + 10

            if (newCoinsCollected >= 10) {
                completeLevelAndCalculateStars(currentState.level, newScore)
            } else {
                _gameState.value = currentState.copy(
                    coinsCollected = newCoinsCollected,
                    score = newScore,
                    currentCoinPosition = generateRandomCoinPosition()
                )
            }
        }
    }

    private fun completeLevelAndCalculateStars(level: Int, finalScore: Int) {
        val timeElapsed = _gameTimer.value
        val stars = calculateStars(timeElapsed, finalScore)

        timerJob?.cancel()

        _gameState.value = GameState.LevelCompleted(
            level = level,
            finalScore = finalScore,
            timeElapsed = timeElapsed,
            stars = stars
        )

        if (finalScore > _highScore.value) {
            _highScore.value = finalScore
        }
    }

    private fun calculateStars(timeElapsed: Long, score: Int): Int {
        return when {
            timeElapsed <= 30000 && score == 100 -> 3 // Perfecto: menos de 30s
            timeElapsed <= 45000 && score >= 80 -> 2  // Muy bien: menos de 45s y buen score
            else -> 1                                 // Completado
        }
    }

    // Función para calcular la distancia entre dos posiciones
    private fun calculateDistance(pos1: Position, pos2: Position): Float {
        val dx = pos1.x - pos2.x
        val dy = pos1.y - pos2.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    // Control de pausa
    fun pauseGame() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing && !currentState.isPaused) {
            timerJob?.cancel()  // Pausar el timer
            _gameState.value = currentState.copy(isPaused = true)
        }
    }

    fun resumeGame() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing && currentState.isPaused) {
            startTimer()  // Reiniciar el timer
            _gameState.value = currentState.copy(isPaused = false)
        }
    }

    fun requestExit() {
        pauseGame() // Aseguramos que el juego se pause antes de mostrar el diálogo
        _gameState.value = GameState.ExitConfirmation
    }

    fun cancelExit() {
        val currentState = _gameState.value
        if (currentState is GameState.ExitConfirmation) {
            val previousState = gameState.value as? GameState.Playing
            if (previousState != null) {
                _gameState.value = previousState.copy(isPaused = true)
            } else {
                _gameState.value = GameState.Playing(
                    level = 1,
                    score = 0,
                    coinsCollected = 0,
                    playerPosition = Position(150f, 150f),
                    currentCoinPosition = generateRandomCoinPosition(),
                    isPaused = true,
                    timeElapsed = _gameTimer.value
                )
            }
        }
    }

    fun confirmExit() {
        timerJob?.cancel()
        resetGameState()
        _selectedCharacter.value = null // Optional: si quieres resetear también el personaje
    }

    private fun resetGameState() {
        timerJob?.cancel()
        _gameTimer.value = 0L
        _gameState.value = GameState.NotStarted
    }

    // Función para guardar el estado del juego
    fun saveGameState() {
        viewModelScope.launch {
            // Implementar guardado de estado
        }
    }

    // Limpieza de recursos
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}