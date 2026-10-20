package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.data.repository.DefaultVocabularyCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultVocabularyCatalogTest {

    @Test
    fun defaultCatalog_wordCountsMatchTarget() {
        assertEquals("Level 1 (A1) must have exactly 150 words", 150, DefaultVocabularyCatalog.levelA1Words.size)
        assertEquals("Level 2 (A2) must have exactly 200 words", 200, DefaultVocabularyCatalog.levelA2Words.size)
        assertEquals("Level 3 (B1) must have exactly 20 words", 20, DefaultVocabularyCatalog.levelB1Words.size)
        assertEquals("Total words in catalog must be 370", 370, DefaultVocabularyCatalog.allWords.size)
    }

    @Test
    fun defaultCatalog_allWordIdsAreUnique() {
        val allIds = DefaultVocabularyCatalog.allWords.map { it.id }
        val uniqueIds = allIds.toSet()
        assertEquals("All 370 word IDs must be globally unique", allIds.size, uniqueIds.size)
    }

    @Test
    fun defaultCatalog_noDuplicateWordsPerLevel() {
        val a1Texts = DefaultVocabularyCatalog.levelA1Words.map { it.text.lowercase().trim() }
        assertEquals("Level A1 words must be unique", a1Texts.size, a1Texts.toSet().size)

        val a2Texts = DefaultVocabularyCatalog.levelA2Words.map { it.text.lowercase().trim() }
        assertEquals("Level A2 words must be unique", a2Texts.size, a2Texts.toSet().size)

        val b1Texts = DefaultVocabularyCatalog.levelB1Words.map { it.text.lowercase().trim() }
        assertEquals("Level B1 words must be unique", b1Texts.size, b1Texts.toSet().size)
    }

    @Test
    fun defaultCatalog_allFieldsAreValidAndNonEmpty() {
        for (word in DefaultVocabularyCatalog.allWords) {
            assertTrue("ID should not be blank: ${word.id}", word.id.isNotBlank())
            assertTrue("Text should not be blank: ${word.id}", word.text.isNotBlank())
            assertTrue("Part of speech should not be blank: ${word.id}", word.partOfSpeech.isNotBlank())
            assertTrue("Definition should not be blank: ${word.id}", word.definition.isNotBlank())
            assertTrue("Example sentence should not be blank: ${word.id}", word.exampleSentence.isNotBlank())
            assertTrue("Level ID should not be blank: ${word.id}", word.levelId.isNotBlank())
            assertTrue("Unit ID should not be blank: ${word.id}", !word.unitId.isNullOrBlank())
            assertTrue("Phonetic should not be blank: ${word.id}", !word.phonetic.isNullOrBlank())
            assertTrue("Translation should not be blank: ${word.id}", !word.translation.isNullOrBlank())
        }
    }

    @Test
    fun defaultCatalog_unitsDistributionMatchesExpected() {
        val a1Units = DefaultVocabularyCatalog.levelA1Words.groupBy { it.unitId }
        assertEquals("Level A1 must have 5 units", 5, a1Units.size)
        a1Units.values.forEach { unitWords ->
            assertEquals("Each Level A1 unit must contain exactly 30 words", 30, unitWords.size)
        }

        val a2Units = DefaultVocabularyCatalog.levelA2Words.groupBy { it.unitId }
        assertEquals("Level A2 must have 7 units", 7, a2Units.size)
    }
}
