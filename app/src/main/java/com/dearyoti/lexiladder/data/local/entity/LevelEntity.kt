package com.dearyoti.lexiladder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dearyoti.lexiladder.domain.model.Level

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val isUnlocked: Boolean = false
) {
    fun toDomainModel(totalWords: Int = 0, masteredWords: Int = 0): Level = Level(
        id = id,
        title = title,
        description = description,
        order = order,
        isUnlocked = isUnlocked,
        totalWords = totalWords,
        masteredWords = masteredWords
    )

    companion object {
        fun fromDomainModel(level: Level): LevelEntity = LevelEntity(
            id = level.id,
            title = level.title,
            description = level.description,
            order = level.order,
            isUnlocked = level.isUnlocked
        )
    }
}
