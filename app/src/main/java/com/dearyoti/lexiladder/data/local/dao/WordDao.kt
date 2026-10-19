package com.dearyoti.lexiladder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dearyoti.lexiladder.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE levelId = :levelId ORDER BY text ASC")
    fun getWordsByLevel(levelId: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE unitId = :unitId ORDER BY text ASC")
    fun getWordsByUnit(unitId: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :wordId LIMIT 1")
    suspend fun getWordById(wordId: String): WordEntity?

    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words WHERE levelId = :levelId")
    suspend fun getWordCountByLevel(levelId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("DELETE FROM words WHERE levelId = :levelId")
    suspend fun deleteWordsByLevel(levelId: String)
}
