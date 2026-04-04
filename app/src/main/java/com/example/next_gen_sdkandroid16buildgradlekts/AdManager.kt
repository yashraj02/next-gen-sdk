package com.example.next_gen_sdkandroid16buildgradlekts

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"

    // Test IDs
    private const val BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val REWARDED_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/5354046379"
    private const val NATIVE_ID = "ca-app-pub-3940256099942544/2247696110"

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }

    fun loadInterstitial(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, INTERSTITIAL_ID, adRequest, object : InterstitialAdLoadCallback() {
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

    fun loadRewarded(context: Context) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, REWARDED_ID, adRequest, object : RewardedAdLoadCallback() {
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
            onRewardEarned(rewardItem.amount)
        } ?: Toast.makeText(activity, "Rewarded not ready", Toast.LENGTH_SHORT).show()
    }

    fun loadRewardedInterstitial(context: Context) {
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(context, REWARDED_INTERSTITIAL_ID, adRequest, object : RewardedInterstitialAdLoadCallback() {
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
            onRewardEarned(rewardItem.amount)
        } ?: Toast.makeText(activity, "Rewarded Interstitial not ready", Toast.LENGTH_SHORT).show()
    }

    fun loadNativeAd(context: Context, onNativeAdLoaded: (NativeAd) -> Unit) {
        val adLoader = AdLoader.Builder(context, NATIVE_ID)
            .forNativeAd { nativeAd -> onNativeAdLoaded(nativeAd) }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Native ad failed to load: ${error.message}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun createBannerAd(context: Context): AdView {
        return AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = BANNER_ID
            loadAd(AdRequest.Builder().build())
        }
    }
}
