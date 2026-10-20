package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.data.preferences.UserPreferencesRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoUpdateCheckTest {

    @Test
    fun testFirstLaunchAlwaysTriggersCheck() {
        val lastCheckTime = 0L
        val shouldCheck = UserPreferencesRepository.isCheckIntervalElapsed(lastCheckTime)
        assertTrue("First launch (timestamp 0) should always trigger update check", shouldCheck)
    }

    @Test
    fun testElapsedMoreThan24HoursTriggersCheck() {
        val now = System.currentTimeMillis()
        val twentyFiveHoursAgo = now - (25 * 60 * 60 * 1000L)
        val shouldCheck = UserPreferencesRepository.isCheckIntervalElapsed(twentyFiveHoursAgo)
        assertTrue("Timestamp older than 24 hours should trigger update check", shouldCheck)
    }

    @Test
    fun testWithin24HoursDoesNotTriggerCheck() {
        val now = System.currentTimeMillis()
        val twoHoursAgo = now - (2 * 60 * 60 * 1000L)
        val shouldCheck = UserPreferencesRepository.isCheckIntervalElapsed(twoHoursAgo)
        assertFalse("Timestamp within 24 hours should NOT trigger update check", shouldCheck)
    }

    @Test
    fun testCustomIntervalSupport() {
        val now = System.currentTimeMillis()
        val tenMinutesAgo = now - (10 * 60 * 1000L)
        val fiveMinutesInterval = 5 * 60 * 1000L

        val shouldCheck = UserPreferencesRepository.isCheckIntervalElapsed(tenMinutesAgo, fiveMinutesInterval)
        assertTrue("Custom interval elapsed should return true", shouldCheck)
    }
}
