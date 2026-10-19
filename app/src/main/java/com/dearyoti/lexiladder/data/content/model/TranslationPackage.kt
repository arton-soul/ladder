package com.dearyoti.lexiladder.data.content.model

data class TranslationPackage(
    val schemaVersion: Int = 1,
    val packageId: String,
    val contentVersion: Int,
    val locale: String,
    val targetLanguage: String = "en",
    val levelId: String,
    val entries: List<TranslationEntry> = emptyList()
)

data class TranslationEntry(
    val wordId: String,
    val translation: String,
    val definition: String? = null,
    val exampleTranslation: String? = null
)
