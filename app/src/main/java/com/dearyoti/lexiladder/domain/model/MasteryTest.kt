package com.dearyoti.lexiladder.domain.model

enum class LevelStatus {
    LOCKED,
    AVAILABLE,
    IN_PROGRESS,
    READY_FOR_TEST,
    MASTERED
}

enum class QuestionType {
    WORD_TO_TRANSLATION,
    TRANSLATION_TO_WORD,
    FILL_IN_BLANK
}

data class MasteryTestQuestion(
    val id: String,
    val wordId: String,
    val questionText: String,
    val promptDetail: String? = null,
    val options: List<String>,
    val correctAnswer: String,
    val type: QuestionType,
    val targetWord: Word
)

data class MasteryTestResult(
    val scorePercentage: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val passingPercentage: Int,
    val isPassed: Boolean,
    val incorrectQuestions: List<MasteryTestQuestion> = emptyList()
)

data class MasteryTestAttempt(
    val id: String,
    val levelId: String,
    val scorePercentage: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val passingPercentage: Int = 80,
    val isPassed: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val testVersion: String = "1.0.0"
)
