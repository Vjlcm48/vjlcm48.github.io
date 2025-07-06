package com.example.sumamente.ui

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.sumamente.R
import kotlin.math.abs

class PinesActivity : BaseActivity()  {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnBack: ImageView
    private lateinit var btnClose: ImageView
    private lateinit var tvTituloPines: TextView
    private lateinit var btnPrevious: ImageView
    private lateinit var btnNext: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pines)

        initViews()
        setupPines()
        setupNavigation()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        btnBack = findViewById(R.id.btn_back)
        btnClose = findViewById(R.id.btn_close)
        tvTituloPines = findViewById(R.id.tv_titulo_pines)
        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
    }

    private fun setupPines() {
        val pines = listOf(
            Pin("ic_pin_victoris", getString(R.string.pin_victoris)),
            Pin("ic_pin_optimum", getString(R.string.pin_optimum)),
            Pin("ic_pin_invictus", getString(R.string.pin_invictus))
        )

        val adapter = PinPagerAdapter(this, pines)
        viewPager.adapter = adapter
        viewPager.setPageTransformer(createPinPageTransformer())


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateNavigationButtons(position, pines.size)
            }
        })


        updateNavigationButtons(0, pines.size)
    }

    private fun setupNavigation() {
        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                finish()
            }
        }

        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, MainGameActivity::class.java)
                startActivity(intent)
                finish()

                TrofeosActivity.finishTrofeosActivity()
            }
        }


        btnPrevious.setOnClickListener {
            applyBounceEffect(it) {
                val currentItem = viewPager.currentItem
                if (currentItem > 0) {
                    viewPager.currentItem = currentItem - 1
                }
            }
        }

        btnNext.setOnClickListener {
            applyBounceEffect(it) {
                val currentItem = viewPager.currentItem
                val count = viewPager.adapter?.itemCount ?: 0
                if (currentItem < count - 1) {
                    viewPager.currentItem = currentItem + 1
                }
            }
        }
    }

    private fun updateNavigationButtons(position: Int, totalItems: Int) {
        btnPrevious.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        btnNext.visibility = if (position < totalItems - 1) View.VISIBLE else View.INVISIBLE
    }

    private fun createPinPageTransformer(): ViewPager2.PageTransformer {
        return ViewPager2.PageTransformer { page, position ->
            when {
                position < -1 || position > 1 -> {

                    page.alpha = 0f
                }
                position == 0f -> {

                    page.alpha = 1f
                    page.translationX = 0f
                    page.scaleX = 1f
                    page.scaleY = 1f
                }
                else -> {

                    page.alpha = 1f - abs(position)
                    page.translationX = -position * (page.width / 2)
                    val scaleFactor = 0.85f.coerceAtLeast(1 - abs(position * 0.15f))
                    page.scaleX = scaleFactor
                    page.scaleY = scaleFactor
                }
            }
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
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }
}
