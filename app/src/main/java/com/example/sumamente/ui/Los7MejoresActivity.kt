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

class Los7MejoresActivity : BaseActivity()  {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnBack: ImageView
    private lateinit var btnClose: ImageView
    private lateinit var tvTituloLos7Mejores: TextView
    private lateinit var btnPrevious: ImageView
    private lateinit var btnNext: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_los7mejores)

        initViews()
        setupCondecoraciones()
        setupNavigation()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        btnBack = findViewById(R.id.btn_back)
        btnClose = findViewById(R.id.btn_close)
        tvTituloLos7Mejores = findViewById(R.id.tv_titulo_los7mejores)
        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
    }

    private fun setupCondecoraciones() {
        val condecoraciones = listOf(
            Los7MejoresCondecoracion(
                "ic_discipulus_optimus_7",
                getString(R.string.condecoracion_discipulus_optimus_7),
                getString(R.string.significado_discipulus_optimus_7),
                7
            ),
            Los7MejoresCondecoracion(
                "ic_intellectus_primus_6",
                getString(R.string.condecoracion_intellectus_primus_6),
                getString(R.string.significado_intellectus_primus_6),
                6
            ),
            Los7MejoresCondecoracion(
                "ic_consilium_magnus_5",
                getString(R.string.condecoracion_consilium_magnus_5),
                getString(R.string.significado_consilium_magnus_5),
                5
            ),
            Los7MejoresCondecoracion(
                "ic_doctrinae_princeps_4",
                getString(R.string.condecoracion_doctrinae_princeps_4),
                getString(R.string.significado_doctrinae_princeps_4),
                4
            ),
            Los7MejoresCondecoracion(
                "ic_luminis_rex_3",
                getString(R.string.condecoracion_luminis_rex_3),
                getString(R.string.significado_luminis_rex_3),
                3
            ),
            Los7MejoresCondecoracion(
                "ic_mentis_aurea_2",
                getString(R.string.condecoracion_mentis_aurea_2),
                getString(R.string.significado_mentis_aurea_2),
                2
            ),
            Los7MejoresCondecoracion(
                "ic_sapiens_supremus_1",
                getString(R.string.condecoracion_sapiens_supremus_1),
                getString(R.string.significado_sapiens_supremus_1),
                1
            )
        )

        val adapter = Los7MejoresPagerAdapter(this, condecoraciones)
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
