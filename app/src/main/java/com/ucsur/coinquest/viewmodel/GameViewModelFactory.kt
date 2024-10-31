package com.ucsur.coinquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ucsur.coinquest.utils.SoundManager

class GameViewModelFactory(private val soundManager: SoundManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(soundManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}