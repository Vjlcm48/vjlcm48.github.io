package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.sumamente.R
import java.util.Locale
import kotlin.random.Random

class GameActivitySumaRestaPro : BaseActivity()  {

    private lateinit var backArrow: ImageView
    private lateinit var levelTitle: TextView
    private lateinit var bottomNavHome: ImageView
    private lateinit var bottomNavChallenges: ImageView
    private lateinit var bottomNavStatistics: ImageView
    private lateinit var progressRing: ProgressRingView
    private lateinit var numberTextView: TextView
    private lateinit var promptTextView: TextView
    private lateinit var answerButtonsGrid: View
    private lateinit var btnAnswer1: Button
    private lateinit var btnAnswer2: Button
    private lateinit var btnAnswer3: Button
    private lateinit var btnAnswer4: Button
    private lateinit var scoreTextView: TextView
    private lateinit var manualInputLayout: View
    private lateinit var manualAnswerEditText: EditText
    private lateinit var submitAnswerButton: Button
    private lateinit var blueCircle: View
    private lateinit var vamosTextView: TextView
    private lateinit var chronometerTextView: TextView

    private var currentLevel = 1
    private var numberList = mutableListOf<Int>()
    private val handler = Handler(Looper.getMainLooper())
    private var correctAnswer = 0
    private var attempts = 0
    private var answerTimer: CountDownTimer? = null
    private var useManualAnswer: Boolean = false
    private var timePerNumberList = mutableListOf<Long>()
    private var excludedIndex: Int? = null
    private var chronometerTimer: CountDownTimer? = null
    private var chronometerStartTime: Long = 0
    private var heartbeatAnimator: ObjectAnimator? = null
    private var soundPlayed = false
    private var timeSpentInSeconds: Double = 0.0
    private var userResponses = mutableListOf<Int>()
    private var inputBlocked = false // Cambio #1 bloqueo de mas de 2 intentos //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSharedPreferences("MyPrefsSumaResta", MODE_PRIVATE)
        setContentView(R.layout.activity_game_suma_resta)

        ScoreManager.initSumaRestaPro(this)

        val prefs = getSharedPreferences("MyPrefsSumaResta", MODE_PRIVATE)
        val responseMode = prefs.getString("selectedResponseModeSumaRestaPro", intent.getStringExtra("RESPONSE_MODE"))

        if (responseMode != null) {
            useManualAnswer = responseMode == ResponseModeSumaResta.TYPE_ANSWER.name
            if (!useManualAnswer) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }
        }

        excludedIndex = intent.getIntExtra("EXCLUDED_INDEX", -1)
        backArrow = findViewById(R.id.back_arrow)
        levelTitle = findViewById(R.id.tv_level)
        bottomNavHome = findViewById(R.id.home_icon)
        bottomNavChallenges = findViewById(R.id.calendar_icon)
        bottomNavStatistics = findViewById(R.id.statistics_icon)
        progressRing = findViewById(R.id.progress_ring)
        numberTextView = findViewById(R.id.tv_number)
        promptTextView = findViewById(R.id.tv_prompt)
        answerButtonsGrid = findViewById(R.id.answer_buttons_grid)
        btnAnswer1 = findViewById(R.id.btn_answer_1)
        btnAnswer2 = findViewById(R.id.btn_answer_2)
        btnAnswer3 = findViewById(R.id.btn_answer_3)
        btnAnswer4 = findViewById(R.id.btn_answer_4)
        scoreTextView = findViewById(R.id.tv_score)
        manualInputLayout = findViewById(R.id.manual_input_layout)
        manualAnswerEditText = findViewById(R.id.et_manual_answer)
        submitAnswerButton = findViewById(R.id.btn_submit_answer)
        blueCircle = findViewById(R.id.blue_circle)
        vamosTextView = findViewById(R.id.tv_vamos)
        chronometerTextView = findViewById(R.id.chronometer_text_view)
        chronometerTextView.typeface = Typeface.MONOSPACE
        currentLevel = intent.getIntExtra("LEVEL", 1)
        levelTitle.text = getString(R.string.level_title, currentLevel)
        scoreTextView.text = getString(R.string.score_label, ScoreManager.currentScoreSumaRestaPro)

        backArrow.setOnClickListener {
            showExitConfirmation { finish() }
        }

        bottomNavHome.setOnClickListener {
            showExitConfirmation { navigateToHome() }
        }

        bottomNavChallenges.setOnClickListener {
            showExitConfirmation { navigateToChallenges() }
        }

        bottomNavStatistics.setOnClickListener {
            showExitConfirmation { navigateToStatistics() }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmation { finish() }
            }
        })

        attempts = 0
        inputBlocked = false  // Cambio #2 bloqueo de mas de 2 intentos //
        generateNumbers()
        calculateTimePerNumber()
        startSequence()
    }

    override fun onDestroy() {
        super.onDestroy()
        answerTimer?.cancel()
        handler.removeCallbacksAndMessages(null)
        chronometerTimer?.cancel()
        heartbeatAnimator?.cancel()
    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing) {

            answerTimer?.cancel()
            handler.removeCallbacksAndMessages(null)
            chronometerTimer?.cancel()
            heartbeatAnimator?.cancel()
            finish()
        }
    }

    private fun startSequence() {
        progressRing.visibility = View.INVISIBLE
        blueCircle.visibility = View.INVISIBLE
        numberTextView.visibility = View.INVISIBLE
        vamosTextView.visibility = View.INVISIBLE
        promptTextView.visibility = View.INVISIBLE
        answerButtonsGrid.visibility = View.INVISIBLE
        manualInputLayout.visibility = View.INVISIBLE
        chronometerTextView.visibility = View.GONE
        blueCircle.scaleX = 0f
        blueCircle.scaleY = 0f
        blueCircle.visibility = View.VISIBLE

        val circleAnimation = ObjectAnimator.ofPropertyValuesHolder(
            blueCircle,
            PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
        ).setDuration(500)

        progressRing.scaleX = 0f
        progressRing.scaleY = 0f
        progressRing.visibility = View.VISIBLE

        val ringAnimation = ObjectAnimator.ofPropertyValuesHolder(
            progressRing,
            PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
        ).setDuration(500)

        vamosTextView.scaleX = 0f
        vamosTextView.scaleY = 0f
        vamosTextView.visibility = View.VISIBLE

        val vamosAnimation = ObjectAnimator.ofPropertyValuesHolder(
            vamosTextView,
            PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
        ).setDuration(1000)

        val holdVamos = ObjectAnimator.ofFloat(vamosTextView, "alpha", 1f, 1f).apply {
            duration = 500
        }

        val fadeOutVamos = ObjectAnimator.ofFloat(vamosTextView, "alpha", 1f, 0f).apply {
            duration = 0
        }

        val delayBeforeNumbers = 300L

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            AnimatorSet().apply { playTogether(circleAnimation, ringAnimation) },
            vamosAnimation,
            holdVamos,
            fadeOutVamos
        )
        animatorSet.start()

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                vamosTextView.visibility = View.GONE
                handler.postDelayed({
                    numberTextView.visibility = View.VISIBLE
                    startProgressTimer()
                    showNumbers()
                }, delayBeforeNumbers)
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun generateNumbers() {
        numberList.clear()

        val level = currentLevel

        val smallNumbers = mutableListOf<Int>()
        val largeNumbers = mutableListOf<Int>()
        val smallNegatives = mutableListOf<Int>()
        val largeNegatives = mutableListOf<Int>()

        when (level) {
            in 1..7 -> {
                smallNumbers.addAll(generateRandomNumbers(3, 1..10))
                largeNumbers.addAll(generateRandomNumbers(2, 11..16))
                smallNegatives.addAll(generateRandomNumbers(2, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(1, -14..-6))
            }
            in 8..14 -> {
                smallNumbers.addAll(generateRandomNumbers(4, 1..10))
                largeNumbers.addAll(generateRandomNumbers(3, 11..16))
                smallNegatives.addAll(generateRandomNumbers(2, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(2, -14..-6))
            }
            in 15..21 -> {
                smallNumbers.addAll(generateRandomNumbers(5, 1..10))
                largeNumbers.addAll(generateRandomNumbers(3, 11..16))
                smallNegatives.addAll(generateRandomNumbers(3, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(2, -14..-6))
            }
            in 22..28 -> {
                smallNumbers.addAll(generateRandomNumbers(5, 1..10))
                largeNumbers.addAll(generateRandomNumbers(4, 11..16))
                smallNegatives.addAll(generateRandomNumbers(3, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(3, -14..-6))
            }
            in 29..35 -> {
                smallNumbers.addAll(generateRandomNumbers(4, 1..10))
                largeNumbers.addAll(generateRandomNumbers(5, 11..16))
                smallNegatives.addAll(generateRandomNumbers(4, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(3, -14..-6))
            }
            in 36..42 -> {
                smallNumbers.addAll(generateRandomNumbers(8, 1..12))
                largeNumbers.addAll(generateRandomNumbers(1, 13..20))
                smallNegatives.addAll(generateRandomNumbers(4, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(4, -16..-6))
            }
            in 43..49 -> {
                smallNumbers.addAll(generateRandomNumbers(8, 1..12))
                largeNumbers.addAll(generateRandomNumbers(1, 13..20))
                smallNegatives.addAll(generateRandomNumbers(5, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(4, -16..-6))
            }
            in 50..56 -> {
                smallNumbers.addAll(generateRandomNumbers(8, 1..14))
                largeNumbers.addAll(generateRandomNumbers(1, 15..22))
                smallNegatives.addAll(generateRandomNumbers(5, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(5, -16..-6))
            }
            in 57..63 -> {
                smallNumbers.addAll(generateRandomNumbers(8, 1..14))
                largeNumbers.addAll(generateRandomNumbers(1, 15..22))
                smallNegatives.addAll(generateRandomNumbers(6, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(5, -16..-6))
            }
            in 64..70 -> {
                smallNumbers.addAll(generateRandomNumbers(8, 1..14))
                largeNumbers.addAll(generateRandomNumbers(1, 15..22))
                smallNegatives.addAll(generateRandomNumbers(6, -5..-1))
                largeNegatives.addAll(generateRandomNumbers(6, -16..-6))
            }
        }

        numberList.addAll(smallNumbers)
        numberList.addAll(largeNumbers)
        numberList.addAll(smallNegatives)
        numberList.addAll(largeNegatives)

        numberList.shuffle()
        ensureNoConsecutiveDuplicates()

        while (calculateSum() < -40) {
            numberList.shuffle()
        }
    }

    private fun ensureNoConsecutiveDuplicates() {
        var index = 0
        while (index < numberList.size - 2) {
            if (numberList[index] == numberList[index + 1] && numberList[index + 1] == numberList[index + 2]) {
                val swapIndex = (index + 3) % numberList.size
                val temp = numberList[index + 2]
                numberList[index + 2] = numberList[swapIndex]
                numberList[swapIndex] = temp
                index = 0
            } else {
                index++
            }
        }
    }

    private fun calculateSum(): Int {
        var sum = numberList.sum()
        if (excludedIndex != null && excludedIndex!! in numberList.indices) {
            sum -= numberList[excludedIndex!!]
        }
        return sum
    }

    private fun generateRandomNumbers(count: Int, range: IntRange): MutableList<Int> {
        val numbers = mutableListOf<Int>()
        repeat(count) {
            numbers.add(Random.nextInt(range.first, range.last + 1))
        }
        return numbers
    }

    private fun calculateTimePerNumber() {
        timePerNumberList.clear()
        val level = currentLevel

        var firstNumberTime = 1.80

        val blockNumber = (level - 1) / 5
        firstNumberTime -= blockNumber * 0.07

        val levelInBlock = (level - 1) % 5

        var currentTime = firstNumberTime

        for (i in numberList.indices) {
            timePerNumberList.add((currentTime * 1000).toLong())

            if (i > 0) {
                currentTime -= when (levelInBlock) {
                    0 -> 0.01
                    1 -> 0.015
                    2 -> 0.02
                    3 -> 0.025
                    else -> 0.03
                }
            }

            if (level % 7 == 0 && i == numberList.size - 1) {
                currentTime -= 0.05
            }
        }
    }

    private fun showNumbers() {
        var index = 0

        handler.post(object : Runnable {
            override fun run() {
                if (index < numberList.size) {
                    val number = numberList[index]
                    val spannableString = SpannableStringBuilder(number.toString())

                    if (number < 0) {
                        spannableString.setSpan(
                            ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.red, null)),
                            0,
                            spannableString.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        spannableString.setSpan(
                            ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.black, null)),
                            0,
                            spannableString.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

                    if (index > 0 && number == numberList[index - 1]) {
                        spannableString.setSpan(
                            ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.yellow, null)),
                            0,
                            spannableString.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        handler.postDelayed({
                            val animation = ObjectAnimator.ofFloat(numberTextView, "translationY", 0f, -10f, 0f)
                            animation.duration = 200
                            animation.start()
                        }, 200)
                    }

                    numberTextView.text = spannableString

                    val duration = timePerNumberList[index]

                    index++
                    handler.postDelayed(this, duration)
                } else {
                    transitionToPrompt()
                }
            }
        })
    }

    private fun startProgressTimer() {
        val totalDuration = timePerNumberList.sum()
        progressRing.startProgressAnimation(totalDuration)
    }

    private fun transitionToPrompt() {
        numberTextView.visibility = View.GONE
        progressRing.visibility = View.GONE
        blueCircle.visibility = View.GONE
        promptTextView.visibility = View.VISIBLE
        promptTextView.text = getString(R.string.prompt_choose_correct_answer)

        chronometerTextView.visibility = View.VISIBLE
        startChronometer()

        correctAnswer = calculateSum()

        if (useManualAnswer) {
            showManualInput()
        } else {
            showAnswerButtons()
        }
    }

    private fun startChronometer() {
        chronometerStartTime = System.currentTimeMillis()
        soundPlayed = false

        chronometerTextView.visibility = View.VISIBLE

        if (useManualAnswer) {
            manualAnswerEditText.post {
                manualAnswerEditText.requestFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(manualAnswerEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        startHeartbeatAnimation()

        chronometerTimer = object : CountDownTimer(5000, 75) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedMillis = 5000 - millisUntilFinished
                val elapsedSeconds = elapsedMillis / 1000.0

                // GA1 Cambio para solucionar el formato de los decimales //
                val formattedTime = String.format(Locale.getDefault(), "%04.2f", elapsedSeconds)
                val spannableString = SpannableString(formattedTime)
                val decimalPointIndex = formattedTime.indexOf('.')
                val decimalCommaIndex = formattedTime.indexOf(',')
                val decimalSeparatorIndex = if (decimalPointIndex >= 0) decimalPointIndex else decimalCommaIndex

                if (decimalSeparatorIndex >= 0 && decimalSeparatorIndex + 3 <= formattedTime.length) {
                    val endOfFraction = decimalSeparatorIndex + 3
                    spannableString.setSpan(
                        RelativeSizeSpan(0.75f),
                        decimalSeparatorIndex,
                        endOfFraction,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                // Fin del cambio GA1 //

                val textColor = when {
                    elapsedSeconds < 2.0 -> ContextCompat.getColor(this@GameActivitySumaRestaPro, R.color.green_medium)
                    elapsedSeconds < 3.5 -> ContextCompat.getColor(this@GameActivitySumaRestaPro, R.color.orange_dark)
                    else -> ContextCompat.getColor(this@GameActivitySumaRestaPro, R.color.red)
                }

                spannableString.setSpan(
                    ForegroundColorSpan(textColor),
                    0,
                    formattedTime.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                chronometerTextView.text = spannableString

                if (elapsedSeconds >= 3.5 && !soundPlayed) {
                    soundPlayed = true
                    playAlertSound()
                }
            }

            override fun onFinish() {
                val finalTime = "5.00"
                val spannableString = SpannableString(finalTime)

                spannableString.setSpan(
                    RelativeSizeSpan(0.75f),
                    finalTime.indexOf('.'),
                    finalTime.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@GameActivitySumaRestaPro, R.color.red)),
                    0,
                    finalTime.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                chronometerTextView.text = spannableString

                performFinalHeartbeatAndDisappear()
                navigateToLevelResult(false)
            }
        }.start()
    }

    private fun startHeartbeatAnimation() {
        val scaleUpX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f)
        val scaleUpY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f)

        heartbeatAnimator = ObjectAnimator.ofPropertyValuesHolder(chronometerTextView, scaleUpX, scaleUpY).apply {
            duration = 600
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
    }

    private fun performFinalHeartbeatAndDisappear() {
        heartbeatAnimator?.cancel()

        val scaleUpX = ObjectAnimator.ofFloat(chronometerTextView, "scaleX", 1f, 1.2f)
        val scaleUpY = ObjectAnimator.ofFloat(chronometerTextView, "scaleY", 1f, 1.2f)
        scaleUpX.duration = 150
        scaleUpY.duration = 150

        val scaleDownX = ObjectAnimator.ofFloat(chronometerTextView, "scaleX", 1.2f, 0f)
        val scaleDownY = ObjectAnimator.ofFloat(chronometerTextView, "scaleY", 1.2f, 0f)
        scaleDownX.duration = 150
        scaleDownY.duration = 150

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleUpX).with(scaleUpY)
        animatorSet.play(scaleDownX).with(scaleDownY).after(scaleUpX)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                chronometerTextView.visibility = View.GONE
                chronometerTextView.scaleX = 1f
                chronometerTextView.scaleY = 1f
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSet.start()
    }

    private fun playAlertSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.sonidoerror)
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.start()
    }

    private fun showManualInput() {
        manualInputLayout.visibility = View.VISIBLE

        if (useManualAnswer) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            manualAnswerEditText.requestFocus()

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(manualAnswerEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        manualAnswerEditText.setOnEditorActionListener { _, _, _ ->
            submitManualAnswer()
            true
        }

        submitAnswerButton.setOnClickListener {
            submitManualAnswer()
        }

        startAnswerTimer()
    }

    private fun submitManualAnswer() {
        val userAnswer = manualAnswerEditText.text.toString().toIntOrNull()
        if (userAnswer != null) {
            checkManualAnswer(userAnswer)
        } else {
            Toast.makeText(this, getString(R.string.invalid_manual_answer), Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateTimeSpent() {
        val currentTime = System.currentTimeMillis()
        val elapsedMillis = currentTime - chronometerStartTime
        timeSpentInSeconds = elapsedMillis / 1000.0

        // GA2 Cambio para solucionar el formato de los decimales //
        val formattedTime = String.format(Locale.getDefault(), "%04.2f", timeSpentInSeconds)
        val spannableString = SpannableString(formattedTime)
        val decimalPointIndex = formattedTime.indexOf('.')
        val decimalCommaIndex = formattedTime.indexOf(',')
        val decimalSeparatorIndex = if (decimalPointIndex >= 0) decimalPointIndex else decimalCommaIndex

        if (decimalSeparatorIndex >= 0 && decimalSeparatorIndex + 3 <= formattedTime.length) {
            val endOfFraction = decimalSeparatorIndex + 3
            spannableString.setSpan(
                RelativeSizeSpan(0.75f),
                decimalSeparatorIndex,
                endOfFraction,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        val textColor = when {
            timeSpentInSeconds < 3.0 -> ContextCompat.getColor(this, R.color.green_medium)
            timeSpentInSeconds < 5.0 -> ContextCompat.getColor(this, R.color.orange_dark)
            else -> ContextCompat.getColor(this, R.color.red)
        }
        spannableString.setSpan(
            ForegroundColorSpan(textColor),
            0,
            formattedTime.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        chronometerTextView.text = spannableString
        // Fin del cambio GA2 //
    }

    private fun checkManualAnswer(userAnswer: Int) {
        if (inputBlocked) return // Cambio #3 bloqueo de mas de 2 intentos //
        val isCorrect = userAnswer == correctAnswer
        userResponses.add(userAnswer)

        if (isCorrect) {
            answerTimer?.cancel()
            chronometerTimer?.cancel()

            inputBlocked = true  // Cambio #02 para bloqueo después de respuesta correcta
            disableAllInputs()   // Cambio #02 para bloqueo después de respuesta correcta

            manualAnswerEditText.setBackgroundResource(R.drawable.sombra_correcta)
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
            manualAnswerEditText.startAnimation(shake)

            calculateTimeSpent()

            Handler(Looper.getMainLooper()).postDelayed({
                navigateToLevelResult(true)
            }, 1500)
        } else {
            manualAnswerEditText.text.clear()
            val originalBackground = manualAnswerEditText.background
            manualAnswerEditText.setBackgroundResource(R.drawable.sombra_incorrecta)
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
            manualAnswerEditText.startAnimation(shake)

            attempts++
            if (attempts >= 2) {
                inputBlocked = true  // Cambio #4 bloqueo de mas de 2 intentos //
                disableAllInputs() // Cambio #4 bloqueo de mas de 2 intentos //
                answerTimer?.cancel()
                chronometerTimer?.cancel()

                calculateTimeSpent()

                Handler(Looper.getMainLooper()).postDelayed({
                    manualAnswerEditText.background = originalBackground

                    Handler(Looper.getMainLooper()).postDelayed({
                        navigateToLevelResult(false)
                    }, 1000)

                }, shake.duration)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    manualAnswerEditText.background = originalBackground
                }, shake.duration)
            }
        }
    }

    private fun showAnswerButtons() {
        answerButtonsGrid.visibility = View.VISIBLE
        btnAnswer1.visibility = View.VISIBLE
        btnAnswer2.visibility = View.VISIBLE
        btnAnswer3.visibility = View.VISIBLE
        btnAnswer4.visibility = View.VISIBLE

        setAnswerValues()

        btnAnswer1.setOnClickListener { checkAnswer(btnAnswer1) }
        btnAnswer2.setOnClickListener { checkAnswer(btnAnswer2) }
        btnAnswer3.setOnClickListener { checkAnswer(btnAnswer3) }
        btnAnswer4.setOnClickListener { checkAnswer(btnAnswer4) }

        startAnswerTimer()
    }

    private fun setAnswerValues() {
        val buttons = listOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)
        correctAnswer = calculateSum()

        val incorrectAnswers = mutableSetOf<Int>()
        val rangeOffset = when (currentLevel) {
            in 1..10 -> 3
            in 11..20 -> 4
            in 21..30 -> 5
            in 31..40 -> 6
            in 41..50 -> 7
            in 51..60 -> 8
            else -> 9
        }

        while (incorrectAnswers.size < 3) {
            val incorrectAnswer = correctAnswer + Random.nextInt(-rangeOffset, rangeOffset + 1)
            if (incorrectAnswer != correctAnswer && incorrectAnswer !in incorrectAnswers) {
                incorrectAnswers.add(incorrectAnswer)
            }
        }

        val allAnswers = incorrectAnswers.toMutableList()
        allAnswers.add(correctAnswer)
        allAnswers.shuffle()

        for (i in buttons.indices) {
            buttons[i].text = String.format(Locale.getDefault(), "%d", allAnswers[i])
            buttons[i].setTextColor(ResourcesCompat.getColor(resources, android.R.color.black, null))
        }
    }

    private fun startAnswerTimer() {
        System.currentTimeMillis()
        answerTimer?.cancel()
        answerTimer = object : CountDownTimer(5000, 75) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                navigateToLevelResult(false)
            }
        }.start()
    }

    private fun checkAnswer(selectedButton: Button) {
        if (inputBlocked) return // Cambio #5 bloqueo de mas de 2 intentos //
        selectedButton.clearFocus()

        val selectedAnswer = selectedButton.text.toString().toInt()
        val isCorrect = selectedAnswer == correctAnswer
        userResponses.add(selectedAnswer)

        if (isCorrect) {
            answerTimer?.cancel()
            chronometerTimer?.cancel()

            inputBlocked = true  // Cambio #02 para bloqueo después de respuesta correcta
            disableAllInputs()   // Cambio #02 para bloqueo después de respuesta correcta

            selectedButton.setBackgroundResource(R.drawable.sombra_correcta)
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
            selectedButton.startAnimation(shake)

            calculateTimeSpent()

            Handler(Looper.getMainLooper()).postDelayed({
                navigateToLevelResult(true)
            }, 1500)
        } else {
            val originalBackground = selectedButton.background
            selectedButton.setBackgroundResource(R.drawable.sombra_incorrecta)
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
            selectedButton.startAnimation(shake)

            Handler(Looper.getMainLooper()).postDelayed({
                selectedButton.background = originalBackground
            }, 500)

            attempts++
            if (attempts >= 2) {
                inputBlocked = true // Cambio #6 bloqueo de mas de 2 intentos //
                disableAllInputs() // Cambio #6 bloqueo de mas de 2 intentos //
                answerTimer?.cancel()
                chronometerTimer?.cancel()

                calculateTimeSpent()

                ScoreManager.incrementConsecutiveFailuresSumaRestaPro(currentLevel)

                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLevelResult(false)
                }, 1000)
            }
        }
    }

    // Cambio #7 bloqueo de mas de 2 intentos //
    private fun disableAllInputs() {

        btnAnswer1.isEnabled = false
        btnAnswer2.isEnabled = false
        btnAnswer3.isEnabled = false
        btnAnswer4.isEnabled = false

        manualAnswerEditText.isEnabled = false
        submitAnswerButton.isEnabled = false
    }

    private fun showExitConfirmation(onConfirm: () -> Unit) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(R.string.btn_yes) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.btn_no, null)
        builder.create().show()
    }

    private fun navigateToLevelResult(isSuccessful: Boolean) {
        val intent = Intent(this, LevelResultActivitySumaRestaPro::class.java)
        intent.putExtra("LEVEL", currentLevel)
        intent.putExtra("IS_SUCCESSFUL", isSuccessful)
        intent.putExtra("ATTEMPTS", attempts)
        intent.putExtra("TIME_SPENT", timeSpentInSeconds)

        if (isSuccessful) {
            ScoreManager.resetConsecutiveFailuresSumaRestaPro(currentLevel)
        }
        else if (attempts >= 2) {
            intent.putExtra("NUMBER_LIST", numberList.toIntArray())
            intent.putExtra("CORRECT_ANSWER", correctAnswer)
            intent.putExtra("EXCLUDED_INDEX", excludedIndex ?: -1)
            intent.putExtra("USER_RESPONSES", userResponses.toIntArray())
        }

        intent.putExtra("USE_MANUAL_ANSWER", useManualAnswer)

        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainGameActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    private fun navigateToStatistics() {
        val intent = Intent(this, ClassificationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToChallenges() {
        val intent = Intent(this, DesafiosActivity::class.java)
        startActivity(intent)
        finish()
    }
}

