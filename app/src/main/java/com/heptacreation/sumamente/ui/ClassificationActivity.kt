package com.heptacreation.sumamente.ui

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
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.MusicManager
import java.lang.ref.WeakReference
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge

class ClassificationActivity : BaseActivity() {

    companion object {
        var instanceRef: WeakReference<ClassificationActivity>? = null
        private var internalNavigation = false
    }

    private lateinit var btnComoFunciona: LinearLayout
    private lateinit var btnVerClasificacion: LinearLayout
    private lateinit var btnClasificacionVelocidad: LinearLayout
    private lateinit var btnClasificacionIQPlus: LinearLayout
    private lateinit var btnClasificacionIntegral: LinearLayout
    private lateinit var btnClose: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification)

        instanceRef = WeakReference(this)

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

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MusicManager.stop()
                val intent = Intent(this@ClassificationActivity, MainGameActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {

            if (!MusicManager.isPlaying()) {
                MusicManager.play(this, R.raw.clasificacion, looping = true, volume = 0.2f)
            }
        }

        startAnimations()
    }

    private fun initViews() {
        btnComoFunciona = findViewById(R.id.btn_como_funciona)
        btnVerClasificacion = findViewById(R.id.btn_ver_clasificacion)
        btnClasificacionVelocidad = findViewById(R.id.btn_clasificacion_velocidad)
        btnClasificacionIQPlus = findViewById(R.id.btn_clasificacion_iqplus)
        btnClasificacionIntegral = findViewById(R.id.btn_clasificacion_integral)
        btnClose = findViewById(R.id.btn_close)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupButtons() {
        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                MusicManager.stop()
                val intent = Intent(this, MainGameActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                MusicManager.stop()
                val intent = Intent(this, MainGameActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnComoFunciona.setOnClickListener {
            applyBounceEffect(it) {
                showHowItWorksDialog()
            }
        }

        btnVerClasificacion.setOnClickListener {
            internalNavigation = true
            applyBounceEffect(it) {
                val intent = Intent(this, RankingActivity::class.java)
                startActivity(intent)
            }
        }

        btnClasificacionVelocidad.setOnClickListener {
            internalNavigation = true
            applyBounceEffect(it) {
                val intent = Intent(this, SpeedClassificationActivity::class.java)
                startActivity(intent)
            }
        }

        btnClasificacionIQPlus.setOnClickListener {
            internalNavigation = true
            applyBounceEffect(it) {
                val intent = Intent(this, IQPlusRankingActivity::class.java)
                startActivity(intent)
            }
        }

        btnClasificacionIntegral.setOnClickListener {
            internalNavigation = true
            applyBounceEffect(it) {
                val intent = Intent(this, IntegralRankingActivity::class.java)
                startActivity(intent)
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

    override fun onStop() {
        super.onStop()
        if (!internalNavigation) {
            MusicManager.pause()
        }
        internalNavigation = false
    }

    private fun showHowItWorksDialog() {
        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_classification_rules)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnEntendido = dialog.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_entendido)
        btnEntendido.setOnClickListener {
            applyBounceEffect(it) {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun startAnimations() {
        val logo = findViewById<ImageView>(R.id.app_logo)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val titulo = findViewById<TextView>(R.id.tv_titulo_clasificaciones)

        val animZoomLogo = AnimationUtils.loadAnimation(this, R.anim.logo_zoom_in)
        logo.startAnimation(animZoomLogo)
        logo.alpha = 1f


        btnBack.alpha = 0f
        btnClose.alpha = 0f
        titulo.alpha = 0f
        val buttons = arrayOf(
            findViewById(R.id.btn_como_funciona),
            findViewById(R.id.btn_ver_clasificacion),
            findViewById(R.id.btn_clasificacion_velocidad),
            findViewById(R.id.btn_clasificacion_iqplus),
            findViewById<LinearLayout>(R.id.btn_clasificacion_integral)
        )
        buttons.forEach {
            it.alpha = 0f
            it.translationY = 150f
        }

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
                    .withEndAction {

                        buttons.forEachIndexed { index, button ->
                            button.animate()
                                .alpha(1f)
                                .translationY(0f)
                                .setDuration(500)
                                .setStartDelay((80 * index).toLong())
                                .start()
                        }
                    }
                    .start()
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
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

    override fun onDestroy() {
        super.onDestroy()
        instanceRef?.clear()
        instanceRef = null
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
