package com.ucsur.coinquest.model

data class LevelConfig(
    val levelNumber: Int,
    val requiredCoins: Int,
    val timeLimit: Long,
    val baseScore: Int,
    val threeStarTime: Long,
    val twoStarTime: Long
)

object LevelConfigurations {
    val LEVEL_1 = LevelConfig(
        levelNumber = 1,
        requiredCoins = 10,
        timeLimit = 60000L,      // 60 segundos total
        baseScore = 10,
        threeStarTime = 45000L,  // 45 segundos para 3 estrellas
        twoStarTime = 55000L     // 55 segundos para 2 estrellas
    )

    fun getConfigForLevel(level: Int): LevelConfig {
        return when (level) {
            1 -> LEVEL_1
            // Aquí agregaremos más niveles después
            else -> LEVEL_1
        }
    }
}