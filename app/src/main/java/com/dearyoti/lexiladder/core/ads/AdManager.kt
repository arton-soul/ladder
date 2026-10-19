package com.dearyoti.lexiladder.core.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager private constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isInitializing = false
    private var isInitialized = false

    companion object {
        private const val TAG = "AdManager"

        @Volatile
        private var instance: AdManager? = null

        fun getInstance(): AdManager {
            return instance ?: synchronized(this) {
                instance ?: AdManager().also { instance = it }
            }
        }
    }

    /**
     * Initialize MobileAds asynchronously. Safe against crashes or network failures.
     */
    fun initialize(context: Context) {
        if (isInitialized || isInitializing) return
        isInitializing = true

        try {
            MobileAds.initialize(context) { status ->
                isInitialized = true
                isInitializing = false
                Log.d(TAG, "AdMob MobileAds initialized successfully: ${status.adapterStatusMap.keys}")
                // Preload an interstitial ad for future natural pauses
                loadInterstitialAd(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "AdMob initialization error (offline or missing service): ${e.message}")
            isInitializing = false
        }
    }

    /**
     * Preloads an Interstitial Ad in background.
     */
    fun loadInterstitialAd(context: Context) {
        try {
            val adRequest = AdRequest.Builder().build()
            val adUnitId = AdConfig.interstitialAdUnitId

            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d(TAG, "Interstitial ad loaded successfully.")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating interstitial load: ${e.message}")
        }
    }

    /**
     * Displays an Interstitial Ad at a safe, natural pause (e.g. session end).
     * If user is premium, ad is null, or fails to show, onDismiss is called immediately.
     */
    fun showInterstitialAd(
        activity: Activity,
        isPremium: Boolean,
        onDismiss: () -> Unit
    ) {
        if (isPremium) {
            onDismiss()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    // Preload the next one
                    loadInterstitialAd(activity.applicationContext)
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    Log.w(TAG, "Failed to show interstitial ad: ${adError.message}")
                    loadInterstitialAd(activity.applicationContext)
                    onDismiss()
                }
            }
            ad.show(activity)
        } else {
            // No ad available, continue flow seamlessly without interruption
            loadInterstitialAd(activity.applicationContext)
            onDismiss()
        }
    }

    /**
     * Preload Rewarded Ad.
     */
    fun loadRewardedAd(context: Context) {
        try {
            val adRequest = AdRequest.Builder().build()
            val adUnitId = AdConfig.rewardedAdUnitId

            RewardedAd.load(
                context,
                adUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        Log.d(TAG, "Rewarded ad loaded successfully.")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        rewardedAd = null
                        Log.w(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating rewarded ad load: ${e.message}")
        }
    }

    /**
     * Displays a Rewarded Ad.
     */
    fun showRewardedAd(
        activity: Activity,
        isPremium: Boolean,
        onRewardEarned: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (isPremium) {
            onRewardEarned()
            onDismiss()
            return
        }

        val ad = rewardedAd
        if (ad != null) {
            var earned = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity.applicationContext)
                    if (earned) {
                        onRewardEarned()
                    }
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    Log.w(TAG, "Failed to show rewarded ad: ${adError.message}")
                    loadRewardedAd(activity.applicationContext)
                    onDismiss()
                }
            }
            ad.show(activity) {
                earned = true
            }
        } else {
            loadRewardedAd(activity.applicationContext)
            onDismiss()
        }
    }
}
