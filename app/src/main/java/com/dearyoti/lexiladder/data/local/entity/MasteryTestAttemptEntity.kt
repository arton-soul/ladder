package com.dearyoti.lexiladder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dearyoti.lexiladder.domain.model.MasteryTestAttempt

@Entity(tableName = "mastery_test_attempts")
data class MasteryTestAttemptEntity(
    @PrimaryKey
    val id: String,
    val levelId: String,
    val scorePercentage: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val passingPercentage: Int,
    val isPassed: Boolean,
    val timestamp: Long,
    val testVersion: String
) {
    fun toDomainModel(): MasteryTestAttempt = MasteryTestAttempt(
        id = id,
        levelId = levelId,
        scorePercentage = scorePercentage,
        totalQuestions = totalQuestions,
        correctCount = correctCount,
        passingPercentage = passingPercentage,
        isPassed = isPassed,
        timestamp = timestamp,
        testVersion = testVersion
    )

    companion object {
        fun fromDomainModel(attempt: MasteryTestAttempt): MasteryTestAttemptEntity =
            MasteryTestAttemptEntity(
                id = attempt.id,
                levelId = attempt.levelId,
                scorePercentage = attempt.scorePercentage,
                totalQuestions = attempt.totalQuestions,
                correctCount = attempt.correctCount,
                passingPercentage = attempt.passingPercentage,
                isPassed = attempt.isPassed,
                timestamp = attempt.timestamp,
                testVersion = attempt.testVersion
            )
    }
}
