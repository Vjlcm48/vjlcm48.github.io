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

class InstructionsActivityRomasPro : BaseActivity()  {

    private var responseMode: ResponseModeRomas? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferencesRomas: android.content.SharedPreferences
    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView

    private val timeLimits = mapOf(
        1 to 17.79, 2 to 17.69, 3 to 17.58, 4 to 17.48, 5 to 17.37, 6 to 17.30, 7 to 17.14,
        8 to 21.20, 9 to 20.98, 10 to 20.75, 11 to 20.95, 12 to 20.73, 13 to 20.50, 14 to 20.23,
        15 to 22.34, 16 to 22.82, 17 to 22.49, 18 to 22.16, 19 to 21.83, 20 to 21.50, 21 to 21.93,
        22 to 23.91, 23 to 23.46, 24 to 23.00, 25 to 22.55, 26 to 23.39, 27 to 22.94, 28 to 22.43,
        29 to 22.92, 30 to 22.40, 31 to 23.45, 32 to 22.92, 33 to 22.40, 34 to 21.87, 35 to 21.30,
        36 to 23.36, 37 to 22.76, 38 to 22.16, 39 to 21.56, 40 to 20.96, 41 to 22.24, 42 to 21.59,
        43 to 21.76, 44 to 21.08, 45 to 20.40, 46 to 21.93, 47 to 21.25, 48 to 20.57, 49 to 19.84,
        50 to 19.67, 51 to 21.47, 52 to 20.71, 53 to 19.94, 54 to 19.18, 55 to 18.41, 56 to 20.16,
        57 to 20.00, 58 to 19.15, 59 to 18.30, 60 to 17.44, 61 to 19.53, 62 to 18.68, 63 to 17.77,
        64 to 17.25, 65 to 16.30, 66 to 18.70, 67 to 17.75, 68 to 16.80, 69 to 15.85, 70 to 14.85
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesRomas = getSharedPreferences("MyPrefsRomas", MODE_PRIVATE)
        setContentView(R.layout.activity_instructions_romas_pro)

        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnStart = findViewById<Button>(R.id.btn_start)
        val tvInstructions = findViewById<TextView>(R.id.tv_instructions)
        val tvLevel = findViewById<TextView>(R.id.tv_level)
        val tvTimeLimit = findViewById<TextView>(R.id.tv_time_limit)
        val tvRepeatedNumbersMessage = findViewById<TextView>(R.id.tv_repeated_numbers)
        val tvNegativeNumberWarning = findViewById<TextView>(R.id.tv_negative_numbers)

        tvGameName = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore = findViewById(R.id.tv_score)

        setupInfoBar()

        level = intent.getIntExtra("LEVEL", 1)
        val timeLimit = timeLimits[level] ?: throw IllegalStateException("Time limit not found for level $level")

        val responseModeName = intent.getStringExtra("RESPONSE_MODE_ROMAS")
        if (responseModeName != null) {
            responseMode = ResponseModeRomas.valueOf(responseModeName)
        }

        tvLevel.text = getString(R.string.level_title, level)
        tvInstructions.text = getLevelInstructions(level)

        val formattedTimeLimit = getString(R.string.time_limit_text, timeLimit)

        val startBoldIndex = formattedTimeLimit.indexOf(timeLimit.toString())
        val endBoldIndex = formattedTimeLimit.length

        val spannableTimeLimit = formatTextWithBold(formattedTimeLimit, startBoldIndex, endBoldIndex)
        tvTimeLimit.text = spannableTimeLimit

        val repeatedNumbersMessage = getString(R.string.repeated_numbers_message)
        tvRepeatedNumbersMessage.text = formatStyledText(
            repeatedNumbersMessage,
            12,
            21,
            R.color.yellow,
            R.color.blue_primary
        )

        if (level in listOf(3, 7, 10, 16, 19, 22, 25, 29, 33) || level >= 36) {
            val negativeNumberWarning = getString(R.string.negative_numbers_warning, "negativos")
            tvNegativeNumberWarning.text = formatStyledText(
                negativeNumberWarning,
                35,
                46,
                R.color.red,
                R.color.blue_primary
            )
        } else {
            tvNegativeNumberWarning.visibility = View.GONE
        }

        btnClose.setOnClickListener {
            btnClose.isEnabled = false

            val intent = Intent(this, LevelsActivityRomasPro::class.java)
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
                        prefs.edit { putString("selectedResponseModeRomasPro", mode.name) }
                    }
                    val intent = Intent(this@InstructionsActivityRomasPro, GameActivityRomasPro::class.java)
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
        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.green_medium))

        val difficultyKey = "difficulty_romas"

        val difficultyValue = sharedPreferencesRomas.getString(
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
        tvScore.text = getString(R.string.score_format, ScoreManager.currentScoreRomasPro)

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
                        this@InstructionsActivityRomasPro,
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
        text: String,
        start: Int,
        end: Int,
        textColorResId: Int,
        backgroundResId: Int
    ): SpannableString {
        val spannable = SpannableString(text)

        val textColor = ContextCompat.getColor(this, textColorResId)
        val backgroundColor = ContextCompat.getColor(this, backgroundResId)

        val roundedBackground = RadiusBackgroundSpan(backgroundColor, textColor, 10)
        spannable.setSpan(roundedBackground, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }
}
