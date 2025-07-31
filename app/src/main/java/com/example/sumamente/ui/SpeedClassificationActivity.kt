package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.sumamente.R
import com.example.sumamente.ui.utils.MusicManager

class SpeedClassificationActivity : BaseActivity()  {

    private lateinit var btnBack: ImageView
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

        initViews()
        setupClickListeners()
        setupGameNameColors()

        // Inicio del cambio flecha de regresar del celular
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val intent = Intent(this@SpeedClassificationActivity, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        // Fin del código de flecha de regresar del celular

        setupAnimations()
        setupBackButtonHandler()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        closeButton = findViewById(R.id.closeButton)
        btnNumerosPlus = findViewById(R.id.btn_numeros_plus)
        btnDeciPlus = findViewById(R.id.btn_deci_plus)
        btnRomas = findViewById(R.id.btn_romas)
        btnAlfaNumeros = findViewById(R.id.btn_alfa_numeros)
        btnSumaresta = findViewById(R.id.btn_sumaresta)
        btnMasPlus = findViewById(R.id.btn_mas_plus)
        btnGenioPlus = findViewById(R.id.btn_genio_plus)
    }

    private fun setupClickListeners() {

        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                finish()
            }
        }

        closeButton.setOnClickListener {
            applyBounceEffect(it) {

                val intent = Intent(this, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        }


        val gameButtonClickListener = { view: View, gameType: String, gameColor: Int ->
            animateClockIcon(view.findViewWithTag("clock"))
            applyBounceEffect(view) {
                isFinishingByBack = true
                openSpeedRanking(gameType, gameColor)
            }
        }

        btnNumerosPlus.setOnClickListener { gameButtonClickListener(it, GAME_NUMEROS_PLUS, R.color.blue_primary) }
        btnDeciPlus.setOnClickListener { gameButtonClickListener(it, GAME_DECI_PLUS, R.color.orange_dark) }
        btnRomas.setOnClickListener { gameButtonClickListener(it, GAME_ROMAS, R.color.green_dark) }
        btnAlfaNumeros.setOnClickListener { gameButtonClickListener(it, GAME_ALFA_NUMEROS, R.color.red_primary) }
        btnSumaresta.setOnClickListener { gameButtonClickListener(it, GAME_SUMA_RESTA, R.color.blue_pressed) }
        btnMasPlus.setOnClickListener { gameButtonClickListener(it, GAME_MAS_PLUS, R.color.grey_light) }
        btnGenioPlus.setOnClickListener { gameButtonClickListener(it, GAME_GENIO_PLUS, R.color.blue_pressed) }


        btnNumerosPlus.findViewById<ImageView>(R.id.ic_reloj_numeros_plus).tag = "clock"
        btnDeciPlus.findViewById<ImageView>(R.id.ic_reloj_deci_plus).tag = "clock"
        btnRomas.findViewById<ImageView>(R.id.ic_reloj_romas).tag = "clock"
        btnAlfaNumeros.findViewById<ImageView>(R.id.ic_reloj_alfa_numeros).tag = "clock"
        btnSumaresta.findViewById<ImageView>(R.id.ic_reloj_sumaresta).tag = "clock"
        btnMasPlus.findViewById<ImageView>(R.id.ic_reloj_mas_plus).tag = "clock"
        btnGenioPlus.findViewById<ImageView>(R.id.ic_reloj_genio_plus).tag = "clock"
    }

    private fun setupAnimations() {
        val tvTitle = findViewById<TextView>(R.id.tv_speed_title)
        val container = findViewById<LinearLayout>(R.id.layout_speed_buttons)


        tvTitle.translationY = -50f
        tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(450)
            .setStartDelay(100)
            .start()


        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            view.translationY = 60f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(450)
                .setStartDelay(200 + i * 80L)
                .start()
        }
    }

    private fun setupBackButtonHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val intent = Intent(this@SpeedClassificationActivity, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
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

    private fun animateClockIcon(icon: ImageView) {
        ObjectAnimator.ofFloat(icon, "rotation", 0f, 360f).apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDown = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.9f))
                .with(ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.9f))
            duration = 100
        }
        val scaleUp = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(view, View.SCALE_X, 0.9f, 1f))
                .with(ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.9f, 1f))
            duration = 100
        }
        AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd()
                }
            })
            start()
        }
    }


    private fun applyAlfaNumerosColor(button: ConstraintLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_alfa_numeros)
        val alfaText = getString(R.string.text_alfa)
        val numerosText = getString(R.string.text_numeros)
        val alfaNumerosText = "$alfaText$numerosText"
        val spannableAlfaNumeros = SpannableString(alfaNumerosText)
        spannableAlfaNumeros.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)), 0, alfaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableAlfaNumeros.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)), alfaText.length, alfaNumerosText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableAlfaNumeros
    }

    private fun applySumarestaColor(button: ConstraintLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_sumaresta)
        val sumaText = getString(R.string.text_suma)
        val restaText = getString(R.string.text_resta)
        val sumarestaText = "$sumaText$restaText"
        val spannableSumaresta = SpannableString(sumarestaText)
        spannableSumaresta.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)), 0, sumaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableSumaresta.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), sumaText.length, sumarestaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
