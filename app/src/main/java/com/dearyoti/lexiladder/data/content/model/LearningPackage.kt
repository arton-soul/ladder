package com.dearyoti.lexiladder.data.content.model

data class LearningPackage(
    val schemaVersion: Int = 1,
    val packageId: String,
    val contentVersion: Int,
    val targetLanguage: String = "en",
    val levelId: String,
    val levelTitle: String? = null,
    val levelDescription: String? = null,
    val levelOrder: Int? = null,
    val units: List<LearningUnitEntry> = emptyList(),
    val words: List<LearningWordEntry> = emptyList()
)

data class LearningUnitEntry(
    val unitId: String,
    val title: String,
    val wordIds: List<String> = emptyList()
)

data class LearningWordEntry(
    val wordId: String,
    val term: String,
    val partOfSpeech: String,
    val definition: String,
    val example: String,
    val unitId: String = "unit_1",
    val phonetic: String? = null,
    val translation: String? = null,
    val tags: List<String> = emptyList()
)
