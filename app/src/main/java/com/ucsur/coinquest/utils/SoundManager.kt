package com.ucsur.coinquest.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.ucsur.coinquest.R

class SoundManager(private val context: Context) {
    private var soundPool: SoundPool
    private var mediaPlayer: MediaPlayer? = null

    // IDs de los sonidos
    private var coinSound: Int = 0
    private var playerSound: Int = 0
    private var buttonSound: Int = 0
    private var levelCompletedSound: Int = 0

    // Control de volumen
    private var soundVolume: Float = 1.0f
    private var musicVolume: Float = 1.0f
    private var isSoundEnabled: Boolean = true
    private var isMusicEnabled: Boolean = true

    init {
        // Configurar SoundPool para efectos de sonido cortos
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        // Cargar los sonidos
        loadSounds()
    }

    private fun loadSounds() {
        coinSound = soundPool.load(context, R.raw.coin, 1)
        playerSound = soundPool.load(context, R.raw.player, 1)
        buttonSound = soundPool.load(context, R.raw.buttons, 1)
        levelCompletedSound = soundPool.load(context, R.raw.nivelcompleted, 1)
    }

    fun playCoinSound() {
        if (isSoundEnabled) {
            soundPool.play(coinSound, soundVolume, soundVolume, 1, 0, 1f)
        }
    }

    fun playPlayerSound() {
        if (isSoundEnabled) {
            soundPool.play(playerSound, soundVolume, soundVolume, 1, 0, 1f)
        }
    }

    fun playButtonSound() {
        if (isSoundEnabled) {
            soundPool.play(buttonSound, soundVolume, soundVolume, 1, 0, 1f)
        }
    }

    fun playLevelCompletedSound() {
        if (isSoundEnabled) {
            soundPool.play(levelCompletedSound, soundVolume, soundVolume, 1, 0, 1f)
        }
    }

    fun startBackgroundMusic() {
        if (isMusicEnabled) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, R.raw.background)
            mediaPlayer?.apply {
                isLooping = true
                setVolume(musicVolume, musicVolume)
                start()
            }
        }
    }

    fun pauseBackgroundMusic() {
        mediaPlayer?.pause()
    }

    fun resumeBackgroundMusic() {
        if (isMusicEnabled) {
            mediaPlayer?.start()
        }
    }

    fun stopBackgroundMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.prepare()
    }

    // Funciones para control de volumen y estados
    fun setSoundVolume(volume: Float) {
        soundVolume = volume.coerceIn(0f, 1f)
    }

    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(musicVolume, musicVolume)
    }

    fun toggleSound(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun toggleMusic(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            startBackgroundMusic()
        } else {
            pauseBackgroundMusic()
        }
    }

    // Limpieza de recursos
    fun release() {
        soundPool.release()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}