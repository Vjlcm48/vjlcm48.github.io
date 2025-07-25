package com.example.sumamente.ui

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
import com.example.sumamente.R

class InstructionsActivityDeciPlusPro : BaseActivity()  {

    private var responseMode: ResponseModeDeciPlus? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView

    private val timeLimits = mapOf(
        1 to 20.09, 2 to 19.99, 3 to 19.88, 4 to 19.78, 5 to 19.67, 6 to 19.60, 7 to 19.45,
        8 to 24.40, 9 to 24.18, 10 to 23.95, 11 to 24.15, 12 to 23.93, 13 to 23.70, 14 to 23.43,
        15 to 26.64, 16 to 26.22, 17 to 26.29, 18 to 25.96, 19 to 25.63, 20 to 25.30, 21 to 25.72,
        22 to 28.32, 23 to 27.86, 24 to 27.41, 25 to 27.95, 26 to 27.79, 27 to 27.34, 28 to 27.83,
        29 to 27.63, 30 to 27.10, 31 to 27.63, 32 to 27.10, 33 to 26.58, 34 to 26.00, 35 to 26.75,
        36 to 28.96, 37 to 28.36, 38 to 27.16, 39 to 26.56, 40 to 25.96, 41 to 27.24, 42 to 26.59,
        43 to 27.06, 44 to 26.38, 45 to 25.70, 46 to 27.23, 47 to 26.55, 48 to 25.87, 49 to 25.14,
        50 to 24.97, 51 to 27.07, 52 to 26.31, 53 to 25.54, 54 to 24.78, 55 to 24.01, 56 to 25.76,
        57 to 25.91, 58 to 25.05, 59 to 24.20, 60 to 23.34, 61 to 25.43, 62 to 24.58, 63 to 23.67,
        64 to 23.45, 65 to 22.50, 66 to 24.90, 67 to 23.95, 68 to 23.00, 69 to 22.05, 70 to 21.05
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsDeciPlus", MODE_PRIVATE)
        setContentView(R.layout.activity_instructions_deci_plus)

        tvGameName = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore = findViewById(R.id.tv_score)

        setupInfoBar()

        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnStart = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_start)
        val tvInstructions = findViewById<TextView>(R.id.tv_instructions)
        val tvLevel = findViewById<TextView>(R.id.tv_level)
        val tvTimeLimit = findViewById<TextView>(R.id.tv_time_limit)
        val tvRepeatedNumbersMessage = findViewById<TextView>(R.id.tv_repeated_numbers)
        val tvNegativeNumberWarning = findViewById<TextView>(R.id.tv_negative_numbers)

        level = intent.getIntExtra("LEVEL", 1)
        val timeLimit = timeLimits[level] ?: throw IllegalStateException("Time limit not found for level $level")

        val responseModeName = intent.getStringExtra("RESPONSE_MODE")
        if (responseModeName != null) {
            responseMode = ResponseModeDeciPlus.valueOf(responseModeName)
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

            val intent = Intent(this, LevelsActivityDeciPlusPro::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

            btnClose.isEnabled = true
        }

        val fadeInDuration = 500L
        val animationsList = mutableListOf<Animator>().apply {
            add(ObjectAnimator.ofFloat(tvLevel, "alpha", 0f, 1f).setDuration(fadeInDuration))
            add(ObjectAnimator.ofFloat(tvInstructions, "alpha", 0f, 1f).setDuration(fadeInDuration))
            add(ObjectAnimator.ofFloat(tvRepeatedNumbersMessage, "alpha", 0f, 1f).setDuration(fadeInDuration))
            add(ObjectAnimator.ofFloat(tvNegativeNumberWarning, "alpha", 0f, 1f).setDuration(fadeInDuration))
            add(ObjectAnimator.ofFloat(tvTimeLimit, "alpha", 0f, 1f).setDuration(fadeInDuration))

            if (tvRepeatedNumbersMessage.isVisible) {
                add(ObjectAnimator.ofFloat(tvRepeatedNumbersMessage, "translationY", 0f, -20f, 0f).setDuration(250L))
            }
            if (tvNegativeNumberWarning.isVisible) {
                add(ObjectAnimator.ofFloat(tvNegativeNumberWarning, "translationY", 0f, -20f, 0f).setDuration(250L))
            }

            val startButtonScaleX = ObjectAnimator.ofFloat(btnStart, "scaleX", 0f, 1f).setDuration(fadeInDuration)
            val startButtonScaleY = ObjectAnimator.ofFloat(btnStart, "scaleY", 0f, 1f).setDuration(fadeInDuration)
            val startButtonAlpha = ObjectAnimator.ofFloat(btnStart, "alpha", 0f, 1f).setDuration(fadeInDuration)

            add(AnimatorSet().apply { playTogether(startButtonScaleX, startButtonScaleY, startButtonAlpha) })
        }

        AnimatorSet().apply {
            playSequentially(animationsList)
            start()
        }

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
                        val prefs = getSharedPreferences("MyPrefsDeciPlus", MODE_PRIVATE)
                        prefs.edit { putString("selectedResponseModeDialogDeciPlusPro", mode.name) }
                    }
                    val intent = Intent(this@InstructionsActivityDeciPlusPro, GameActivityDeciPlusPro::class.java)
                    intent.putExtra("LEVEL", level)
                    if (mode != null) {
                        intent.putExtra("RESPONSE_MODE", mode.name)
                    }
                    intent.putExtra("EXCLUDED_INDEX", excludedIndex)
                    startActivity(intent)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun getLevelInstructions(level: Int): String {
        return when (level) {
            in 1..10 -> getString(R.string.instructions_level_1_10)
            in 11..20 -> getString(R.string.instructions_level_11_20)
            in 21..30 -> getRandomExceptionInstruction(level)
            in 31..35 -> getString(R.string.instructions_level_31_35)
            in 36..40 -> getString(R.string.instructions_level_36_40)
            in 41..50 -> getRandomExceptionInstruction(level)
            in 51..60 -> getString(R.string.instructions_level_51_60)
            in 61..70 -> getRandomExceptionInstruction(level)
            else -> getString(R.string.default_instructions)
        }
    }

    private fun getRandomExceptionInstruction(level: Int): String {
        val exceptionMessages = listOf(
            getString(R.string.exception_first),
            getString(R.string.exception_second),
            getString(R.string.exception_third),
            getString(R.string.exception_last)
        )

        return if (level % 2 == 1) {
            when (val selectedMessage = exceptionMessages.random()) {
                getString(R.string.exception_first) -> {
                    excludedIndex = 0
                    selectedMessage
                }
                getString(R.string.exception_second) -> {
                    excludedIndex = 1
                    selectedMessage
                }
                getString(R.string.exception_third) -> {
                    excludedIndex = 2
                    selectedMessage
                }
                getString(R.string.exception_last) -> {
                    excludedIndex = -1
                    selectedMessage
                }
                else -> getString(R.string.default_instructions)
            }
        } else {
            excludedIndex = null
            getString(R.string.default_instructions)
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

    private fun setupInfoBar() {
        tvGameName.text = getString(R.string.game_deci_plus)
        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.orange_dark))

        val difficultyKey = "difficulty_deciplus"

        val difficultyValue = sharedPreferences.getString(
            difficultyKey,
            DifficultySelectionActivity.DIFFICULTY_PRO
        )

        val difficultyText = when (difficultyValue) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> getString(R.string.difficulty_principiante)
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> getString(R.string.difficulty_avanzado)
            DifficultySelectionActivity.DIFFICULTY_PRO -> getString(R.string.difficulty_pro)
            else -> getString(R.string.difficulty_pro)
        }

        tvDifficulty.text = difficultyText

        ScoreManager.initDeciPlusPro(this)
        tvScore.text = getString(R.string.score_format, ScoreManager.currentScoreDeciPlusPro)

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
                        this@InstructionsActivityDeciPlusPro,
                        "DeciPlus",
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
}
