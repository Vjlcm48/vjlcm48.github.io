package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.heptacreation.sumamente.R
import java.util.Locale


class GameSelectionActivity : BaseActivity() {

    private companion object {
        const val TOTAL_LEVELS = 1890.0
        const val ANIMATION_DURATION = 450L
        const val ANIMATION_DELAY_START = 70L
        const val ANIMATION_DELAY_ITEM = 80L
        const val ANIMATION_TRANSLATION_Y = 50f
        const val ANIMATION_TRANSLATION_Y_ITEMS = 60f
        const val BOUNCE_SCALE_DOWN = 0.9f
        const val BOUNCE_DURATION = 50L
    }

    private enum class Game(
        val buttonId: Int,
        val prefsName: String,
        val instructionsKey: String,
        val difficultyKey: String,
        val tutorialActivity: Class<*>,
        val principianteActivity: Class<*>,
        val avanzadoActivity: Class<*>,
        val proActivity: Class<*>
    ) {
        NUMEROS_PLUS(
            R.id.btn_numeros_plus,
            "MyPrefs",
            "hasSeenInstructionsNumeros",
            "difficulty_numerosplus",
            TutorialActivityNumeros::class.java,
            LevelsActivityPrincipiante::class.java,
            LevelsActivity::class.java,
            LevelsActivityPro::class.java
        ),
        DECI_PLUS(
            R.id.btn_deci_plus,
            "MyPrefsDeciPlus",
            "hasSeenInstructionsDeciPlus",
            "difficulty_deciplus",
            TutorialActivityDeciPlus::class.java,
            LevelsActivityDeciPlusPrincipiante::class.java,
            LevelsActivityDeciPlus::class.java,
            LevelsActivityDeciPlusPro::class.java
        ),
        ROMAS(
            R.id.btn_romas,
            "MyPrefsRomas",
            "hasSeenInstructionsRomas",
            "difficulty_romas",
            TutorialActivityRomas::class.java,
            LevelsActivityRomasPrincipiante::class.java,
            LevelsActivityRomas::class.java,
            LevelsActivityRomasPro::class.java
        ),
        ALFA_NUMEROS(
            R.id.btn_alfa_numeros,
            "MyPrefsAlfaNumeros",
            "hasSeenInstructionsAlfaNumeros",
            "difficulty_alfanumeros",
            TutorialActivityAlfaNumeros::class.java,
            LevelsActivityAlfaNumerosPrincipiante::class.java,
            LevelsActivityAlfaNumeros::class.java,
            LevelsActivityAlfaNumerosPro::class.java
        ),
        SUMA_RESTA(
            R.id.btn_sumaresta,
            "MyPrefsSumaResta",
            "hasSeenInstructionsSumaResta",
            "difficulty_sumaresta",
            TutorialActivitySumaResta::class.java,
            LevelsActivitySumaRestaPrincipiante::class.java,
            LevelsActivitySumaResta::class.java,
            LevelsActivitySumaRestaPro::class.java
        ),
        MAS_PLUS(
            R.id.btn_mas_plus,
            "MyPrefsMasPlus",
            "hasSeenInstructionsMasPlus",
            "difficulty_masplus",
            TutorialActivityMasPlus::class.java,
            LevelsActivityMasPlusPrincipiante::class.java,
            LevelsActivityMasPlus::class.java,
            LevelsActivityMasPlusPro::class.java
        ),
        GENIO_PLUS(
            R.id.btn_genio_plus,
            "MyPrefsGenioPlus",
            "hasSeenInstructionsGenioPlus",
            "difficulty_genioplus",
            TutorialActivityGenioPlus::class.java,
            LevelsActivityGenioPlusPrincipiante::class.java,
            LevelsActivityGenioPlus::class.java,
            LevelsActivityGenioPlusPro::class.java
        ),
        FOCO_PLUS(
            R.id.btn_foco_plus,
            "MyPrefsFocoPlus",
            "hasSeenInstructionsFocoPlus",
            "difficulty_focoplus",
            InstructionsLevelsActivityFocoPlus::class.java,
            InstructionsLevelsActivityFocoPlus::class.java,
            InstructionsLevelsActivityFocoPlus::class.java,
            InstructionsLevelsActivityFocoPlus::class.java
        )

    }

    // Datos del juego
    private data class GameScores(
        val avanzado: Int,
        val principiante: Int,
        val pro: Int
    ) {
        val total: Int get() = avanzado + principiante + pro
    }

    private lateinit var tvTitle: TextView
    private lateinit var container: LinearLayout
    private lateinit var btnProgreso: RelativeLayout
    private lateinit var closeButton: ImageView
    private lateinit var tvPercentageProgreso: TextView
    private lateinit var btnFocoPlus: RelativeLayout
    private lateinit var tvGameNameFocoPlus: TextView
    private lateinit var tvGameSubtitleFocoPlus: TextView
    private lateinit var tvPillProximamenteFoco: TextView
    private lateinit var btnMathPlus: RelativeLayout
    private lateinit var tvGameNameMathPlus: TextView
    private lateinit var tvPillNuevoMath: TextView
    private lateinit var tvGameSubtitleMathPlus: TextView


    private val gameButtons = mutableMapOf<Game, RelativeLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        setContentView(R.layout.activity_game_selection)

        initializeScoreManager()
        initializeViews()
        setupAnimations()
        setupClickListeners()
        updateUI()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@GameSelectionActivity, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        initializeScoreManager()
        updateUI()
        updateProgressButton()
        startShineLoop(tvPillProximamenteFoco)
        startShineLoop(tvPillNuevoMath)

    }

    override fun onPause() {
        super.onPause()
        // limpiar shader del brillo cuando la activity no está visible
        if (::tvPillProximamenteFoco.isInitialized) {
            tvPillProximamenteFoco.paint.shader = null
        }
        if (::tvPillNuevoMath.isInitialized) {
            tvPillNuevoMath.paint.shader = null
        }
    }

    private fun initializeScoreManager() {
        with(ScoreManager) {
            init(this@GameSelectionActivity)
            initPrincipiante(this@GameSelectionActivity)
            initPro(this@GameSelectionActivity)
            initDeciPlus(this@GameSelectionActivity)
            initDeciPlusPrincipiante(this@GameSelectionActivity)
            initDeciPlusPro(this@GameSelectionActivity)
            initRomas(this@GameSelectionActivity)
            initRomasPrincipiante(this@GameSelectionActivity)
            initRomasPro(this@GameSelectionActivity)
            initAlfaNumeros(this@GameSelectionActivity)
            initAlfaNumerosPrincipiante(this@GameSelectionActivity)
            initAlfaNumerosPro(this@GameSelectionActivity)
            initSumaResta(this@GameSelectionActivity)
            initSumaRestaPrincipiante(this@GameSelectionActivity)
            initSumaRestaPro(this@GameSelectionActivity)
            initMasPlus(this@GameSelectionActivity)
            initMasPlusPrincipiante(this@GameSelectionActivity)
            initMasPlusPro(this@GameSelectionActivity)
            initGenioPlus(this@GameSelectionActivity)
            initGenioPlusPrincipiante(this@GameSelectionActivity)
            initGenioPlusPro(this@GameSelectionActivity)
            initFocoPlus(this@GameSelectionActivity)
            initFocoPlusPrincipiante(this@GameSelectionActivity)
            initFocoPlusPro(this@GameSelectionActivity)
        }
    }

    private fun initializeViews() {
        tvTitle = findViewById(R.id.tv_select_game_title)
        container = findViewById(R.id.layout_game_buttons)
        btnProgreso = findViewById(R.id.btn_progreso)
        closeButton = findViewById(R.id.closeButton)
        tvPercentageProgreso = btnProgreso.findViewById(R.id.tv_percentage_progreso)

        btnFocoPlus = findViewById(R.id.btn_foco_plus)
        tvGameNameFocoPlus = btnFocoPlus.findViewById(R.id.tv_game_name_foco_plus)
        tvGameSubtitleFocoPlus = btnFocoPlus.findViewById(R.id.tv_game_subtitle_foco_plus)
        tvPillProximamenteFoco = btnFocoPlus.findViewById(R.id.tv_pill_proximamente_foco)

        btnMathPlus = findViewById(R.id.btn_math_plus)

        btnMathPlus.visibility = View.GONE   ///  al eliminar esta línea el botón de Math plus vuelve a aparecer ///

        tvGameNameMathPlus = btnMathPlus.findViewById(R.id.tv_game_name_math_plus)
        tvGameSubtitleMathPlus = btnMathPlus.findViewById(R.id.tv_game_subtitle_math_plus)
        tvPillNuevoMath = btnMathPlus.findViewById(R.id.tv_pill_nuevo_math)

        Game.entries.forEach { game ->
            gameButtons[game] = findViewById(game.buttonId)
        }
    }

    private fun setupAnimations() {
        animateTitleEntry()
        animateButtonsEntry()
        animateProgressButton()
    }

    private fun animateTitleEntry() {
        tvTitle.apply {
            translationY = ANIMATION_TRANSLATION_Y
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(ANIMATION_DURATION)
                .setStartDelay(ANIMATION_DELAY_START)
                .start()
        }
    }

    private fun animateButtonsEntry() {
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            view.alpha = 0f
            view.translationY = ANIMATION_TRANSLATION_Y_ITEMS
            val animator = view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(ANIMATION_DURATION)
                .setStartDelay(200 + i * ANIMATION_DELAY_ITEM)

            if (i == container.childCount - 1) {
                animator.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        startTitleShineAnimation(tvTitle)
                    }
                })
            }
            animator.start()
        }
    }

    private fun animateProgressButton() {
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_progress_button)
        btnProgreso.startAnimation(pulseAnimation)
    }

    private fun startTitleShineAnimation(textView: TextView) {
        textView.post {
            val textWidth = textView.paint.measureText(textView.text.toString())
            val baseColor = textView.currentTextColor
            val shineColor = ContextCompat.getColor(this, R.color.white)

            val shader = LinearGradient(
                -textWidth, 0f, 0f, 0f,
                intArrayOf(baseColor, shineColor, baseColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )

            textView.paint.shader = shader
            val matrix = Matrix()

            val animator = ValueAnimator.ofFloat(0f, 2 * textWidth)
            animator.duration = 800
            animator.startDelay = 500
            animator.addUpdateListener {
                val translate = it.animatedValue as Float
                matrix.setTranslate(translate, 0f)
                shader.setLocalMatrix(matrix)
                textView.invalidate()
            }
            animator.addListener(object: AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {

                    textView.paint.shader = null
                }
            })
            animator.start()
        }
    }

    private fun setupClickListeners() {
        btnProgreso.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, ProgressSummaryActivity::class.java))
            }
        }

        closeButton.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        btnFocoPlus.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(DifficultySelectionActivity.createIntent(this, "FocoPlus"))
            }
        }

        btnMathPlus.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(DifficultySelectionActivity.createIntent(this, "MathPlus"))
            }
        }

        Game.entries.forEach { game ->
            gameButtons[game]?.setOnClickListener {
                applyBounceEffect(it) {
                    handleGameButtonClick(game)
                }
            }
        }
    }

    private fun handleGameButtonClick(game: Game) {
        val prefs = getSharedPreferences(game.prefsName, MODE_PRIVATE)
        val hasSeenInstructions = prefs.getBoolean(game.instructionsKey, false)

        if (!hasSeenInstructions) {
            startActivity(Intent(this, game.tutorialActivity))
            return
        }

        val hasDifficulty = prefs.contains(game.difficultyKey)
        if (!hasDifficulty) {
            // Mapear el enum al string correcto que espera DifficultySelectionActivity
            val gameTypeString = when(game) {
                Game.NUMEROS_PLUS -> "NumerosPlus"
                Game.DECI_PLUS -> "DeciPlus"
                Game.ROMAS -> "Romas"
                Game.ALFA_NUMEROS -> "AlfaNumeros"
                Game.SUMA_RESTA -> "Sumaresta"
                Game.MAS_PLUS -> "MasPlus"
                Game.GENIO_PLUS -> "GenioPlus"
                Game.FOCO_PLUS -> "FocoPlus"
            }
            startActivity(DifficultySelectionActivity.createIntent(this, gameTypeString))
            return
        }

        val difficulty = prefs.getString(game.difficultyKey, DifficultySelectionActivity.DIFFICULTY_AVANZADO)

        if (game == Game.FOCO_PLUS && (difficulty == DifficultySelectionActivity.DIFFICULTY_AVANZADO || difficulty == DifficultySelectionActivity.DIFFICULTY_PRO)) {
            startActivity(DifficultySelectionActivity.createIntent(this, "FocoPlus"))
            return
        }

        val intent = when (difficulty) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> Intent(this, game.principianteActivity)
            DifficultySelectionActivity.DIFFICULTY_PRO -> Intent(this, game.proActivity)
            else -> Intent(this, game.avanzadoActivity)
        }
        startActivity(intent)
    }

    private fun updateUI() {
        val scores = getGameScores()

        updateGameButton(Game.NUMEROS_PLUS, scores[Game.NUMEROS_PLUS]!!)
        updateGameButton(Game.DECI_PLUS, scores[Game.DECI_PLUS]!!)
        updateGameButton(Game.ROMAS, scores[Game.ROMAS]!!)
        updateGameButton(Game.ALFA_NUMEROS, scores[Game.ALFA_NUMEROS]!!)
        updateGameButton(Game.SUMA_RESTA, scores[Game.SUMA_RESTA]!!)
        updateGameButton(Game.MAS_PLUS, scores[Game.MAS_PLUS]!!)
        updateGameButton(Game.GENIO_PLUS, scores[Game.GENIO_PLUS]!!)
        updateGameButton(Game.FOCO_PLUS, scores[Game.FOCO_PLUS]!!)

        applySpecialColors()
    }

    private fun getGameScores(): Map<Game, GameScores> {
        return mapOf(
            Game.NUMEROS_PLUS to GameScores(
                ScoreManager.currentScore,
                ScoreManager.currentScorePrincipiante,
                ScoreManager.currentScorePro
            ),
            Game.DECI_PLUS to GameScores(
                ScoreManager.currentScoreDeciPlus,
                ScoreManager.currentScoreDeciPlusPrincipiante,
                ScoreManager.currentScoreDeciPlusPro
            ),
            Game.ROMAS to GameScores(
                ScoreManager.currentScoreRomas,
                ScoreManager.currentScoreRomasPrincipiante,
                ScoreManager.currentScoreRomasPro
            ),
            Game.ALFA_NUMEROS to GameScores(
                ScoreManager.currentScoreAlfaNumeros,
                ScoreManager.currentScoreAlfaNumerosPrincipiante,
                ScoreManager.currentScoreAlfaNumerosPro
            ),
            Game.SUMA_RESTA to GameScores(
                ScoreManager.currentScoreSumaResta,
                ScoreManager.currentScoreSumaRestaPrincipiante,
                ScoreManager.currentScoreSumaRestaPro
            ),
            Game.MAS_PLUS to GameScores(
                ScoreManager.currentScoreMasPlus,
                ScoreManager.currentScoreMasPlusPrincipiante,
                ScoreManager.currentScoreMasPlusPro
            ),
            Game.GENIO_PLUS to GameScores(
                ScoreManager.currentScoreGenioPlus,
                ScoreManager.currentScoreGenioPlusPrincipiante,
                ScoreManager.currentScoreGenioPlusPro
            ),
            Game.FOCO_PLUS to GameScores(
                ScoreManager.currentScoreFocoPlus,
                ScoreManager.currentScoreFocoPlusPrincipiante,
                ScoreManager.currentScoreFocoPlusPro
            )
        )
    }

    private fun updateGameButton(game: Game, scores: GameScores) {
        val button = gameButtons[game] ?: return
        val gameNameTextView: TextView
        val pointsTextView: TextView
        val gameSubtitleTextView: TextView
        var titleResId: Int? = null
        var subtitleResId: Int?

        when (game) {
            Game.NUMEROS_PLUS -> {
                gameNameTextView = button.findViewById(R.id.tv_game_name_numeros_plus)
                pointsTextView = button.findViewById(R.id.tv_points_numeros_plus)
                gameSubtitleTextView = button.findViewById(R.id.tv_game_subtitle_numeros_plus)
                titleResId = R.string.game_numeros_plus
                subtitleResId = R.string.game_subtitle_numeros_plus
            }
            Game.DECI_PLUS -> {
                gameNameTextView = button.findViewById(R.id.tv_game_name_deci_plus)
                pointsTextView = button.findViewById(R.id.tv_points_deci_plus)
                gameSubtitleTextView = button.findViewById(R.id.tv_game_subtitle_deci_plus)
                titleResId = R.string.game_deci_plus
                subtitleResId = R.string.game_subtitle_deci_plus
            }
            Game.ROMAS -> {
                gameNameTextView = button.findViewById(R.id.tv_game_name_romas)
                pointsTextView = button.findViewById(R.id.tv_points_romas)
                gameSubtitleTextView = button.findViewById(R.id.tv_game_subtitle_romas)
                titleResId = R.string.game_romas
                subtitleResId = R.string.game_subtitle_romas
            }
            Game.ALFA_NUMEROS -> {
                gameNameTextView = button.findViewById(R.id.tv_game_name_alfa_numeros)
                pointsTextView = button.findViewById(R.id.tv_points_alfa_numeros)
                gameSubtitleTextView = button.findViewById(R.id.tv_game_subtitle_alfa_numeros)
                subtitleResId = R.string.game_subtitle_alfanumeros
            }
            Game.SUMA_RESTA -> {
                gameNameTextView = button.findViewById(R.id.tv_game_name_sumaresta)
                pointsTextView = button.findViewById(R.id.tv_points_sumaresta)
                gameSubtitleTextView = button.findViewById(R.id.tv_game_subtitle_sumaresta)
                subtitleResId = R.string.game_subtitle_sumaresta
            }
            Game.MAS_PLUS -> {
                gameNameTextView = button.findViewById(R.id.tv_game_name_mas_plus)
                pointsTextView = button.findViewById(R.id.tv_points_mas_plus)
                gameSubtitleTextView = button.findViewById(R.id.tv_game_subtitle_mas_plus)
                titleResId = R.string.game_mas_plus
                subtitleResId = R.string.game_subtitle_mas_plus
            }
            Game.GENIO_PLUS -> {
                gameNameTextView = button.findViewById(R.id.tv_game_name_genio_plus)
                pointsTextView = button.findViewById(R.id.tv_points_genio_plus)
                gameSubtitleTextView = button.findViewById(R.id.tv_game_subtitle_genio_plus)
                titleResId = R.string.game_genio_plus
                subtitleResId = R.string.game_subtitle_genio_plus
            }
            Game.FOCO_PLUS -> {
                gameNameTextView = button.findViewById(R.id.tv_game_name_foco_plus)
                pointsTextView = button.findViewById(R.id.tv_points_foco_plus)
                gameSubtitleTextView = button.findViewById(R.id.tv_game_subtitle_foco_plus)
                titleResId = R.string.game_foco_plus
                subtitleResId = R.string.game_subtitle_foco_plus
            }
        }

        titleResId?.let { gameNameTextView.text = getString(it) }

        if (game == Game.FOCO_PLUS) {
            subtitleResId.let {
                gameSubtitleTextView.text = getString(it)
                gameSubtitleTextView.visibility = View.VISIBLE
            }
        } else if (Locale.getDefault().language != "es") {
            subtitleResId.let {
                gameSubtitleTextView.text = getString(it)
                gameSubtitleTextView.visibility = View.VISIBLE
            }
        } else {
            gameSubtitleTextView.visibility = View.GONE
        }

        if (scores.total > 0) {
            pointsTextView.text = scores.total.toString()
            pointsTextView.visibility = View.VISIBLE
        } else {
            pointsTextView.visibility = View.GONE
        }
    }

    private fun applySpecialColors() {
        gameButtons[Game.ALFA_NUMEROS]?.let { button ->
            applyAlfaNumerosColor(button)
        }

        gameButtons[Game.SUMA_RESTA]?.let { button ->
            applySumarestaColor(button)
        }

        gameButtons[Game.MAS_PLUS]?.let { button ->
            val textView = button.findViewById<TextView>(R.id.tv_game_name_mas_plus)
            if (isNightMode()) {
                textView.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
            } else {
                textView.setTextColor(ContextCompat.getColor(this, R.color.grey_light))
            }
        }

        gameButtons[Game.GENIO_PLUS]?.let { button ->
            val textView = button.findViewById<TextView>(R.id.tv_game_name_genio_plus)
            if (isNightMode()) {
                textView.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
            } else {
                textView.setTextColor(ContextCompat.getColor(this, R.color.blue_pressed))
            }
        }
    }

    private fun applyAlfaNumerosColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_alfa_numeros)

        if (isNightMode()) {
            textView.text = getString(R.string.game_alfa_numeros)
            textView.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
        } else {
            val alfaText = getString(R.string.text_alfa)
            val numerosText = getString(R.string.text_numeros)
            val alfaNumerosText = "$alfaText$numerosText"
            val spannable = SpannableString(alfaNumerosText)
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)),
                0, alfaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)),
                alfaText.length, alfaNumerosText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannable
        }
    }

    private fun applySumarestaColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_sumaresta)

        if (isNightMode()) {
            textView.text = getString(R.string.game_sumaresta)
            textView.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
        } else {
            val sumaText = getString(R.string.text_suma)
            val restaText = getString(R.string.text_resta)
            val sumarestaText = "$sumaText$restaText"
            val spannable = SpannableString(sumarestaText)
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)),
                0, sumaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                sumaText.length, sumarestaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannable
        }
    }

    private fun updateProgressButton() {
        val totalCompleted = ScoreManager.getTotalUniqueLevelsCompletedAllGames()
        val percentage = (totalCompleted / TOTAL_LEVELS) * 100
        val percentageString = String.format(Locale.getDefault(), "%.2f", percentage)
        tvPercentageProgreso.text = getString(R.string.percentage_format, percentageString)


        if (isNightMode()) {
            tvPercentageProgreso.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
        } else {
            tvPercentageProgreso.setTextColor(ContextCompat.getColor(this, R.color.blue_primary_dark))
        }
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
    }

    private fun getColorFromAttr(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    private fun startShineLoop(textView: TextView) {
        textView.post {
            val text = textView.text?.toString() ?: return@post
            val textWidth = textView.paint.measureText(text)
            val baseColor = textView.currentTextColor
            val shineColor = ContextCompat.getColor(this, R.color.white)

            val shader = LinearGradient(
                -textWidth, 0f, 0f, 0f,
                intArrayOf(baseColor, shineColor, baseColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            textView.paint.shader = shader
            val matrix = Matrix()

            ValueAnimator.ofFloat(0f, 2 * textWidth).apply {
                duration = 1400
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener {
                    val translate = it.animatedValue as Float
                    matrix.setTranslate(translate, 0f)
                    shader.setLocalMatrix(matrix)
                    textView.invalidate()
                }
                start()
            }
        }
    }


    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, BOUNCE_SCALE_DOWN).setDuration(BOUNCE_DURATION)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, BOUNCE_SCALE_DOWN).setDuration(BOUNCE_DURATION)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", BOUNCE_SCALE_DOWN, 1f).setDuration(BOUNCE_DURATION)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", BOUNCE_SCALE_DOWN, 1f).setDuration(BOUNCE_DURATION)

        AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
            playTogether(scaleUpX, scaleUpY)
            playSequentially(scaleDownX, scaleUpX)

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd()
                }
            })

            start()
        }
    }
}
