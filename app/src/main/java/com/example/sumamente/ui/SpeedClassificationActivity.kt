package com.example.sumamente.ui

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sumamente.R

class SpeedClassificationActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: android.content.SharedPreferences

    companion object {
        const val GAME_NUMEROS_PLUS = "NumerosPlus"
        const val GAME_DECI_PLUS = "DeciPlus"
        const val GAME_ROMAS = "Romas"
        const val GAME_ALFA_NUMEROS = "AlfaNumeros"
        const val GAME_SUMA_RESTA = "SumaResta"
        const val GAME_MAS_PLUS = "MasPlus"
        const val GAME_GENIO_PLUS = "GenioPlus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_classification)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        initViews()
        setupButtons()
        setupMusic()
        setupGameNameColors()
    }

    private fun initViews() {

    }

    private fun setupMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.clasificacion)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.2f, 0.2f)

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            mediaPlayer.start()
        }
    }

    private fun setupButtons() {
        val closeButton = findViewById<ImageView>(R.id.closeButton)
        val btnNumerosPlus = findViewById<RelativeLayout>(R.id.btn_numeros_plus)
        val btnDeciPlus = findViewById<RelativeLayout>(R.id.btn_deci_plus)
        val btnRomas = findViewById<RelativeLayout>(R.id.btn_romas)
        val btnAlfaNumeros = findViewById<RelativeLayout>(R.id.btn_alfa_numeros)
        val btnSumaresta = findViewById<RelativeLayout>(R.id.btn_sumaresta)
        val btnMasPlus = findViewById<RelativeLayout>(R.id.btn_mas_plus)
        val btnGenioPlus = findViewById<RelativeLayout>(R.id.btn_genio_plus)

        closeButton.setOnClickListener {
            applyBounceEffect(it) {
                finish()
            }
        }

        btnNumerosPlus.setOnClickListener {
            applyBounceEffect(it) {
                openSpeedRanking(GAME_NUMEROS_PLUS, R.color.blue_primary)
            }
        }

        btnDeciPlus.setOnClickListener {
            applyBounceEffect(it) {
                openSpeedRanking(GAME_DECI_PLUS, R.color.orange_dark)
            }
        }

        btnRomas.setOnClickListener {
            applyBounceEffect(it) {
                openSpeedRanking(GAME_ROMAS, R.color.green_dark)
            }
        }

        btnAlfaNumeros.setOnClickListener {
            applyBounceEffect(it) {
                openSpeedRanking(GAME_ALFA_NUMEROS, R.color.red_primary)
            }
        }

        btnSumaresta.setOnClickListener {
            applyBounceEffect(it) {
                openSpeedRanking(GAME_SUMA_RESTA, R.color.blue_pressed)
            }
        }

        btnMasPlus.setOnClickListener {
            applyBounceEffect(it) {
                openSpeedRanking(GAME_MAS_PLUS, R.color.grey_light)
            }
        }

        btnGenioPlus.setOnClickListener {
            applyBounceEffect(it) {
                openSpeedRanking(GAME_GENIO_PLUS, R.color.blue_pressed)
            }
        }
    }

    private fun openSpeedRanking(gameType: String, gameColor: Int) {
        val intent = Intent(this, SpeedRankingActivity::class.java).apply {
            putExtra(SpeedRankingActivity.EXTRA_GAME_TYPE, gameType)
            putExtra(SpeedRankingActivity.EXTRA_GAME_COLOR, gameColor)
        }
        startActivity(intent)
    }

    private fun setupGameNameColors() {

        val btnAlfaNumeros = findViewById<RelativeLayout>(R.id.btn_alfa_numeros)
        val btnSumaresta = findViewById<RelativeLayout>(R.id.btn_sumaresta)

        applyAlfaNumerosColor(btnAlfaNumeros)
        applySumarestaColor(btnSumaresta)
        applyMasPlusColor(findViewById(R.id.btn_mas_plus))
        applyGenioPlusColor(findViewById(R.id.btn_genio_plus))
    }

    private fun applyAlfaNumerosColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_alfa_numeros)

        val alfaText = getString(R.string.text_alfa)
        val numerosText = getString(R.string.text_numeros)
        val alfaNumerosText = "$alfaText$numerosText"
        val spannableAlfaNumeros = SpannableString(alfaNumerosText)

        spannableAlfaNumeros.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)),
            0, alfaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableAlfaNumeros.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)),
            alfaText.length, alfaNumerosText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableAlfaNumeros
    }

    private fun applySumarestaColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_sumaresta)

        val sumaText = getString(R.string.text_suma)
        val restaText = getString(R.string.text_resta)
        val sumarestaText = "$sumaText$restaText"
        val spannableSumaresta = SpannableString(sumarestaText)

        spannableSumaresta.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)),
            0, sumaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableSumaresta.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
            sumaText.length, sumarestaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableSumaresta
    }

    private fun applyMasPlusColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_mas_plus)
        textView.setTextColor(ContextCompat.getColor(this, R.color.grey_light))
    }

    private fun applyGenioPlusColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_genio_plus)
        textView.setTextColor(ContextCompat.getColor(this, R.color.blue_pressed))
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

    override fun onResume() {
        super.onResume()
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
