package com.example.next_gen_sdkandroid16buildgradlekts

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd
import com.google.android.libraries.ads.mobile.sdk.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.libraries.ads.mobile.sdk.banner.AdView


class MainActivity : AppCompatActivity() {

    private lateinit var tvLogs: TextView
    private val backgroundScope = CoroutineScope(Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    private var bannerAdView: AdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLogs = findViewById(R.id.tv_logs)

        setupButtons()

        logEvent("Next-Gen SDK: Initializing...")

        backgroundScope.launch {
            try {
                val initConfig = InitializationConfig.Builder("ca-app-pub-3940256099942544~3347511713").build()
                MobileAds.initialize(this@MainActivity, initConfig) {
                    mainScope.launch {
                        logEvent("Next-Gen SDK: Initialized")
                        loadBannerAd()
                        loadInterstitialAd()
                        loadRewardedAd()
                        loadRewardedInterstitialAd()
                        loadNativeAd()
                    }
                }
            } catch (e: Exception) {
                logEvent("Init Error: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAdView?.destroy()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_interstitial).setOnClickListener {
            interstitialAd?.show(this) ?: logEvent("Interstitial: Not ready")
        }
        findViewById<Button>(R.id.btn_rewarded).setOnClickListener {
            rewardedAd?.show(this) { reward ->
                logEvent("Rewarded: Earned ${reward.amount} ${reward.type}")
            } ?: logEvent("Rewarded: Not ready")
        }
        findViewById<Button>(R.id.btn_rewarded_interstitial).setOnClickListener {
            rewardedInterstitialAd?.show(this) { reward ->
                logEvent("Rewarded Interstitial: Earned ${reward.amount} ${reward.type}")
            } ?: logEvent("Rewarded Interstitial: Not ready")
        }
    }

    private fun loadBannerAd() {
        val adUnitId = "ca-app-pub-3940256099942544/6300978111"
        val bannerContainer = findViewById<FrameLayout>(R.id.banner_container)

        // NEW PATTERN: AdView is constructed first, then loadAd() is called on it.
        // AdView is itself a FrameLayout — add it to the container immediately.
        // No need to get a view from the loaded ad later.
        val adView = AdView(this)
        bannerAdView = adView
        bannerContainer.removeAllViews()
        bannerContainer.addView(adView)

        val adRequest = BannerAdRequest.Builder(adUnitId, AdSize.BANNER).build()
        adView.loadAd(adRequest, object : AdLoadCallback<BannerAd> {
            override fun onAdLoaded(ad: BannerAd) {
                // AdView renders itself — no getView() call needed at all.
                // runOnUiThread not needed here because adView is already
                // attached to the hierarchy and AdView handles its own rendering.
                logEvent("Banner: Loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                logEvent("Banner: Failed - ${error.message}")
            }
        })
    }

    private fun loadInterstitialAd() {
        val adUnitId = "ca-app-pub-3940256099942544/1033173712"
        val adRequest = AdRequest.Builder(adUnitId).build()
        InterstitialAd.load(adRequest, object : AdLoadCallback<InterstitialAd> {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                logEvent("Interstitial: Loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                logEvent("Interstitial: Failed - ${error.message}")
            }
        })
    }

    private fun loadRewardedAd() {
        val adUnitId = "ca-app-pub-3940256099942544/5224354917"
        val adRequest = AdRequest.Builder(adUnitId).build()
        RewardedAd.load(adRequest, object : AdLoadCallback<RewardedAd> {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                logEvent("Rewarded: Loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                logEvent("Rewarded: Failed - ${error.message}")
            }
        })
    }

    private fun loadRewardedInterstitialAd() {
        val adUnitId = "ca-app-pub-3940256099942544/5354046379"
        val adRequest = AdRequest.Builder(adUnitId).build()
        RewardedInterstitialAd.load(adRequest, object : AdLoadCallback<RewardedInterstitialAd> {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                rewardedInterstitialAd = ad
                logEvent("Rewarded Interstitial: Loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                logEvent("Rewarded Interstitial: Failed - ${error.message}")
            }
        })
    }

    private fun loadNativeAd() {
        val adUnitId = "ca-app-pub-3940256099942544/2247696110"
        val adRequest = NativeAdRequest.Builder(adUnitId, listOf(NativeAd.NativeAdType.NATIVE)).build()

        NativeAdLoader.load(adRequest, object : NativeAdLoaderCallback {
            override fun onNativeAdLoaded(nativeAd: NativeAd) {
                runOnUiThread {
                    populateNativeAdView(nativeAd, findViewById(R.id.native_ad_container))
                    logEvent("Native: Loaded")
                }
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                logEvent("Native: Failed - ${error.message}")
            }
        })
    }

    private fun populateNativeAdView(nativeAd: NativeAd, parent: FrameLayout) {
        val adView = layoutInflater.inflate(R.layout.native_ad_layout, null) as NativeAdView

        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        headlineView.text = nativeAd.headline

        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
        bodyView.text = nativeAd.body
        adView.bodyView = bodyView

        val ctaView = adView.findViewById<Button>(R.id.ad_call_to_action)
        ctaView.text = nativeAd.callToAction

        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        adView.registerNativeAd(nativeAd, mediaView)

        parent.removeAllViews()
        parent.addView(adView)
    }

    private fun logEvent(message: String) {
        mainScope.launch {
            val currentLogs = tvLogs.text.toString()
            tvLogs.text = "$message\n$currentLogs"
            Log.d("MainActivity", message)
        }
    }
}
