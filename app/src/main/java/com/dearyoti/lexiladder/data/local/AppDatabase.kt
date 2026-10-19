package com.dearyoti.lexiladder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dearyoti.lexiladder.data.local.dao.LevelDao
import com.dearyoti.lexiladder.data.local.dao.UserProgressDao
import com.dearyoti.lexiladder.data.local.dao.WordDao
import com.dearyoti.lexiladder.data.local.entity.LevelEntity
import com.dearyoti.lexiladder.data.local.entity.UserProgressEntity
import com.dearyoti.lexiladder.data.local.entity.WordEntity

@Database(
    entities = [
        WordEntity::class,
        UserProgressEntity::class,
        LevelEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun levelDao(): LevelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lexiladder_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
