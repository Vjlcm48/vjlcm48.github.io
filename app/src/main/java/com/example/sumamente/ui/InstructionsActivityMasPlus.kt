package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.example.sumamente.R

class InstructionsActivityMasPlus : AppCompatActivity() {

    private var responseMode: ResponseModeMasPlus? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView

    private val timeLimits = mapOf(
        1 to 19.04, 2 to 18.93, 3 to 18.83, 4 to 18.73, 5 to 18.62, 6 to 18.55, 7 to 18.39,
        8 to 22.90, 9 to 22.68, 10 to 22.45, 11 to 22.65, 12 to 22.43, 13 to 22.20, 14 to 21.93,
        15 to 24.34, 16 to 24.82, 17 to 24.49, 18 to 24.16, 19 to 23.83, 20 to 23.50, 21 to 23.93,
        22 to 26.21, 23 to 25.76, 24 to 25.30, 25 to 24.85, 26 to 25.69, 27 to 25.23, 28 to 24.73,
        29 to 25.38, 30 to 24.85, 31 to 25.90, 32 to 25.38, 33 to 24.85, 34 to 24.32, 35 to 23.75,
        36 to 25.96, 37 to 25.36, 38 to 24.76, 39 to 24.16, 40 to 23.56, 41 to 24.84, 42 to 24.19,
        43 to 24.51, 44 to 23.83, 45 to 23.15, 46 to 24.68, 47 to 24.00, 48 to 23.32, 49 to 22.59,
        50 to 22.57, 51 to 24.37, 52 to 23.61, 53 to 22.84, 54 to 22.07, 55 to 21.31, 56 to 23.06,
        57 to 23.05, 58 to 22.20, 59 to 21.34, 60 to 20.49, 61 to 22.58, 62 to 21.73, 63 to 20.82,
        64 to 20.45, 65 to 19.50, 66 to 21.90, 67 to 20.95, 68 to 20.00, 69 to 19.05, 70 to 18.05
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsMasPlus", MODE_PRIVATE)
        setContentView(R.layout.activity_instructions_mas_plus)

        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnStart = findViewById<Button>(R.id.btn_start)
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

        val formattedTimeLimit = getString(R.string.time_limit_text, timeLimit)
        val startBoldIndex = formattedTimeLimit.indexOf(timeLimit.toString())
        val endBoldIndex = formattedTimeLimit.length
        val spannableTimeLimit = formatTextWithBold(formattedTimeLimit, startBoldIndex, endBoldIndex)
        tvTimeLimit.text = spannableTimeLimit

        val repeatedNumbersMessage = getString(R.string.repeated_numbers_message_more)
        tvRepeatedNumbersMessage.text = formatStyledText(
            repeatedNumbersMessage,
            R.color.blue_pressed,
            R.color.blue_focused
        )

        btnClose.setOnClickListener {
            btnClose.isEnabled = false
            val prefs = getSharedPreferences("MyPrefsMasPlus", Context.MODE_PRIVATE)
            val storedModeName = prefs.getString("selectedResponseModeMasPlus", null)
            if (storedModeName == null) {

               val intent = Intent(this, ResponseModeDialogMasPlus::class.java)
                intent.putExtra("LEVEL", level)
                startActivity(intent)
                finish()

            } else {

                val intent = Intent(this, LevelsActivityMasPlus::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
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
                        val prefs = getSharedPreferences("MyPrefsMasPlus", Context.MODE_PRIVATE)
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
        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.grey_light))

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
        text: String,
        textColorResId: Int,
        backgroundResId: Int
    ): SpannableString {
        val spannable = SpannableString(text)

        val start = 12
        val end = 18

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
