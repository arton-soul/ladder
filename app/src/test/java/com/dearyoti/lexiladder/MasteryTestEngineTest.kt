package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.model.LevelStatus
import com.dearyoti.lexiladder.domain.model.Word
import com.dearyoti.lexiladder.domain.service.MasteryTestEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MasteryTestEngineTest {

    private val sampleWords = listOf(
        Word(id = "w1", text = "Achieve", partOfSpeech = "verb", definition = "To accomplish", exampleSentence = "She worked hard to achieve goals.", levelId = "level_a1", unitId = "unit_1", phonetic = "/əˈtʃiːv/", translation = "Mencapai"),
        Word(id = "w2", text = "Brief", partOfSpeech = "adjective", definition = "Short duration", exampleSentence = "A brief meeting.", levelId = "level_a1", unitId = "unit_1", phonetic = "/briːf/", translation = "Singkat"),
        Word(id = "w3", text = "Courage", partOfSpeech = "noun", definition = "Bravery", exampleSentence = "She showed great courage.", levelId = "level_a1", unitId = "unit_1", phonetic = "/ˈkʌr.ɪdʒ/", translation = "Keberanian"),
        Word(id = "w4", text = "Diligent", partOfSpeech = "adjective", definition = "Hardworking", exampleSentence = "A diligent worker.", levelId = "level_a1", unitId = "unit_1", phonetic = "/ˈdɪl.ə.dʒənt/", translation = "Tekun"),
        Word(id = "w5", text = "Explore", partOfSpeech = "verb", definition = "To travel to new places", exampleSentence = "Explore the world.", levelId = "level_a1", unitId = "unit_1", phonetic = "/ɪkˈsplɔːr/", translation = "Menjelajah")
    )

    @Test
    fun `determineLevelStatus returns LOCKED when level is not unlocked`() {
        val status = MasteryTestEngine.determineLevelStatus(
            isUnlocked = false,
            isMastered = false,
            totalWords = 10,
            studiedWords = 10
        )
        assertEquals(LevelStatus.LOCKED, status)
    }

    @Test
    fun `determineLevelStatus returns MASTERED when level is mastered`() {
        val status = MasteryTestEngine.determineLevelStatus(
            isUnlocked = true,
            isMastered = true,
            totalWords = 10,
            studiedWords = 10
        )
        assertEquals(LevelStatus.MASTERED, status)
    }

    @Test
    fun `determineLevelStatus returns READY_FOR_TEST when study ratio reaches 75 percent`() {
        val status = MasteryTestEngine.determineLevelStatus(
            isUnlocked = true,
            isMastered = false,
            totalWords = 20,
            studiedWords = 15 // 15/20 = 75%
        )
        assertEquals(LevelStatus.READY_FOR_TEST, status)
    }

    @Test
    fun `determineLevelStatus returns IN_PROGRESS when some words studied but less than 75 percent`() {
        val status = MasteryTestEngine.determineLevelStatus(
            isUnlocked = true,
            isMastered = false,
            totalWords = 20,
            studiedWords = 5 // 5/20 = 25%
        )
        assertEquals(LevelStatus.IN_PROGRESS, status)
    }

    @Test
    fun `determineLevelStatus returns AVAILABLE when unlocked but zero words studied`() {
        val status = MasteryTestEngine.determineLevelStatus(
            isUnlocked = true,
            isMastered = false,
            totalWords = 20,
            studiedWords = 0
        )
        assertEquals(LevelStatus.AVAILABLE, status)
    }

    @Test
    fun `generateQuestions creates questions where each has 4 options including correct answer`() {
        val questions = MasteryTestEngine.generateQuestions(
            words = sampleWords,
            questionCount = 5,
            randomSeed = 42L
        )

        assertEquals(5, questions.size)
        questions.forEach { q ->
            assertTrue("Options should contain correct answer", q.options.contains(q.correctAnswer))
            assertEquals("Options count should be at most 4", 4, q.options.size)
            assertEquals("Options should be distinct", q.options.distinct().size, q.options.size)
            assertNotNull(q.targetWord)
        }
    }

    @Test
    fun `evaluateTest calculates score and passes when score reaches passing threshold`() {
        val questions = MasteryTestEngine.generateQuestions(
            words = sampleWords,
            questionCount = 5,
            randomSeed = 100L
        )

        // Answer 4 out of 5 correctly (80%)
        val answers = mutableMapOf<String, String>()
        questions.take(4).forEach { answers[it.id] = it.correctAnswer }
        answers[questions[4].id] = "WRONG_ANSWER"

        val result = MasteryTestEngine.evaluateTest(questions, answers, passingPercentage = 80)

        assertEquals(80, result.scorePercentage)
        assertEquals(4, result.correctCount)
        assertEquals(5, result.totalQuestions)
        assertTrue("Should pass at 80%", result.isPassed)
        assertEquals(1, result.incorrectQuestions.size)
        assertEquals(questions[4].id, result.incorrectQuestions[0].id)
    }

    @Test
    fun `evaluateTest fails when score is below passing threshold`() {
        val questions = MasteryTestEngine.generateQuestions(
            words = sampleWords,
            questionCount = 5,
            randomSeed = 200L
        )

        // Answer 3 out of 5 correctly (60% < 80%)
        val answers = mutableMapOf<String, String>()
        questions.take(3).forEach { answers[it.id] = it.correctAnswer }
        answers[questions[3].id] = "WRONG_1"
        answers[questions[4].id] = "WRONG_2"

        val result = MasteryTestEngine.evaluateTest(questions, answers, passingPercentage = 80)

        assertEquals(60, result.scorePercentage)
        assertEquals(3, result.correctCount)
        assertFalse("Should fail below 80%", result.isPassed)
        assertEquals(2, result.incorrectQuestions.size)
    }

    @Test
    fun `findNextLevelToUnlock returns next level in ascending order`() {
        val levels = listOf(
            Level(id = "level_a1", title = "Level 1", description = "", order = 1, isUnlocked = true, isMastered = true),
            Level(id = "level_a2", title = "Level 2", description = "", order = 2, isUnlocked = false),
            Level(id = "level_b1", title = "Level 3", description = "", order = 3, isUnlocked = false)
        )

        val nextLevel = MasteryTestEngine.findNextLevelToUnlock(currentLevelOrder = 1, allLevels = levels)
        assertNotNull(nextLevel)
        assertEquals("level_a2", nextLevel?.id)
        assertEquals(2, nextLevel?.order)
    }

    @Test
    fun `findNextLevelToUnlock returns null if all subsequent levels are already unlocked`() {
        val levels = listOf(
            Level(id = "level_a1", title = "Level 1", description = "", order = 1, isUnlocked = true, isMastered = true),
            Level(id = "level_a2", title = "Level 2", description = "", order = 2, isUnlocked = true),
            Level(id = "level_b1", title = "Level 3", description = "", order = 3, isUnlocked = true)
        )

        val nextLevel = MasteryTestEngine.findNextLevelToUnlock(currentLevelOrder = 1, allLevels = levels)
        assertNull(nextLevel)
    }
}
