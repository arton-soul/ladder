package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.core.ads.AdConfig
import com.dearyoti.lexiladder.core.billing.BillingManager
import com.dearyoti.lexiladder.data.preferences.UserPreferences
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MonetizationAndAdTest {

    @Before
    fun setUp() {
        AdConfig.forceTestMode = true
    }

    @After
    fun tearDown() {
        AdConfig.forceTestMode = null
    }

    @Test
    fun testTestModeAdUnitIdsUseGoogleSampleIds() {
        AdConfig.forceTestMode = true

        assertEquals(AdConfig.TEST_BANNER_AD_UNIT_ID, AdConfig.bannerAdUnitId)
        assertEquals(AdConfig.TEST_INTERSTITIAL_AD_UNIT_ID, AdConfig.interstitialAdUnitId)
        assertEquals(AdConfig.TEST_REWARDED_AD_UNIT_ID, AdConfig.rewardedAdUnitId)

        assertEquals("ca-app-pub-3940256099942544/6300978111", AdConfig.bannerAdUnitId)
        assertEquals("ca-app-pub-3940256099942544/1033173712", AdConfig.interstitialAdUnitId)
        assertEquals("ca-app-pub-3940256099942544/5224354917", AdConfig.rewardedAdUnitId)
    }

    @Test
    fun testProductionModeAdUnitIdsMatchUserRegisteredIds() {
        AdConfig.forceTestMode = false

        assertEquals(AdConfig.PROD_BANNER_AD_UNIT_ID, AdConfig.bannerAdUnitId)
        assertEquals(AdConfig.PROD_INTERSTITIAL_AD_UNIT_ID, AdConfig.interstitialAdUnitId)
        assertEquals(AdConfig.PROD_REWARDED_AD_UNIT_ID, AdConfig.rewardedAdUnitId)

        assertEquals("ca-app-pub-4098008677804088/3371650160", AdConfig.bannerAdUnitId)
        assertEquals("ca-app-pub-4098008677804088/5833417114", AdConfig.interstitialAdUnitId)
        assertEquals("ca-app-pub-4098008677804088/6795144996", AdConfig.rewardedAdUnitId)
    }

    @Test
    fun testAdStrictlyForbiddenDuringMasteryTest() {
        // Ads must NEVER display during active mastery tests regardless of premium status
        assertFalse(AdConfig.canDisplayAd("mastery_test", isPremium = false))
        assertFalse(AdConfig.canDisplayAd("mastery_test", isPremium = true))
    }

    @Test
    fun testAdStrictlyForbiddenDuringActiveQuizQuestions() {
        // Ads must NEVER display during active quiz questions
        assertFalse(AdConfig.canDisplayAd("quiz_question", isPremium = false))
        assertFalse(AdConfig.canDisplayAd("quiz_active", isPremium = false))
        assertFalse(AdConfig.canDisplayAd("exam_evaluation", isPremium = false))
    }

    @Test
    fun testAdAllowedOnNonLearningScreensWhenNotPremium() {
        // Free users can see banner on non-learning, non-test screens
        assertTrue(AdConfig.canDisplayAd("home", isPremium = false))
        assertTrue(AdConfig.canDisplayAd("progress", isPremium = false))
        assertTrue(AdConfig.canDisplayAd("settings", isPremium = false))
    }

    @Test
    fun testPremiumUsersNeverSeeAdsAnywhere() {
        // Upgraded premium users must NEVER see ads on ANY screen
        assertFalse(AdConfig.canDisplayAd("home", isPremium = true))
        assertFalse(AdConfig.canDisplayAd("progress", isPremium = true))
        assertFalse(AdConfig.canDisplayAd("settings", isPremium = true))
        assertFalse(AdConfig.canDisplayAd("quiz_result", isPremium = true))
        assertFalse(AdConfig.canDisplayAd("mastery_test", isPremium = true))
    }

    @Test
    fun testBillingProductConstants() {
        assertEquals("lexiladder_premium_remove_ads", BillingManager.PRODUCT_PREMIUM_REMOVE_ADS)
        assertEquals("lexiladder_exam_booster_pack", BillingManager.PRODUCT_EXAM_BOOSTER)
        assertEquals("lexiladder_all_access_lifetime", BillingManager.PRODUCT_ALL_ACCESS_LIFETIME)

        assertEquals(3, BillingManager.ALL_PRODUCT_IDS.size)
        assertTrue(BillingManager.ALL_PRODUCT_IDS.contains(BillingManager.PRODUCT_ALL_ACCESS_LIFETIME))
        assertTrue(BillingManager.ALL_PRODUCT_IDS.contains(BillingManager.PRODUCT_EXAM_BOOSTER))
        assertTrue(BillingManager.ALL_PRODUCT_IDS.contains(BillingManager.PRODUCT_PREMIUM_REMOVE_ADS))
    }

    @Test
    fun testUserPreferencesPremiumDefaultFalse() {
        val prefs = UserPreferences()
        assertFalse(prefs.isPremium)
        assertFalse(prefs.hasExamBooster)
        assertFalse(prefs.hasAllAccessLifetime)
        assertFalse(prefs.isAdFree)
        assertFalse(prefs.isExamBoosterUnlocked)
    }

    @Test
    fun testUserPreferencesPremiumActivation() {
        val prefs = UserPreferences(isPremium = true)
        assertTrue(prefs.isPremium)
        assertTrue(prefs.isAdFree)
        assertFalse(prefs.hasExamBooster)
        assertFalse(prefs.isExamBoosterUnlocked)
    }

    @Test
    fun testUserPreferencesExamBoosterActivation() {
        val prefs = UserPreferences(hasExamBooster = true)
        assertFalse(prefs.isPremium)
        assertFalse(prefs.isAdFree)
        assertTrue(prefs.hasExamBooster)
        assertTrue(prefs.isExamBoosterUnlocked)
    }

    @Test
    fun testUserPreferencesAllAccessLifetimeActivation() {
        val prefs = UserPreferences(hasAllAccessLifetime = true)
        assertTrue(prefs.hasAllAccessLifetime)
        assertTrue(prefs.isAdFree)
        assertTrue(prefs.isExamBoosterUnlocked)
    }
}
