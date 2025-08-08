package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.os.Bundle
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
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.MusicManager


class SpeedClassificationActivity : BaseActivity() {

    private data class GameButton(
        val id: Int,
        val name: String,
        val colorRes: Int,
        val iconId: Int,
        var button: ConstraintLayout? = null,
        var icon: ImageView? = null
    )

    private val gameButtons = listOf(
        GameButton(R.id.btn_numeros_plus, GAME_NUMEROS_PLUS, R.color.blue_primary, R.id.ic_reloj_numeros_plus),
        GameButton(R.id.btn_deci_plus, GAME_DECI_PLUS, R.color.orange_dark, R.id.ic_reloj_deci_plus),
        GameButton(R.id.btn_romas, GAME_ROMAS, R.color.green_dark, R.id.ic_reloj_romas),
        GameButton(R.id.btn_alfa_numeros, GAME_ALFA_NUMEROS, R.color.red_primary, R.id.ic_reloj_alfa_numeros),
        GameButton(R.id.btn_sumaresta, GAME_SUMA_RESTA, R.color.blue_pressed, R.id.ic_reloj_sumaresta),
        GameButton(R.id.btn_mas_plus, GAME_MAS_PLUS, R.color.grey_light, R.id.ic_reloj_mas_plus),
        GameButton(R.id.btn_genio_plus, GAME_GENIO_PLUS, R.color.blue_pressed, R.id.ic_reloj_genio_plus)
    )

    private lateinit var btnBack: ImageView
    private lateinit var closeButton: ImageView
    private var isFinishingByBack = false

    companion object {
        const val GAME_NUMEROS_PLUS = "NumerosPlus"
        const val GAME_DECI_PLUS = "DeciPlus"
        const val GAME_ROMAS = "Romas"
        const val GAME_ALFA_NUMEROS = "AlfaNumeros"
        const val GAME_SUMA_RESTA = "SumaResta"
        const val GAME_MAS_PLUS = "MasPlus"
        const val GAME_GENIO_PLUS = "GenioPlus"
        private const val ANIMATION_DELAY = 700L // ms
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_classification)

        initViews()
        setupGameNameColors()
        setupClickListeners()
        setupBackNavigation()
        setupEntranceAnimations()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        closeButton = findViewById(R.id.closeButton)
        gameButtons.forEach { game ->
            game.button = findViewById(game.id)
            game.icon = game.button?.findViewById(game.iconId)
            game.icon?.tag = "clock"
        }
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
                goToMainGame()
            }
        }

        gameButtons.forEach { game ->
            game.button?.setOnClickListener { view ->
                game.icon?.let { animateClockIcon(it) }
                applyBounceEffect(view) {
                    view.postDelayed({
                        isFinishingByBack = true
                        openSpeedRanking(game.name, game.colorRes)
                    }, ANIMATION_DELAY)
                }
            }
        }
    }

    private fun setupEntranceAnimations() {
        val tvTitle = findViewById<TextView>(R.id.tv_speed_title)
        val container = findViewById<LinearLayout>(R.id.layout_speed_buttons)

        tvTitle.animate()
            .alpha(1f)
            .setDuration(450)
            .setStartDelay(100)
            .start()

        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            view.translationY = 60f
            val animator = view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(450)
                .setStartDelay(200 + i * 80L)

            if (i == container.childCount - 1) {
                animator.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        startTitleShineAnimation(tvTitle)
                    }
                })
            }
            animator.start()
        }
    }

    private fun startTitleShineAnimation(textView: TextView) {
        textView.post {
            val textWidth = textView.paint.measureText(textView.text.toString())
            val baseColor = textView.currentTextColor
            val shineColor = ContextCompat.getColor(textView.context, R.color.white)

            val shader = LinearGradient(
                -textWidth, 0f, 0f, 0f,
                intArrayOf(baseColor, shineColor, baseColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )

            textView.paint.shader = shader
            val matrix = Matrix()

            val animator = ValueAnimator.ofFloat(0f, 2 * textWidth)
            animator.duration = 800
            animator.startDelay = 500
            animator.addUpdateListener {
                val translate = it.animatedValue as Float
                matrix.setTranslate(translate, 0f)
                shader.setLocalMatrix(matrix)
                textView.invalidate()
            }
            animator.addListener(object: AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {
                    textView.paint.shader = null
                }
            })
            animator.start()
        }
    }

    private fun setupBackNavigation() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToMainGame()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun goToMainGame() {
        val intent = Intent(this, MainGameActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun openSpeedRanking(gameType: String, gameColor: Int) {
        val intent = Intent(this, SpeedRankingActivity::class.java).apply {
            putExtra(SpeedRankingActivity.EXTRA_GAME_TYPE, gameType)
            putExtra(SpeedRankingActivity.EXTRA_GAME_COLOR, gameColor)
        }
        startActivity(intent)
    }

    private fun animateClockIcon(icon: ImageView) {
        ObjectAnimator.ofFloat(icon, "rotation", 0f, 360f).apply {
            duration = ANIMATION_DELAY
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

    private fun setupGameNameColors() {
        gameButtons.forEach { game ->
            when (game.name) {
                GAME_ALFA_NUMEROS -> applyAlfaNumerosColor(game.button)
                GAME_SUMA_RESTA -> applySumarestaColor(game.button)
                GAME_MAS_PLUS -> applyMasPlusColor(game.button)
                GAME_GENIO_PLUS -> applyGenioPlusColor(game.button)
            }
        }
    }

    private fun applyAlfaNumerosColor(button: ConstraintLayout?) {
        button ?: return
        val textView = button.findViewById<TextView>(R.id.tv_game_name_alfa_numeros)
        val alfaText = getString(R.string.text_alfa)
        val numerosText = getString(R.string.text_numeros)
        val alfaNumerosText = "$alfaText$numerosText"
        val spannableAlfaNumeros = SpannableString(alfaNumerosText)
        spannableAlfaNumeros.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)),
            0, alfaText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableAlfaNumeros.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)),
            alfaText.length, alfaNumerosText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableAlfaNumeros
    }

    private fun applySumarestaColor(button: ConstraintLayout?) {
        button ?: return
        val textView = button.findViewById<TextView>(R.id.tv_game_name_sumaresta)
        val sumaText = getString(R.string.text_suma)
        val restaText = getString(R.string.text_resta)
        val sumarestaText = "$sumaText$restaText"
        val spannableSumaresta = SpannableString(sumarestaText)
        spannableSumaresta.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)),
            0, sumaText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableSumaresta.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
            sumaText.length, sumarestaText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableSumaresta
    }

    private fun applyMasPlusColor(button: ConstraintLayout?) {
        button ?: return
        val textView = button.findViewById<TextView>(R.id.tv_game_name_mas_plus)
        textView.setTextColor(ContextCompat.getColor(this, R.color.grey_light))
    }

    private fun applyGenioPlusColor(button: ConstraintLayout?) {
        button ?: return
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
