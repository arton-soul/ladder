package com.dearyoti.lexiladder.domain.scheduler

import com.dearyoti.lexiladder.domain.model.LearningStage
import com.dearyoti.lexiladder.domain.model.ReviewRating
import com.dearyoti.lexiladder.domain.model.UserProgress
import kotlin.math.roundToInt

object SpacedRepetitionScheduler {

    const val MIN_EASE_FACTOR = 1.3f
    const val DEFAULT_EASE_FACTOR = 2.5f
    const val MAX_EASE_FACTOR = 3.0f

    const val ONE_HOUR_MS = 60 * 60 * 1000L
    const val ONE_DAY_MS = 24 * ONE_HOUR_MS

    /**
     * Calculates the next [UserProgress] state after a review session.
     *
     * @param current Current progress state for the word.
     * @param rating User performance rating: WRONG, HARD, GOOD, EASY.
     * @param now Current timestamp in milliseconds (defaults to system time).
     * @return Updated [UserProgress] with new intervals, ease factor, and mastery status.
     */
    fun scheduleNextReview(
        current: UserProgress,
        rating: ReviewRating,
        now: Long = System.currentTimeMillis()
    ): UserProgress {
        // Safe timestamp guard: if user's clock moved backwards before lastReviewedAt,
        // use lastReviewedAt as safe baseline to prevent negative time intervals.
        val effectiveNow = if (now < current.lastReviewedAt) current.lastReviewedAt else now

        val newReviewCount = current.reviewCount + 1
        var newCorrectCount = current.correctCount
        var newIncorrectCount = current.incorrectCount
        var newConsecutiveCorrect = current.consecutiveCorrect
        var newEaseFactor = current.easeFactor.coerceIn(MIN_EASE_FACTOR, MAX_EASE_FACTOR)
        var newIntervalDays = current.intervalDays
        var nextIntervalMs: Long
        var newStage = current.learningStage
        var newIsMastered = current.isMastered

        when (rating) {
            ReviewRating.WRONG -> {
                newIncorrectCount++
                newConsecutiveCorrect = 0
                // Ease factor decreases on failure
                newEaseFactor = (newEaseFactor - 0.20f).coerceIn(MIN_EASE_FACTOR, MAX_EASE_FACTOR)
                newIntervalDays = 0
                // Re-review in 4 hours (same day)
                nextIntervalMs = 4 * ONE_HOUR_MS
                newStage = LearningStage.LEARNING
                newIsMastered = false
            }

            ReviewRating.HARD -> {
                newCorrectCount++
                newConsecutiveCorrect++
                // Hard recall slightly reduces ease factor
                newEaseFactor = (newEaseFactor - 0.15f).coerceIn(MIN_EASE_FACTOR, MAX_EASE_FACTOR)
                newIntervalDays = 1
                nextIntervalMs = 1 * ONE_DAY_MS
                newStage = LearningStage.LEARNING
                // Hard never marks mastery
                newIsMastered = false
            }

            ReviewRating.GOOD -> {
                newCorrectCount++
                newConsecutiveCorrect++
                // Standard recall preserves ease factor

                newIntervalDays = when (newConsecutiveCorrect) {
                    1 -> 1
                    2 -> 3
                    else -> {
                        val base = if (current.intervalDays > 0) current.intervalDays else 2
                        (base * newEaseFactor).roundToInt().coerceAtLeast(base + 1)
                    }
                }
                nextIntervalMs = newIntervalDays * ONE_DAY_MS

                // Mastery threshold check:
                // Requires at least 3 consecutive correct recalls, interval >= 7 days, and overall accuracy >= 75%
                val newAccuracy = (newCorrectCount.toFloat() / newReviewCount.toFloat())
                if (newConsecutiveCorrect >= 3 && newIntervalDays >= 7 && newAccuracy >= 0.75f) {
                    newIsMastered = true
                    newStage = LearningStage.MASTERED
                } else {
                    newStage = LearningStage.REVIEWING
                }
            }

            ReviewRating.EASY -> {
                newCorrectCount++
                newConsecutiveCorrect++
                // Effortless recall increases ease factor
                newEaseFactor = (newEaseFactor + 0.15f).coerceIn(MIN_EASE_FACTOR, MAX_EASE_FACTOR)

                newIntervalDays = when (newConsecutiveCorrect) {
                    1 -> 2
                    2 -> 5
                    else -> {
                        val base = if (current.intervalDays > 0) current.intervalDays else 3
                        (base * newEaseFactor * 1.3f).roundToInt().coerceAtLeast(base + 2)
                    }
                }
                nextIntervalMs = newIntervalDays * ONE_DAY_MS

                val newAccuracy = (newCorrectCount.toFloat() / newReviewCount.toFloat())
                if (newConsecutiveCorrect >= 2 && newIntervalDays >= 7 && newAccuracy >= 0.75f) {
                    newIsMastered = true
                    newStage = LearningStage.MASTERED
                } else {
                    newStage = LearningStage.REVIEWING
                }
            }
        }

        return current.copy(
            reviewCount = newReviewCount,
            correctCount = newCorrectCount,
            incorrectCount = newIncorrectCount,
            consecutiveCorrect = newConsecutiveCorrect,
            easeFactor = newEaseFactor,
            intervalDays = newIntervalDays,
            learningStage = newStage,
            lastRating = rating,
            isMastered = newIsMastered,
            lastReviewedAt = effectiveNow,
            nextReviewAt = effectiveNow + nextIntervalMs
        )
    }

    /**
     * Determines whether a word is currently due for review.
     */
    fun isDueForReview(progress: UserProgress, now: Long = System.currentTimeMillis()): Boolean {
        if (progress.reviewCount == 0 || progress.nextReviewAt == 0L) return false
        return now >= progress.nextReviewAt
    }

    /**
     * Determines whether a word is considered difficult (frequent mistakes or depressed ease factor).
     */
    fun isDifficultWord(progress: UserProgress): Boolean {
        if (progress.reviewCount == 0) return false
        return progress.incorrectCount > 0 &&
                (progress.incorrectCount >= progress.correctCount || progress.easeFactor < 2.0f)
    }
}
