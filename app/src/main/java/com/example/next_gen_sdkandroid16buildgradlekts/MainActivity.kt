package com.example.next_gen_sdk_android

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
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd
import com.google.android.libraries.ads.mobile.sdk.rewardedinterstitial.RewardedInterstitialAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tvLogs: TextView

    private val backgroundScope = CoroutineScope(Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)

    // -----------------------------------------------------------
    // Ad Unit IDs — replace with your own for production
    // -----------------------------------------------------------
    private val BANNER_AD_UNIT_ID              = "ca-app-pub-3940256099942544/6300978111"
    private val INTERSTITIAL_AD_UNIT_ID        = "ca-app-pub-3940256099942544/1033173712"
    private val REWARDED_AD_UNIT_ID            = "ca-app-pub-3940256099942544/5224354917"
    private val REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379"
    private val NATIVE_AD_UNIT_ID              = "ca-app-pub-3940256099942544/2247696110"

    // Loaded ad instances — null means not yet ready
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var bannerAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLogs = findViewById(R.id.tv_logs)
        setupButtons()

        // Initialize the Next-Gen Mobile Ads SDK before loading any ads.
        // Pass your app's AdMob App ID to InitializationConfig.
        logEvent("Initializing Next-Gen SDK...")
        backgroundScope.launch {
            val initConfig = InitializationConfig.Builder("ca-app-pub-3940256099942544~3347511713").build()
            MobileAds.initialize(this@MainActivity, initConfig) {
                mainScope.launch {
                    logEvent("SDK Initialized — loading ads")
                    loadBannerAd()
                    loadInterstitialAd()
                    loadRewardedAd()
                    loadRewardedInterstitialAd()
                    loadNativeAd()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAdView?.destroy()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_interstitial).setOnClickListener { showInterstitialAd() }
        findViewById<Button>(R.id.btn_rewarded).setOnClickListener { showRewardedAd() }
        findViewById<Button>(R.id.btn_rewarded_interstitial).setOnClickListener { showRewardedInterstitialAd() }

        // Ad Inspector — useful for debugging ad issues during development
        findViewById<Button>(R.id.btn_inspector).setOnClickListener {
            MobileAds.openAdInspector { error ->
                error?.let { logEvent("Inspector error: ${it.message}") }
            }
        }
    }

    // -----------------------------------------------------------
    // BANNER
    // -----------------------------------------------------------

    private fun loadBannerAd() {
        val bannerContainer = findViewById<FrameLayout>(R.id.banner_container)
        val adView = AdView(this).also { bannerAdView = it }
        bannerContainer.removeAllViews()
        bannerContainer.addView(adView)

        val adRequest = BannerAdRequest.Builder(BANNER_AD_UNIT_ID, AdSize.BANNER).build()
        adView.loadAd(adRequest, object : AdLoadCallback<BannerAd> {
            override fun onAdLoaded(ad: BannerAd) {
                logEvent("Banner: Loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                logEvent("Banner: Failed — ${error.message}")
            }
        })
    }

    // -----------------------------------------------------------
    // INTERSTITIAL
    // -----------------------------------------------------------

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder(INTERSTITIAL_AD_UNIT_ID).build()
        InterstitialAd.load(adRequest, object : AdLoadCallback<InterstitialAd> {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                logEvent("Interstitial: Ready")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
                logEvent("Interstitial: Failed — ${error.message}")
            }
        })
    }

    private fun showInterstitialAd() {
        val ad = interstitialAd ?: run {
            // Ad not ready — trigger a fresh load and inform the user
            logEvent("Interstitial: Not ready, loading...")
            loadInterstitialAd()
            return
        }
        ad.show(this)
        interstitialAd = null
        // Reload immediately so the next show() call has an ad ready
        loadInterstitialAd()
    }

    // -----------------------------------------------------------
    // REWARDED
    // -----------------------------------------------------------

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder(REWARDED_AD_UNIT_ID).build()
        RewardedAd.load(adRequest, object : AdLoadCallback<RewardedAd> {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                logEvent("Rewarded: Ready")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedAd = null
                logEvent("Rewarded: Failed — ${error.message}")
            }
        })
    }

    private fun showRewardedAd() {
        val ad = rewardedAd ?: run {
            logEvent("Rewarded: Not ready, loading...")
            loadRewardedAd()
            return
        }
        ad.show(this) { reward ->
            logEvent("Rewarded: Earned ${reward.amount} ${reward.type}")
        }
        rewardedAd = null
        loadRewardedAd()
    }

    // -----------------------------------------------------------
    // REWARDED INTERSTITIAL
    // -----------------------------------------------------------

    private fun loadRewardedInterstitialAd() {
        val adRequest = AdRequest.Builder(REWARDED_INTERSTITIAL_AD_UNIT_ID).build()
        RewardedInterstitialAd.load(adRequest, object : AdLoadCallback<RewardedInterstitialAd> {
            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                rewardedInterstitialAd = ad
                logEvent("Rewarded Interstitial: Ready")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedInterstitialAd = null
                logEvent("Rewarded Interstitial: Failed — ${error.message}")
            }
        })
    }

    private fun showRewardedInterstitialAd() {
        val ad = rewardedInterstitialAd ?: run {
            logEvent("Rewarded Interstitial: Not ready, loading...")
            loadRewardedInterstitialAd()
            return
        }
        ad.show(this) { reward ->
            logEvent("Rewarded Interstitial: Earned ${reward.amount} ${reward.type}")
        }
        rewardedInterstitialAd = null
        loadRewardedInterstitialAd()
    }

    // -----------------------------------------------------------
    // NATIVE
    // -----------------------------------------------------------

    private fun loadNativeAd() {
        val adRequest = NativeAdRequest.Builder(
            NATIVE_AD_UNIT_ID,
            listOf(NativeAd.NativeAdType.NATIVE)
        ).build()

        NativeAdLoader.load(adRequest, object : NativeAdLoaderCallback {
            override fun onNativeAdLoaded(nativeAd: NativeAd) {
                runOnUiThread {
                    populateNativeAdView(nativeAd, findViewById(R.id.native_ad_container))
                    logEvent("Native: Loaded")
                }
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                logEvent("Native: Failed — ${error.message}")
            }
        })
    }

    private fun populateNativeAdView(nativeAd: NativeAd, parent: FrameLayout) {
        val adView = layoutInflater.inflate(R.layout.native_ad_layout, null) as NativeAdView

        adView.findViewById<TextView>(R.id.ad_headline).text = nativeAd.headline

        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
        bodyView.text = nativeAd.body
        adView.bodyView = bodyView

        adView.findViewById<Button>(R.id.ad_call_to_action).text = nativeAd.callToAction

        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        adView.registerNativeAd(nativeAd, mediaView)

        parent.removeAllViews()
        parent.addView(adView)
    }

    // -----------------------------------------------------------
    // LOGGING
    // -----------------------------------------------------------

    private fun logEvent(message: String) {
        mainScope.launch {
            tvLogs.text = "$message\n${tvLogs.text}"
            Log.d("AdsDemo", message)
        }
    }
}