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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer


@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onNavigateToCharacterSelect: () -> Unit,
    onNavigateToMenu: () -> Unit
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
                        onStartGame = { viewModel.startGame() }
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
                    onExit = viewModel::requestExit
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
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StartGameScreen(
    characterName: String,
    onStartGame: () -> Unit
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
            onClick = onStartGame,
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
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp) // Añadimos padding superior
    ) {
        GameHUD(
            level = state.level,
            score = state.score,
            coins = state.coinsCollected,
            timeElapsed = timeElapsed,
            onPause = onPause,
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

        if (state.isPaused) {
            PauseMenu(
                onResume = onResume,
                onExit = onExit
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
    modifier: Modifier = Modifier  // Añadir este parámetro
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

        IconButton(onClick = onPause) {
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)  // Altura fija para el área de controles
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
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onMove(Position(currentPosition.x, currentPosition.y - GameViewModel.MOVEMENT_STEP))
                },
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    "Arriba",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fila de botones izquierda y derecha
            Row(
                horizontalArrangement = Arrangement.spacedBy(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Botón Izquierda
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onMove(Position(currentPosition.x - GameViewModel.MOVEMENT_STEP, currentPosition.y))
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        "Izquierda",
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }

                // Botón Derecha
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onMove(Position(currentPosition.x + GameViewModel.MOVEMENT_STEP, currentPosition.y))
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        "Derecha",
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Abajo
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onMove(Position(currentPosition.x, currentPosition.y + GameViewModel.MOVEMENT_STEP))
                },
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    "Abajo",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun PauseMenu(
    onResume: () -> Unit,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Juego Pausado",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Button(
                onClick = onResume,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Continuar")
            }

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Salir")
            }
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
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Salir del juego?") },
        text = { Text("Perderás todo el progreso actual") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Salir")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
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

// Añadir esta función de extensión al final del archivo
private fun Modifier.pressedAlpha(alpha: Float): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    graphicsLayer(alpha = if (isPressed) alpha else 1f)
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) { }
}