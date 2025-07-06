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

class InstructionsActivityGenioPlusPrincipiante : BaseActivity()  {

    private var responseMode: ResponseModeGenioPlusPrincipiante? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView

    private val timeLimits = mapOf(
        1 to 22.50, 2 to 22.40, 3 to 22.30, 4 to 22.20, 5 to 22.10, 6 to 22.00, 7 to 21.90,
        8 to 25.50, 9 to 25.30, 10 to 25.10, 11 to 25.40, 12 to 25.20, 13 to 25.00, 14 to 24.80,
        15 to 27.50, 16 to 28.00, 17 to 27.60, 18 to 27.20, 19 to 26.80, 20 to 26.40, 21 to 27.00,
        22 to 29.50, 23 to 29.00, 24 to 28.50, 25 to 28.00, 26 to 28.80, 27 to 28.30, 28 to 27.80,
        29 to 28.70, 30 to 28.20, 31 to 29.50, 32 to 29.00, 33 to 28.50, 34 to 28.00, 35 to 27.50,
        36 to 30.00, 37 to 29.50, 38 to 29.00, 39 to 28.50, 40 to 28.00, 41 to 29.50, 42 to 29.00,
        43 to 29.50, 44 to 28.90, 45 to 28.30, 46 to 30.00, 47 to 29.50, 48 to 28.80, 49 to 28.20,
        50 to 28.10, 51 to 30.00, 52 to 29.30, 53 to 28.60, 54 to 27.90, 55 to 27.20, 56 to 29.00,
        57 to 28.90, 58 to 28.10, 59 to 27.30, 60 to 26.50, 61 to 28.50, 62 to 27.70, 63 to 26.80,
        64 to 26.40, 65 to 25.50, 66 to 28.00, 67 to 27.00, 68 to 26.00, 69 to 25.00, 70 to 24.00
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsGenioPlus", MODE_PRIVATE)
        setContentView(R.layout.activity_instructions_genio_plus)

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
            responseMode = ResponseModeGenioPlusPrincipiante.valueOf(responseModeName)
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

        val repeatedNumbersMessage = getString(R.string.repeated_numbers_message_dinamic, "amarillos")
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
            val prefs = getSharedPreferences("MyPrefsGenioPlus", MODE_PRIVATE)
            val storedModeName = prefs.getString("selectedResponseModeGenioPlusPrincipiante", null)
            if (storedModeName == null) {

                val intent = Intent(this, ResponseModeDialogGenioPlusPrincipiante::class.java)
                intent.putExtra("LEVEL", level)
                startActivity(intent)
                finish()

            } else {

                val intent = Intent(this, LevelsActivityGenioPlusPrincipiante::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
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
                        val prefs = getSharedPreferences("MyPrefsGenioPlus", MODE_PRIVATE)
                        prefs.edit { putString("selectedResponseModeGenioPlusPrincipiante", mode.name) }
                    }
                    val intent = Intent(this@InstructionsActivityGenioPlusPrincipiante, GameActivityGenioPlusPrincipiante::class.java)
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
        tvGameName.text = getString(R.string.game_genio_plus)
        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.blue_primary_darker))

        val difficultyKey = "difficulty_genioplus"
        val difficultyValue = sharedPreferences.getString(
            difficultyKey,
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE
        )

        val difficultyText = when (difficultyValue) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> getString(R.string.difficulty_principiante)
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> getString(R.string.difficulty_avanzado)
            DifficultySelectionActivity.DIFFICULTY_PRO -> getString(R.string.difficulty_pro)
            else -> getString(R.string.difficulty_principiante)
        }

        tvDifficulty.text = difficultyText

        ScoreManager.initGenioPlusPrincipiante(this)
        tvScore.text = getString(R.string.score_format, ScoreManager.currentScoreGenioPlusPrincipiante)

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
                        this@InstructionsActivityGenioPlusPrincipiante,
                        "GenioPlus",
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
