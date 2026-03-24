package com.heptacreation.sumamente.ui

import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import com.airbnb.lottie.LottieAnimationView
import com.heptacreation.sumamente.R
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

class LevelResultActivityFocoPlus : BaseActivity() {

    private lateinit var mainMessageTextView: TextView
    private lateinit var pointsTextView: TextView
    private lateinit var checkImageView: ImageView
    private lateinit var timeSpentTextView: TextView
    private lateinit var checkBlueImageView: ImageView
    private lateinit var unlockLevelTextView: TextView
    private lateinit var rankingChangedTextView: TextView
    private lateinit var repeatLevelTextView: TextView
    private lateinit var closeButton: ImageView
    private lateinit var animationView: LottieAnimationView
    private lateinit var successInfoLayout: View
    private lateinit var currentScoreTextView: TextView
    private lateinit var starImageView: ImageView
    private lateinit var reviewExerciseTextView: TextView
    private val mainHandler = Handler(Looper.getMainLooper())

    private var currentLevel = 1
    private var totalExercises = 14
    private var correctCount = 0
    private var errorsCount = 0
    private var maxErrorsAllowed = 6

    private var isSuccessful = false
    private var timeSpentInSeconds = 0.0
    private var rawTimeSpent = 0.0
    private var pointsEarned = 0.0

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var mediaPlayer: MediaPlayer? = null
    private var currentDifficulty: String = DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE

    private var exercisesShown: Array<String>? = null
    private var userResponses: Array<String>? = null
    private var correctAnswers: Array<String>? = null
    private var subtype: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()


        currentDifficulty = intent.getStringExtra("DIFFICULTY") ?: DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE

        sharedPreferences = getSharedPreferences(getPrefsName(currentDifficulty), MODE_PRIVATE)
        setContentView(R.layout.activity_level_result)

        initializeScoreManager(currentDifficulty)

        currentLevel = intent.getIntExtra("LEVEL", 1)
        correctCount = intent.getIntExtra("CORRECT", 0)
        totalExercises = intent.getIntExtra("TOTAL", 14)
        maxErrorsAllowed = intent.getIntExtra("MAX_ERRORS", 6)
        errorsCount = intent.getIntExtra("ERRORS", 0)
        timeSpentInSeconds = intent.getDoubleExtra("TOTAL_TIME", 0.0)

        exercisesShown = intent.getStringArrayExtra("EXERCISES_SHOWN")
        userResponses = intent.getStringArrayExtra("USER_RESPONSES")
        correctAnswers = intent.getStringArrayExtra("CORRECT_ANSWERS")
        subtype = intent.getIntExtra("SUBTYPE", 1)

        isSuccessful = errorsCount < maxErrorsAllowed && correctCount > 0

        rawTimeSpent = timeSpentInSeconds

        findViews()
        setupUI()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToLevels()
                finish()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        stopCurrentSound()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCurrentSound()
        mainHandler.removeCallbacksAndMessages(null)
    }

    private fun findViews() {
        mainMessageTextView = findViewById(R.id.mainMessageTextView)
        pointsTextView = findViewById(R.id.pointsTextView)
        checkImageView = findViewById(R.id.checkImageView)
        timeSpentTextView = findViewById(R.id.timeSpentTextView)
        checkBlueImageView = findViewById(R.id.blueCheckImageView)
        unlockLevelTextView = findViewById(R.id.unlockLevelTextView)
        rankingChangedTextView = findViewById(R.id.rankingChangedTextView)
        repeatLevelTextView = findViewById(R.id.repeatLevelTextView)
        closeButton = findViewById(R.id.closeButton)
        animationView = findViewById(R.id.animationView)
        successInfoLayout = findViewById(R.id.successInfoLayout)
        currentScoreTextView = findViewById(R.id.currentScoreTextView)
        starImageView = findViewById(R.id.starImageView)
        reviewExerciseTextView = findViewById(R.id.review_exercise_textview)
    }

    private fun setupUI() {
        applyTouchAnimation(unlockLevelTextView)
        applyTouchAnimation(rankingChangedTextView)
        applyTouchAnimation(repeatLevelTextView)
        applyTouchAnimation(closeButton)
        applyTouchAnimation(reviewExerciseTextView)

        mainMessageTextView.visibility = View.INVISIBLE
        animationView.visibility = View.INVISIBLE
        successInfoLayout.visibility = View.INVISIBLE
        rankingChangedTextView.visibility = View.INVISIBLE
        unlockLevelTextView.visibility = View.INVISIBLE
        repeatLevelTextView.visibility = View.INVISIBLE
        reviewExerciseTextView.visibility = View.GONE

        if (isSuccessful) {
            handleSuccessScenario()
        } else {
            handleFailureScenario()
        }
    }

    private fun getPrefsName(difficulty: String): String = when (difficulty) {
        DifficultySelectionActivity.DIFFICULTY_AVANZADO -> "MyPrefsFocoPlus"
        DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> "MyPrefsFocoPlusPrincipiante"
        DifficultySelectionActivity.DIFFICULTY_PRO -> "MyPrefsFocoPlusPro"
        else -> "MyPrefsFocoPlusPrincipiante"
    }

    private fun initializeScoreManager(difficulty: String) {
        when (difficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.initFocoPlus(this)
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.initFocoPlusPrincipiante(this)
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.initFocoPlusPro(this)
        }
        CondecoracionTracker.init(this)
    }

    private fun getScoreManagerFunction(functionName: String): () -> Unit {
        return when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> when (functionName) {
                "addCompleted" -> { -> ScoreManager.addCompletedLevelFocoPlus(currentLevel) }
                "saveScore" -> { -> ScoreManager.saveScoreFocoPlus() }
                "getCurrentScore" -> { -> currentScoreTextView.text = formatScoreText(ScoreManager.currentScoreFocoPlus) }
                "hasCompleted" -> { -> ScoreManager.hasCompletedLevelFocoPlus(currentLevel) }
                "resetFailures" -> { -> ScoreManager.resetConsecutiveFailuresFocoPlus(currentLevel) }
                "updateStats" -> { -> updateSuccessStats(ScoreManager.totalGamesFocoPlusAvanzado) }
                else -> { -> }
            }
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> when (functionName) {
                "addCompleted" -> { -> ScoreManager.addCompletedLevelFocoPlusPrincipiante(currentLevel) }
                "saveScore" -> { -> ScoreManager.saveScoreFocoPlusPrincipiante() }
                "getCurrentScore" -> { -> currentScoreTextView.text = formatScoreText(ScoreManager.currentScoreFocoPlusPrincipiante) }
                "hasCompleted" -> { -> ScoreManager.hasCompletedLevelFocoPlusPrincipiante(currentLevel) }
                "resetFailures" -> { -> ScoreManager.resetConsecutiveFailuresFocoPlusPrincipiante(currentLevel) }
                "updateStats" -> { -> updateSuccessStats(ScoreManager.totalGamesFocoPlusPrincipiante) }
                else -> { -> }
            }
            DifficultySelectionActivity.DIFFICULTY_PRO -> when (functionName) {
                "addCompleted" -> { -> ScoreManager.addCompletedLevelFocoPlusPro(currentLevel) }
                "saveScore" -> { -> ScoreManager.saveScoreFocoPlusPro() }
                "getCurrentScore" -> { -> currentScoreTextView.text = formatScoreText(ScoreManager.currentScoreFocoPlusPro) }
                "hasCompleted" -> { -> ScoreManager.hasCompletedLevelFocoPlusPro(currentLevel) }
                "resetFailures" -> { -> ScoreManager.resetConsecutiveFailuresFocoPlusPro(currentLevel) }
                "updateStats" -> { -> updateSuccessStats(ScoreManager.totalGamesFocoPlusPro) }
                else -> { -> }
            }
            else -> { -> }
        }
    }

    private fun handleSuccessScenario() {
        pointsEarned = calculatePoints()
        val currentScoreMap = getCurrentScoreMap()

        currentScoreMap[currentLevel]?.let { previousScore ->
            updateCurrentScore(previousScore, subtract = true)
        }
        currentScoreMap[currentLevel] = pointsEarned.roundToInt()
        updateCurrentScore(pointsEarned.roundToInt(), subtract = false)

        getScoreManagerFunction("addCompleted").invoke()
        getScoreManagerFunction("resetFailures").invoke()

        getScoreManagerFunction("updateStats").invoke()
        updateIqComponent(success = true)

        getScoreManagerFunction("saveScore").invoke()
        ScoreManager.saveStatsGlobalAndFocoPlus()

        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.heptacreation.sumamente.ui.utils.SyncWorker>().build()
        androidx.work.WorkManager.getInstance(this).enqueue(syncRequest)

        showSuccessDialog()
    }

    private fun handleFailureScenario() {

        updateFailureStats()

        incrementFailureCount()

        updateScoreToZero()

        updateIqComponent(success = false)

        getScoreManagerFunction("saveScore").invoke()
        ScoreManager.saveStatsGlobalAndFocoPlus()

        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.heptacreation.sumamente.ui.utils.SyncWorker>().build()
        androidx.work.WorkManager.getInstance(this).enqueue(syncRequest)

        showFailureDialog()
    }

    private fun getCurrentScoreMap(): MutableMap<Int, Int> = when (currentDifficulty) {
        DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.levelScoresFocoPlus
        DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.levelScoresFocoPlusPrincipiante
        DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.levelScoresFocoPlusPro
        else -> ScoreManager.levelScoresFocoPlusPrincipiante
    }

    private fun updateCurrentScore(points: Int, subtract: Boolean) {
        val update: (Int) -> Int = if (subtract) { total -> max(0, total - points) } else { total -> total + points }
        when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.currentScoreFocoPlus = update(ScoreManager.currentScoreFocoPlus)
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.currentScoreFocoPlusPrincipiante = update(ScoreManager.currentScoreFocoPlusPrincipiante)
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.currentScoreFocoPlusPro = update(ScoreManager.currentScoreFocoPlusPro)
        }
    }

    private fun incrementFailureCount() {
        when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.incrementConsecutiveFailuresFocoPlus(currentLevel)
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.incrementConsecutiveFailuresFocoPlusPrincipiante(currentLevel)
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.incrementConsecutiveFailuresFocoPlusPro(currentLevel)
        }
    }

    private fun isLevelBlockedByFailures(): Boolean = when (currentDifficulty) {
        DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.isLevelBlockedByFailuresFocoPlus(currentLevel)
        DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.isLevelBlockedByFailuresFocoPlusPrincipiante(currentLevel)
        DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.isLevelBlockedByFailuresFocoPlusPro(currentLevel)
        else -> false
    }

    private fun updateSuccessStats(gameCounter: Int) {
        ScoreManager.totalGamesGlobal += 1
        ScoreManager.correctGamesGlobal += 1
        ScoreManager.totalGamesFocoPlus += 1
        ScoreManager.totalGamesFocoPlusExitos += 1

        val tiempoNormalizado = rawTimeSpent / 100.0
        ScoreManager.totalTimeFocoPlusExitos += tiempoNormalizado

        when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.totalGamesFocoPlusAvanzado = gameCounter + 1
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.totalGamesFocoPlusPrincipiante = gameCounter + 1
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.totalGamesFocoPlusPro = gameCounter + 1
        }

        ScoreManager.totalTimeFocoPlus += tiempoNormalizado
    }

    private fun updateFailureStats() {
        ScoreManager.totalGamesGlobal += 1
        ScoreManager.totalGamesFocoPlus += 1

        when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.totalGamesFocoPlusAvanzado += 1
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.totalGamesFocoPlusPrincipiante += 1
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.totalGamesFocoPlusPro += 1
        }

        val tiempoNormalizado = rawTimeSpent / 100.0
        ScoreManager.totalTimeFocoPlus += tiempoNormalizado
    }

    private fun updateIqComponent(success: Boolean) {
        val factor = obtenerFactorCorreccion()
        val velocidad = 1.0 / ScoreManager.getTiempoPromedioGlobal()
        val precision = ScoreManager.correctGamesGlobal.toDouble() / ScoreManager.totalGamesGlobal.toDouble()

        val multiplier = when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> 14.0
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> 17.0
            DifficultySelectionActivity.DIFFICULTY_PRO -> 20.0
            else -> 14.0
        }

        val aporte = if (success) {
            ((factor * velocidad * precision * multiplier) * 100).roundToInt() / 100.0
        } else {
            0.0
        }

        ScoreManager.updateIqComponent("FocoPlus", currentDifficulty, aporte)
    }

    private fun obtenerFactorCorreccion(): Double {

        return when (currentLevel) {
            in 1..84 -> 0.80
            in 85..168 -> 0.85
            in 169..252 -> 0.90
            in 253..336 -> 0.95
            in 337..420 -> 1.00
            else -> 1.00
        }
    }

    private fun calculatePoints(): Double {
        val band = ceil(currentLevel / 20.0).toInt()
        val basePoints = when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> band * 200.0
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> band * 500.0
            DifficultySelectionActivity.DIFFICULTY_PRO -> band * 800.0
            else -> band * 200.0
        }

        val accuracyPercentage = if (totalExercises == 0) 0.0 else (correctCount.toDouble() / totalExercises.toDouble())

        val points = basePoints * accuracyPercentage

        val precisionGlobal = ScoreManager.getPrecisionGlobal()
        val velocidadBonus = when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> 40.0
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> 140.0
            DifficultySelectionActivity.DIFFICULTY_PRO -> 240.0
            else -> 40.0
        }

        val tiempoPromedio = ScoreManager.getTiempoPromedioFocoPlus()
        val tiempoNormalizadoActual = timeSpentInSeconds / 100.0

        val puntosPorVelocidad = if (tiempoPromedio > 0) {
            (velocidadBonus * (1.0 / (tiempoPromedio + tiempoNormalizadoActual)))
        } else {
            0.0
        }

        val puntajeFinal = (points * precisionGlobal) + puntosPorVelocidad

        return puntajeFinal
    }

    private fun updateScoreToZero() {
        val currentScoreMap = getCurrentScoreMap()
        currentScoreMap[currentLevel]?.let { previousScore ->
            updateCurrentScore(previousScore, subtract = true)
        }
        currentScoreMap[currentLevel] = 0
    }

    private fun showSuccessDialog() {
        mainMessageTextView.visibility = View.VISIBLE
        successInfoLayout.visibility = View.VISIBLE
        animationView.visibility = View.VISIBLE
        starImageView.setImageResource(R.drawable.ic_star_numeros)

        val firstAttemptMessages = listOf(
            getString(R.string.genial), getString(R.string.perfecto), getString(R.string.increible),
            getString(R.string.asombroso), getString(R.string.excelente), getString(R.string.fantastico),
            getString(R.string.imparable), getString(R.string.brillante), getString(R.string.magistral),
            getString(R.string.soberbio), getString(R.string.fenomenal), getString(R.string.esplendido),
            getString(R.string.muy_bien)
        )


        mainMessageTextView.text = firstAttemptMessages.random()

        val messageAnimation = AnimationUtils.loadAnimation(this, R.anim.message_appear_with_shake)
        mainMessageTextView.startAnimation(messageAnimation)

        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled) {
            playSound(R.raw.campanillas)
        }

        animationView.setAnimation("stars_animation.json")
        animationView.playAnimation()

        mainHandler.postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.dialog_fade_out)
            animationView.startAnimation(fadeOut)

            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    animationView.setAnimation("confetti_animation.json")
                    val fadeIn = AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.dialog_fade_in)
                    animationView.startAnimation(fadeIn)
                    animationView.playAnimation()

                    if (soundEnabled) {
                        playSound(R.raw.trompeta)
                    }

                    pointsTextView.visibility = View.VISIBLE

                    val puntosObtenidos = getString(R.string.puntos_obtenidos, pointsEarned.roundToInt())
                    val spannable = SpannableString(puntosObtenidos)
                    val puntosStr = pointsEarned.roundToInt().toString()
                    val startIdx = puntosObtenidos.indexOf(puntosStr)
                    val endIdx = startIdx + puntosStr.length
                    if (startIdx >= 0 && endIdx <= puntosObtenidos.length) {
                        spannable.setSpan(StyleSpan(Typeface.BOLD), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    pointsTextView.text = spannable

                    val pointsAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.points_appear_from_back)
                    pointsTextView.startAnimation(pointsAnimation)

                    pointsAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            checkImageView.visibility = View.VISIBLE
                            val checkAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.check_appear)
                            checkImageView.startAnimation(checkAnimation)

                            checkAnimation.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {}
                                override fun onAnimationEnd(animation: Animation?) {
                                    getScoreManagerFunction("getCurrentScore").invoke()
                                    currentScoreTextView.visibility = View.VISIBLE
                                    starImageView.visibility = View.VISIBLE

                                    val puntajeActualAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.points_appear_from_back)
                                    currentScoreTextView.startAnimation(puntajeActualAnimation)
                                    starImageView.startAnimation(puntajeActualAnimation)

                                    puntajeActualAnimation.setAnimationListener(object : Animation.AnimationListener {
                                        override fun onAnimationStart(animation: Animation?) {}
                                        override fun onAnimationEnd(animation: Animation?) {

                                            val formattedTime = String.format(Locale.getDefault(), "%.2f", rawTimeSpent)
                                            val tiempoEmpleadoText = getString(R.string.tiempo_empleado, formattedTime)
                                            val spannableTime = SpannableString(tiempoEmpleadoText)
                                            val startIdxTime = tiempoEmpleadoText.indexOf(formattedTime)
                                            val endIdxTime = startIdxTime + formattedTime.length
                                            if (startIdxTime >= 0 && endIdxTime <= tiempoEmpleadoText.length) {
                                                spannableTime.setSpan(StyleSpan(Typeface.BOLD), startIdxTime, endIdxTime, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                            }
                                            timeSpentTextView.text = spannableTime

                                            timeSpentTextView.visibility = View.VISIBLE
                                            val timeAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.points_appear_from_back)
                                            timeSpentTextView.startAnimation(timeAnimation)

                                            timeAnimation.setAnimationListener(object : Animation.AnimationListener {
                                                override fun onAnimationStart(animation: Animation?) {}
                                                override fun onAnimationEnd(animation: Animation?) {
                                                    checkBlueImageView.visibility = View.VISIBLE
                                                    val checkBlueAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.check_appear)
                                                    checkBlueImageView.startAnimation(checkBlueAnimation)

                                                    unlockLevelTextView.text = getString(R.string.jugar_un_nuevo_nivel)

                                                    mainHandler.postDelayed({
                                                        rankingChangedTextView.visibility = View.VISIBLE
                                                        rankingChangedTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.dialog_fade_in))
                                                    }, 300)

                                                    mainHandler.postDelayed({
                                                        unlockLevelTextView.visibility = View.VISIBLE
                                                        unlockLevelTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.dialog_fade_in))
                                                    }, 600)

                                                    mainHandler.postDelayed({
                                                        repeatLevelTextView.visibility = View.VISIBLE
                                                        repeatLevelTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityFocoPlus, R.anim.dialog_fade_in))
                                                    }, 900)
                                                }

                                                override fun onAnimationRepeat(animation: Animation?) {}
                                            })
                                        }

                                        override fun onAnimationRepeat(animation: Animation?) {}
                                    })
                                }

                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                        }

                        override fun onAnimationRepeat(animation: Animation?) {}
                    })
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 2000)

        unlockLevelTextView.setOnClickListener {
            finish()
            navigateToLevels()
        }

        rankingChangedTextView.setOnClickListener {
            finish()
            navigateToClassification()
        }

        repeatLevelTextView.setOnClickListener {
            finish()
            navigateToInstructions()
        }

        closeButton.setOnClickListener {
            finish()
            navigateToHome()
        }
    }

    private fun showFailureDialog() {
        animationView.visibility = View.GONE
        successInfoLayout.visibility = View.GONE
        checkImageView.visibility = View.GONE
        checkBlueImageView.visibility = View.GONE
        pointsTextView.visibility = View.GONE
        timeSpentTextView.visibility = View.GONE
        starImageView.visibility = View.GONE
        currentScoreTextView.visibility = View.GONE

        mainMessageTextView.visibility = View.VISIBLE
        rankingChangedTextView.visibility = View.VISIBLE
        unlockLevelTextView.visibility = View.VISIBLE
        reviewExerciseTextView.visibility = View.VISIBLE

        val isLevelBlocked = isLevelBlockedByFailures()

        if (isLevelBlocked) {
            repeatLevelTextView.visibility = View.GONE
        } else {
            repeatLevelTextView.visibility = View.VISIBLE
        }

        mainMessageTextView.text = getString(R.string.has_agotado_tus_intentos)

        val messageAnimation = AnimationUtils.loadAnimation(this, R.anim.message_appear_with_shake)
        mainMessageTextView.startAnimation(messageAnimation)

        unlockLevelTextView.text = getString(R.string.jugar_un_nuevo_nivel)

        unlockLevelTextView.setOnClickListener {
            finish()
            navigateToLevels()
        }

        rankingChangedTextView.setOnClickListener {
            finish()
            navigateToClassification()
        }

        repeatLevelTextView.setOnClickListener {
            finish()
            navigateToInstructions()
        }

        closeButton.setOnClickListener {
            finish()
            navigateToHome()
        }

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.dialog_fade_in)
        rankingChangedTextView.startAnimation(fadeIn)
        unlockLevelTextView.startAnimation(fadeIn)
        reviewExerciseTextView.startAnimation(fadeIn)
        if (!isLevelBlocked) {
            repeatLevelTextView.startAnimation(fadeIn)
        }

        if (exercisesShown != null && userResponses != null && correctAnswers != null) {
            reviewExerciseTextView.visibility = View.VISIBLE
            applyTouchAnimation(reviewExerciseTextView)

            reviewExerciseTextView.setOnClickListener {
                val intent = Intent(this, ExerciseReviewActivityFocoPlus::class.java)
                intent.putExtra("EXERCISES_SHOWN", exercisesShown)
                intent.putExtra("USER_RESPONSES", userResponses)
                intent.putExtra("CORRECT_ANSWERS", correctAnswers)
                intent.putExtra("SUBTYPE", subtype)
                intent.putExtra("LEVEL", currentLevel)
                startActivity(intent)
            }

            reviewExerciseTextView.startAnimation(fadeIn)
        } else {
            reviewExerciseTextView.visibility = View.GONE
        }
    }

    private fun formatScoreText(score: Int): SpannableString {
        val scoreText = getString(R.string.puntaje_actual, score)
        val spannable = SpannableString(scoreText)
        val scoreStr = score.toString()
        val startIdx = scoreText.indexOf(scoreStr)
        val endIdx = startIdx + scoreStr.length
        if (startIdx >= 0 && endIdx <= scoreText.length) {
            spannable.setSpan(StyleSpan(Typeface.BOLD), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }

    private fun applyTouchAnimation(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.90f).scaleY(0.90f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    if (event.action == MotionEvent.ACTION_UP) {
                        v.performClick()
                    }
                }
            }
            true
        }
    }

    private fun stopCurrentSound() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.release()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error al detener audio", e)
        }
    }

    private fun playSound(soundResourceId: Int) {
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (!soundEnabled) return

        try {
            mediaPlayer?.let { mp ->
                try {
                    if (mp.isPlaying) { mp.stop() }
                    mp.release()
                } catch (e: Exception) { Log.e("MediaPlayer", "Error al liberar MediaPlayer anterior", e) }
                mediaPlayer = null
            }

            mediaPlayer = MediaPlayer.create(this, soundResourceId)
            mediaPlayer?.let { mp ->
                mp.setOnCompletionListener { player ->
                    try {
                        player.release()
                        if (mediaPlayer == player) { mediaPlayer = null }
                    } catch (e: Exception) { Log.e("MediaPlayer", "Error en OnCompletionListener", e) }
                }
                mp.setOnErrorListener { player, what, extra ->
                    Log.e("MediaPlayer", "Error reproduciendo sonido: what=$what, extra=$extra")
                    try {
                        player.release()
                        if (mediaPlayer == player) { mediaPlayer = null }
                    } catch (e: Exception) { Log.e("MediaPlayer", "Error liberando MediaPlayer en OnError", e) }
                    true
                }
                mp.start()
            } ?: run {
                Log.e("MediaPlayer", "No se pudo crear MediaPlayer para recurso: $soundResourceId")
            }
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Excepción general al reproducir sonido", e)
            mediaPlayer?.let { mp ->
                try { mp.release() } catch (releaseException: Exception) { Log.e("MediaPlayer", "Error al liberar en catch", releaseException) }
                mediaPlayer = null
            }
        }
    }

    private fun navigateToInstructions() {
        val targetActivity = when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> InstructionsLevelsActivityFocoPlus::class.java

            else -> InstructionsLevelsActivityFocoPlus::class.java
        }
        val intent = Intent(this, targetActivity)
        intent.putExtra("LEVEL", currentLevel)
        startActivity(intent)
        finish()
    }

    private fun navigateToLevels() {
        val targetActivity = when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> InstructionsLevelsActivityFocoPlus::class.java

            else -> InstructionsLevelsActivityFocoPlus::class.java
        }
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainGameActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    private fun navigateToClassification() {
        val intent = Intent(this, ClassificationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
