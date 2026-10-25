package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.data.preferences.UserPreferences
import com.dearyoti.lexiladder.data.preferences.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DailyStreakTest {

    @Test
    fun calculateStreak_firstStudySessionStartsStreakAtOne() {
        val today = LocalDate.of(2026, 10, 25)
        val (streak, best) = UserPreferencesRepository.calculateStreak(
            lastActiveDateStr = "",
            currentStreak = 0,
            bestStreak = 0,
            today = today
        )
        assertEquals("First study session should give streak 1", 1, streak)
        assertEquals("Best streak should become 1", 1, best)
    }

    @Test
    fun calculateStreak_consecutiveDayIncrementsStreakAndUpdatesBest() {
        val day1 = LocalDate.of(2026, 10, 25)
        val day2 = LocalDate.of(2026, 10, 26)
        val day3 = LocalDate.of(2026, 10, 27)

        val (streakDay2, bestDay2) = UserPreferencesRepository.calculateStreak(
            lastActiveDateStr = day1.toString(),
            currentStreak = 1,
            bestStreak = 1,
            today = day2
        )
        assertEquals("Consecutive day should increment streak to 2", 2, streakDay2)
        assertEquals("Best streak should update to 2", 2, bestDay2)

        val (streakDay3, bestDay3) = UserPreferencesRepository.calculateStreak(
            lastActiveDateStr = day2.toString(),
            currentStreak = streakDay2,
            bestStreak = bestDay2,
            today = day3
        )
        assertEquals("Consecutive day should increment streak to 3", 3, streakDay3)
        assertEquals("Best streak should update to 3", 3, bestDay3)
    }

    @Test
    fun calculateStreak_sameDayStudyDoesNotIncrementStreak() {
        val today = LocalDate.of(2026, 10, 25)
        val (streak, best) = UserPreferencesRepository.calculateStreak(
            lastActiveDateStr = today.toString(),
            currentStreak = 4,
            bestStreak = 10,
            today = today
        )
        assertEquals("Studying multiple times on same day keeps current streak", 4, streak)
        assertEquals("Best streak remains untouched", 10, best)
    }

    @Test
    fun calculateStreak_brokenStreakResetsToOneAndPreservesBestStreak() {
        val day1 = LocalDate.of(2026, 10, 20)
        val dayMissed = LocalDate.of(2026, 10, 25) // 5 days later

        val (streak, best) = UserPreferencesRepository.calculateStreak(
            lastActiveDateStr = day1.toString(),
            currentStreak = 7,
            bestStreak = 14,
            today = dayMissed
        )
        assertEquals("Broken streak resets to 1", 1, streak)
        assertEquals("Best streak of 14 is preserved", 14, best)
    }

    @Test
    fun calculateEffectiveStreak_returnsExpectedValues() {
        val today = LocalDate.of(2026, 10, 25)
        val yesterday = LocalDate.of(2026, 10, 24)
        val twoDaysAgo = LocalDate.of(2026, 10, 23)

        // Empty date
        assertEquals(0, UserPreferencesRepository.calculateEffectiveStreak(5, "", today))

        // Studied today: streak active
        assertEquals(5, UserPreferencesRepository.calculateEffectiveStreak(5, today.toString(), today))

        // Studied yesterday: streak still valid waiting for today's session
        assertEquals(5, UserPreferencesRepository.calculateEffectiveStreak(5, yesterday.toString(), today))

        // Missed yesterday: streak expired
        assertEquals(0, UserPreferencesRepository.calculateEffectiveStreak(5, twoDaysAgo.toString(), today))
    }

    @Test
    fun userPreferences_effectiveStreakAndIsStudiedToday() {
        val todayStr = LocalDate.now().toString()
        val prefsActiveToday = UserPreferences(
            currentStreak = 5,
            bestStreak = 10,
            lastActiveDate = todayStr
        )
        assertEquals(5, prefsActiveToday.effectiveStreak)
        assertTrue(prefsActiveToday.isStudiedToday)

        val prefsBroken = UserPreferences(
            currentStreak = 5,
            bestStreak = 10,
            lastActiveDate = "2020-01-01"
        )
        assertEquals(0, prefsBroken.effectiveStreak)
        assertFalse(prefsBroken.isStudiedToday)
    }
}
