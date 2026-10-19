package com.dearyoti.lexiladder.domain.model

data class Level(
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val isUnlocked: Boolean = false,
    val isMastered: Boolean = false,
    val status: LevelStatus = LevelStatus.LOCKED,
    val totalWords: Int = 0,
    val studiedWords: Int = 0,
    val masteredWords: Int = 0,
    val requiredPassingScore: Int = 80
)
