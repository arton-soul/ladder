package com.dearyoti.lexiladder.domain.model

data class Word(
    val id: String,
    val text: String,
    val partOfSpeech: String,
    val definition: String,
    val exampleSentence: String,
    val levelId: String,
    val unitId: String,
    val phonetic: String? = null,
    val translation: String? = null
)
