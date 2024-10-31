package com.ucsur.coinquest.ui.screens

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

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingsTopBar(onNavigateBack)
        Spacer(modifier = Modifier.height(32.dp))
        SoundSettings(settings, viewModel)
    }
}

@Composable
private fun SettingsTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
        }
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
private fun SoundSettings(
    settings: Settings,
    viewModel: SettingsViewModel
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
            SoundToggleRow(
                title = "Efectos de sonido",
                isEnabled = settings.isSoundEnabled,
                onToggle = viewModel::toggleSound
            )

            if (settings.isSoundEnabled) {
                VolumeSlider(
                    title = "Volumen de efectos",
                    volume = settings.soundVolume,
                    onVolumeChange = viewModel::updateSoundVolume,
                    isEnabled = settings.isSoundEnabled
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Música de fondo
            SoundToggleRow(
                title = "Música de fondo",
                isEnabled = settings.isMusicEnabled,
                onToggle = viewModel::toggleMusic
            )

            if (settings.isMusicEnabled) {
                VolumeSlider(
                    title = "Volumen de música",
                    volume = settings.musicVolume,
                    onVolumeChange = viewModel::updateMusicVolume,
                    isEnabled = settings.isMusicEnabled
                )
            }
        }
    }
}

@Composable
private fun SoundToggleRow(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun VolumeSlider(
    title: String,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    isEnabled: Boolean
) {
    Text(
        text = title,
        modifier = Modifier.padding(top = 8.dp)
    )
    Slider(
        value = volume,
        onValueChange = onVolumeChange,
        enabled = isEnabled
    )
}