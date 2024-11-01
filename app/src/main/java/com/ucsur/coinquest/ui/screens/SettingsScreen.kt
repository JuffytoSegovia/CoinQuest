package com.ucsur.coinquest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.ucsur.coinquest.viewmodel.SettingsViewModel
import com.ucsur.coinquest.model.Settings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val scope = rememberCoroutineScope()
    var hasChanges by remember { mutableStateOf(false) }
    var showSaveConfirmation by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }

    // Función para manejar la salida
    fun handleExit() {
        if (hasChanges) {
            showExitConfirmation = true
        } else {
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Barra superior con botón de retroceso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { handleExit() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
            }
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Ajustes de Sonido
        SoundSettings(
            settings = settings,
            viewModel = viewModel,
            onSettingsChanged = { hasChanges = true }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón Guardar centrado
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    scope.launch {
                        viewModel.saveSettings()
                        hasChanges = false
                        showSaveConfirmation = true
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .padding(vertical = 16.dp),
                enabled = hasChanges
            ) {
                Text("Guardar Cambios")
            }
        }
    }

    // Diálogo de confirmación de guardado
    if (showSaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showSaveConfirmation = false },
            title = { Text("Ajustes Guardados") },
            text = { Text("Los cambios se han guardado correctamente.") },
            confirmButton = {
                Button(
                    onClick = { showSaveConfirmation = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Diálogo de confirmación al salir con cambios sin guardar
    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Cambios sin guardar") },
            text = { Text("¿Deseas guardar los cambios antes de salir?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.saveSettings()
                            showExitConfirmation = false
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("Guardar y salir")
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            showExitConfirmation = false
                            onNavigateBack()
                        }
                    ) {
                        Text("Salir sin guardar")
                    }
                    TextButton(
                        onClick = { showExitConfirmation = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    // Manejar el botón de retroceso del sistema
    BackHandler { handleExit() }
}

@Composable
private fun SoundSettings(
    settings: Settings,
    viewModel: SettingsViewModel,
    onSettingsChanged: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sonido",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Efectos de sonido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Efectos de sonido")
                Switch(
                    checked = settings.isSoundEnabled,
                    onCheckedChange = {
                        viewModel.toggleSound(it)
                        onSettingsChanged()
                    }
                )
            }

            if (settings.isSoundEnabled) {
                Text(
                    text = "Volumen de efectos",
                    modifier = Modifier.padding(top = 8.dp)
                )
                Slider(
                    value = settings.soundVolume,
                    onValueChange = {
                        viewModel.updateSoundVolume(it)
                        onSettingsChanged()
                    },
                    enabled = settings.isSoundEnabled
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Música de fondo")
                Switch(
                    checked = settings.isMusicEnabled,
                    onCheckedChange = {
                        viewModel.toggleMusic(it)
                        onSettingsChanged()
                    }
                )
            }

            if (settings.isMusicEnabled) {
                Text(
                    text = "Volumen de música",
                    modifier = Modifier.padding(top = 8.dp)
                )
                Slider(
                    value = settings.musicVolume,
                    onValueChange = {
                        viewModel.updateMusicVolume(it)
                        onSettingsChanged()
                    },
                    enabled = settings.isMusicEnabled
                )
            }
        }
    }
}