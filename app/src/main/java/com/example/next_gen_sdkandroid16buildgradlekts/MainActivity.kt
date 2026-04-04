package com.example.next_gen_sdkandroid16buildgradlekts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class MainActivity : AppCompatActivity() {

    private lateinit var appOpenAdManager: AppOpenAdManager
    private lateinit var tvLogs: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AdMob
        AdManager.initialize(this)
        
        // Initialize App Open Ad Manager
        appOpenAdManager = AppOpenAdManager(application)

        // Setup UI
        tvLogs = findViewById(R.id.tv_logs)
        setupAds()
    }

    private fun setupAds() {
        // Banner Ad
        val bannerContainer = findViewById<FrameLayout>(R.id.banner_container)
        bannerContainer.addView(AdManager.createBannerAd(this))
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
        adView.mediaView = adView.findViewById(R.id.ad_media)

        (adView.headlineView as TextView).text = nativeAd.headline
        nativeAd.mediaContent?.let { adView.mediaView?.setMediaContent(it) }

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

        adView.setNativeAd(nativeAd)
        parent.removeAllViews()
        parent.addView(adView)
    }

    private fun logEvent(message: String) {
        val currentLogs = tvLogs.text.toString()
        tvLogs.text = "$message\n$currentLogs"
    }
}
