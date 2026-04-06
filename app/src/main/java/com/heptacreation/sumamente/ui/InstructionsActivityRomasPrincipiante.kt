package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.heptacreation.sumamente.R
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.ads.AdView

class InstructionsActivityRomasPrincipiante : BaseActivity()  {

    private var responseMode: ResponseModeRomas? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferencesRomas: android.content.SharedPreferences
    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView
    private lateinit var adView: AdView

    private val timeLimits = mapOf(
        1 to 24.29, 2 to 24.19, 3 to 24.08, 4 to 23.98, 5 to 23.87, 6 to 23.80, 7 to 23.65,
        8 to 30.40, 9 to 30.18, 10 to 29.95, 11 to 30.15, 12 to 29.93, 13 to 29.70, 14 to 29.43,
        15 to 33.64, 16 to 33.22, 17 to 33.49, 18 to 33.16, 19 to 32.83, 20 to 32.50, 21 to 32.92,
        22 to 36.72, 23 to 36.26, 24 to 35.81, 25 to 36.35, 26 to 36.19, 27 to 35.74, 28 to 36.23,
        29 to 36.63, 30 to 36.10, 31 to 36.63, 32 to 36.10, 33 to 35.58, 34 to 35.00, 35 to 35.75,
        36 to 37.96, 37 to 37.36, 38 to 36.76, 39 to 36.16, 40 to 35.56, 41 to 36.84, 42 to 36.19,
        43 to 37.26, 44 to 36.58, 45 to 35.90, 46 to 37.43, 47 to 36.75, 48 to 36.07, 49 to 35.34,
        50 to 35.17, 51 to 37.87, 52 to 37.11, 53 to 36.34, 54 to 35.58, 55 to 34.81, 56 to 36.56,
        57 to 37.31, 58 to 36.45, 59 to 35.60, 60 to 34.74, 61 to 36.83, 62 to 35.98, 63 to 35.07,
        64 to 35.45, 65 to 34.50, 66 to 36.90, 67 to 35.95, 68 to 35.00, 69 to 34.05, 70 to 33.05
    )


    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        sharedPreferencesRomas = getSharedPreferences("MyPrefsRomas", MODE_PRIVATE)
        setContentView(R.layout.activity_instructions_romas)

        tvGameName = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore = findViewById(R.id.tv_score)
        AdManager.initialize(this)
        adView = findViewById(R.id.adView)
        AdManager.loadBanner(this, adView)

        val btnClose = findViewById<ImageView>(R.id.btn_close)

        val btnChangeMode = findViewById<ImageView>(R.id.btn_change_mode)
        btnChangeMode.setOnClickListener {
            val dialog = ResponseModeDialogRomasPrincipiante(this)
            dialog.setOnResponseModeSelectedListener(object : ResponseModeDialogRomasPrincipiante.OnResponseModeSelectedListenerRomasPrincipiante {
                override fun onResponseModeSelected(mode: ResponseModeRomasPrincipiante) {
                    responseMode = when(mode) {
                        ResponseModeRomasPrincipiante.SIMPLE_SELECTION_ROMAS -> ResponseModeRomas.SIMPLE_SELECTION_ROMAS
                        ResponseModeRomasPrincipiante.TYPE_ANSWER_ROMAS -> ResponseModeRomas.TYPE_ANSWER_ROMAS
                    }
                    getSharedPreferences("MyPrefsRomas", MODE_PRIVATE)
                        .edit {
                            putString("selectedResponseModeRomasPrincipiante", mode.name)
                        }
                }
            })
            dialog.show()
        }

        val btnStart = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_start)
        val tvInstructions = findViewById<TextView>(R.id.tv_instructions)
        val tvLevel = findViewById<TextView>(R.id.tv_level)
        val tvTimeLimit = findViewById<TextView>(R.id.tv_time_limit)
        val tvRepeatedNumbersMessage = findViewById<TextView>(R.id.tv_repeated_numbers)
        val tvNegativeNumberWarning = findViewById<TextView>(R.id.tv_negative_numbers)



        setupInfoBar()

        level = intent.getIntExtra("LEVEL", 1)
        val timeLimit = timeLimits[level] ?: throw IllegalStateException("Time limit not found for level $level")

        val responseModeName = intent.getStringExtra("RESPONSE_MODE_ROMAS")
        if (responseModeName != null) {
            responseMode = ResponseModeRomas.valueOf(responseModeName)
        }

        tvLevel.text = getString(R.string.level_title, level)
        tvInstructions.text = getLevelInstructions(level)

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

            val intent = Intent(this, LevelsActivityRomasPrincipiante::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

            btnClose.isEnabled = true
        }

        val fadeInDuration = 500L
        val levelAnimation = ObjectAnimator.ofFloat(tvLevel, "alpha", 0f, 1f).setDuration(fadeInDuration)
        val instructionsAnimation = ObjectAnimator.ofFloat(tvInstructions, "alpha", 0f, 1f).setDuration(fadeInDuration)
        val repeatedNumbersAnimation = ObjectAnimator.ofFloat(tvRepeatedNumbersMessage, "alpha", 0f, 1f).setDuration(fadeInDuration)
        val negativeNumberAnimation = ObjectAnimator.ofFloat(tvNegativeNumberWarning, "alpha", 0f, 1f).setDuration(fadeInDuration)
        val timeLimitAnimation = ObjectAnimator.ofFloat(tvTimeLimit, "alpha", 0f, 1f).setDuration(fadeInDuration)

        val animatorSet = AnimatorSet()
        val animationsList = mutableListOf<Animator>()

        animationsList.add(levelAnimation)
        animationsList.add(instructionsAnimation)
        animationsList.add(repeatedNumbersAnimation)
        animationsList.add(negativeNumberAnimation)
        animationsList.add(timeLimitAnimation)

        if (tvRepeatedNumbersMessage.isVisible && tvNegativeNumberWarning.isVisible) {
            val bounceAmarillos = ObjectAnimator.ofFloat(tvRepeatedNumbersMessage, "translationY", 0f, -20f, 0f).apply {
                duration = 250L
            }
            val bounceNegativos = ObjectAnimator.ofFloat(tvNegativeNumberWarning, "translationY", 0f, -20f, 0f).apply {
                duration = 250L
            }
            animationsList.add(bounceAmarillos)
            animationsList.add(bounceNegativos)
        } else if (tvRepeatedNumbersMessage.isVisible) {
            val bounceAmarillos = ObjectAnimator.ofFloat(tvRepeatedNumbersMessage, "translationY", 0f, -20f, 0f).apply {
                duration = 250L
            }
            animationsList.add(bounceAmarillos)
        } else if (tvNegativeNumberWarning.isVisible) {
            val bounceNegativos = ObjectAnimator.ofFloat(tvNegativeNumberWarning, "translationY", 0f, -20f, 0f).apply {
                duration = 250L
            }
            animationsList.add(bounceNegativos)
        }

        val startButtonScaleX = ObjectAnimator.ofFloat(btnStart, "scaleX", 0f, 1f).setDuration(fadeInDuration)
        val startButtonScaleY = ObjectAnimator.ofFloat(btnStart, "scaleY", 0f, 1f).setDuration(fadeInDuration)
        val startButtonAlpha = ObjectAnimator.ofFloat(btnStart, "alpha", 0f, 1f).setDuration(fadeInDuration)

        val startButtonAnimatorSet = AnimatorSet()
        startButtonAnimatorSet.playTogether(startButtonScaleX, startButtonScaleY, startButtonAlpha)
        animationsList.add(startButtonAnimatorSet)

        animatorSet.playSequentially(animationsList)
        animatorSet.start()

        btnStart.setOnClickListener {
            val scaleDownX = ObjectAnimator.ofFloat(btnStart, "scaleX", 1f, 0.9f).setDuration(50)
            val scaleDownY = ObjectAnimator.ofFloat(btnStart, "scaleY", 1f, 0.9f).setDuration(50)
            val scaleUpX = ObjectAnimator.ofFloat(btnStart, "scaleX", 0.9f, 1f).setDuration(50)
            val scaleUpY = ObjectAnimator.ofFloat(btnStart, "scaleY", 0.9f, 1f).setDuration(50)

            val clickAnimatorSet = AnimatorSet()
            clickAnimatorSet.playSequentially(scaleDownX, scaleDownY, scaleUpX, scaleUpY)
            clickAnimatorSet.start()

            clickAnimatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    val mode = responseMode
                    if (mode != null) {
                        val prefs = getSharedPreferences("MyPrefsRomas", MODE_PRIVATE)
                        prefs.edit { putString("selectedResponseModeRomasPrincipiante", mode.name) }
                    }
                    val intent = Intent(this@InstructionsActivityRomasPrincipiante, GameActivityRomasPrincipiante::class.java)
                    intent.putExtra("LEVEL", level)
                    if (mode != null) {
                        intent.putExtra("RESPONSE_MODE_ROMAS", mode.name)
                    }
                    intent.putExtra("EXCLUDED_INDEX", excludedIndex)
                    startActivity(intent)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun setupInfoBar() {
        tvGameName.text = getString(R.string.game_romas)
        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.game_name_romas_color))

        val difficultyKey = "difficulty_romas"

        val difficultyValue = sharedPreferencesRomas.getString(
            difficultyKey,
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE
        )

        val difficultyText = when(difficultyValue) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> getString(R.string.difficulty_principiante)
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> getString(R.string.difficulty_avanzado)
            DifficultySelectionActivity.DIFFICULTY_PRO -> getString(R.string.difficulty_pro)
            else -> getString(R.string.difficulty_principiante)
        }

        tvDifficulty.text = difficultyText
        tvScore.text = getString(R.string.score_format, ScoreManager.currentScoreRomasPrincipiante)

        tvDifficulty.setOnClickListener {
            val scaleDownX = ObjectAnimator.ofFloat(it, "scaleX", 1f, 0.9f).setDuration(50)
            val scaleDownY = ObjectAnimator.ofFloat(it, "scaleY", 1f, 0.9f).setDuration(50)
            val scaleUpX = ObjectAnimator.ofFloat(it, "scaleX", 0.9f, 1f).setDuration(50)
            val scaleUpY = ObjectAnimator.ofFloat(it, "scaleY", 0.9f, 1f).setDuration(50)
            val clickAnimatorSet = AnimatorSet()
            clickAnimatorSet.playSequentially(scaleDownX, scaleDownY, scaleUpX, scaleUpY)
            clickAnimatorSet.start()

            clickAnimatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    val intent = DifficultySelectionActivity.createIntent(
                        this@InstructionsActivityRomasPrincipiante,
                        "Romas",
                        true,
                        level,
                        responseMode?.name
                    )
                    startActivity(intent)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun getLevelInstructions(level: Int): String {
        return when (level) {
            in 1..10 -> getString(R.string.instructions_romans_1_10)
            in 11..20 -> getString(R.string.instructions_romans_11_20)
            in 21..30 -> getRandomExceptionInstruction(level)
            in 31..35 -> getString(R.string.instructions_romans_31_35)
            in 36..40 -> getString(R.string.instructions_romans_36_40)
            in 41..50 -> getRandomExceptionInstruction(level)
            in 51..60 -> getString(R.string.instructions_romans_51_60)
            in 61..70 -> getRandomExceptionInstruction(level)
            else -> getString(R.string.instructions_romans_default)
        }
    }

    private fun getRandomExceptionInstruction(level: Int): String {
        val exceptionMessages = listOf(
            getString(R.string.exception_romans_first),
            getString(R.string.exception_romans_second),
            getString(R.string.exception_romans_third),
            getString(R.string.exception_romans_last)
        )

        return if (level % 2 == 1) {
            when (val selectedMessage = exceptionMessages.random()) {
                getString(R.string.exception_romans_first) -> {
                    excludedIndex = 0
                    selectedMessage
                }
                getString(R.string.exception_romans_second) -> {
                    excludedIndex = 1
                    selectedMessage
                }
                getString(R.string.exception_romans_third) -> {
                    excludedIndex = 2
                    selectedMessage
                }
                getString(R.string.exception_romans_last) -> {
                    excludedIndex = -1
                    selectedMessage
                }
                else -> getString(R.string.instructions_romans_default)
            }
        } else {
            excludedIndex = null
            getString(R.string.instructions_romans_default)
        }
    }


    private fun formatTextWithBold(text: String, start: Int, end: Int): SpannableString {
        val spannable = SpannableString(text)
        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
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
