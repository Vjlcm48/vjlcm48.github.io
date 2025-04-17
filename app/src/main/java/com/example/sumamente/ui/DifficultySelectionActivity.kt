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

        when (gameType) {
            "NumerosPlus", "DeciPlus", "Romas", "AlfaNumeros" -> {
                btnPrincipiante.isEnabled = true
                btnPrincipiante.alpha = 1.0f
                btnAvanzado.isEnabled = true
                btnPro.isEnabled = gameType != "AlfaNumeros"
                btnPro.alpha = if (gameType != "AlfaNumeros") 1.0f else 0.5f
            }
            else -> {
                btnPrincipiante.isEnabled = false
                btnPrincipiante.alpha = 0.5f
                btnAvanzado.isEnabled = true
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
        val targetIntent = when {

            gameType == "NumerosPlus" && difficulty == DIFFICULTY_PRINCIPIANTE -> {
                Intent(this, LevelsActivityPrincipiante::class.java)
            }
            gameType == "NumerosPlus" && difficulty == DIFFICULTY_AVANZADO -> {
                Intent(this, LevelsActivity::class.java)
            }
            gameType == "NumerosPlus" && difficulty == DIFFICULTY_PRO -> {
                Intent(this, LevelsActivityPro::class.java)
            }

            gameType == "DeciPlus" && difficulty == DIFFICULTY_AVANZADO -> {
                Intent(this, LevelsActivityDeciPlus::class.java)
            }
            gameType == "DeciPlus" && difficulty == DIFFICULTY_PRINCIPIANTE -> {
                Intent(this, LevelsActivityDeciPlusPrincipiante::class.java)
            }
            gameType == "DeciPlus" && difficulty == DIFFICULTY_PRO -> {
                Intent(this, LevelsActivityDeciPlusPro::class.java)
            }

            gameType == "Romas" && difficulty == DIFFICULTY_PRINCIPIANTE -> {
                Intent(this, LevelsActivityRomasPrincipiante::class.java)
            }
            gameType == "Romas" && difficulty == DIFFICULTY_AVANZADO -> {
                Intent(this, LevelsActivityRomas::class.java)
            }
            gameType == "Romas" && difficulty == DIFFICULTY_PRO -> {
                Intent(this, LevelsActivityRomasPro::class.java)
            }

            gameType == "AlfaNumeros" && difficulty == DIFFICULTY_PRINCIPIANTE -> {
                Intent(this, LevelsActivityAlfaNumerosPrincipiante::class.java)
            }
            gameType == "AlfaNumeros" && difficulty == DIFFICULTY_AVANZADO -> {
                Intent(this, LevelsActivityAlfaNumeros::class.java)
            }
            gameType == "AlfaNumeros" && difficulty == DIFFICULTY_PRO -> {
                Intent(this, LevelsActivityAlfaNumeros::class.java)
            }

            gameType == "Sumaresta" -> {
                if (fromInstructions) {
                    Intent(this, InstructionsActivitySumaResta::class.java).apply {
                        putExtra("LEVEL", level)
                        if (responseMode != null) putExtra("RESPONSE_MODE", responseMode)
                    }
                } else {
                    Intent(this, LevelsActivitySumaResta::class.java)
                }
            }

            gameType == "MasPlus" -> {
                if (fromInstructions) {
                    Intent(this, InstructionsActivityMasPlus::class.java).apply {
                        putExtra("LEVEL", level)
                        if (responseMode != null) putExtra("RESPONSE_MODE", responseMode)
                    }
                } else {
                    Intent(this, LevelsActivityMasPlus::class.java)
                }
            }

            gameType == "GenioPlus" -> {
                if (fromInstructions) {
                    Intent(this, InstructionsActivityGenioPlus::class.java).apply {
                        putExtra("LEVEL", level)
                        if (responseMode != null) putExtra("RESPONSE_MODE", responseMode)
                    }
                } else {
                    Intent(this, LevelsActivityGenioPlus::class.java)
                }
            }

            else -> {
                Intent(this, GameSelectionActivity::class.java)
            }
        }

        startActivity(targetIntent)
        finish()
    }

    private fun saveScoreForDifficulty(difficulty: String) {
        when (gameType) {
            "NumerosPlus" -> {
                when (difficulty) {
                    DIFFICULTY_PRINCIPIANTE -> ScoreManager.saveScorePrincipiante()
                    DIFFICULTY_AVANZADO -> ScoreManager.saveScore()
                    DIFFICULTY_PRO -> ScoreManager.saveScorePro()
                }
            }
            "DeciPlus" -> {
                when (difficulty) {
                    DIFFICULTY_PRINCIPIANTE -> ScoreManager.saveScoreDeciPlusPrincipiante()
                    DIFFICULTY_AVANZADO -> ScoreManager.saveScoreDeciPlus()
                    DIFFICULTY_PRO -> ScoreManager.saveScoreDeciPlusPro()
                }
            }
            "Romas" -> {
                when (difficulty) {
                    DIFFICULTY_PRINCIPIANTE -> ScoreManager.saveScoreRomasPrincipiante()
                    DIFFICULTY_AVANZADO -> ScoreManager.saveScoreRomas()
                    DIFFICULTY_PRO -> ScoreManager.saveScoreRomas()
                }
            }
            "AlfaNumeros" -> {
                when (difficulty) {
                    DIFFICULTY_PRINCIPIANTE -> ScoreManager.saveScoreAlfaNumerosPrincipiante()
                    DIFFICULTY_AVANZADO -> ScoreManager.saveScoreAlfaNumeros()
                    DIFFICULTY_PRO -> ScoreManager.saveScoreAlfaNumeros()
                }
            }
            "Sumaresta" -> ScoreManager.saveScoreSumaResta()
            "MasPlus" -> ScoreManager.saveScoreMasPlus()
            "GenioPlus" -> ScoreManager.saveScoreGenioPlus()
        }
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
