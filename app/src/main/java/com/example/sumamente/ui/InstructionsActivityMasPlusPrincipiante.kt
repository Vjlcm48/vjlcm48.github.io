package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.example.sumamente.R

class InstructionsActivityMasPlusPrincipiante : BaseActivity()  {

    private var responseMode: ResponseModeMasPlusPrincipiante? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView

    private val timeLimits = mapOf(
        1 to 23.59, 2 to 23.49, 3 to 23.38, 4 to 23.28, 5 to 23.17, 6 to 23.10, 7 to 22.95,
        8 to 29.40, 9 to 29.18, 10 to 28.95, 11 to 29.15, 12 to 28.93, 13 to 28.70, 14 to 28.43,
        15 to 32.84, 16 to 32.42, 17 to 32.29, 18 to 31.96, 19 to 31.63, 20 to 31.30, 21 to 31.72,
        22 to 35.32, 23 to 34.86, 24 to 34.41, 25 to 34.95, 26 to 34.79, 27 to 34.34, 28 to 34.83,
        29 to 35.13, 30 to 34.60, 31 to 35.13, 32 to 34.60, 33 to 34.08, 34 to 33.50, 35 to 34.25,
        36 to 36.36, 37 to 35.76, 38 to 35.16, 39 to 34.56, 40 to 33.96, 41 to 35.24, 42 to 34.59,
        43 to 35.56, 44 to 34.88, 45 to 34.20, 46 to 35.73, 47 to 35.05, 48 to 34.37, 49 to 33.64,
        50 to 33.47, 51 to 36.07, 52 to 35.31, 53 to 34.54, 54 to 33.78, 55 to 33.01, 56 to 34.76,
        57 to 35.41, 58 to 34.55, 59 to 33.70, 60 to 32.84, 61 to 34.93, 62 to 34.08, 63 to 33.17,
        64 to 33.45, 65 to 32.50, 66 to 34.90, 67 to 33.95, 68 to 33.00, 69 to 32.05, 70 to 31.05
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsMasPlus", MODE_PRIVATE)
        setContentView(R.layout.activity_instructions_mas_plus)

        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnStart = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_start)
        val tvInstructions = findViewById<TextView>(R.id.tv_instructions)
        val tvLevel = findViewById<TextView>(R.id.tv_level)
        val tvTimeLimit = findViewById<TextView>(R.id.tv_time_limit)
        val tvLetterConversion = findViewById<TextView>(R.id.tv_letter_conversion)
        val tvRepeatedNumbersMessage = findViewById<TextView>(R.id.tv_repeated_numbers)
        val tvNegativeNumberWarning = findViewById<TextView>(R.id.tv_negative_numbers)

        level = intent.getIntExtra("LEVEL", 1)
        val timeLimit = timeLimits[level] ?: throw IllegalStateException("Time limit not found for level $level")

        val responseModeName = intent.getStringExtra("RESPONSE_MODE")
        if (responseModeName != null) {
            responseMode = ResponseModeMasPlusPrincipiante.valueOf(responseModeName)
        }

        tvGameName = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore = findViewById(R.id.tv_score)

        setupInfoBar()

        tvLevel.text = getString(R.string.level_title, level)
        tvInstructions.text = getLevelInstructions(level)
        tvLetterConversion.text = getString(R.string.letter_to_number_conversion)

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

        val wordToHighlightBlue = getString(R.string.highlight_word_blue)
        val repeatedNumbersTemplate = getString(R.string.repeated_numbers_blue_formatted)
        val fullRepeatedNumbersMessage = String.format(repeatedNumbersTemplate, wordToHighlightBlue)

        tvRepeatedNumbersMessage.text = formatStyledText(
            fullText = fullRepeatedNumbersMessage,
            wordToStyle = wordToHighlightBlue,
            textColorResId = R.color.blue_pressed,
            backgroundResId = R.color.blue_focused
        )

        btnClose.setOnClickListener {
            btnClose.isEnabled = false

            val intent = Intent(this, LevelsActivityMasPlusPrincipiante::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

            btnClose.isEnabled = true
        }

        val fadeInDuration = 500L
        val letterConversionAnimation = ObjectAnimator.ofFloat(tvLetterConversion, "alpha", 0f, 1f).setDuration(fadeInDuration)

        val animationsList = mutableListOf<Animator>().apply {
            add(ObjectAnimator.ofFloat(tvLevel, "alpha", 0f, 1f).setDuration(fadeInDuration))
            add(ObjectAnimator.ofFloat(tvInstructions, "alpha", 0f, 1f).setDuration(fadeInDuration))
            add(ObjectAnimator.ofFloat(tvRepeatedNumbersMessage, "alpha", 0f, 1f).setDuration(fadeInDuration))
            add(letterConversionAnimation)
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
                        val prefs = getSharedPreferences("MyPrefsMasPlus", MODE_PRIVATE)
                        prefs.edit { putString("selectedResponseModeMasPlusPrincipiante", mode.name) }
                    }
                    val intent = Intent(this@InstructionsActivityMasPlusPrincipiante, GameActivityMasPlusPrincipiante::class.java)
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

    private fun setupInfoBar() {
        tvGameName.text = getString(R.string.game_mas_plus)
        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.red))

        val difficultyKey = "difficulty_masplus"

        val difficultyValue = sharedPreferences.getString(
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

        ScoreManager.initMasPlusPrincipiante(this)
        tvScore.text = getString(R.string.score_format, ScoreManager.currentScoreMasPlusPrincipiante)

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
                        this@InstructionsActivityMasPlusPrincipiante,
                        "MasPlus",
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
            in 1..10 -> getString(R.string.general_instructions)
            in 11..20 -> getString(R.string.general_instructions)
            in 21..30 -> getRandomExceptionInstruction(level)
            in 31..35 -> getString(R.string.general_instructions)
            in 36..40 -> getString(R.string.instructions_level_36_40)
            in 41..50 -> getRandomExceptionInstruction(level)
            in 51..60 -> getString(R.string.general_instructions)
            in 61..70 -> getRandomExceptionInstruction(level)
            else -> getString(R.string.general_instructions)
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
                else -> getString(R.string.general_instructions)
            }
        } else {
            excludedIndex = null
            getString(R.string.general_instructions)
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

        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }
}
