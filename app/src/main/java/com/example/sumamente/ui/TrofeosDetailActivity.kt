package com.example.sumamente.ui

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.sumamente.R
import kotlin.math.abs

class TrofeosDetailActivity : BaseActivity()  {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnBack: ImageView
    private lateinit var btnClose: ImageView
    private lateinit var tvTituloTrofeos: TextView
    private lateinit var btnPrevious: ImageView
    private lateinit var btnNext: ImageView

    private val pageTransitionDuration = 800L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trofeos_detail)

        initViews()
        setupTrofeos()
        setupNavigation()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        btnBack = findViewById(R.id.btn_back)
        btnClose = findViewById(R.id.btn_close)
        tvTituloTrofeos = findViewById(R.id.tv_titulo_trofeos_detail)
        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
    }

    private fun setupTrofeos() {
        val trofeos = listOf(

            Trofeo("ic_trofeo_initia", getString(R.string.trofeo_initia_numeros_principiante)),
            Trofeo("ic_trofeo_constantia", getString(R.string.trofeo_constantia_numeros_avanzado)),
            Trofeo("ic_trofeo_confectus", getString(R.string.trofeo_confectus_numeros_pro)),

            Trofeo("ic_trofeo_via", getString(R.string.trofeo_via_deci_principiante)),
            Trofeo("ic_trofeo_altus", getString(R.string.trofeo_altus_deci_avanzado)),
            Trofeo("ic_trofeo_perseverantia", getString(R.string.trofeo_perseverantia_deci_pro)),

            Trofeo("ic_trofeo_gradus", getString(R.string.trofeo_gradus_romas_principiante)),
            Trofeo("ic_trofeo_fortitudo", getString(R.string.trofeo_fortitudo_romas_avanzado)),
            Trofeo("ic_trofeo_metam", getString(R.string.trofeo_metam_romas_pro)),

            Trofeo("ic_trofeo_fundamentum", getString(R.string.trofeo_fundamentum_alfanumeros_principiante)),
            Trofeo("ic_trofeo_praemium", getString(R.string.trofeo_praemium_alfanumeros_avanzado)),
            Trofeo("ic_trofeo_glorificus", getString(R.string.trofeo_glorificus_alfanumeros_pro)),

            Trofeo("ic_trofeo_scala", getString(R.string.trofeo_scala_sumaresta_principiante)),
            Trofeo("ic_trofeo_tenacitas", getString(R.string.trofeo_tenacitas_sumaresta_avanzado)),
            Trofeo("ic_trofeo_perfectus", getString(R.string.trofeo_perfectus_sumaresta_pro)),

            Trofeo("ic_trofeo_origo", getString(R.string.trofeo_origo_mas_principiante)),
            Trofeo("ic_trofeo_proficium", getString(R.string.trofeo_proficium_mas_avanzado)),
            Trofeo("ic_trofeo_exemplaritas", getString(R.string.trofeo_exemplaritas_mas_pro)),

            Trofeo("ic_trofeo_ascensus", getString(R.string.trofeo_ascensus_genio_principiante)),
            Trofeo("ic_trofeo_magnificus", getString(R.string.trofeo_magnificus_genio_avanzado)),
            Trofeo("ic_trofeo_potens", getString(R.string.trofeo_potens_genio_pro))
        )

        val adapter = TrofeoPagerAdapter(this, trofeos)
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 1

        viewPager.setPageTransformer(createTrofeoPageTransformer())

        try {
            val accessField = ViewPager2::class.java.getDeclaredField("mScrollEventAdapter")
            accessField.isAccessible = true
            val scrollEventAdapter = accessField.get(viewPager)
            val scrollDurationField = scrollEventAdapter.javaClass.getDeclaredField("mScrollDuration")
            scrollDurationField.isAccessible = true
            scrollDurationField.setInt(scrollEventAdapter, pageTransitionDuration.toInt())
        } catch (_: Exception) {

        }

        try {
            val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(viewPager) as RecyclerView
            recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        } catch (_: Exception) {

        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateNavigationButtons(position, trofeos.size)
            }
        })

        updateNavigationButtons(0, trofeos.size)
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

    private fun createTrofeoPageTransformer(): ViewPager2.PageTransformer {
        return ViewPager2.PageTransformer { page, position ->
            when {
                position < -1 || position > 1 -> {

                    page.alpha = 0f
                    page.visibility = View.INVISIBLE
                }
                position == 0f -> {

                    page.alpha = 1f
                    page.translationX = 0f
                    page.scaleX = 1f
                    page.scaleY = 1f
                    page.visibility = View.VISIBLE
                    page.translationZ = 1f
                }
                else -> {

                    if (abs(position) <= 0.5) {

                        val fadeInterpolation = 1 - (abs(position) * 2)
                        page.alpha = fadeInterpolation * fadeInterpolation

                        page.translationX = -position * (page.width / 4)

                        val scaleFactor = 0.97f.coerceAtLeast(1 - abs(position * 0.07f))
                        page.scaleX = scaleFactor
                        page.scaleY = scaleFactor
                        page.visibility = View.VISIBLE

                        page.translationZ = 0f
                    } else {

                        page.alpha = 0f
                        page.visibility = View.INVISIBLE
                    }
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
