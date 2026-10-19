package com.dearyoti.lexiladder.domain.model

data class UserProgress(
    val wordId: String,
    val reviewCount: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val isMastered: Boolean = false,
    val isFavorite: Boolean = false,
    val nextReviewAt: Long = 0L,
    val lastReviewedAt: Long = 0L
) {
    val accuracy: Float
        get() = if (reviewCount > 0) (correctCount.toFloat() / reviewCount.toFloat()) * 100f else 0f
}
