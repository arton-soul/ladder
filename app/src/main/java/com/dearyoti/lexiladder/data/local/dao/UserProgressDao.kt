package com.dearyoti.lexiladder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dearyoti.lexiladder.data.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE wordId = :wordId LIMIT 1")
    suspend fun getProgressForWord(wordId: String): UserProgressEntity?

    @Query("SELECT * FROM user_progress")
    fun getAllProgress(): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE isFavorite = 1")
    fun getFavoriteWords(): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE isMastered = 1")
    fun getMasteredWords(): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE nextReviewAt > 0 AND nextReviewAt <= :currentTime")
    suspend fun getWordsDueForReview(currentTime: Long): List<UserProgressEntity>

    @Query("SELECT COUNT(*) FROM user_progress WHERE isMastered = 1")
    fun getMasteredWordCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_progress WHERE isMastered = 1 AND wordId IN (SELECT id FROM words WHERE levelId = :levelId)")
    suspend fun getMasteredWordCountByLevel(levelId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET isFavorite = :isFavorite WHERE wordId = :wordId")
    suspend fun setFavorite(wordId: String, isFavorite: Boolean)
}
