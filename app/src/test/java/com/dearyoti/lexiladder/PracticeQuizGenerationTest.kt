package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.domain.model.Word
import com.dearyoti.lexiladder.ui.screens.generateQuizFromWords
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PracticeQuizGenerationTest {

    private val sampleWords = listOf(
        Word(
            id = "w1",
            levelId = "level_a1",
            unitId = "unit_1",
            text = "apple",
            phonetic = "/ˈæp.əl/",
            partOfSpeech = "noun",
            definition = "a round fruit with red or green skin",
            translation = "apel",
            exampleSentence = "She ate a fresh apple for breakfast."
        ),
        Word(
            id = "w2",
            levelId = "level_a1",
            unitId = "unit_1",
            text = "book",
            phonetic = "/bʊk/",
            partOfSpeech = "noun",
            definition = "a written or printed work consisting of pages",
            translation = "buku",
            exampleSentence = "He reads a book every night."
        ),
        Word(
            id = "w3",
            levelId = "level_a1",
            unitId = "unit_1",
            text = "cat",
            phonetic = "/kæt/",
            partOfSpeech = "noun",
            definition = "a small domesticated carnivorous mammal",
            translation = "kucing",
            exampleSentence = "The cat slept on the warm sofa."
        ),
        Word(
            id = "w4",
            levelId = "level_a1",
            unitId = "unit_1",
            text = "water",
            phonetic = "/ˈwɔː.tər/",
            partOfSpeech = "noun",
            definition = "a clear liquid that has no color, taste, or smell",
            translation = "air",
            exampleSentence = "Drink enough water every day."
        ),
        Word(
            id = "w5",
            levelId = "level_a1",
            unitId = "unit_1",
            text = "house",
            phonetic = "/haʊs/",
            partOfSpeech = "noun",
            definition = "a building for human habitation",
            translation = "rumah",
            exampleSentence = "Their house is near the beautiful park."
        )
    )

    @Test
    fun `generateQuizFromWords produces valid quiz for single due review word`() {
        val singleDueWord = listOf(sampleWords[0])
        val questions = generateQuizFromWords(
            words = singleDueWord,
            fallbackPool = sampleWords,
            random = Random(42),
            explanationLanguage = "id",
            uiLanguage = "id"
        )

        assertEquals(1, questions.size)
        val q = questions.first()
        assertEquals("w1", q.wordId)
        assertEquals(4, q.options.size)
        assertTrue(q.correctOptionIndex in 0..3)

        // For style 0 (word to definition), correct option is definition
        val expectedDef = sampleWords[0].definition
        assertEquals(expectedDef, q.options[q.correctOptionIndex])

        // Verify options are distinct (no duplicates)
        assertEquals(q.options.size, q.options.distinct().size)
    }

    @Test
    fun `all generated questions have correctOptionIndex pointing to correct answer`() {
        val questions = generateQuizFromWords(
            words = sampleWords,
            fallbackPool = sampleWords,
            random = Random(12345),
            explanationLanguage = "id",
            uiLanguage = "id"
        )

        assertEquals(5, questions.size)

        for (q in questions) {
            val word = sampleWords.first { it.id == q.wordId }
            assertTrue("Correct option index must be within range", q.correctOptionIndex in 0 until q.options.size)
            val selectedAnswer = q.options[q.correctOptionIndex]

            // The answer must either be the definition, text, or translation
            val validAnswers = setOf(word.definition.trim(), word.text.trim(), (word.translation ?: "").trim())
            assertTrue(
                "Selected option '$selectedAnswer' must match word properties",
                validAnswers.contains(selectedAnswer)
            )

            // No duplicates in options
            assertEquals(
                "Options must not contain duplicate items: ${q.options}",
                q.options.size,
                q.options.distinct().size
            )
        }
    }

    @Test
    fun `generateQuizFromWords returns empty when fallbackPool is less than 4`() {
        val questions = generateQuizFromWords(
            words = sampleWords.take(2),
            fallbackPool = sampleWords.take(3),
            random = Random(42)
        )
        assertTrue(questions.isEmpty())
    }

    @Test
    fun `generateQuizFromWords returns empty when words list is empty`() {
        val questions = generateQuizFromWords(
            words = emptyList(),
            fallbackPool = sampleWords,
            random = Random(42)
        )
        assertTrue(questions.isEmpty())
    }
}
