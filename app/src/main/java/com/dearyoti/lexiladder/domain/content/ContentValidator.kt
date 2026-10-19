package com.dearyoti.lexiladder.domain.content

import com.dearyoti.lexiladder.data.content.model.LearningPackage
import com.dearyoti.lexiladder.data.content.model.TranslationPackage
import com.dearyoti.lexiladder.data.content.model.UiCatalogPackage
import java.security.MessageDigest

sealed interface ContentValidationResult {
    object Valid : ContentValidationResult
    data class Invalid(val reason: String) : ContentValidationResult

    val isValid: Boolean get() = this is Valid
}

object ContentValidator {

    const val SUPPORTED_SCHEMA_VERSION = 1
    const val MAX_PACKAGE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB limit for safety

    fun calculateSha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun validateChecksum(bytes: ByteArray, expectedSha256: String): Boolean {
        if (expectedSha256.isBlank()) return false
        val actualSha256 = calculateSha256(bytes)
        return actualSha256.equals(expectedSha256.trim(), ignoreCase = true)
    }

    fun validateSize(
        bytes: ByteArray,
        expectedSize: Long = 0L,
        maxSize: Long = MAX_PACKAGE_SIZE_BYTES
    ): ContentValidationResult {
        val actualSize = bytes.size.toLong()
        if (actualSize > maxSize) {
            return ContentValidationResult.Invalid("Package size ($actualSize bytes) exceeds maximum limit ($maxSize bytes)")
        }
        if (expectedSize > 0 && actualSize != expectedSize) {
            return ContentValidationResult.Invalid("Package size ($actualSize bytes) does not match expected size ($expectedSize bytes)")
        }
        return ContentValidationResult.Valid
    }

    fun validateAppCompatibility(minimumAppVersion: Int, currentAppVersion: Int): Boolean {
        return currentAppVersion >= minimumAppVersion
    }

    fun validateSchemaVersion(
        schemaVersion: Int,
        supportedSchemaVersion: Int = SUPPORTED_SCHEMA_VERSION
    ): Boolean {
        return schemaVersion in 1..supportedSchemaVersion
    }

    fun validateLearningPackage(pkg: LearningPackage): ContentValidationResult {
        if (pkg.packageId.isBlank()) {
            return ContentValidationResult.Invalid("Package ID cannot be blank")
        }
        if (pkg.levelId.isBlank()) {
            return ContentValidationResult.Invalid("Level ID cannot be blank")
        }
        if (!validateSchemaVersion(pkg.schemaVersion)) {
            return ContentValidationResult.Invalid("Unsupported schema version: ${pkg.schemaVersion}")
        }
        if (pkg.words.isEmpty()) {
            return ContentValidationResult.Invalid("Package contains no words")
        }

        val seenWordIds = mutableSetOf<String>()
        for (word in pkg.words) {
            if (word.wordId.isBlank()) {
                return ContentValidationResult.Invalid("Word ID cannot be blank")
            }
            if (!seenWordIds.add(word.wordId)) {
                return ContentValidationResult.Invalid("Duplicate wordId found: '${word.wordId}'")
            }
            if (word.term.isBlank()) {
                return ContentValidationResult.Invalid("Word term cannot be blank for ID: ${word.wordId}")
            }
            if (word.definition.isBlank()) {
                return ContentValidationResult.Invalid("Word definition cannot be blank for ID: ${word.wordId}")
            }
            if (word.example.isBlank()) {
                return ContentValidationResult.Invalid("Word example cannot be blank for ID: ${word.wordId}")
            }
        }

        return ContentValidationResult.Valid
    }

    fun validateTranslationPackage(pkg: TranslationPackage): ContentValidationResult {
        if (pkg.packageId.isBlank()) {
            return ContentValidationResult.Invalid("Package ID cannot be blank")
        }
        if (pkg.locale.isBlank()) {
            return ContentValidationResult.Invalid("Locale cannot be blank")
        }
        if (!validateSchemaVersion(pkg.schemaVersion)) {
            return ContentValidationResult.Invalid("Unsupported schema version: ${pkg.schemaVersion}")
        }
        if (pkg.entries.isEmpty()) {
            return ContentValidationResult.Invalid("Translation package contains no entries")
        }

        val seenWordIds = mutableSetOf<String>()
        for (entry in pkg.entries) {
            if (entry.wordId.isBlank()) {
                return ContentValidationResult.Invalid("Translation entry wordId cannot be blank")
            }
            if (!seenWordIds.add(entry.wordId)) {
                return ContentValidationResult.Invalid("Duplicate wordId in translation entries: '${entry.wordId}'")
            }
            if (entry.translation.isBlank()) {
                return ContentValidationResult.Invalid("Translation text cannot be blank for ID: ${entry.wordId}")
            }
        }

        return ContentValidationResult.Valid
    }

    fun validateUiCatalog(pkg: UiCatalogPackage): ContentValidationResult {
        if (pkg.packageId.isBlank()) {
            return ContentValidationResult.Invalid("UI Catalog package ID cannot be blank")
        }
        if (pkg.locale.isBlank()) {
            return ContentValidationResult.Invalid("Locale cannot be blank")
        }
        if (!validateSchemaVersion(pkg.schemaVersion)) {
            return ContentValidationResult.Invalid("Unsupported schema version: ${pkg.schemaVersion}")
        }
        if (pkg.strings.isEmpty()) {
            return ContentValidationResult.Invalid("UI Catalog contains no string mappings")
        }

        return ContentValidationResult.Valid
    }
}
