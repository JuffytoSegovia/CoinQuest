package com.ucsur.coinquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsur.coinquest.model.GameState
import com.ucsur.coinquest.model.GameCharacter
import com.ucsur.coinquest.model.LevelConfig
import com.ucsur.coinquest.model.LevelConfigurations
import com.ucsur.coinquest.model.Position
import com.ucsur.coinquest.utils.SoundManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class GameViewModel(private val soundManager: SoundManager) : ViewModel() {

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

    // Init aquí
    init {
        soundManager.startBackgroundMusic()
    }

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

    private var coinMovementJob: Job? = null

    // Función para iniciar el juego
    fun startGame() {
        if (_selectedCharacter.value != null) {
            val currentLevel = (_gameState.value as? GameState.LevelCompleted)?.level?.plus(1) ?: 1
            val levelConfig = LevelConfigurations.getConfigForLevel(currentLevel)

            resetGameState()
            startTimer()

            val initialCoinPosition = generateRandomCoinPosition()
            _gameState.value = GameState.Playing(
                level = currentLevel,
                score = 0,
                coinsCollected = 0,
                playerPosition = Position(
                    x = (GAME_AREA_RIGHT - PLAYER_SIZE) / 2,
                    y = (GAME_AREA_BOTTOM - PLAYER_SIZE) / 2
                ),
                currentCoinPosition = initialCoinPosition,
                coinInitialPosition = if (levelConfig.hasMovingCoins) initialCoinPosition else null,
                isPaused = false,
                timeElapsed = 0L,
                timeLimit = levelConfig.timeLimit // Agregamos el timeLimit
            )

            if (levelConfig.hasMovingCoins) {
                startCoinMovement(levelConfig)
            }

            soundManager.resumeBackgroundMusic()
        }
    }

    private fun startCoinMovement(levelConfig: LevelConfig) {
        coinMovementJob?.cancel()
        coinMovementJob = viewModelScope.launch {
            while (isActive) {
                delay(16) // Aproximadamente 60 FPS
                updateCoinPosition(levelConfig)
            }
        }
    }

    private fun updateCoinPosition(levelConfig: LevelConfig) {
        val currentState = _gameState.value as? GameState.Playing ?: return
        if (currentState.isPaused) return

        val initialPosition = currentState.coinInitialPosition ?: return
        val currentDirection = currentState.coinMovementDirection
        val newX = currentState.currentCoinPosition.x + (levelConfig.coinMovementSpeed * currentDirection)

        // Verificar límites y cambiar dirección si es necesario
        val newDirection = when {
            newX <= GAME_AREA_LEFT -> 1f
            newX >= GAME_AREA_RIGHT - COIN_SIZE -> -1f
            else -> currentDirection
        }

        _gameState.value = currentState.copy(
            currentCoinPosition = Position(
                x = newX.coerceIn(GAME_AREA_LEFT, GAME_AREA_RIGHT - COIN_SIZE),
                y = currentState.currentCoinPosition.y
            ),
            coinMovementDirection = newDirection
        )
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
            soundManager.playPlayerSound() // Añadir sonido de movimiento
            val validX = newPosition.x.coerceIn(
                GAME_AREA_LEFT,
                GAME_AREA_RIGHT - PLAYER_SIZE
            )
            val validY = newPosition.y.coerceIn(
                GAME_AREA_TOP,
                GAME_AREA_BOTTOM - PLAYER_SIZE
            )

            _gameState.value = currentState.copy(
                playerPosition = Position(validX, validY)
            )

            checkCoinCollection(currentState)
        }
    }

    // Función para verificar la colección de monedas
    private fun checkCoinCollection(currentState: GameState.Playing) {
        val levelConfig = LevelConfigurations.getConfigForLevel(currentState.level)
        val distance = calculateDistance(
            currentState.playerPosition,
            currentState.currentCoinPosition
        )

        if (distance < 30f) {
            soundManager.playCoinSound()
            val newCoinsCollected = currentState.coinsCollected + 1
            val newScore = currentState.score + levelConfig.baseScore

            if (newCoinsCollected >= levelConfig.requiredCoins) {
                soundManager.playLevelCompletedSound()
                completeLevelAndCalculateStars(currentState.level, newScore)
            } else {
                val newCoinPosition = generateRandomCoinPosition()
                _gameState.value = currentState.copy(
                    coinsCollected = newCoinsCollected,
                    score = newScore,
                    currentCoinPosition = newCoinPosition,
                    coinInitialPosition = if (levelConfig.hasMovingCoins) newCoinPosition else null,
                    coinMovementDirection = 1f  // Reiniciar la dirección cuando se genera una nueva moneda
                )

                if (levelConfig.hasMovingCoins) {
                    startCoinMovement(levelConfig)  // Reiniciar el movimiento para la nueva moneda
                }
            }
        }
    }

    private fun completeLevelAndCalculateStars(level: Int, finalScore: Int) {
        val timeElapsed = _gameTimer.value
        val stars = calculateStars(timeElapsed, finalScore)

        timerJob?.cancel()
        coinMovementJob?.cancel()

        _gameState.value = GameState.LevelCompleted(
            level = level,
            finalScore = finalScore,
            timeElapsed = timeElapsed,
            stars = stars,
            nextLevelUnlocked = true
        )

        if (finalScore > _highScore.value) {
            _highScore.value = finalScore
        }
    }

    // Actualizar calculateStars para usar la configuración del nivel actual
    private fun calculateStars(timeElapsed: Long, score: Int): Int {
        val currentState = _gameState.value as? GameState.Playing ?: return 1
        val levelConfig = LevelConfigurations.getConfigForLevel(currentState.level)
        return when {
            timeElapsed <= levelConfig.threeStarTime &&
                    score == (levelConfig.requiredCoins * levelConfig.baseScore) -> 3
            timeElapsed <= levelConfig.twoStarTime &&
                    score >= (levelConfig.requiredCoins * levelConfig.baseScore * 0.8) -> 2
            else -> 1
        }
    }

    fun startNextLevel() {
        val currentState = _gameState.value as? GameState.LevelCompleted ?: return
        val nextLevel = currentState.level + 1

        if (nextLevel <= 2) {  // Por ahora solo tenemos 2 niveles
            val levelConfig = LevelConfigurations.getConfigForLevel(nextLevel)

            resetGameState()
            startTimer()

            val initialCoinPosition = generateRandomCoinPosition()
            _gameState.value = GameState.Playing(
                level = nextLevel,
                score = 0,
                coinsCollected = 0,
                playerPosition = Position(
                    x = (GAME_AREA_RIGHT - PLAYER_SIZE) / 2,
                    y = (GAME_AREA_BOTTOM - PLAYER_SIZE) / 2
                ),
                currentCoinPosition = initialCoinPosition,
                coinInitialPosition = if (levelConfig.hasMovingCoins) initialCoinPosition else null,
                isPaused = false,
                timeElapsed = 0L,
                timeLimit = levelConfig.timeLimit
            )

            if (levelConfig.hasMovingCoins) {
                startCoinMovement(levelConfig)
            }

            soundManager.resumeBackgroundMusic()
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
            timerJob?.cancel()
            coinMovementJob?.cancel() // Agregar esta línea
            soundManager.pauseBackgroundMusic()
            _gameState.value = currentState.copy(isPaused = true)
        }
    }

    fun resumeGame() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing && currentState.isPaused) {
            startTimer()
            val levelConfig = LevelConfigurations.getConfigForLevel(currentState.level)
            if (levelConfig.hasMovingCoins) {
                startCoinMovement(levelConfig) // Agregar esta línea
            }
            soundManager.resumeBackgroundMusic()
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
                // Obtenemos la configuración del nivel 1
                val levelConfig = LevelConfigurations.getConfigForLevel(1)
                _gameState.value = GameState.Playing(
                    level = 1,
                    score = 0,
                    coinsCollected = 0,
                    playerPosition = Position(150f, 150f),
                    currentCoinPosition = generateRandomCoinPosition(),
                    isPaused = true,
                    timeElapsed = _gameTimer.value,
                    timeLimit = levelConfig.timeLimit  // Agregamos el timeLimit del nivel
                )
            }
        }
    }

    fun confirmExit() {
        timerJob?.cancel()
        resetGameState()
        _selectedCharacter.value = null // Optional: si quieres resetear también el personaje
        soundManager.resumeBackgroundMusic()
    }

    private fun resetGameState() {
        timerJob?.cancel()
        coinMovementJob?.cancel()
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
        coinMovementJob?.cancel()
    }
}