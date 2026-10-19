package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.data.content.model.LearningPackage
import com.dearyoti.lexiladder.data.content.model.LearningUnitEntry
import com.dearyoti.lexiladder.data.content.model.LearningWordEntry
import com.dearyoti.lexiladder.data.content.model.TranslationEntry
import com.dearyoti.lexiladder.data.content.model.TranslationPackage
import com.dearyoti.lexiladder.data.content.model.UiCatalogPackage
import com.dearyoti.lexiladder.domain.content.ContentValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentValidatorTest {

    @Test
    fun `validateChecksum passes for correct sha256 and fails for tampered bytes`() {
        val sampleData = "Hello LexiLadder Content System".toByteArray(Charsets.UTF_8)
        val validHash = ContentValidator.calculateSha256(sampleData)

        assertTrue("Valid hash should pass", ContentValidator.validateChecksum(sampleData, validHash))
        assertFalse("Tampered hash should fail", ContentValidator.validateChecksum(sampleData, "0123456789abcdef"))

        val tamperedData = "Hello Tampered Content System".toByteArray(Charsets.UTF_8)
        assertFalse("Tampered data should fail checksum", ContentValidator.validateChecksum(tamperedData, validHash))
    }

    @Test
    fun `validateSize passes for matching size and fails when size exceeds limit`() {
        val sampleData = ByteArray(1024) // 1 KB
        val validResult = ContentValidator.validateSize(sampleData, expectedSize = 1024L, maxSize = 2048L)
        assertTrue(validResult.isValid)

        val mismatchedResult = ContentValidator.validateSize(sampleData, expectedSize = 512L, maxSize = 2048L)
        assertFalse(mismatchedResult.isValid)

        val oversizedResult = ContentValidator.validateSize(sampleData, expectedSize = 1024L, maxSize = 512L)
        assertFalse(oversizedResult.isValid)
    }

    @Test
    fun `validateAppCompatibility rejects packages requiring higher app version`() {
        assertTrue("Equal version is compatible", ContentValidator.validateAppCompatibility(minimumAppVersion = 1, currentAppVersion = 1))
        assertTrue("Higher app version is compatible", ContentValidator.validateAppCompatibility(minimumAppVersion = 1, currentAppVersion = 2))
        assertFalse("Package requiring higher app version is rejected", ContentValidator.validateAppCompatibility(minimumAppVersion = 2, currentAppVersion = 1))
    }

    @Test
    fun `validateSchemaVersion rejects unsupported future schema versions`() {
        assertTrue("Schema version 1 is supported", ContentValidator.validateSchemaVersion(1, supportedSchemaVersion = 1))
        assertFalse("Schema version 2 is rejected if app only supports 1", ContentValidator.validateSchemaVersion(2, supportedSchemaVersion = 1))
        assertFalse("Schema version 0 is rejected", ContentValidator.validateSchemaVersion(0, supportedSchemaVersion = 1))
    }

    @Test
    fun `validateLearningPackage succeeds for valid package`() {
        val validPkg = LearningPackage(
            schemaVersion = 1,
            packageId = "learning.en.a1",
            contentVersion = 1,
            levelId = "level_a1",
            units = listOf(LearningUnitEntry(unitId = "unit_1", title = "Basics", wordIds = listOf("w1", "w2"))),
            words = listOf(
                LearningWordEntry(wordId = "w1", term = "Apple", partOfSpeech = "noun", definition = "A fruit", example = "I eat an apple"),
                LearningWordEntry(wordId = "w2", term = "Book", partOfSpeech = "noun", definition = "Printed work", example = "Read a book")
            )
        )

        val result = ContentValidator.validateLearningPackage(validPkg)
        assertTrue(result.isValid)
    }

    @Test
    fun `validateLearningPackage rejects duplicate wordId`() {
        val duplicatePkg = LearningPackage(
            schemaVersion = 1,
            packageId = "learning.en.a1",
            contentVersion = 1,
            levelId = "level_a1",
            words = listOf(
                LearningWordEntry(wordId = "w1", term = "Apple", partOfSpeech = "noun", definition = "A fruit", example = "I eat an apple"),
                LearningWordEntry(wordId = "w1", term = "Duplicate Apple", partOfSpeech = "noun", definition = "Duplicate", example = "Duplicate example")
            )
        )

        val result = ContentValidator.validateLearningPackage(duplicatePkg)
        assertFalse("Duplicate wordId must be rejected", result.isValid)
    }

    @Test
    fun `validateLearningPackage rejects missing required fields`() {
        val invalidPkg = LearningPackage(
            schemaVersion = 1,
            packageId = "learning.en.a1",
            contentVersion = 1,
            levelId = "level_a1",
            words = listOf(
                LearningWordEntry(wordId = "w1", term = "", partOfSpeech = "noun", definition = "A fruit", example = "I eat an apple")
            )
        )

        val result = ContentValidator.validateLearningPackage(invalidPkg)
        assertFalse("Blank term must be rejected", result.isValid)
    }

    @Test
    fun `validateTranslationPackage succeeds for valid entries and rejects duplicates`() {
        val validPkg = TranslationPackage(
            schemaVersion = 1,
            packageId = "translation.id.en.a1",
            contentVersion = 1,
            locale = "id",
            levelId = "level_a1",
            entries = listOf(
                TranslationEntry(wordId = "w1", translation = "Apel"),
                TranslationEntry(wordId = "w2", translation = "Buku")
            )
        )
        assertTrue(ContentValidator.validateTranslationPackage(validPkg).isValid)

        val duplicatePkg = validPkg.copy(
            entries = listOf(
                TranslationEntry(wordId = "w1", translation = "Apel"),
                TranslationEntry(wordId = "w1", translation = "Apel Duplikat")
            )
        )
        assertFalse("Duplicate translation entries must be rejected", ContentValidator.validateTranslationPackage(duplicatePkg).isValid)
    }

    @Test
    fun `validateUiCatalog succeeds for valid strings map and rejects empty catalog`() {
        val validCatalog = UiCatalogPackage(
            schemaVersion = 1,
            packageId = "ui.id",
            locale = "id",
            contentVersion = 1,
            strings = mapOf("home_title" to "Beranda", "settings_title" to "Pengaturan")
        )
        assertTrue(ContentValidator.validateUiCatalog(validCatalog).isValid)

        val emptyCatalog = validCatalog.copy(strings = emptyMap())
        assertFalse("Empty UI catalog must be rejected", ContentValidator.validateUiCatalog(emptyCatalog).isValid)
    }
}
