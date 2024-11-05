package com.ucsur.coinquest.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ucsur.coinquest.R
import com.ucsur.coinquest.model.GameState
import com.ucsur.coinquest.model.Position
import com.ucsur.coinquest.viewmodel.GameViewModel
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import java.util.Locale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import com.ucsur.coinquest.model.LevelConfigurations
import com.ucsur.coinquest.ui.theme.SecondaryButtonColor
import com.ucsur.coinquest.utils.SoundManager

// Función para formatear el tiempo - fuera de las composables
private fun formatTime(timeInMillis: Long): String {
    val seconds = (timeInMillis / 1000) % 60
    val minutes = (timeInMillis / (1000 * 60)) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onNavigateToCharacterSelect: () -> Unit,
    onNavigateToMenu: () -> Unit,
    soundManager: SoundManager
) {
    val gameState by viewModel.gameState.collectAsState()
    val selectedCharacter by viewModel.selectedCharacter.collectAsState()
    val gameTimer by viewModel.gameTimer.collectAsState()

    BackHandler {
        when (gameState) {
            is GameState.NotStarted -> onNavigateToCharacterSelect()
            is GameState.Playing -> viewModel.pauseGame()
            is GameState.ExitConfirmation -> viewModel.cancelExit()
            is GameState.LevelCompleted -> {}
            is GameState.GameOver -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        when (val currentState = gameState) {
            is GameState.NotStarted -> {
                if (selectedCharacter != null) {
                    StartGameScreen(
                        characterName = selectedCharacter?.customName ?: selectedCharacter?.defaultName ?: "",
                        onStartGame = { viewModel.startGame() },
                        soundManager = soundManager
                    )
                }
            }

            is GameState.Playing -> {
                GamePlayScreen(
                    state = currentState,
                    characterImageRes = selectedCharacter?.imageRes ?: 0,
                    timeElapsed = gameTimer,
                    onMove = viewModel::updatePlayerPosition,
                    onPause = viewModel::pauseGame,
                    onResume = viewModel::resumeGame,
                    onExit = viewModel::requestExit,
                    soundManager = soundManager
                )
            }

            is GameState.LevelCompleted -> {
                LevelCompletedScreen(
                    state = currentState,
                    onBackToMenu = onNavigateToMenu,
                    onRestartGame = viewModel::restartGame,
                    onNextLevel = viewModel::startNextLevel,
                    soundManager = soundManager
                )
            }

            is GameState.GameOver -> {
                GameOverScreen(
                    state = currentState,
                    onRestartGame = viewModel::restartFromGameOver,
                    onBackToMenu = onNavigateToMenu,
                    soundManager = soundManager
                )
            }

            is GameState.ExitConfirmation -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                ) {
                    ExitConfirmationDialog(
                        onConfirm = {
                            viewModel.confirmExit()
                            onNavigateToMenu()
                        },
                        onDismiss = {
                            viewModel.cancelExit()
                        },
                        soundManager = soundManager
                    )
                }
            }
        }
    }
}

@Composable
private fun StartGameScreen(
    characterName: String,
    onStartGame: () -> Unit,
    soundManager: SoundManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido, $characterName!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    soundManager.playButtonSound()
                    onStartGame()
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp)
            ) {
                Text("Comenzar Nivel 1")
            }
        }
    }
}

@Composable
private fun GamePlayScreen(
    state: GameState.Playing,
    characterImageRes: Int,
    timeElapsed: Long,
    onMove: (Position) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onExit: () -> Unit,
    soundManager: SoundManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            GameHUD(
                level = state.level,
                score = state.score,
                coins = state.coinsCollected,
                timeElapsed = timeElapsed,
                onPause = onPause,
                soundManager = soundManager,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Primero dibujamos el contenedor con borde
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                            RoundedCornerShape(0.dp)  // Quitamos el redondeo
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(0.dp)  // Quitamos el redondeo
                        )
                ) {
                    // Luego dibujamos la cuadrícula
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSize = 50f
                        for (x in 0..size.width.toInt() step gridSize.toInt()) {
                            drawLine(
                                Color.Gray.copy(alpha = 0.3f),
                                start = Offset(x.toFloat(), 0f),
                                end = Offset(x.toFloat(), size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (y in 0..size.height.toInt() step gridSize.toInt()) {
                            drawLine(
                                Color.Gray.copy(alpha = 0.3f),
                                start = Offset(0f, y.toFloat()),
                                end = Offset(size.width, y.toFloat()),
                                strokeWidth = 1f
                            )
                        }
                    }

                    // El Box para el personaje y la moneda
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = characterImageRes),
                            contentDescription = "Player Character",
                            modifier = Modifier
                                .size(GameViewModel.PLAYER_SIZE.dp)
                                .offset(
                                    x = state.playerPosition.x.dp,
                                    y = state.playerPosition.y.dp
                                )
                        )

                        Image(
                            painter = painterResource(id = R.drawable.coin),
                            contentDescription = "Coin",
                            modifier = Modifier
                                .size(GameViewModel.COIN_SIZE.dp)
                                .offset(
                                    x = state.currentCoinPosition.x.dp,
                                    y = state.currentCoinPosition.y.dp
                                )
                        )
                    }
                }
            }

            GameControls(
                modifier = Modifier.fillMaxWidth(),
                onMove = onMove,
                currentPosition = state.playerPosition
            )
        }

        if (state.isPaused) {
            PauseOverlay(
                onResume = onResume,
                onExit = onExit,
                soundManager = soundManager
            )
        }
    }
}

@Composable
private fun GameHUD(
    level: Int,
    score: Int,
    coins: Int,
    timeElapsed: Long,
    onPause: () -> Unit,
    soundManager: SoundManager,
    modifier: Modifier = Modifier
) {
    val levelConfig = LevelConfigurations.getConfigForLevel(level)
    val timeRemaining = (levelConfig.timeLimit - timeElapsed).coerceAtLeast(0L)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Nivel: $level")
            Text("Puntuación: $score")
            Text(
                text = "Monedas: $coins/${levelConfig.requiredCoins}",
                color = if (coins >= levelConfig.requiredCoins) MaterialTheme.colorScheme.primary else Color.Unspecified
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTime(timeRemaining),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    timeRemaining <= 10000 -> Color.Red
                    timeRemaining <= 20000 -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = "Tiempo restante",
                fontSize = 12.sp
            )
        }

        IconButton(
            onClick = {
                soundManager.playButtonSound()
                onPause()
            }
        ) {
            Icon(Icons.Outlined.Pause, "Pausar")
        }
    }
}

@Composable
private fun GameControls(
    modifier: Modifier = Modifier,
    onMove: (Position) -> Unit,
    currentPosition: Position
) {
    val haptic = LocalHapticFeedback.current

    @Composable
    fun DirectionalButton(
        onClick: () -> Unit,
        icon: ImageVector,
        contentDescription: String
    ) {
        Box(
            modifier = Modifier
                .requiredSize(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(36.dp),
                tint = Color.White
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DirectionalButton(
                onClick = { onMove(Position(currentPosition.x, currentPosition.y - GameViewModel.MOVEMENT_STEP)) },
                icon = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Arriba"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                DirectionalButton(
                    onClick = { onMove(Position(currentPosition.x - GameViewModel.MOVEMENT_STEP, currentPosition.y)) },
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Izquierda"
                )

                DirectionalButton(
                    onClick = { onMove(Position(currentPosition.x + GameViewModel.MOVEMENT_STEP, currentPosition.y)) },
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Derecha"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            DirectionalButton(
                onClick = { onMove(Position(currentPosition.x, currentPosition.y + GameViewModel.MOVEMENT_STEP)) },
                icon = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Abajo"
            )
        }
    }
}

@Composable
private fun PauseOverlay(
    onResume: () -> Unit,
    onExit: () -> Unit,
    soundManager: SoundManager
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Juego Pausado",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    soundManager.playButtonSound()
                    onResume()
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    soundManager.playButtonSound()
                    onExit()
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "Salir",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LevelCompletedScreen(
    state: GameState.LevelCompleted,
    onBackToMenu: () -> Unit,
    onRestartGame: () -> Unit,
    onNextLevel: () -> Unit,
    soundManager: SoundManager
) {
    var showExitConfirmation by remember { mutableStateOf(false) }

    BackHandler {
        showExitConfirmation = true
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("¿Salir del juego?") },
            text = { Text("Perderás el progreso del nivel completado") },
            confirmButton = {
                Button(
                    onClick = {
                        soundManager.playButtonSound()
                        onBackToMenu()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Salir")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        soundManager.playButtonSound()
                        showExitConfirmation = false
                    }
                ) {
                    Text("Continuar")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Nivel ${state.level} Completado!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Puntuación: ${state.finalScore}",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Tiempo: ${formatTime(state.timeElapsed)}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Estrella",
                        tint = if (index < state.stars) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botones en columna vertical
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onRestartGame,
                    modifier = Modifier
                        .width(250.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        "Jugar de nuevo",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (state.nextLevelUnlocked && state.level < 2) {
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier
                            .width(250.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Siguiente Nivel",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showExitConfirmation = true },
                    modifier = Modifier
                        .width(250.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryButtonColor
                    )
                ) {
                    Text(
                        "Volver al menú",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    soundManager: SoundManager
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Salir del juego?") },
        text = { Text("Perderás todo el progreso actual") },
        confirmButton = {
            Button(
                onClick = {
                    soundManager.playButtonSound()
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Salir")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    soundManager.playButtonSound()
                    onDismiss()
                }
            ) {
                Text("Continuar jugando")
            }
        }
    )
}

@Composable
private fun GameOverScreen(
    state: GameState.GameOver,
    onRestartGame: () -> Unit,
    onBackToMenu: () -> Unit,
    soundManager: SoundManager
) {
    var showExitConfirmation by remember { mutableStateOf(false) }

    BackHandler {
        showExitConfirmation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Tiempo Agotado!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nivel ${state.level}",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Puntuación: ${state.finalScore}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Monedas recolectadas: ${state.coinsCollected}/${state.totalCoins}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Tiempo: ${formatTime(state.timeElapsed)}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    soundManager.playButtonSound()
                    onRestartGame()
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Intentar de nuevo",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showExitConfirmation = true },
                modifier = Modifier
                    .width(250.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryButtonColor
                )
            ) {
                Text(
                    "Volver al menú",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("¿Salir del juego?") },
            text = { Text("¿Estás seguro que quieres volver al menú principal?") },
            confirmButton = {
                Button(
                    onClick = {
                        soundManager.playButtonSound()
                        onBackToMenu()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Salir")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        soundManager.playButtonSound()
                        showExitConfirmation = false
                    }
                ) {
                    Text("Continuar")
                }
            }
        )
    }
}