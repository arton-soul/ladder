package com.dearyoti.lexiladder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dearyoti.lexiladder.domain.model.Word

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val partOfSpeech: String,
    val definition: String,
    val exampleSentence: String,
    val levelId: String,
    val unitId: String,
    val phonetic: String? = null,
    val translation: String? = null
) {
    fun toDomainModel(): Word = Word(
        id = id,
        text = text,
        partOfSpeech = partOfSpeech,
        definition = definition,
        exampleSentence = exampleSentence,
        levelId = levelId,
        unitId = unitId,
        phonetic = phonetic,
        translation = translation
    )

    companion object {
        fun fromDomainModel(word: Word): WordEntity = WordEntity(
            id = word.id,
            text = word.text,
            partOfSpeech = word.partOfSpeech,
            definition = word.definition,
            exampleSentence = word.exampleSentence,
            levelId = word.levelId,
            unitId = word.unitId,
            phonetic = word.phonetic,
            translation = word.translation
        )
    }
}
