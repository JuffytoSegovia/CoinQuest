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
import java.util.Locale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import com.ucsur.coinquest.utils.SoundManager


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

    // Manejador del botón de retroceso
    BackHandler {
        when (gameState) {
            is GameState.NotStarted -> {
                // Si estamos en la pantalla de bienvenida, volvemos a la selección de personaje
                onNavigateToCharacterSelect()
            }
            is GameState.Playing -> {
                // Si está jugando, mostrar menú de pausa
                viewModel.pauseGame()
            }
            is GameState.ExitConfirmation -> {
                // Si está en el diálogo de confirmación, cancelar
                viewModel.cancelExit()
            }
            else -> {
                // En otros estados, navegar al menú
                onNavigateToMenu()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val currentState = gameState) {
            is GameState.NotStarted -> {
                if (selectedCharacter != null) {
                    StartGameScreen(
                        characterName = selectedCharacter?.customName ?: selectedCharacter?.defaultName ?: "",
                        onStartGame = { viewModel.startGame() },
                        soundManager = soundManager  // Pasar el soundManager
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
                    soundManager = soundManager  // Pasar el soundManager
                )
            }

            is GameState.LevelCompleted -> {
                LevelCompletedScreen(
                    state = currentState,
                    onBackToMenu = onNavigateToMenu,
                    onRestartGame = viewModel::restartGame
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
                        soundManager = soundManager  // Pasar el soundManager
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
    soundManager: SoundManager  // Añadir este parámetro
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
                soundManager.playButtonSound()  // Añadir sonido
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
    Box(modifier = Modifier.fillMaxSize()) {  // Contenedor principal Box
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

            // Área de juego
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                // Cuadrícula
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridSize = 50f

                    // Dibujar líneas verticales
                    for (x in 0..size.width.toInt() step gridSize.toInt()) {
                        drawLine(
                            Color.Gray.copy(alpha = 0.3f),
                            start = Offset(x.toFloat(), 0f),
                            end = Offset(x.toFloat(), size.height),
                            strokeWidth = 1f
                        )
                    }

                    // Dibujar líneas horizontales
                    for (y in 0..size.height.toInt() step gridSize.toInt()) {
                        drawLine(
                            Color.Gray.copy(alpha = 0.3f),
                            start = Offset(0f, y.toFloat()),
                            end = Offset(size.width, y.toFloat()),
                            strokeWidth = 1f
                        )
                    }
                }

                // Personaje y moneda
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Personaje
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

                    // Moneda
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

            // Controles en la parte inferior
            GameControls(
                modifier = Modifier.fillMaxWidth(),
                onMove = onMove,
                currentPosition = state.playerPosition
            )
        }

        // Superposición del menú de pausa
        if (state.isPaused) {
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
    }
}

@Composable
private fun GameHUD(
    level: Int,
    score: Int,
    coins: Int,
    timeElapsed: Long,
    onPause: () -> Unit,
    soundManager: SoundManager,  // Añadir este parámetro
    modifier: Modifier = Modifier
) {
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
    )  {
        Column {
            Text("Nivel: $level")
            Text("Puntuación: $score")
            Text("Monedas: $coins/10")
        }

        Text(
            text = formatTime(timeElapsed),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = {
                soundManager.playButtonSound()  // Añadir sonido
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
                .requiredSize(60.dp) // Fuerza un tamaño exacto
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
            // Botón Arriba
            DirectionalButton(
                onClick = { onMove(Position(currentPosition.x, currentPosition.y - GameViewModel.MOVEMENT_STEP)) },
                icon = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Arriba"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fila de botones izquierda y derecha
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

            // Botón Abajo
            DirectionalButton(
                onClick = { onMove(Position(currentPosition.x, currentPosition.y + GameViewModel.MOVEMENT_STEP)) },
                icon = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Abajo"
            )
        }
    }
}

@Composable
private fun LevelCompletedScreen(
    state: GameState.LevelCompleted,
    onBackToMenu: () -> Unit,
    onRestartGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Nivel ${state.level} Completado!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Puntuación: ${state.finalScore}",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Tiempo: ${formatTime(state.timeElapsed)}",
            style = MaterialTheme.typography.titleMedium
        )

        // Mostrar estrellas
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Estrella",
                    tint = if (index < state.stars) Color(0xFFFFD700) else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onRestartGame) {
                Text("Jugar de nuevo")
            }

            Button(onClick = onBackToMenu) {
                Text("Volver al menú")
            }
        }
    }
}

@Composable
private fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    soundManager: SoundManager  // Añadir este parámetro
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Salir del juego?") },
        text = { Text("Perderás todo el progreso actual") },
        confirmButton = {
            Button(
                onClick = {
                    soundManager.playButtonSound()  // Añadir sonido
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
                    soundManager.playButtonSound()  // Añadir sonido
                    onDismiss()
                }
            ) {
                Text("Continuar jugando")
            }
        }
    )
}

private fun formatTime(timeInMillis: Long): String {
    val seconds = (timeInMillis / 1000) % 60
    val minutes = (timeInMillis / (1000 * 60)) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}