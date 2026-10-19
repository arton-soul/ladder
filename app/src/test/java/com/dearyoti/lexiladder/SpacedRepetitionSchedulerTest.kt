package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.domain.model.LearningStage
import com.dearyoti.lexiladder.domain.model.ReviewRating
import com.dearyoti.lexiladder.domain.model.UserProgress
import com.dearyoti.lexiladder.domain.scheduler.SpacedRepetitionScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpacedRepetitionSchedulerTest {

    private val baseTime = 1700000000000L // arbitrary fixed timestamp

    @Test
    fun wrongRating_resetsStreak_setsShortInterval() {
        val initial = UserProgress(
            wordId = "w1",
            reviewCount = 3,
            correctCount = 3,
            consecutiveCorrect = 3,
            intervalDays = 7,
            easeFactor = 2.5f,
            isMastered = true
        )

        val updated = SpacedRepetitionScheduler.scheduleNextReview(initial, ReviewRating.WRONG, baseTime)

        assertEquals(4, updated.reviewCount)
        assertEquals(3, updated.correctCount)
        assertEquals(1, updated.incorrectCount)
        assertEquals(0, updated.consecutiveCorrect)
        assertEquals(0, updated.intervalDays)
        assertEquals(2.3f, updated.easeFactor, 0.01f)
        assertFalse(updated.isMastered)
        assertEquals(LearningStage.LEARNING, updated.learningStage)
        // Re-review in 4 hours
        assertEquals(baseTime + 4 * 60 * 60 * 1000L, updated.nextReviewAt)
    }

    @Test
    fun singleCorrectAnswer_doesNotGrantFullMastery() {
        val newWordProgress = UserProgress(wordId = "w1")

        // First review: Good
        val afterFirstGood = SpacedRepetitionScheduler.scheduleNextReview(newWordProgress, ReviewRating.GOOD, baseTime)
        assertFalse("Single correct answer must not immediately mark mastery", afterFirstGood.isMastered)
        assertEquals(1, afterFirstGood.intervalDays)
        assertEquals(LearningStage.REVIEWING, afterFirstGood.learningStage)

        // First review: Easy
        val afterFirstEasy = SpacedRepetitionScheduler.scheduleNextReview(newWordProgress, ReviewRating.EASY, baseTime)
        assertFalse("Single easy answer must not immediately mark mastery", afterFirstEasy.isMastered)
        assertEquals(2, afterFirstEasy.intervalDays)
        assertEquals(LearningStage.REVIEWING, afterFirstEasy.learningStage)
    }

    @Test
    fun repeatedGoodAnswers_progressivelyIncreaseInterval() {
        val start = UserProgress(wordId = "w1")

        // Step 1: Good
        val r1 = SpacedRepetitionScheduler.scheduleNextReview(start, ReviewRating.GOOD, baseTime)
        assertEquals(1, r1.intervalDays)
        assertEquals(1, r1.consecutiveCorrect)
        assertFalse(r1.isMastered)

        // Step 2: Good
        val time2 = baseTime + 1 * 24 * 60 * 60 * 1000L
        val r2 = SpacedRepetitionScheduler.scheduleNextReview(r1, ReviewRating.GOOD, time2)
        assertEquals(3, r2.intervalDays)
        assertEquals(2, r2.consecutiveCorrect)
        assertFalse(r2.isMastered)

        // Step 3: Good
        val time3 = time2 + 3 * 24 * 60 * 60 * 1000L
        val r3 = SpacedRepetitionScheduler.scheduleNextReview(r2, ReviewRating.GOOD, time3)
        assertTrue("Interval should be at least 7 days after 3 consecutive good answers", r3.intervalDays >= 7)
        assertEquals(3, r3.consecutiveCorrect)
        assertTrue("Mastery granted after 3 consecutive good reviews with interval >= 7 days", r3.isMastered)
        assertEquals(LearningStage.MASTERED, r3.learningStage)

        // Step 4: Good
        val time4 = time3 + r3.intervalDays * 24 * 60 * 60 * 1000L
        val r4 = SpacedRepetitionScheduler.scheduleNextReview(r3, ReviewRating.GOOD, time4)
        assertTrue("Interval should grow even further on 4th consecutive review", r4.intervalDays > r3.intervalDays)
        assertTrue(r4.isMastered)
    }

    @Test
    fun hardRating_keepsIntervalShort_andDecreasesEase() {
        val initial = UserProgress(
            wordId = "w1",
            reviewCount = 1,
            correctCount = 1,
            consecutiveCorrect = 1,
            easeFactor = 2.5f
        )

        val updated = SpacedRepetitionScheduler.scheduleNextReview(initial, ReviewRating.HARD, baseTime)
        assertEquals(1, updated.intervalDays)
        assertEquals(2.35f, updated.easeFactor, 0.01f)
        assertFalse(updated.isMastered)
        assertEquals(LearningStage.LEARNING, updated.learningStage)
    }

    @Test
    fun easeFactor_doesNotDropBelowMinimum() {
        var progress = UserProgress(wordId = "w1", easeFactor = 1.4f)
        // Two consecutive failures
        progress = SpacedRepetitionScheduler.scheduleNextReview(progress, ReviewRating.WRONG, baseTime)
        progress = SpacedRepetitionScheduler.scheduleNextReview(progress, ReviewRating.WRONG, baseTime + 1000)

        assertEquals(SpacedRepetitionScheduler.MIN_EASE_FACTOR, progress.easeFactor, 0.001f)
    }

    @Test
    fun isDueForReview_evaluatesTimestampsCorrectly() {
        val progress = UserProgress(
            wordId = "w1",
            reviewCount = 1,
            nextReviewAt = baseTime + 24 * 60 * 60 * 1000L
        )

        // Before due time
        assertFalse(SpacedRepetitionScheduler.isDueForReview(progress, baseTime + 12 * 60 * 60 * 1000L))
        // Exactly at due time
        assertTrue(SpacedRepetitionScheduler.isDueForReview(progress, progress.nextReviewAt))
        // After due time
        assertTrue(SpacedRepetitionScheduler.isDueForReview(progress, progress.nextReviewAt + 1000L))
    }

    @Test
    fun clockSkew_negativeTimeDelta_handledSafely() {
        val progress = UserProgress(
            wordId = "w1",
            lastReviewedAt = baseTime
        )

        // System clock moves backwards by 2 hours
        val pastClock = baseTime - 2 * 60 * 60 * 1000L
        val updated = SpacedRepetitionScheduler.scheduleNextReview(progress, ReviewRating.GOOD, pastClock)

        assertTrue(
            "Effective review time must not be before lastReviewedAt when clock moves backwards",
            updated.lastReviewedAt >= progress.lastReviewedAt
        )
        assertTrue(
            "Next review timestamp must remain in the future relative to baseline",
            updated.nextReviewAt > progress.lastReviewedAt
        )
    }

    @Test
    fun difficultWord_classification_works() {
        val newWord = UserProgress(wordId = "w1", reviewCount = 0)
        assertFalse(SpacedRepetitionScheduler.isDifficultWord(newWord))

        val cleanWord = UserProgress(wordId = "w2", reviewCount = 4, correctCount = 4, incorrectCount = 0)
        assertFalse(SpacedRepetitionScheduler.isDifficultWord(cleanWord))

        val wordWithMistakes = UserProgress(wordId = "w3", reviewCount = 4, correctCount = 2, incorrectCount = 2)
        assertTrue(SpacedRepetitionScheduler.isDifficultWord(wordWithMistakes))

        val wordWithDepressedEase = UserProgress(wordId = "w4", reviewCount = 5, correctCount = 3, incorrectCount = 2, easeFactor = 1.8f)
        assertTrue(SpacedRepetitionScheduler.isDifficultWord(wordWithDepressedEase))
    }
}
