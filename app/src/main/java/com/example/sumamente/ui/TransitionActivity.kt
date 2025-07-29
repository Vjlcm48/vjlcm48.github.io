package com.example.sumamente.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.content.edit
import com.example.sumamente.R
import kotlin.random.Random

class TransitionActivity : BaseActivity() {

    private lateinit var innerCircleView: View
    private lateinit var outerCircleView: View
    private lateinit var loadingView: LoadingView
    private lateinit var loadingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)

        clearAppDataOnLaunch()

        innerCircleView = findViewById(R.id.innerCircleView)
        outerCircleView = findViewById(R.id.outerCircleView)
        loadingView = findViewById(R.id.loadingView)
        loadingTextView = findViewById(R.id.loadingTextView)


        startAnimationSequence()
    }

    private fun startAnimationSequence() {

        setupRandomLoadingText()


        val innerCircleZoom = createZoomInAnimation(innerCircleView, 500, OvershootInterpolator())
        val outerCircleDraw = createZoomInAnimation(outerCircleView, 700, AccelerateDecelerateInterpolator())
        val textFadeIn = ObjectAnimator.ofFloat(loadingTextView, "alpha", 0f, 1f).apply {
            duration = 800
        }
        val loadingViewFadeIn = ObjectAnimator.ofFloat(loadingView, "alpha", 0f, 1f).apply {
            duration = 600
        }

        val breathingAnim = createBreathingAnimation(loadingView)

        val sequence = AnimatorSet()
        sequence.play(innerCircleZoom)
        sequence.play(outerCircleDraw).after(200)
        sequence.play(textFadeIn).after(700)
        sequence.play(loadingViewFadeIn).after(700)

        sequence.addListener(onEnd = {

            breathingAnim.start()

        })

        sequence.start()

        android.os.Handler(mainLooper).postDelayed({
            transicionPantalla()
        }, 5000)
    }

    private fun setupRandomLoadingText() {
        val loadingTexts = listOf(
            R.string.loading_text_1, R.string.loading_text_2, R.string.loading_text_3,
            R.string.loading_text_4, R.string.loading_text_5, R.string.loading_text_6,
            R.string.loading_text_7, R.string.loading_text_8, R.string.loading_text_9
        )
        val randomIndex = Random.nextInt(loadingTexts.size)
        loadingTextView.setText(loadingTexts[randomIndex])
    }

    private fun createZoomInAnimation(view: View, duration: Long, interpolator: android.view.animation.Interpolator): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        return AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            this.duration = duration
            this.interpolator = interpolator
        }
    }

    private fun createBreathingAnimation(view: View): ValueAnimator {
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            android.animation.PropertyValuesHolder.ofFloat("scaleX", 1f, 1.05f),
            android.animation.PropertyValuesHolder.ofFloat("scaleY", 1f, 1.05f)
        ).apply {
            duration = 1500
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
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
