package com.example.next_gen_sdkandroid16buildgradlekts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView

class MainActivity : AppCompatActivity() {

    private lateinit var appOpenAdManager: AppOpenAdManager
    private lateinit var tvLogs: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLogs = findViewById(R.id.tv_logs)

        // Initialize AdMob
        AdManager.initialize(this) {
            findViewById<TextView>(R.id.tv_sdk_status).apply {
                text = "Using Next Gen GMA SDK"
                setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
            }
            tvLogs.text = "GMA Next-Gen SDK Initialized"
            
            // Initialize App Open Ad Manager
            appOpenAdManager = AppOpenAdManager(application)
            
            setupAds()
        }
    }

    private fun setupAds() {
        // Banner Ad
        val bannerContainer = findViewById<FrameLayout>(R.id.banner_container)
        val bannerAdView = AdManager.createBannerAd(this, object : AdLoadCallback<BannerAd> {
            override fun onAdLoaded(ad: BannerAd) {
                logEvent("Banner: Loaded")
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                logEvent("Banner: Failed to load - ${adError.message}")
            }
        })
        bannerContainer.addView(bannerAdView)
        logEvent("Banner: Loading...")

        // Interstitial Ad
        AdManager.loadInterstitial(this)
        findViewById<Button>(R.id.btn_interstitial).setOnClickListener {
            AdManager.showInterstitial(this)
            AdManager.loadInterstitial(this) // Preload next
        }

        // Rewarded Ad
        AdManager.loadRewarded(this)
        findViewById<Button>(R.id.btn_rewarded).setOnClickListener {
            AdManager.showRewarded(this) { amount ->
                logEvent("Rewarded: Earned $amount coins!")
                AdManager.loadRewarded(this) // Preload next
            }
        }

        // Rewarded Interstitial Ad
        AdManager.loadRewardedInterstitial(this)
        findViewById<Button>(R.id.btn_rewarded_interstitial).setOnClickListener {
            AdManager.showRewardedInterstitial(this) { amount ->
                logEvent("Rewarded Interstitial: Earned $amount coins!")
                AdManager.loadRewardedInterstitial(this) // Preload next
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
        
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        (adView.headlineView as TextView).text = nativeAd.headline
        nativeAd.mediaContent?.let { content: com.google.android.libraries.ads.mobile.sdk.nativead.MediaContent ->
            mediaView.mediaContent = content
        }

        if (nativeAd.body == null) {
            adView.bodyView?.visibility = android.view.View.INVISIBLE
        } else {
            adView.bodyView?.visibility = android.view.View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = android.view.View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = android.view.View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = android.view.View.GONE
        } else {
            (adView.iconView as android.widget.ImageView).setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = android.view.View.VISIBLE
        }

        // Use registerNativeAd in Next-Gen
        adView.registerNativeAd(nativeAd, mediaView)
        parent.removeAllViews()
        parent.addView(adView)
    }

    private fun logEvent(message: String) {
        runOnUiThread {
            val currentLogs = tvLogs.text.toString()
            tvLogs.text = "$message\n$currentLogs"
        }
    }
}
