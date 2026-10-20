package com.dearyoti.lexiladder.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val uiLanguage: String = "en",
    val explanationLanguage: String = "id",
    val targetLanguage: String = "en",
    val dailyGoalMinutes: Int = 5,
    val isDarkMode: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    val isPremium: Boolean = false,
    val lastContentCheckTimestamp: Long = 0L
)

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val KEY_UI_LANGUAGE = stringPreferencesKey("ui_language")
        val KEY_EXPLANATION_LANGUAGE = stringPreferencesKey("explanation_language")
        val KEY_TARGET_LANGUAGE = stringPreferencesKey("target_language")
        val KEY_DAILY_GOAL = intPreferencesKey("daily_goal_minutes")
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_IS_PREMIUM = booleanPreferencesKey("is_premium")
        val KEY_LAST_CONTENT_CHECK = longPreferencesKey("last_content_check_timestamp")

        const val DEFAULT_CHECK_INTERVAL_MS = 24 * 60 * 60 * 1000L // 24 hours (once a day)

        fun isCheckIntervalElapsed(lastCheckTime: Long, intervalMs: Long = DEFAULT_CHECK_INTERVAL_MS): Boolean {
            if (lastCheckTime <= 0L) return true
            return (System.currentTimeMillis() - lastCheckTime) >= intervalMs
        }
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            uiLanguage = preferences[KEY_UI_LANGUAGE] ?: "en",
            explanationLanguage = preferences[KEY_EXPLANATION_LANGUAGE] ?: "id",
            targetLanguage = preferences[KEY_TARGET_LANGUAGE] ?: "en",
            dailyGoalMinutes = preferences[KEY_DAILY_GOAL] ?: 5,
            isDarkMode = preferences[KEY_DARK_MODE] ?: false,
            isOnboardingCompleted = preferences[KEY_ONBOARDING_COMPLETED] ?: false,
            isPremium = preferences[KEY_IS_PREMIUM] ?: false,
            lastContentCheckTimestamp = preferences[KEY_LAST_CONTENT_CHECK] ?: 0L
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

    suspend fun setCompletedOnboarding(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setPremium(isPremium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_PREMIUM] = isPremium
        }
    }

    suspend fun updateLastContentCheckTimestamp(timestamp: Long = System.currentTimeMillis()) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAST_CONTENT_CHECK] = timestamp
        }
    }
}
