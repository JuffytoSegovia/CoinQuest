package com.ucsur.coinquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ucsur.coinquest.data.SettingsDataStore
import com.ucsur.coinquest.navigation.NavGraph
import com.ucsur.coinquest.ui.theme.CoinQuestTheme
import com.ucsur.coinquest.utils.SoundManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager
    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundManager = SoundManager(this)
        settingsDataStore = SettingsDataStore(this)

        // Cargar ajustes guardados inmediatamente
        runBlocking {
            settingsDataStore.settingsFlow.first().let { settings ->
                soundManager.apply {
                    toggleSound(settings.isSoundEnabled)
                    toggleMusic(settings.isMusicEnabled)
                    setSoundVolume(settings.soundVolume)
                    setMusicVolume(settings.musicVolume)
                }
            }
        }

        setContent {
            CoinQuestTheme {
                NavGraph(
                    soundManager = soundManager,  // Pasar el soundManager
                    onExitApp = {
                        soundManager.release()
                        finish()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundManager.resumeBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        soundManager.pauseBackgroundMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}