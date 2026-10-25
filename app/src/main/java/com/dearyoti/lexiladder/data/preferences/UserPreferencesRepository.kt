package com.dearyoti.lexiladder.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val uiLanguage: String = "en",
    val explanationLanguage: String = "id",
    val targetLanguage: String = "en",
    val dailyGoalMinutes: Int = 5,
    val isDarkMode: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    val isPremium: Boolean = false,
    val lastContentCheckTimestamp: Long = 0L,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveDate: String = "",
    val activeDates: Set<String> = emptySet()
) {
    val effectiveStreak: Int
        get() = UserPreferencesRepository.calculateEffectiveStreak(currentStreak, lastActiveDate)

    val isStudiedToday: Boolean
        get() = lastActiveDate == LocalDate.now().toString()
}

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
        val KEY_CURRENT_STREAK = intPreferencesKey("current_streak")
        val KEY_BEST_STREAK = intPreferencesKey("best_streak")
        val KEY_LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
        val KEY_ACTIVE_DATES = stringSetPreferencesKey("active_dates")

        const val DEFAULT_CHECK_INTERVAL_MS = 24 * 60 * 60 * 1000L // 24 hours (once a day)

        fun isCheckIntervalElapsed(lastCheckTime: Long, intervalMs: Long = DEFAULT_CHECK_INTERVAL_MS): Boolean {
            if (lastCheckTime <= 0L) return true
            return (System.currentTimeMillis() - lastCheckTime) >= intervalMs
        }

        fun calculateStreak(
            lastActiveDateStr: String,
            currentStreak: Int,
            bestStreak: Int,
            today: LocalDate = LocalDate.now()
        ): Pair<Int, Int> {
            val todayStr = today.toString()
            if (lastActiveDateStr.isBlank()) {
                val newStreak = 1
                return Pair(newStreak, maxOf(bestStreak, newStreak))
            }
            if (lastActiveDateStr == todayStr) {
                val streak = if (currentStreak == 0) 1 else currentStreak
                return Pair(streak, maxOf(bestStreak, streak))
            }
            val lastDate = runCatching { LocalDate.parse(lastActiveDateStr) }.getOrNull()
            val newStreak = if (lastDate != null && lastDate.plusDays(1) == today) {
                currentStreak + 1
            } else {
                1
            }
            return Pair(newStreak, maxOf(bestStreak, newStreak))
        }

        fun calculateEffectiveStreak(
            currentStreak: Int,
            lastActiveDateStr: String,
            today: LocalDate = LocalDate.now()
        ): Int {
            if (lastActiveDateStr.isBlank() || currentStreak <= 0) return 0
            val lastDate = runCatching { LocalDate.parse(lastActiveDateStr) }.getOrNull() ?: return 0
            val daysDiff = ChronoUnit.DAYS.between(lastDate, today)
            return if (daysDiff in 0L..1L) currentStreak else 0
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
            lastContentCheckTimestamp = preferences[KEY_LAST_CONTENT_CHECK] ?: 0L,
            currentStreak = preferences[KEY_CURRENT_STREAK] ?: 0,
            bestStreak = preferences[KEY_BEST_STREAK] ?: 0,
            lastActiveDate = preferences[KEY_LAST_ACTIVE_DATE] ?: "",
            activeDates = preferences[KEY_ACTIVE_DATES] ?: emptySet()
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

    suspend fun recordDailyStudyActivity(today: LocalDate = LocalDate.now()) {
        context.dataStore.edit { preferences ->
            val lastDateStr = preferences[KEY_LAST_ACTIVE_DATE] ?: ""
            val currentStreak = preferences[KEY_CURRENT_STREAK] ?: 0
            val bestStreak = preferences[KEY_BEST_STREAK] ?: 0
            val existingDates = preferences[KEY_ACTIVE_DATES] ?: emptySet()

            val (newStreak, newBest) = calculateStreak(lastDateStr, currentStreak, bestStreak, today)
            val cutoff = today.minusDays(60).toString()
            val updatedDates = (existingDates + today.toString()).filter { it >= cutoff }.toSet()

            preferences[KEY_CURRENT_STREAK] = newStreak
            preferences[KEY_BEST_STREAK] = newBest
            preferences[KEY_LAST_ACTIVE_DATE] = today.toString()
            preferences[KEY_ACTIVE_DATES] = updatedDates
        }
    }
}
