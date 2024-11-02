package com.ucsur.coinquest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import com.ucsur.coinquest.data.SettingsDataStore
import com.ucsur.coinquest.navigation.NavGraph
import com.ucsur.coinquest.ui.theme.CoinQuestTheme
import com.ucsur.coinquest.utils.SoundManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager
    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        soundManager = SoundManager(this)
        settingsDataStore = SettingsDataStore(this)

        // Verificación temporal
        val db = Firebase.firestore
        db.collection("scores")
            .add(mapOf("test" to "test_connection"))
            .addOnSuccessListener {
                Log.d("Firebase", "Conexión exitosa: ${it.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error de conexión", e)
            }

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