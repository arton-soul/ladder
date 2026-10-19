package com.dearyoti.lexiladder.core.ads

import com.dearyoti.lexiladder.BuildConfig

/**
 * AdMob configuration holding Google Test IDs and Production IDs.
 * Strictly uses Google sample test IDs during debug testing to prevent account strikes.
 */
object AdConfig {
    // Production IDs registered for LexiLadder
    const val PROD_APP_ID = "ca-app-pub-4098008677804088~2532466359"
    const val PROD_BANNER_AD_UNIT_ID = "ca-app-pub-4098008677804088/3371650160"
    const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4098008677804088/5833417114"
    const val PROD_REWARDED_AD_UNIT_ID = "ca-app-pub-4098008677804088/6795144996"

    // Official Google AdMob Sample Test IDs for safe development & verification
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    /**
     * Flag allowing test overrides or defaulting to BuildConfig.DEBUG
     */
    var forceTestMode: Boolean? = null

    val isTestMode: Boolean
        get() = forceTestMode ?: BuildConfig.DEBUG

    val bannerAdUnitId: String
        get() = if (isTestMode) TEST_BANNER_AD_UNIT_ID else PROD_BANNER_AD_UNIT_ID

    val interstitialAdUnitId: String
        get() = if (isTestMode) TEST_INTERSTITIAL_AD_UNIT_ID else PROD_INTERSTITIAL_AD_UNIT_ID

    val rewardedAdUnitId: String
        get() = if (isTestMode) TEST_REWARDED_AD_UNIT_ID else PROD_REWARDED_AD_UNIT_ID

    /**
     * Enforce strict non-intrusive ad placement rules per rancangan.md and AGENTS.md:
     * - Ads must NEVER be displayed during active quiz questions, answers, or mastery tests.
     * - Ads must NEVER be displayed if the user has unlocked Premium.
     */
    fun canDisplayAd(screenName: String, isPremium: Boolean): Boolean {
        if (isPremium) return false

        // Restricted screens where ads are strictly forbidden
        val forbiddenScreens = setOf(
            "mastery_test",
            "quiz_question",
            "quiz_active",
            "exam_evaluation"
        )

        return !forbiddenScreens.contains(screenName.lowercase())
    }
}
