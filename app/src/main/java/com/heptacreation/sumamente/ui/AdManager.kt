package com.heptacreation.sumamente.ui

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

object AdManager {

    private const val BANNER_AD_UNIT_ID = "ca-app-pub-1889227735632244/6798507634"

    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }

    fun loadBanner(context: Context, adView: AdView) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isPremium = sharedPreferences.getBoolean("isPremium", false)

        if (isPremium) {
            adView.visibility = View.GONE
            Log.d("AdManager", "Banner ocultado - Usuario Premium")
            return
        }

        adView.adUnitId = BANNER_AD_UNIT_ID
        adView.visibility = View.VISIBLE
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        Log.d("AdManager", "Banner cargado - Usuario No Premium")
    }
}
