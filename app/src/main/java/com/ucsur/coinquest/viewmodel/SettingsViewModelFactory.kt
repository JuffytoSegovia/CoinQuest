package com.ucsur.coinquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ucsur.coinquest.data.SettingsDataStore
import com.ucsur.coinquest.utils.SoundManager

class SettingsViewModelFactory(
    private val soundManager: SoundManager,
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(soundManager, settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}