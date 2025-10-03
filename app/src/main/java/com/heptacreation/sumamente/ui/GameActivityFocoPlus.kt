package com.heptacreation.sumamente.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.gridlayout.widget.GridLayout
import com.heptacreation.sumamente.R
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameActivityFocoPlus : BaseActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var tvLevel: TextView
    private lateinit var tvScore: TextView

    private lateinit var boardContainer: FrameLayout
    private lateinit var tvVamos: TextView
    private lateinit var singleSlot: View
    private lateinit var tvSingle: TextView
    private lateinit var dualSlot: View
    private lateinit var tvLeft: TextView
    private lateinit var tvRight: TextView

    private lateinit var chrono: TextView
    private lateinit var timeProgress: ProgressBar
    private lateinit var tvErrorsCounter: TextView
    private var perExerciseMs: Long = 3210L
    private var rawTimeSpent: Double = 0.0
    private var exerciseTimer: CountDownTimer? = null

    private lateinit var answersGrid: GridLayout
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var correctnessPlan: MutableList<Boolean>
    private var leftExpectedMetaIndex: Int = -1
    private var rightExpectedMetaIndex: Int = -1

    private var lastPressedIndex: Int = -1

    private var btnNone: Button? = null
    private var currentLevel: Int = 1
    private var currentDifficulty: String? = null
    private var subtype: Int = 1
    private var totalExercises: Int = 14
    private var indexExercise: Int = 0
    private var maxErrorsAllowed: Int = 6
    private var errorsCount: Int = 0
    private var correctCount: Int = 0
    private var running = false
    private var isTwoTerms: Boolean = false
    private var levelTimer: CountDownTimer? = null
    private var levelStartTime: Long = 0
    private var totalLevelTimeMs: Long = 0
    private var globalTimer: CountDownTimer? = null
    private var lastPressedButton: Button? = null
    private var dualExerciseState = DualExerciseState.WAITING_FIRST
    private var dualResponsesGiven = 0
    private var firstResponseCorrect = false
    private var isDoubleNingunoExercise = false

    private enum class DualExerciseState {
        WAITING_FIRST,
        WAITING_SECOND,
        COMPLETED
    }

    private sealed class Meta {
        data class Numeric(val value: Int) : Meta()
        data class PairFig(val aIcon: Int, val aColor: Int, val bIcon: Int, val bColor: Int) : Meta()
    }
    private val metas: MutableList<Meta> = mutableListOf()

    private val dualVerticalOffsetDp = 24

    private var hiddenMetaIndexForFg: Int = -1

    private val iconNames = listOf(
        "ic_14_ancla", "ic_14_android", "ic_14_balon", "ic_14_caballo", "ic_14_casa",
        "ic_14_circulo", "ic_14_corazon", "ic_14_escudo", "ic_14_feliz", "ic_14_infinito",
        "ic_14_licuadora", "ic_14_lluvia", "ic_14_luna", "ic_14_pie", "ic_14_reciclar",
        "ic_14_rectangulo", "ic_14_reloj", "ic_14_scooter", "ic_14_tractor", "ic_14_triangulo",
        "ic_14_van"
    )

    private val colorResIds = listOf(
        android.R.color.white,
        R.color.blue_primary_darker,
        R.color.red_primary,
        R.color.orange_dark,
        R.color.cyan,
        R.color.green_light
    )

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        setContentView(R.layout.activity_game_foco_plus)

        currentLevel = intent.getIntExtra("LEVEL", 1)
        currentDifficulty = intent.getStringExtra("DIFFICULTY")
        subtype = intent.getIntExtra("SUBTYPE", 1)

        val levelSeed = currentLevel.toLong() + subtype * 1000L
        Random(levelSeed)

        findViews()
        setUpBackHandler()
        applyHeaders()

        val baseTimeSeconds = 5.0 - (currentLevel - 1) * 0.001
        perExerciseMs = (baseTimeSeconds * 1000).toLong()

        isTwoTerms = isTwoTermsSubtype(subtype)
        totalExercises = 14
        maxErrorsAllowed = if (isTwoTerms) 12 else 6

        val pauseTimeMs = (totalExercises - 1) * 400L
        totalLevelTimeMs = totalExercises * perExerciseMs + pauseTimeMs

        boardContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    boardContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    buildLevelMetas(subtype)
                    correctnessPlan = if (isTwoTerms) {
                        buildDualCorrectnessPlan(totalExercises)
                    } else {
                        buildCorrectnessPlan(totalExercises)
                    }

                    showVamosThenStart()
                }
            }
        )
    }

    private fun buildDualCorrectnessPlan(exerciseCount: Int): MutableList<Boolean> {
        val plan = MutableList(exerciseCount) { false }
        var exercisesWithMatch = 0
        val targetExercisesWithMatch = 6

        while (exercisesWithMatch < targetExercisesWithMatch) {
            val exerciseIndex = Random.nextInt(0, exerciseCount)
            if (!plan[exerciseIndex]) {
                plan[exerciseIndex] = true
                exercisesWithMatch++
            }
        }

        return plan
    }

    override fun onDestroy() {
        super.onDestroy()
        exerciseTimer?.cancel()
        levelTimer?.cancel()
        globalTimer?.cancel()
        lastPressedButton = null
    }

    private fun findViews() {
        backArrow = findViewById(R.id.back_arrow)
        tvLevel = findViewById(R.id.tv_level)
        tvScore = findViewById(R.id.tv_score)

        boardContainer = findViewById(R.id.board_container)
        tvVamos = findViewById(R.id.tv_vamos)
        singleSlot = findViewById(R.id.single_slot)
        tvSingle = findViewById(R.id.tv_single_exercise)
        dualSlot = findViewById(R.id.dual_slot)
        tvLeft = findViewById(R.id.tv_left_exercise)
        tvRight = findViewById(R.id.tv_right_exercise)

        chrono = findViewById<TextView>(R.id.chronometer_text_view).apply {
            typeface = Typeface.MONOSPACE
        }
        timeProgress = findViewById(R.id.time_progress)
        tvErrorsCounter = findViewById(R.id.tv_errors_counter)

        answersGrid = findViewById(R.id.answers_grid)
        btn1 = findViewById(R.id.btn_meta_1)
        btn2 = findViewById(R.id.btn_meta_2)
        btn3 = findViewById(R.id.btn_meta_3)
        btn4 = findViewById(R.id.btn_meta_4)
        btnNone = findViewById(R.id.btn_meta_none)

        val listener = View.OnClickListener { v ->
            if (!running) return@OnClickListener
            when (v.id) {
                R.id.btn_meta_1 -> onMetaPressed(0)
                R.id.btn_meta_2 -> onMetaPressed(1)
                R.id.btn_meta_3 -> onMetaPressed(2)
                R.id.btn_meta_4 -> onMetaPressed(3)
                R.id.btn_meta_none -> onNonePressed()
            }
        }
        btn1.setOnClickListener(listener)
        btn2.setOnClickListener(listener)
        btn3.setOnClickListener(listener)
        btn4.setOnClickListener(listener)
        btnNone?.setOnClickListener(listener)

        backArrow.setOnClickListener {
            showExitConfirmation { finish() }
        }
    }

    private fun setUpBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmation { finish() }
            }
        })
    }

    private fun applyHeaders() {
        tvLevel.text = getString(R.string.level_title, currentLevel)
        tvScore.text = getString(R.string.score_label, 0)
    }

    private fun showVamosThenStart() {
        tvVamos.alpha = 0f
        tvVamos.visibility = View.VISIBLE
        val fadeIn = ObjectAnimator.ofFloat(tvVamos, "alpha", 0f, 1f).apply { duration = 600 }
        val hold = ObjectAnimator.ofFloat(tvVamos, "alpha", 1f, 1f).apply { duration = 500 }
        val fadeOut = ObjectAnimator.ofFloat(tvVamos, "alpha", 1f, 0f).apply { duration = 250 }

        fadeIn.start()
        fadeIn.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                hold.start()
                hold.addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        fadeOut.start()
                        fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: android.animation.Animator) {
                                tvVamos.visibility = View.GONE
                                startLevel()
                            }
                        })
                    }
                })
            }
        })
    }

    private fun startLevel() {
        indexExercise = 0
        errorsCount = 0
        correctCount = 0
        updateErrorsCounter()
        levelStartTime = System.currentTimeMillis()
        startLevelTimer()
        startGlobalVisibleTimer()
        nextExercise()
    }

    private fun startLevelTimer() {
        levelTimer?.cancel()
        timeProgress.progress = 0

        levelTimer = object : CountDownTimer(totalLevelTimeMs, 50) {
            override fun onTick(msLeft: Long) {
                val elapsed = totalLevelTimeMs - msLeft
                val progress = ((elapsed.toDouble() / totalLevelTimeMs) * 1000).toInt()
                timeProgress.progress = min(1000, max(0, progress))
            }
            override fun onFinish() {
                timeProgress.progress = 1000
            }
        }.start()
    }

    private fun startGlobalVisibleTimer() {
        globalTimer?.cancel()
        chrono.visibility = View.VISIBLE
        chrono.text = getString(R.string.initial_time_format)


        globalTimer = object : CountDownTimer(300000L, 20) {
            override fun onTick(msLeft: Long) {
                val elapsed = 300000L - msLeft
                val seconds = elapsed / 1000.0
                chrono.text = String.format(Locale.getDefault(), "%05.2f", seconds)
            }
            override fun onFinish() {

            }
        }.start()
    }

    private fun nextExercise() {
        if (indexExercise >= totalExercises) {
            finishLevel()
            return
        }

        dualExerciseState = DualExerciseState.WAITING_FIRST
        dualResponsesGiven = 0
        firstResponseCorrect = false

        applyFgHidingIfNeeded()

        if (isTwoTerms) {
            singleSlot.visibility = View.GONE
            dualSlot.visibility = View.VISIBLE
            val wantsMatch = correctnessPlan[indexExercise]
            renderDualExercise(wantsMatch)
            startExerciseTimer()
            startDualMovement()
        } else {
            dualSlot.visibility = View.GONE
            singleSlot.visibility = View.VISIBLE
            val wantsMatch = correctnessPlan[indexExercise]
            renderSingleExercise(wantsMatch)
            startExerciseTimer()
            startSingleMovement()
        }

        running = true
    }

    private fun startExerciseTimer() {
        exerciseTimer?.cancel()

    }

    private fun startExerciseResponseTimer() {
        exerciseTimer = object : CountDownTimer(perExerciseMs, 40) {
            override fun onTick(msLeft: Long) {

            }
            override fun onFinish() {
                if (running) {
                    running = false
                    handleNoAnswer()
                }
            }
        }.start()
    }

    private fun buildLevelMetas(subtype: Int) {

        val start = System.currentTimeMillis()

        metas.clear()
        when (subtype) {
            in 1..10 -> {
                val range = when(subtype) {
                    1 -> 4..20
                    2 -> 6..35
                    3 -> 6..35
                    4 -> -4..20
                    5 -> -10..35
                    6 -> -10..20
                    7 -> 1..99
                    8 -> 1..112
                    9 -> 1..50
                    10 -> -12..50
                    else -> 1..48
                }
                val chosen = pickDistinctInts(4, range, forbidZero = true)
                chosen.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForNumbers(chosen)
            }
            11 -> {
                val chosen = pickDistinctInts(4, -10..20, forbidZero = true)
                chosen.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForRomans(chosen)
            }
            12 -> {
                val chosen = pickDistinctInts(4, -10..12, forbidZero = true)
                chosen.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForLetters(chosen)
            }
            13 -> {
                val letters = pickDistinctInts(2, -10..12, forbidZero = true)
                val romans = pickDistinctInts(2, -10..12, forbidZero = true, exclude = letters.toSet())
                val all = letters + romans
                all.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForLettersAndRomans(letters, romans)
            }
            14 -> {
                val pairs = pickDistinctFigurePairs()
                pairs.forEach { (a, ac, b, bc) -> metas += Meta.PairFig(a, ac, b, bc) }
                setMetaButtonsForFigures(pairs)
                hiddenMetaIndexForFg = Random.nextInt(0, 4)
            }
        }

        btnNone?.text = resolveNoneText()
        btnNone?.compoundDrawablePadding = dp(4)
        btnNone?.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

        Log.d("PERF", "buildLevelMetas terminó en ${System.currentTimeMillis() - start} ms")
    }

    private data class Expr(val text: String, val result: Int?, val signature: String)

    private fun renderDualExercise(wantsMatch: Boolean) {
        val used = mutableSetOf<String>()
        val metasValues = metas.filterIsInstance<Meta.Numeric>().map { it.value }.toSet()

        val leftIsMatch = wantsMatch && Random.nextBoolean()
        val rightIsMatch = if (leftIsMatch) false else wantsMatch

        val left = buildExprForSubtypeTwoTerms(subtype, metasValues, mustMatch = leftIsMatch, usedSignatures = used)
        used += left.signature
        val right = buildExprForSubtypeTwoTerms(subtype, metasValues, mustMatch = rightIsMatch, usedSignatures = used)

        setColoredExercise(tvLeft, left.text)
        setColoredExercise(tvRight, right.text)

        leftExpectedMetaIndex = if (leftIsMatch) indexOfFirstInMetas(left.result) else -1
        rightExpectedMetaIndex = if (rightIsMatch) indexOfFirstInMetas(right.result) else -1

    }

    private fun renderSingleExercise(wantsMatch: Boolean) {
        val metasValues = metas.filterIsInstance<Meta.Numeric>().map { it.value }.toSet()
        val expr = buildExprForSubtypeThreeTerms(subtype, metasValues, mustMatch = wantsMatch)
        setColoredExercise(tvSingle, expr.text)
        currentExpectedMetaIndex = if (wantsMatch) indexOfFirstInMetas(expr.result) else -1
    }

    private fun startDualMovement() {
        boardContainer.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                boardContainer.viewTreeObserver.removeOnPreDrawListener(this)
                val width = boardContainer.width.toFloat()


                tvLeft.translationX = -width * 0.15f
                tvRight.translationX = width * 0.15f
                tvLeft.translationY = -dp(dualVerticalOffsetDp).toFloat()
                tvRight.translationY = dp(dualVerticalOffsetDp).toFloat()

                startExerciseResponseTimer()

                ObjectAnimator.ofFloat(tvLeft, "translationX", -width * 0.15f, width * 0.85f).apply {
                    duration = perExerciseMs
                    start()
                }

                ObjectAnimator.ofFloat(tvRight, "translationX", width * 0.15f, -width * 0.85f).apply {
                    duration = perExerciseMs
                    start()
                }

                return true
            }
        })
    }

    private fun startSingleMovement() {
        boardContainer.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                boardContainer.viewTreeObserver.removeOnPreDrawListener(this)
                val width = boardContainer.width.toFloat()
                val fromLeft = Random.nextBoolean()

                val visibleX = if (fromLeft) -width * 0.15f else width * 0.15f
                tvSingle.translationX = visibleX
                tvSingle.translationY = 0f

                startExerciseResponseTimer()

                val endX = if (fromLeft) width * 0.85f else -width * 0.85f
                ObjectAnimator.ofFloat(tvSingle, "translationX", visibleX, endX).apply {
                    duration = perExerciseMs
                    start()
                }

                return true
            }
        })
    }

    private var currentExpectedMetaIndex: Int = -1

    private fun onMetaPressed(index: Int) {
        if (!running) return

        val pressedButton = listOf(btn1, btn2, btn3, btn4)[index]
        lastPressedButton = pressedButton
        applySoftBounceEffect(pressedButton)

        val correct = if (isTwoTerms) {
            (index == leftExpectedMetaIndex || index == rightExpectedMetaIndex)
        } else {
            (index == currentExpectedMetaIndex)
        }
        applyButtonFeedback(pressedButton, correct)

        if (isTwoTerms) {
            lastPressedIndex = index
            handleDualExerciseResponse()
        } else {

            running = false
            exerciseTimer?.cancel()

            if (correct) correctCount++ else errorsCount++
            updateErrorsCounter()

            boardContainer.postDelayed({
                proceedOrFinish()
            }, 400)
        }
    }

    private fun onNonePressed() {
        if (!running) return

        lastPressedButton = btnNone
        applySoftBounceEffect(btnNone!!)

        val correct = if (isTwoTerms) {
            (leftExpectedMetaIndex == -1 || rightExpectedMetaIndex == -1)
        } else {
            (currentExpectedMetaIndex == -1)
        }
        applyButtonFeedback(btnNone!!, correct)

        if (isTwoTerms) {
            lastPressedIndex = -1
            handleDualExerciseResponse()
        } else {

            running = false
            exerciseTimer?.cancel()

            if (correct) correctCount++ else errorsCount++
            updateErrorsCounter()

            boardContainer.postDelayed({
                proceedOrFinish()
            }, 400)
        }
    }

    private fun handleDualExerciseResponse() {
        dualResponsesGiven++

        if (dualExerciseState == DualExerciseState.WAITING_FIRST) {
            isDoubleNingunoExercise = (leftExpectedMetaIndex == -1 && rightExpectedMetaIndex == -1)
        }

        val isValidForLeft = (lastPressedIndex == leftExpectedMetaIndex && leftExpectedMetaIndex != -99)
        val isValidForRight = (lastPressedIndex == rightExpectedMetaIndex && rightExpectedMetaIndex != -99)
        val isNingunoPressed = (lastPressedIndex == -1)

        val responseIsValid = if (isDoubleNingunoExercise && isNingunoPressed) {

            true
        } else {

            isValidForLeft || isValidForRight
        }

        when (dualExerciseState) {
            DualExerciseState.WAITING_FIRST -> {
                firstResponseCorrect = responseIsValid
                dualExerciseState = DualExerciseState.WAITING_SECOND

                if (!responseIsValid) {
                    errorsCount++
                    updateErrorsCounter()
                }

                if (!isDoubleNingunoExercise) {
                    if (isValidForLeft) {
                        leftExpectedMetaIndex = -99
                    }
                    if (isValidForRight) {
                        rightExpectedMetaIndex = -99
                    }
                }
            }

            DualExerciseState.WAITING_SECOND -> {
                dualExerciseState = DualExerciseState.COMPLETED

                if (!responseIsValid) {
                    errorsCount++
                }

                val exerciseCorrect = firstResponseCorrect || responseIsValid
                if (exerciseCorrect) {
                    correctCount++
                }

                updateErrorsCounter()
                running = false
                exerciseTimer?.cancel()

                isDoubleNingunoExercise = false

                boardContainer.postDelayed({
                    proceedOrFinish()
                }, 400)
            }

            DualExerciseState.COMPLETED -> {
                return
            }
        }
    }

    private fun applySoftBounceEffect(button: Button) {
        val scaleDown = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f).apply { duration = 30 }
        val scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f).apply { duration = 30 }
        val scaleUp = ObjectAnimator.ofFloat(button, "scaleX", 0.95f, 1f).apply { duration = 30 }
        val scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 0.95f, 1f).apply { duration = 30 }

        val animatorSet = android.animation.AnimatorSet()
        animatorSet.playTogether(scaleDown, scaleDownY)
        animatorSet.playTogether(scaleUp, scaleUpY)
        animatorSet.playSequentially(scaleDown, scaleUp)
        animatorSet.start()
    }

    private fun applyButtonFeedback(button: Button, success: Boolean) {
        val originalBackground = button.background
        val feedbackResource = if (success) R.drawable.sombra_correcta else R.drawable.sombra_incorrecta

        button.setBackgroundResource(feedbackResource)

        boardContainer.postDelayed({
            button.background = originalBackground
        }, 120)
    }

    private fun handleNoAnswer() {
        if (isTwoTerms) {

            errorsCount += 2
        } else {

            errorsCount++
        }

        updateErrorsCounter()
        applyFeedback()
        proceedOrFinish()
    }

    private fun updateErrorsCounter() {
        val maxErrors = if (isTwoTerms) 12 else 6
        val counterText = getString(R.string.errors_counter_format_dynamic, errorsCount, maxErrors)
        tvErrorsCounter.text = counterText
    }

    private fun applyFeedback() {
        val color = R.color.red
        val overlay = View(this).apply {
            setBackgroundColor(ContextCompat.getColor(this@GameActivityFocoPlus, color))
            alpha = 0f
        }
        boardContainer.addView(
            overlay,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        overlay.animate().alpha(0.18f).setDuration(90).withEndAction {
            overlay.animate().alpha(0f).setDuration(180).withEndAction {
                boardContainer.removeView(overlay)
            }.start()
        }.start()
    }

    private fun proceedOrFinish() {
        indexExercise++
        val maxErrors = if (isTwoTerms) 12 else 6
        if (errorsCount >= maxErrors) {
            finishLevel()
            return
        }

        boardContainer.postDelayed({
            nextExercise()
        }, 400)
    }

    private fun finishLevel() {
        levelTimer?.cancel()
        globalTimer?.cancel()
        exerciseTimer?.cancel()
        rawTimeSpent = (System.currentTimeMillis() - levelStartTime) / 1000.0

        val isSuccessful = errorsCount < maxErrorsAllowed && correctCount > 0

        val intent = Intent(this, LevelResultActivityFocoPlus::class.java)
        intent.putExtra("LEVEL", currentLevel)
        intent.putExtra("CORRECT", correctCount)
        intent.putExtra("TOTAL", totalExercises)
        intent.putExtra("ERRORS", errorsCount)
        intent.putExtra("MAX_ERRORS", maxErrorsAllowed)
        intent.putExtra("TOTAL_TIME", rawTimeSpent)
        intent.putExtra("DIFFICULTY", currentDifficulty)
        intent.putExtra("IS_SUCCESSFUL", isSuccessful)
        startActivity(intent)
        finish()
    }

    private fun buildExprForSubtypeTwoTerms(
        subtype: Int,
        metas: Set<Int>,
        mustMatch: Boolean,
        usedSignatures: MutableSet<String>
    ): Expr {

        val start = System.currentTimeMillis()

        repeat(200) {
            when (subtype) {

                1, 2 -> {
                    val a = pickInt(1, 12, forbidZero = true)
                    val b = pickInt(1, 12, forbidZero = true)
                    val r = a + b
                    if (r == 0) return@repeat
                    if (isPermutationUsed(usedSignatures, 'S', a, b)) return@repeat
                    val text = "$a + $b"
                    if (mustMatch == metas.contains(r)) {
                        val norm = normSig('S', a, b)
                        return Expr(text, r, norm)
                    }
                }

                4, 5 -> {
                    val putNegOnA = (usedSignatures.count { it == "SR-A" } < 14)
                    val putNegOnB = !putNegOnA
                    val a0 = pickInt(1, 12, forbidZero = true)
                    val b0 = pickInt(1, 12, forbidZero = true)
                    val a = if (putNegOnA) -a0 else a0
                    val b = if (putNegOnB) -b0 else b0

                    val r = a + b
                    if (r == 0) return@repeat
                    if (isPermutationUsed(usedSignatures, 'R', a, b)) return@repeat
                    val text = "${fmt(a)} + ${fmt(b)}"
                    if (mustMatch == metas.contains(r)) {
                        val norm = normSig('R', a, b)
                        usedSignatures += if (putNegOnA) "SR-A" else "SR-B"
                        return Expr(text, r, norm)
                    }
                }

                7 -> {
                    val range = 1..20
                    val a = pickInt(range.first, range.last, forbidZero = true)
                    val b = pickInt(range.first, range.last, forbidZero = true)
                    val r = a * b
                    if (r == 0) return@repeat
                    if (isPermutationUsed(usedSignatures, 'M', a, b)) return@repeat
                    val text = "$a × $b"
                    if (mustMatch == metas.contains(r)) {
                        val norm = normSig('M', a, b)
                        return Expr(text, r, norm)
                    }
                }

                9 -> {
                    val divisor = pickInt(1, 12, forbidZero = true)
                    val target = if (mustMatch && metas.isNotEmpty()) metas.random() else pickInt(1, 12, forbidZero = true)
                    val dividend = divisor * target
                    if (dividend == 0) return@repeat
                    if (usedSignatures.contains("D:$dividend/$divisor")) return@repeat
                    val text = "$dividend ÷ $divisor"
                    if (mustMatch || !metas.contains(target)) {
                        return Expr(text, target, "D:$dividend/$divisor")
                    }
                }

                13 -> {
                    val a = pickInt(1, 12, forbidZero = true)
                    val b = pickInt(1, 12, forbidZero = true)
                    val signPlus = Random.nextBoolean()
                    val r = if (signPlus) a + b else a - b
                    if (r == 0) return@repeat
                    if (isPermutationUsed(usedSignatures, if (signPlus) 'S' else 'Q', a, b)) return@repeat
                    val left = if (Random.nextBoolean()) letterOf(a) else romanOf(a)
                    val right = if (Random.nextBoolean()) letterOf(b) else romanOf(b)
                    val op = if (signPlus) "+" else "−"
                    val text = "$left $op $right"
                    if (mustMatch == metas.contains(r)) {
                        val norm = normSig(if (signPlus) 'S' else 'Q', a, b)
                        return Expr(text, r, norm)
                    }
                }

                14 -> {

                    val pairs = metasFiguresAsSet()
                    val left = randomFigurePair()
                    val right = randomFigurePair()
                    val leftMatch = pairs.contains(left)
                    val rightMatch = pairs.contains(right)
                    val hasMatch = leftMatch || rightMatch

                    if ((mustMatch && hasMatch && !(leftMatch && rightMatch)) ||
                        (!mustMatch && !hasMatch)) {
                        val leftText = "◼︎◼︎"
                        val rightText = "◻︎◻︎"
                        val sig = "FG:${left.first}-${left.second}|${right.first}-${right.second}"
                        tvLeft.text = leftText
                        tvRight.text = rightText

                        paintFigurePairOnBoard(tvLeft, left)
                        paintFigurePairOnBoard(tvRight, right)

                        currentExpectedMetaIndex = when {
                            leftMatch -> indexOfPairInMetas(left)
                            rightMatch && !leftMatch -> indexOfPairInMetas(right)
                            else -> -1
                        }

                        return Expr(sig, null, sig)
                    }
                }
            }
        }
        Log.d("PERF", "buildExprForSubtypeTwoTerms terminó en ${System.currentTimeMillis() - start} ms")
        return Expr("—", null, "—")
    }

    private fun buildExprForSubtypeThreeTerms(
        subtype: Int,
        metas: Set<Int>,
        mustMatch: Boolean
    ): Expr {

        val start = System.currentTimeMillis()

        repeat(200) {
            when (subtype) {
                3 -> {
                    val a = pickInt(1, 12, true); val b = pickInt(1, 12, true); val c = pickInt(1, 12, true)
                    val r = a + b + c
                    if (r == 0) return@repeat
                    if (mustMatch == metas.contains(r)) return Expr("$a + $b + $c", r, "S3:$a,$b,$c")
                }
                6 -> {
                    val putNegOnFirst = (countForKey("SR3-N1") < 7)
                    val a0 = pickInt(1, 12, true)
                    val b0 = pickInt(1, 12, true)
                    val c0 = pickInt(1, 12, true)
                    val a = if (putNegOnFirst) -a0 else a0
                    val b = if (!putNegOnFirst) -b0 else b0
                    val c = c0
                    val r = a + b + c
                    if (r == 0) return@repeat
                    if (mustMatch == metas.contains(r)) {
                        incKey(if (putNegOnFirst) "SR3-N1" else "SR3-N2")
                        return Expr("${fmt(a)} + ${fmt(b)} + ${fmt(c)}", r, "SR3:$a,$b,$c")
                    }
                }
                8 -> {
                    val a = pickInt(1, 9, true); val b = pickInt(1, 9, true); val c = pickInt(1, 12, true)
                    val plus = Random.nextBoolean()
                    val r = if (plus) (a * b + c) else (a * b - c)
                    if (r == 0) return@repeat
                    val op = if (plus) "+" else "−"
                    if (mustMatch == metas.contains(r)) return Expr("$a × $b $op $c", r, "MS:$a,$b,$c,$plus")
                }
                10 -> {
                    val divisor = pickInt(1, 12, true)
                    val base = pickInt(1, 12, true)
                    val dividend = base * divisor
                    val k = -pickInt(1, 12, true)
                    val r = base + k
                    if (r == 0) return@repeat
                    val text = "$dividend ÷ $divisor ${if (k < 0) "−" else "+"} ${abs(k)}"
                    if (mustMatch == metas.contains(r)) return Expr(text, r, "DR:$dividend,$divisor,$k")
                }
                11 -> {
                    val a = pickInt(1, 12, true); val b = pickInt(1, 12, true); val c = pickInt(1, 12, true)
                    val negOnFirst = (countForKey("R3-N1") < 7)
                    val aa = if (negOnFirst) -a else a
                    val bb = if (!negOnFirst) -b else b
                    val cc = c
                    val r = aa + bb + cc
                    if (r == 0) return@repeat
                    val text = "${romanFmt(aa)} + ${romanFmt(bb)} + ${romanFmt(cc)}"
                    if (mustMatch == metas.contains(r)) {
                        incKey(if (negOnFirst) "R3-N1" else "R3-N2")
                        return Expr(text, r, "R3:$aa,$bb,$cc")
                    }
                }
                12 -> {
                    val a = pickInt(1, 12, true); val b = pickInt(1, 12, true); val c = pickInt(1, 12, true)
                    val negOnFirst = (countForKey("L3-N1") < 7)
                    val aa = if (negOnFirst) -a else a
                    val bb = if (!negOnFirst) -b else b
                    val cc = c
                    val r = aa + bb + cc
                    if (r == 0) return@repeat
                    val text = "${letterFmt(aa)} + ${letterFmt(bb)} + ${letterFmt(cc)}"
                    if (mustMatch == metas.contains(r)) {
                        incKey(if (negOnFirst) "L3-N1" else "L3-N2")
                        return Expr(text, r, "L3:$aa,$bb,$cc")
                    }
                }
            }
        }

        Log.d("PERF", "buildExprForSubtypeThreeTerms terminó en ${System.currentTimeMillis() - start} ms")

        return Expr("—", null, "—")
    }


    private fun setMetaButtonsForNumbers(values: List<Int>) {
        val b = listOf(btn1, btn2, btn3, btn4)
        for (i in 0 until 4) {
            b[i].text = values[i].toString()
            b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun setMetaButtonsForRomans(values: List<Int>) {
        val b = listOf(btn1, btn2, btn3, btn4)
        for (i in 0 until 4) {
            b[i].text = romanOf(values[i])
            b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun setMetaButtonsForLetters(values: List<Int>) {
        val b = listOf(btn1, btn2, btn3, btn4)
        for (i in 0 until 4) {
            b[i].text = letterOf(values[i])
            b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun setMetaButtonsForLettersAndRomans(letters: List<Int>, romans: List<Int>) {
        val all = letters + romans
        val b = listOf(btn1, btn2, btn3, btn4)
        for (i in 0 until 4) {
            val v = all[i]
            b[i].text = if (i < 2) letterOf(v) else romanOf(v)
            b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun setMetaButtonsForFigures(pairs: List<Quad>) {
        val b = listOf(btn1, btn2, btn3, btn4)
        for (i in 0 until 4) {
            b[i].text = ""
            val layer = makePairDrawable(pairs[i].aIcon, pairs[i].aColor, pairs[i].bIcon, pairs[i].bColor)
            b[i].compoundDrawablePadding = dp(6)
            b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, layer, null, null)
        }
    }

    private fun applyFgHidingIfNeeded() {
        if (subtype != 14) return
        val b = listOf(btn1, btn2, btn3, btn4)
        val showFigures = indexExercise < 11
        for (i in 0 until 4) {
            val meta = metas[i] as Meta.PairFig
            if (!showFigures && i == hiddenMetaIndexForFg) {
                b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                b[i].text = ""
            } else {
                b[i].text = ""
                val layer = makePairDrawable(meta.aIcon, meta.aColor, meta.bIcon, meta.bColor)
                b[i].compoundDrawablePadding = dp(6)
                b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, layer, null, null)
            }
        }
    }

    private fun pickInt(minV: Int, maxV: Int, forbidZero: Boolean, exclude: Set<Int> = emptySet()): Int {
        var v: Int
        do {
            v = Random.nextInt(minV, maxV + 1)
        } while ((forbidZero && v == 0) || exclude.contains(v))
        return v
    }

    private fun pickDistinctInts(count: Int, range: IntRange, forbidZero: Boolean, exclude: Set<Int> = emptySet()): List<Int> {
        val set = linkedSetOf<Int>()
        while (set.size < count) {
            val v = pickInt(range.first, range.last, forbidZero, exclude)
            set += v
        }
        return set.toList()
    }


    private fun fmt(n: Int): String = if (n < 0) "−${abs(n)}" else "$n"
    private fun letterOf(v: Int): String = "ABCDEFGHIJKL"[max(1, min(12, v)) - 1].toString()
    private fun letterFmt(v: Int): String = if (v < 0) "−${letterOf(abs(v))}" else letterOf(v)

    private fun romanOf(v: Int): String {

        return when (v) {
            1 -> "I"; 2 -> "II"; 3 -> "III"; 4 -> "IV"; 5 -> "V"; 6 -> "VI"
            7 -> "VII"; 8 -> "VIII"; 9 -> "IX"; 10 -> "X"; 11 -> "XI"; 12 -> "XII"
            else -> v.toString()
        }
    }

    private fun romanFmt(v: Int): String = if (v < 0) "−${romanOf(abs(v))}" else romanOf(v)

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()

    private fun isTwoTermsSubtype(sub: Int): Boolean {

        return when (sub) {
            1, 2, 4, 5, 7, 9, 13, 14 -> true
            else -> false
        }
    }

    private val quotaCounters = HashMap<String, Int>()
    private fun countForKey(key: String): Int = quotaCounters[key] ?: 0
    private fun incKey(key: String) { quotaCounters[key] = (quotaCounters[key] ?: 0) + 1 }


    private fun isPermutationUsed(used: Set<String>, op: Char, a: Int, b: Int): Boolean {
        val norm = normSig(op, a, b)
        return used.contains(norm)
    }

    private fun normSig(op: Char, a: Int, b: Int): String {
        return if (op == 'D') "D:$a/$b" else {
            val x = min(a, b)
            val y = max(a, b)
            "$op:$x,$y"
        }
    }

    private fun indexOfFirstInMetas(value: Int?): Int {
        if (value == null) return -1
        val list = metas.filterIsInstance<Meta.Numeric>().map { it.value }
        val idx = list.indexOf(value)
        return if (idx in 0..3) idx else -1
    }

    private data class Quad(val aIcon: Int, val aColor: Int, val bIcon: Int, val bColor: Int)

    private fun randomFigurePair(): Pair<Int, Int> {
        val a = iconsRandomId()
        val b = iconsRandomId(excludeIcons = setOf(a))
        return Pair(a, b)
    }

    private fun pickDistinctFigurePairs(): List<Quad> {
        val out = mutableListOf<Quad>()
        val used = mutableSetOf<Set<Int>>()
        while (out.size < 4) {
            val aIcon = iconsRandomId()
            val bIcon = iconsRandomId(excludeIcons = setOf(aIcon))
            val set = setOf(aIcon, bIcon)
            if (used.contains(set)) continue
            used += set
            val aColor = colorsRandom()
            val bColor = colorsRandom()
            out += Quad(aIcon, aColor, bIcon, bColor)
        }
        return out
    }

    private fun iconsRandomId(excludeIcons: Set<Int> = emptySet()): Int {
        while (true) {
            val name = iconNames.random()
            val id = resources.getIdentifier(name, "drawable", packageName)
            if (id != 0 && !excludeIcons.contains(id)) return id
        }
    }

    private fun colorsRandom(): Int = colorResIds.random()

    private fun metasFiguresAsSet(): Set<Pair<Int, Int>> {
        val set = mutableSetOf<Pair<Int, Int>>()
        metas.forEach {
            if (it is Meta.PairFig) {

                set += normalizedPair(it.aIcon, it.bIcon)
            }
        }
        return set
    }

    private fun indexOfPairInMetas(pair: Pair<Int, Int>): Int {
        val norm = normalizedPair(pair.first, pair.second)
        metas.forEachIndexed { idx, m ->
            if (m is Meta.PairFig) {
                val mNorm = normalizedPair(m.aIcon, m.bIcon)
                if (mNorm == norm) return idx
            }
        }
        return -1
    }

    private fun normalizedPair(a: Int, b: Int): Pair<Int, Int> =
        if (a <= b) Pair(a, b) else Pair(b, a)

    private fun makePairDrawable(aIcon: Int, aColorRes: Int, bIcon: Int, bColorRes: Int): Drawable? {

        val start = System.currentTimeMillis()

        val d1 = AppCompatResources.getDrawable(this, aIcon)?.mutate() ?: return null
        val d2 = AppCompatResources.getDrawable(this, bIcon)?.mutate() ?: return null
        DrawableCompat.setTint(d1, ContextCompat.getColor(this, aColorRes))
        DrawableCompat.setTint(d2, ContextCompat.getColor(this, bColorRes))

        val layer = LayerDrawable(arrayOf(d1, d2))
        val size = dp(28)
        d1.setBounds(0, 0, size, size)
        d2.setBounds(0, 0, size, size)

        val inset = dp(12)
        layer.setLayerInset(0, 0, 0, inset, 0)
        layer.setLayerInset(1, inset, 0, 0, 0)

        Log.d("PERF", "makePairDrawable terminó en ${System.currentTimeMillis() - start} ms")

        return layer
    }

    private fun paintFigurePairOnBoard(target: TextView, pair: Pair<Int, Int>) {

        val layer = makePairDrawable(pair.first, randomTint(), pair.second, randomTint())
        target.setCompoundDrawablesRelativeWithIntrinsicBounds(null, layer, null, null)
        target.compoundDrawablePadding = dp(6)
        target.text = ""
    }

    private fun randomTint(): Int = colorResIds.random()

    private fun buildCorrectnessPlan(total: Int): MutableList<Boolean> {
        val start = System.currentTimeMillis()
        val plan = MutableList(total) { false }
        var placed = 0
        val wantCorrect = 6

        while (placed < wantCorrect) {
            val i = Random.nextInt(0, total)
            if (!plan[i]) { plan[i] = true; placed++ }
        }

        Log.d("PERF", "buildCorrectnessPlan terminó en ${System.currentTimeMillis() - start} ms")
        return plan
    }

    private fun resolveNoneText(): String {
        val candidates = arrayOf("none_option", "ninguno", "btn_none")
        for (name in candidates) {
            val id = resources.getIdentifier(name, "string", packageName)
            if (id != 0) return getString(id)
        }
        return "NINGUNO"
    }

    private fun showExitConfirmation(onConfirm: () -> Unit) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(R.string.btn_yes) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.btn_no, null)
        builder.create().show()
    }

    private fun colorizeNegatives(text: String): CharSequence {
        val white = android.graphics.Color.WHITE
        val red = ContextCompat.getColor(this, R.color.red)

        val sb = android.text.SpannableStringBuilder(text)
        sb.setSpan(
            android.text.style.ForegroundColorSpan(white),
            0, sb.length,
            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        val chars = text.toCharArray()
        var i = 0
        while (i < chars.size) {
            val c = chars[i]
            val isMinus = (c == '-' || c == '−')
            if (isMinus) {
                val start = i
                var j = i + 1


                if (j < chars.size && chars[j].isWhitespace()) {
                    j++
                }

                fun isTokenChar(ch: Char): Boolean {
                    val up = ch.uppercaseChar()
                    val isRoman = (up == 'M' || up == 'D' || up == 'C' || up == 'L' || up == 'X' || up == 'V' || up == 'I')
                    val isLetter = up in 'A'..'Z'
                    val isDigit = ch in '0'..'9'
                    val isDot = ch == '.'
                    return isRoman || isLetter || isDigit || isDot
                }

                while (j < chars.size) {
                    val ch = chars[j]
                    val isStop = ch.isWhitespace() ||
                            ch == '+' || ch == '×' || ch == '÷' || ch == '/' ||
                            ch == '-' || ch == '−' || ch == '(' || ch == ')' || ch == ','

                    if (isStop) break
                    if (!isTokenChar(ch)) break
                    j++
                }

                sb.setSpan(
                    android.text.style.ForegroundColorSpan(red),
                    start, j,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                i = j
                continue
            }
            i++
        }

        return sb
    }

    private fun setColoredExercise(textView: TextView, rawText: String) {
        textView.setTextColor(android.graphics.Color.WHITE)
        textView.text = colorizeNegatives(rawText)
    }

}
