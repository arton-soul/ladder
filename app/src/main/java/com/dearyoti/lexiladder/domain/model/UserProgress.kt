package com.dearyoti.lexiladder.domain.model

enum class ReviewRating {
    WRONG, // 0 - Failed to recall
    HARD,  // 1 - Recalled with significant difficulty
    GOOD,  // 2 - Recalled with standard effort
    EASY   // 3 - Perfect, effortless recall
}

enum class LearningStage {
    NEW,
    LEARNING,
    REVIEWING,
    MASTERED
}

data class UserProgress(
    val wordId: String,
    val reviewCount: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val consecutiveCorrect: Int = 0,
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 0,
    val learningStage: LearningStage = LearningStage.NEW,
    val lastRating: ReviewRating? = null,
    val isMastered: Boolean = false,
    val isFavorite: Boolean = false,
    val nextReviewAt: Long = 0L,
    val lastReviewedAt: Long = 0L
) {
    val accuracy: Float
        get() = if (reviewCount > 0) (correctCount.toFloat() / reviewCount.toFloat()) * 100f else 0f
}
