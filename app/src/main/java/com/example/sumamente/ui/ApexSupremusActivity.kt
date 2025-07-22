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

class ApexSupremusActivity : BaseActivity()  {


    private lateinit var btnClose: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var ivApexTrophy: ImageView


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
                finish()
            }
        }
    }

    private fun animateTrophy() {

        val rotateAnimation = ObjectAnimator.ofFloat(ivApexTrophy, "rotationY", 0f, 360f).apply {
            duration = 3000
            repeatCount = 0
        }

        val scaleXAnimation = ObjectAnimator.ofFloat(ivApexTrophy, "scaleX", 0.8f, 1.1f, 1.0f).apply {
            duration = 1500
        }

        val scaleYAnimation = ObjectAnimator.ofFloat(ivApexTrophy, "scaleY", 0.8f, 1.1f, 1.0f).apply {
            duration = 1500
        }

        AnimatorSet().apply {
            playTogether(rotateAnimation, scaleXAnimation, scaleYAnimation)
            start()
        }
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


}

