package com.dearyoti.lexiladder.data.repository

import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.model.UserProgress
import com.dearyoti.lexiladder.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
    fun getWordsByLevel(levelId: String): Flow<List<Word>>
    fun getWordsByUnit(unitId: String): Flow<List<Word>>
    fun getAllLevels(): Flow<List<Level>>
    fun getMasteredWordCount(): Flow<Int>
    fun getFavorites(): Flow<List<Word>>
    suspend fun getWordById(wordId: String): Word?
    suspend fun getProgressForWord(wordId: String): UserProgress?
    suspend fun recordReviewResult(wordId: String, isCorrect: Boolean)
    suspend fun toggleFavorite(wordId: String)
    suspend fun seedInitialDataIfEmpty()
}
