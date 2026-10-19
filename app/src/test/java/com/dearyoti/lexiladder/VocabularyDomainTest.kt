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
            assertTrue(
                "Correct option '$correctOption' must match target word's definition, text, or translation",
                sampleWords.any { it.definition == correctOption || it.text == correctOption || it.translation == correctOption }
            )
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

    @Test
    fun quizGeneration_generatesMultipleQuestionFormatsAndRandomizes() {
        val sampleWords = listOf(
            Word("1", "Achieve", "verb", "To complete successfully", "She worked hard to achieve goals.", "level_a1", "unit_1", "/əˈtʃiːv/", "Mencapai"),
            Word("2", "Brief", "adjective", "Short in duration", "A brief meeting before work.", "level_a1", "unit_1", "/briːf/", "Singkat"),
            Word("3", "Courage", "noun", "Bravery under fear", "It took courage to speak up.", "level_a1", "unit_1", "/ˈkʌr.ɪdʒ/", "Keberanian"),
            Word("4", "Diligent", "adjective", "Hardworking and attentive", "The diligent student studied.", "level_a1", "unit_1", "/ˈdɪl.ə.dʒənt/", "Rajin"),
            Word("5", "Explore", "verb", "Travel to discover", "They explore the ancient city.", "level_a1", "unit_1", "/ɪkˈsplɔːr/", "Menjelajah")
        )

        // Generate quiz
        val quiz = generateQuizFromWords(sampleWords)
        assertFalse(quiz.isEmpty())

        // Verify that question texts are not all identical
        val questionTexts = quiz.map { it.questionText }.distinct()
        assertTrue("Quiz should generate varied question texts, got ${questionTexts.size}", questionTexts.size > 1)

        // Verify every question has 4 distinct options
        for (q in quiz) {
            assertEquals(4, q.options.size)
            assertEquals(4, q.options.distinct().size)
            val correctOpt = q.options[q.correctOptionIndex]
            assertTrue(sampleWords.any { it.text == correctOpt || it.definition == correctOpt || it.translation == correctOpt })
        }
    }

    @Test
    fun quizGeneration_handlesMultiLevelWordsCorrectly() {
        val multiLevelWords = listOf(
            Word("1", "Achieve", "verb", "To complete successfully", "She achieved her goal.", "level_a1", "unit_1", null, "Mencapai"),
            Word("2", "Adapt", "verb", "To adjust to new conditions", "Living abroad helps you adapt.", "level_a2", "unit_1", null, "Beradaptasi"),
            Word("3", "Leadership", "noun", "Action of leading a group", "Effective leadership inspires.", "level_b1", "unit_1", null, "Kepemimpinan"),
            Word("4", "Community", "noun", "A group of people", "The local community gathered.", "level_a2", "unit_3", null, "Komunitas")
        )

        val quiz = generateQuizFromWords(multiLevelWords)
        assertEquals(4, quiz.size)
        val wordIdsInQuiz = quiz.map { it.wordId }.toSet()
        assertEquals(4, wordIdsInQuiz.size)
    }
}
