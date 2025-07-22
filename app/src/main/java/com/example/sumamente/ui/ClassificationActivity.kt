package com.example.sumamente.ui

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import com.example.sumamente.R

class ClassificationActivity : BaseActivity()  {


    private lateinit var btnComoFunciona: LinearLayout
    private lateinit var btnVerClasificacion: LinearLayout
    private lateinit var btnClasificacionVelocidad: LinearLayout
    private lateinit var btnClasificacionIQPlus: LinearLayout
    private lateinit var btnClasificacionIntegral: LinearLayout
    private lateinit var btnClose: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        initViews()
        setupButtons()
        setupMusic()
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

    private fun setupMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.clasificacion)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.2f, 0.2f)

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            mediaPlayer.start()
        }

        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SettingsActivity.SOUND_ENABLED) {
                val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
                if (soundEnabled) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.pause()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun setupButtons() {
        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                mediaPlayer.fadeOut()
                val intent = Intent(this, MainGameActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                mediaPlayer.fadeOut()
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
            applyBounceEffect(it) {
                val intent = Intent(this, RankingActivity::class.java)
                startActivity(intent)
            }
        }

        btnClasificacionVelocidad.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, SpeedClassificationActivity::class.java)
                startActivity(intent)
            }
        }

        btnClasificacionIQPlus.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, IQPlusRankingActivity::class.java)
                startActivity(intent)
            }
        }

        btnClasificacionIntegral.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, IntegralRankingActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun showHowItWorksDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_classification_rules)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val btnEntendido = dialog.findViewById<Button>(R.id.btn_entendido)
        btnEntendido.setOnClickListener {
            applyBounceEffect(it) {
                dialog.dismiss()
            }
        }

        dialog.show()
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

    private fun MediaPlayer.fadeOut() {
        val fadeOutDuration = 2000L
        val fadeStep = 0.05f
        val fadeHandler = Handler(Looper.getMainLooper())
        var currentVolume = 0.2f

        val fadeRunnable = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    currentVolume -= fadeStep
                    if (currentVolume > 0) {
                        setVolume(currentVolume, currentVolume)
                        fadeHandler.postDelayed(
                            this,
                            (fadeOutDuration / (1 / fadeStep)).toLong()
                        )
                    } else {
                        pause()
                        setVolume(0f, 0f)
                    }
                }
            }
        }
        fadeHandler.post(fadeRunnable)
    }

    override fun onResume() {
        super.onResume()
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}