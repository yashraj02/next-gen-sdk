package com.example.next_gen_sdkandroid16buildgradlekts

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView

class MainActivity : AppCompatActivity() {

    private lateinit var appOpenAdManager: AppOpenAdManager
    private lateinit var tvLogs: TextView
    private lateinit var tvSdkStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLogs = findViewById(R.id.tv_logs)
        tvSdkStatus = findViewById(R.id.tv_sdk_status)

        // Initialize AdMob
        AdManager.initialize(this) {
            tvSdkStatus.text = "Using Next Gen GMA SDK"
            tvSdkStatus.setBackgroundColor(Color.parseColor("#4CAF50"))
            logEvent("GMA Next-Gen SDK Initialized")
            
            // Initialize App Open Ad Manager after SDK is ready
            appOpenAdManager = AppOpenAdManager(application)
            
            // Setup UI and Ads
            setupAds()
        }
    }

    private fun setupAds() {
        // Banner Ad
        val bannerContainer = findViewById<FrameLayout>(R.id.banner_container)
        bannerContainer.addView(AdManager.createBannerAd(this) {
            logEvent("Banner: Loaded")
        })
        logEvent("Banner: Loading...")

        // Interstitial Ad
        AdManager.loadInterstitial()
        findViewById<Button>(R.id.btn_interstitial).setOnClickListener {
            AdManager.showInterstitial(this)
            AdManager.loadInterstitial() // Preload next
        }

        // Rewarded Ad
        AdManager.loadRewarded()
        findViewById<Button>(R.id.btn_rewarded).setOnClickListener {
            AdManager.showRewarded(this) { amount ->
                logEvent("Rewarded: Earned $amount coins!")
                AdManager.loadRewarded() // Preload next
            }
        }

        // Rewarded Interstitial Ad
        AdManager.loadRewardedInterstitial()
        findViewById<Button>(R.id.btn_rewarded_interstitial).setOnClickListener {
            AdManager.showRewardedInterstitial(this) { amount ->
                logEvent("Rewarded Interstitial: Earned $amount coins!")
                AdManager.loadRewardedInterstitial() // Preload next
            }
        }

        // Native Ad
        AdManager.loadNativeAd(this) { nativeAd ->
            populateNativeAdView(nativeAd, findViewById(R.id.native_ad_container))
            logEvent("Native Ad: Loaded")
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, parent: FrameLayout) {
        val adView = layoutInflater.inflate(R.layout.native_ad_layout, null) as NativeAdView
        
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
        val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
        val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
        val mediaView = adView.findViewById<com.google.android.libraries.ads.mobile.sdk.nativead.MediaView>(R.id.ad_media)

        headlineView.text = nativeAd.headline

        if (nativeAd.body == null) {
            bodyView.visibility = android.view.View.INVISIBLE
        } else {
            bodyView.visibility = android.view.View.VISIBLE
            bodyView.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            callToActionView.visibility = android.view.View.INVISIBLE
        } else {
            callToActionView.visibility = android.view.View.VISIBLE
            callToActionView.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            iconView.visibility = android.view.View.GONE
        } else {
            iconView.setImageDrawable(nativeAd.icon?.drawable)
            iconView.visibility = android.view.View.VISIBLE
        }

        // Mandatory registration for Next-Gen SDK
        adView.registerNativeAd(nativeAd, mediaView)
        
        parent.removeAllViews()
        parent.addView(adView)
    }

    private fun logEvent(message: String) {
        val currentLogs = tvLogs.text.toString()
        tvLogs.text = "$message\n$currentLogs"
    }
}
