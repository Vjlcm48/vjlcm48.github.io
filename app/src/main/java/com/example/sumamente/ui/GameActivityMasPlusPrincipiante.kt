package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import android.util.TypedValue
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.sumamente.R
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

class GameActivityMasPlusPrincipiante : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var levelTitle: TextView
    private lateinit var logoImage: ImageView
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

    private val elementsList = mutableListOf<MASPlusElementPrincipiante>()
    private var correctAnswer = 0
    private var timePerElementList = mutableListOf<Long>()

    private val handler = Handler(Looper.getMainLooper())
    private var attempts = 0
    private var startTime: Long = 0
    private var answerTimer: CountDownTimer? = null
    private var useManualAnswer: Boolean = false
    private var excludedIndex: Int? = null
    private var chronometerTimer: CountDownTimer? = null
    private var chronometerStartTime: Long = 0
    private var heartbeatAnimator: ObjectAnimator? = null
    private var soundPlayed = false
    private var timeSpentInSeconds: Double = 0.0
    private lateinit var sharedPreferences: SharedPreferences
    private var userResponses = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsMasPlus", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_game_mas_plus)

        ScoreManager.initMasPlusPrincipiante(this)

        val prefs = getSharedPreferences("MyPrefsMasPlus", Context.MODE_PRIVATE)
        val responseMode = prefs.getString("selectedResponseModeMasPlusPrincipiante", intent.getStringExtra("RESPONSE_MODE_MASPLUS"))

        if (responseMode != null) {
            useManualAnswer = responseMode == ResponseModeMasPlusPrincipiante.TYPE_ANSWER.name
            if (!useManualAnswer) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }
        }

        excludedIndex = intent.getIntExtra("EXCLUDED_INDEX", -1)
        backArrow = findViewById(R.id.back_arrow)
        levelTitle = findViewById(R.id.tv_level)
        logoImage = findViewById(R.id.icon_central)
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
        scoreTextView.text = getString(R.string.score_label, ScoreManager.currentScoreMasPlusPrincipiante)

        chronometerTextView.typeface = Typeface.MONOSPACE

        backArrow.setOnClickListener {
            showExitConfirmation { finish() }
        }

        bottomNavHome.setOnClickListener {
            showExitConfirmation { navigateToHome() }
        }

        bottomNavChallenges.setOnClickListener {
            showExitConfirmation { }
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

        generateElementsForLevel(currentLevel)
        calculateTimePerElement()
        startSequence()
    }


    override fun onDestroy() {
        super.onDestroy()
        answerTimer?.cancel()
        handler.removeCallbacksAndMessages(null)
        chronometerTimer?.cancel()
        heartbeatAnimator?.cancel()
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

        val delayBeforeElements = 300L

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
                    showElements()
                }, delayBeforeElements)
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun generateElementsForLevel(level: Int) {
        elementsList.clear()
        generateMASPlusElementsPrincipiante(level)
    }

    private fun generateMASPlusElementsPrincipiante(level: Int) {

        val totalElements: Int
        val intRange: IntRange
        val romanCount: Int
        val letterCount: Int
        var letterPool = listOf<Char>()
        val decimalCount: Int
        var decimalRange = 0.1..0.1
        val combinationCount: Int
        val combinationTypes = mutableListOf<CombinationTypePrincipiante>()
        val negativeCount: Int

        when (level) {
            in 1..7 -> {
                totalElements = 7
                intRange = 1..10
                romanCount = 1
                letterCount = 0
                decimalCount = 0
                combinationCount = 0
                negativeCount = 2
            }
            in 8..14 -> {
                totalElements = 10
                intRange = 1..12
                romanCount = 2
                letterCount = 0
                decimalCount = 0
                combinationCount = 0
                negativeCount = 3
            }
            in 15..21 -> {
                totalElements = 12
                intRange = 1..12
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C')
                decimalCount = 0
                combinationCount = 0
                negativeCount = 2
            }
            in 22..28 -> {
                totalElements = 14
                intRange = 1..8
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D')
                decimalCount = 2
                decimalRange = 0.1..0.4
                combinationCount = 2
                combinationTypes.add(CombinationTypePrincipiante.RomanNumber)
                combinationTypes.add(CombinationTypePrincipiante.RomanRoman)
                negativeCount = 1
            }
            in 29..35 -> {
                totalElements = 15
                intRange = 1..8
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D')
                decimalCount = 2
                decimalRange = 0.1..0.5
                combinationCount = 2
                combinationTypes.add(CombinationTypePrincipiante.LetterNumber)
                combinationTypes.add(CombinationTypePrincipiante.LetterLetter)
                negativeCount = 2
            }
            in 36..42 -> {
                totalElements = 16
                intRange = 1..9
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D', 'E')
                decimalCount = 2
                decimalRange = 0.1..0.6
                combinationCount = 2
                combinationTypes.add(CombinationTypePrincipiante.NumberDecimal)
                negativeCount = 2
            }
            in 43..49 -> {
                totalElements = 17
                intRange = 1..9
                romanCount = 2
                letterCount = 3
                letterPool = listOf('A', 'B', 'C', 'D', 'E')
                decimalCount = 2
                decimalRange = 0.1..0.7
                combinationCount = 2
                combinationTypes.add(CombinationTypePrincipiante.LetterDecimal)
                negativeCount = 3
            }
            in 50..56 -> {
                totalElements = 18
                intRange = 1..10
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D', 'E', 'F')
                decimalCount = 3
                decimalRange = 0.1..0.8
                combinationCount = 2
                combinationTypes.add(CombinationTypePrincipiante.RomanLetter)
                negativeCount = 2
            }
            in 57..63 -> {
                totalElements = 19
                intRange = 1..11
                romanCount = 2
                letterCount = 3
                letterPool = listOf('A', 'B', 'C', 'D', 'E', 'F')
                decimalCount = 2
                decimalRange = 0.1..0.9
                combinationCount = 2
                combinationTypes.add(CombinationTypePrincipiante.RomanDecimal)
                negativeCount = 3
            }
            else -> {
                totalElements = 20
                intRange = 1..12
                romanCount = 3
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G')
                decimalCount = 3
                decimalRange = 0.1..0.9
                combinationCount = 2
                combinationTypes.add(CombinationTypePrincipiante.RomanLetter)
                combinationTypes.add(CombinationTypePrincipiante.RomanNumber)
                negativeCount = 3
            }
        }

        val generatedElements = mutableListOf<MASPlusElementPrincipiante>()

        repeat(romanCount) {
            val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
            val roman = convertToRoman(randomInt)
            generatedElements.add(MASPlusElementPrincipiante(roman, isNegative = false))
        }

        if (letterPool.isNotEmpty()) {
            repeat(letterCount) {
                val letter = letterPool.random()
                generatedElements.add(MASPlusElementPrincipiante(letter.toString(), isNegative = false))
            }
        }

        repeat(decimalCount) {
            val decimalValue = generateRandomDecimal(decimalRange.start, decimalRange.endInclusive)
            val formattedDecimal = String.format(Locale.getDefault(), "%.1f", decimalValue)
            generatedElements.add(MASPlusElementPrincipiante(formattedDecimal, isNegative = false))
        }

        val usedUpSoFar = romanCount + letterCount + decimalCount
        val neededBeforeCombos = totalElements - combinationCount - usedUpSoFar
        for (i in 1..neededBeforeCombos) {
            val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
            generatedElements.add(MASPlusElementPrincipiante(randomInt.toString()))
        }

        val combinationElements = generateCombinations(
            combinationCount, combinationTypes, intRange, letterPool
        )
        generatedElements.addAll(combinationElements)
        generatedElements.shuffle()

        val validIndicesForNegative = generatedElements
            .mapIndexed { index, elem -> index to elem }
            .filter { (_, elem) ->
                val isCombo = elem.value.matches(Regex("""^\(.*\)$"""))
                val isDecimal = elem.value.matches(Regex("""^-?\d+\.\d+$"""))
                !isCombo && !isDecimal
            }
            .map { it.first }

        var negativesToAssign = negativeCount
        while (negativesToAssign > 0 && validIndicesForNegative.isNotEmpty()) {
            val indexToNegate = validIndicesForNegative.random()
            val elem = generatedElements[indexToNegate]
            if (!elem.isNegative) {
                generatedElements[indexToNegate] = elem.copy(
                    value = if (elem.value.startsWith("-")) elem.value else "-${elem.value}",
                    isNegative = true
                )
                negativesToAssign--
            }
        }

        do {
            val sum = calculateSum(generatedElements)
            if (sum < 1) generatedElements.shuffle() else break
        } while (true)

        ensureNoConsecutiveDuplicates(generatedElements)
        elementsList.addAll(generatedElements)
    }

    private fun ensureNoConsecutiveDuplicates(list: MutableList<MASPlusElementPrincipiante>) {
        var index = 0
        while (index < list.size - 2) {
            val currentVal = list[index].value
            val nextVal = list[index + 1].value
            val nextNextVal = list[index + 2].value
            if (currentVal == nextVal && nextVal == nextNextVal) {
                val swapIndex = (index + 3) % list.size
                val temp = list[index + 2]
                list[index + 2] = list[swapIndex]
                list[swapIndex] = temp
                index = 0
            } else {
                index++
            }
        }
    }

    private fun calculateTimePerElement() {
        timePerElementList.clear()
        val level = currentLevel

        var baseTime = 1.90

        val blockNumber = (level - 1) / 5
        baseTime -= blockNumber * 0.05

        val levelInBlock = (level - 1) % 5
        var currentTime = baseTime

        for (i in elementsList.indices) {
            timePerElementList.add((currentTime * 1000).toLong())
            if (i > 0) {
                currentTime -= when (levelInBlock) {
                    0 -> 0.01
                    1 -> 0.015
                    2 -> 0.02
                    3 -> 0.025
                    else -> 0.03
                }
            }
            if (level % 7 == 0 && i == elementsList.size - 1) {
                currentTime -= 0.05
            }
        }
    }

    private fun showElements() {
        var index = 0
        handler.post(object : Runnable {
            override fun run() {
                if (index < elementsList.size) {
                    val masplusElement = elementsList[index]
                    val spannableString = SpannableStringBuilder(masplusElement.value)

                    if (masplusElement.isNegative) {
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

                    if (index > 0 && masplusElement.value == elementsList[index - 1].value) {
                        spannableString.setSpan(
                            ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.blue_pressed, null)),
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

                    if (masplusElement.value.matches(Regex("\\(.*\\+.*\\)"))) {
                        numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 70f)
                    } else {
                        numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 140f)
                    }

                    numberTextView.text = spannableString

                    val duration = timePerElementList[index]
                    index++
                    handler.postDelayed(this, duration)
                } else {
                    transitionToPrompt()
                }
            }
        })
    }

    private fun startProgressTimer() {
        val totalDuration = timePerElementList.sum()
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

        correctAnswer = calculateSum(elementsList)

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
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(manualAnswerEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        startHeartbeatAnimation()


        chronometerTimer = object : CountDownTimer(10000, 75) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedMillis = 10000 - millisUntilFinished
                val elapsedSeconds = elapsedMillis / 1000.0
                val formattedTime = String.format(Locale.getDefault(), "%04.2f", elapsedSeconds)
                val spannableString = SpannableString(formattedTime)
                val decimalPointIndex = formattedTime.indexOf('.')
                val endOfFraction = decimalPointIndex + 3

                spannableString.setSpan(
                    RelativeSizeSpan(0.75f),
                    decimalPointIndex,
                    endOfFraction,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val textColor = when {
                    elapsedSeconds < 5.0 -> ContextCompat.getColor(this@GameActivityMasPlusPrincipiante, R.color.green_medium)
                    elapsedSeconds < 8.0 -> ContextCompat.getColor(this@GameActivityMasPlusPrincipiante, R.color.orange_dark)
                    else -> ContextCompat.getColor(this@GameActivityMasPlusPrincipiante, R.color.red)
                }

                spannableString.setSpan(
                    ForegroundColorSpan(textColor),
                    0,
                    formattedTime.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                chronometerTextView.text = spannableString

                if (elapsedSeconds >= 8.0 && !soundPlayed) {
                    soundPlayed = true
                    playAlertSound()
                }
            }

            override fun onFinish() {
                val finalTime = "10.00"
                val spannableString = SpannableString(finalTime)
                spannableString.setSpan(
                    RelativeSizeSpan(0.75f),
                    finalTime.indexOf('.'),
                    finalTime.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@GameActivityMasPlusPrincipiante, R.color.red)),
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
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    private fun checkManualAnswer(userAnswer: Int) {
        val isCorrect = userAnswer == correctAnswer
        userResponses.add(userAnswer)
        if (isCorrect) {
            answerTimer?.cancel()
            chronometerTimer?.cancel()
            manualAnswerEditText.setBackgroundResource(R.drawable.sombra_correcta)
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
            manualAnswerEditText.startAnimation(shake)
            calculateTimeSpent()

            ScoreManager.totalGamesGlobal++
            ScoreManager.correctGamesGlobal++
            ScoreManager.totalGamesMasPlus++
            ScoreManager.totalTimeMasPlus += timeSpentInSeconds
            ScoreManager.saveStatsGlobalAndMasPlus()


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

                ScoreManager.incrementConsecutiveFailuresMasPlusPrincipiante(currentLevel)

                answerTimer?.cancel()
                chronometerTimer?.cancel()
                calculateTimeSpent()

                ScoreManager.totalGamesGlobal++
                ScoreManager.totalGamesMasPlus++
                ScoreManager.saveStatsGlobalAndMasPlus()


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

    private fun calculateTimeSpent() {
        val currentTime = System.currentTimeMillis()
        val elapsedMillis = currentTime - chronometerStartTime
        timeSpentInSeconds = elapsedMillis / 1000.0

        val formattedTime = String.format(Locale.getDefault(), "%04.2f", timeSpentInSeconds)
        val spannableString = SpannableString(formattedTime)
        val decimalPointIndex = formattedTime.indexOf('.')
        val endOfFraction = decimalPointIndex + 3

        spannableString.setSpan(
            RelativeSizeSpan(0.75f),
            decimalPointIndex,
            endOfFraction,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val textColor = when {
            timeSpentInSeconds < 5.0 -> ContextCompat.getColor(this, R.color.green_medium)
            timeSpentInSeconds < 8.0 -> ContextCompat.getColor(this, R.color.orange_dark)
            else -> ContextCompat.getColor(this, R.color.red)
        }

        spannableString.setSpan(
            ForegroundColorSpan(textColor),
            0,
            formattedTime.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        chronometerTextView.text = spannableString
    }

    private fun showAnswerButtons() {
        answerButtonsGrid.visibility = View.VISIBLE
        btnAnswer1.visibility = View.VISIBLE
        btnAnswer2.visibility = View.VISIBLE
        btnAnswer3.visibility = View.VISIBLE
        btnAnswer4.visibility = View.VISIBLE

        setAnswerValues()

        btnAnswer1.setOnClickListener { checkAnswerButton(btnAnswer1) }
        btnAnswer2.setOnClickListener { checkAnswerButton(btnAnswer2) }
        btnAnswer3.setOnClickListener { checkAnswerButton(btnAnswer3) }
        btnAnswer4.setOnClickListener { checkAnswerButton(btnAnswer4) }

        startAnswerTimer()
    }

    private fun setAnswerValues() {
        val buttons = listOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)
        val realSum = calculateSum(elementsList)
        correctAnswer = realSum

        val rangeOffset = when (currentLevel) {
            in 1..10 -> 3
            in 11..20 -> 4
            in 21..30 -> 5
            in 31..40 -> 6
            in 41..50 -> 7
            in 51..60 -> 8
            else -> 9
        }

        val incorrectAnswers = mutableSetOf<Int>()
        while (incorrectAnswers.size < 3) {
            val candidate = realSum + Random.nextInt(-rangeOffset, rangeOffset + 1)
            if (candidate != realSum && candidate !in incorrectAnswers && candidate >= 1) {
                incorrectAnswers.add(candidate)
            }
        }

        val allAnswers = incorrectAnswers.toMutableList()
        allAnswers.add(realSum)
        allAnswers.shuffle()

        for (i in buttons.indices) {
            buttons[i].text = String.format(Locale.getDefault(), "%d", allAnswers[i])
            buttons[i].setTextColor(ResourcesCompat.getColor(resources, android.R.color.black, null))
        }
    }

    private fun startAnswerTimer() {
        startTime = System.currentTimeMillis()
        answerTimer?.cancel()
        answerTimer = object : CountDownTimer(10000, 75) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                navigateToLevelResult(false)
            }
        }.start()
    }

    private fun checkAnswerButton(selectedButton: Button) {
        selectedButton.clearFocus()
        val selectedAnswer = selectedButton.text.toString().toInt()
        val isCorrect = selectedAnswer == correctAnswer
        userResponses.add(selectedAnswer)

        if (isCorrect) {
            answerTimer?.cancel()
            chronometerTimer?.cancel()
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

                ScoreManager.incrementConsecutiveFailuresMasPlusPrincipiante(currentLevel)

                answerTimer?.cancel()
                chronometerTimer?.cancel()
                calculateTimeSpent()

                ScoreManager.totalGamesGlobal++
                ScoreManager.totalGamesMasPlus++
                ScoreManager.saveStatsGlobalAndMasPlus()


                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLevelResult(false)
                }, 1000)
            }
        }
    }


    private fun showExitConfirmation(onConfirm: () -> Unit) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(R.string.btn_yes) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.btn_no, null)
        builder.create().show()
    }

    private fun navigateToLevelResult(isSuccessful: Boolean) {
        val intent = Intent(this, LevelResultActivityMasPlusPrincipiante::class.java)
        intent.putExtra("LEVEL", currentLevel)
        intent.putExtra("IS_SUCCESSFUL", isSuccessful)
        intent.putExtra("ATTEMPTS", attempts)
        intent.putExtra("TIME_SPENT", timeSpentInSeconds)

        if (isSuccessful) {
            ScoreManager.resetConsecutiveFailuresMasPlusPrincipiante(currentLevel)
        }
        else if (attempts >= 2) {
            ScoreManager.incrementConsecutiveFailuresMasPlusPrincipiante(currentLevel)
            intent.putExtra("NUMBER_LIST", elementsList.map { it.value }.toTypedArray())
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


    private fun generateRandomDecimal(min: Double, max: Double): Double {
        return Random.nextDouble(min, max)
    }

    private fun generateCombinations(
        count: Int,
        comboTypes: List<CombinationTypePrincipiante>,
        intRange: IntRange,
        letterPool: List<Char>
    ): List<MASPlusElementPrincipiante> {
        if (count <= 0) return emptyList()
        val combos = mutableListOf<MASPlusElementPrincipiante>()

        repeat(count) {
            val comboType = comboTypes.random()
            val comboString = when (comboType) {
                CombinationTypePrincipiante.RomanNumber -> {
                    val randomRomanInt = Random.nextInt(intRange.first, intRange.last + 1)
                    val roman = convertToRoman(randomRomanInt)
                    val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
                    "($roman + $randomInt)"
                }
                CombinationTypePrincipiante.RomanRoman -> {
                    val rand1 = convertToRoman(Random.nextInt(intRange.first, intRange.last + 1))
                    val rand2 = convertToRoman(Random.nextInt(intRange.first, intRange.last + 1))
                    "($rand1 + $rand2)"
                }
                CombinationTypePrincipiante.LetterNumber -> {
                    if (letterPool.isEmpty()) {
                        "(A + 1)"
                    } else {
                        val randomLetter = letterPool.random()
                        val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
                        "($randomLetter + $randomInt)"
                    }
                }
                CombinationTypePrincipiante.LetterLetter -> {
                    if (letterPool.size < 2) {
                        "(A + B)"
                    } else {
                        val l1 = letterPool.random()
                        val l2 = letterPool.random()
                        "($l1 + $l2)"
                    }
                }
                CombinationTypePrincipiante.NumberDecimal -> {
                    val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
                    val randDecimal = generateRandomDecimal(0.1, 0.9)
                    val formatted = String.format(Locale.getDefault(), "%.1f", randDecimal)
                    "($randomInt + $formatted)"
                }
                CombinationTypePrincipiante.LetterDecimal -> {
                    val randomLetter = if (letterPool.isEmpty()) 'A' else letterPool.random()
                    val randDecimal = generateRandomDecimal(0.1, 0.9)
                    val formatted = String.format(Locale.getDefault(), "%.1f", randDecimal)
                    "($randomLetter + $formatted)"
                }
                CombinationTypePrincipiante.RomanLetter -> {
                    val roman = convertToRoman(Random.nextInt(intRange.first, intRange.last + 1))
                    val randomLetter = if (letterPool.isEmpty()) 'A' else letterPool.random()
                    "($roman + $randomLetter)"
                }
                CombinationTypePrincipiante.RomanDecimal -> {
                    val roman = convertToRoman(Random.nextInt(intRange.first, intRange.last + 1))
                    val randDecimal = generateRandomDecimal(0.1, 0.9)
                    val formatted = String.format(Locale.getDefault(), "%.1f", randDecimal)
                    "($roman + $formatted)"
                }
            }
            combos.add(MASPlusElementPrincipiante(comboString))
        }
        return combos
    }

    private fun convertToRoman(number: Int): String {
        if (number == 0) return "N"
        val numAbs = abs(number)
        val numeralValues = listOf(
            1000 to "M",
            900 to "CM",
            500 to "D",
            400 to "CD",
            100 to "C",
            90 to "XC",
            50 to "L",
            40 to "XL",
            10 to "X",
            9 to "IX",
            5 to "V",
            4 to "IV",
            1 to "I"
        )

        var n = numAbs
        val result = StringBuilder()
        for ((value, numeral) in numeralValues) {
            while (n >= value) {
                result.append(numeral)
                n -= value
            }
        }
        return if (number < 0) "-$result" else result.toString()
    }

    private fun calculateSum(list: List<MASPlusElementPrincipiante>): Int {
        var total = 0.0
        for (elem in list) {
            val sign = if (elem.isNegative) -1 else 1
            val rawValue = elem.value.removePrefix("-")

            when {
                rawValue.matches(Regex("""^\d+\.\d+$""")) -> {
                    total += (rawValue.toDouble() * sign)
                }
                rawValue.matches(Regex("""^[A-G]$""")) -> {
                    val letterValue = (rawValue[0] - 'A') + 1
                    total += letterValue * sign
                }
                rawValue.matches(Regex("""^[IVXLCDM]+$""")) -> {
                    val romanInt = romanToInt(rawValue)
                    total += romanInt * sign
                }
                rawValue.matches(Regex("""^\d+$""")) -> {
                    total += rawValue.toInt() * sign
                }
                rawValue.matches(Regex("""^\(.*\)$""")) -> {
                    val inside = rawValue.removeSurrounding("(", ")")
                    val parts = inside.split(" + ")
                    if (parts.size == 2) {
                        val p1 = parseElementValue(parts[0])
                        val p2 = parseElementValue(parts[1])
                        total += (p1 + p2) * sign
                    }
                }
            }
        }
        return total.toInt()
    }

    private fun romanToInt(roman: String): Int {
        val map = mapOf(
            'I' to 1,
            'V' to 5,
            'X' to 10,
            'L' to 50,
            'C' to 100,
            'D' to 500,
            'M' to 1000
        )
        var result = 0
        var i = 0
        while (i < roman.length) {
            val value = map[roman[i]] ?: 0
            val nextValue = if (i + 1 < roman.length) map[roman[i + 1]] ?: 0 else 0
            if (nextValue > value) {
                result += (nextValue - value)
                i += 2
            } else {
                result += value
                i++
            }
        }
        return result
    }

    private fun parseElementValue(raw: String): Double {
        return when {
            raw.matches(Regex("""^\d+\.\d+$""")) -> raw.toDouble()
            raw.matches(Regex("""^[A-G]$""")) -> (raw[0] - 'A' + 1).toDouble()
            raw.matches(Regex("""^[IVXLCDM]+$""")) -> romanToInt(raw).toDouble()
            raw.matches(Regex("""^\d+$""")) -> raw.toDouble()
            else -> 0.0
        }
    }
}

data class MASPlusElementPrincipiante(
    val value: String,
    val isNegative: Boolean = false
)

enum class CombinationTypePrincipiante {
    RomanNumber,
    RomanRoman,
    LetterNumber,
    LetterLetter,
    NumberDecimal,
    LetterDecimal,
    RomanLetter,
    RomanDecimal
}
