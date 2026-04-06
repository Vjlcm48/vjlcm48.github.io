package com.heptacreation.sumamente.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.Calendar
import java.util.TimeZone

object AdManager {

    private const val BANNER_AD_UNIT_ID     = "ca-app-pub-1889227735632244/5877495907"
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1889227735632244/6314173275"
    private const val REWARDED_AD_UNIT_ID   = "ca-app-pub-1889227735632244/6258332958"

    private const val PREFS = "MyPrefs"

    // Claves para el conteo de rankings/condecoraciones
    private const val KEY_RANKING_COUNT     = "ad_ranking_daily_count"
    private const val KEY_RANKING_DATE      = "ad_ranking_last_date"

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isInterstitialLoading = false

    // =====================================================================
    // INICIALIZACIÓN
    // =====================================================================
    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }

    // =====================================================================
    // BANNER
    // =====================================================================
    fun loadBanner(context: Context, adView: AdView) {
        if (isPremium(context)) {
            adView.visibility = View.GONE
            Log.d("AdManager", "Banner ocultado - Usuario Premium")
            return
        }

        adView.adUnitId = BANNER_AD_UNIT_ID
        adView.visibility = View.VISIBLE
        adView.loadAd(AdRequest.Builder().build())
        Log.d("AdManager", "Banner cargado")
    }

    // =====================================================================
    // INTERSTITIAL — precarga
    // =====================================================================
    fun preloadInterstitial(context: Context) {
        if (isPremium(context) || isInterstitialLoading || interstitialAd != null) return

        isInterstitialLoading = true
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d("AdManager", "Interstitial precargado")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w("AdManager", "Interstitial falló al cargar: ${error.message}")
                }
            }
        )
    }

    // =====================================================================
    // INTERSTITIAL — al INICIAR nivel (cada 4 niveles)
    // Muestra si level % 4 == 0
    // =====================================================================
    fun showInterstitialOnLevelStart(
        activity: Activity,
        level: Int,
        onDismissed: () -> Unit
    ) {
        if (isPremium(activity)) { onDismissed(); return }
        if (level % 4 != 0) { onDismissed(); return }

        showInterstitial(activity, onDismissed)
    }

    // =====================================================================
    // INTERSTITIAL — al TERMINAR nivel (cada 5 niveles)
    // Muestra si level % 5 == 0, EXCEPTO si también es múltiplo de 4
    // (en ese caso ya se mostró al iniciar)
    // =====================================================================
    fun showInterstitialOnLevelEnd(
        activity: Activity,
        level: Int,
        onDismissed: () -> Unit
    ) {
        if (isPremium(activity)) { onDismissed(); return }
        if (level % 5 != 0) { onDismissed(); return }
        // Si coincide con inicio (múltiplo de 4 también), no mostrar al terminar
        if (level % 4 == 0) { onDismissed(); return }

        showInterstitial(activity, onDismissed)
    }

    // =====================================================================
    // INTERSTITIAL — rankings y condecoraciones (cada 3 ingresos por día)
    // El conteo se reinicia a las 00:00 Chicago
    // =====================================================================
    fun showInterstitialOnRankingOrTrophyEnter(
        activity: Activity,
        onDismissed: () -> Unit
    ) {
        if (isPremium(activity)) { onDismissed(); return }

        val prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val todayStr = getChicagoDateString()
        val lastDate = prefs.getString(KEY_RANKING_DATE, "")
        val count = if (lastDate == todayStr) prefs.getInt(KEY_RANKING_COUNT, 0) else 0

        val newCount = count + 1
        prefs.edit()
            .putString(KEY_RANKING_DATE, todayStr)
            .putInt(KEY_RANKING_COUNT, newCount)
            .apply()

        Log.d("AdManager", "Ingreso ranking/condecoraciones: $newCount hoy")

        if (newCount % 3 != 0) { onDismissed(); return }

        showInterstitial(activity, onDismissed)
    }

    // =====================================================================
    // REWARDED — para ganar monedas
    // =====================================================================
    fun loadRewarded(context: Context) {
        if (rewardedAd != null) return

        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d("AdManager", "Rewarded cargado")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    Log.w("AdManager", "Rewarded falló al cargar: ${error.message}")
                }
            }
        )
    }

    fun showRewarded(
        activity: Activity,
        onRewarded: (coins: Int) -> Unit,
        onNotAvailable: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad == null) {
            onNotAvailable()
            loadRewarded(activity)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewarded(activity)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                onNotAvailable()
                loadRewarded(activity)
            }
        }

        ad.show(activity) { _ ->
            onRewarded(20)
            Log.d("AdManager", "Rewarded completado — otorgando 20 monedas")
        }
    }

    // =====================================================================
    // HELPERS PRIVADOS
    // =====================================================================
    private fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            onDismissed()
            preloadInterstitial(activity)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onDismissed()
                preloadInterstitial(activity)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                onDismissed()
                preloadInterstitial(activity)
            }
        }

        ad.show(activity)
        Log.d("AdManager", "Interstitial mostrado")
    }

    private fun isPremium(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean("isPremium", false)
    }

    private fun getChicagoDateString(): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
    }
}