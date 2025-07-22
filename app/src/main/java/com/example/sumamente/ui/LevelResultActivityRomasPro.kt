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
import com.airbnb.lottie.LottieAnimationView
import com.example.sumamente.R
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

class LevelResultActivityRomasPro : BaseActivity()  {

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
    private var currentLevel = 1
    private var isSuccessful = false
    private var attempts = 0
    private var timeSpentInSeconds = 0.0
    private var rawTimeSpent = 0.0 // C1 //
    private var pointsEarned = 0

    private var numberList: IntArray? = null
    private var correctAnswer: Int = 0
    private var userResponses: IntArray? = null
    private var excludedIndex: Int? = null
    private lateinit var reviewExerciseTextView: TextView

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefsRomas", MODE_PRIVATE)
        setContentView(R.layout.activity_level_result_romas)

        ScoreManager.initRomasPro(this)
        CondecoracionTracker.init(this)

        currentLevel = intent.getIntExtra("LEVEL", 1)
        isSuccessful = intent.getBooleanExtra("IS_SUCCESSFUL", false)
        attempts = intent.getIntExtra("ATTEMPTS", 0)
        timeSpentInSeconds = intent.getDoubleExtra("TIME_SPENT", 0.0)

        // C2 //
        rawTimeSpent = intent.getDoubleExtra("TIME_SPENT", 0.0)
        val useManualAnswer = intent.getBooleanExtra("USE_MANUAL_ANSWER", false)
        timeSpentInSeconds = rawTimeSpent
        if (useManualAnswer) {
            timeSpentInSeconds *= 0.7
        }

        numberList = intent.getIntArrayExtra("NUMBER_LIST")
        correctAnswer = intent.getIntExtra("CORRECT_ANSWER", 0)
        userResponses = intent.getIntArrayExtra("USER_RESPONSES")
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
        ScoreManager.totalGamesRomas += 1
        ScoreManager.totalGamesRomasPro += 1

        ScoreManager.totalGamesRomasExitos += 1
        ScoreManager.totalTimeRomasExitos += timeSpentInSeconds

        ScoreManager.totalTimeRomas += timeSpentInSeconds
        ScoreManager.saveStatsGlobalAndRomas()

        ScoreManager.levelScoresRomasPro[currentLevel]?.let { previousScore ->
            ScoreManager.currentScoreRomasPro -= previousScore
        }

        ScoreManager.levelScoresRomasPro[currentLevel] = pointsEarned
        ScoreManager.currentScoreRomasPro = max(ScoreManager.currentScoreRomasPro + pointsEarned, 0)

        if (!ScoreManager.hasCompletedLevelRomasPro(currentLevel)) {
            ScoreManager.addCompletedLevelRomasPro(currentLevel)
        }

        CondecoracionTracker.marcarNivelConTimestamp("Romas", "Pro", currentLevel)
        CondecoracionTracker.verificarYEntregarPines()

       // if (currentLevel >= ScoreManager.unlockedLevelsRomasPro) {
        //    ScoreManager.unlockedLevelsRomasPro = currentLevel + 1
        //}

        ScoreManager.saveScoreRomasPro()

        verificarMedallasAntesDeMostrarExito()

        val factor = obtenerFactorCorreccion(currentLevel)
        val velocidad = 1 / ScoreManager.getTiempoPromedioGlobal()
        val precision = ScoreManager.correctGamesGlobal.toDouble() / ScoreManager.totalGamesGlobal.toDouble()

        val aporte = ((factor * velocidad * precision * 12) * 100).roundToInt() / 100.0
        ScoreManager.updateIqComponent("Romas", "Pro", aporte)
        ScoreManager.saveStatsGlobalAndRomas()

    }

    private fun handleFailureScenario() {
        ScoreManager.totalGamesGlobal += 1
        ScoreManager.totalGamesRomas += 1
        ScoreManager.totalGamesRomasPro += 1
        ScoreManager.totalTimeRomas += timeSpentInSeconds
        ScoreManager.saveStatsGlobalAndRomas()

        updateScoreToZero()
        showFailureDialog()

        ScoreManager.updateIqComponent("Romas", "Pro", 0.0)
        ScoreManager.saveStatsGlobalAndRomas()
    }

    private fun verificarMedallasAntesDeMostrarExito() {
        CondecoracionTracker.verificarYEntregarMedallas { nuevaMedalla ->

            if (nuevaMedalla != null && currentLevel == 70) {
                verificarTrofeosParaDobleCondecoracion(nuevaMedalla)
            } else if (nuevaMedalla != null) {
                mostrarAnimacionMedalla(nuevaMedalla)
            } else if (currentLevel == 70) {
                verificarTrofeosAntesDeMostrarExito()
            } else {
                showSuccessDialog()
            }
        }
    }

    private fun verificarTrofeosParaDobleCondecoracion(nuevaMedalla: CondecoracionTracker.MedallaObtenida) {
        CondecoracionTracker.verificarYEntregarTrofeos("Romas", "Pro") { nuevoTrofeo ->
            if (nuevoTrofeo != null) {
                mostrarDobleCelebracion(nuevaMedalla, nuevoTrofeo)
            } else {
                mostrarAnimacionMedalla(nuevaMedalla)
            }
        }
    }

    private fun verificarTrofeosAntesDeMostrarExito() {
        CondecoracionTracker.verificarYEntregarTrofeos("Romas", "Pro") { nuevoTrofeo ->
            if (nuevoTrofeo != null) {
                mostrarAnimacionTrofeo(nuevoTrofeo)
            } else {
                showSuccessDialog()
            }
        }
    }

    private fun mostrarDobleCelebracion(
        medalla: CondecoracionTracker.MedallaObtenida,
        trofeo: CondecoracionTracker.TrofeoObtenido
    ) {
        val dialog = CondecoracionAnimationDialog(
            context = this,
            tipoCondecoracion = TipoCondecoracion.DOBLE_CELEBRACION,
            onAnimationComplete = {
                mostrarAnimacionMedalla(medalla) {
                    mostrarAnimacionTrofeo(trofeo)
                }
            }
        )
        dialog.show()
    }

    private fun mostrarAnimacionTrofeo(
        trofeo: CondecoracionTracker.TrofeoObtenido,
        onComplete: (() -> Unit)? = null
    ) {
        val dialog = CondecoracionAnimationDialog(
            this,
            tipoCondecoracion = TipoCondecoracion.TROFEO,
            nombreTrofeo = trofeo.nombreTrofeo,
            onAnimationComplete = {
                onComplete?.invoke() ?: showSuccessDialog()
            }
        )
        dialog.show()
    }

    private fun mostrarAnimacionMedalla(
        medalla: CondecoracionTracker.MedallaObtenida,
        onComplete: (() -> Unit)? = null
    ) {
        val medallasObtenidas = CondecoracionTracker.getMedallasObtenidas().size
        val medallasRestantes = 12 - medallasObtenidas

        val dialog = CondecoracionAnimationDialog(
            this,
            medallaTipo = medalla.tipo,
            medallasObtenidas = medallasObtenidas,
            medallasRestantes = medallasRestantes,
            onAnimationComplete = {
                onComplete?.invoke() ?: showSuccessDialog()
            }
        )
        dialog.show()
    }

    private fun obtenerFactorCorreccion(maxNivel: Int): Double {
        return when (maxNivel) {
            in 1..14 -> 0.80
            in 15..28 -> 0.85
            in 29..42 -> 0.90
            in 43..56 -> 0.95
            in 57..70 -> 1.00
            else -> 0.0
        }
    }

    private fun calculatePoints(): Int {
        val basePoints = when (currentLevel) {
            in 1..7 -> 700
            in 8..14 -> 1200
            in 15..21 -> 1700
            in 22..28 -> 2200
            in 29..35 -> 2700
            in 36..42 -> 3200
            in 43..49 -> 3700
            in 50..56 -> 4200
            in 57..63 -> 4700
            else -> 5200
        }

        val pointsAfterAttempts = when (attempts) {
            0 -> basePoints
            1 -> basePoints / 2
            else -> 0
        }
        if (pointsAfterAttempts == 0) return 0

        val precisionGlobal = ScoreManager.getPrecisionGlobal()

        val velocidadBonus = 260

        val tiempoPromedio = if (ScoreManager.totalGamesRomas > 0) {
            (ScoreManager.totalTimeRomas + timeSpentInSeconds) / (ScoreManager.totalGamesRomas + 1)
        } else {
            timeSpentInSeconds
        }

        // C3 ELIMINAR EL 0.7 //

        val puntosPorVelocidad = if (tiempoPromedio > 0) {
            (velocidadBonus * (1.0 / tiempoPromedio))
        } else {
            0.0
        }

        val puntajeFinal = (pointsAfterAttempts * precisionGlobal) + puntosPorVelocidad

        return puntajeFinal.toInt()
    }

    private fun updateScoreToZero() {
        ScoreManager.levelScoresRomasPro[currentLevel]?.let { previousScore ->
            ScoreManager.currentScoreRomasPro -= previousScore
        }
        ScoreManager.levelScoresRomasPro[currentLevel] = 0
        ScoreManager.saveScoreRomasPro()
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
                    val fadeIn = AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.dialog_fade_in)
                    animationView.startAnimation(fadeIn)
                    animationView.playAnimation()

                    if (soundEnabled) {
                        playSound(R.raw.trompeta)
                    }

                    pointsTextView.visibility = View.VISIBLE

                    // LR1 Cambio para solucionar el formato de los decimales //
                    val puntosObtenidos = getString(R.string.puntos_obtenidos, pointsEarned)
                    val spannable = SpannableString(puntosObtenidos)
                    val puntosStr = pointsEarned.toString()
                    val startIdx = puntosObtenidos.indexOf(puntosStr)
                    val endIdx = startIdx + puntosStr.length
                    if (startIdx >= 0 && endIdx <= puntosObtenidos.length) {
                        spannable.setSpan(StyleSpan(Typeface.BOLD), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    pointsTextView.text = spannable
                    // Fin del cambio LR1 //

                    val pointsAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.points_appear_from_back)
                    pointsTextView.startAnimation(pointsAnimation)

                    pointsAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                        override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                        override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                            checkImageView.visibility = View.VISIBLE
                            val checkAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.check_appear)
                            checkImageView.startAnimation(checkAnimation)

                            checkAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                                    val puntosRomasPro = ScoreManager.currentScoreRomasPro
                                    val puntajeActualText = getString(R.string.puntaje_actual, puntosRomasPro)
                                    val spannablePuntajeActual = SpannableString(puntajeActualText)
                                    val puntosStrActual = puntosRomasPro.toString()
                                    val startIdxActual = puntajeActualText.indexOf(puntosStrActual)

                                    // LR2 Cambio para solucionar el formato de los decimales //
                                    val endIdxActual = startIdxActual + puntosStrActual.length
                                    if (startIdxActual >= 0 && endIdxActual <= puntajeActualText.length) {
                                        spannablePuntajeActual.setSpan(StyleSpan(Typeface.BOLD), startIdxActual, endIdxActual, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    }
                                    currentScoreTextView.text = spannablePuntajeActual
                                    // Fin del cambio LR2 //

                                    currentScoreTextView.visibility = View.VISIBLE
                                    starImageView.visibility = View.VISIBLE

                                    val puntajeActualAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.points_appear_from_back)
                                    currentScoreTextView.startAnimation(puntajeActualAnimation)
                                    starImageView.startAnimation(puntajeActualAnimation)

                                    puntajeActualAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                                        override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                                        override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                                            // Cambio de variable para tiempo real mostrado C4 //
                                            // LR3 Cambio para solucionar el formato de los decimales //
                                            val formattedTime = String.format(Locale.getDefault(), "%.2f", rawTimeSpent)
                                            val tiempoEmpleadoText = getString(R.string.tiempo_empleado, formattedTime)
                                            val spannableTime = SpannableString(tiempoEmpleadoText)
                                            val startIdxTime = tiempoEmpleadoText.indexOf(formattedTime)
                                            val endIdxTime = startIdxTime + formattedTime.length
                                            if (startIdxTime >= 0 && endIdxTime <= tiempoEmpleadoText.length) {
                                                spannableTime.setSpan(StyleSpan(Typeface.BOLD), startIdxTime, endIdxTime, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                            }
                                            timeSpentTextView.text = spannableTime

                                            // Fin del cambio LR3 //

                                            timeSpentTextView.visibility = View.VISIBLE
                                            val timeAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.points_appear_from_back)
                                            timeSpentTextView.startAnimation(timeAnimation)

                                            timeAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                                                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                                                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                                                    checkBlueImageView.visibility = View.VISIBLE
                                                    val checkBlueAnimation = AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.check_appear)
                                                    checkBlueImageView.startAnimation(checkBlueAnimation)

                                                    unlockLevelTextView.text = getString(R.string.jugar_un_nuevo_nivel)

                                                    mainHandler.postDelayed({
                                                        rankingChangedTextView.visibility = View.VISIBLE
                                                        rankingChangedTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.dialog_fade_in))
                                                    }, 300)

                                                    mainHandler.postDelayed({
                                                        unlockLevelTextView.visibility = View.VISIBLE
                                                        unlockLevelTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.dialog_fade_in))
                                                    }, 600)

                                                    mainHandler.postDelayed({
                                                        repeatLevelTextView.visibility = View.VISIBLE
                                                        repeatLevelTextView.startAnimation(AnimationUtils.loadAnimation(this@LevelResultActivityRomasPro, R.anim.dialog_fade_in))
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

        mainMessageTextView.visibility = View.VISIBLE
        rankingChangedTextView.visibility = View.VISIBLE
        unlockLevelTextView.visibility = View.VISIBLE
        repeatLevelTextView.visibility = View.VISIBLE

        val isLevelBlockedAfterThisFail = ScoreManager.getConsecutiveFailuresRomasPro(currentLevel) >= 12

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
        repeatLevelTextView.startAnimation(fadeIn)

        if (attempts >= 2 && numberList != null && userResponses != null) {
            reviewExerciseTextView.visibility = View.VISIBLE
            applyTouchAnimation(reviewExerciseTextView)

            reviewExerciseTextView.setOnClickListener {
                val intent = Intent(this, ExerciseReviewActivityRomas::class.java)
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
    private fun isSoundEnabled(): Boolean {
        val globalPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return globalPrefs.getBoolean(SettingsActivity.SOUND_ENABLED, true)
    }

    private fun playSound(soundResourceId: Int) {
        if (!isSoundEnabled()) return

        try {
            // Liberar MediaPlayer anterior de forma segura
            mediaPlayer?.let { mp ->
                try {
                    if (mp.isPlaying) {
                        mp.stop()
                    }
                    mp.release()
                } catch (e: Exception) {
                    android.util.Log.e("MediaPlayer", "Error al liberar MediaPlayer anterior", e)
                }
                mediaPlayer = null
            }

            // Crear nuevo MediaPlayer
            mediaPlayer = MediaPlayer.create(this, soundResourceId)
            mediaPlayer?.let { mp ->
                // Configurar listeners
                mp.setOnCompletionListener { player ->
                    try {
                        player.release()
                        if (mediaPlayer == player) {
                            mediaPlayer = null
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MediaPlayer", "Error en OnCompletionListener", e)
                    }
                }

                mp.setOnErrorListener { player, what, extra ->
                    android.util.Log.e("MediaPlayer", "Error reproduciendo sonido: what=$what, extra=$extra")
                    try {
                        player.release()
                        if (mediaPlayer == player) {
                            mediaPlayer = null
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MediaPlayer", "Error liberando MediaPlayer en OnError", e)
                    }
                    true // Indica que manejamos el error
                }

                // Iniciar reproducción
                mp.start()

            } ?: run {
                android.util.Log.e("MediaPlayer", "No se pudo crear MediaPlayer para recurso: $soundResourceId")
            }

        } catch (e: Exception) {
            android.util.Log.e("MediaPlayer", "Excepción general al reproducir sonido", e)
            mediaPlayer?.let { mp ->
                try {
                    mp.release()
                } catch (releaseException: Exception) {
                    android.util.Log.e("MediaPlayer", "Error al liberar en catch", releaseException)
                }
                mediaPlayer = null
            }
        }
    }

    private fun navigateToInstructions() {
        val intent = Intent(this, InstructionsActivityRomasPro::class.java)
        intent.putExtra("LEVEL", currentLevel)
        startActivity(intent)
        finish()
    }

    private fun navigateToLevels() {
        val intent = Intent(this, LevelsActivityRomasPro::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainGameActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToClassification() {
        val intent = Intent(this, ClassificationActivity::class.java)
        startActivity(intent)
        finish()
    }

}
