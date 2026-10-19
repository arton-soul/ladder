package com.dearyoti.lexiladder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dearyoti.lexiladder.domain.model.LearningStage
import com.dearyoti.lexiladder.domain.model.ReviewRating
import com.dearyoti.lexiladder.domain.model.UserProgress

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val wordId: String,
    val reviewCount: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val consecutiveCorrect: Int = 0,
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 0,
    val learningStage: String = "NEW",
    val lastRating: String? = null,
    val isMastered: Boolean = false,
    val isFavorite: Boolean = false,
    val nextReviewAt: Long = 0L,
    val lastReviewedAt: Long = 0L
) {
    fun toDomainModel(): UserProgress = UserProgress(
        wordId = wordId,
        reviewCount = reviewCount,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        consecutiveCorrect = consecutiveCorrect,
        easeFactor = easeFactor,
        intervalDays = intervalDays,
        learningStage = try {
            LearningStage.valueOf(learningStage)
        } catch (_: Exception) {
            LearningStage.NEW
        },
        lastRating = lastRating?.let {
            try {
                ReviewRating.valueOf(it)
            } catch (_: Exception) {
                null
            }
        },
        isMastered = isMastered,
        isFavorite = isFavorite,
        nextReviewAt = nextReviewAt,
        lastReviewedAt = lastReviewedAt
    )

    companion object {
        fun fromDomainModel(progress: UserProgress): UserProgressEntity = UserProgressEntity(
            wordId = progress.wordId,
            reviewCount = progress.reviewCount,
            correctCount = progress.correctCount,
            incorrectCount = progress.incorrectCount,
            consecutiveCorrect = progress.consecutiveCorrect,
            easeFactor = progress.easeFactor,
            intervalDays = progress.intervalDays,
            learningStage = progress.learningStage.name,
            lastRating = progress.lastRating?.name,
            isMastered = progress.isMastered,
            isFavorite = progress.isFavorite,
            nextReviewAt = progress.nextReviewAt,
            lastReviewedAt = progress.lastReviewedAt
        )
    }
}
