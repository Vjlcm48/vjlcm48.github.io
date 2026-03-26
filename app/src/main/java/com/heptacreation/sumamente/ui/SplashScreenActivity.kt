package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.databinding.ActivitySplashScreenBinding
import com.heptacreation.sumamente.ui.utils.AppSecurityManager

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var mediaPlayer: MediaPlayer? = null
    private var playerReleased = false
    private var hasNavigated = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var showCompanyNameRunnable: Runnable
    private lateinit var hideCompanyNameRunnable: Runnable
    private lateinit var showTextSumamenteRunnable: Runnable
    private lateinit var showAttributionRunnable: Runnable
    private lateinit var showTaglineRunnable: Runnable
    private lateinit var goNextScreenRunnable: Runnable
    private var fadeOutRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_SumaMente)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        startAnimationSequence()

        binding.root.setOnClickListener { navigateToNextScreen() }
    }

    private fun startAnimationSequence() {
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled) {
            mediaPlayer = MediaPlayer.create(this, R.raw.presentacion)
            try {
                mediaPlayer?.start()
            } catch (_: Throwable) {
                safeReleasePlayer()
            }
        } else {
            mediaPlayer = null
        }

        showCompanyNameRunnable = Runnable {
            binding.companyNameInitial.animate().alpha(1f).setDuration(2000).start()
        }

        hideCompanyNameRunnable = Runnable {
            binding.companyNameInitial.animate().alpha(0f).setDuration(1500).start()
            binding.logoSumamente.animate().alpha(1f).setDuration(1500).start()
        }

        showTextSumamenteRunnable = Runnable {
            binding.textSumamente.animate().alpha(1f).setDuration(1000).start()
        }

        showAttributionRunnable = Runnable {
            binding.attributionLayout.animate().alpha(1f).setDuration(1000).start()
        }

        showTaglineRunnable = Runnable {
            val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_progress_button)
            binding.logoSumamente.startAnimation(pulseAnimation)
            binding.techTagline.animate().alpha(1f).setDuration(1000).start()
        }

        goNextScreenRunnable = Runnable { navigateToNextScreen() }

        handler.postDelayed(showCompanyNameRunnable, 500)
        handler.postDelayed(hideCompanyNameRunnable, 2500)
        handler.postDelayed(showTextSumamenteRunnable, 4000)
        handler.postDelayed(showAttributionRunnable, 4500)
        handler.postDelayed(showTaglineRunnable, 5000)
        handler.postDelayed(goNextScreenRunnable, 7000)
    }

    private fun cancelAllSplashRunnables() {
        fun remove(r: Runnable?) {
            if (r != null) handler.removeCallbacks(r)
        }

        if (::showCompanyNameRunnable.isInitialized) remove(showCompanyNameRunnable)
        if (::hideCompanyNameRunnable.isInitialized) remove(hideCompanyNameRunnable)
        if (::showTextSumamenteRunnable.isInitialized) remove(showTextSumamenteRunnable)
        if (::showAttributionRunnable.isInitialized) remove(showAttributionRunnable)
        if (::showTaglineRunnable.isInitialized) remove(showTaglineRunnable)
        if (::goNextScreenRunnable.isInitialized) remove(goNextScreenRunnable)
        remove(fadeOutRunnable)
    }

    private fun safeReleasePlayer() {
        val mp = mediaPlayer ?: run {
            playerReleased = true
            return
        }
        try {
            mp.stop()
        } catch (_: IllegalStateException) {
        } catch (_: Throwable) {
        }
        try {
            mp.release()
        } catch (_: Throwable) {
        }
        mediaPlayer = null
        playerReleased = true
    }

    private fun navigateToNextScreen() {
        if (hasNavigated) return

        cancelAllSplashRunnables()

        val mp = mediaPlayer
        if (mp == null || playerReleased) {
            proceedToNextScreen()
            return
        }

        val totalDuration = 1000
        val steps = 20
        val stepDuration = (totalDuration / steps).toLong()
        var currentStep = 0

        fadeOutRunnable = object : Runnable {
            override fun run() {
                if (playerReleased) {
                    proceedToNextScreen()
                    return
                }
                val now = mediaPlayer
                if (now == null) {
                    proceedToNextScreen()
                    return
                }
                try {
                    if (currentStep < steps) {
                        val newVol = 1f - (currentStep / steps.toFloat())
                        now.setVolume(newVol, newVol)
                        currentStep++
                        handler.postDelayed(this, stepDuration)
                    } else {
                        safeReleasePlayer()
                        proceedToNextScreen()
                    }
                } catch (_: IllegalStateException) {
                    safeReleasePlayer()
                    proceedToNextScreen()
                } catch (_: Throwable) {
                    safeReleasePlayer()
                    proceedToNextScreen()
                }
            }
        }
        handler.post(fadeOutRunnable!!)
    }

    @SuppressLint("HardwareIds")
    private fun proceedToNextScreen() {
        if (hasNavigated) return

        binding.root.animate()
            .alpha(0f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (hasNavigated) return
                    hasNavigated = true

                    AppSecurityManager.verifyActiveDeviceIfNeeded(this@SplashScreenActivity) { authorized ->

                        if (!authorized) {
                            // Ya fue expulsado internamente (logout + limpieza + redirección)
                            return@verifyActiveDeviceIfNeeded
                        }

                        val shouldContinueAsExistingUser =
                            AppSecurityManager.shouldContinueAsExistingUser(this@SplashScreenActivity)

                        val intent = if (shouldContinueAsExistingUser) {
                            Intent(this@SplashScreenActivity, TransitionActivity::class.java)
                                .putExtra("SOURCE", "SplashScreen")
                        } else {
                            Intent(this@SplashScreenActivity, LanguageSelectionActivity::class.java)
                        }

                        val options = ActivityOptions.makeCustomAnimation(
                            this@SplashScreenActivity,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                        startActivity(intent, options.toBundle())
                        finish()
                    }
                }
            })
            .start()
    }

    override fun onPause() {
        super.onPause()

        if (isFinishing) {
            cancelAllSplashRunnables()
            safeReleasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAllSplashRunnables()
        safeReleasePlayer()
    }
}