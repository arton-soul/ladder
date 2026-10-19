package com.dearyoti.lexiladder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.model.LevelStatus

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val isUnlocked: Boolean = false,
    val isMastered: Boolean = false
) {
    fun toDomainModel(
        totalWords: Int = 0,
        studiedWords: Int = 0,
        masteredWords: Int = 0,
        status: LevelStatus = LevelStatus.LOCKED,
        requiredPassingScore: Int = 80
    ): Level = Level(
        id = id,
        title = title,
        description = description,
        order = order,
        isUnlocked = isUnlocked,
        isMastered = isMastered,
        status = status,
        totalWords = totalWords,
        studiedWords = studiedWords,
        masteredWords = masteredWords,
        requiredPassingScore = requiredPassingScore
    )

    companion object {
        fun fromDomainModel(level: Level): LevelEntity = LevelEntity(
            id = level.id,
            title = level.title,
            description = level.description,
            order = level.order,
            isUnlocked = level.isUnlocked,
            isMastered = level.isMastered
        )
    }
}
