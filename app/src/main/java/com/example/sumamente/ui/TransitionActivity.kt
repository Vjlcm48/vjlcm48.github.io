package com.example.sumamente.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.animation.addListener
import com.example.sumamente.R
import kotlin.random.Random

class TransitionActivity : BaseActivity() {

    private lateinit var innerCircleView: View
    private lateinit var outerCircleView: View
    private lateinit var loadingView: LoadingView
    private lateinit var loadingTextView: TextView

    private var hasNavigated = false
    private lateinit var handler: Handler
    private lateinit var transitionRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)

        innerCircleView = findViewById(R.id.innerCircleView)
        outerCircleView = findViewById(R.id.outerCircleView)
        loadingView = findViewById(R.id.loadingView)
        loadingTextView = findViewById(R.id.loadingTextView)

        findViewById<View>(R.id.rootView)?.setOnClickListener {
            skipTransition()
        }

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

        handler = Handler(Looper.getMainLooper())
        transitionRunnable = Runnable {
            transicionPantalla()
        }
        handler.postDelayed(transitionRunnable, 5000)
    }

    private fun skipTransition() {
        if (hasNavigated) return
        hasNavigated = true

        if (::handler.isInitialized) handler.removeCallbacks(transitionRunnable)

        findViewById<View>(R.id.rootView)?.animate()
            ?.alpha(0f)
            ?.setDuration(1000)
            ?.withEndAction {
                navigateToNextScreen()
            }
            ?.start()
    }

    private fun navigateToNextScreen() {

        val source = intent.getStringExtra("SOURCE")
        val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val hasUsername = sharedPrefs.getString("savedUserName", null) != null

        val targetActivity = when (source) {
            "SplashScreen" -> MainGameActivity::class.java
            "LanguageSelection" -> {
                if (hasUsername) MainGameActivity::class.java
                else NotificationsActivity::class.java
            }
            else -> NotificationsActivity::class.java
        }

        val intent = Intent(this, targetActivity)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
        finish()
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
        if (hasNavigated) return
        hasNavigated = true

        findViewById<View>(R.id.rootView)?.animate()
            ?.alpha(0f)
            ?.setDuration(1000)
            ?.withEndAction {
                navigateToNextScreen()
            }
            ?.start()
    }

}
