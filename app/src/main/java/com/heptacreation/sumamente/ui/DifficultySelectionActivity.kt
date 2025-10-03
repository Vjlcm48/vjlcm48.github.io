package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.edit
import com.heptacreation.sumamente.R
import java.util.Locale
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback


class DifficultySelectionActivity : BaseActivity()   {

    companion object {

        const val DIFFICULTY_PRINCIPIANTE = "principiante"
        const val DIFFICULTY_AVANZADO = "avanzado"
        const val DIFFICULTY_PRO = "pro"

        const val EXTRA_GAME_TYPE = "game_type"
        const val GAME_TYPE_FOCO_PLUS = "FocoPlus"

        fun createIntent(context: Context, gameType: String, isFromInstructions: Boolean = false,
                         level: Int = 0, responseMode: String? = null): Intent {
            return Intent(context, DifficultySelectionActivity::class.java).apply {
                putExtra(EXTRA_GAME_TYPE, gameType)
                putExtra("FROM_INSTRUCTIONS", isFromInstructions)
                if (level > 0) putExtra("LEVEL", level)
                if (responseMode != null) putExtra("RESPONSE_MODE", responseMode)
            }
        }
    }

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var gameType: String
    private lateinit var difficultyKey: String
    private lateinit var prefsName: String

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()


        gameType = intent.getStringExtra(EXTRA_GAME_TYPE) ?: "NumerosPlus"
        difficultyKey = "difficulty_${gameType.lowercase(Locale.getDefault())}"
        prefsName = when (gameType) {
            "NumerosPlus" -> "MyPrefs"
            "DeciPlus" -> "MyPrefsDeciPlus"
            "Romas" -> "MyPrefsRomas"
            "AlfaNumeros" -> "MyPrefsAlfaNumeros"
            "Sumaresta" -> "MyPrefsSumaResta"
            "MasPlus" -> "MyPrefsMasPlus"
            "GenioPlus" -> "MyPrefsGenioPlus"
            "MathPlus" -> "MyPrefsMathPlus"
            "FocoPlus" -> "MyPrefsFocoPlus"
            else -> "MyPrefs"
        }

        sharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
        setContentView(R.layout.activity_difficulty_selection)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })

        val tvGameName = findViewById<TextView>(R.id.tv_game_name)
        val tvGameTitle = findViewById<TextView>(R.id.tv_game_title)

        val gameName = when (gameType) {
            "NumerosPlus" -> getString(R.string.game_numeros_plus)
            "DeciPlus" -> getString(R.string.game_deci_plus)
            "Romas" -> getString(R.string.game_romas)
            "AlfaNumeros" -> getString(R.string.game_alfa_numeros)
            "Sumaresta" -> getString(R.string.game_sumaresta)
            "MasPlus" -> getString(R.string.game_mas_plus)
            "GenioPlus" -> getString(R.string.game_genio_plus)
            "MathPlus" -> getString(R.string.game_math_plus)
            "FocoPlus" -> getString(R.string.game_foco_plus)
            else -> gameType
        }

        tvGameName.text = gameName

        val tvGameSubtitle = findViewById<TextView>(R.id.tv_game_subtitle)

        if (Locale.getDefault().language != "es") {
            val subtitleResId = when (gameType) {
                "NumerosPlus" -> R.string.game_subtitle_numeros_plus
                "DeciPlus" -> R.string.game_subtitle_deci_plus
                "Romas" -> R.string.game_subtitle_romas
                "AlfaNumeros" -> R.string.game_subtitle_alfanumeros
                "Sumaresta" -> R.string.game_subtitle_sumaresta
                "MasPlus" -> R.string.game_subtitle_mas_plus
                "GenioPlus" -> R.string.game_subtitle_genio_plus
                "MathPlus" -> R.string.game_subtitle_math_plus
                "FocoPlus" -> R.string.game_subtitle_foco_plus
                else -> null
            }

            subtitleResId?.let {
                tvGameSubtitle.text = getString(it)
                tvGameSubtitle.visibility = View.VISIBLE
            }
        }

        tvGameTitle.text = getString(R.string.difficulty_selection_title)

        val closeButton = findViewById<ImageView>(R.id.closeButton)
        val btnPrincipiante = findViewById<RelativeLayout>(R.id.btn_principiante)
        val btnAvanzado = findViewById<RelativeLayout>(R.id.btn_avanzado)
        val btnPro = findViewById<RelativeLayout>(R.id.btn_pro)

        val isFromInstructions = intent.getBooleanExtra("FROM_INSTRUCTIONS", false)
        val level = if (isFromInstructions) intent.getIntExtra("LEVEL", 1) else 0
        val responseModeStr = intent.getStringExtra("RESPONSE_MODE")

        val currentDifficulty = sharedPreferences.getString(difficultyKey, null)

        btnAvanzado.isEnabled = true
        btnPrincipiante.isEnabled = true
        btnPrincipiante.alpha = 1.0f

        btnPro.isEnabled = true
        btnPro.alpha = 1.0f

        closeButton.setOnClickListener {
            applyBounceEffect(it) {
                showExitConfirmationDialog()
            }
        }

        btnPrincipiante.setOnClickListener {
            applyBounceEffect(it) {
                if (!btnPrincipiante.isEnabled) return@applyBounceEffect

                val isChangingDifficulty = currentDifficulty != DIFFICULTY_PRINCIPIANTE

                if (isChangingDifficulty) {
                    if (currentDifficulty != null) {
                        saveScoreForDifficulty(currentDifficulty)
                    }
                }

                saveDifficultyPreference(DIFFICULTY_PRINCIPIANTE)
                navigateBasedOnSelection(DIFFICULTY_PRINCIPIANTE, isFromInstructions, level, responseModeStr)
            }
        }

        btnAvanzado.setOnClickListener {
            applyBounceEffect(it) {
                val isChangingDifficulty = currentDifficulty != DIFFICULTY_AVANZADO

                if (isChangingDifficulty) {
                    if (currentDifficulty != null) {
                        saveScoreForDifficulty(currentDifficulty)
                    }
                }

                saveDifficultyPreference(DIFFICULTY_AVANZADO)
                navigateBasedOnSelection(DIFFICULTY_AVANZADO, isFromInstructions, level, responseModeStr)
            }
        }

        btnPro.setOnClickListener {
            applyBounceEffect(it) {
                if (!btnPro.isEnabled) return@applyBounceEffect

                val isChangingDifficulty = currentDifficulty != DIFFICULTY_PRO

                if (isChangingDifficulty) {
                    if (currentDifficulty != null) {
                        saveScoreForDifficulty(currentDifficulty)
                    }
                }

                saveDifficultyPreference(DIFFICULTY_PRO)
                navigateBasedOnSelection(DIFFICULTY_PRO, isFromInstructions, level, responseModeStr)
            }
        }
    }

    private fun navigateBasedOnSelection(
        difficulty: String,
        fromInstructions: Boolean,
        level: Int,
        responseMode: String?
    ) {

        if (gameType == "MathPlus") {
            when (difficulty) {
                DIFFICULTY_PRINCIPIANTE -> { return }
                DIFFICULTY_AVANZADO, DIFFICULTY_PRO -> { showComingSoonDialog(); return }
            }
        }

        if (gameType == GAME_TYPE_FOCO_PLUS) {
            when (difficulty) {
                DIFFICULTY_PRINCIPIANTE -> {
                    val intent = Intent(this, InstructionsLevelsActivityFocoPlus::class.java)
                    startActivity(intent)
                    finish()
                    return
                }
                DIFFICULTY_AVANZADO, DIFFICULTY_PRO -> {
                    showComingSoonDialogFoco()
                    return
                }
            }
        }

        fun Intent.withCommonExtras(): Intent = apply {
            putExtra("LEVEL", level)
            putExtra("DIFFICULTY", difficulty)
            responseMode?.let { putExtra("RESPONSE_MODE", it) }
        }

        if (fromInstructions) {
            val prefsName = getPrefsName(gameType)
            val key = getResponseModeKey(gameType, difficulty)

            val hasConfirmedMode = prefsName != null && key != null &&
                    getSharedPreferences(prefsName, MODE_PRIVATE).contains(key)

            if (hasConfirmedMode) {
                val instructionsIntent: Intent? = when (gameType to difficulty) {

                    "NumerosPlus" to DIFFICULTY_PRINCIPIANTE -> Intent(this, InstructionsActivityPrincipiante::class.java)
                    "NumerosPlus" to DIFFICULTY_AVANZADO    -> Intent(this, InstructionsActivity::class.java)
                    "NumerosPlus" to DIFFICULTY_PRO         -> Intent(this, InstructionsActivityPro::class.java)

                    "DeciPlus" to DIFFICULTY_PRINCIPIANTE   -> Intent(this, InstructionsActivityDeciPlusPrincipiante::class.java)
                    "DeciPlus" to DIFFICULTY_AVANZADO       -> Intent(this, InstructionsActivityDeciPlus::class.java)
                    "DeciPlus" to DIFFICULTY_PRO            -> Intent(this, InstructionsActivityDeciPlusPro::class.java)

                    "Romas" to DIFFICULTY_PRINCIPIANTE      -> Intent(this, InstructionsActivityRomasPrincipiante::class.java)
                    "Romas" to DIFFICULTY_AVANZADO          -> Intent(this, InstructionsActivityRomas::class.java)
                    "Romas" to DIFFICULTY_PRO               -> Intent(this, InstructionsActivityRomasPro::class.java)

                    "AlfaNumeros" to DIFFICULTY_PRINCIPIANTE -> Intent(this, InstructionsActivityAlfaNumerosPrincipiante::class.java)
                    "AlfaNumeros" to DIFFICULTY_AVANZADO     -> Intent(this, InstructionsActivityAlfaNumeros::class.java)
                    "AlfaNumeros" to DIFFICULTY_PRO          -> Intent(this, InstructionsActivityAlfaNumerosPro::class.java)

                    "Sumaresta" to DIFFICULTY_PRINCIPIANTE   -> Intent(this, InstructionsActivitySumaRestaPrincipiante::class.java)
                    "Sumaresta" to DIFFICULTY_AVANZADO       -> Intent(this, InstructionsActivitySumaResta::class.java)
                    "Sumaresta" to DIFFICULTY_PRO            -> Intent(this, InstructionsActivitySumaRestaPro::class.java)

                    "MasPlus" to DIFFICULTY_PRINCIPIANTE     -> Intent(this, InstructionsActivityMasPlusPrincipiante::class.java)
                    "MasPlus" to DIFFICULTY_AVANZADO         -> Intent(this, InstructionsActivityMasPlus::class.java)
                    "MasPlus" to DIFFICULTY_PRO              -> Intent(this, InstructionsActivityMasPlusPro::class.java)

                    "GenioPlus" to DIFFICULTY_PRINCIPIANTE   -> Intent(this, InstructionsActivityGenioPlusPrincipiante::class.java)
                    "GenioPlus" to DIFFICULTY_AVANZADO       -> Intent(this, InstructionsActivityGenioPlus::class.java)
                    "GenioPlus" to DIFFICULTY_PRO            -> Intent(this, InstructionsActivityGenioPlusPro::class.java)

                    else -> null
                }

                instructionsIntent?.withCommonExtras()?.let {
                    startActivity(it)
                    finish()
                    return
                }

            }

        }

        val targetIntent: Intent? = when (gameType to difficulty) {

            "NumerosPlus" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivityPrincipiante::class.java)
            "NumerosPlus" to DIFFICULTY_AVANZADO    -> Intent(this, LevelsActivity::class.java)
            "NumerosPlus" to DIFFICULTY_PRO         -> Intent(this, LevelsActivityPro::class.java)

            "DeciPlus" to DIFFICULTY_PRINCIPIANTE   -> Intent(this, LevelsActivityDeciPlusPrincipiante::class.java)
            "DeciPlus" to DIFFICULTY_AVANZADO       -> Intent(this, LevelsActivityDeciPlus::class.java)
            "DeciPlus" to DIFFICULTY_PRO            -> Intent(this, LevelsActivityDeciPlusPro::class.java)

            "Romas" to DIFFICULTY_PRINCIPIANTE      -> Intent(this, LevelsActivityRomasPrincipiante::class.java)
            "Romas" to DIFFICULTY_AVANZADO          -> Intent(this, LevelsActivityRomas::class.java)
            "Romas" to DIFFICULTY_PRO               -> Intent(this, LevelsActivityRomasPro::class.java)

            "AlfaNumeros" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivityAlfaNumerosPrincipiante::class.java)
            "AlfaNumeros" to DIFFICULTY_AVANZADO     -> Intent(this, LevelsActivityAlfaNumeros::class.java)
            "AlfaNumeros" to DIFFICULTY_PRO          -> Intent(this, LevelsActivityAlfaNumerosPro::class.java)

            "Sumaresta" to DIFFICULTY_PRINCIPIANTE   -> Intent(this, LevelsActivitySumaRestaPrincipiante::class.java)
            "Sumaresta" to DIFFICULTY_AVANZADO       -> Intent(this, LevelsActivitySumaResta::class.java)
            "Sumaresta" to DIFFICULTY_PRO            -> Intent(this, LevelsActivitySumaRestaPro::class.java)

            "MasPlus" to DIFFICULTY_PRINCIPIANTE     -> Intent(this, LevelsActivityMasPlusPrincipiante::class.java)
            "MasPlus" to DIFFICULTY_AVANZADO         -> Intent(this, LevelsActivityMasPlus::class.java)
            "MasPlus" to DIFFICULTY_PRO              -> Intent(this, LevelsActivityMasPlusPro::class.java)

            "GenioPlus" to DIFFICULTY_PRINCIPIANTE   -> Intent(this, LevelsActivityGenioPlusPrincipiante::class.java)
            "GenioPlus" to DIFFICULTY_AVANZADO       -> Intent(this, LevelsActivityGenioPlus::class.java)
            "GenioPlus" to DIFFICULTY_PRO            -> Intent(this, LevelsActivityGenioPlusPro::class.java)

            else -> null
        }

        targetIntent?.let {
            startActivity(it)
            finish()
        }

    }

    private fun getPrefsName(gameType: String): String? = when (gameType) {
        "NumerosPlus" -> "MyPrefs"
        "DeciPlus"    -> "MyPrefsDeciPlus"
        "Romas"       -> "MyPrefsRomas"
        "AlfaNumeros" -> "MyPrefsAlfaNumeros"
        "Sumaresta"   -> "MyPrefsSumaResta"
        "MasPlus"     -> "MyPrefsMasPlus"
        "GenioPlus"   -> "MyPrefsGenioPlus"
        "FocoPlus"    -> "MyPrefsFocoPlus"
        else -> null
    }

    private fun getResponseModeKey(gameType: String, difficulty: String): String? = when (gameType to difficulty) {

        "NumerosPlus" to DIFFICULTY_PRINCIPIANTE -> "selectedResponseModePrincipiante"
        "NumerosPlus" to DIFFICULTY_AVANZADO    -> "selectedResponseMode"
        "NumerosPlus" to DIFFICULTY_PRO         -> "selectedResponseModePro"

        "DeciPlus" to DIFFICULTY_PRINCIPIANTE   -> "selectedResponseModeDialogDeciPlusPrincipiante"
        "DeciPlus" to DIFFICULTY_AVANZADO       -> "selectedResponseModeDialogDeciPlus"
        "DeciPlus" to DIFFICULTY_PRO            -> "selectedResponseModeDialogDeciPlusPro"

        "Romas" to DIFFICULTY_PRINCIPIANTE      -> "selectedResponseModeRomasPrincipiante"
        "Romas" to DIFFICULTY_AVANZADO          -> "selectedResponseModeRomas"
        "Romas" to DIFFICULTY_PRO               -> "selectedResponseModeRomasPro"

        "AlfaNumeros" to DIFFICULTY_PRINCIPIANTE -> "selectedResponseModeAlfaNumerosPrincipiante"
        "AlfaNumeros" to DIFFICULTY_AVANZADO     -> "selectedResponseModeAlfaNumeros"
        "AlfaNumeros" to DIFFICULTY_PRO          -> "selectedResponseModeAlfaNumerosPro"

        "Sumaresta" to DIFFICULTY_PRINCIPIANTE   -> "selectedResponseModeSumaRestaPrincipiante"
        "Sumaresta" to DIFFICULTY_AVANZADO       -> "selectedResponseModeSumaResta"
        "Sumaresta" to DIFFICULTY_PRO            -> "selectedResponseModeSumaRestaPro"

        "MasPlus" to DIFFICULTY_PRINCIPIANTE     -> "selectedResponseModeMasPlusPrincipiante"
        "MasPlus" to DIFFICULTY_AVANZADO         -> "selectedResponseModeMasPlus"
        "MasPlus" to DIFFICULTY_PRO              -> "selectedResponseModeMasPlusPro"

        "GenioPlus" to DIFFICULTY_PRINCIPIANTE   -> "selectedResponseModeGenioPlusPrincipiante"
        "GenioPlus" to DIFFICULTY_AVANZADO       -> "selectedResponseModeGenioPlus"
        "GenioPlus" to DIFFICULTY_PRO            -> "selectedResponseModeGenioPlusPro"

        else -> null
    }

    private fun showComingSoonDialogFoco() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_foco_plus_info, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<ImageView>(R.id.btn_cerrar_dialog).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_entendido).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun saveScoreForDifficulty(difficulty: String) {
        val saveActions = mapOf(
            "NumerosPlus" to mapOf(
                DIFFICULTY_PRINCIPIANTE to ScoreManager::saveScorePrincipiante,
                DIFFICULTY_AVANZADO to ScoreManager::saveScore,
                DIFFICULTY_PRO to ScoreManager::saveScorePro
            ),
            "DeciPlus" to mapOf(
                DIFFICULTY_PRINCIPIANTE to ScoreManager::saveScoreDeciPlusPrincipiante,
                DIFFICULTY_AVANZADO to ScoreManager::saveScoreDeciPlus,
                DIFFICULTY_PRO to ScoreManager::saveScoreDeciPlusPro
            ),
            "Romas" to mapOf(
                DIFFICULTY_PRINCIPIANTE to ScoreManager::saveScoreRomasPrincipiante,
                DIFFICULTY_AVANZADO to ScoreManager::saveScoreRomas,
                DIFFICULTY_PRO to ScoreManager::saveScoreRomasPro
            ),
            "AlfaNumeros" to mapOf(
                DIFFICULTY_PRINCIPIANTE to ScoreManager::saveScoreAlfaNumerosPrincipiante,
                DIFFICULTY_AVANZADO to ScoreManager::saveScoreAlfaNumeros,
                DIFFICULTY_PRO to ScoreManager::saveScoreAlfaNumerosPro
            ),
            "Sumaresta" to mapOf(
                DIFFICULTY_PRINCIPIANTE to ScoreManager::saveScoreSumaRestaPrincipiante,
                DIFFICULTY_AVANZADO to ScoreManager::saveScoreSumaResta,
                DIFFICULTY_PRO to ScoreManager::saveScoreSumaRestaPro
            ),
            "MasPlus" to mapOf(
                DIFFICULTY_PRINCIPIANTE to ScoreManager::saveScoreMasPlusPrincipiante,
                DIFFICULTY_AVANZADO to ScoreManager::saveScoreMasPlus,
                DIFFICULTY_PRO to ScoreManager::saveScoreMasPlusPro
            ),
            "GenioPlus" to mapOf(
                DIFFICULTY_PRINCIPIANTE to ScoreManager::saveScoreGenioPlusPrincipiante,
                DIFFICULTY_AVANZADO to ScoreManager::saveScoreGenioPlus,
                DIFFICULTY_PRO to ScoreManager::saveScoreGenioPlusPro
            )
        )

        saveActions[gameType]?.get(difficulty)?.invoke()
    }

    private fun saveDifficultyPreference(difficulty: String) {
        sharedPreferences.edit {
            putString(difficultyKey, difficulty)
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.difficulty_exit_dialog_title))
        builder.setMessage(getString(R.string.difficulty_exit_dialog_message))
        builder.setPositiveButton(getString(R.string.difficulty_exit_dialog_positive)) { _, _ ->
            val currentDifficulty = sharedPreferences.getString(difficultyKey, null)

            if (!currentDifficulty.isNullOrEmpty()) {
                saveScoreForDifficulty(currentDifficulty)
            }

            startActivity(Intent(this, GameSelectionActivity::class.java))
            finish()
        }
        builder.setNegativeButton(getString(R.string.difficulty_exit_dialog_negative), null)
        builder.show()
    }

    private fun showComingSoonDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_math_plus_coming_soon, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<ImageView>(R.id.btn_cerrar_dialog).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_entendido).setOnClickListener { dialog.dismiss() }

        dialog.show()
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
}
