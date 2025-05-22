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

class LevelResultActivityGenioPlus : AppCompatActivity() {

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
    private lateinit var sharedPreferences: android.content.SharedPreferences

    private var currentLevel = 1
    private var isSuccessful = false
    private var attempts = 0
    private var timeSpentInSeconds = 0.0
    private var pointsEarned = 0
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsGenioPlus", MODE_PRIVATE)
        setContentView(R.layout.activity_level_result_genio_plus)

        ScoreManager.initGenioPlus(this)

        currentLevel = intent.getIntExtra("LEVEL", 1)
        isSuccessful = intent.getBooleanExtra("IS_SUCCESSFUL", false)
        attempts = intent.getIntExtra("ATTEMPTS", 0)
        timeSpentInSeconds = intent.getDoubleExtra("TIME_SPENT", 0.0)

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
        ScoreManager.totalGamesGenioPlus += 1
        ScoreManager.totalTimeGenioPlus += timeSpentInSeconds
        ScoreManager.saveStatsGlobalAndGenioPlus()

        ScoreManager.levelScoresGenioPlus[currentLevel]?.let { previousScore ->
            ScoreManager.currentScoreGenioPlus -= previousScore
        }

        ScoreManager.levelScoresGenioPlus[currentLevel] = pointsEarned
        ScoreManager.currentScoreGenioPlus = max(ScoreManager.currentScoreGenioPlus + pointsEarned, 0)

        if (!ScoreManager.hasCompletedLevelGenioPlus(currentLevel)) {
            ScoreManager.addCompletedLevelGenioPlus(currentLevel)
        }

        if (currentLevel >= ScoreManager.unlockedLevelsGenioPlus) {
            ScoreManager.unlockedLevelsGenioPlus = currentLevel + 1
        }

        ScoreManager.saveScoreGenioPlus()

        showSuccessDialog()
    }

    private fun handleFailureScenario() {
        ScoreManager.totalGamesGlobal += 1
        ScoreManager.totalGamesGenioPlus += 1
        ScoreManager.totalTimeGenioPlus += timeSpentInSeconds
        ScoreManager.saveStatsGlobalAndGenioPlus()

        updateScoreToZero()
        showFailureDialog()
    }

    private fun calculatePoints(): Int {
        val basePoints = when (currentLevel) {
            in 1..7 -> 700
            in 8..14 -> 1200
            in 15..21 -> 1700
            in 22..28 -> 2500
            in 29..35 -> 3200
            in 36..42 -> 4000
            in 43..49 -> 5000
            in 50..56 -> 6000
            in 57..63 -> 7000
            else -> 8000
        }

        val pointsAfterAttempts = when (attempts) {
            0 -> basePoints
            1 -> basePoints / 2
            else -> 0
        }
        if (pointsAfterAttempts == 0) return 0

        val precisionGlobal = ScoreManager.getPrecisionGlobal()
        val velocidadBonus = 200.0

        var tiempoPromedio = if (ScoreManager.totalGamesGenioPlus > 0) {
            (ScoreManager.totalTimeGenioPlus + timeSpentInSeconds) / (ScoreManager.totalGamesGenioPlus + 1)
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
        ScoreManager.levelScoresGenioPlus[currentLevel]?.let { previousScore ->
            ScoreManager.currentScoreGenioPlus -= previousScore
        }
        ScoreManager.levelScoresGenioPlus[currentLevel] = 0
        ScoreManager.saveScoreGenioPlus()
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
                    val fadeIn = AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.dialog_fade_in)
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

                    val pointsAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.points_appear_from_back)
                    pointsTextView.startAnimation(pointsAnimation)

                    pointsAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                        override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                        override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                            checkImageView.visibility = View.VISIBLE
                            val checkAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.check_appear)
                            checkImageView.startAnimation(checkAnimation)

                            checkAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                                    val puntosGenios = ScoreManager.currentScoreGenioPlus
                                    val puntajeActualText = getString(R.string.puntaje_actual, puntosGenios)
                                    val spannablePuntajeActual = SpannableString(puntajeActualText)
                                    val puntosStrActual = puntosGenios.toString()
                                    val startIdxActual = puntajeActualText.indexOf(puntosStrActual)
                                    val endIdxActual = startIdxActual + puntosStrActual.length
                                    spannablePuntajeActual.setSpan(StyleSpan(Typeface.BOLD), startIdxActual, endIdxActual, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    currentScoreTextView.text = spannablePuntajeActual

                                    currentScoreTextView.visibility = View.VISIBLE
                                    starImageView.visibility = View.VISIBLE

                                    val puntajeActualAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.points_appear_from_back)
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
                                            val timeAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.points_appear_from_back)
                                            timeSpentTextView.startAnimation(timeAnimation)

                                            timeAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                                                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                                                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                                                    checkBlueImageView.visibility = View.VISIBLE
                                                    val checkBlueAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.check_appear)
                                                    checkBlueImageView.startAnimation(checkBlueAnimation)

                                                    unlockLevelTextView.text = getString(R.string.jugar_un_nuevo_nivel)

                                                    mainHandler.postDelayed({
                                                        rankingChangedTextView.visibility = View.VISIBLE
                                                        rankingChangedTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.dialog_fade_in))
                                                    }, 300)

                                                    mainHandler.postDelayed({
                                                        unlockLevelTextView.visibility = View.VISIBLE
                                                        unlockLevelTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.dialog_fade_in))
                                                    }, 600)

                                                    mainHandler.postDelayed({
                                                        repeatLevelTextView.visibility = View.VISIBLE
                                                        repeatLevelTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityGenioPlus, R.anim.dialog_fade_in))
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

        val isLevelBlockedAfterThisFail = ScoreManager.getConsecutiveFailuresGenioPlus(currentLevel) >= 12

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
        if (!isLevelBlockedAfterThisFail) {
            repeatLevelTextView.startAnimation(fadeIn)
        }

        val numberList = intent.getStringArrayExtra("NUMBER_LIST")
        val correctAnswer = intent.getIntExtra("CORRECT_ANSWER", 0)
        val userResponses = intent.getIntArrayExtra("USER_RESPONSES")
        val excludedIndex = intent.getIntExtra("EXCLUDED_INDEX", -1)

        if (attempts >= 2 && numberList != null && userResponses != null) {
            reviewExerciseTextView.visibility = View.VISIBLE
            applyTouchAnimation(reviewExerciseTextView)

            reviewExerciseTextView.setOnClickListener {
                val reviewIntent = Intent(this, ExerciseReviewActivityGenioPlus::class.java)
                reviewIntent.putExtra("NUMBER_LIST", numberList)
                reviewIntent.putExtra("CORRECT_ANSWER", correctAnswer)
                reviewIntent.putExtra("USER_RESPONSES", userResponses)
                reviewIntent.putExtra("EXCLUDED_INDEX", excludedIndex)
                reviewIntent.putExtra("LEVEL", currentLevel)
                startActivity(reviewIntent)
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
        val intent = Intent(this, InstructionsActivityGenioPlus::class.java)
        intent.putExtra("LEVEL", currentLevel)
        startActivity(intent)
        finish()
    }

    private fun navigateToLevels() {
        val intent = Intent(this, LevelsActivityGenioPlus::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainGameActivity::class.java)
        startActivity(intent)
        finish()
    }
}
