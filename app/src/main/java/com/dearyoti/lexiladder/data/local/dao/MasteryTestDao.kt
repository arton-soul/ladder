package com.dearyoti.lexiladder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dearyoti.lexiladder.data.local.entity.MasteryTestAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MasteryTestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: MasteryTestAttemptEntity)

    @Query("SELECT * FROM mastery_test_attempts WHERE levelId = :levelId ORDER BY timestamp DESC")
    fun getAttemptsByLevel(levelId: String): Flow<List<MasteryTestAttemptEntity>>

    @Query("SELECT * FROM mastery_test_attempts WHERE levelId = :levelId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestAttempt(levelId: String): Flow<MasteryTestAttemptEntity?>

    @Query("SELECT COUNT(*) FROM mastery_test_attempts WHERE levelId = :levelId AND isPassed = 1")
    fun getPassedCount(levelId: String): Flow<Int>

    @Query("SELECT COUNT(*) > 0 FROM mastery_test_attempts WHERE levelId = :levelId AND isPassed = 1")
    suspend fun hasPassedLevel(levelId: String): Boolean

    @Query("SELECT * FROM mastery_test_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<MasteryTestAttemptEntity>>
}
