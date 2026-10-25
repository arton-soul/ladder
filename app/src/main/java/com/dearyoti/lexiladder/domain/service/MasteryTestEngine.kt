package com.dearyoti.lexiladder.domain.service

import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.model.LevelStatus
import com.dearyoti.lexiladder.domain.model.MasteryTestQuestion
import com.dearyoti.lexiladder.domain.model.MasteryTestResult
import com.dearyoti.lexiladder.domain.model.QuestionType
import com.dearyoti.lexiladder.domain.model.Word
import kotlin.random.Random

object MasteryTestEngine {

    const val DEFAULT_PASSING_PERCENTAGE = 80
    const val DEFAULT_QUESTION_COUNT = 25
    const val REQUIRED_MASTERY_PERCENTAGE_FOR_TEST = 60
    const val REQUIRED_MASTERY_RATIO_FOR_TEST = 0.60f
    const val REQUIRED_STUDY_RATIO = 0.75f // Legacy reference

    fun calculateRequiredMasteryCount(totalWords: Int): Int {
        if (totalWords <= 0) return 0
        return kotlin.math.ceil((totalWords * REQUIRED_MASTERY_PERCENTAGE_FOR_TEST) / 100.0).toInt()
    }

    fun isTestEligible(level: Level): Boolean {
        return isTestEligible(
            isUnlocked = level.isUnlocked,
            isMastered = level.isMastered,
            totalWords = level.totalWords,
            masteredWords = level.masteredWords
        )
    }

    fun isTestEligible(
        isUnlocked: Boolean,
        isMastered: Boolean,
        totalWords: Int,
        masteredWords: Int
    ): Boolean {
        if (!isUnlocked) return false
        if (isMastered) return true
        if (totalWords <= 0) return false
        val reqCount = calculateRequiredMasteryCount(totalWords)
        return masteredWords >= reqCount
    }

    fun determineLevelStatus(
        isUnlocked: Boolean,
        isMastered: Boolean,
        totalWords: Int,
        studiedWords: Int = 0,
        masteredWords: Int = 0,
        requiredMasteryRatio: Float = REQUIRED_MASTERY_RATIO_FOR_TEST
    ): LevelStatus {
        if (!isUnlocked) {
            return LevelStatus.LOCKED
        }
        if (isMastered) {
            return LevelStatus.MASTERED
        }
        if (totalWords > 0 && masteredWords >= calculateRequiredMasteryCount(totalWords)) {
            return LevelStatus.READY_FOR_TEST
        }
        if (masteredWords > 0 || studiedWords > 0) {
            return LevelStatus.IN_PROGRESS
        }
        return LevelStatus.AVAILABLE
    }

    fun generateQuestions(
        words: List<Word>,
        questionCount: Int = DEFAULT_QUESTION_COUNT,
        randomSeed: Long? = null
    ): List<MasteryTestQuestion> {
        if (words.isEmpty()) return emptyList()

        val rng = if (randomSeed != null) Random(randomSeed) else Random.Default
        val selectedWords = words.shuffled(rng).take(questionCount)
        val questionTypes = listOf(
            QuestionType.WORD_TO_TRANSLATION,
            QuestionType.TRANSLATION_TO_WORD,
            QuestionType.FILL_IN_BLANK
        )

        return selectedWords.mapIndexed { index, targetWord ->
            val type = questionTypes[index % questionTypes.size]
            val otherWords = words.filter { it.id != targetWord.id }

            when (type) {
                QuestionType.WORD_TO_TRANSLATION -> {
                    val correctTranslation = targetWord.translation ?: targetWord.definition
                    val distractors = otherWords.shuffled(rng)
                        .mapNotNull { it.translation ?: it.definition }
                        .filter { it != correctTranslation }
                        .distinct()
                        .take(3)

                    val options = (distractors + correctTranslation).shuffled(rng)
                    MasteryTestQuestion(
                        id = "mq_${targetWord.id}_${index}",
                        wordId = targetWord.id,
                        questionText = "What is the meaning of \"${targetWord.text}\"?",
                        promptDetail = "Part of Speech: ${targetWord.partOfSpeech}",
                        options = options,
                        correctAnswer = correctTranslation,
                        type = QuestionType.WORD_TO_TRANSLATION,
                        targetWord = targetWord
                    )
                }
                QuestionType.TRANSLATION_TO_WORD -> {
                    val targetTranslation = targetWord.translation ?: targetWord.definition
                    val distractors = otherWords.shuffled(rng)
                        .map { it.text }
                        .filter { it.lowercase() != targetWord.text.lowercase() }
                        .distinct()
                        .take(3)

                    val options = (distractors + targetWord.text).shuffled(rng)
                    MasteryTestQuestion(
                        id = "mq_${targetWord.id}_${index}",
                        wordId = targetWord.id,
                        questionText = "Which word translates to \"$targetTranslation\"?",
                        promptDetail = "Phonetic: ${targetWord.phonetic ?: ""}",
                        options = options,
                        correctAnswer = targetWord.text,
                        type = QuestionType.TRANSLATION_TO_WORD,
                        targetWord = targetWord
                    )
                }
                QuestionType.FILL_IN_BLANK -> {
                    val sentence = targetWord.exampleSentence
                    val regex = Regex("\\b${Regex.escape(targetWord.text)}\\b", RegexOption.IGNORE_CASE)
                    val blankSentence = if (sentence.contains(regex)) {
                        sentence.replace(regex, "______")
                    } else {
                        "Complete the sentence with \"${targetWord.text}\": $sentence"
                    }

                    val distractors = otherWords.shuffled(rng)
                        .map { it.text }
                        .filter { it.lowercase() != targetWord.text.lowercase() }
                        .distinct()
                        .take(3)

                    val options = (distractors + targetWord.text).shuffled(rng)
                    MasteryTestQuestion(
                        id = "mq_${targetWord.id}_${index}",
                        wordId = targetWord.id,
                        questionText = "Complete the sentence with the appropriate word:",
                        promptDetail = blankSentence,
                        options = options,
                        correctAnswer = targetWord.text,
                        type = QuestionType.FILL_IN_BLANK,
                        targetWord = targetWord
                    )
                }
            }
        }
    }

    fun evaluateTest(
        questions: List<MasteryTestQuestion>,
        answers: Map<String, String>,
        passingPercentage: Int = DEFAULT_PASSING_PERCENTAGE
    ): MasteryTestResult {
        if (questions.isEmpty()) {
            return MasteryTestResult(
                scorePercentage = 0,
                totalQuestions = 0,
                correctCount = 0,
                passingPercentage = passingPercentage,
                isPassed = false
            )
        }

        var correctCount = 0
        val incorrectQuestions = mutableListOf<MasteryTestQuestion>()

        for (question in questions) {
            val userAnswer = answers[question.id]
            if (userAnswer != null && userAnswer.trim().equals(question.correctAnswer.trim(), ignoreCase = true)) {
                correctCount++
            } else {
                incorrectQuestions.add(question)
            }
        }

        val total = questions.size
        val score = ((correctCount.toFloat() / total.toFloat()) * 100).toInt()
        val isPassed = score >= passingPercentage

        return MasteryTestResult(
            scorePercentage = score,
            totalQuestions = total,
            correctCount = correctCount,
            passingPercentage = passingPercentage,
            isPassed = isPassed,
            incorrectQuestions = incorrectQuestions
        )
    }

    fun findNextLevelToUnlock(currentLevelOrder: Int, allLevels: List<Level>): Level? {
        return allLevels
            .filter { it.order > currentLevelOrder && !it.isUnlocked }
            .minByOrNull { it.order }
    }
}
