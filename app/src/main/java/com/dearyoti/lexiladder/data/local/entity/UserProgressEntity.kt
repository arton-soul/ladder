package com.dearyoti.lexiladder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dearyoti.lexiladder.domain.model.UserProgress

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val wordId: String,
    val reviewCount: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
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
            isMastered = progress.isMastered,
            isFavorite = progress.isFavorite,
            nextReviewAt = progress.nextReviewAt,
            lastReviewedAt = progress.lastReviewedAt
        )
    }
}
