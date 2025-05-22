package com.example.sumamente.ui

import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.sumamente.R
import java.util.Locale
import kotlin.math.max

class LevelResultActivityDeciPlusPrincipiante : AppCompatActivity() {

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

    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferences: android.content.SharedPreferences

    private var currentLevel = 1
    private var isSuccessful = false
    private var attempts = 0
    private var timeSpentInSeconds = 0.0
    private var pointsEarned = 0
    private var mediaPlayer: MediaPlayer? = null


    private var numberList: DoubleArray? = null
    private var correctAnswer: Double = 0.0
    private var userResponses: DoubleArray? = null
    private var excludedIndex: Int? = null
    private lateinit var reviewExerciseTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsDeciPlus", MODE_PRIVATE)
        setContentView(R.layout.activity_level_result_deci_plus_principiante)

        ScoreManager.initDeciPlusPrincipiante(this)


        currentLevel = intent.getIntExtra("LEVEL", 1)
        isSuccessful = intent.getBooleanExtra("IS_SUCCESSFUL", false)
        attempts = intent.getIntExtra("ATTEMPTS", 0)
        timeSpentInSeconds = intent.getDoubleExtra("TIME_SPENT", 0.0)

        numberList = intent.getDoubleArrayExtra("NUMBER_LIST")
        correctAnswer = intent.getDoubleExtra("CORRECT_ANSWER", 0.0)
        userResponses = intent.getDoubleArrayExtra("USER_RESPONSES")
        val exclIndex = intent.getIntExtra("EXCLUDED_INDEX", -1)
        excludedIndex = if (exclIndex >= 0) exclIndex else null


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
        reviewExerciseTextView = findViewById(R.id.reviewexercisetextview)

        setupUI()
    }

    private fun setupUI() {
        applyTouchAnimation(unlockLevelTextView)
        applyTouchAnimation(rankingChangedTextView)
        applyTouchAnimation(repeatLevelTextView)
        applyTouchAnimation(closeButton)

        mainMessageTextView.visibility = View.INVISIBLE
        animationView.visibility = View.INVISIBLE
        successInfoLayout.visibility = View.INVISIBLE
        rankingChangedTextView.visibility = View.INVISIBLE
        unlockLevelTextView.visibility = View.INVISIBLE
        repeatLevelTextView.visibility = View.INVISIBLE

        if (isSuccessful) {
            handleSuccessScenario()
        } else {
            handleFailureScenario()
        }
    }

    private fun handleSuccessScenario() {
        pointsEarned = calculatePoints()

        ScoreManager.totalGamesGlobal += 1
        ScoreManager.correctGamesGlobal += 1
        ScoreManager.totalGamesDeciPlus += 1
        ScoreManager.totalTimeDeciPlus += timeSpentInSeconds
        ScoreManager.saveStatsGlobalAndDeciPlus()

        ScoreManager.levelScoresDeciPlusPrincipiante[currentLevel]?.let { previousScore ->
            ScoreManager.currentScoreDeciPlusPrincipiante -= previousScore
        }

        ScoreManager.levelScoresDeciPlusPrincipiante[currentLevel] = pointsEarned
        ScoreManager.currentScoreDeciPlusPrincipiante = max(ScoreManager.currentScoreDeciPlusPrincipiante + pointsEarned, 0)

        if (!ScoreManager.hasCompletedLevelDeciPlusPrincipiante(currentLevel)) {
            ScoreManager.addCompletedLevelDeciPlusPrincipiante(currentLevel)
        }

        if (currentLevel >= ScoreManager.unlockedLevelsDeciPlusPrincipiante) {
            ScoreManager.unlockedLevelsDeciPlusPrincipiante = currentLevel + 1
        }

        ScoreManager.saveScoreDeciPlusPrincipiante()

        showSuccessDialog()
    }

    private fun handleFailureScenario() {
        ScoreManager.totalGamesGlobal += 1
        ScoreManager.totalGamesDeciPlus += 1
        ScoreManager.totalTimeDeciPlus += timeSpentInSeconds
        ScoreManager.saveStatsGlobalAndDeciPlus()

        updateScoreToZero()
        showFailureDialog()
    }

    private fun calculatePoints(): Int {
        val basePoints = when (currentLevel) {
            in 1..7 -> 300
            in 8..14 -> 900
            in 15..21 -> 1900
            in 22..28 -> 2900
            in 29..35 -> 3900
            in 36..42 -> 4900
            in 43..49 -> 5900
            in 50..56 -> 6900
            in 57..63 -> 7900
            else -> 8900
        }

        val pointsAfterAttempts = when (attempts) {
            0 -> basePoints
            1 -> basePoints / 2
            else -> 0
        }
        if (pointsAfterAttempts == 0) return 0

        val precisionGlobal = ScoreManager.getPrecisionGlobal()
        val velocidadBonus = 50.0


        var tiempoPromedio = if (ScoreManager.totalGamesDeciPlus > 0) {
            (ScoreManager.totalTimeDeciPlus + timeSpentInSeconds) / (ScoreManager.totalGamesDeciPlus + 1)
        } else {
            timeSpentInSeconds
        }

        val useManualAnswer = intent.getBooleanExtra("USE_MANUAL_ANSWER", false)
        if (useManualAnswer) {
            tiempoPromedio *= 0.7
        }

        val puntosPorVelocidad = if (tiempoPromedio > 0) {
            (velocidadBonus * (1.0 / tiempoPromedio))
        } else {
            0.0
        }

        val puntajeFinal = (pointsAfterAttempts * precisionGlobal) + puntosPorVelocidad

        return puntajeFinal.toInt()
    }


    private fun updateScoreToZero() {
        ScoreManager.levelScoresDeciPlusPrincipiante[currentLevel]?.let { previousScore ->
            ScoreManager.currentScoreDeciPlusPrincipiante -= previousScore
        }
        ScoreManager.levelScoresDeciPlusPrincipiante[currentLevel] = 0
        ScoreManager.saveScoreDeciPlusPrincipiante()
    }

    private fun showSuccessDialog() {
        mainMessageTextView.visibility = View.VISIBLE
        successInfoLayout.visibility = View.VISIBLE
        animationView.visibility = View.VISIBLE

        val firstAttemptMessages = listOf(
            getString(R.string.genial),
            getString(R.string.perfecto),
            getString(R.string.increible),
            getString(R.string.asombroso),
            getString(R.string.excelente),
            getString(R.string.fantastico),
            getString(R.string.imparable),
            getString(R.string.brillante),
            getString(R.string.magistral),
            getString(R.string.soberbio),
            getString(R.string.fenomenal),
            getString(R.string.esplendido),
            getString(R.string.muy_bien)
        )

        val secondAttemptMessages = listOf(
            getString(R.string.buen_trabajo),
            getString(R.string.bien_hecho),
            getString(R.string.vamos_bien),
            getString(R.string.gran_esfuerzo),
            getString(R.string.lo_lograste),
            getString(R.string.buen_avance),
            getString(R.string.sigue_asi),
            getString(R.string.asi_es),
            getString(R.string.casi_perfecto),
            getString(R.string.mejorando),
            getString(R.string.persistencia),
            getString(R.string.buen_intento),
            getString(R.string.vas_bien)
        )

        mainMessageTextView.text = if (attempts == 0) {
            firstAttemptMessages.random()
        } else {
            secondAttemptMessages.random()
        }

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

            fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    animationView.setAnimation("confetti_animation.json")
                    val fadeIn = AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.dialog_fade_in)
                    animationView.startAnimation(fadeIn)
                    animationView.playAnimation()

                    if (soundEnabled) {
                        playSound(R.raw.trompeta)
                    }

                    pointsTextView.visibility = View.VISIBLE

                    val puntosObtenidos = getString(R.string.puntos_obtenidos, pointsEarned)
                    val spannable = SpannableString(puntosObtenidos)
                    val puntosStr = pointsEarned.toString()
                    val startIdx = puntosObtenidos.indexOf(puntosStr)
                    val endIdx = startIdx + puntosStr.length
                    spannable.setSpan(StyleSpan(Typeface.BOLD), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    pointsTextView.text = spannable

                    val pointsAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.points_appear_from_back)
                    pointsTextView.startAnimation(pointsAnimation)

                    pointsAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                        override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                        override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                            checkImageView.visibility = View.VISIBLE
                            val checkAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.check_appear)
                            checkImageView.startAnimation(checkAnimation)

                            checkAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                                    val puntosDeciPlusPrincipiante = ScoreManager.currentScoreDeciPlusPrincipiante
                                    val puntajeActualText = getString(R.string.puntaje_actual, puntosDeciPlusPrincipiante)
                                    val spannablePuntajeActual = SpannableString(puntajeActualText)
                                    val puntosStrActual = puntosDeciPlusPrincipiante.toString()
                                    val startIdxActual = puntajeActualText.indexOf(puntosStrActual)
                                    val endIdxActual = startIdxActual + puntosStrActual.length
                                    spannablePuntajeActual.setSpan(StyleSpan(Typeface.BOLD), startIdxActual, endIdxActual, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    currentScoreTextView.text = spannablePuntajeActual

                                    currentScoreTextView.visibility = View.VISIBLE
                                    starImageView.visibility = View.VISIBLE

                                    val puntajeActualAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.points_appear_from_back)
                                    currentScoreTextView.startAnimation(puntajeActualAnimation)
                                    starImageView.startAnimation(puntajeActualAnimation)

                                    puntajeActualAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                                        override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                                        override fun onAnimationEnd(animation: android.view.animation.Animation?) {

                                            val formattedTime = String.format(Locale.getDefault(), "%.2f", timeSpentInSeconds)
                                            val tiempoEmpleadoText = getString(R.string.tiempo_empleado, formattedTime)
                                            val spannableTime = SpannableString(tiempoEmpleadoText)
                                            val startIdxTime = tiempoEmpleadoText.indexOf(formattedTime)
                                            val endIdxTime = startIdxTime + formattedTime.length
                                            spannableTime.setSpan(StyleSpan(Typeface.BOLD), startIdxTime, endIdxTime, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                            timeSpentTextView.text = spannableTime

                                            timeSpentTextView.visibility = View.VISIBLE
                                            val timeAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.points_appear_from_back)
                                            timeSpentTextView.startAnimation(timeAnimation)

                                            timeAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                                                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                                                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                                                    checkBlueImageView.visibility = View.VISIBLE
                                                    val checkBlueAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.check_appear)
                                                    checkBlueImageView.startAnimation(checkBlueAnimation)

                                                    unlockLevelTextView.text = getString(R.string.jugar_un_nuevo_nivel)

                                                    mainHandler.postDelayed({
                                                        rankingChangedTextView.visibility = View.VISIBLE
                                                        rankingChangedTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.dialog_fade_in))
                                                    }, 300)

                                                    mainHandler.postDelayed({
                                                        unlockLevelTextView.visibility = View.VISIBLE
                                                        unlockLevelTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.dialog_fade_in))
                                                    }, 600)

                                                    mainHandler.postDelayed({
                                                        repeatLevelTextView.visibility = View.VISIBLE
                                                        repeatLevelTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityDeciPlusPrincipiante, R.anim.dialog_fade_in))
                                                    }, 900)
                                                }

                                                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                                            })

                                        }

                                        override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                                    })
                                }

                                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                            })
                        }

                        override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                    })
                }

                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }, 2000)

        unlockLevelTextView.setOnClickListener {
            finish()
            navigateToLevels()
        }

        rankingChangedTextView.setOnClickListener {
            finish()
            navigateToHome()
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

        mainMessageTextView.visibility = View.VISIBLE
        rankingChangedTextView.visibility = View.VISIBLE
        unlockLevelTextView.visibility = View.VISIBLE
        repeatLevelTextView.visibility = View.VISIBLE

        val isLevelBlockedAfterThisFail = ScoreManager.getConsecutiveFailuresDeciPlusPrincipiante(currentLevel) >= 12

        if (isLevelBlockedAfterThisFail) {
            repeatLevelTextView.visibility = View.GONE
        } else {
            repeatLevelTextView.visibility = View.VISIBLE
        }

        mainMessageTextView.text = if (attempts >= 2) {
            getString(R.string.has_agotado_tus_intentos)
        } else {
            getString(R.string.se_agoto_el_tiempo)
        }

        val messageAnimation = AnimationUtils.loadAnimation(this, R.anim.message_appear_with_shake)
        mainMessageTextView.startAnimation(messageAnimation)

        unlockLevelTextView.text = getString(R.string.jugar_un_nuevo_nivel)

        unlockLevelTextView.setOnClickListener {
            finish()
            navigateToLevels()
        }

        rankingChangedTextView.setOnClickListener {
            finish()
            navigateToHome()
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
        repeatLevelTextView.startAnimation(fadeIn)

        if (attempts >= 2 && numberList != null && userResponses != null) {
            reviewExerciseTextView.visibility = View.VISIBLE
            applyTouchAnimation(reviewExerciseTextView)

            reviewExerciseTextView.setOnClickListener {
                val intent = Intent(this, ExerciseReviewActivityDeciPlus::class.java)
                intent.putExtra("NUMBER_LIST", numberList)
                intent.putExtra("CORRECT_ANSWER", correctAnswer)
                intent.putExtra("USER_RESPONSES", userResponses)
                intent.putExtra("EXCLUDED_INDEX", excludedIndex ?: -1)
                intent.putExtra("LEVEL", currentLevel)
                startActivity(intent)
            }

            reviewExerciseTextView.startAnimation(fadeIn)
        } else {
            reviewExerciseTextView.visibility = View.GONE
        }
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

    private fun playSound(soundResourceId: Int) {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled) {
            mediaPlayer = MediaPlayer.create(this, soundResourceId)
            mediaPlayer?.setOnCompletionListener {
                it.release()
            }
            mediaPlayer?.start()
        }
    }

    private fun navigateToInstructions() {
        val intent = Intent(this, InstructionsActivityDeciPlusPrincipiante::class.java)
        intent.putExtra("LEVEL", currentLevel)
        startActivity(intent)
        finish()
    }

    private fun navigateToLevels() {
        val intent = Intent(this, LevelsActivityDeciPlusPrincipiante::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainGameActivity::class.java)
        startActivity(intent)
        finish()
    }
}
