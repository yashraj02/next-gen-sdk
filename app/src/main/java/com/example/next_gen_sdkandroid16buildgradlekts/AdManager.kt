package com.example.next_gen_sdkandroid16buildgradlekts

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.initialization.OnAdapterInitializationCompleteListener
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd
import com.google.android.libraries.ads.mobile.sdk.rewardedinterstitial.RewardedInterstitialAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AdManager {
    private const val TAG = "AdManager"

    // Test IDs
    private const val BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val REWARDED_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/5354046379"
    private const val NATIVE_ID = "ca-app-pub-3940256099942544/2247696110"
    private const val APPLICATION_ID = "ca-app-pub-3940256099942544~3347511713"

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    fun initialize(context: Context, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val config = InitializationConfig.Builder(APPLICATION_ID).build()
            MobileAds.initialize(context, config) { statusMap ->
                Log.d(TAG, "GMA Next-Gen SDK Initialized")
                CoroutineScope(Dispatchers.Main).launch {
                    onComplete()
                }
            }
        }
    }

    fun loadInterstitial() {
        val adRequest = AdRequest.Builder(INTERSTITIAL_ID).build()
        InterstitialAd.load(adRequest, object : AdLoadCallback<InterstitialAd> {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.d(TAG, "Interstitial loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
                Log.e(TAG, "Interstitial failed to load: ${error.message}")
            }
        })
    }

    fun showInterstitial(activity: Activity) {
        interstitialAd?.show(activity) ?: Toast.makeText(activity, "Interstitial not ready", Toast.LENGTH_SHORT).show()
    }

    fun loadRewarded() {
        val adRequest = AdRequest.Builder(REWARDED_ID).build()
        RewardedAd.load(adRequest, object : AdLoadCallback<RewardedAd> {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                Log.d(TAG, "Rewarded loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedAd = null
                Log.e(TAG, "Rewarded failed to load: ${error.message}")
            }
        })
    }

    fun showRewarded(activity: Activity, onRewardEarned: (Int) -> Unit) {
        rewardedAd?.show(activity) { rewardItem ->
            activity.runOnUiThread {
                onRewardEarned(rewardItem.amount)
            }
        } ?: Toast.makeText(activity, "Rewarded not ready", Toast.LENGTH_SHORT).show()
    }

    fun loadRewardedInterstitial() {
        val adRequest = AdRequest.Builder(REWARDED_INTERSTITIAL_ID).build()
        RewardedInterstitialAd.load(adRequest, object : AdLoadCallback<RewardedInterstitialAd> {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                rewardedInterstitialAd = ad
                Log.d(TAG, "Rewarded Interstitial loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedInterstitialAd = null
                Log.e(TAG, "Rewarded Interstitial failed to load: ${error.message}")
            }
        })
    }

    fun showRewardedInterstitial(activity: Activity, onRewardEarned: (Int) -> Unit) {
        rewardedInterstitialAd?.show(activity) { rewardItem ->
            activity.runOnUiThread {
                onRewardEarned(rewardItem.amount)
            }
        } ?: Toast.makeText(activity, "Rewarded Interstitial not ready", Toast.LENGTH_SHORT).show()
    }

    fun loadNativeAd(activity: Activity, onNativeAdLoaded: (NativeAd) -> Unit) {
        val adRequest = NativeAdRequest.Builder(NATIVE_ID, listOf(NativeAd.NativeAdType.NATIVE)).build()
        NativeAdLoader.load(adRequest, object : NativeAdLoaderCallback {
            override fun onNativeAdLoaded(nativeAd: NativeAd) {
                activity.runOnUiThread {
                    onNativeAdLoaded(nativeAd)
                }
            }

            override fun onCustomNativeAdLoaded(customNativeAd: com.google.android.libraries.ads.mobile.sdk.nativead.CustomNativeAd) {
                // Not used
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Native ad failed to load: ${error.message}")
            }
        })
    }

    fun createBannerAd(activity: Activity, onAdLoaded: () -> Unit): AdView {
        val adView = AdView(activity)
        val bannerAdRequest = BannerAdRequest.Builder(BANNER_ID, AdSize.BANNER).build()
        adView.loadAd(bannerAdRequest, object : AdLoadCallback<BannerAd> {
            override fun onAdLoaded(ad: BannerAd) {
                activity.runOnUiThread {
                    onAdLoaded()
                }
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Banner failed to load: ${error.message}")
            }
        })
        return adView
    }
}
