package com.ucsur.coinquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsur.coinquest.model.Settings
import com.ucsur.coinquest.utils.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val soundManager: SoundManager
) : ViewModel() {
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    fun toggleSound(enabled: Boolean) {
        _settings.value = _settings.value.copy(isSoundEnabled = enabled)
        soundManager.toggleSound(enabled)
    }

    fun toggleMusic(enabled: Boolean) {
        _settings.value = _settings.value.copy(isMusicEnabled = enabled)
        soundManager.toggleMusic(enabled)
    }

    fun toggleVibration(enabled: Boolean) {
        _settings.value = _settings.value.copy(isVibrationEnabled = enabled)
    }

    fun updateSoundVolume(volume: Float) {
        _settings.value = _settings.value.copy(soundVolume = volume)
        soundManager.setSoundVolume(volume)
        // Reproducir un sonido de prueba
        soundManager.playButtonSound()
    }

    fun updateMusicVolume(volume: Float) {
        _settings.value = _settings.value.copy(musicVolume = volume)
        soundManager.setMusicVolume(volume)
    }

    // Aquí añadiremos después la lógica para guardar/cargar ajustes
}