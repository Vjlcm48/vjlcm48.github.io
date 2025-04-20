package com.example.sumamente.ui

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.sumamente.R
import kotlin.math.abs

class Top10Activity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnBack: ImageView
    private lateinit var btnClose: ImageView
    private lateinit var tvTituloTop10: TextView
    private lateinit var btnPrevious: ImageView
    private lateinit var btnNext: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top10)

        initViews()
        setupCondecoraciones()
        setupNavigation()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        btnBack = findViewById(R.id.btn_back)
        btnClose = findViewById(R.id.btn_close)
        tvTituloTop10 = findViewById(R.id.tv_titulo_top10)
        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
    }

    private fun setupCondecoraciones() {
        val condecoraciones = listOf(
            Top10Condecoracion(
                "ic_estrella_honorabilis",
                getString(R.string.condecoracion_honorabilis),
                10
            ),
            Top10Condecoracion(
                "ic_estrella_virtuosus",
                getString(R.string.condecoracion_virtuosus),
                9
            ),
            Top10Condecoracion(
                "ic_estrella_insignis",
                getString(R.string.condecoracion_insignis),
                8
            ),
            Top10Condecoracion(
                "ic_estrella_praestans",
                getString(R.string.condecoracion_praestans),
                7
            ),
            Top10Condecoracion(
                "ic_estrella_illustris",
                getString(R.string.condecoracion_illustris),
                6
            ),
            Top10Condecoracion(
                "ic_antorcha_gloriosus",
                getString(R.string.condecoracion_gloriosus),
                5
            ),
            Top10Condecoracion(
                "ic_antorcha_venerabilis",
                getString(R.string.condecoracion_venerabilis),
                4
            ),
            Top10Condecoracion(
                "ic_antorcha_magnanimous",
                getString(R.string.condecoracion_magnanimous),
                3
            ),
            Top10Condecoracion(
                "ic_corona_summum",
                getString(R.string.condecoracion_summum),
                2
            ),
            Top10Condecoracion(
                "ic_corona_excelsitur",
                getString(R.string.condecoracion_excelsitur),
                1
            )
        )

        val adapter = Top10PagerAdapter(this, condecoraciones)
        viewPager.adapter = adapter
        viewPager.setPageTransformer(createPageTransformer())

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateNavigationButtons(position, condecoraciones.size)
            }
        })

        updateNavigationButtons(0, condecoraciones.size)
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

    private fun createPageTransformer(): ViewPager2.PageTransformer {
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
