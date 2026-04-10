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
import kotlin.random.Random

class GameActivityAlfaNumerosPrincipiante : BaseActivity()  {

    data class GameElement(val value: String, val isNegative: Boolean = false)

    private lateinit var backArrow: ImageView
    private lateinit var levelTitle: TextView
    private lateinit var bottomNavHome: TextView
    private lateinit var bottomNavChallenges: TextView
    private lateinit var bottomNavStatistics: TextView
    private lateinit var progressRing: ProgressRingView
    private lateinit var elementTextView: TextView
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
    private var elementList = mutableListOf<GameElement>()
    private val handler = Handler(Looper.getMainLooper())
    private var correctAnswer = 0
    private var attempts = 0
    private var answerTimer: CountDownTimer? = null
    private var useManualAnswer: Boolean = false
    private var timePerElementList = mutableListOf<Long>()
    private var excludedIndex: Int? = null
    private var chronometerTimer: CountDownTimer? = null
    private var chronometerStartTime: Long = 0
    private var heartbeatAnimator: ObjectAnimator? = null
    private var soundPlayed = false
    private var timeSpentInSeconds: Double = 0.0
    private var userResponses = mutableListOf<Int>()
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

        getSharedPreferences("MyPrefsAlfaNumeros", MODE_PRIVATE)
        setContentView(R.layout.activity_game_alfanumeros)

        AdManager.preloadInterstitial(this)

        ScoreManager.initAlfaNumerosPrincipiante(this)

        val prefs = getSharedPreferences("MyPrefsAlfaNumeros", MODE_PRIVATE)
        val responseMode = prefs.getString(
            "selectedResponseModeAlfaNumerosPrincipiante",
            intent.getStringExtra("RESPONSE_MODE")
        )

        if (responseMode != null) {
            useManualAnswer = responseMode == ResponseModeAlfaNumeros.TYPE_ANSWER.name
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
        elementTextView = findViewById(R.id.tv_number)
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
        scoreTextView.text = getString(
            R.string.score_label,
            ScoreManager.currentScoreAlfaNumerosPrincipiante
        )

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
            generateElements()
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
        elementTextView.visibility = View.INVISIBLE
        vamosTextView.visibility = View.INVISIBLE
        promptTextView.visibility = View.INVISIBLE
        answerButtonsGrid.visibility = View.INVISIBLE
        manualInputLayout.visibility = View.INVISIBLE
        hintOptionsLayout.visibility = View.GONE
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
                    elementTextView.visibility = View.VISIBLE
                    startProgressTimer()
                    showElements()
                }, delayBeforeElements)
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun generateElements() {
        elementList.clear()

        val level = currentLevel

        val totalCount = when (level) {
            in 1..7 -> 7
            in 8..14 -> 9
            in 15..21 -> 11
            in 22..28 -> 13
            in 29..35 -> 14
            in 36..42 -> 15
            in 43..49 -> 16
            in 50..56 -> 17
            in 57..63 -> 18
            else -> 19
        }

        val numberRange = when (level) {
            in 1..7 -> 1..10
            in 8..14 -> 1..12
            in 15..21 -> 1..15
            in 22..28 -> 1..17
            in 29..35 -> 1..19
            in 36..42 -> 1..21
            else -> 1..23
        }

        val extraLargeNumberRange = when (level) {
            in 1..7 -> 7..10
            in 8..14 -> 7..12
            in 15..21 -> 7..15
            in 22..28 -> 7..17
            else -> null
        }

        val letterRange = when (level) {
            in 1..5 -> 'A'..'D'
            in 6..10 -> 'A'..'E'
            in 11..15 -> 'A'..'F'
            in 16..20 -> 'A'..'G'
            in 21..25 -> 'A'..'H'
            else -> 'A'..'H'
        }

        val combinationLetterRange = when (level) {
            in 31..35 -> 'A'..'D'
            in 36..40 -> 'A'..'E'
            in 41..50 -> 'A'..'F'
            in 51..60 -> 'A'..'G'
            else -> 'A'..'H'
        }

        val letterCount = when {
            level >= 31 -> 6
            level <= 5 -> 3
            level <= 10 -> 5
            level <= 15 -> 6
            level <= 20 -> 7
            level <= 25 -> 8
            else -> 9
        }

        val combinationCount = if (level >= 31) {
            when (level) {
                in 31..35 -> 2
                in 36..40 -> 3
                in 41..50 -> 4
                in 51..60 -> 5
                else -> 6
            }
        } else {
            0
        }

        val numbersCount = totalCount - letterCount - combinationCount

        var smallNumbersCount = when (level) {
            in 1..7 -> 6
            in 8..14 -> 8
            in 15..21 -> 9
            in 22..28 -> 10
            else -> numbersCount
        }

        if (smallNumbersCount > numbersCount) {
            smallNumbersCount = numbersCount.coerceAtLeast(0)
        }

        val largeNumbersCount = (numbersCount - smallNumbersCount).coerceAtLeast(0)

        val numbers = mutableListOf<Int>()
        numbers.addAll(generateRandomNumbers(smallNumbersCount, numberRange))
        if (extraLargeNumberRange != null && largeNumbersCount > 0) {
            numbers.addAll(generateRandomNumbers(largeNumbersCount, extraLargeNumberRange))
        }

        val letters = generateRandomLetters(letterCount, letterRange)

        val combinations = if (level >= 31) {
            generateCombinations(combinationCount, combinationLetterRange, 1..(10 + (level / 10)))
        } else {
            emptyList()
        }

        val numNegatives: Int = when {
            level in setOf(3, 7, 10, 16, 19, 22, 25, 29, 33) -> 1
            level >= 36 -> 2
            else -> 0
        }

        val generatedNumberElements = numbers.map { GameElement(it.toString(), isNegative = false) }.toMutableList()
        val generatedLetterElements = letters.map { GameElement(it.toString(), isNegative = false) }.toMutableList()
        val generatedCombinationElements = combinations.map { GameElement(it, isNegative = false) }.toMutableList()

        elementList.addAll(generatedNumberElements)
        elementList.addAll(generatedLetterElements)
        elementList.addAll(generatedCombinationElements)
        elementList.shuffle()

        ensureNoConsecutiveDuplicates()

        val positiveIndices = elementList.indices.filter { elementList[it].isPositiveElement() }.toMutableList()
        repeat(numNegatives) {
            if (positiveIndices.isNotEmpty()) {
                val indexToNegate = positiveIndices.random()
                val elem = elementList[indexToNegate]
                val newElement = if (elem.value.isLetter()) {
                    GameElement(elem.value.negativizeLetter(), isNegative = true)
                } else {
                    val intVal = elem.value.toInt()
                    GameElement((-intVal).toString(), isNegative = true)
                }
                elementList[indexToNegate] = newElement
                positiveIndices.remove(indexToNegate)
            }
        }

        while (calculateSum() < 1) {
            elementList.shuffle()
        }
    }

    private fun generateRandomNumbers(count: Int, range: IntRange): MutableList<Int> {
        return MutableList(count) { Random.nextInt(range.first, range.last + 1) }
    }

    private fun generateRandomLetters(count: Int, range: CharRange): MutableList<Char> {
        return MutableList(count) { range.random() }
    }

    private fun generateCombinations(count: Int, range: CharRange, numberRange: IntRange): MutableList<String> {
        return MutableList(count) {
            val letter1 = range.random()
            if (Random.nextBoolean()) {
                val number = Random.nextInt(numberRange.first, numberRange.last + 1)
                "($letter1 + $number)"
            } else {
                val letter2 = range.random()
                "($letter1 + $letter2)"
            }
        }
    }

    private fun String.isPositiveNumber(): Boolean {
        val number = this.toIntOrNull()
        return number != null && number > 0
    }

    private fun String.isLetter(): Boolean {
        return this.matches(Regex("^-?[A-H]$"))
    }

    private fun GameElement.isPositiveElement(): Boolean {
        val num = this.value.toIntOrNull()
        return if (num != null) {
            num > 0
        } else {
            this.value.matches(Regex("^[A-H]$"))
        }
    }

    private fun String.negativizeLetter(): String {
        return if (this.startsWith("-")) this else "-$this"
    }

    private fun ensureNoConsecutiveDuplicates() {
        val n = elementList.size
        if (n < 3) return
        if (elementList.map { it.value }.toSet().size == 1) return

        repeat(8) {
            var ok = true
            var i = 0
            while (i <= n - 3) {
                val a = elementList[i].value
                if (a == elementList[i + 1].value && a == elementList[i + 2].value) {
                    var j = i + 3
                    while (j < n && elementList[j].value == a) j++
                    if (j < n) {
                        val tmp = elementList[i + 2]
                        elementList[i + 2] = elementList[j]
                        elementList[j] = tmp
                    } else {
                        elementList.shuffle()
                    }
                    ok = false
                    break
                }
                i++
            }
            if (ok) return
        }
    }

    private fun calculateSum(): Int {
        val filteredList = if (excludedIndex != null && excludedIndex!! in elementList.indices) {
            elementList.filterIndexed { i, _ -> i != excludedIndex }
        } else {
            elementList
        }

        return filteredList.sumOf { elem ->
            when {
                elem.value.isPositiveNumber() -> elem.value.toInt()
                elem.value.matches(Regex("\\(\\w \\+ \\d+\\)")) -> {
                    val parts = Regex("\\((\\w) \\+ (\\d+)\\)").find(elem.value)!!.destructured
                    val letterValue = parts.component1()[0] - 'A' + 1
                    val numberValue = parts.component2().toInt()
                    if (elem.isNegative) -(letterValue + numberValue) else (letterValue + numberValue)
                }
                elem.value.matches(Regex("\\(\\w \\+ \\w\\)")) -> {
                    val parts = Regex("\\((\\w) \\+ (\\w)\\)").find(elem.value)!!.destructured
                    val letter1Value = parts.component1()[0] - 'A' + 1
                    val letter2Value = parts.component2()[0] - 'A' + 1
                    if (elem.isNegative) -(letter1Value + letter2Value) else (letter1Value + letter2Value)
                }
                elem.value.matches(Regex("^-[A-H]$")) -> {
                    val letterChar = elem.value[1]
                    val letterValue = letterChar - 'A' + 1
                    -letterValue
                }
                elem.value.matches(Regex("^[A-H]$")) -> {
                    val letterChar = elem.value[0]
                    val letterValue = letterChar - 'A' + 1
                    if (elem.isNegative) -letterValue else letterValue
                }
                else -> {
                    val num = elem.value.toIntOrNull()
                    num ?: 0
                }
            }
        }
    }

    private fun calculateTimePerElement() {
        timePerElementList.clear()
        val level = currentLevel

        var firstElementTime = 2.3

        val blockNumber = (level - 1) / 5
        firstElementTime -= blockNumber * 0.07

        val levelInBlock = (level - 1) % 5

        var currentTime = firstElementTime

        for (i in elementList.indices) {
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

            if (level % 7 == 0 && i == elementList.size - 1) {
                currentTime -= 0.05
            }
        }
    }

    private fun showElements() {
        var index = 0

        handler.post(object : Runnable {
            override fun run() {
                if (index < elementList.size) {
                    val element = elementList[index]
                    val spannableString = SpannableStringBuilder(element.value)

                    if (element.isNegative) {
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

                    if (index > 0 && element.value == elementList[index - 1].value) {
                        spannableString.setSpan(
                            ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.yellow, null)),
                            0,
                            spannableString.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        handler.postDelayed({
                            val animation = ObjectAnimator.ofFloat(elementTextView, "translationY", 0f, -10f, 0f)
                            animation.duration = 200
                            animation.start()
                        }, 200)
                    }

                    if (element.value.matches(Regex("\\(.*\\+.*\\)"))) {
                        elementTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 70f)
                    } else {
                        elementTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 140f)
                    }

                    elementTextView.text = spannableString

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
        elementTextView.visibility = View.GONE
        progressRing.visibility = View.GONE
        blueCircle.visibility = View.GONE
        progressRingContainer.visibility = View.GONE
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

        chronometerTimer = object : CountDownTimer(10000, 75) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedMillis = 10000 - millisUntilFinished
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
                    elapsedSeconds < 5.0 -> ContextCompat.getColor(this@GameActivityAlfaNumerosPrincipiante, R.color.green_medium)
                    elapsedSeconds < 8.0 -> ContextCompat.getColor(this@GameActivityAlfaNumerosPrincipiante, R.color.orange_dark)
                    else -> ContextCompat.getColor(this@GameActivityAlfaNumerosPrincipiante, R.color.red)
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
                    ForegroundColorSpan(ContextCompat.getColor(this@GameActivityAlfaNumerosPrincipiante, R.color.red)),
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

                ScoreManager.incrementConsecutiveFailuresAlfaNumerosPrincipiante(currentLevel)

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

        if (pistaActivada) {
            aplicarPistaSeleccion()
        }

        btnAnswer1.setOnClickListener { checkAnswer(btnAnswer1) }
        btnAnswer2.setOnClickListener { checkAnswer(btnAnswer2) }
        btnAnswer3.setOnClickListener { checkAnswer(btnAnswer3) }
        btnAnswer4.setOnClickListener { checkAnswer(btnAnswer4) }

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
            if (incorrectAnswer != correctAnswer && incorrectAnswer !in incorrectAnswers && incorrectAnswer >= 1) {
                incorrectAnswers.add(incorrectAnswer)
            }
        }

        val allAnswers = incorrectAnswers.toMutableList()
        allAnswers.add(correctAnswer)
        allAnswers.shuffle()

        for (i in buttons.indices) {
            buttons[i].text = String.format(Locale.getDefault(), "%d", allAnswers[i])
        }
    }

    private fun startAnswerTimer() {
        System.currentTimeMillis()
        answerTimer?.cancel()

        answerTimer = object : CountDownTimer(10000, 75) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                navigateToLevelResult(false)
            }
        }.start()
    }

    private fun checkAnswer(selectedButton: Button) {
        if (inputBlocked) return
        selectedButton.clearFocus()

        val selectedAnswer = selectedButton.text.toString().toInt()
        val isCorrect = selectedAnswer == correctAnswer
        userResponses.add(selectedAnswer)

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

                ScoreManager.incrementConsecutiveFailuresAlfaNumerosPrincipiante(currentLevel)

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
        val intent = Intent(this, LevelResultActivityAlfaNumerosPrincipiante::class.java)
        intent.putExtra("LEVEL", currentLevel)
        intent.putExtra("IS_SUCCESSFUL", isSuccessful)
        intent.putExtra("ATTEMPTS", attempts)
        intent.putExtra("TIME_SPENT", timeSpentInSeconds)

        if (isSuccessful) {
            ScoreManager.resetConsecutiveFailuresAlfaNumerosPrincipiante(currentLevel)
            if (ScoreManager.isLevelBlockedByFailuresAlfaNumerosPrincipiante(currentLevel + 1)) {
                ScoreManager.resetConsecutiveFailuresAlfaNumerosPrincipiante(currentLevel + 1)
            }
        }

        else if (attempts >= 2 || (pistaActivada && attempts >= 1)) {
            val elementValuesArray = elementList.map { it.value }.toTypedArray()
            intent.putExtra("ELEMENT_LIST", elementValuesArray)
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
}