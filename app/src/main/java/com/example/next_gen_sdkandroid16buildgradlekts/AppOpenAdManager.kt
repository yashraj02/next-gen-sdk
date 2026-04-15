package com.example.next_gen_sdkandroid16buildgradlekts

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError

class AppOpenAdManager(private val application: Application) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var currentActivity: Activity? = null

    private val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) return

        isLoadingAd = true
        val request = AdRequest.Builder(AD_UNIT_ID).build()
        AppOpenAd.load(request, object : AdLoadCallback<AppOpenAd> {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                isLoadingAd = false
                Log.d("AppOpenAdManager", "App Open Ad loaded")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                isLoadingAd = false
                Log.e("AppOpenAdManager", "App Open Ad failed to load: ${adError.message}")
            }
        })
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }

    override fun onStart(owner: LifecycleOwner) {
        showAdIfAvailable()
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            currentActivity?.let {
                appOpenAd?.show(it)
                appOpenAd = null
                loadAd(it)
            }
        } else {
            currentActivity?.let { loadAd(it) }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }
}
