package com.dearyoti.lexiladder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dearyoti.lexiladder.data.local.entity.LevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Query("SELECT * FROM levels ORDER BY `order` ASC")
    fun getAllLevels(): Flow<List<LevelEntity>>

    @Query("SELECT * FROM levels WHERE id = :levelId LIMIT 1")
    suspend fun getLevelById(levelId: String): LevelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<LevelEntity>)

    @Query("UPDATE levels SET isUnlocked = :isUnlocked WHERE id = :levelId")
    suspend fun setLevelUnlocked(levelId: String, isUnlocked: Boolean)
}
