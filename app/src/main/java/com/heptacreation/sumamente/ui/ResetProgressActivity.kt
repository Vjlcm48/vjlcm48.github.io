package com.heptacreation.sumamente.ui

import android.animation.*
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.drawable.toDrawable
import com.heptacreation.sumamente.R
import java.util.Locale
import androidx.activity.enableEdgeToEdge

class ResetProgressActivity : BaseActivity() {

    private var selectedGame: String? = null
    private var selectedDifficulty: String? = null

    private lateinit var tvTitle: TextView
    private lateinit var btnClose: ImageView
    private lateinit var layoutButtons: LinearLayout
    private lateinit var btnNumerosPlus: ConstraintLayout
    private lateinit var btnDeciPlus: ConstraintLayout
    private lateinit var btnRomas: ConstraintLayout
    private lateinit var btnAlfaNumeros: ConstraintLayout
    private lateinit var btnSumaresta: ConstraintLayout
    private lateinit var btnMasPlus: ConstraintLayout
    private lateinit var btnGenioPlus: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initializeScoreManagers()
        setContentView(R.layout.activity_reset_progress)
        bindViews()
        styleColoredButtons()
        setupClickListeners()
        setupEntranceAnimations()
    }

    private fun initializeScoreManagers() {
        ScoreManager.init(this)
        ScoreManager.initPrincipiante(this)
        ScoreManager.initPro(this)
        ScoreManager.initDeciPlus(this)
        ScoreManager.initDeciPlusPrincipiante(this)
        ScoreManager.initDeciPlusPro(this)
        ScoreManager.initRomas(this)
        ScoreManager.initRomasPrincipiante(this)
        ScoreManager.initRomasPro(this)
        ScoreManager.initAlfaNumeros(this)
        ScoreManager.initAlfaNumerosPrincipiante(this)
        ScoreManager.initAlfaNumerosPro(this)
        ScoreManager.initSumaResta(this)
        ScoreManager.initSumaRestaPrincipiante(this)
        ScoreManager.initSumaRestaPro(this)
        ScoreManager.initMasPlus(this)
        ScoreManager.initMasPlusPrincipiante(this)
        ScoreManager.initMasPlusPro(this)
        ScoreManager.initGenioPlus(this)
        ScoreManager.initGenioPlusPrincipiante(this)
        ScoreManager.initGenioPlusPro(this)
    }

    private fun bindViews() {
        tvTitle = findViewById(R.id.tv_reset_title)
        btnClose = findViewById(R.id.closeButton)
        layoutButtons = findViewById(R.id.layout_reset_buttons)
        btnNumerosPlus = findViewById(R.id.btn_numeros_plus)
        btnDeciPlus = findViewById(R.id.btn_deci_plus)
        btnRomas = findViewById(R.id.btn_romas)
        btnAlfaNumeros = findViewById(R.id.btn_alfa_numeros)
        btnSumaresta = findViewById(R.id.btn_sumaresta)
        btnMasPlus = findViewById(R.id.btn_mas_plus)
        btnGenioPlus = findViewById(R.id.btn_genio_plus)

        btnNumerosPlus.findViewById<ImageView>(R.id.ic_reset_numeros_plus).tag = "reset"
        btnDeciPlus.findViewById<ImageView>(R.id.ic_reset_deci_plus).tag = "reset"
        btnRomas.findViewById<ImageView>(R.id.ic_reset_romas).tag = "reset"
        btnAlfaNumeros.findViewById<ImageView>(R.id.ic_reset_alfa_numeros).tag = "reset"
        btnSumaresta.findViewById<ImageView>(R.id.ic_reset_sumaresta).tag = "reset"
        btnMasPlus.findViewById<ImageView>(R.id.ic_reset_mas_plus).tag = "reset"
        btnGenioPlus.findViewById<ImageView>(R.id.ic_reset_genio_plus).tag = "reset"

    }

    private fun styleColoredButtons() {

        val tvAlfaNumeros = btnAlfaNumeros.findViewById<TextView>(R.id.tv_game_name_alfa_numeros)
        if (isNightMode()) {
            tvAlfaNumeros.text = getString(R.string.game_alfa_numeros)
            tvAlfaNumeros.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
        } else {
            val alfaText = getString(R.string.text_alfa)
            val numerosText = getString(R.string.text_numeros)
            val spannableAlfaNumeros = SpannableString("$alfaText$numerosText").apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@ResetProgressActivity, R.color.red_primary)),
                    0, alfaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@ResetProgressActivity, R.color.blue_primary_darker)),
                    alfaText.length, alfaText.length + numerosText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            tvAlfaNumeros.text = spannableAlfaNumeros
        }

        val tvSumaresta = btnSumaresta.findViewById<TextView>(R.id.tv_game_name_sumaresta)
        if (isNightMode()) {
            tvSumaresta.text = getString(R.string.game_sumaresta)
            tvSumaresta.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
        } else {
            val sumaText = getString(R.string.text_suma)
            val restaText = getString(R.string.text_resta)
            val spannableSumaresta = SpannableString("$sumaText$restaText").apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@ResetProgressActivity, R.color.blue_pressed)),
                    0, sumaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@ResetProgressActivity, R.color.red)),
                    sumaText.length, sumaText.length + restaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            tvSumaresta.text = spannableSumaresta
        }

        val tvMasPlus = btnMasPlus.findViewById<TextView>(R.id.tv_game_name_mas_plus)
        if (isNightMode()) {
            tvMasPlus.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
        } else {
            tvMasPlus.setTextColor(ContextCompat.getColor(this, R.color.grey_light))
        }

        val tvGenioPlus = btnGenioPlus.findViewById<TextView>(R.id.tv_game_name_genio_plus)
        if (isNightMode()) {
            tvGenioPlus.setTextColor(getColorFromAttr(this, R.attr.colorOnBackground))
        } else {
            tvGenioPlus.setTextColor(ContextCompat.getColor(this, R.color.blue_pressed))
        }
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun getColorFromAttr(context: android.content.Context, attrId: Int): Int {
        val typedValue = android.util.TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    private fun setupClickListeners() {
        btnNumerosPlus.setOnClickListener {
            animateResetIcon(it.findViewWithTag("reset")!!)
            applyBounceEffect(it) {
                it.postDelayed({
                    selectGameAndShowDialog("NumerosPlus", R.string.game_numeros_plus)
                }, 700)
            }
        }
        btnDeciPlus.setOnClickListener {
            animateResetIcon(it.findViewWithTag("reset")!!)
            applyBounceEffect(it) {
                it.postDelayed({
                    selectGameAndShowDialog("DeciPlus", R.string.game_deci_plus)
                }, 700)
            }
        }
        btnRomas.setOnClickListener {
            animateResetIcon(it.findViewWithTag("reset")!!)
            applyBounceEffect(it) {
                it.postDelayed({
                    selectGameAndShowDialog("Romas", R.string.game_romas)
                }, 700)
            }
        }
        btnAlfaNumeros.setOnClickListener {
            animateResetIcon(it.findViewWithTag("reset")!!)
            applyBounceEffect(it) {
                it.postDelayed({
                    selectGameAndShowDialog("AlfaNumeros", R.string.game_alfa_numeros)
                }, 700)
            }
        }
        btnSumaresta.setOnClickListener {
            animateResetIcon(it.findViewWithTag("reset")!!)
            applyBounceEffect(it) {
                it.postDelayed({
                    selectGameAndShowDialog("Sumaresta", R.string.game_sumaresta)
                }, 700)
            }
        }
        btnMasPlus.setOnClickListener {
            animateResetIcon(it.findViewWithTag("reset")!!)
            applyBounceEffect(it) {
                it.postDelayed({
                    selectGameAndShowDialog("MasPlus", R.string.game_mas_plus)
                }, 700)
            }
        }
        btnGenioPlus.setOnClickListener {
            animateResetIcon(it.findViewWithTag("reset")!!)
            applyBounceEffect(it) {
                it.postDelayed({
                    selectGameAndShowDialog("GenioPlus", R.string.game_genio_plus)
                }, 700)
            }
        }

        btnClose.setOnClickListener { applyBounceEffect(it) { finish() } }
    }


    private fun animateResetIcon(icon: ImageView) {
        ObjectAnimator.ofFloat(icon, "rotation", 0f, 360f).apply {
            duration = 700
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun selectGameAndShowDialog(gameKey: String, titleRes: Int) {
        selectedGame = gameKey
        showDifficultySelectionDialog(getString(titleRes))
    }

    private fun setupEntranceAnimations() {
        // Título
        tvTitle.alpha = 0f
        tvTitle.animate()
            .alpha(1f)
            .setDuration(450)
            .setStartDelay(100)
            .start()

        btnClose.alpha = 0f
        btnClose.translationY = 40f
        btnClose.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setStartDelay(70)
            .start()

        for (i in 0 until layoutButtons.childCount) {
            val view = layoutButtons.getChildAt(i)
            view.alpha = 0f
            view.translationY = 60f
            val animator = view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(450)
                .setStartDelay(200 + i * 80L)
            if (i == layoutButtons.childCount - 1) {
                animator.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        startTitleShineAnimation(tvTitle)
                    }
                })
            }
            animator.start()
        }
    }

    private fun startTitleShineAnimation(textView: TextView) {
        textView.post {
            val textWidth = textView.paint.measureText(textView.text.toString())
            val baseColor = textView.currentTextColor
            val shineColor = ContextCompat.getColor(this, R.color.white)

            val shader = android.graphics.LinearGradient(
                -textWidth, 0f, 0f, 0f,
                intArrayOf(baseColor, shineColor, baseColor),
                floatArrayOf(0f, 0.5f, 1f),
                android.graphics.Shader.TileMode.CLAMP
            )

            textView.paint.shader = shader
            val matrix = android.graphics.Matrix()

            val animator = ValueAnimator.ofFloat(0f, 2 * textWidth)
            animator.duration = 800
            animator.startDelay = 500
            animator.addUpdateListener {
                val translate = it.animatedValue as Float
                matrix.setTranslate(translate, 0f)
                shader.setLocalMatrix(matrix)
                textView.invalidate()
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    textView.paint.shader = null
                }
            })
            animator.start()
        }
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleDownX, scaleDownY)
        animatorSet.playTogether(scaleUpX, scaleUpY)
        animatorSet.playSequentially(scaleDownX, scaleUpX)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }

    private fun getDifficultyKey(gameType: String): String {
        return "difficulty_${gameType.lowercase(Locale.getDefault())}"
    }

    private fun showDifficultySelectionDialog(gameName: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_difficulty, null)

        val tvGameName = dialogView.findViewById<TextView>(R.id.tv_game_name)
        tvGameName.text = gameName

        val btnPrincipiante = dialogView.findViewById<RelativeLayout>(R.id.btn_principiante)
        val btnAvanzado = dialogView.findViewById<RelativeLayout>(R.id.btn_avanzado)
        val btnPro = dialogView.findViewById<RelativeLayout>(R.id.btn_pro)
        val closeButton = dialogView.findViewById<ImageView>(R.id.closeButton)

        btnAvanzado.isEnabled = true
        btnPrincipiante.isEnabled = true
        btnPrincipiante.alpha = 1.0f
        btnPro.isEnabled = true
        btnPro.alpha = 1.0f

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        closeButton.setOnClickListener { view ->
            applyBounceEffect(view) {
                alertDialog.dismiss()
            }
        }

        alertDialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnPrincipiante.setOnClickListener {
            applyBounceEffect(it) {
                selectedDifficulty = DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE
                if (selectedGame == "NumerosPlus") {
                    val prefsNumeros = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    prefsNumeros.edit {
                        putString(getDifficultyKey(selectedGame!!), DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)
                    }
                }
                alertDialog.dismiss()
                showResetOptionsDialog()
            }
        }

        btnAvanzado.setOnClickListener {
            applyBounceEffect(it) {
                selectedDifficulty = DifficultySelectionActivity.DIFFICULTY_AVANZADO
                if (selectedGame == "NumerosPlus") {
                    val prefsNumeros = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    prefsNumeros.edit {
                        putString(getDifficultyKey(selectedGame!!), DifficultySelectionActivity.DIFFICULTY_AVANZADO)
                    }
                }
                alertDialog.dismiss()
                showResetOptionsDialog()
            }
        }

        btnPro.setOnClickListener {
            applyBounceEffect(it) {
                selectedDifficulty = DifficultySelectionActivity.DIFFICULTY_PRO
                if (selectedGame == "NumerosPlus") {
                    val prefsNumeros = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    prefsNumeros.edit {
                        putString(getDifficultyKey(selectedGame!!), DifficultySelectionActivity.DIFFICULTY_PRO)
                    }
                }
                alertDialog.dismiss()
                showResetOptionsDialog()
            }
        }

        alertDialog.show()

        alertDialog.window?.let { window ->
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            val roundedDrawable = GradientDrawable().apply {
                setColor(getColorFromAttr(this@ResetProgressActivity, android.R.attr.colorBackground))
                cornerRadius = 30f
            }
            dialogView.findViewById<RelativeLayout>(R.id.difficulty_selection_container).background = roundedDrawable
        }
    }

    private fun showResetOptionsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_options, null)
        val checkboxResetScore = dialogView.findViewById<CheckBox>(R.id.checkbox_reset_score)
        val checkboxResetResponseMode = dialogView.findViewById<CheckBox>(R.id.checkbox_reset_response_mode)
        val buttonAccept = dialogView.findViewById<Button>(R.id.button_accept)
        val closeButtonDialog = dialogView.findViewById<ImageView>(R.id.closeButtonDialog)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        buttonAccept.setOnClickListener { view ->
            applyBounceEffect(view) {
                val selectedOptions = mutableListOf<String>()
                if (checkboxResetScore.isChecked) {
                    selectedOptions.add(getString(R.string.reset_score))
                }
                if (checkboxResetResponseMode.isChecked) {
                    selectedOptions.add(getString(R.string.reset_response_mode))
                }

                if (selectedOptions.isNotEmpty()) {
                    showConfirmationDialog(selectedOptions)
                } else {
                    Toast.makeText(this, getString(R.string.toast_select_option), Toast.LENGTH_SHORT).show()
                }
                alertDialog.dismiss()
            }
        }

        closeButtonDialog.setOnClickListener { view ->
            applyBounceEffect(view) {
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
        alertDialog.window?.let { window ->
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            val rootView = dialogView.rootView
            if (rootView is ViewGroup) {
                val roundedDrawable = GradientDrawable().apply {
                    setColor(Color.WHITE)
                    cornerRadius = 30f
                }
                rootView.background = roundedDrawable
            }
        }
    }

    private fun showConfirmationDialog(selectedOptions: List<String>) {
        val message = getString(R.string.confirmation_message)
        val yesText = SpannableString(getString(R.string.yes))
        yesText.setSpan(StyleSpan(Typeface.BOLD), 0, yesText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val noText = SpannableString(getString(R.string.no))
        noText.setSpan(StyleSpan(Typeface.BOLD), 0, noText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val alertDialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(yesText, null)
            .setNegativeButton(noText, null)
            .create()

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener { view ->
            applyBounceEffect(view) {
                resetSelectedData(selectedOptions)
                alertDialog.dismiss()
            }
        }

        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negativeButton.setOnClickListener { view ->
            applyBounceEffect(view) {
                alertDialog.dismiss()
            }
        }
    }

    private fun resetSelectedData(selectedOptions: List<String>) {
        selectedGame?.let { game ->
            selectedDifficulty?.let { difficulty ->
                if (selectedOptions.contains(getString(R.string.reset_score))) {
                    clearScoreDataForGame(game, difficulty)
                }
                if (selectedOptions.contains(getString(R.string.reset_response_mode))) {
                    clearResponseModeForGame(game, difficulty)
                }
            }
        }
        initializeScoreManagers()
        Toast.makeText(this, getString(R.string.data_reset_successfully), Toast.LENGTH_SHORT).show()
        selectedGame = null
        selectedDifficulty = null
    }

    private fun clearScoreDataForGame(gameName: String, difficulty: String) {
        when (gameName) {
            "NumerosPlus" -> {

                val myPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsNumeros", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsPrincipiante",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetPrincipiante()
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefs", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.reset()
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsPro", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetPro()
                    }
                }

                myPrefs.edit { putBoolean("hasSeenInstructionsNumeros", hasSeenTutorial) }
            }
            "DeciPlus" -> {

                val myPrefs = getSharedPreferences("MyPrefsDeciPlus", MODE_PRIVATE)
                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsDeciPlus", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsDeciPlusPrincipiante",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetDeciPlusPrincipiante()
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsDeciPlus", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetDeciPlus()
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsDeciPlusPro", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetDeciPlusPro()
                    }
                }

                myPrefs.edit { putBoolean("hasSeenInstructionsDeciPlus", hasSeenTutorial) }
            }
            "Romas" -> {
                val myPrefs = getSharedPreferences("MyPrefsRomas", MODE_PRIVATE)
                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsRomas", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsRomasPrincipiante",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetRomasPrincipiante()
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsRomas", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetRomas()
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsRomasPro", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetRomasPro()
                    }
                }

                myPrefs.edit { putBoolean("hasSeenInstructionsRomas", hasSeenTutorial) }
            }
            "AlfaNumeros" -> {
                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsAlfaNumerosPrincipiante",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetAlfaNumerosPrincipiante()
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsAlfaNumeros", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetAlfaNumeros()
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsAlfaNumerosPro",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetAlfaNumerosPro()
                    }
                }
            }
            "SumaResta" -> {
                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsSumaRestaPrincipiante",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetSumaRestaPrincipiante()
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsSumaResta", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetSumaResta()
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsSumaRestaPro",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetSumaRestaPro()

                    }
                }
            }
            "MasPlus" -> {
                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsMasPlusPrincipiante",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetMasPlusPrincipiante()
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsMasPlus", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetMasPlus()
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsMasPlusPro", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetMasPlusPro()
                    }
                }
            }
            "GenioPlus" -> {
                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsGenioPlusPrincipiante",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetGenioPlusPrincipiante()
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsGenioPlus", MODE_PRIVATE)
                        scorePrefs.edit { clear() }
                        ScoreManager.resetGenioPlus()
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        val scorePrefs = getSharedPreferences("ScorePrefsGenioPlusPro",
                            MODE_PRIVATE
                        )
                        scorePrefs.edit { clear() }
                        ScoreManager.resetGenioPlusPro()
                    }
                }
            }
        }
    }

    private fun clearResponseModeForGame(gameName: String, difficulty: String) {
        when (gameName) {
            "NumerosPlus" -> {
                val myPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)

                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsNumeros", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        myPrefs.edit {
                            remove("selectedResponseModePrincipiante")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)

                            putBoolean("hasSeenInstructionsNumeros", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        myPrefs.edit {
                            remove("selectedResponseMode")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_AVANZADO)

                            putBoolean("hasSeenInstructionsNumeros", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        myPrefs.edit {
                            remove("selectedResponseModePro")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRO)

                            putBoolean("hasSeenInstructionsNumeros", hasSeenTutorial)
                        }
                    }
                }
            }
            "DeciPlus" -> {
                val myPrefs = getSharedPreferences("MyPrefsDeciPlus", MODE_PRIVATE)

                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsDeciPlus", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        myPrefs.edit {
                            remove("selectedResponseModeDialogDeciPlusPrincipiante")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)

                            putBoolean("hasSeenInstructionsDeciPlus", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeDialogDeciPlus")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_AVANZADO)

                            putBoolean("hasSeenInstructionsDeciPlus", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeDialogDeciPlusPro")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRO)

                            putBoolean("hasSeenInstructionsDeciPlus", hasSeenTutorial)
                        }
                    }
                }
            }
            "Romas" -> {
                val myPrefs = getSharedPreferences("MyPrefsRomas", MODE_PRIVATE)
                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsRomas", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        myPrefs.edit {
                            remove("selectedResponseModeRomasPrincipiante")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)
                            putBoolean("hasSeenInstructionsRomas", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeRomas")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_AVANZADO)
                            putBoolean("hasSeenInstructionsRomas", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeRomasPro")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRO)
                            putBoolean("hasSeenInstructionsRomas", hasSeenTutorial)
                        }
                    }
                }
            }
            "AlfaNumeros" -> {
                val myPrefs = getSharedPreferences("MyPrefsAlfaNumeros", MODE_PRIVATE)
                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsAlfaNumeros", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        myPrefs.edit {
                            remove("selectedResponseModeAlfaNumerosPrincipiante")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)
                            putBoolean("hasSeenInstructionsAlfaNumeros", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeAlfaNumeros")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_AVANZADO)
                            putBoolean("hasSeenInstructionsAlfaNumeros", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeAlfaNumerosPro")
                            putString(
                                getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRO
                            )
                            putBoolean("hasSeenInstructionsAlfaNumeros", hasSeenTutorial)
                        }
                    }
                }
            }
            "Sumaresta" -> {
                val myPrefs = getSharedPreferences("MyPrefsSumaResta", MODE_PRIVATE)
                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsSumaResta", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        myPrefs.edit {
                            remove("selectedResponseModeSumaRestaPrincipiante")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)
                            putBoolean("hasSeenInstructionsSumaResta", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeSumaResta")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_AVANZADO)
                            putBoolean("hasSeenInstructionsSumaResta", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeSumaRestaPro")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRO)
                            putBoolean("hasSeenInstructionsSumaResta", hasSeenTutorial)
                        }

                    }
                }
            }
            "MasPlus" -> {
                val myPrefs = getSharedPreferences("MyPrefsMasPlus", MODE_PRIVATE)
                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsMasPlus", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        myPrefs.edit {
                            remove("selectedResponseModeMasPlusPrincipiante")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)
                            putBoolean("hasSeenInstructionsMasPlus", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeMasPlus")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_AVANZADO)
                            putBoolean("hasSeenInstructionsMasPlus", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeMasPlusPro")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRO)
                            putBoolean("hasSeenInstructionsMasPlus", hasSeenTutorial)
                        }
                    }
                }
            }
            "GenioPlus" -> {
                val myPrefs = getSharedPreferences("MyPrefsGenioPlus", MODE_PRIVATE)
                val hasSeenTutorial = myPrefs.getBoolean("hasSeenInstructionsGenioPlus", false)

                when (difficulty) {
                    DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> {
                        myPrefs.edit {
                            remove("selectedResponseModeGenioPlusPrincipiante")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)
                            putBoolean("hasSeenInstructionsGenioPlus", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_AVANZADO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeGenioPlus")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_AVANZADO)
                            putBoolean("hasSeenInstructionsGenioPlus", hasSeenTutorial)
                        }
                    }
                    DifficultySelectionActivity.DIFFICULTY_PRO -> {
                        myPrefs.edit {
                            remove("selectedResponseModeGenioPlusPro")
                            putString(getDifficultyKey(selectedGame!!),
                                DifficultySelectionActivity.DIFFICULTY_PRO)
                            putBoolean("hasSeenInstructionsGenioPlus", hasSeenTutorial)
                        }
                    }
                }
            }
        }
    }
}


