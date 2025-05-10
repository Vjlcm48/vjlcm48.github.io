package com.example.sumamente.ui

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.sumamente.R
import java.util.Locale

class DifficultySelectionActivity : AppCompatActivity() {

    companion object {

        const val DIFFICULTY_PRINCIPIANTE = "principiante"
        const val DIFFICULTY_AVANZADO = "avanzado"
        const val DIFFICULTY_PRO = "pro"

        const val EXTRA_GAME_TYPE = "game_type"

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
            else -> "MyPrefs"
        }

        sharedPreferences = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_difficulty_selection)

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
            else -> gameType
        }

        tvGameName.text = gameName
        tvGameTitle.text = getString(R.string.difficulty_selection_title)

        val closeButton = findViewById<ImageView>(R.id.closeButton)
        val btnPrincipiante = findViewById<RelativeLayout>(R.id.btn_principiante)
        val btnAvanzado = findViewById<RelativeLayout>(R.id.btn_avanzado)
        val btnPro = findViewById<RelativeLayout>(R.id.btn_pro)

        val isFromInstructions = intent.getBooleanExtra("FROM_INSTRUCTIONS", false)
        val level = if (isFromInstructions) intent.getIntExtra("LEVEL", 1) else 0
        val responseModeStr = intent.getStringExtra("RESPONSE_MODE")
        val currentDifficulty = sharedPreferences.getString(difficultyKey, DIFFICULTY_AVANZADO)

        btnAvanzado.isEnabled = true

        when (gameType) {
            "NumerosPlus", "DeciPlus", "Romas", "AlfaNumeros", "Sumaresta", "MasPlus" -> {
                btnPrincipiante.isEnabled = true
                btnPrincipiante.alpha = 1.0f
                btnPro.isEnabled = true
                btnPro.alpha = 1.0f
            }
            else -> {
                btnPrincipiante.isEnabled = true
                btnPrincipiante.alpha = 1.0f
                btnPro.isEnabled = false
                btnPro.alpha = 0.5f
            }
        }

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

    private fun navigateBasedOnSelection(difficulty: String, fromInstructions: Boolean, level: Int, responseMode: String?) {
        val targetIntent = when (gameType to difficulty) {
            "NumerosPlus" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivityPrincipiante::class.java)
            "NumerosPlus" to DIFFICULTY_AVANZADO -> Intent(this, LevelsActivity::class.java)
            "NumerosPlus" to DIFFICULTY_PRO -> Intent(this, LevelsActivityPro::class.java)

            "DeciPlus" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivityDeciPlusPrincipiante::class.java)
            "DeciPlus" to DIFFICULTY_AVANZADO -> Intent(this, LevelsActivityDeciPlus::class.java)
            "DeciPlus" to DIFFICULTY_PRO -> Intent(this, LevelsActivityDeciPlusPro::class.java)

            "Romas" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivityRomasPrincipiante::class.java)
            "Romas" to DIFFICULTY_AVANZADO -> Intent(this, LevelsActivityRomas::class.java)
            "Romas" to DIFFICULTY_PRO -> Intent(this, LevelsActivityRomasPro::class.java)

            "AlfaNumeros" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivityAlfaNumerosPrincipiante::class.java)
            "AlfaNumeros" to DIFFICULTY_AVANZADO -> Intent(this, LevelsActivityAlfaNumeros::class.java)
            "AlfaNumeros" to DIFFICULTY_PRO -> Intent(this, LevelsActivityAlfaNumerosPro::class.java)

            "Sumaresta" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivitySumaRestaPrincipiante::class.java)
            "Sumaresta" to DIFFICULTY_AVANZADO -> Intent(this, LevelsActivitySumaResta::class.java)
            "Sumaresta" to DIFFICULTY_PRO -> Intent(this, LevelsActivitySumaRestaPro::class.java)

            "MasPlus" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivityMasPlusPrincipiante::class.java)
            "MasPlus" to DIFFICULTY_AVANZADO -> Intent(this, LevelsActivityMasPlus::class.java)
            "MasPlus" to DIFFICULTY_PRO -> Intent(this, LevelsActivityMasPlusPro::class.java)

            "GenioPlus" to DIFFICULTY_PRINCIPIANTE -> Intent(this, LevelsActivityGenioPlusPrincipiante::class.java)
            "GenioPlus" to DIFFICULTY_AVANZADO -> Intent(this, LevelsActivityGenioPlus::class.java)
            "GenioPlus" to DIFFICULTY_PRO -> Intent(this, LevelsActivityGenioPlus::class.java)

            else -> {
                if (fromInstructions) {
                    Intent(this, InstructionsActivityGenioPlus::class.java).apply {
                        putExtra("LEVEL", level)
                        responseMode?.let { putExtra("RESPONSE_MODE", it) }
                    }
                } else {
                    Intent(this, GameSelectionActivity::class.java)
                }
            }
        }

        startActivity(targetIntent)
        finish()
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
                DIFFICULTY_PRO to ScoreManager::saveScoreGenioPlus
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
            val currentDifficulty = sharedPreferences.getString(difficultyKey, DIFFICULTY_AVANZADO)
            saveScoreForDifficulty(currentDifficulty ?: DIFFICULTY_AVANZADO)

            startActivity(Intent(this, GameSelectionActivity::class.java))
            finish()
        }
        builder.setNegativeButton(getString(R.string.difficulty_exit_dialog_negative), null)
        builder.show()
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
