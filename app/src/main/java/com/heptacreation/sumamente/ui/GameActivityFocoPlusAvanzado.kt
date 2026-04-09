package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import com.heptacreation.sumamente.R
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random


class GameActivityFocoPlusAvanzado : BaseActivity() {

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
    private var rawTimeSpent: Double = 0.0
    private var exerciseTimer: CountDownTimer? = null
    private lateinit var answersGrid: GridLayout
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var correctnessPlan: MutableList<Boolean>
    private var preGeneratedExercises: MutableList<Pair<String, String>> = mutableListOf()
    private var perExerciseMs: Long = 0L
    private var exerciseMetaIndices: MutableList<Int> = mutableListOf()
    private var extractedMetas: List<Int> = emptyList()
    private var leftExpectedMetaIndex: Int = -1
    private var rightExpectedMetaIndex: Int = -1
    private var lastPressedIndex: Int = -1
    private var lastPressedView: View? = null
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
    private var isGlobalTimerPausedVisual = false
    private var pauseCompensationMs: Long = 0L
    private var isLevelProgressPaused: Boolean = false
    private var levelProgressCompensationMs: Long = 0L
    private var preHideMessageShown = false
    private var dualExerciseState = DualExerciseState.WAITING_FIRST
    private var firstResponseCorrect = false
    private var firstResponseAnsweredLeft: Boolean = true
    private var isDoubleNingunoExercise = false
    private var exercisesShownList = mutableListOf<String>()
    private var timersStartedForThisExercise = false
    private var dualAnimLeft: ObjectAnimator? = null
    private var dualAnimRight: ObjectAnimator? = null
    private var userResponsesList = mutableListOf<String>()
    private var correctAnswersList = mutableListOf<String>()
    private val hintCostCoins = 7
    private var pistaActivada = false
    private var hintCountDownTimer: CountDownTimer? = null
    private lateinit var hintOptionsLayout: View
    private lateinit var tvHintDesc: TextView
    private lateinit var tvHintBalance: TextView
    private lateinit var btnUseHint: AppCompatButton
    private lateinit var btnSkipHint: AppCompatButton
    private lateinit var hintTimerBar: ProgressBar

    private enum class DualExerciseState {
        WAITING_FIRST,
        WAITING_SECOND,
        COMPLETED
    }

    private sealed class Meta {
        data class Numeric(val value: Int) : Meta()
    }
    private val metas: MutableList<Meta> = mutableListOf()

    private val dualVerticalOffsetPx by lazy {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, resources.displayMetrics)
    }
    private var hiddenMetaIndexForFg: Int = -1


    private val validMultiplicationTargets = listOf(
        8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 22, 24, 25, 26, 27, 28,
        30, 32, 33, 34, 35, 36, 38, 39, 40, 42, 44, 45, 46, 48, 49, 50,
        51, 52, 54, 55, 56, 57, 58, 60, 62, 63, 64, 65, 66, 68, 69, 70,
        72, 78, 80
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pistaActivada = false

        setContentView(R.layout.activity_game_foco_plus)

        AdManager.preloadInterstitial(this)

        currentLevel = intent.getIntExtra("LEVEL", 1)
        currentDifficulty = intent.getStringExtra("DIFFICULTY")
        subtype = intent.getIntExtra("SUBTYPE", 1)

        val miniblockIndex = (currentLevel - 1) / 14
        if (miniblockIndex > 0) {
            val levelIndexInsideBlock = (currentLevel - 1) % 14

            var order: IntArray? = intent.getIntArrayExtra("FOCO_SHUFFLE_ORDER")

            if (order == null) {
                val diffKey = (currentDifficulty ?: "AVANZADO")
                val prefs = getSharedPreferences("MyPrefsFocoPlus", MODE_PRIVATE)
                val key = "focoplus_miniblock_${diffKey}_${miniblockIndex}"
                val csv = prefs.getString(key, null)
                if (!csv.isNullOrBlank()) {
                    val list = csv.split(",").mapNotNull { it.toIntOrNull() }
                    if (list.size == 14) order = list.toIntArray()
                }
            }

            if (order != null && order.size == 14) {
                subtype = order[levelIndexInsideBlock]
            }
        }

        val levelSeed = currentLevel.toLong() + subtype * 1000L
        Random(levelSeed)

        ScoreManager.initFocoPlus(this)

        findViews()
        setUpBackHandler()
        applyHeaders()

        val baseTimeSeconds = 8.5 - (currentLevel - 1) * 0.01
        perExerciseMs = (baseTimeSeconds * 1000).toLong()

        isTwoTerms = isTwoTermsSubtype(subtype)

        if (subtype == 15) {
            val params = boardContainer.layoutParams as ConstraintLayout.LayoutParams
            params.dimensionRatio = "8:6"
            boardContainer.layoutParams = params
        }

        totalExercises = 14
        maxErrorsAllowed = if (isTwoTerms) 8 else 4

        val pauseTimeMs = (totalExercises - 1) * 1L
        totalLevelTimeMs = totalExercises * perExerciseMs + pauseTimeMs

        boardContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    boardContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    correctnessPlan = if (isTwoTerms) {
                        buildDualCorrectnessPlan(totalExercises)
                    } else {
                        buildSingleCorrectnessPlan(totalExercises)
                    }

                    buildLevelMetas(subtype)

                    verificarYMostrarPista()
                }
            }
        )
    }

    private fun buildDualCorrectnessPlan(exerciseCount: Int): MutableList<Boolean> {

        if (!isTwoTermsSubtype(subtype)) {
            return buildSingleCorrectnessPlan(exerciseCount)
        }

        preGeneratedExercises.clear()
        exerciseMetaIndices.clear()

        val generatedEquations = when (subtype) {
            1  -> generateSubtype1Equations()
            2  -> generateSubtype2Equations()
            4  -> generateSubtype4Equations()
            5  -> generateSubtype5Equations()
            7  -> generateSubtype7Equations()
            9  -> generateSubtype9Equations()
            12 -> generateSubtype12Equations()
            13 -> generateSubtype13Equations()
            else -> emptyList()
        }

        extractedMetas = generatedEquations
            .mapNotNull { it.metaValue }
            .distinct()
            .sorted()
            .take(5)

        generatedEquations.forEach { eq ->
            preGeneratedExercises.add(Pair(eq.left, eq.right ?: eq.left))

            val idx = when {
                eq.expectedMetaIndex == -1 -> -1
                else -> {
                    val mv = eq.metaValue
                    if (mv != null) extractedMetas.indexOf(mv) else -1
                }
            }
            exerciseMetaIndices.add(idx)

        }

        return MutableList(exerciseCount) { true }
    }

    private fun buildSingleCorrectnessPlan(exerciseCount: Int): MutableList<Boolean> {
        preGeneratedExercises.clear()
        exerciseMetaIndices.clear()

        val generatedEquations = when (subtype) {
            3  -> generateSubtype3Equations()
            6  -> generateSubtype6Equations()
            8  -> generateSubtype8Equations()
            10 -> generateSubtype10Equations()
            11 -> generateSubtype11Equations()
            14 -> generateSubtype14Equations()
            15 -> generateSubtype15Equations()
            else -> emptyList()
        }

        if (subtype in setOf(3, 6, 8, 10, 11, 14, 15)) {
            val (finalEquations, finalMetas) = selectFinal10Plus4(
                pool = generatedEquations,
                resultOf = { it.metaValue },
                isNone   = { it.expectedMetaIndex == -1 },
                textKeyOf = { it.left + "|" + (it.right ?: "") }
            )

            preGeneratedExercises.clear()
            exerciseMetaIndices.clear()

            finalEquations.forEach { eq ->
                preGeneratedExercises.add(Pair(eq.left, eq.right ?: eq.left))
                val mv = eq.metaValue
                val idx = if (mv != null) finalMetas.indexOf(mv) else -1
                exerciseMetaIndices.add(idx)
            }

            extractedMetas = finalMetas

            return MutableList(exerciseCount) { true }
        }

        generatedEquations.forEach { eq ->
            preGeneratedExercises.add(Pair(eq.left, eq.right ?: eq.left))
            exerciseMetaIndices.add(eq.expectedMetaIndex)
        }

        extractedMetas = generatedEquations
            .mapNotNull { it.metaValue }
            .distinct()
            .sorted()
            .take(5)

        val plan = MutableList(exerciseCount) { true }

        return plan
    }

    private fun <T> selectFinal10Plus4(
        pool: List<T>,
        resultOf: (T) -> Int?,
        isNone: (T) -> Boolean,
        textKeyOf: (T) -> String
    ): Pair<List<T>, List<Int>> {

        val withValue = pool.filter { !isNone(it) && resultOf(it) != null }
        val nonePool  = pool.filter { isNone(it) || resultOf(it) == null }
        val grouped: Map<Int, List<T>> = withValue.groupBy { resultOf(it)!! }
        val metasCon2 = grouped.filter { it.value.size >= 2 }.keys.toMutableList()

        metasCon2.shuffle()
        val metasElegidas = if (metasCon2.size >= 5) metasCon2.take(5).toMutableList() else metasCon2

        if (metasElegidas.size < 5) {
            val metasCon1 = grouped.filter { it.value.isNotEmpty() }.keys
                .filterNot { metasElegidas.contains(it) }
                .toMutableList()
            metasCon1.shuffle()
            while (metasElegidas.size < 5 && metasCon1.isNotEmpty()) {
                metasElegidas += metasCon1.removeAt(0)
            }
        }

        val usados = mutableSetOf<String>()
        val finalesConValor = mutableListOf<T>()
        metasElegidas.forEach { meta ->
            val candidates = (grouped[meta] ?: emptyList()).shuffled()
            var added = 0
            for (c in candidates) {
                val key = textKeyOf(c)
                if (usados.add(key)) {
                    finalesConValor += c
                    added++
                    if (added == 2) break
                }
            }
        }

        val deficitValor = 10 - finalesConValor.size
        if (deficitValor > 0) {
            val restantes = withValue.shuffled().filter { !usados.contains(textKeyOf(it)) && resultOf(it)?.let { r -> metasElegidas.contains(r) } == true }
            finalesConValor += restantes.take(deficitValor)
            restantes.forEach { usados.add(textKeyOf(it)) }
        }

        val finalesNinguno = mutableListOf<T>()
        val noneCandidates = (nonePool + withValue.filter { r -> resultOf(r)?.let { !metasElegidas.contains(it) } ?: false })
            .shuffled()
        for (c in noneCandidates) {
            val key = textKeyOf(c)
            val res = resultOf(c)
            if (usados.add(key) && (res == null || !metasElegidas.contains(res))) {
                finalesNinguno += c
                if (finalesNinguno.size == 4) break
            }
        }

        val finales = (finalesConValor + finalesNinguno).shuffled()
        return Pair(finales.take(14), metasElegidas.take(5))
    }

    override fun onDestroy() {
        super.onDestroy()
        exerciseTimer?.cancel()
        hintCountDownTimer?.cancel()
        levelTimer?.cancel()
        globalTimer?.cancel()

    }

    private fun findViews() {

        hintOptionsLayout = findViewById(R.id.hint_options_layout)
        tvHintDesc = findViewById(R.id.tv_hint_desc)
        tvHintBalance = findViewById(R.id.tv_hint_balance)
        btnUseHint = findViewById(R.id.btn_use_hint)
        btnSkipHint = findViewById(R.id.btn_skip_hint)
        hintTimerBar = findViewById(R.id.hint_timer_bar)

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
        chrono.visibility = View.VISIBLE
        chrono.text = getString(R.string.initial_time_format)

        timeProgress = findViewById(R.id.time_progress)
        tvErrorsCounter = findViewById(R.id.tv_errors_counter)

        answersGrid = findViewById(R.id.answers_grid)
        btn1 = findViewById(R.id.btn_meta_1)
        btn2 = findViewById(R.id.btn_meta_2)
        btn3 = findViewById(R.id.btn_meta_3)
        btn4 = findViewById(R.id.btn_meta_4)
        btn5 = findViewById(R.id.btn_meta_5)
        btnNone = findViewById(R.id.btn_meta_none)


        val listener = View.OnClickListener { v ->
            if (!running) return@OnClickListener
            when (v.id) {
                R.id.btn_meta_1 -> onMetaPressed(0)
                R.id.btn_meta_2 -> onMetaPressed(1)
                R.id.btn_meta_3 -> onMetaPressed(2)
                R.id.btn_meta_4 -> onMetaPressed(3)
                R.id.btn_meta_5 -> onMetaPressed(4)
                R.id.btn_meta_none -> onNonePressed()
            }
        }
        btn1.setOnClickListener(listener)
        btn2.setOnClickListener(listener)
        btn3.setOnClickListener(listener)
        btn4.setOnClickListener(listener)
        btn5.setOnClickListener(listener)
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
        tvScore.text = getString(R.string.score_label, ScoreManager.currentScoreFocoPlus)

    }

    private fun showVamosThenStart() {
        tvVamos.alpha = 0f
        tvVamos.visibility = View.VISIBLE
        val fadeIn = ObjectAnimator.ofFloat(tvVamos, "alpha", 0f, 1f).apply { duration = 600 }
        val hold = ObjectAnimator.ofFloat(tvVamos, "alpha", 1f, 1f).apply { duration = 500 }
        val fadeOut = ObjectAnimator.ofFloat(tvVamos, "alpha", 1f, 0f).apply { duration = 250 }

        fadeIn.start()
        fadeIn.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                hold.start()
                hold.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        fadeOut.start()
                        fadeOut.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
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
        preHideMessageShown = false
        updateErrorsCounter()
        levelStartTime = System.currentTimeMillis()

        nextExercise()
    }

    private fun startLevelTimer() {
        levelTimer?.cancel()
        timeProgress.progress = 0

        levelTimer = object : CountDownTimer(totalLevelTimeMs, 50) {
            override fun onTick(msLeft: Long) {
                if (isLevelProgressPaused) return
                val elapsedEffective = max(0L, (totalLevelTimeMs - msLeft) - levelProgressCompensationMs)
                val progress = ((elapsedEffective.toDouble() / totalLevelTimeMs) * 1000).toInt()
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
        
        globalTimer = object : CountDownTimer(300000L, 70) {
            override fun onTick(msLeft: Long) {
                if (!isGlobalTimerPausedVisual) {
                    val elapsedRaw = 300000L - msLeft
                    val elapsed = max(0L, elapsedRaw - pauseCompensationMs)

                    val seconds = elapsed / 1000.0
                    chrono.text = String.format(Locale.getDefault(), "%05.2f", seconds)
                }
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
        firstResponseCorrect = false
        timersStartedForThisExercise = false

        val miniblockIndex = (currentLevel - 1) / 14
        val activationMiniblocks = listOf(4, 7, 10, 13, 16, 19, 22, 25, 28)
        val activatedCount = activationMiniblocks.count { it <= miniblockIndex }
        val activeHideSubtypes = buildList {
            add(14)
            for (s in 1..activatedCount) {
                add(s)
            }
        }

        val hideFeatureActive = activeHideSubtypes.contains(subtype)

        if (hideFeatureActive && indexExercise == 10 && !preHideMessageShown) {

            running = false
            exerciseTimer?.cancel()
            isGlobalTimerPausedVisual = true
            isLevelProgressPaused = true

            tvLeft.clearAnimation()
            tvRight.clearAnimation()
            singleSlot.visibility = View.GONE
            dualSlot.visibility = View.GONE

            if (hiddenMetaIndexForFg !in 0..4) hiddenMetaIndexForFg = Random.nextInt(0, 5)

            tvVamos.text = getString(R.string.attention_hide_button)
            tvVamos.visibility = View.VISIBLE
            tvVamos.alpha = 1f
            tvVamos.bringToFront()
            tvVamos.elevation = 10f
            boardContainer.invalidate()

            val zoomIn = AnimationUtils.loadAnimation(this, R.anim.logo_zoom_in)
            tvVamos.startAnimation(zoomIn)

            val metasViews = getMetaOptionViews()
            val target = metasViews[hiddenMetaIndexForFg]
            val shakeNow = {
                val a = AnimationUtils.loadAnimation(this, R.anim.shake)
                a.duration = 300
                target.startAnimation(a)
            }

            boardContainer.postDelayed({ shakeNow() }, 0)
            boardContainer.postDelayed({ shakeNow() }, 1000)
            boardContainer.postDelayed({ shakeNow() }, 2000)

            boardContainer.postDelayed({
                tvVamos.visibility = View.GONE
                tvVamos.elevation = 0f
                pauseCompensationMs += 3000L
                levelProgressCompensationMs += 3000L
                isGlobalTimerPausedVisual = false
                isLevelProgressPaused = false
                running = true
                preHideMessageShown = true
                nextExercise()

            }, 3000)

            return
        }

        applyHidingIfNeeded(activeHideSubtypes)

        if (isTwoTerms) {
            singleSlot.visibility = View.GONE
            dualSlot.visibility = View.VISIBLE

            renderDualExercise()

            if (pistaActivada) aplicarPistaABotones()

            isDoubleNingunoExercise = false
            startExerciseTimer()
            startDualMovement()


        } else {
            dualSlot.visibility = View.GONE
            singleSlot.visibility = View.VISIBLE
            renderSingleExercise()

            if (pistaActivada) aplicarPistaABotones()

            startExerciseTimer()
            startSingleMovement()

        }

        running = true
    }

    private fun getMetaButtonContent(index: Int): String {
        return when {
            index == -1 -> getString(R.string.answer_none)
            index == -99 -> "-"
            subtype == 14 -> {
                val meta = metas.getOrNull(index)
                if (meta is Meta.Numeric) {
                    fmtDec(meta.value / 10.0)
                } else {
                    "?"
                }
            }
            subtype == 15 -> {
                val meta = metas.getOrNull(index)
                if (meta is Meta.Numeric) {
                    formatMinutesToTime(meta.value)
                } else {
                    "?"
                }
            }
            else -> {

                val meta = metas.getOrNull(index)
                if (meta is Meta.Numeric) {
                    meta.value.toString()
                } else {
                    "?"
                }
            }
        }
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

        answersGrid.visibility = View.VISIBLE


        when (subtype) {
            in 1..10, 11 -> {
                extractedMetas.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForNumbers(extractedMetas)
            }
            12 -> {
                extractedMetas.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForNumbers(extractedMetas)
                listOf(btn1, btn2, btn3, btn4, btn5).forEach { it.typeface = Typeface.DEFAULT }
            }
            13 -> {
                extractedMetas.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForNumbers(extractedMetas)
                listOf(btn1, btn2, btn3, btn4, btn5).forEach { it.typeface = Typeface.DEFAULT }
            }
            14 -> {
                extractedMetas.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForDecimals(extractedMetas)
            }
            15 -> {
                extractedMetas.forEach { metas += Meta.Numeric(it) }
                setMetaButtonsForTime(extractedMetas)
            }
        }

        btnNone?.text = getString(R.string.answer_none)


        Log.d("PERF", "buildLevelMetas termina en ${System.currentTimeMillis() - start} ms")
    }

    private fun setMetaButtonsForTime(values: List<Int>) {
        if (values.isEmpty() || values.size < 5) {
            Log.w("FocoPlus", "setMetaButtonsForTime: lista de metas insuficiente (${values.size})")
            return
        }

        val b = listOf(btn1, btn2, btn3, btn4, btn5)
        for (i in 0 until 5) {
            b[i].text = formatMinutesToTime(values[i])
            b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun setMetaButtonsForDecimals(values: List<Int>) {
        if (values.isEmpty() || values.size < 5) return
        val b = listOf(btn1, btn2, btn3, btn4, btn5)
        for (i in 0 until 5) {
            b[i].text = fmtDec(values[i] / 10.0)
            b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private data class GeneratedEquation(
        val left: String,
        val right: String? = null,
        val expectedMetaIndex: Int,
        val metaValue: Int? = null
    )

    private fun formatMinutesToTime(minutes: Int): String {
        val absMinutes = abs(minutes)
        val hours = absMinutes / 60
        val mins = absMinutes % 60
        val formatted = String.format(Locale.US, "%02d:%02d", hours, mins)
        return if (minutes < 0) "−$formatted" else formatted
    }

    private fun generateSubtype1Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()
        val metas = pickDistinctInts(5, 18..30, forbidZero = true)
        val divisors = List(5) { listOf(2, 3, 4).random() }

        for (metaIndex in 0..4) {
            val meta = metas[metaIndex]
            val divisor = divisors[metaIndex]

            val a = round(meta.toDouble() / divisor).toInt()
            val b = meta - a

            val baseEquations = listOf(
                "${sqrtFmt(a)} + ${sqrtFmt(b)}",
                "${sqrtFmt(a + 1)} + ${sqrtFmt(b - 1)}",
                "${sqrtFmt(a + 2)} + ${sqrtFmt(b - 2)}",
                "${sqrtFmt(a + 3)} + ${sqrtFmt(b - 3)}"
            )

            baseEquations.forEach { eq ->
                equations.add(GeneratedEquation(eq, null, metaIndex, meta))
            }
        }

        val ningunoEquations = mutableListOf<GeneratedEquation>()
        val usedEquations = mutableSetOf<String>()

        val candidatePool = equations.filter {
            val parts = it.left.split(" + ")
            parseSqrtFmt(parts[0]) != parseSqrtFmt(parts[1])
        }.toMutableList()

        for (candidateEq in candidatePool) {
            if (ningunoEquations.size >= 8) break

            val parts = candidateEq.left.split(" + ")
            val termA = parseSqrtFmt(parts[0])
            val termB = parseSqrtFmt(parts[1])
            val mayor = maxOf(termA, termB)

            for (decrement in 1..3) {
                val newMayor = mayor - decrement
                val newResult = termA + termB - decrement

                if (!metas.contains(newResult) && !usedEquations.contains("$newMayor")) {
                    val newEq = if (termA == mayor) {
                        "$newMayor + $termB"
                    } else {
                        "$termA + $newMayor"
                    }
                    ningunoEquations.add(GeneratedEquation(newEq, null, -1, null))
                    usedEquations.add("$newMayor")
                    break
                }
            }
        }

        while (ningunoEquations.size < 8) {
            val a = pickInt(1, 29, forbidZero = true)
            val b = pickInt(1, 19, forbidZero = true)
            val result = a + b
            if (!metas.contains(result) && !usedEquations.contains("$a+$b")) {
                ningunoEquations.add(GeneratedEquation("${sqrtFmt(a)} + ${sqrtFmt(b)}", null, -1, null))
                usedEquations.add("$a+$b")
            }
        }

        equations.addAll(ningunoEquations)
        equations.shuffle()

        val sumPattern = Regex("""^\s*(-?\d+)\s*\+\s*(-?\d+)\s*$""")
        val normalized = equations.map { ge ->
            val m = sumPattern.find(ge.left)
            if (m != null) {
                val a = m.groupValues[1]
                val b = m.groupValues[2]
                GeneratedEquation("$a + $b", null, ge.expectedMetaIndex, ge.metaValue)
            } else {
                ge
            }
        }

        return normalized
    }

    private fun generateSubtype2Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()

        val metas = pickDistinctInts(5, 22..45, forbidZero = true)

        val divisors = List(5) { listOf(2, 3, 4).random() }

        for (metaIndex in 0..4) {
            val meta = metas[metaIndex]
            val divisor = divisors[metaIndex]
            val a = round(meta.toDouble() / divisor).toInt()
            val b = meta - a

            val baseEquations = listOf(
                "${sqrtFmt(a)} + ${sqrtFmt(b)}",
                "${sqrtFmt(a + 1)} + ${sqrtFmt(b - 1)}",
                "${sqrtFmt(a + 2)} + ${sqrtFmt(b - 2)}",
                "${sqrtFmt(a + 3)} + ${sqrtFmt(b - 3)}"
            )

            baseEquations.forEach { eq ->
                equations.add(GeneratedEquation(eq, null, metaIndex, meta))
            }
        }

        val ningunoEquations = mutableListOf<GeneratedEquation>()
        val usedEquations = mutableSetOf<String>()

        val candidatePool = equations.filter {
            val parts = it.left.split(" + ")
            parseSqrtFmt(parts[0]) != parseSqrtFmt(parts[1])
        }.toMutableList()

        candidatePool.shuffle()

        for (candidateEq in candidatePool) {
            if (ningunoEquations.size >= 8) break

            val parts = candidateEq.left.split(" + ")
            val termA = parseSqrtFmt(parts[0])
            val termB = parseSqrtFmt(parts[1])

            val mayor = maxOf(termA, termB)

            for (decrement in 1..3) {
                val newMayor = mayor - decrement
                val newResult = termA + termB - decrement

                if (!metas.contains(newResult) && !usedEquations.contains("$newMayor")) {
                    val newEq = if (termA == mayor) {
                        "$newMayor + $termB"
                    } else {
                        "$termA + $newMayor"
                    }
                    ningunoEquations.add(GeneratedEquation(newEq, null, -1, null))
                    usedEquations.add("$newMayor")
                    break
                }
            }
        }

        while (ningunoEquations.size < 8) {
            val a = pickInt(1, 44, forbidZero = true)
            val b = pickInt(1, 34, forbidZero = true)
            val result = a + b
            if (!metas.contains(result) && !usedEquations.contains("$a+$b")) {
                ningunoEquations.add(GeneratedEquation("${sqrtFmt(a)} + ${sqrtFmt(b)}", null, -1, null))
                usedEquations.add("$a+$b")
            }
        }

        equations.addAll(ningunoEquations)
        equations.shuffle()

        return equations
    }

    private fun generateSubtype3Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()

        val metas = pickDistinctInts(5, 25..45, forbidZero = true)

        val divisors = listOf(2, 3, 2, 3, 2)
        val restas = listOf(2, 3, 4, 2, 3)

        for (metaIndex in 0..4) {
            val meta = metas[metaIndex]
            val divisor = divisors[metaIndex]
            val resta = restas[metaIndex]

            val a = round(meta.toDouble() / divisor).toInt()
            val b = a - resta
            val c = meta - a - b

            val terms = listOf(a, b, c).sorted()
            val menor = terms[0]
            val medio = terms[1]
            val mayor = terms[2]

            val isMayorA = (a == mayor)
            val isMayorB = (b == mayor)
            val isMayorC = (c == mayor)
            val isMenorA = (a == menor)
            val isMenorB = (b == menor)
            val isMenorC = (c == menor)

            equations.add(GeneratedEquation("${sqrtFmt(a)} + ${sqrtFmt(b)} + ${sqrtFmt(c)}", null, metaIndex, meta))

            for (delta in 1..3) {
                val newA = when {
                    isMayorA -> mayor - delta
                    isMenorA -> menor + delta
                    else -> medio
                }
                val newB = when {
                    isMayorB -> mayor - delta
                    isMenorB -> menor + delta
                    else -> medio
                }
                val newC = when {
                    isMayorC -> mayor - delta
                    isMenorC -> menor + delta
                    else -> medio
                }

                equations.add(GeneratedEquation("${sqrtFmt(newA)} + ${sqrtFmt(newB)} + ${sqrtFmt(newC)}", null, metaIndex, meta))
            }
        }

        val ningunoEquations = mutableListOf<GeneratedEquation>()
        val candidatePool = equations.toMutableList()
        candidatePool.shuffle()

        for (candidateEq in candidatePool) {
            if (ningunoEquations.size >= 8) break

            val parts = candidateEq.left.split(" + ").map { parseSqrtFmt(it) }
            val mayor = parts.maxOrNull() ?: continue

            for (increment in 1..4) {
                val newMayor = mayor + increment
                val newResult = parts.sum() + increment

                if (!metas.contains(newResult)) {
                    val newParts = parts.map { if (it == mayor) newMayor else it }
                    val newEq = newParts.joinToString(" + ") { sqrtFmt(it) }
                    ningunoEquations.add(GeneratedEquation(newEq, null, -1, null))
                    break
                }
            }
        }

        while (ningunoEquations.size < 8) {
            val a = pickInt(1, 25, forbidZero = true)
            val b = pickInt(1, 25, forbidZero = true)
            val c = pickInt(1, 25, forbidZero = true)
            val result = a + b + c
            if (!metas.contains(result)) {
                ningunoEquations.add(GeneratedEquation("${sqrtFmt(a)} + ${sqrtFmt(b)} + ${sqrtFmt(c)}", null, -1, null))
            }
        }

        equations.addAll(ningunoEquations.take(8))
        equations.shuffle()

        return equations
    }

    private fun generateSubtype4Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()

        val metas = pickDistinctInts(5, -4..30, forbidZero = true)

        val termsA = List(14) { -Random.nextInt(1, 21) }
        for (i in 0..13) {
            val metaIndex = i % 5
            val meta = metas[metaIndex]
            val a = termsA[i]
            val b = meta - a
            equations.add(GeneratedEquation("${fmt(a)} + ${sqrtFmt(b)}", null, metaIndex, meta))
        }

        val termsB = List(14) { -Random.nextInt(1, 21) }
        for (i in 0..13) {
            val metaIndex = i % 5
            val meta = metas[metaIndex]
            val b = termsB[i]
            val a = meta - b
            equations.add(GeneratedEquation("${fmt(a)} + ${sqrtFmt(b)}", null, metaIndex, meta))
        }

        val ningunoEquations = mutableListOf<GeneratedEquation>()
        val usedEquations = mutableSetOf<String>()

        val candidatePool = equations.filter {
            val parts = it.left.replace("âˆ'", "-").split(" + ")
            parseSqrtFmt(parts[0]) != parseSqrtFmt(parts[1])
        }.toMutableList()

        candidatePool.shuffle()

        for (candidateEq in candidatePool) {
            if (ningunoEquations.size >= 8) break

            val parts = candidateEq.left.replace("âˆ'", "-").split(" + ")
            val termA = parseSqrtFmt(parts[0])
            val termB = parseSqrtFmt(parts[1])
            val mayor = if (abs(termA) > abs(termB)) termA else termB

            for (increment in 1..5) {
                val newMayor = mayor + increment
                val newResult = termA + termB + increment

                if (!metas.contains(newResult) && !usedEquations.contains("$newMayor")) {
                    val newEq = if (termA == mayor) {
                        "${fmt(newMayor)} + ${fmt(termB)}"
                    } else {
                        "${fmt(termA)} + ${fmt(newMayor)}"
                    }
                    ningunoEquations.add(GeneratedEquation(newEq, null, -1, null))
                    usedEquations.add("$newMayor")
                    break
                }
            }
        }

        val finalEquations = equations.take(20).toMutableList()
        finalEquations.addAll(ningunoEquations.take(8))
        finalEquations.shuffle()

        return finalEquations
    }

    private fun generateSubtype5Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()

        val metas = pickDistinctInts(5, -8..40, forbidZero = true)

        val termsA = List(14) { -Random.nextInt(1, 21) }
        for (i in 0..13) {
            val metaIndex = i % 5
            val meta = metas[metaIndex]
            val a = termsA[i]
            val b = meta - a
            equations.add(GeneratedEquation("${fmt(a)} + ${sqrtFmt(b)}", null, metaIndex, meta))
        }

        val termsB = List(14) { -Random.nextInt(1, 21) }
        for (i in 0..13) {
            val metaIndex = i % 5
            val meta = metas[metaIndex]
            val b = termsB[i]
            val a = meta - b
            equations.add(GeneratedEquation("${fmt(a)} + ${sqrtFmt(b)}", null, metaIndex, meta))
        }

        val ningunoEquations = mutableListOf<GeneratedEquation>()
        val usedEquations = mutableSetOf<String>()

        val candidatePool = equations.filter {
            val parts = it.left.replace("âˆ'", "-").split(" + ")
            parseSqrtFmt(parts[0]) != parseSqrtFmt(parts[1])
        }.toMutableList()

        candidatePool.shuffle()

        for (candidateEq in candidatePool) {
            if (ningunoEquations.size >= 8) break

            val parts = candidateEq.left.replace("âˆ'", "-").split(" + ")
            val termA = parseSqrtFmt(parts[0])
            val termB = parseSqrtFmt(parts[1])
            val mayor = if (abs(termA) > abs(termB)) termA else termB

            for (increment in 1..5) {
                val newMayor = mayor + increment
                val newResult = termA + termB + increment

                if (!metas.contains(newResult) && !usedEquations.contains("$newMayor")) {
                    val newEq = if (termA == mayor) {
                        "${fmt(newMayor)} + ${fmt(termB)}"
                    } else {
                        "${fmt(termA)} + ${fmt(newMayor)}"
                    }
                    ningunoEquations.add(GeneratedEquation(newEq, null, -1, null))
                    usedEquations.add("$newMayor")
                    break
                }
            }
        }

        val finalEquations = equations.take(20).toMutableList()
        finalEquations.addAll(ningunoEquations.take(8))
        finalEquations.shuffle()

        return finalEquations
    }

    private fun generateSubtype6Equations(): List<GeneratedEquation> {

        val sevenMetas = pickDistinctInts(7, -8..40, forbidZero = false)
        val fourteenNegatives = List(14) { -Random.nextInt(1, 21) }
        val divisionPattern = intArrayOf(2, 3)

        fun buildABC(meta: Int, negative: Int, useIndex: Int): Triple<Int, Int, Int> {
            val a = meta + negative
            val divisor = divisionPattern[useIndex % 2]
            val divided = a.toDouble() / divisor
            val rounded = roundHalfUp(divided)
            val b = rounded + negative
            val c = meta - b - a
            return Triple(a, b, c)
        }

        val eqRegex = Regex("""^(-?\d+|√\d+) \+ (-?\d+|√\d+) \+ (-?\d+|√\d+)$""")

        fun render(a: Int, b: Int, c: Int): String {
            val da = if (a >= 0) sqrtFmt(a) else fmt(a)
            val db = if (b >= 0) sqrtFmt(b) else fmt(b)
            val dc = if (c >= 0) sqrtFmt(c) else fmt(c)
            return "$da + $db + $dc"
        }

        fun tryAddUnique(
            used: MutableSet<String>,
            list: MutableList<GeneratedEquation>,
            a: Int, b: Int, c: Int,
            meta: Int
        ): Boolean {

            val candidates = arrayOf(
                render(a, b, c),
                render(a, c, b),
                render(b, a, c),
                render(b, c, a),
                render(c, a, b),
                render(c, b, a)
            )
            for (eq in candidates) {
                if (eqRegex.matches(eq) && used.add(eq)) {
                    list.add(GeneratedEquation(eq, null, /* metaIndex temp */ -1, meta))
                    return true
                }
            }
            return false
        }

        val equations = mutableListOf<GeneratedEquation>()
        val usedEquations = mutableSetOf<String>()

        for (i in 0 until 14) {
            val meta = sevenMetas[i % 7]
            val neg = fourteenNegatives[i]
            val (a, b, c) = buildABC(meta, neg, i)
            tryAddUnique(usedEquations, equations, a, b, c, meta)

        }


        val countByMeta = equations.groupingBy { it.metaValue!! }.eachCount().toMutableMap()
        for (meta in sevenMetas) {
            while ((countByMeta[meta] ?: 0) < 2) {

                val neg = -Random.nextInt(1, 21)
                val useIndex = Random.nextInt(0, 2)
                val (a, b, c) = buildABC(meta, neg, useIndex)
                if (tryAddUnique(usedEquations, equations, a, b, c, meta)) {
                    countByMeta[meta] = (countByMeta[meta] ?: 0) + 1
                }
            }
        }


        val byMetaOrdered = sevenMetas.flatMap { m -> equations.filter { it.metaValue == m }.take(2) }
        val exactly14 = byMetaOrdered.take(14).toMutableList()

        val selectedMetasForButtons = sevenMetas.shuffled().take(5)

        val finalEquations = exactly14.map { eq ->
            val m = eq.metaValue
            val metaIndex = if (m != null && selectedMetasForButtons.contains(m)) {
                selectedMetasForButtons.indexOf(m)
            } else {
                -1 // NINGUNO
            }
            GeneratedEquation(eq.left, eq.right, metaIndex, m)
        }

        return finalEquations.shuffled()
    }

    private fun generateSubtype7Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()
        val metasCandidates = validMultiplicationTargets.shuffled()
        val metas = mutableListOf<Int>()

        for (candidate in metasCandidates) {
            if (metas.size >= 5) break

            val factorizations = getAllFactorizations(candidate)
            if (factorizations.size >= 4) {
                metas.add(candidate)
            }
        }

        if (metas.size < 5) {
            while (metas.size < 5) {
                val fallback = validMultiplicationTargets.random()
                if (!metas.contains(fallback)) {
                    metas.add(fallback)
                }
            }
        }

        for (metaIndex in 0..4) {
            val meta = metas[metaIndex]
            val factorizations = getAllFactorizations(meta)
            val chosen = factorizations.shuffled().take(4)
            chosen.forEach { (a, b) ->
                equations.add(GeneratedEquation("${sqrtFmt(a)} × ${sqrtFmt(b)}", null, metaIndex, meta))
            }
        }

        val ningunoEquations = mutableListOf<GeneratedEquation>()
        val candidatePool = equations.toMutableList()
        candidatePool.shuffle()

        for (candidateEq in candidatePool) {
            if (ningunoEquations.size >= 8) break

            val parts = candidateEq.left.split(" × ")
            val termA = parseSqrtFmt(parts[0])
            val termB = parseSqrtFmt(parts[1])
            val menor = minOf(termA, termB)

            for (increment in 1..3) {
                val newMenor = menor + increment
                val newResult = if (termA == menor) newMenor * termB else termA * newMenor

                if (!metas.contains(newResult)) {
                    val newEq = if (termA == menor) {
                        "${sqrtFmt(newMenor)} × ${sqrtFmt(termB)}"
                    } else {
                        "${sqrtFmt(termA)} × ${sqrtFmt(newMenor)}"
                    }
                    ningunoEquations.add(GeneratedEquation(newEq, null, -1, null))
                    break
                }
            }
        }

        equations.addAll(ningunoEquations)
        equations.shuffle()
        return equations.take(28)
    }

    private fun getAllFactorizations(n: Int): List<Pair<Int, Int>> {
        val factorizations = mutableListOf<Pair<Int, Int>>()
        for (i in 1..n) {
            if (n % i == 0) {
                val j = n / i
                if (i <= j) {
                    factorizations.add(Pair(i, j))
                    if (i != j) {
                        factorizations.add(Pair(j, i))
                    }
                }
            }
        }
        return factorizations
    }

    private fun generateSubtype8Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()
        val metasCandidates = validMultiplicationTargets.shuffled()
        val metasBase = mutableListOf<Int>()

        for (candidate in metasCandidates) {
            if (metasBase.size >= 5) break
            val factorizations = getAllFactorizations(candidate)
            if (factorizations.size >= 4) {
                metasBase.add(candidate)
            }
        }

        if (metasBase.size < 5) {
            Log.w("FocoPlus", "Subtipo 8: Solo se encontraron ${metasBase.size} metas válidas")
        }

        val terminosAdicionales = List(5) { Random.nextInt(1, 23) }
        val metasFinales = metasBase.zip(terminosAdicionales).map { it.first + it.second }

        for (metaIndex in 0..4) {
            val metaBase = metasBase[metaIndex]
            val terminoAdicional = terminosAdicionales[metaIndex]
            val factorizations = getAllFactorizations(metaBase)

            val chosen = factorizations.shuffled().take(4)
            chosen.forEach { (a, b) ->
                equations.add(GeneratedEquation("${sqrtFmt(a)} × ${sqrtFmt(b)} + ${sqrtFmt(terminoAdicional)}", null, metaIndex, metasFinales[metaIndex]))
            }
        }

        val ningunoEquations = mutableListOf<GeneratedEquation>()
        val candidatePool = equations.toMutableList()
        candidatePool.shuffle()

        for (candidateEq in candidatePool) {
            if (ningunoEquations.size >= 8) break

            val parts = candidateEq.left.split(" + ")
            val multiplicationPart = parts[0]
            val terminoActual = parseSqrtFmt(parts[1])

            for (increment in 1..4) {
                val newTermino = terminoActual + increment
                val partsMulti = multiplicationPart.split(" × ")
                val newResult = parseSqrtFmt(partsMulti[0]) * parseSqrtFmt(partsMulti[1]) + newTermino

                if (!metasFinales.contains(newResult)) {
                    val newEq = "$multiplicationPart + ${sqrtFmt(newTermino)}"
                    ningunoEquations.add(GeneratedEquation(newEq, null, -1, null))
                    break
                }
            }
        }

        equations.addAll(ningunoEquations.take(8))
        equations.shuffle()

        return equations
    }

    private fun generateSubtype9Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()

        val valoresBase = pickDistinctInts(7, 11..26, forbidZero = true)
        val metasNumericas = valoresBase.take(5)
        val basesNinguno = valoresBase.takeLast(2)
        val gruposDivisores = List(7) {
            pickDistinctInts(4, 1..8, forbidZero = true)
        }

        for (metaIndex in 0..4) {
            val valor = metasNumericas[metaIndex]
            val divisores = gruposDivisores[metaIndex]

            divisores.forEach { divisor ->
                val dividendo = valor * divisor
                equations.add(GeneratedEquation("$dividendo ÷  ${sqrtFmt(divisor)}", null, metaIndex, valor))
            }
        }

        for (i in 0..1) {
            val valorNinguno = basesNinguno[i]
            val divisores = gruposDivisores[5 + i]

            divisores.forEach { divisor ->
                val dividendo = valorNinguno * divisor
                equations.add(GeneratedEquation("$dividendo ÷  ${sqrtFmt(divisor)}", null, -1, null))
            }
        }

        equations.shuffle()

        return equations
    }

    private fun generateSubtype10Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()

        val valoresBase = pickDistinctInts(7, 11..26, forbidZero = true)
        val metasBase = valoresBase.take(5)
        val basesNinguno = valoresBase.takeLast(2)
        var terminosRestaMetas: List<Int>
        var metasFinales: List<Int>
        var guard = 0
        while (true) {
            terminosRestaMetas = List(5) { Random.nextInt(1, 23) }
            metasFinales = metasBase.indices.map { metasBase[it] - terminosRestaMetas[it] }
            if (metasFinales.toSet().size == 5) break
            guard++
            if (guard > 50) {

                val set = mutableSetOf<Int>()
                metasFinales = metasBase.indices.map { idx ->
                    var cand = metasBase[idx] - terminosRestaMetas[idx]
                    var bump = 0
                    while (!set.add(cand)) {
                        bump++
                        cand = (metasBase[idx] - terminosRestaMetas[idx]) + bump
                    }
                    cand
                }
                break
            }
        }

        val terminosRestaNinguno = MutableList(2) { Random.nextInt(1, 23) }
        for (i in 0 until 2) {
            var resta = terminosRestaNinguno[i]
            var intento = 0
            while (metasFinales.contains(basesNinguno[i] - resta)) {
                resta = Random.nextInt(1, 13)
                intento++
                if (intento > 50) {

                    resta = if ((basesNinguno[i] - resta) == metasFinales.first()) (resta % 12) + 1 else resta
                    break
                }
            }
            terminosRestaNinguno[i] = resta
        }

        val gruposDivisores = (0 until 7).map { pickDistinctInts(2, 1..8, forbidZero = true) }

        for (metaIndex in 0 until 5) {
            val base = metasBase[metaIndex]
            val resta = terminosRestaMetas[metaIndex]
            val divisores = gruposDivisores[metaIndex] // exactamente 2
            val metaFinal = metasFinales[metaIndex]
            divisores.forEach { divisor ->
                val dividendo = base * divisor

                val leftExpr = "$dividendo ÷  ${sqrtFmt(divisor)} - ${sqrtFmt(resta)}"
                equations.add(GeneratedEquation(leftExpr, null, metaIndex, metaFinal))
            }
        }

        for (i in 0 until 2) {
            val baseNinguno = basesNinguno[i]
            val resta = terminosRestaNinguno[i]
            val divisores = gruposDivisores[5 + i]
            val resultadoNinguno = baseNinguno - resta

            if (metasFinales.contains(resultadoNinguno)) {

                continue
            }

            divisores.forEach { divisor ->
                val dividendo = baseNinguno * divisor
                val leftExpr = "$dividendo ÷ ${sqrtFmt(divisor)} - ${sqrtFmt(resta)}"
                equations.add(GeneratedEquation(leftExpr, null, -1, null))
            }
        }

        if (equations.size > 14) {
            equations.shuffle()
            return equations.take(14)
        }

        equations.shuffle()
        return equations
    }

    private fun generateSubtype11Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()
        val metas = mutableListOf<Int>()

        repeat(5) {
            var a = 0
            var b = 0
            var c = 0
            var meta = 0
            var foundValid = false

            val triedCValues = mutableSetOf<Int>()

            while (!foundValid && triedCValues.size < 15) {
                a = pickInt(1, 25, forbidZero = false)
                b = pickInt(1, 25, forbidZero = false)

                val candidateC = pickInt(1, 14, forbidZero = false, exclude = triedCValues)
                triedCValues.add(candidateC)

                val metaPositive = a + b + candidateC
                if (metaPositive in 11..25 && !metas.contains(metaPositive)) {
                    c = candidateC
                    meta = metaPositive
                    foundValid = true
                } else {
                    val metaNegative = a + b + (-candidateC)
                    if (metaNegative in 11..25 && !metas.contains(metaNegative)) {
                        c = -candidateC
                        meta = metaNegative
                        foundValid = true
                    }
                }
            }

            if (foundValid) {
                metas.add(meta)
                equations.add(GeneratedEquation("${fmt(a)} + ${fmt(b)} + ${fmt(c)}", null, equations.size, meta))
            } else {
                Log.w("FocoPlus", "Subtipo 11: No se pudo generar meta numérica")
            }
        }

        for (i in 0..4) {
            if (i >= equations.size) break

            val genesis = equations[i]
            val parts = genesis.left.replace("−", "-").replace("∞", "-").split(" + ").map { it.toInt() }
            val a = parts[0]
            val b = parts[1]
            val c = parts[2]

            val newA = a - 2
            val newB = b + 1
            val newC = c + 1

            equations.add(GeneratedEquation("${fmt(newA)} + ${fmt(newB)} + ${fmt(newC)}", null, i, metas[i]))
        }

        val candidatesIndices = (0 until equations.size).toMutableList()
        candidatesIndices.shuffle()

        var ningunoCount = 0
        for (idx in candidatesIndices) {
            if (ningunoCount >= 4) break

            val eq = equations[idx]
            val parts = eq.left.replace("−", "-").replace("∞", "-").split(" + ").map { it.toInt() }
            val termIndex = Random.nextInt(0, 3)

            for (increment in 1..5) {
                val newParts = parts.toMutableList()
                newParts[termIndex] = parts[termIndex] + increment
                val newResult = newParts.sum()

                if (!metas.contains(newResult)) {
                    equations.add(GeneratedEquation("${fmt(newParts[0])} + ${fmt(newParts[1])} + ${fmt(newParts[2])}", null, -1, null))
                    ningunoCount++

                    break
                }
            }
        }

        val romanEquations = equations.map { eq ->
            val parts = eq.left.replace("−", "-").replace("∞", "-").replace("âˆ'", "-").replace("Â", "").split(" + ")
            val romanParts = parts.map { part ->
                val value = part.toInt()
                romanFmt(value)
            }
            val romanText = romanParts.joinToString(" + ")
            GeneratedEquation(romanText, null, eq.expectedMetaIndex, eq.metaValue)
        }.toMutableList()

        romanEquations.shuffle()
        return romanEquations.take(14)
    }

    private fun generateSubtype12Equations(): List<GeneratedEquation> {
        val equationsSubtype1 = generateSubtype1Equations()
        val sumPattern = Regex("""^\s*(-?√?\d+)\s*\+\s*(-?√?\d+)\s*$""")
        fun toDisplay(v: Int): String {
            val absV = abs(v)
            return if (absV in 1..10) {
                if (v < 0) "-${letterOf(absV)}" else letterOf(absV)
            } else {
                v.toString()
            }
        }

        val letterEquations = equationsSubtype1.map { eq ->
            val m = sumPattern.find(eq.left)
            if (m != null) {
                val termA = parseSqrtFmt(m.groupValues[1])
                val termB = parseSqrtFmt(m.groupValues[2])
                val displayA = toDisplay(termA)
                val displayB = toDisplay(termB)
                val displayText = "$displayA + $displayB"

                GeneratedEquation(displayText, null, eq.expectedMetaIndex, eq.metaValue)
            } else {
                GeneratedEquation(eq.left, null, eq.expectedMetaIndex, eq.metaValue)
            }
        }
        return letterEquations
    }

    private fun generateSubtype13Equations(): List<GeneratedEquation> {

        val equationsSubtype4 = generateSubtype4Equations()
        val mixedEquations = equationsSubtype4.map { eq ->
            val parts = eq.left.replace("-", "-").split(" + ")
            val termA = parseSqrtFmt(parts[0])
            val termB = parseSqrtFmt(parts[1])
            val (menor, mayor) = if (abs(termA) > abs(termB)) {
                Pair(termB, termA)
            } else {
                Pair(termA, termB)
            }

            val convertedText = if (abs(menor) > 10) {
                val romanTermA = romanFmt(termA)
                val romanTermB = romanFmt(termB)
                "$romanTermA + $romanTermB"
            } else {
                val romanTerm = romanFmt(mayor)
                val letterTerm = letterFmt(menor)

                if (termA == mayor) {
                    "$romanTerm + $letterTerm"
                } else {
                    "$letterTerm + $romanTerm"
                }
            }
            GeneratedEquation(convertedText, null, eq.expectedMetaIndex, eq.metaValue)
        }
        return mixedEquations
    }

    private fun generateSubtype14Equations(): List<GeneratedEquation> {
        val equations = mutableListOf<GeneratedEquation>()
        val metasInt = pickDistinctInts(5, 18..30, forbidZero = true)
        val divisors = List(5) { listOf(2, 3, 4).random() }

        for (metaIndex in 0..4) {
            val metaInt = metasInt[metaIndex]
            val divisor = divisors[metaIndex]
            val aInt = round(metaInt.toDouble() / divisor).toInt()
            val bInt = metaInt - aInt

            val baseEquations = listOf(
                "${fmtDec(aInt / 10.0)} + ${fmtDec(bInt / 10.0)}",
                "${fmtDec((aInt + 1) / 10.0)} + ${fmtDec((bInt - 1) / 10.0)}",
                "${fmtDec((aInt + 2) / 10.0)} + ${fmtDec((bInt - 2) / 10.0)}",
                "${fmtDec((aInt + 3) / 10.0)} + ${fmtDec((bInt - 3) / 10.0)}"
            )

            baseEquations.forEach { eq ->
                equations.add(GeneratedEquation(eq, null, metaIndex, metaInt))
            }
        }

        val ningunoEquations = mutableListOf<GeneratedEquation>()
        val usedEquations = mutableSetOf<String>()

        while (ningunoEquations.size < 8) {
            val aInt = pickInt(1, 29, forbidZero = true)
            val bInt = pickInt(1, 29, forbidZero = true)
            val resultInt = aInt + bInt
            val eq = "${fmtDec(aInt / 10.0)} + ${fmtDec(bInt / 10.0)}"
            if (!metasInt.contains(resultInt) && !usedEquations.contains(eq)) {
                ningunoEquations.add(GeneratedEquation(eq, null, -1, null))
                usedEquations.add(eq)
            }
        }

        equations.addAll(ningunoEquations)
        equations.shuffle()
        return equations
    }

    private fun generateSubtype15Equations(): List<GeneratedEquation> {

        val sevenMetas = pickDistinctInts(7, 45..95, forbidZero = true)

        val divisors = intArrayOf(5, 3, 5, 3, 5, 3, 5)
        val multipliers = intArrayOf(2, 4, 2, 4, 2, 4, 2)

        val equations = mutableListOf<GeneratedEquation>()

        for (i in 0 until 7) {
            val meta = sevenMetas[i]

            val a1 = roundHalfUp(meta.toDouble() / divisors[i])
            val b1 = a1 * multipliers[i]
            val c1 = meta - a1 - b1

            val equationBase = "${formatMinutesToTime(a1)}  +\n${formatMinutesToTime(b1)}  +\n${formatMinutesToTime(c1)}"
            equations.add(GeneratedEquation(equationBase, null, -1, meta))

            val a2 = a1 + 5
            val isOdd = ((i + 1) % 2 != 0)
            val b2 = if (isOdd) b1 - 3 else b1 - 2
            val c2 = if (isOdd) c1 - 2 else c1 - 3

            val equationVariant = "${formatMinutesToTime(a2)}  +\n${formatMinutesToTime(b2)}  +\n${formatMinutesToTime(c2)}"
            equations.add(GeneratedEquation(equationVariant, null, -1, meta))
        }


        val selectedMetasForButtons = sevenMetas.shuffled().take(5)

        val finalEquations = equations.map { eq ->
            val m = eq.metaValue
            val metaIndex = if (m != null && selectedMetasForButtons.contains(m)) {
                selectedMetasForButtons.indexOf(m)
            } else {
                -1
            }
            GeneratedEquation(eq.left, eq.right, metaIndex, m)
        }

        return finalEquations.shuffled()
    }


    private fun renderDualExercise() {
        val exerciseIdx = indexExercise * 2

        if (exerciseIdx + 1 >= preGeneratedExercises.size) {
            setColoredExercise(tvLeft, "—")
            setColoredExercise(tvRight, "—")
            leftExpectedMetaIndex = -1
            rightExpectedMetaIndex = -1
            return
        }

        val leftEq = preGeneratedExercises[exerciseIdx].first
        val rightEq = preGeneratedExercises[exerciseIdx + 1].first

        tvLeft.visibility = View.VISIBLE
        tvRight.visibility = View.VISIBLE
        setColoredExercise(tvLeft, leftEq)
        setColoredExercise(tvRight, rightEq)
        if (subtype == 12 || subtype == 13) {
            tvLeft.typeface = Typeface.DEFAULT
            tvRight.typeface = Typeface.DEFAULT
        }

        leftExpectedMetaIndex = exerciseMetaIndices[exerciseIdx]
        rightExpectedMetaIndex = exerciseMetaIndices[exerciseIdx + 1]
    }

    private fun renderSingleExercise() {

        if (indexExercise >= preGeneratedExercises.size) {
            setColoredExercise(tvSingle, "—")
            currentExpectedMetaIndex = -1
            return
        }

        val equation = preGeneratedExercises[indexExercise].first

        setColoredExercise(tvSingle, equation)

        if (subtype == 15) {
            tvSingle.gravity = Gravity.END
        }

        currentExpectedMetaIndex = exerciseMetaIndices[indexExercise]
    }

    private fun startDualMovement() {
        boardContainer.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                boardContainer.viewTreeObserver.removeOnPreDrawListener(this)

                val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                val halfSlot = dualSlot.width.toFloat() / 2f

                dualAnimLeft?.cancel();  dualAnimLeft = null
                dualAnimRight?.cancel(); dualAnimRight = null

                val leftView  = tvLeft
                val rightView = tvRight

                tvLeft.animate().cancel()
                tvRight.animate().cancel()
                tvLeft.alpha = 1f
                tvRight.alpha = 1f
                tvLeft.visibility = View.VISIBLE
                tvRight.visibility = View.VISIBLE

                tvLeft.translationY  = -dualVerticalOffsetPx
                tvRight.translationY =  dualVerticalOffsetPx

                val leftEnd  =  (halfSlot - marginPx).coerceAtLeast(0f)
                val rightEnd = -(halfSlot - marginPx).coerceAtLeast(0f)

                val leftStart: Float
                val rightStart: Float

                if (indexExercise == 0) {
                    leftStart  = -leftView.width.toFloat()
                    rightStart =  rightView.width.toFloat()
                } else {
                    leftStart  = 0f
                    rightStart = 0f
                }

                leftView.translationX  = leftStart
                rightView.translationX = rightStart

                val updateListener = ValueAnimator.AnimatorUpdateListener {
                    if (!timersStartedForThisExercise && indexExercise < totalExercises) {
                        val shouldStart = if (indexExercise == 0) {
                            val leftVisibleEnough  = (leftView.translationX + leftView.width) > leftView.width * 0.40f
                            val rightVisibleEnough =  rightView.translationX < rightView.width * 0.60f
                            leftVisibleEnough && rightVisibleEnough
                        } else {
                            true
                        }
                        if (shouldStart) {
                            timersStartedForThisExercise = true
                            startExerciseResponseTimer()
                            if (indexExercise == 0) {
                                startLevelTimer()
                                startGlobalVisibleTimer()
                            }
                        }
                    }
                }

                dualAnimLeft = ObjectAnimator.ofFloat(leftView, "translationX", leftStart, leftEnd).apply {
                    duration = perExerciseMs / 2
                    interpolator = AccelerateDecelerateInterpolator()
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = 1
                    addUpdateListener(updateListener)
                }

                dualAnimRight = ObjectAnimator.ofFloat(rightView, "translationX", rightStart, rightEnd).apply {
                    duration = perExerciseMs / 2
                    interpolator = AccelerateDecelerateInterpolator()
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = 1
                    addUpdateListener(updateListener)
                }

                dualAnimLeft?.start()
                dualAnimRight?.start()

                return true
            }
        })
    }


    private fun startSingleMovement() {
        boardContainer.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                boardContainer.viewTreeObserver.removeOnPreDrawListener(this)

                val marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                val slotWidth = singleSlot.width.toFloat()
                val textWidth = tvSingle.width.toFloat()
                val maxShift = ((slotWidth - textWidth) / 2f - marginPx).coerceAtLeast(0f)

                tvSingle.translationX = -maxShift
                tvSingle.translationY = 0f
                tvSingle.alpha = 1f
                tvSingle.visibility = View.VISIBLE

                ObjectAnimator.ofFloat(tvSingle, "translationX", -maxShift, maxShift).apply {
                    duration = perExerciseMs / 2
                    interpolator = AccelerateDecelerateInterpolator()
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = 1
                    addUpdateListener {
                        if (!timersStartedForThisExercise && indexExercise < totalExercises) {
                            timersStartedForThisExercise = true
                            startExerciseResponseTimer()
                            if (indexExercise == 0) {
                                startLevelTimer()
                                startGlobalVisibleTimer()
                            }
                        }
                    }
                    start()
                }
                return true
            }
        })
    }

    private var currentExpectedMetaIndex: Int = -1

    private fun onMetaPressed(index: Int) {
        if (!running) return

        lastPressedIndex = index

        lastPressedView = listOf(btn1, btn2, btn3, btn4, btn5)[index]

        applySoftBounceEffect(lastPressedView!!)

        val isCorrect: Boolean = if (isTwoTerms) {
            (index == leftExpectedMetaIndex || index == rightExpectedMetaIndex)
        } else {
            (index == currentExpectedMetaIndex)
        }

        if (isTwoTerms) {
            val exerciseIdx = indexExercise * 2
            val isFirstResponse = (dualExerciseState == DualExerciseState.WAITING_FIRST)

            if (isFirstResponse && exerciseIdx < preGeneratedExercises.size) {
                val answeredLeft = (index == leftExpectedMetaIndex)
                firstResponseAnsweredLeft = answeredLeft
                val answeredExerciseIdx = if (answeredLeft) exerciseIdx else exerciseIdx + 1
                val correctForFirst = if (answeredLeft) leftExpectedMetaIndex else rightExpectedMetaIndex
                if (answeredExerciseIdx < preGeneratedExercises.size) {
                    exercisesShownList.add(preGeneratedExercises[answeredExerciseIdx].first)
                }
                correctAnswersList.add(getMetaButtonContent(correctForFirst))
                userResponsesList.add(getMetaButtonContent(index))
            } else if (!isFirstResponse && exerciseIdx + 1 < preGeneratedExercises.size) {
                val correctForSecond = if (firstResponseAnsweredLeft) rightExpectedMetaIndex else leftExpectedMetaIndex
                val remainingExerciseIdx = if (firstResponseAnsweredLeft) exerciseIdx + 1 else exerciseIdx
                if (remainingExerciseIdx < preGeneratedExercises.size) {
                    exercisesShownList.add(preGeneratedExercises[remainingExerciseIdx].first)
                }
                correctAnswersList.add(getMetaButtonContent(correctForSecond))
                userResponsesList.add(getMetaButtonContent(index))
            }
        } else {
            if (indexExercise < preGeneratedExercises.size) {
                exercisesShownList.add(preGeneratedExercises[indexExercise].first)
                correctAnswersList.add(getMetaButtonContent(currentExpectedMetaIndex))
                userResponsesList.add(getMetaButtonContent(index))
            }
        }

        applyViewFeedback(lastPressedView!!, isCorrect)

        if (isTwoTerms) {
            handleDualExerciseResponse()
        } else {
            running = false
            exerciseTimer?.cancel()
            if (isCorrect) correctCount++ else errorsCount++
            updateErrorsCounter()

            clearBoardImmediately()
            boardContainer.postDelayed({ proceedOrFinish() }, 400)
        }
    }

    private fun onNonePressed() {
        if (!running) return

        lastPressedIndex = -1

        lastPressedView = btnNone

        applySoftBounceEffect(lastPressedView!!)

        val isCorrect: Boolean = if (isTwoTerms) {
            (leftExpectedMetaIndex == -1 || rightExpectedMetaIndex == -1)
        } else {
            (currentExpectedMetaIndex == -1)
        }

        if (isTwoTerms) {
            val exerciseIdx = indexExercise * 2
            val isFirstResponse = (dualExerciseState == DualExerciseState.WAITING_FIRST)

            if (isFirstResponse && exerciseIdx < preGeneratedExercises.size) {
                val ningunoMatchesLeft = (leftExpectedMetaIndex == -1)
                firstResponseAnsweredLeft = ningunoMatchesLeft
                val answeredExerciseIdx = if (ningunoMatchesLeft) exerciseIdx else exerciseIdx + 1
                val correctForFirst = if (ningunoMatchesLeft) leftExpectedMetaIndex else rightExpectedMetaIndex
                if (answeredExerciseIdx < preGeneratedExercises.size) {
                    exercisesShownList.add(preGeneratedExercises[answeredExerciseIdx].first)
                }
                correctAnswersList.add(getMetaButtonContent(correctForFirst))
                userResponsesList.add(getMetaButtonContent(-1))
            } else if (!isFirstResponse && exerciseIdx + 1 < preGeneratedExercises.size) {
                val correctForSecond = if (firstResponseAnsweredLeft) rightExpectedMetaIndex else leftExpectedMetaIndex
                val remainingExerciseIdx = if (firstResponseAnsweredLeft) exerciseIdx + 1 else exerciseIdx
                if (remainingExerciseIdx < preGeneratedExercises.size) {
                    exercisesShownList.add(preGeneratedExercises[remainingExerciseIdx].first)
                }
                correctAnswersList.add(getMetaButtonContent(correctForSecond))
                userResponsesList.add(getMetaButtonContent(-1))
            }
        } else {
            if (indexExercise < preGeneratedExercises.size) {
                exercisesShownList.add(preGeneratedExercises[indexExercise].first)
                correctAnswersList.add(getMetaButtonContent(currentExpectedMetaIndex))
                userResponsesList.add(getMetaButtonContent(-1))
            }
        }

        applyViewFeedback(lastPressedView!!, isCorrect)

        if (isTwoTerms) {
            handleDualExerciseResponse()
        } else {
            running = false
            exerciseTimer?.cancel()
            if (isCorrect) correctCount++ else errorsCount++
            updateErrorsCounter()
            clearBoardImmediately()
            boardContainer.postDelayed({ proceedOrFinish() }, 400)
        }
    }

    private fun handleDualExerciseResponse() {

        if (dualExerciseState == DualExerciseState.WAITING_FIRST) {
            isDoubleNingunoExercise = (leftExpectedMetaIndex == -1 && rightExpectedMetaIndex == -1)
        }

        val isValidForLeft = (lastPressedIndex == leftExpectedMetaIndex && leftExpectedMetaIndex != -99)
        val isValidForRight = (lastPressedIndex == rightExpectedMetaIndex && rightExpectedMetaIndex != -99)
        val isNingunoPressed = (lastPressedIndex == -1)

        when (dualExerciseState) {
            DualExerciseState.WAITING_FIRST -> {
                val responseIsValid = if (isDoubleNingunoExercise && isNingunoPressed) {
                    true
                } else {
                    isValidForLeft || isValidForRight
                }

                firstResponseCorrect = responseIsValid
                dualExerciseState = DualExerciseState.WAITING_SECOND

                if (!responseIsValid) {
                    errorsCount++
                    updateErrorsCounter()

                    if (errorsCount >= maxErrorsAllowed) {
                        running = false
                        exerciseTimer?.cancel()
                        isDoubleNingunoExercise = false
                        clearBoardImmediately()
                        finishLevel()
                        return
                    }
                }

                if (!isDoubleNingunoExercise) {
                    if (isValidForLeft && isValidForRight && leftExpectedMetaIndex == rightExpectedMetaIndex && leftExpectedMetaIndex >= 0) {
                        leftExpectedMetaIndex = -99
                    } else {
                        if (isValidForLeft) {
                            leftExpectedMetaIndex = -99
                        }
                        if (isValidForRight) {
                            rightExpectedMetaIndex = -99
                        }
                    }
                }
            }

            DualExerciseState.WAITING_SECOND -> {
                val responseIsValid = if (isDoubleNingunoExercise && isNingunoPressed) {
                    true
                } else {
                    isValidForLeft || isValidForRight
                }

                dualExerciseState = DualExerciseState.COMPLETED

                if (!responseIsValid) {
                    errorsCount++
                    updateErrorsCounter()

                    if (errorsCount >= maxErrorsAllowed) {
                        running = false
                        exerciseTimer?.cancel()
                        isDoubleNingunoExercise = false
                        clearBoardImmediately()
                        finishLevel()
                        return
                    }
                }

                val exerciseCorrect = firstResponseCorrect || responseIsValid
                if (exerciseCorrect) {
                    correctCount++
                }

                updateErrorsCounter()
                running = false
                exerciseTimer?.cancel()
                isDoubleNingunoExercise = false
                clearBoardImmediately()
                proceedOrFinish()
            }

            DualExerciseState.COMPLETED -> {
                return
            }
        }
    }

    private fun applySoftBounceEffect(view: View) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f).apply { duration = 30 }
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f).apply { duration = 30 }
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f).apply { duration = 30 }
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f).apply { duration = 30 }

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleDownX).with(scaleDownY)
        animatorSet.play(scaleUpX).with(scaleUpY).after(scaleDownX)
        animatorSet.start()
    }

    private fun applyViewFeedback(view: View, success: Boolean) {
        val originalBackground = view.background
        val feedbackResource = if (success) R.drawable.sombra_correcta else R.drawable.sombra_incorrecta

        view.setBackgroundResource(feedbackResource)

        boardContainer.postDelayed({
            view.background = originalBackground
        }, 120)
    }

    private fun handleNoAnswer() {

        if (isTwoTerms) {
            val exerciseIdx = indexExercise * 2

            if (dualExerciseState == DualExerciseState.WAITING_FIRST) {

                if (exerciseIdx < preGeneratedExercises.size) {
                    exercisesShownList.add(preGeneratedExercises[exerciseIdx].first)
                    correctAnswersList.add(getMetaButtonContent(leftExpectedMetaIndex))
                    userResponsesList.add(getMetaButtonContent(-99))
                }
                if (exerciseIdx + 1 < preGeneratedExercises.size) {
                    exercisesShownList.add(preGeneratedExercises[exerciseIdx + 1].first)
                    correctAnswersList.add(getMetaButtonContent(rightExpectedMetaIndex))
                    userResponsesList.add(getMetaButtonContent(-99))
                }
                errorsCount += 2

            } else {

                val remainingExerciseIdx = if (firstResponseAnsweredLeft) exerciseIdx + 1 else exerciseIdx
                val remainingCorrect = if (firstResponseAnsweredLeft) rightExpectedMetaIndex else leftExpectedMetaIndex
                if (remainingExerciseIdx < preGeneratedExercises.size) {
                    exercisesShownList.add(preGeneratedExercises[remainingExerciseIdx].first)
                    correctAnswersList.add(getMetaButtonContent(remainingCorrect))
                    userResponsesList.add(getMetaButtonContent(-99))
                }
                errorsCount += 1
            }

        } else {
            if (indexExercise < preGeneratedExercises.size) {
                exercisesShownList.add(preGeneratedExercises[indexExercise].first)
                correctAnswersList.add(getMetaButtonContent(currentExpectedMetaIndex))
                userResponsesList.add(getMetaButtonContent(-99))
            }
            errorsCount++
        }

        updateErrorsCounter()
        applyFeedback()
        proceedOrFinish()
    }

    private fun clearBoardImmediately() {

        dualAnimLeft?.cancel();  dualAnimLeft = null
        dualAnimRight?.cancel(); dualAnimRight = null

        if (isTwoTerms) {
            cancelAnimAndReset(tvLeft)
            cancelAnimAndReset(tvRight)
            tvLeft.text = ""
            tvRight.text = ""
            tvLeft.visibility = View.INVISIBLE
            tvRight.visibility = View.INVISIBLE
        } else {

            cancelAnimAndReset(tvSingle)

            tvSingle.text = ""
            tvSingle.visibility = View.INVISIBLE
        }
    }

    private fun cancelAnimAndReset(v: View) {

        v.animate().cancel()

        v.translationX = 0f
        v.translationY = 0f
        v.scaleX = 1f
        v.scaleY = 1f
        v.alpha = 1f
    }


    fun updateErrorsCounter() {
        val maxErrors = if (isTwoTerms) 8 else 4
        val counterText = getString(R.string.errors_counter_format_dynamic, errorsCount, maxErrors)

        val ss = SpannableString(counterText)
        val numStr = errorsCount.toString()
        val start = counterText.indexOf(numStr)
        if (start >= 0) {
            val end = start + numStr.length
            ss.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(this, R.color.red)
                ),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        tvErrorsCounter.text = ss
    }

    private fun applyFeedback() {
        val color = R.color.red
        val overlay = View(this).apply {
            setBackgroundColor(ContextCompat.getColor(this@GameActivityFocoPlusAvanzado, color))
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
        val maxErrors = if (isTwoTerms) 8 else 4
        if (errorsCount >= maxErrors) {
            finishLevel()
            return
        }

        nextExercise()
    }

    private fun finishLevel() {
        levelTimer?.cancel()
        globalTimer?.cancel()
        exerciseTimer?.cancel()
        rawTimeSpent = (System.currentTimeMillis() - levelStartTime) / 1000.0

        val isSuccessful = errorsCount < maxErrorsAllowed && correctCount > 0

        val maxErrors = if (isTwoTerms) 8 else 4
        val shouldPassReviewData = errorsCount >= maxErrors

        val intent = Intent(this, LevelResultActivityFocoPlus::class.java)
        intent.putExtra("LEVEL", currentLevel)
        intent.putExtra("CORRECT", correctCount)
        intent.putExtra("TOTAL", totalExercises)
        intent.putExtra("ERRORS", errorsCount)
        intent.putExtra("MAX_ERRORS", maxErrorsAllowed)
        intent.putExtra("TOTAL_TIME", rawTimeSpent)
        intent.putExtra("DIFFICULTY", currentDifficulty)
        intent.putExtra("IS_SUCCESSFUL", isSuccessful)

        intent.putExtra("USED_HINT", pistaActivada)

        if (shouldPassReviewData) {
            intent.putExtra("EXERCISES_SHOWN", exercisesShownList.toTypedArray())
            intent.putExtra("USER_RESPONSES", userResponsesList.toTypedArray())
            intent.putExtra("CORRECT_ANSWERS", correctAnswersList.toTypedArray())
            intent.putExtra("SUBTYPE", subtype)
        }

        AdManager.showInterstitialOnLevelEnd(this, currentLevel) {
            startActivity(intent)
            finish()
        }
    }

    private fun setMetaButtonsForNumbers(values: List<Int>) {

        if (values.isEmpty() || values.size < 5) {
            Log.w("FocoPlus", "setMetaButtonsForNumbers: lista de metas insuficiente (${values.size}). Reintentando armado...")

            return
        }

        val b = listOf(btn1, btn2, btn3, btn4, btn5)
        for (i in 0 until 5) {
            b[i].text = values[i].toString()
            b[i].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun getMetaOptionViews(): List<View> {
        return listOf(btn1, btn2, btn3, btn4, btn5)
    }

    private fun showQuestionOnButton(btn: Button) {
        btn.text = "?"
        btn.typeface = Typeface.DEFAULT_BOLD
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
        btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        btn.gravity = Gravity.CENTER
    }

    private fun applyHidingIfNeeded(activeHideSubtypes: List<Int>) {

        val hideFeatureActive = activeHideSubtypes.contains(subtype)
        if (!hideFeatureActive) {

            return
        }

        if (indexExercise == 11) {
            if (hiddenMetaIndexForFg !in 0..4) hiddenMetaIndexForFg = Random.nextInt(0, 5)

            val buttons = listOf(btn1, btn2, btn3, btn4, btn5)
            val idx = hiddenMetaIndexForFg.coerceIn(0, 4)
            showQuestionOnButton(buttons[idx])
            val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
            buttons[idx].startAnimation(pulse)
            return
        }

        if (indexExercise > 11) {

            return
        }

        if (hiddenMetaIndexForFg in 0..4) {
            val buttons = listOf(btn1, btn2, btn3, btn4, btn5)
            val idx = hiddenMetaIndexForFg.coerceIn(0, 4)
            val metaVal = extractedMetas.getOrNull(idx)
            if (metaVal != null) {
                buttons[idx].text = when (subtype) {
                    15 -> formatMinutesToTime(metaVal)
                    14 -> fmtDec(metaVal / 10.0)
                    else -> metaVal.toString()
                }
                buttons[idx].typeface = Typeface.DEFAULT
                buttons[idx].setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                buttons[idx].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                buttons[idx].gravity = Gravity.CENTER
            }
        }

        hiddenMetaIndexForFg = -1
        return
    }

    private fun verificarYMostrarPista() {
        val balance = CoinManager.getBalance(this)
        if (balance >= hintCostCoins) {
            mostrarDialogoPista()
        } else {
            showVamosThenStart()
        }
    }

    private fun mostrarDialogoPista() {
        hintOptionsLayout.visibility = View.VISIBLE

        val balance = CoinManager.getBalance(this)
        tvHintDesc.text = getString(
            if (isTwoTerms) R.string.hint_desc_foco_dual
            else R.string.hint_desc_foco_simple
        )
        tvHintBalance.text = balance.toString()

        hintTimerBar.max = 100
        hintTimerBar.progress = 100

        btnUseHint.setOnClickListener {
            cerrarDialogoPista(usarPista = true)
        }
        btnSkipHint.setOnClickListener {
            cerrarDialogoPista(usarPista = false)
        }

        hintCountDownTimer?.cancel()
        hintCountDownTimer = object : CountDownTimer(7000L, 50) {
            override fun onTick(msLeft: Long) {
                hintTimerBar.progress = ((msLeft / 7000.0) * 100).toInt()
            }
            override fun onFinish() {
                hintTimerBar.progress = 0
                cerrarDialogoPista(usarPista = false)
            }
        }.start()
    }

    private fun cerrarDialogoPista(usarPista: Boolean) {
        hintCountDownTimer?.cancel()
        if (usarPista) {
            CoinManager.spendCoins(this, hintCostCoins)
            pistaActivada = true
            val nuevoSaldo = CoinManager.getBalance(this)
            tvHintBalance.text = nuevoSaldo.toString()
            Handler(Looper.getMainLooper()).postDelayed({
                hintOptionsLayout.visibility = View.GONE
                showVamosThenStart()
            }, 700)
        } else {
            hintOptionsLayout.visibility = View.GONE
            showVamosThenStart()
        }
    }

    private fun aplicarPistaABotones() {
        val allViews: List<Pair<Int, View>> = listOfNotNull(
            Pair(0, btn1), Pair(1, btn2), Pair(2, btn3), Pair(3, btn4), Pair(4, btn5),
            btnNone?.let { Pair(-1, it) }
        )

        val allIndices = allViews.map { it.first }

        val visibleIndices: Set<Int> = if (isTwoTerms) {
            val leftCorrect = leftExpectedMetaIndex
            val rightCorrect = rightExpectedMetaIndex
            if (leftCorrect == rightCorrect) {
                val distractor = allIndices.filter { it != leftCorrect }.shuffled().take(1)
                (setOf(leftCorrect) + distractor)
            } else {
                val correctos = setOf(leftCorrect, rightCorrect)
                val distractores = allIndices.filter { !correctos.contains(it) }.shuffled().take(2)
                correctos + distractores
            }
        } else {
            val correctIdx = currentExpectedMetaIndex
            val distractor = allIndices.filter { it != correctIdx }.shuffled().take(1)
            (setOf(correctIdx) + distractor)
        }

        allViews.forEach { (metaIndex, view) ->
            if (visibleIndices.contains(metaIndex)) {
                view.alpha = 1f
                view.isClickable = true
                view.isFocusable = true
            } else {
                view.alpha = 0.2f
                view.isClickable = false
                view.isFocusable = false
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

    private fun roundHalfUp(value: Double): Int {
        return if (value >= 0) {
            (value + 0.5).toInt()
        } else {
            (value - 0.5).toInt()
        }
    }

    private fun fmt(n: Int): String = if (n < 0) "-${abs(n)}" else "$n"

    private fun sqrtFmt(v: Int): String {
        return if (v in 1..12) "√${v * v}" else fmt(v)
    }

    private fun fmtDec(v: Double): String {
        return String.format(Locale.US, "%.1f", v)
    }

    private fun parseSqrtFmt(s: String): Int {
        return if (s.startsWith("√")) {
            sqrt(s.substring(1).toDouble()).toInt()
        } else {
            s.trim().toInt()
        }
    }

    private fun letterOf(v: Int): String = "ABCDEFGHIJ"[max(1, min(10, v)) - 1].toString()

    private fun romanOf(v: Int): String {
        return when (v) {
            1 -> "I"; 2 -> "II"; 3 -> "III"; 4 -> "IV"; 5 -> "V"
            6 -> "VI"; 7 -> "VII"; 8 -> "VIII"; 9 -> "IX"; 10 -> "X"
            11 -> "XI"; 12 -> "XII"; 13 -> "XIII"; 14 -> "XIV"; 15 -> "XV"
            16 -> "XVI"; 17 -> "XVII"; 18 -> "XVIII"; 19 -> "XIX"; 20 -> "XX"
            21 -> "XXI"; 22 -> "XXII"; 23 -> "XXIII"; 24 -> "XXIV"; 25 -> "XXV"
            else -> v.toString()
        }
    }

    private fun romanFmt(v: Int): String = if (v < 0) "-${romanOf(abs(v))}" else romanOf(v)

    private fun letterFmt(v: Int): String {
        return if (v < 0) "-${letterOf(abs(v))}" else letterOf(v)
    }

    private fun isTwoTermsSubtype(subtype: Int): Boolean {
        return when (subtype) {
            1, 2, 4, 5, 7, 9, 12, 13 -> true
            else -> false
        }
    }

    private fun showExitConfirmation(onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(this)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(R.string.btn_yes) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.btn_no, null)
        builder.create().show()
    }

    private fun colorizeNegatives(text: String): CharSequence {
        val white = Color.WHITE
        val red = ContextCompat.getColor(this, R.color.red)

        val sb = SpannableStringBuilder(text)
        sb.setSpan(
            ForegroundColorSpan(white),
            0, sb.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
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
                    val isColon = ch == ':'
                    return isRoman || isLetter || isDigit || isDot || isColon
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
                    ForegroundColorSpan(red),
                    start, j,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                i = j
                continue
            }
            i++
        }

        return sb
    }

    private fun setColoredExercise(textView: TextView, rawText: String) {
        textView.setTextColor(Color.WHITE)
        textView.text = colorizeNegatives(rawText)
    }


}