package com.dearyoti.lexiladder.data.repository

import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.model.ReviewRating
import com.dearyoti.lexiladder.domain.model.UserProgress
import com.dearyoti.lexiladder.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
    fun getAllWords(): Flow<List<Word>>
    fun searchWords(query: String): Flow<List<Word>>
    fun getWordsByLevel(levelId: String): Flow<List<Word>>
    fun getWordsByUnit(unitId: String): Flow<List<Word>>
    fun getAllLevels(): Flow<List<Level>>
    fun getMasteredWordCount(): Flow<Int>
    fun getFavorites(): Flow<List<Word>>
    fun getWordsDueForReview(currentTime: Long = System.currentTimeMillis()): Flow<List<Word>>
    fun getDueReviewCount(currentTime: Long = System.currentTimeMillis()): Flow<Int>
    fun getDifficultWords(): Flow<List<Word>>
    suspend fun getWordById(wordId: String): Word?
    suspend fun getProgressForWord(wordId: String): UserProgress?
    suspend fun recordReviewRating(wordId: String, rating: ReviewRating, now: Long = System.currentTimeMillis())
    suspend fun recordReviewResult(wordId: String, isCorrect: Boolean)
    suspend fun toggleFavorite(wordId: String)
    suspend fun seedInitialDataIfEmpty()
}
