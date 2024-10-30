package com.ucsur.coinquest.model

data class GameCharacter(
    val id: Int,
    val defaultName: String,
    val imageRes: Int,
    var customName: String? = null
)