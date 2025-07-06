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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.sumamente.R
import com.example.sumamente.ui.utils.isPositiveNumber
import java.util.Locale
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

class GameActivityGenioPlusPrincipiante : BaseActivity()  {

    data class GameElement(val value: String, val isNegative: Boolean = false)

    private lateinit var backArrow: ImageView
    private lateinit var levelTitle: TextView
    private lateinit var logoImage: ImageView
    private lateinit var bottomNavHome: ImageView
    private lateinit var bottomNavChallenges: ImageView
    private lateinit var bottomNavStatistics: ImageView
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
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var userResponses = mutableListOf<Int>()

    private var currentLevel = 1
    private var elementList = mutableListOf<GameElement>()
    private val handler = Handler(Looper.getMainLooper())
    private var correctAnswer = 0
    private var attempts = 0
    private var startTime: Long = 0
    private var answerTimer: CountDownTimer? = null
    private var useManualAnswer: Boolean = false
    private var timePerElementList = mutableListOf<Long>()
    private var excludedIndex: Int? = null
    private var chronometerTimer: CountDownTimer? = null
    private var chronometerStartTime: Long = 0
    private var heartbeatAnimator: ObjectAnimator? = null
    private var soundPlayed = false
    private var timeSpentInSeconds: Double = 0.0

    private val groupsOfFractions = mapOf(
        "Grupo A" to listOf((3 to 1), (6 to 3), (40 to 10), (15 to 5), (90 to 9), (1 to 1), (6 to 1), (72 to 8), (54 to 6), (20 to 4)),
        "Grupo B" to listOf((9 to 1), (40 to 5), (7 to 7), (48 to 8), (4 to 2), (80 to 10), (5 to 5), (32 to 8), (40 to 8), (40 to 4)),
        "Grupo C" to listOf((8 to 2), (25 to 5), (4 to 4), (12 to 2), (18 to 9), (6 to 3), (80 to 8), (56 to 7), (64 to 8), (6 to 2)),
        "Grupo D" to listOf((16 to 8), (72 to 9), (20 to 5), (18 to 3), (32 to 4), (21 to 7), (3 to 3), (42 to 7), (48 to 6), (81 to 9)),
        "Grupo E" to listOf((21 to 3), (20 to 2), (35 to 5), (8 to 4), (30 to 5), (30 to 6), (30 to 10), (42 to 6), (24 to 6), (63 to 7)),
        "Grupo F" to listOf((60 to 6), (56 to 8), (14 to 2), (54 to 9), (16 to 4), (45 to 5), (49 to 7), (90 to 10), (10 to 5), (4 to 1)),
        "Grupo G" to listOf((36 to 6), (18 to 6), (9 to 9), (14 to 7), (12 to 6), (12 to 3), (100 to 10), (63 to 9), (70 to 7), (50 to 5)),
        "Grupo H" to listOf((18 to 2), (8 to 8), (27 to 3), (28 to 7), (2 to 1), (45 to 9), (24 to 4), (70 to 10), (50 to 10), (24 to 8)),
        "Grupo I" to listOf((15 to 3), (10 to 2), (28 to 4), (36 to 9), (16 to 2), (24 to 3), (60 to 10), (30 to 3), (10 to 10), (6 to 6)),
        "Grupo J" to listOf((8 to 1), (7 to 1), (10 to 1), (2 to 2), (20 to 10), (12 to 4), (5 to 1), (27 to 9), (36 to 4), (35 to 7))
    )

    private val groupsOfRoots = mapOf(
        "Grupo A" to listOf("√1", "√4", "√9", "√16", "√25", "√36"),
        "Grupo B" to listOf("√49", "√64", "√81", "√100", "√121", "√144"),
        "Grupo C" to listOf("∛1", "∛8", "∛27", "∛64", "∛125"),
        "Grupo D" to listOf("√49", "√64", "√81", "∛8", "∛27"),
        "Grupo E" to listOf("√25", "∛1", "∛125", "√121"),
        "Grupo F" to listOf("√100", "√144", "∛64", "∛27")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsGenioPlus", MODE_PRIVATE)
        setContentView(R.layout.activity_game_genio_plus)

        ScoreManager.initGenioPlusPrincipiante(this)

        val prefs = getSharedPreferences("MyPrefsGenioPlus", MODE_PRIVATE)
        val responseMode = prefs.getString("selectedResponseModeGenioPlusPrincipiante", intent.getStringExtra("RESPONSE_MODE"))

        if (responseMode != null) {
            useManualAnswer = responseMode == ResponseModeGenioPlusPrincipiante.TYPE_ANSWER.name
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
        currentLevel = intent.getIntExtra("LEVEL", 1)
        levelTitle.text = getString(R.string.level_title, currentLevel)
        scoreTextView.text = getString(R.string.score_label, ScoreManager.currentScoreGenioPlusPrincipiante)


        backArrow.setOnClickListener {
            showExitConfirmation { finish() }
        }

        bottomNavHome.setOnClickListener {
            showExitConfirmation { navigateToHome() }
        }

        bottomNavChallenges.setOnClickListener {
            showExitConfirmation {  }
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
        generateElements()
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
        elementTextView.visibility = View.INVISIBLE
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

        val fraccionesPool = when (level) {
            in 1..7 -> groupsOfFractions["Grupo A"]!!
            in 8..14 -> combinePools("Grupo A", "Grupo B")
            in 15..21 -> combinePools("Grupo A", "Grupo B", "Grupo C")
            in 22..28 -> combinePools("Grupo A", "Grupo B", "Grupo C", "Grupo D")
            in 29..35 -> combinePools("Grupo A", "Grupo B", "Grupo C", "Grupo D", "Grupo E")
            else -> groupsOfFractions.values.flatten()
        }

        val raicesPool = when (level) {
            in 1..7 -> groupsOfRoots["Grupo A"] ?: emptyList()
            in 8..14 -> combinePoolsRoots("Grupo A", "Grupo B")
            in 22..28 -> combinePoolsRoots("Grupo A", "Grupo B", "Grupo C")
            in 36..42 -> combinePoolsRoots("Grupo A", "Grupo B", "Grupo C", "Grupo D")
            in 50..56 -> combinePoolsRoots("Grupo A", "Grupo B", "Grupo C", "Grupo D", "Grupo E")
            else -> combinePoolsRoots("Grupo A", "Grupo B", "Grupo C", "Grupo D", "Grupo E", "Grupo F")
        }

        val integersCount = when (level) {
            in 1..7 -> 2
            in 8..14 -> 3
            in 15..21 -> 5
            in 22..28 -> 6
            else -> 7
        }

        val fractionsCount = when (level) {
            in 1..28 -> 3
            in 29..42 -> 4
            in 43..56 -> 5
            else -> 6
        }

        val rootsCount = when (level) {
            in 1..21 -> 2
            in 22..42 -> 3
            in 43..56 -> 4
            else -> 5
        }

        val combinationsCount = when (level) {
            in 1..7 -> 1
            in 8..35 -> 2
            else -> 3
        }

        val integers = generateRandomNumbers(integersCount, 1..12)
        val fractions = selectRandomElements(fractionsCount, fraccionesPool)
        val roots = selectRandomElements(rootsCount, raicesPool)
        val combinations = generateCombinations(combinationsCount, fraccionesPool, raicesPool)

        val allElements = mutableListOf<GameElement>().apply {
            addAll(integers.map { GameElement(it.toString(), isNegative = false) })
            addAll(fractions.map { GameElement("${it.first}/${it.second}", isNegative = false) })
            addAll(roots.map { GameElement(it, isNegative = false) })
            addAll(combinations.map { GameElement(it, isNegative = false) })
            shuffle()
        }

        val numNegatives: Int = when {
            level in setOf(3, 7, 10, 16, 19, 22, 25, 29, 33) -> 1
            level >= 36 -> 2
            else -> 0
        }
        applyNegatives(allElements, numNegatives)

        ensureNoConsecutiveDuplicates(allElements)

        elementList.addAll(allElements)
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

                elem.value.matches(Regex("^-[A-G]$")) -> {
                    val letterChar = elem.value[1]
                    val letterValue = letterChar - 'A' + 1
                    -letterValue
                }

                elem.value.matches(Regex("^[A-G]$")) -> {
                    val letterChar = elem.value[0]
                    val letterValue = letterChar - 'A' + 1
                    if (elem.isNegative) -letterValue else letterValue
                }

                elem.value.matches(Regex("^√\\d+$")) -> {

                    val number = elem.value.substring(1).toIntOrNull() ?: 0
                    val rootValue = sqrt(number.toDouble()).toInt()
                    if (elem.isNegative) -rootValue else rootValue
                }

                elem.value.matches(Regex("^∛\\d+$")) -> {

                    val number = elem.value.substring(1).toIntOrNull() ?: 0
                    val rootValue = round(number.toDouble().pow(1.0 / 3.0)).toInt()
                    if (elem.isNegative) -rootValue else rootValue
                }


                elem.value.matches(Regex("^-?\\d+/\\d+$")) -> {

                    val fractionParts = elem.value.replace("-", "").split("/")
                    val numerator = fractionParts[0].toInt()
                    val denominator = fractionParts[1].toInt()
                    val fractionResult = numerator.toDouble() / denominator.toDouble()
                    if (elem.isNegative || elem.value.startsWith("-")) -fractionResult.toInt() else fractionResult.toInt()
                }


                elem.value.matches(Regex("\\(.*\\+.*\\)")) -> {

                    parseMixedExpression(elem)
                }

                else -> {
                    val num = elem.value.toIntOrNull()
                    num ?: 0
                }
            }
        }
    }


    private fun generateRandomNumbers(count: Int, range: IntRange): MutableList<Int> {
        return MutableList(count) { Random.nextInt(range.first, range.last + 1) }
    }

    private fun <T> selectRandomElements(count: Int, pool: List<T>): List<T> {
        return pool.shuffled().take(count)
    }

    private fun combinePools(vararg groups: String): List<Pair<Int, Int>> {
        return groups.flatMap { groupsOfFractions[it] ?: emptyList() }
    }

    private fun combinePoolsRoots(vararg groups: String): List<String> {
        return groups.flatMap { groupsOfRoots[it] ?: emptyList() }
    }

    private fun generateCombinations(
        count: Int,
        fraccionesPool: List<Pair<Int, Int>>,
        raicesPool: List<String>
    ): List<String> {
        return List(count) {
            when ((1..4).random()) {
                1 -> {
                    val number = (1..12).random()
                    val fraction = fraccionesPool.random()
                    "($number + ${fraction.first}/${fraction.second})"
                }
                2 -> {
                    val fraction1 = fraccionesPool.random()
                    val fraction2 = fraccionesPool.random()
                    "(${fraction1.first}/${fraction1.second} + ${fraction2.first}/${fraction2.second})"
                }
                3 -> {
                    val root = raicesPool.random()
                    val number = (1..12).random()
                    "($root + $number)"
                }
                else -> {
                    val root = raicesPool.random()
                    val fraction = fraccionesPool.random()
                    "($root + ${fraction.first}/${fraction.second})"
                }
            }
        }
    }


    private fun applyNegatives(elements: MutableList<GameElement>, numNegatives: Int) {
        if (numNegatives <= 0) return
        repeat(numNegatives) {
            val randomIndex = Random.nextInt(elements.size)
            val elem = elements[randomIndex]

            if (!elem.isNegative) {
                val newValue = if (elem.value.startsWith("-")) elem.value else "-${elem.value}"
                val newElement = GameElement(newValue, isNegative = true)
                elements[randomIndex] = newElement
            }
        }
    }

    private fun ensureNoConsecutiveDuplicates(list: MutableList<GameElement>) {
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

    private fun parseMixedExpression(elem: GameElement): Int {

        val expressionContent = elem.value.removePrefix("(").removeSuffix(")")
        val parts = expressionContent.split("+").map { it.trim() }

        var sum = 0.0
        parts.forEach { part ->
            when {
                part.matches(Regex("^√\\d+$")) -> {
                    val number = part.substring(1).toIntOrNull() ?: 0
                    sum += sqrt(number.toDouble())
                }
                part.matches(Regex("^∛\\d+$")) -> {
                    val number = part.substring(1).toIntOrNull() ?: 0
                    sum += round(number.toDouble().pow(1.0 / 3.0))
                }
                part.matches(Regex("^\\d+/\\d+$")) -> {
                    val fractionParts = part.split("/")
                    val numerator = fractionParts[0].toInt()
                    val denominator = fractionParts[1].toInt()
                    sum += numerator.toDouble() / denominator.toDouble()
                }
                part.matches(Regex("^\\d+$")) -> {
                    sum += part.toInt()
                }

                part.matches(Regex("^-?[A-G]$")) -> {
                    val letter = part.replace("-", "")[0]
                    val letterValue = (letter - 'A') + 1
                    sum += if (part.startsWith("-")) -letterValue else letterValue
                }
                else -> {

                    sum += 0
                }
            }
        }

        return if (elem.isNegative) -sum.toInt() else sum.toInt()
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
                            ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.yellow_dark, null)),
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
                        elementTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                    } else {
                        elementTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100f)
                    }

                    elementTextView.text = spannableString

                    val duration = timePerElementList[index]

                    index++
                    handler.postDelayed(this, duration)
                } else {
                    transitionToPrompt()
                }
            }
        })
    }

    private fun calculateTimePerElement() {
        timePerElementList.clear()
        val level = currentLevel

        var firstNumberTime = 3.0

        val blockNumber = (level - 1) / 5
        firstNumberTime -= blockNumber * 0.07

        val levelInBlock = (level - 1) % 5

        var currentTime = firstNumberTime

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

    private fun startProgressTimer() {
        val totalDuration = timePerElementList.sum()
        progressRing.startProgressAnimation(totalDuration)
    }

    private fun transitionToPrompt() {
        elementTextView.visibility = View.GONE
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
                    elapsedSeconds < 5.0 -> ContextCompat.getColor(this@GameActivityGenioPlusPrincipiante, R.color.green_medium)
                    elapsedSeconds < 8.0 -> ContextCompat.getColor(this@GameActivityGenioPlusPrincipiante, R.color.orange_dark)
                    else -> ContextCompat.getColor(this@GameActivityGenioPlusPrincipiante, R.color.red)
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
                    ForegroundColorSpan(ContextCompat.getColor(this@GameActivityGenioPlusPrincipiante, R.color.red)),
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
                ScoreManager.incrementConsecutiveFailuresGenioPlusPrincipiante(currentLevel)
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
            if (incorrectAnswer != correctAnswer && incorrectAnswer !in incorrectAnswers && incorrectAnswer >= 1) {
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
        startTime = System.currentTimeMillis()
        answerTimer?.cancel()
        answerTimer = object : CountDownTimer(10000, 75) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                navigateToLevelResult(false)
            }
        }.start()
    }

    private fun checkAnswer(selectedButton: Button) {
        selectedButton.clearFocus()

        val selectedAnswer = selectedButton.text.toString().toInt()
        userResponses.add(selectedAnswer)

        val isCorrect = selectedAnswer == correctAnswer

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
                ScoreManager.incrementConsecutiveFailuresGenioPlusPrincipiante(currentLevel)
                answerTimer?.cancel()
                chronometerTimer?.cancel()

                calculateTimeSpent()

                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToLevelResult(false)
                }, 1000)
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


    private fun showExitConfirmation(onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(this)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(R.string.btn_yes) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.btn_no, null)
        builder.create().show()
    }

    private fun navigateToLevelResult(isSuccessful: Boolean) {
        val intent = Intent(this, LevelResultActivityGenioPlusPrincipiante::class.java)
        intent.putExtra("LEVEL", currentLevel)
        intent.putExtra("IS_SUCCESSFUL", isSuccessful)
        intent.putExtra("ATTEMPTS", attempts)
        intent.putExtra("TIME_SPENT", timeSpentInSeconds)

        if (isSuccessful) {
            ScoreManager.resetConsecutiveFailuresGenioPlusPrincipiante(currentLevel)
        } else if (attempts >= 2) {
            ScoreManager.incrementConsecutiveFailuresGenioPlusPrincipiante(currentLevel)
            if (userResponses.isEmpty()) userResponses.add(-1)
            intent.putExtra("NUMBER_LIST", elementList.map { it.value }.toTypedArray())
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
}


