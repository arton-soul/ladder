package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.data.local.entity.WordEntity
import com.dearyoti.lexiladder.domain.model.UserProgress
import com.dearyoti.lexiladder.domain.model.Word
import com.dearyoti.lexiladder.ui.screens.generateQuizFromWords
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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

        val isMastered = initialProgress.correctCount >= 3 &&
                (initialProgress.correctCount.toFloat() / initialProgress.reviewCount.toFloat()) >= 0.75f
        assertTrue(isMastered)
    }

    @Test
    fun quizGeneration_createsQuestionsWithValidOptions() {
        val sampleWords = listOf(
            Word("1", "Achieve", "verb", "To complete successfully", "Ex 1", "level_a1", "unit_1", null, "Mencapai"),
            Word("2", "Brief", "adjective", "Short in duration", "Ex 2", "level_a1", "unit_1", null, "Singkat"),
            Word("3", "Courage", "noun", "Bravery under fear", "Ex 3", "level_a1", "unit_1", null, "Keberanian"),
            Word("4", "Diligent", "adjective", "Hardworking and attentive", "Ex 4", "level_a1", "unit_1", null, "Rajin"),
            Word("5", "Explore", "verb", "Travel to discover", "Ex 5", "level_a1", "unit_1", null, "Menjelajah")
        )

        val questions = generateQuizFromWords(sampleWords)
        assertFalse(questions.isEmpty())

        for (q in questions) {
            assertEquals(4, q.options.size)
            assertTrue(q.correctOptionIndex in 0..3)
            val correctOption = q.options[q.correctOptionIndex]
            assertNotNull(correctOption)
            assertTrue(sampleWords.any { it.definition == correctOption })
        }
    }

    @Test
    fun wordSearch_filtersCorrectlyByTextOrTranslation() {
        val words = listOf(
            Word("1", "Courage", "noun", "Bravery", "Ex", "level_a1", "unit_1", null, "Keberanian"),
            Word("2", "Patience", "noun", "Endurance", "Ex", "level_a1", "unit_2", null, "Kesabaran")
        )

        val searchByEnglish = words.filter {
            it.text.contains("courage", ignoreCase = true) || (it.translation?.contains("courage", ignoreCase = true) == true)
        }
        assertEquals(1, searchByEnglish.size)
        assertEquals("Courage", searchByEnglish[0].text)

        val searchByIndonesian = words.filter {
            it.text.contains("kesabaran", ignoreCase = true) || (it.translation?.contains("kesabaran", ignoreCase = true) == true)
        }
        assertEquals(1, searchByIndonesian.size)
        assertEquals("Patience", searchByIndonesian[0].text)
    }
}
