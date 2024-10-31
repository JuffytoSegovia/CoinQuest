package com.ucsur.coinquest.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucsur.coinquest.R
import androidx.activity.compose.BackHandler
import com.ucsur.coinquest.utils.SoundManager

@Composable
fun MenuScreen(
    onNavigateToGame: () -> Unit,
    onNavigateToScores: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCredits: () -> Unit,
    soundManager: SoundManager  // Añadir este parámetro
) {
    var selectedButton by remember { mutableStateOf<String?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Manejador del botón de retroceso
    BackHandler {
        showExitDialog = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje de bienvenida
            Text(
                text = "¡Bienvenido a",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )

            // Logo
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "Game Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(vertical = 8.dp)
            )

            // Título
            Text(
                text = "Coin Quest!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Botones del menú
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                MenuButton(
                    text = "Jugar",
                    isSelected = selectedButton == "jugar",
                    onClick = {
                        soundManager.playButtonSound()  // Añadir sonido
                        selectedButton = "jugar"
                        onNavigateToGame()
                    }
                )

                MenuButton(
                    text = "Puntajes",
                    isSelected = selectedButton == "puntajes",
                    onClick = {
                        soundManager.playButtonSound()
                        selectedButton = "puntajes"
                        onNavigateToScores()
                    }
                )

                MenuButton(
                    text = "Ajustes",
                    isSelected = selectedButton == "ajustes",
                    onClick = {
                        soundManager.playButtonSound()
                        selectedButton = "ajustes"
                        onNavigateToSettings()
                    }
                )

                MenuButton(
                    text = "Créditos",
                    isSelected = selectedButton == "creditos",
                    onClick = {
                        soundManager.playButtonSound()
                        selectedButton = "creditos"
                        onNavigateToCredits()
                    }
                )
            }
        }

        // Diálogo de confirmación de salida
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = {
                    Text(
                        "Salir del juego",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Text(
                        "¿Estás seguro que deseas salir?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            android.os.Process.killProcess(android.os.Process.myPid())
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
                        onClick = { showExitDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale = animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .scale(scale.value)
            .width(220.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}