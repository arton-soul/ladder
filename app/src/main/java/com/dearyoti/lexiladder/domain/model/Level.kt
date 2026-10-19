package com.dearyoti.lexiladder.domain.model

data class Level(
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val isUnlocked: Boolean = false,
    val totalWords: Int = 0,
    val masteredWords: Int = 0
)
