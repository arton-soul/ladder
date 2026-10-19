package com.dearyoti.lexiladder.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val uiLanguage: String,
    val explanationLanguage: String,
    val targetLanguage: String,
    val dailyGoalMinutes: Int,
    val isDarkMode: Boolean
)

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val KEY_UI_LANGUAGE = stringPreferencesKey("ui_language")
        val KEY_EXPLANATION_LANGUAGE = stringPreferencesKey("explanation_language")
        val KEY_TARGET_LANGUAGE = stringPreferencesKey("target_language")
        val KEY_DAILY_GOAL = intPreferencesKey("daily_goal_minutes")
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            uiLanguage = preferences[KEY_UI_LANGUAGE] ?: "en",
            explanationLanguage = preferences[KEY_EXPLANATION_LANGUAGE] ?: "id",
            targetLanguage = preferences[KEY_TARGET_LANGUAGE] ?: "en",
            dailyGoalMinutes = preferences[KEY_DAILY_GOAL] ?: 5,
            isDarkMode = preferences[KEY_DARK_MODE] ?: false
        )
    }

    suspend fun updateUiLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_UI_LANGUAGE] = language
        }
    }

    suspend fun updateExplanationLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_EXPLANATION_LANGUAGE] = language
        }
    }

    suspend fun updateDailyGoal(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DAILY_GOAL] = minutes
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = enabled
        }
    }
}
