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
    val LEVEL_1 = LevelConfig(
        levelNumber = 1,
        requiredCoins = 3, // Temporalmente 3 monedas para pruebas
        timeLimit = 60000L,
        baseScore = 10,
        threeStarTime = 45000L,
        twoStarTime = 55000L
    )

    val LEVEL_2 = LevelConfig(
        levelNumber = 2,
        requiredCoins = 5, // Temporalmente 5 monedas para pruebas
        timeLimit = 75000L,      // 1 minuto y 15 segundos
        baseScore = 15,          // Más puntos por moneda
        threeStarTime = 60000L,  // 1 minuto para 3 estrellas
        twoStarTime = 70000L,    // 1 minuto y 10 segundos para 2 estrellas
        hasMovingCoins = true,
        coinMovementSpeed = 1.5f, // Reducido a 1.5 para mejor jugabilidad
        coinMovementRange = 100f
    )

    // Para facilitar el cambio entre modo prueba y modo normal
    private const val TEST_MODE = true // Cambiar a false para versión final

    fun getConfigForLevel(level: Int): LevelConfig {
        val config = when (level) {
            1 -> LEVEL_1
            2 -> LEVEL_2
            else -> LEVEL_1
        }

        return if (TEST_MODE) {
            config.copy(
                requiredCoins = when (level) {
                    1 -> 3  // Monedas para pruebas nivel 1
                    2 -> 5  // Monedas para pruebas nivel 2
                    else -> 3
                }
            )
        } else {
            config
        }
    }
}