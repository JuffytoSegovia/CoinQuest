package com.ucsur.coinquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsur.coinquest.data.SettingsDataStore
import com.ucsur.coinquest.model.Settings
import com.ucsur.coinquest.utils.SoundManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SettingsViewModel(
    private val soundManager: SoundManager,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val settings: StateFlow<Settings> = settingsDataStore.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings()
        )

    private val _savingState = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            settings.collect { settings ->
                // Actualizar el SoundManager con los valores guardados
                soundManager.toggleSound(settings.isSoundEnabled)
                soundManager.toggleMusic(settings.isMusicEnabled)
                soundManager.setSoundVolume(settings.soundVolume)
                soundManager.setMusicVolume(settings.musicVolume)
            }
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateSoundEnabled(enabled)
            soundManager.toggleSound(enabled)
        }
    }

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateMusicEnabled(enabled)
            soundManager.toggleMusic(enabled)
        }
    }

    fun updateSoundVolume(volume: Float) {
        viewModelScope.launch {
            settingsDataStore.updateSoundVolume(volume)
            soundManager.setSoundVolume(volume)
            // Reproducir un sonido de prueba
            soundManager.playButtonSound()
        }
    }

    fun updateMusicVolume(volume: Float) {
        viewModelScope.launch {
            settingsDataStore.updateMusicVolume(volume)
            soundManager.setMusicVolume(volume)
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            _savingState.value = true
            // Aquí podrías agregar lógica adicional de guardado si es necesario
            delay(500) // Simular tiempo de guardado
            _savingState.value = false
        }
    }
}