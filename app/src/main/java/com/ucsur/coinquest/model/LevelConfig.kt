package com.ucsur.coinquest.model

data class LevelConfig(
    val levelNumber: Int,
    val requiredCoins: Int,
    val timeLimit: Long,
    val baseScore: Int,
    val threeStarTime: Long,
    val twoStarTime: Long,
    val hasMovingCoins: Boolean = false,
    val coinMovementSpeed: Float = 0f,
    val coinMovementRange: Float = 0f
)

object LevelConfigurations {
    // Constante para cambiar entre modos
    private const val TEST_MODE = false // Cambiar a false para modo final

    // Configuraciones para modo TEST
    private val TEST_LEVEL_1 = LevelConfig(
        levelNumber = 1,
        requiredCoins = 3,
        timeLimit = 15000L,         // 15 segundos total
        baseScore = 10,
        threeStarTime = 4000L,      // 4 segundos para 3 estrellas
        twoStarTime = 5000L,        // 5 segundos para 2 estrellas
        // Más de 5 segundos = 1 estrella
        hasMovingCoins = false
    )

    private val TEST_LEVEL_2 = LevelConfig(
        levelNumber = 2,
        requiredCoins = 5,
        timeLimit = 20000L,         // 20 segundos total
        baseScore = 15,
        threeStarTime = 10000L,     // 10 segundos para 3 estrellas
        twoStarTime = 12000L,       // 12 segundos para 2 estrellas
        // Más de 12 segundos = 1 estrella
        hasMovingCoins = true,
        coinMovementSpeed = 1.5f,
        coinMovementRange = 100f
    )

    // Configuraciones para modo FINAL
    private val FINAL_LEVEL_1 = LevelConfig(
        levelNumber = 1,
        requiredCoins = 10,
        timeLimit = 45000L,         // 45 segundos total
        baseScore = 10,
        threeStarTime = 24000L,     // 24 segundos para 3 estrellas
        twoStarTime = 28000L,       // 28 segundos para 2 estrellas
        // Más de 28 segundos = 1 estrella
        hasMovingCoins = false
    )

    private val FINAL_LEVEL_2 = LevelConfig(
        levelNumber = 2,
        requiredCoins = 15,
        timeLimit = 60000L,         // 60 segundos total
        baseScore = 15,
        threeStarTime = 35000L,     // 30 segundos para 3 estrellas
        twoStarTime = 40000L,       // 40 segundos para 2 estrellas
        // Más de 40 segundos = 1 estrella
        hasMovingCoins = true,
        coinMovementSpeed = 1.5f,
        coinMovementRange = 100f
    )

    fun getConfigForLevel(level: Int): LevelConfig {
        return if (TEST_MODE) {
            when (level) {
                1 -> TEST_LEVEL_1
                2 -> TEST_LEVEL_2
                else -> TEST_LEVEL_1
            }
        } else {
            when (level) {
                1 -> FINAL_LEVEL_1
                2 -> FINAL_LEVEL_2
                else -> FINAL_LEVEL_1
            }
        }
    }
}