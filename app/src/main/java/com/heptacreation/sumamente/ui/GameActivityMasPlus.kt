package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
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
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.heptacreation.sumamente.R
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

class GameActivityMasPlus : BaseActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var levelTitle: TextView
    private lateinit var bottomNavHome: TextView
    private lateinit var bottomNavChallenges: TextView
    private lateinit var bottomNavStatistics: TextView
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
    private lateinit var progressRingContainer: View

    // Vistas del sistema de pistas
    private lateinit var hintOverlay: FrameLayout
    private lateinit var tvHintBalance: TextView
    private lateinit var tvHintDescription: TextView
    private lateinit var btnUseHint: MaterialButton
    private lateinit var btnSkipHint: MaterialButton
    private lateinit var hintTimerBar: ProgressBar
    private lateinit var hintOptionsLayout: LinearLayout
    private lateinit var btnHintOption1: MaterialButton
    private lateinit var btnHintOption2: MaterialButton

    private var currentLevel = 1

    private val elementsList = mutableListOf<MASPlusElement>()
    private var correctAnswer = 0
    private var userResponses: MutableList<Int> = mutableListOf()

    private var timePerElementList = mutableListOf<Long>()

    private val handler = Handler(Looper.getMainLooper())
    private var attempts = 0
    private var answerTimer: CountDownTimer? = null
    private var useManualAnswer: Boolean = false
    private var excludedIndex: Int? = null
    private var chronometerTimer: CountDownTimer? = null
    private var chronometerStartTime: Long = 0
    private var heartbeatAnimator: ObjectAnimator? = null
    private var soundPlayed = false
    private var timeSpentInSeconds: Double = 0.0
    private var inputBlocked = false

    // Variables del sistema de pistas
    private var pistaActivada = false
    private var hintTimerAnimator: ObjectAnimator? = null
    private var hintCountDownTimer: CountDownTimer? = null

    companion object {
        private const val HINT_COST_COINS = 4
        private const val HINT_TIMER_MS = 5000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        getSharedPreferences("MyPrefsMasPlus", MODE_PRIVATE)
        setContentView(R.layout.activity_game_mas_plus)

        AdManager.preloadInterstitial(this)

        ScoreManager.initMasPlus(this)

        val prefs = getSharedPreferences("MyPrefsMasPlus", MODE_PRIVATE)
        val responseMode = prefs.getString(
            "selectedResponseModeMasPlus",
            intent.getStringExtra("RESPONSE_MODE_MASPLUS")
        )

        if (responseMode != null) {
            useManualAnswer = responseMode == ResponseMode.TYPE_ANSWER.name
            if (!useManualAnswer) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }
        }

        excludedIndex = intent.getIntExtra("EXCLUDED_INDEX", -1)

        backArrow = findViewById(R.id.back_arrow)
        levelTitle = findViewById(R.id.tv_level)
        bottomNavHome = findViewById(R.id.home_button)
        bottomNavChallenges = findViewById(R.id.calendar_button)
        bottomNavStatistics = findViewById(R.id.statistics_button)
        progressRing = findViewById(R.id.progress_ring)
        progressRingContainer = findViewById(R.id.progress_ring_container)
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

        // Vistas de pistas
        hintOverlay = findViewById(R.id.hint_overlay)
        tvHintBalance = findViewById(R.id.tv_hint_balance)
        tvHintDescription = findViewById(R.id.tv_hint_description)
        btnUseHint = findViewById(R.id.btn_use_hint)
        btnSkipHint = findViewById(R.id.btn_skip_hint)
        hintTimerBar = findViewById(R.id.hint_timer_bar)
        hintOptionsLayout = findViewById(R.id.hint_options_layout)
        btnHintOption1 = findViewById(R.id.btn_hint_option_1)
        btnHintOption2 = findViewById(R.id.btn_hint_option_2)

        currentLevel = intent.getIntExtra("LEVEL", 1)
        levelTitle.text = getString(R.string.level_title, currentLevel)
        scoreTextView.text = getString(R.string.score_label, ScoreManager.currentScoreMasPlus)

        backArrow.setOnClickListener { showExitConfirmation { finish() } }
        bottomNavHome.setOnClickListener { showExitConfirmation { navigateToHome() } }
        bottomNavChallenges.setOnClickListener { showExitConfirmation { navigateToChallenges() } }
        bottomNavStatistics.setOnClickListener { showExitConfirmation { navigateToStatistics() } }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmation { finish() }
            }
        })

        attempts = 0
        inputBlocked = false
        pistaActivada = false

        Thread {
            generateElementsForLevel(currentLevel)
            calculateTimePerElement()

            runOnUiThread {
                ajustarIconosInferiores()
                startSequence()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        answerTimer?.cancel()
        handler.removeCallbacksAndMessages(null)
        chronometerTimer?.cancel()
        heartbeatAnimator?.cancel()
        hintTimerAnimator?.cancel()
        hintCountDownTimer?.cancel()
    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing) {
            answerTimer?.cancel()
            handler.removeCallbacksAndMessages(null)
            chronometerTimer?.cancel()
            heartbeatAnimator?.cancel()
            hintTimerAnimator?.cancel()
            hintCountDownTimer?.cancel()
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
        generateMASPlusElements(level)
    }

    private fun generateMASPlusElements(level: Int) {
        val totalElements: Int
        val intRange: IntRange
        val romanCount: Int
        val letterCount: Int
        var letterPool = listOf<Char>()
        val decimalCount: Int
        var decimalRange = 0.1..0.1
        val combinationCount: Int
        val combinationTypes = mutableListOf<CombinationType>()
        val negativeCount: Int

        when (level) {
            in 1..7 -> {
                totalElements = 7
                intRange = 1..12
                romanCount = 1
                letterCount = 0
                decimalCount = 0
                combinationCount = 0
                negativeCount = 2
            }
            in 8..14 -> {
                totalElements = 10
                intRange = 1..14
                romanCount = 2
                letterCount = 0
                decimalCount = 0
                combinationCount = 0
                negativeCount = 3
            }
            in 15..21 -> {
                totalElements = 12
                intRange = 1..16
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C')
                decimalCount = 0
                combinationCount = 0
                negativeCount = 2
            }
            in 22..28 -> {
                totalElements = 14
                intRange = 1..18
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D')
                decimalCount = 2
                decimalRange = 0.1..0.4
                combinationCount = 2
                combinationTypes.add(CombinationType.RomanNumber)
                combinationTypes.add(CombinationType.RomanRoman)
                negativeCount = 1
            }
            in 29..35 -> {
                totalElements = 15
                intRange = 1..18
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D')
                decimalCount = 2
                decimalRange = 0.1..0.5
                combinationCount = 2
                combinationTypes.add(CombinationType.LetterNumber)
                combinationTypes.add(CombinationType.LetterLetter)
                negativeCount = 2
            }
            in 36..42 -> {
                totalElements = 16
                intRange = 1..20
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D', 'E')
                decimalCount = 2
                decimalRange = 0.1..0.6
                combinationCount = 2
                combinationTypes.add(CombinationType.NumberDecimal)
                negativeCount = 2
            }
            in 43..49 -> {
                totalElements = 17
                intRange = 1..20
                romanCount = 2
                letterCount = 3
                letterPool = listOf('A', 'B', 'C', 'D', 'E')
                decimalCount = 2
                decimalRange = 0.1..0.7
                combinationCount = 2
                combinationTypes.add(CombinationType.LetterDecimal)
                negativeCount = 3
            }
            in 50..56 -> {
                totalElements = 18
                intRange = 1..20
                romanCount = 2
                letterCount = 2
                letterPool = listOf('A', 'B', 'C', 'D', 'E', 'F')
                decimalCount = 3
                decimalRange = 0.1..0.8
                combinationCount = 2
                combinationTypes.add(CombinationType.RomanLetter)
                negativeCount = 2
            }
            in 57..63 -> {
                totalElements = 19
                intRange = 1..22
                romanCount = 2
                letterCount = 3
                letterPool = listOf('A', 'B', 'C', 'D', 'E', 'F')
                decimalCount = 2
                decimalRange = 0.1..0.9
                combinationCount = 2
                combinationTypes.add(CombinationType.RomanDecimal)
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
                combinationTypes.add(CombinationType.RomanLetter)
                combinationTypes.add(CombinationType.RomanNumber)
                negativeCount = 3
            }
        }

        val generatedElements = mutableListOf<MASPlusElement>()

        repeat(romanCount) {
            val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
            val roman = convertToRoman(randomInt)
            generatedElements.add(MASPlusElement(roman, isNegative = false))
        }

        if (letterPool.isNotEmpty()) {
            repeat(letterCount) {
                val letter = letterPool.random()
                generatedElements.add(MASPlusElement(letter.toString(), isNegative = false))
            }
        }

        repeat(decimalCount) {
            val decimalValue = generateRandomDecimal(decimalRange.start, decimalRange.endInclusive)
            val formattedDecimal = String.format(Locale.getDefault(), "%.1f", decimalValue)
            generatedElements.add(MASPlusElement(formattedDecimal, isNegative = false))
        }

        val usedUpSoFar = romanCount + letterCount + decimalCount
        val neededBeforeCombos = totalElements - combinationCount - usedUpSoFar
        repeat(neededBeforeCombos) {
            val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
            generatedElements.add(MASPlusElement(randomInt.toString()))
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

    private fun ensureNoConsecutiveDuplicates(list: MutableList<MASPlusElement>) {
        if (list.size < 3) return

        var index = 0
        while (index < list.size - 2) {
            val a = list[index].value
            val b = list[index + 1].value
            val c = list[index + 2].value

            if (a == b && b == c) {
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

        var baseTime = 2.15

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
                    verificarYMostrarPista()
                }
            }
        })
    }

    private fun startProgressTimer() {
        val totalDuration = timePerElementList.sum()
        progressRing.startProgressAnimation(totalDuration)
    }

    // Sistema de pistas

    private fun verificarYMostrarPista() {
        val saldo = CoinManager.getBalance(this)
        if (saldo >= HINT_COST_COINS) {
            mostrarOverlayPista(saldo)
        } else {
            transitionToPrompt()
        }
    }

    private fun mostrarOverlayPista(saldo: Int) {
        tvHintBalance.text = saldo.toString()
        tvHintDescription.text = if (useManualAnswer) {
            getString(R.string.hint_desc_writing)
        } else {
            getString(R.string.hint_desc_selection)
        }

        hintOverlay.visibility = View.VISIBLE

        hintTimerBar.progress = 100
        hintTimerAnimator = ObjectAnimator.ofInt(hintTimerBar, "progress", 100, 0).apply {
            duration = HINT_TIMER_MS
            interpolator = LinearInterpolator()
            start()
        }

        hintCountDownTimer = object : CountDownTimer(HINT_TIMER_MS, HINT_TIMER_MS) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                cerrarOverlayYContinuar(false)
            }
        }.start()

        btnUseHint.setOnClickListener {
            hintTimerAnimator?.cancel()
            hintCountDownTimer?.cancel()
            animarGastoMonedas(saldo) {
                cerrarOverlayYContinuar(true)
            }
        }

        btnSkipHint.setOnClickListener {
            hintTimerAnimator?.cancel()
            hintCountDownTimer?.cancel()
            cerrarOverlayYContinuar(false)
        }
    }

    private fun animarGastoMonedas(saldoAntes: Int, onFin: () -> Unit) {
        CoinManager.spendCoins(this, HINT_COST_COINS)
        val saldoDespues = CoinManager.getBalance(this)

        ValueAnimator.ofInt(saldoAntes, saldoDespues).apply {
            duration = 400
            addUpdateListener {
                tvHintBalance.text = (it.animatedValue as Int).toString()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    Handler(Looper.getMainLooper()).postDelayed({ onFin() }, 300)
                }
            })
            start()
        }
    }

    private fun cerrarOverlayYContinuar(usoPista: Boolean) {
        pistaActivada = usoPista
        hintOverlay.visibility = View.GONE
        transitionToPrompt()
    }

    private fun transitionToPrompt() {
        numberTextView.visibility = View.GONE
        progressRing.visibility = View.GONE
        blueCircle.visibility = View.GONE
        progressRingContainer.visibility = View.GONE
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
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(manualAnswerEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        startHeartbeatAnimation()

        chronometerTimer = object : CountDownTimer(7000, 75) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedMillis = 7000 - millisUntilFinished
                val elapsedSeconds = elapsedMillis / 1000.0

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

                val textColor = when {
                    elapsedSeconds < 3.0 -> ContextCompat.getColor(this@GameActivityMasPlus, R.color.green_medium)
                    elapsedSeconds < 5.0 -> ContextCompat.getColor(this@GameActivityMasPlus, R.color.orange_dark)
                    else -> ContextCompat.getColor(this@GameActivityMasPlus, R.color.red)
                }

                spannableString.setSpan(
                    ForegroundColorSpan(textColor),
                    0,
                    formattedTime.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                chronometerTextView.text = spannableString

                if (elapsedSeconds >= 5.0 && !soundPlayed) {
                    soundPlayed = true
                    playAlertSound()
                }
            }

            override fun onFinish() {
                val finalTime = "7.00"
                val spannableString = SpannableString(finalTime)
                spannableString.setSpan(
                    RelativeSizeSpan(0.75f),
                    finalTime.indexOf('.'),
                    finalTime.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@GameActivityMasPlus, R.color.red)),
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
        if (pistaActivada) {
            mostrarOpcionesPistaEscritura()
        }

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

    private fun mostrarOpcionesPistaEscritura() {
        val rangeOffset = when (currentLevel) {
            in 1..10 -> 3
            in 11..20 -> 4
            in 21..30 -> 5
            in 31..40 -> 6
            in 41..50 -> 7
            in 51..60 -> 8
            else -> 9
        }

        var incorrecta: Int
        do {
            incorrecta = correctAnswer + Random.nextInt(-rangeOffset, rangeOffset + 1)
        } while (incorrecta == correctAnswer || incorrecta < 1)

        val opciones = listOf(correctAnswer, incorrecta).shuffled()

        btnHintOption1.text = opciones[0].toString()
        btnHintOption2.text = opciones[1].toString()

        hintOptionsLayout.visibility = View.VISIBLE

        btnHintOption1.setOnClickListener {
            manualAnswerEditText.setText(opciones[0].toString())
        }
        btnHintOption2.setOnClickListener {
            manualAnswerEditText.setText(opciones[1].toString())
        }
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
        if (inputBlocked) return

        val isCorrect = userAnswer == correctAnswer
        userResponses.add(userAnswer)

        if (isCorrect) {
            answerTimer?.cancel()
            chronometerTimer?.cancel()

            inputBlocked = true
            disableAllInputs()

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

            if (attempts >= 2 || (pistaActivada && attempts >= 1)) {
                inputBlocked = true
                disableAllInputs()

                answerTimer?.cancel()
                chronometerTimer?.cancel()
                calculateTimeSpent()

                Handler(Looper.getMainLooper()).postDelayed({
                    manualAnswerEditText.background = originalBackground
                    navigateToLevelResult(false)
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
    }

    private fun showAnswerButtons() {
        answerButtonsGrid.visibility = View.VISIBLE
        btnAnswer1.visibility = View.VISIBLE
        btnAnswer2.visibility = View.VISIBLE
        btnAnswer3.visibility = View.VISIBLE
        btnAnswer4.visibility = View.VISIBLE

        setAnswerValues()

        if (pistaActivada) {
            aplicarPistaSeleccion()
        }

        btnAnswer1.setOnClickListener { checkAnswerButton(btnAnswer1) }
        btnAnswer2.setOnClickListener { checkAnswerButton(btnAnswer2) }
        btnAnswer3.setOnClickListener { checkAnswerButton(btnAnswer3) }
        btnAnswer4.setOnClickListener { checkAnswerButton(btnAnswer4) }

        startAnswerTimer()
    }

    private fun aplicarPistaSeleccion() {
        val botones = listOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)
        val incorrectos = botones.filter {
            it.text.toString().toIntOrNull() != correctAnswer
        }.toMutableList()

        incorrectos.shuffle()
        incorrectos.take(2).forEach { boton ->
            boton.alpha = 0.3f
            boton.isEnabled = false
            boton.isClickable = false
        }
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
        }
    }

    private fun startAnswerTimer() {
        answerTimer?.cancel()
        answerTimer = object : CountDownTimer(7000, 75) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                navigateToLevelResult(false)
            }
        }.start()
    }

    private fun checkAnswerButton(selectedButton: Button) {
        if (inputBlocked) return

        selectedButton.clearFocus()
        val selectedAnswer = selectedButton.text.toString().toInt()
        userResponses.add(selectedAnswer)

        val isCorrect = selectedAnswer == correctAnswer

        if (isCorrect) {
            answerTimer?.cancel()
            chronometerTimer?.cancel()

            inputBlocked = true
            disableAllInputs()

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
            if (attempts >= 2 || (pistaActivada && attempts >= 1)) {
                inputBlocked = true
                disableAllInputs()

                answerTimer?.cancel()
                chronometerTimer?.cancel()
                calculateTimeSpent()

                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLevelResult(false)
                }, 1000)
            }
        }
    }

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
        val intent = Intent(this, LevelResultActivityMasPlus::class.java)
        intent.putExtra("LEVEL", currentLevel)
        intent.putExtra("IS_SUCCESSFUL", isSuccessful)
        intent.putExtra("ATTEMPTS", attempts)
        intent.putExtra("TIME_SPENT", timeSpentInSeconds)

        if (isSuccessful) {
            ScoreManager.resetConsecutiveFailuresMasPlus(currentLevel)
            if (ScoreManager.isLevelBlockedByFailuresMasPlus(currentLevel + 1)) {
                ScoreManager.resetConsecutiveFailuresMasPlus(currentLevel + 1)
            }
        }

        else if (attempts >= 2 || (pistaActivada && attempts >= 1)) {
            ScoreManager.incrementConsecutiveFailuresMasPlus(currentLevel)
            intent.putExtra("NUMBER_LIST", elementsList.map { it.value }.toTypedArray())
            intent.putExtra("CORRECT_ANSWER", correctAnswer)
            intent.putExtra("EXCLUDED_INDEX", excludedIndex ?: -1)
            intent.putExtra("USER_RESPONSES", userResponses.toIntArray())
        }

        intent.putExtra("USED_HINT", pistaActivada)
        intent.putExtra("USE_MANUAL_ANSWER", useManualAnswer)

        AdManager.showInterstitialOnLevelEnd(this, currentLevel) {
            startActivity(intent)
            finish()
        }
    }

    private fun ajustarIconosInferiores() {
        val iconoReferencia = backArrow

        iconoReferencia.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    iconoReferencia.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val anchoIcono = iconoReferencia.width
                    val altoIcono = iconoReferencia.height

                    val botones = listOf(bottomNavHome, bottomNavChallenges, bottomNavStatistics)
                    for (boton in botones) {
                        val iconoTop = boton.compoundDrawables[1]
                        iconoTop?.setBounds(0, 0, anchoIcono, altoIcono)

                        boton.setCompoundDrawables(
                            boton.compoundDrawables[0],
                            iconoTop,
                            boton.compoundDrawables[2],
                            boton.compoundDrawables[3]
                        )
                    }
                }
            }
        )
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

    private fun generateRandomDecimal(min: Double, max: Double): Double {
        return Random.nextDouble(min, max)
    }

    private fun generateCombinations(
        count: Int,
        comboTypes: List<CombinationType>,
        intRange: IntRange,
        letterPool: List<Char>
    ): List<MASPlusElement> {
        if (count <= 0) return emptyList()
        val combos = mutableListOf<MASPlusElement>()

        repeat(count) {
            val comboType = comboTypes.random()
            val comboString = when (comboType) {
                CombinationType.RomanNumber -> {
                    val randomRomanInt = Random.nextInt(intRange.first, intRange.last + 1)
                    val roman = convertToRoman(randomRomanInt)
                    val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
                    "($roman + $randomInt)"
                }
                CombinationType.RomanRoman -> {
                    val rand1 = convertToRoman(Random.nextInt(intRange.first, intRange.last + 1))
                    val rand2 = convertToRoman(Random.nextInt(intRange.first, intRange.last + 1))
                    "($rand1 + $rand2)"
                }
                CombinationType.LetterNumber -> {
                    if (letterPool.isEmpty()) {
                        "(A + 1)"
                    } else {
                        val randomLetter = letterPool.random()
                        val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
                        "($randomLetter + $randomInt)"
                    }
                }
                CombinationType.LetterLetter -> {
                    if (letterPool.size < 2) {
                        "(A + B)"
                    } else {
                        val l1 = letterPool.random()
                        val l2 = letterPool.random()
                        "($l1 + $l2)"
                    }
                }
                CombinationType.NumberDecimal -> {
                    val randomInt = Random.nextInt(intRange.first, intRange.last + 1)
                    val randDecimal = generateRandomDecimal(0.1, 0.9)
                    val formatted = String.format(Locale.getDefault(), "%.1f", randDecimal)
                    "($randomInt + $formatted)"
                }
                CombinationType.LetterDecimal -> {
                    val randomLetter = if (letterPool.isEmpty()) 'A' else letterPool.random()
                    val randDecimal = generateRandomDecimal(0.1, 0.9)
                    val formatted = String.format(Locale.getDefault(), "%.1f", randDecimal)
                    "($randomLetter + $formatted)"
                }
                CombinationType.RomanLetter -> {
                    val roman = convertToRoman(Random.nextInt(intRange.first, intRange.last + 1))
                    val randomLetter = if (letterPool.isEmpty()) 'A' else letterPool.random()
                    "($roman + $randomLetter)"
                }
                CombinationType.RomanDecimal -> {
                    val roman = convertToRoman(Random.nextInt(intRange.first, intRange.last + 1))
                    val randDecimal = generateRandomDecimal(0.1, 0.9)
                    val formatted = String.format(Locale.getDefault(), "%.1f", randDecimal)
                    "($roman + $formatted)"
                }
            }
            combos.add(MASPlusElement(comboString))
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

    private fun calculateSum(list: List<MASPlusElement>): Int {
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

data class MASPlusElement(
    val value: String,
    val isNegative: Boolean = false
)

enum class CombinationType {
    RomanNumber,
    RomanRoman,
    LetterNumber,
    LetterLetter,
    NumberDecimal,
    LetterDecimal,
    RomanLetter,
    RomanDecimal
}