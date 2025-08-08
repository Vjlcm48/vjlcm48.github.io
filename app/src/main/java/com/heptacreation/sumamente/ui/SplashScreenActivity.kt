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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private var mediaPlayer: MediaPlayer? = null
    private var hasNavigated = false

    private lateinit var handler: Handler
    private lateinit var showCompanyNameRunnable: Runnable
    private lateinit var hideCompanyNameRunnable: Runnable
    private lateinit var showTextSumamenteRunnable: Runnable
    private lateinit var showAttributionRunnable: Runnable
    private lateinit var showTaglineRunnable: Runnable
    private lateinit var goNextScreenRunnable: Runnable
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_SumaMente)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        startAnimationSequence()

        binding.root.setOnClickListener {
            navigateToNextScreen()
        }
    }

    private fun startAnimationSequence() {
        handler = Handler(Looper.getMainLooper())

        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled) {
            mediaPlayer = MediaPlayer.create(this, R.raw.presentacion)
            mediaPlayer?.start()
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
        goNextScreenRunnable = Runnable {
            navigateToNextScreen()
        }

        handler.postDelayed(showCompanyNameRunnable, 500)
        handler.postDelayed(hideCompanyNameRunnable, 2500)
        handler.postDelayed(showTextSumamenteRunnable, 4000)
        handler.postDelayed(showAttributionRunnable, 4500)
        handler.postDelayed(showTaglineRunnable, 5000)
        handler.postDelayed(goNextScreenRunnable, 7000)
    }

    private fun cancelAllSplashRunnables() {
        if (::handler.isInitialized) {
            handler.removeCallbacks(showCompanyNameRunnable)
            handler.removeCallbacks(hideCompanyNameRunnable)
            handler.removeCallbacks(showTextSumamenteRunnable)
            handler.removeCallbacks(showAttributionRunnable)
            handler.removeCallbacks(showTaglineRunnable)
            handler.removeCallbacks(goNextScreenRunnable)
        }
    }

    private fun navigateToNextScreen() {
        if (hasNavigated) return

        cancelAllSplashRunnables()

        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                val handler = Handler(Looper.getMainLooper())
                val fadeOutDuration = 1000
                val steps = 20
                val stepDuration = fadeOutDuration / steps
                val volumeStep = 1.0f / steps

                var currentStep = 0
                val fadeOutRunnable = object : Runnable {
                    override fun run() {
                        if (currentStep < steps && player.isPlaying) {
                            val newVolume = 1.0f - (volumeStep * currentStep)
                            player.setVolume(newVolume, newVolume)
                            currentStep++
                            handler.postDelayed(this, stepDuration.toLong())
                        } else {
                            player.stop()
                        }
                    }
                }
                fadeOutRunnable.run()
            }
        }

        binding.root.animate().alpha(0f).setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (hasNavigated) return
                    hasNavigated = true

                    val username = sharedPreferences.getString("savedUserName", null)
                    val intent: Intent

                    if (username == null) {

                        intent = Intent(this@SplashScreenActivity, LanguageSelectionActivity::class.java)
                    } else {

                        intent = Intent(this@SplashScreenActivity, TransitionActivity::class.java)

                        intent.putExtra("SOURCE", "SplashScreen")
                    }


                    val options = ActivityOptions.makeCustomAnimation(
                        this@SplashScreenActivity,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                    startActivity(intent, options.toBundle())
                    finish()
                }
            }).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAllSplashRunnables()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
