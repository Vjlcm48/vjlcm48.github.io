package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.sumamente.R
import com.example.sumamente.ui.utils.MusicManager

class ApexSupremusActivity : BaseActivity()  {


    private lateinit var btnClose: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var ivApexTrophy: ImageView
    private var isFinishingByBack = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apex_supremus)

        initViews()

        setupButtons()
        animateTrophy()
    }

    private fun initViews() {
        btnClose = findViewById(R.id.btn_close)
        btnBack = findViewById(R.id.btn_back)
        ivApexTrophy = findViewById(R.id.iv_apex_trophy)
    }



    private fun setupButtons() {

        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                TrofeosActivity.finishTrofeosActivity()

                val intent = Intent(this, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)

                finish()
            }
        }

        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                finish()
            }
        }
    }

    private fun animateTrophy() {

        val fadeIn = ObjectAnimator.ofFloat(ivApexTrophy, "alpha", 0f, 1f).apply {
            duration = 1500
        }

        val scaleUpX = ObjectAnimator.ofFloat(ivApexTrophy, "scaleX", 0.5f, 1.0f).apply {
            duration = 1500
        }

        val scaleUpY = ObjectAnimator.ofFloat(ivApexTrophy, "scaleY", 0.5f, 1.0f).apply {
            duration = 1500
        }

        val appearanceAnimatorSet = AnimatorSet().apply {
            playTogether(fadeIn, scaleUpX, scaleUpY)
        }

        val pulseX = ObjectAnimator.ofFloat(ivApexTrophy, "scaleX", 1.0f, 1.03f, 1.0f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
        }

        val pulseY = ObjectAnimator.ofFloat(ivApexTrophy, "scaleY", 1.0f, 1.03f, 1.0f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
        }

        val pulseAnimatorSet = AnimatorSet().apply {
            playTogether(pulseX, pulseY)
        }

        val finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.play(appearanceAnimatorSet).before(pulseAnimatorSet)
        finalAnimatorSet.start()
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }

    override fun onStart() {
        super.onStart()
        val trofeosSigueViva = TrofeosActivity.instanceRef?.get() != null
        val context = TrofeosActivity.instanceRef?.get()
        val sonidoActivo = context?.let {
            val prefs = it.getSharedPreferences("MyPrefs", MODE_PRIVATE)
            prefs.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        } ?: true

        if (trofeosSigueViva && sonidoActivo) {
            MusicManager.resume()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishingByBack) {
            MusicManager.pause()
        }
        isFinishingByBack = false
    }


}

