package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sumamente.R

class InstructionsActivityGenioPlus : AppCompatActivity() {

    private var responseMode: ResponseModeGenioPlus? = null
    private var level: Int = 1
    private var excludedIndex: Int? = null

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private val timeLimits = mapOf(
        1 to 20.00, 2 to 19.90, 3 to 19.80, 4 to 19.70, 5 to 19.60, 6 to 19.50, 7 to 19.40,
        8 to 23.00, 9 to 22.80, 10 to 22.60, 11 to 22.90, 12 to 22.70, 13 to 22.50, 14 to 22.30,
        15 to 25.00, 16 to 25.50, 17 to 25.10, 18 to 24.70, 19 to 24.30, 20 to 23.90, 21 to 24.50,
        22 to 27.00, 23 to 26.50, 24 to 26.00, 25 to 25.50, 26 to 26.30, 27 to 25.80, 28 to 25.30,
        29 to 26.20, 30 to 25.70, 31 to 27.00, 32 to 26.50, 33 to 26.00, 34 to 25.50, 35 to 25.00,
        36 to 27.50, 37 to 27.00, 38 to 26.50, 39 to 26.00, 40 to 25.50, 41 to 27.00, 42 to 26.50,
        43 to 27.00, 44 to 26.40, 45 to 25.80, 46 to 27.50, 47 to 27.00, 48 to 26.30, 49 to 25.70,
        50 to 25.60, 51 to 27.50, 52 to 26.80, 53 to 26.10, 54 to 25.40, 55 to 24.70, 56 to 26.50,
        57 to 26.40, 58 to 25.60, 59 to 24.80, 60 to 24.00, 61 to 26.00, 62 to 25.20, 63 to 24.30,
        64 to 23.90, 65 to 23.00, 66 to 25.50, 67 to 24.50, 68 to 23.50, 69 to 22.50, 70 to 21.50
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsGenioPlus", Context.MODE_PRIVATE)
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
            responseMode = ResponseModeGenioPlus.valueOf(responseModeName)
        }

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
            val prefs = getSharedPreferences("MyPrefsGenioPlus", Context.MODE_PRIVATE)
            val storedModeName = prefs.getString("selectedResponseModeGenioPlus", null)
            if (storedModeName == null) {

                val intent = Intent(this, ResponseModeDialogGenioPlus::class.java)
                intent.putExtra("LEVEL", level)
                startActivity(intent)
                finish()

            } else {

                val intent = Intent(this, LevelsActivityGenioPlus::class.java)
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

        if (tvRepeatedNumbersMessage.visibility == View.VISIBLE && tvNegativeNumberWarning.visibility == View.VISIBLE) {
            val bounceAmarillos = ObjectAnimator.ofFloat(tvRepeatedNumbersMessage, "translationY", 0f, -20f, 0f).apply {
                duration = 250L
            }
            val bounceNegativos = ObjectAnimator.ofFloat(tvNegativeNumberWarning, "translationY", 0f, -20f, 0f).apply {
                duration = 250L
            }
            animationsList.add(bounceAmarillos)
            animationsList.add(bounceNegativos)
        } else if (tvRepeatedNumbersMessage.visibility == View.VISIBLE) {
            val bounceAmarillos = ObjectAnimator.ofFloat(tvRepeatedNumbersMessage, "translationY", 0f, -20f, 0f).apply {
                duration = 250L
            }
            animationsList.add(bounceAmarillos)
        } else if (tvNegativeNumberWarning.visibility == View.VISIBLE) {
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
                        val prefs = getSharedPreferences("MyPrefsGenioPlus", Context.MODE_PRIVATE)
                        prefs.edit().putString("selectedResponseModeGenioPlus", mode.name).apply()
                    }
                    val intent = Intent(this@InstructionsActivityGenioPlus, GameActivityGenioPlus::class.java)
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
