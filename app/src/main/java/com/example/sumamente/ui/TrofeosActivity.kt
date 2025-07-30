package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sumamente.R
import com.example.sumamente.ui.utils.MusicManager
import java.lang.ref.WeakReference

class TrofeosActivity : BaseActivity() {

    companion object {
        var instanceRef: WeakReference<TrofeosActivity>? = null
        private var internalNavigation = false
        fun finishTrofeosActivity() {
            instanceRef?.get()?.finish()
        }
    }

    private lateinit var btnPin: LinearLayout
    private lateinit var btnCorona: LinearLayout
    private lateinit var btnMedalla: LinearLayout
    private lateinit var btnTrofeo: LinearLayout
    private lateinit var btnMisCondecoraciones: LinearLayout
    private lateinit var btnApexSupremus: LinearLayout
    private lateinit var btnTop10: LinearLayout
    private lateinit var btnLos7Mejores: LinearLayout
    private lateinit var btnLos5Mejores: LinearLayout
    private lateinit var btnClose: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var misCondecoracionesRedDot: View
    private lateinit var appLogo: ImageView
    private lateinit var titulo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trofeos)

        instanceRef = WeakReference(this)
        CondecoracionTracker.init(this)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SettingsActivity.SOUND_ENABLED) {
                val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
                if (soundEnabled) {
                    MusicManager.resume()
                } else {
                    MusicManager.pause()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        initViews()
        setupButtons()

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            MusicManager.play(this, R.raw.condecoraciones, looping = true, volume = 0.2f)
        }

        CondecoracionTracker.verificarYEntregarPines()
        updateMisCondecoracionesRedDot()

        startEntranceAnimation()
    }

    private fun initViews() {
        appLogo = findViewById(R.id.app_logo)
        btnPin = findViewById(R.id.btn_pin)
        btnCorona = findViewById(R.id.btn_corona)
        btnMedalla = findViewById(R.id.btn_medalla)
        btnTrofeo = findViewById(R.id.btn_trofeo)
        btnMisCondecoraciones = findViewById(R.id.btn_mis_condecoraciones)
        btnApexSupremus = findViewById(R.id.btn_apex_supremus)
        btnTop10 = findViewById(R.id.btn_top_10)
        btnLos7Mejores = findViewById(R.id.btn_los_7_mejores)
        btnLos5Mejores = findViewById(R.id.btn_los_5_mejores)
        btnClose = findViewById(R.id.btn_close)
        btnBack = findViewById(R.id.btn_back)
        misCondecoracionesRedDot = findViewById(R.id.mis_condecoraciones_red_dot)
        titulo = findViewById(R.id.tv_titulo_trofeos)
    }

    private fun setupButtons() {
        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                finish()
            }
        }

        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                finish()
            }
        }

        btnPin.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, PinesActivity::class.java))
            }
        }
        btnCorona.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, CoronasActivity::class.java))
            }
        }
        btnMedalla.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, MedallasActivity::class.java))
            }
        }
        btnTrofeo.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, TrofeosDetailActivity::class.java))
            }
        }
        btnMisCondecoraciones.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, MisCondecoracionesActivity::class.java))
            }
        }
        btnApexSupremus.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, ApexSupremusActivity::class.java))
            }
        }
        btnTop10.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, Top10Activity::class.java))
            }
        }
        btnLos7Mejores.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, Los7MejoresActivity::class.java))
            }
        }
        btnLos5Mejores.setOnClickListener {
            applyBounceEffect(it) {
                internalNavigation = true
                startActivity(Intent(this, Los5MejoresActivity::class.java))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled) {
            MusicManager.resume()
        }
    }

    override fun onResume() {
        super.onResume()
        updateMisCondecoracionesRedDot()
    }

    override fun onStop() {
        super.onStop()
        if (!internalNavigation) {
            MusicManager.pause()
        }
        internalNavigation = false
    }

    private fun updateMisCondecoracionesRedDot() {
        misCondecoracionesRedDot.visibility =
            if (CondecoracionTracker.shouldShowMisCondecoracionesRedDot())
                View.VISIBLE
            else
                View.GONE
    }

    private fun startEntranceAnimation() {
        val animZoomLogo = AnimationUtils.loadAnimation(this, R.anim.logo_zoom_in)
        appLogo.startAnimation(animZoomLogo)
        appLogo.alpha = 1f

        animZoomLogo.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                btnBack.animate()
                    .alpha(1f)
                    .setDuration(350)
                    .setStartDelay(0)
                    .start()
                btnClose.animate()
                    .alpha(1f)
                    .setDuration(350)
                    .setStartDelay(0)
                    .start()
                titulo.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay(100)
                    .start()

                val buttons = arrayOf(
                    btnMisCondecoraciones, btnPin, btnCorona, btnMedalla, btnTrofeo,
                    btnTop10, btnLos7Mejores, btnLos5Mejores, btnApexSupremus
                )
                buttons.forEachIndexed { index, button ->
                    button.alpha = 0f
                    button.translationY = 150f
                    button.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(500)
                        .setStartDelay(350 + (80 * index).toLong())
                        .start()
                }
            }
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })

        btnBack.alpha = 0f
        btnClose.alpha = 0f
        titulo.alpha = 0f
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply { playTogether(scaleDownX, scaleDownY) }
        val scaleUp = AnimatorSet().apply { playTogether(scaleUpX, scaleUpY) }
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })
        animatorSet.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        instanceRef?.clear()
        instanceRef = null
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
