package com.example.sumamente.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sumamente.R

class MainActivity : AppCompatActivity() {

    private lateinit var textViewName: TextView
    private lateinit var textViewNameBrillo: TextView
    private lateinit var mediaPlayerNombre: MediaPlayer
    private lateinit var mediaPlayerBrillo: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private var isMusicPaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        setTheme(R.style.Theme_SumaMente)
        setContentView(R.layout.activity_splash_screen)

        textViewName = findViewById(R.id.heptacreationText)
        textViewNameBrillo = findViewById(R.id.heptacreationText_brillo)
        mediaPlayerNombre = MediaPlayer.create(this, R.raw.nombrehepta)
        mediaPlayerBrillo = MediaPlayer.create(this, R.raw.brillonombre)

        iniciarAnimacion()

        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "sound_enabled") {
                val soundEnabled = sharedPreferences.getBoolean("sound_enabled", true)
                if (!soundEnabled) {
                    pauseMusic()
                } else {
                    resumeMusic()
                }
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun iniciarAnimacion() {
        val animacionEntrada = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.anim_appear_from_bottom)
        textViewName.startAnimation(animacionEntrada)

        if (sharedPreferences.getBoolean("sound_enabled", true)) {
            mediaPlayerNombre.start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayerNombre.isPlaying) {
                mediaPlayerNombre.pause()
            }
            textViewNameBrillo.visibility = TextView.VISIBLE
            iniciarEfectoBrillo()
        }, 3400)
    }

    private fun iniciarEfectoBrillo() {
        val animacionBrillo = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.color_movement)
        textViewNameBrillo.startAnimation(animacionBrillo)

        if (sharedPreferences.getBoolean("sound_enabled", true)) {
            mediaPlayerBrillo.start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayerBrillo.isPlaying) {
                mediaPlayerBrillo.pause()
            }
            transicionPantalla()
        }, 4200)
    }

    private fun transicionPantalla() {
        val intent = Intent(this, TransitionActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun pauseMusic() {
        if (mediaPlayerNombre.isPlaying) {
            mediaPlayerNombre.pause()
            isMusicPaused = true
        }
        if (mediaPlayerBrillo.isPlaying) {
            mediaPlayerBrillo.pause()
        }
    }

    private fun resumeMusic() {
        if (isMusicPaused) {
            mediaPlayerNombre.start()
            isMusicPaused = false
        }
        mediaPlayerBrillo.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerNombre.release()
        mediaPlayerBrillo.release()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
