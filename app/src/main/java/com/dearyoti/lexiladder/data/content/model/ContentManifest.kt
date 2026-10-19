package com.dearyoti.lexiladder.data.content.model

data class ContentManifest(
    val manifestSchemaVersion: Int = 1,
    val manifestVersion: Int = 1,
    val appContent: ManifestAppContent? = null,
    val packages: List<ContentPackageMetadata> = emptyList()
)

data class ManifestAppContent(
    val fallbackUiLocale: String = "en",
    val targetLanguage: String = "en"
)

data class ContentPackageMetadata(
    val packageId: String,
    val type: String, // "learning-package", "translation-package", "ui-catalog"
    val locale: String,
    val contentVersion: Int,
    val schemaVersion: Int = 1,
    val minimumAppVersion: Int = 1,
    val status: String = "published", // "published", "deprecated", "testing"
    val url: String,
    val sha256: String,
    val sizeBytes: Long,
    val description: String? = null
)
