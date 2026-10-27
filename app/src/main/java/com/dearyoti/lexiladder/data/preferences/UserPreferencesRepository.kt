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
    val hasExamBooster: Boolean = false,
    val hasAllAccessLifetime: Boolean = false,
    val lastContentCheckTimestamp: Long = 0L,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveDate: String = "",
    val activeDates: Set<String> = emptySet(),
    val todayStudyCount: Int = 0,
    val todayStudyDate: String = "",
    val selectedLevelId: String = "level_a1",
    val notifiedExam60LevelIds: Set<String> = emptySet(),
    val notifiedExam100LevelIds: Set<String> = emptySet()
) {
    val effectiveStreak: Int
        get() = UserPreferencesRepository.calculateEffectiveStreak(currentStreak, lastActiveDate)

    val isStudiedToday: Boolean
        get() = lastActiveDate == LocalDate.now().toString()

    val effectiveTodayStudyCount: Int
        get() = if (todayStudyDate == LocalDate.now().toString()) todayStudyCount else 0

    val dailyTargetReviews: Int
        get() = (dailyGoalMinutes * 2).coerceAtLeast(1)

    val dailyGoalProgressFraction: Float
        get() = (effectiveTodayStudyCount.toFloat() / dailyTargetReviews.toFloat()).coerceIn(0f, 1f)

    val isAdFree: Boolean
        get() = isPremium || hasAllAccessLifetime

    val isExamBoosterUnlocked: Boolean
        get() = hasExamBooster || hasAllAccessLifetime
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
        val KEY_HAS_EXAM_BOOSTER = booleanPreferencesKey("has_exam_booster")
        val KEY_HAS_ALL_ACCESS_LIFETIME = booleanPreferencesKey("has_all_access_lifetime")
        val KEY_LAST_CONTENT_CHECK = longPreferencesKey("last_content_check_timestamp")
        val KEY_CURRENT_STREAK = intPreferencesKey("current_streak")
        val KEY_BEST_STREAK = intPreferencesKey("best_streak")
        val KEY_LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
        val KEY_ACTIVE_DATES = stringSetPreferencesKey("active_dates")
        val KEY_TODAY_STUDY_COUNT = intPreferencesKey("today_study_count")
        val KEY_TODAY_STUDY_DATE = stringPreferencesKey("today_study_date")
        val KEY_SELECTED_LEVEL_ID = stringPreferencesKey("selected_level_id")
        val KEY_NOTIFIED_EXAM_60_LEVEL_IDS = stringSetPreferencesKey("notified_exam_60_level_ids")
        val KEY_NOTIFIED_EXAM_100_LEVEL_IDS = stringSetPreferencesKey("notified_exam_100_level_ids")

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
            hasExamBooster = preferences[KEY_HAS_EXAM_BOOSTER] ?: false,
            hasAllAccessLifetime = preferences[KEY_HAS_ALL_ACCESS_LIFETIME] ?: false,
            lastContentCheckTimestamp = preferences[KEY_LAST_CONTENT_CHECK] ?: 0L,
            currentStreak = preferences[KEY_CURRENT_STREAK] ?: 0,
            bestStreak = preferences[KEY_BEST_STREAK] ?: 0,
            lastActiveDate = preferences[KEY_LAST_ACTIVE_DATE] ?: "",
            activeDates = preferences[KEY_ACTIVE_DATES] ?: emptySet(),
            todayStudyCount = preferences[KEY_TODAY_STUDY_COUNT] ?: 0,
            todayStudyDate = preferences[KEY_TODAY_STUDY_DATE] ?: "",
            selectedLevelId = preferences[KEY_SELECTED_LEVEL_ID] ?: "level_a1",
            notifiedExam60LevelIds = preferences[KEY_NOTIFIED_EXAM_60_LEVEL_IDS] ?: emptySet(),
            notifiedExam100LevelIds = preferences[KEY_NOTIFIED_EXAM_100_LEVEL_IDS] ?: emptySet()
        )
    }

    suspend fun markExamEligibleNotified(levelId: String, isHundredPercent: Boolean = false) {
        context.dataStore.edit { preferences ->
            if (isHundredPercent) {
                val existing100 = preferences[KEY_NOTIFIED_EXAM_100_LEVEL_IDS] ?: emptySet()
                preferences[KEY_NOTIFIED_EXAM_100_LEVEL_IDS] = existing100 + levelId
                val existing60 = preferences[KEY_NOTIFIED_EXAM_60_LEVEL_IDS] ?: emptySet()
                preferences[KEY_NOTIFIED_EXAM_60_LEVEL_IDS] = existing60 + levelId
            } else {
                val existing60 = preferences[KEY_NOTIFIED_EXAM_60_LEVEL_IDS] ?: emptySet()
                preferences[KEY_NOTIFIED_EXAM_60_LEVEL_IDS] = existing60 + levelId
            }
        }
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

    suspend fun setExamBooster(hasBooster: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HAS_EXAM_BOOSTER] = hasBooster
        }
    }

    suspend fun setAllAccessLifetime(hasAllAccess: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HAS_ALL_ACCESS_LIFETIME] = hasAllAccess
            if (hasAllAccess) {
                // All-Access automatically covers premium (ad-free) and exam booster
                preferences[KEY_IS_PREMIUM] = true
                preferences[KEY_HAS_EXAM_BOOSTER] = true
            }
        }
    }

    suspend fun updateSelectedLevel(levelId: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_LEVEL_ID] = levelId
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

            val todayStr = today.toString()
            val storedStudyDate = preferences[KEY_TODAY_STUDY_DATE] ?: ""
            val storedCount = preferences[KEY_TODAY_STUDY_COUNT] ?: 0
            val updatedCount = if (storedStudyDate == todayStr) storedCount + 1 else 1

            preferences[KEY_CURRENT_STREAK] = newStreak
            preferences[KEY_BEST_STREAK] = newBest
            preferences[KEY_LAST_ACTIVE_DATE] = todayStr
            preferences[KEY_ACTIVE_DATES] = updatedDates
            preferences[KEY_TODAY_STUDY_DATE] = todayStr
            preferences[KEY_TODAY_STUDY_COUNT] = updatedCount
        }
    }
}
