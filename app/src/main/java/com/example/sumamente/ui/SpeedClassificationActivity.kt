package com.example.sumamente.ui

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.sumamente.R
import com.example.sumamente.ui.utils.MusicManager

class SpeedClassificationActivity : BaseActivity()  {

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private lateinit var closeButton: ImageView
    private lateinit var btnNumerosPlus: ConstraintLayout
    private lateinit var btnDeciPlus: ConstraintLayout
    private lateinit var btnRomas: ConstraintLayout
    private lateinit var btnAlfaNumeros: ConstraintLayout
    private lateinit var btnSumaresta: ConstraintLayout
    private lateinit var btnMasPlus: ConstraintLayout
    private lateinit var btnGenioPlus: ConstraintLayout
    private var isFinishingByBack = false

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

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        initViews()
        setupButtons()

        setupGameNameColors()
    }

    private fun initViews() {
        closeButton = findViewById(R.id.closeButton)
        btnNumerosPlus = findViewById(R.id.btn_numeros_plus)
        btnDeciPlus = findViewById(R.id.btn_deci_plus)
        btnRomas = findViewById(R.id.btn_romas)
        btnAlfaNumeros = findViewById(R.id.btn_alfa_numeros)
        btnSumaresta = findViewById(R.id.btn_sumaresta)
        btnMasPlus = findViewById(R.id.btn_mas_plus)
        btnGenioPlus = findViewById(R.id.btn_genio_plus)
    }

    private fun setupButtons() {
        closeButton.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                finish()
            }
        }

        btnNumerosPlus.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                openSpeedRanking(GAME_NUMEROS_PLUS, R.color.blue_primary)
            }
        }

        btnDeciPlus.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                openSpeedRanking(GAME_DECI_PLUS, R.color.orange_dark)
            }
        }

        btnRomas.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                openSpeedRanking(GAME_ROMAS, R.color.green_dark)
            }
        }

        btnAlfaNumeros.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                openSpeedRanking(GAME_ALFA_NUMEROS, R.color.red_primary)
            }
        }

        btnSumaresta.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                openSpeedRanking(GAME_SUMA_RESTA, R.color.blue_pressed)
            }
        }

        btnMasPlus.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                openSpeedRanking(GAME_MAS_PLUS, R.color.grey_light)
            }
        }

        btnGenioPlus.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
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
        applyAlfaNumerosColor(btnAlfaNumeros)
        applySumarestaColor(btnSumaresta)
        applyMasPlusColor(btnMasPlus)
        applyGenioPlusColor(btnGenioPlus)
    }

    private fun applyAlfaNumerosColor(button: ConstraintLayout) {
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

    private fun applySumarestaColor(button: ConstraintLayout) {
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

    private fun applyMasPlusColor(button: ConstraintLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_mas_plus)
        textView.setTextColor(ContextCompat.getColor(this, R.color.grey_light))
    }

    private fun applyGenioPlusColor(button: ConstraintLayout) {
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

    override fun onStart() {
        super.onStart()
        val clasificacionSigueViva = ClassificationActivity.instanceRef?.get() != null
        val context = ClassificationActivity.instanceRef?.get()
        val sonidoActivo = context?.let {
            val prefs = it.getSharedPreferences("MyPrefs", MODE_PRIVATE)
            prefs.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        } ?: true

        if (clasificacionSigueViva && sonidoActivo) {
            MusicManager.resume()
        }
    }


    override fun onStop() {
        super.onStop()
        if (!isFinishingByBack) {
            MusicManager.pause()
        }
        isFinishingByBack = false
    }



}
