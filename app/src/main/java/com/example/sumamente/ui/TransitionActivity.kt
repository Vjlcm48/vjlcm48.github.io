package com.example.sumamente.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import com.example.sumamente.R

class TransitionActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)

        clearAppDataOnLaunch()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            transicionPantalla()
        }, 5000)
    }

    private fun transicionPantalla() {

        val source = intent.getStringExtra("SOURCE")

        val targetActivity = if (source == "SplashScreen") {

            MainGameActivity::class.java
        } else {

            NotificationsActivity::class.java
        }

        val intent = Intent(this, targetActivity)
        val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun clearAppDataOnLaunch() {
        val myPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        myPrefs.edit { clear() }

        val scorePrefs = getSharedPreferences("ScorePrefs", MODE_PRIVATE)
        scorePrefs.edit { clear() }

        val scorePrefsPrincipiante = getSharedPreferences("ScorePrefsPrincipiante", MODE_PRIVATE)
        scorePrefsPrincipiante.edit { clear() }

        val scorePrefsPro = getSharedPreferences("ScorePrefsPro", MODE_PRIVATE)
        scorePrefsPro.edit { clear() }

        val myPrefsDeciPlus = getSharedPreferences("MyPrefsDeciPlus", MODE_PRIVATE)
        myPrefsDeciPlus.edit { clear() }

        val scorePrefsDeciPlus = getSharedPreferences("ScorePrefsDeciPlus", MODE_PRIVATE)
        scorePrefsDeciPlus.edit { clear() }

        val scorePrefsDeciPlusPrincipiante = getSharedPreferences("ScorePrefsDeciPlusPrincipiante", MODE_PRIVATE)
        scorePrefsDeciPlusPrincipiante.edit { clear() }

        val scorePrefsDeciPlusPro = getSharedPreferences("ScorePrefsDeciPlusPro", MODE_PRIVATE)
        scorePrefsDeciPlusPro.edit { clear() }

        val myPrefsRomas = getSharedPreferences("MyPrefsRomas", MODE_PRIVATE)
        myPrefsRomas.edit { clear() }

        val scorePrefsRomas = getSharedPreferences("ScorePrefsRomas", MODE_PRIVATE)
        scorePrefsRomas.edit { clear() }

        val scorePrefsRomasPrincipiante = getSharedPreferences("ScorePrefsRomasPrincipiante", MODE_PRIVATE)
        scorePrefsRomasPrincipiante.edit { clear() }

        val scorePrefsRomasPro = getSharedPreferences("ScorePrefsRomasPro", MODE_PRIVATE)
        scorePrefsRomasPro.edit { clear() }

        val myPrefsAlfaNumeros = getSharedPreferences("MyPrefsAlfaNumeros", MODE_PRIVATE)
        myPrefsAlfaNumeros.edit { clear() }

        val scorePrefsAlfaNumeros = getSharedPreferences("ScorePrefsAlfaNumeros", MODE_PRIVATE)
        scorePrefsAlfaNumeros.edit { clear() }

        val scorePrefsAlfaNumerosPrincipiante = getSharedPreferences("ScorePrefsAlfaNumerosPrincipiante", MODE_PRIVATE)
        scorePrefsAlfaNumerosPrincipiante.edit { clear() }

        val scorePrefsAlfaNumerosPro = getSharedPreferences("ScorePrefsAlfaNumerosPro", MODE_PRIVATE)
        scorePrefsAlfaNumerosPro.edit { clear() }

        val myPrefsSumaResta = getSharedPreferences("MyPrefsSumaResta", MODE_PRIVATE)
        myPrefsSumaResta.edit { clear() }

        val scorePrefsSumaResta = getSharedPreferences("ScorePrefsSumaResta", MODE_PRIVATE)
        scorePrefsSumaResta.edit { clear() }

        val scorePrefsSumaRestaPrincipiante = getSharedPreferences("ScorePrefsSumaRestaPrincipiante", MODE_PRIVATE)
        scorePrefsSumaRestaPrincipiante.edit { clear() }

        val scorePrefsSumaRestaPro = getSharedPreferences("ScorePrefsSumaRestaPro", MODE_PRIVATE)
        scorePrefsSumaRestaPro.edit { clear() }

        val myPrefsMasPlus = getSharedPreferences("MyPrefsMasPlus", MODE_PRIVATE)
        myPrefsMasPlus.edit { clear() }

        val scorePrefsMasPlus = getSharedPreferences("ScorePrefsMasPlus", MODE_PRIVATE)
        scorePrefsMasPlus.edit { clear() }

        val scorePrefsMasPlusPrincipiante = getSharedPreferences("ScorePrefsMasPlusPrincipiante", MODE_PRIVATE)
        scorePrefsMasPlusPrincipiante.edit { clear() }

        val scorePrefsMasPlusPro = getSharedPreferences("ScorePrefsMasPlusPro", MODE_PRIVATE)
        scorePrefsMasPlusPro.edit { clear() }

        val myPrefsGenioPlus = getSharedPreferences("MyPrefsGenioPlus", MODE_PRIVATE)
        myPrefsGenioPlus.edit { clear() }

        val scorePrefsGenioPlus = getSharedPreferences("ScorePrefsGenioPlus", MODE_PRIVATE)
        scorePrefsGenioPlus.edit { clear() }

        val scorePrefsGenioPlusPrincipiante = getSharedPreferences("ScorePrefsGenioPlusPrincipiante", MODE_PRIVATE)
        scorePrefsGenioPlusPrincipiante.edit { clear() }

        val scorePrefsGenioPlusPro = getSharedPreferences("ScorePrefsGenioPlusPro", MODE_PRIVATE)
        scorePrefsGenioPlusPro.edit { clear() }

        ScoreManager.resetStatsAndTimes()
    }
}
