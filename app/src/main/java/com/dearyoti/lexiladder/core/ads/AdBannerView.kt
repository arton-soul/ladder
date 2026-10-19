package com.dearyoti.lexiladder.core.ads

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdBannerView(
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    if (isPremium) {
        // Zero footprint when premium is active
        return
    }

    val context = LocalContext.current
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = AdConfig.bannerAdUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d("AdBannerView", "Banner ad loaded successfully.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w("AdBannerView", "Banner ad failed to load: ${error.message} (code: ${error.code})")
                }
            }
            try {
                loadAd(AdRequest.Builder().build())
            } catch (e: Exception) {
                Log.e("AdBannerView", "Failed to request banner ad: ${e.message}")
            }
        }
    }

    DisposableEffect(adView) {
        onDispose {
            try {
                adView.destroy()
            } catch (e: Exception) {
                Log.e("AdBannerView", "Error destroying adView: ${e.message}")
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
