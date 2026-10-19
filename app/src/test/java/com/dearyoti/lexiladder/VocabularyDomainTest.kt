package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.data.local.entity.WordEntity
import com.dearyoti.lexiladder.domain.model.UserProgress
import com.dearyoti.lexiladder.domain.model.Word
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VocabularyDomainTest {

    @Test
    fun userProgress_accuracyCalculatedCorrectly() {
        val progress = UserProgress(
            wordId = "w1",
            reviewCount = 10,
            correctCount = 8,
            incorrectCount = 2
        )
        assertEquals(80.0f, progress.accuracy, 0.01f)
    }

    @Test
    fun userProgress_zeroReviews_accuracyIsZero() {
        val progress = UserProgress(wordId = "w1", reviewCount = 0)
        assertEquals(0.0f, progress.accuracy, 0.01f)
    }

    @Test
    fun wordEntity_domainModelConversion_isPreserved() {
        val word = Word(
            id = "w_001",
            text = "Achieve",
            partOfSpeech = "verb",
            definition = "To complete successfully",
            exampleSentence = "She achieved her goal.",
            levelId = "level_a1",
            unitId = "unit_1",
            phonetic = "/əˈtʃiːv/",
            translation = "Mencapai"
        )

        val entity = WordEntity.fromDomainModel(word)
        assertEquals("w_001", entity.id)
        assertEquals("Achieve", entity.text)
        assertEquals("Mencapai", entity.translation)

        val convertedBack = entity.toDomainModel()
        assertEquals(word, convertedBack)
    }

    @Test
    fun userProgress_masteryThreshold_logic() {
        val initialProgress = UserProgress(
            wordId = "w1",
            reviewCount = 4,
            correctCount = 3,
            incorrectCount = 1,
            isMastered = false
        )

        val isMastered = initialProgress.correctCount >= 3 && (initialProgress.correctCount.toFloat() / initialProgress.reviewCount.toFloat()) >= 0.75f
        assertTrue(isMastered)
    }
}
