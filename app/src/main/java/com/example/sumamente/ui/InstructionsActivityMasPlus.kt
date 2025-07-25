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

class InstructionsActivityMasPlus : BaseActivity()  {

    private var responseMode: ResponseModeMasPlus? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView

    private val timeLimits = mapOf(
        1 to 21.84, 2 to 21.74, 3 to 21.63, 4 to 21.53, 5 to 21.42, 6 to 21.35, 7 to 21.20,
        8 to 26.90, 9 to 26.68, 10 to 26.45, 11 to 26.65, 12 to 26.43, 13 to 26.20, 14 to 25.93,
        15 to 29.64, 16 to 29.22, 17 to 29.29, 18 to 28.96, 19 to 28.63, 20 to 28.30, 21 to 28.72,
        22 to 31.82, 23 to 31.36, 24 to 30.91, 25 to 31.45, 26 to 31.29, 27 to 30.84, 28 to 31.33,
        29 to 31.38, 30 to 30.85, 31 to 31.38, 32 to 30.85, 33 to 30.33, 34 to 29.75, 35 to 30.50,
        36 to 32.36, 37 to 31.76, 38 to 31.16, 39 to 30.56, 40 to 29.96, 41 to 31.24, 42 to 30.59,
        43 to 31.56, 44 to 30.88, 45 to 29.95, 46 to 31.48, 47 to 30.80, 48 to 30.12, 49 to 29.39,
        50 to 29.22, 51 to 31.57, 52 to 30.81, 53 to 30.04, 54 to 29.28, 55 to 28.51, 56 to 30.26,
        57 to 30.66, 58 to 29.80, 59 to 28.95, 60 to 28.09, 61 to 30.18, 62 to 29.33, 63 to 28.42,
        64 to 28.45, 65 to 27.50, 66 to 29.90, 67 to 28.95, 68 to 28.00, 69 to 27.05, 70 to 26.05
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
        val tvRepeatedNumbersMessage = findViewById<TextView>(R.id.tv_repeated_numbers)
        val tvNegativeNumberWarning = findViewById<TextView>(R.id.tv_negative_numbers)

        level = intent.getIntExtra("LEVEL", 1)
        val timeLimit = timeLimits[level] ?: throw IllegalStateException("Time limit not found for level $level")

        val responseModeName = intent.getStringExtra("RESPONSE_MODE")
        if (responseModeName != null) {
            responseMode = ResponseModeMasPlus.valueOf(responseModeName)
        }

        tvGameName = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore = findViewById(R.id.tv_score)

        setupInfoBar()

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

            val intent = Intent(this, LevelsActivityMasPlus::class.java)
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
                        val prefs = getSharedPreferences("MyPrefsMasPlus", MODE_PRIVATE)
                        prefs.edit { putString("selectedResponseModeMasPlus", mode.name) }
                    }
                    val intent = Intent(this@InstructionsActivityMasPlus, GameActivityMasPlus::class.java)
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
            DifficultySelectionActivity.DIFFICULTY_AVANZADO
        )

        val difficultyText = when(difficultyValue) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> getString(R.string.difficulty_principiante)
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> getString(R.string.difficulty_avanzado)
            DifficultySelectionActivity.DIFFICULTY_PRO -> getString(R.string.difficulty_pro)
            else -> getString(R.string.difficulty_avanzado)
        }

        tvDifficulty.text = difficultyText

        ScoreManager.initMasPlus(this)
        tvScore.text = getString(R.string.score_format, ScoreManager.currentScoreMasPlus)

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
                        this@InstructionsActivityMasPlus,
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
