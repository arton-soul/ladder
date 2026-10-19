package com.dearyoti.lexiladder.domain.model

data class QuizQuestion(
    val id: String,
    val wordId: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)
