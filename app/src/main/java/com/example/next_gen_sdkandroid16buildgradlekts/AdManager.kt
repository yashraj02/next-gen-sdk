package com.example.next_gen_sdkandroid16buildgradlekts

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
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
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd
import com.google.android.libraries.ads.mobile.sdk.rewardedinterstitial.RewardedInterstitialAd

object AdManager {
    private const val TAG = "AdManager"

    // Test IDs
    private const val APP_ID = "ca-app-pub-3940256099942544~3347511713"
    private const val BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val REWARDED_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/5354046379"
    private const val NATIVE_ID = "ca-app-pub-3940256099942544/2247696110"

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    fun initialize(context: Context, onComplete: () -> Unit) {
        val config = InitializationConfig.Builder(APP_ID).build()
        Thread {
            MobileAds.initialize(context, config) {
                Handler(Looper.getMainLooper()).post {
                    onComplete()
                }
            }
        }.start()
    }

    fun loadInterstitial(context: Context) {
        val adRequest = AdRequest.Builder(INTERSTITIAL_ID).build()
        InterstitialAd.load(adRequest, object : AdLoadCallback<InterstitialAd> {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.d(TAG, "Interstitial loaded")
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
                Log.e(TAG, "Interstitial failed to load: ${adError.message}")
            }
        })
    }

    fun showInterstitial(activity: Activity) {
        interstitialAd?.show(activity) ?: Toast.makeText(activity, "Interstitial not ready", Toast.LENGTH_SHORT).show()
    }

    fun loadRewarded(context: Context) {
        val adRequest = AdRequest.Builder(REWARDED_ID).build()
        RewardedAd.load(adRequest, object : AdLoadCallback<RewardedAd> {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                Log.d(TAG, "Rewarded loaded")
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
                Log.e(TAG, "Rewarded failed to load: ${adError.message}")
            }
        })
    }

    fun showRewarded(activity: Activity, onRewardEarned: (Int) -> Unit) {
        rewardedAd?.show(activity) { rewardItem ->
            Handler(Looper.getMainLooper()).post {
                onRewardEarned(rewardItem.amount)
            }
        } ?: Toast.makeText(activity, "Rewarded not ready", Toast.LENGTH_SHORT).show()
    }

    fun loadRewardedInterstitial(context: Context) {
        val adRequest = AdRequest.Builder(REWARDED_INTERSTITIAL_ID).build()
        RewardedInterstitialAd.load(adRequest, object : AdLoadCallback<RewardedInterstitialAd> {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                rewardedInterstitialAd = ad
                Log.d(TAG, "Rewarded Interstitial loaded")
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedInterstitialAd = null
                Log.e(TAG, "Rewarded Interstitial failed to load: ${adError.message}")
            }
        })
    }

    fun showRewardedInterstitial(activity: Activity, onRewardEarned: (Int) -> Unit) {
        rewardedInterstitialAd?.show(activity) { rewardItem ->
            Handler(Looper.getMainLooper()).post {
                onRewardEarned(rewardItem.amount)
            }
        } ?: Toast.makeText(activity, "Rewarded Interstitial not ready", Toast.LENGTH_SHORT).show()
    }

    fun loadNativeAd(context: Context, onNativeAdLoaded: (NativeAd) -> Unit) {
        val nativeAdRequest = NativeAdRequest.Builder(NATIVE_ID, listOf(NativeAd.NativeAdType.NATIVE)).build()
        NativeAdLoader.load(nativeAdRequest, object : NativeAdLoaderCallback {
            override fun onNativeAdLoaded(nativeAd: NativeAd) {
                Handler(Looper.getMainLooper()).post {
                    onNativeAdLoaded(nativeAd)
                }
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Native ad failed to load: ${adError.message}")
            }
        })
    }

    fun createBannerAd(context: Context, callback: AdLoadCallback<BannerAd>): AdView {
        val adView = AdView(context)
        val bannerAdRequest = BannerAdRequest.Builder(BANNER_ID, AdSize.BANNER).build()
        adView.loadAd(bannerAdRequest, callback)
        return adView
    }
}
