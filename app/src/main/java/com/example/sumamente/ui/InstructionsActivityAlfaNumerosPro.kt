package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.example.sumamente.R

class InstructionsActivityAlfaNumerosPro : BaseActivity()  {

    private var responseMode: ResponseModeAlfaNumeros? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferencesAlfaNumeros: android.content.SharedPreferences
    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView


    private val timeLimits = mapOf(
        1 to 17.99, 2 to 17.89, 3 to 17.78, 4 to 17.68, 5 to 17.57, 6 to 17.50, 7 to 17.34,
        8 to 21.40, 9 to 21.18, 10 to 20.95, 11 to 21.15, 12 to 20.93, 13 to 20.70, 14 to 20.43,
        15 to 22.54, 16 to 23.02, 17 to 22.69, 18 to 22.36, 19 to 22.03, 20 to 21.70, 21 to 22.13,
        22 to 24.11, 23 to 23.66, 24 to 23.20, 25 to 22.75, 26 to 23.59, 27 to 23.14, 28 to 22.63,
        29 to 23.12, 30 to 22.60, 31 to 23.65, 32 to 23.12, 33 to 22.60, 34 to 22.07, 35 to 21.50,
        36 to 23.56, 37 to 22.96, 38 to 22.36, 39 to 21.76, 40 to 21.16, 41 to 22.44, 42 to 21.79,
        43 to 21.96, 44 to 21.28, 45 to 20.60, 46 to 22.13, 47 to 21.45, 48 to 20.77, 49 to 20.04,
        50 to 19.87, 51 to 21.67, 52 to 20.91, 53 to 20.14, 54 to 19.38, 55 to 18.61, 56 to 20.36,
        57 to 20.20, 58 to 19.35, 59 to 18.50, 60 to 17.64, 61 to 19.73, 62 to 18.88, 63 to 17.97,
        64 to 17.45, 65 to 16.50, 66 to 18.90, 67 to 17.95, 68 to 17.00, 69 to 16.05, 70 to 15.05
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesAlfaNumeros = getSharedPreferences("MyPrefsAlfaNumeros", MODE_PRIVATE)
        setContentView(R.layout.activity_instructions_alfanumeros_pro)

        tvGameName   = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore      = findViewById(R.id.tv_score)
        setupInfoBar()

        val btnClose                = findViewById<ImageView>(R.id.btn_close)
        val btnStart                = findViewById<Button>(R.id.btn_start)
        val tvInstructions          = findViewById<TextView>(R.id.tv_instructions)
        val tvLevel                 = findViewById<TextView>(R.id.tv_level)
        val tvTimeLimit             = findViewById<TextView>(R.id.tv_time_limit)
        val tvRepeatedNumbersMessage= findViewById<TextView>(R.id.tv_repeated_numbers)
        val tvNegativeNumberWarning = findViewById<TextView>(R.id.tv_negative_numbers)


        level = intent.getIntExtra("LEVEL", 1)
        val timeLimit = timeLimits[level] ?: throw IllegalStateException(
            getString(R.string.error_time_limit_not_found, level)
        )

        intent.getStringExtra("RESPONSE_MODE")?.let {
            responseMode = ResponseModeAlfaNumeros.valueOf(it)
        }


        tvLevel.text         = getString(R.string.level_title, level)
        tvInstructions.text  = getLevelInstructions(level)

        // IA1 Cambio para solucionar el formato de los decimales //
        val locale = resources.configuration.locales[0]
        val formattedTimeLimit = getString(R.string.time_limit_text, timeLimit)
        val displayedTime = String.format(locale, "%.2f", timeLimit)
        val startBoldIndex = formattedTimeLimit.indexOf(displayedTime)
        val endBoldIndex = if (startBoldIndex != -1) startBoldIndex + displayedTime.length else formattedTimeLimit.length

        tvTimeLimit.text = if (startBoldIndex != -1) {
            formatTextWithBold(formattedTimeLimit, startBoldIndex, endBoldIndex)
        } else {
            formattedTimeLimit
        }
        // Fin del cambio IA1 //

        // Cambio de las palabras resaltadas
        val wordToHighlightYellow = getString(R.string.highlight_word_yellow)
        val repeatedNumbersTemplate = getString(R.string.repeated_numbers_yellow_formatted)
        val fullRepeatedNumbersMessage = String.format(repeatedNumbersTemplate, wordToHighlightYellow)

        tvRepeatedNumbersMessage.text = formatStyledText(
            fullText = fullRepeatedNumbersMessage,
            wordToStyle = wordToHighlightYellow,
            textColorResId = R.color.yellow,
            backgroundResId = R.color.blue_primary
        )

        if (level in listOf(3, 7, 10, 16, 19, 22, 25, 29, 33) || level >= 36) {
            val wordToHighlightNegative = getString(R.string.highlight_word_negative)
            val negativeWarningTemplate = getString(R.string.negative_numbers_warning_formatted)
            val fullNegativeWarningMessage = String.format(negativeWarningTemplate, wordToHighlightNegative)

            tvNegativeNumberWarning.text = formatStyledText(
                fullText = fullNegativeWarningMessage,
                wordToStyle = wordToHighlightNegative,
                textColorResId = R.color.red,
                backgroundResId = R.color.blue_primary
            )
        } else {
            tvNegativeNumberWarning.visibility = View.GONE
        }
        // Fin del cambio de las palabras resaltadas


        btnClose.setOnClickListener {
            btnClose.isEnabled = false
            val prefs = getSharedPreferences("MyPrefsAlfaNumeros", MODE_PRIVATE)
            val storedModeName = prefs.getString("selectedResponseModeAlfaNumerosPro", null)

            val intent = if (storedModeName == null) {
                Intent(this, ResponseModeDialogAlfaNumerosPro::class.java).apply {
                    putExtra("LEVEL", level)
                }
            } else {
                Intent(this, LevelsActivityAlfaNumerosPro::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            startActivity(intent)
            finish()
            btnClose.isEnabled = true
        }

        val fadeIn = 500L
        val animatorSet = AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(tvLevel,                 "alpha", 0f, 1f).setDuration(fadeIn),
                ObjectAnimator.ofFloat(tvInstructions,          "alpha", 0f, 1f).setDuration(fadeIn),
                ObjectAnimator.ofFloat(tvRepeatedNumbersMessage,"alpha", 0f, 1f).setDuration(fadeIn),
                ObjectAnimator.ofFloat(tvNegativeNumberWarning, "alpha", 0f, 1f).setDuration(fadeIn),
                ObjectAnimator.ofFloat(tvTimeLimit,             "alpha", 0f, 1f).setDuration(fadeIn),
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(btnStart,"scaleX",0f,1f).setDuration(fadeIn),
                        ObjectAnimator.ofFloat(btnStart,"scaleY",0f,1f).setDuration(fadeIn),
                        ObjectAnimator.ofFloat(btnStart,"alpha" ,0f,1f).setDuration(fadeIn)
                    )
                }
            )
        }
        animatorSet.start()


        fun bounce(view: View) = ObjectAnimator.ofFloat(view,"translationY",0f,-20f,0f).setDuration(250L)
        if (tvRepeatedNumbersMessage.isVisible) bounce(tvRepeatedNumbersMessage).start()
        if (tvNegativeNumberWarning.isVisible)  bounce(tvNegativeNumberWarning).start()


        btnStart.setOnClickListener {
            val clickAnim = AnimatorSet().apply {
                playSequentially(
                    ObjectAnimator.ofFloat(btnStart,"scaleX",1f,0.9f).setDuration(50),
                    ObjectAnimator.ofFloat(btnStart,"scaleY",1f,0.9f).setDuration(50),
                    ObjectAnimator.ofFloat(btnStart,"scaleX",0.9f,1f).setDuration(50),
                    ObjectAnimator.ofFloat(btnStart,"scaleY",0.9f,1f).setDuration(50)
                )
            }
            clickAnim.start()
            clickAnim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    responseMode?.let {
                        getSharedPreferences("MyPrefsAlfaNumeros", MODE_PRIVATE)
                            .edit { putString("selectedResponseModeAlfaNumerosPro", it.name) }
                    }
                    Intent(this@InstructionsActivityAlfaNumerosPro, GameActivityAlfaNumerosPro::class.java).apply {
                        putExtra("LEVEL", level)
                        responseMode?.let { putExtra("RESPONSE_MODE", it.name) }
                        putExtra("EXCLUDED_INDEX", excludedIndex)
                        startActivity(this)
                    }
                }
                override fun onAnimationStart(a: Animator) {}
                override fun onAnimationCancel(a: Animator) {}
                override fun onAnimationRepeat(a: Animator) {}
            })
        }
    }


    private fun setupInfoBar() {

        val alfa  = getString(R.string.text_alfa)
        val nums  = getString(R.string.text_numeros)
        val blend = "$alfa$nums"
        val span  = SpannableString(blend).apply {
            setSpan(
                android.text.style.ForegroundColorSpan(ContextCompat.getColor(this@InstructionsActivityAlfaNumerosPro, R.color.red_primary)),
                0, alfa.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                android.text.style.ForegroundColorSpan(ContextCompat.getColor(this@InstructionsActivityAlfaNumerosPro, R.color.blue_primary_darker)),
                alfa.length, blend.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        tvGameName.text = span


        val difficultyKey = "difficulty_alfanumeros"
        val difficulty = sharedPreferencesAlfaNumeros.getString(
            difficultyKey, DifficultySelectionActivity.DIFFICULTY_PRO
        )
        tvDifficulty.text = when (difficulty) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> getString(R.string.difficulty_principiante)
            DifficultySelectionActivity.DIFFICULTY_AVANZADO     -> getString(R.string.difficulty_avanzado)
            DifficultySelectionActivity.DIFFICULTY_PRO          -> getString(R.string.difficulty_pro)
            else                                                -> getString(R.string.difficulty_pro)
        }


        ScoreManager.initAlfaNumerosPro(this)
        tvScore.text = getString(R.string.score_format, ScoreManager.currentScoreAlfaNumerosPro)


        tvDifficulty.setOnClickListener {
            val bounce = AnimatorSet().apply {
                playSequentially(
                    ObjectAnimator.ofFloat(it,"scaleX",1f,0.9f).setDuration(50),
                    ObjectAnimator.ofFloat(it,"scaleY",1f,0.9f).setDuration(50),
                    ObjectAnimator.ofFloat(it,"scaleX",0.9f,1f).setDuration(50),
                    ObjectAnimator.ofFloat(it,"scaleY",0.9f,1f).setDuration(50)
                )
            }
            bounce.start()
            bounce.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(a: Animator) {
                    startActivity(
                        DifficultySelectionActivity.createIntent(
                            this@InstructionsActivityAlfaNumerosPro,
                            "AlfaNumeros",
                            true,
                            level,
                            responseMode?.name
                        )
                    )
                }
                override fun onAnimationStart(a: Animator) {}
                override fun onAnimationCancel(a: Animator) {}
                override fun onAnimationRepeat(a: Animator) {}
            })
        }
    }


    private fun getLevelInstructions(level: Int) = when (level) {
        in 1..10   -> getString(R.string.instructions_level_1_10)
        in 11..20  -> getString(R.string.instructions_level_11_20)
        in 21..30  -> getRandomExceptionInstruction(level)
        in 31..35  -> getString(R.string.instructions_level_31_35)
        in 36..40  -> getString(R.string.instructions_level_36_40)
        in 41..50  -> getRandomExceptionInstruction(level)
        in 51..60  -> getString(R.string.instructions_level_51_60)
        in 61..70  -> getRandomExceptionInstruction(level)
        else       -> getString(R.string.default_instructions)
    }


    private fun getRandomExceptionInstruction(level: Int): String {
        val exceptions = listOf(
            getString(R.string.exception_first),
            getString(R.string.exception_second),
            getString(R.string.exception_third),
            getString(R.string.exception_last)
        )
        return if (level % 2 == 1) {
            when (val msg = exceptions.random()) {
                getString(R.string.exception_first)  -> { excludedIndex = 0;  msg }
                getString(R.string.exception_second) -> { excludedIndex = 1;  msg }
                getString(R.string.exception_third)  -> { excludedIndex = 2;  msg }
                getString(R.string.exception_last)   -> { excludedIndex = -1; msg }
                else                                 -> getString(R.string.default_instructions)
            }
        } else {
            excludedIndex = null
            getString(R.string.default_instructions)
        }
    }

    private fun formatTextWithBold(text: String, start: Int, end: Int) = SpannableString(text).apply {
        setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun formatStyledText(
        fullText: String,
        wordToStyle: String,
        textColorResId: Int,
        backgroundResId: Int
    ): SpannableString {
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(wordToStyle)

        if (start == -1) {
            return spannable
        }

        val end = start + wordToStyle.length
        val textColor = ContextCompat.getColor(this, textColorResId)
        val backgroundColor = ContextCompat.getColor(this, backgroundResId)
        val roundedBackground = RadiusBackgroundSpan(backgroundColor, textColor, 10)
        spannable.setSpan(roundedBackground, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }
}
