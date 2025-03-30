package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sumamente.R

class GameSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        ScoreManager.initSumaResta(this)
        ScoreManager.initMasPlus(this)
        ScoreManager.initGenioPlus(this)

        setContentView(R.layout.activity_game_selection)

        val btnNumerosPlus = findViewById<RelativeLayout>(R.id.btn_numeros_plus)
        val btnDeciPlus = findViewById<RelativeLayout>(R.id.btn_deci_plus)
        val btnRomas = findViewById<RelativeLayout>(R.id.btn_romas)
        val btnAlfaNumeros = findViewById<RelativeLayout>(R.id.btn_alfa_numeros)
        val btnSumaresta = findViewById<RelativeLayout>(R.id.btn_sumaresta)
        val btnMasPlus = findViewById<RelativeLayout>(R.id.btn_mas_plus)
        val btnGenioPlus = findViewById<RelativeLayout>(R.id.btn_genio_plus)
        val closeButton = findViewById<ImageView>(R.id.closeButton)

        val puntosNumerosPlus = ScoreManager.currentScore
        val puntosPrincipiante = ScoreManager.currentScorePrincipiante
        val puntosPro = ScoreManager.currentScorePro
        val puntosDeciPlus = ScoreManager.currentScoreDeciPlus
        val puntosDeciPlusPrincipiante = ScoreManager.currentScoreDeciPlusPrincipiante
        val puntosDeciPlusPro = ScoreManager.currentScoreDeciPlusPro
        val puntosRomas = ScoreManager.currentScoreRomas
        val puntosRomasPrincipiante = ScoreManager.currentScoreRomasPrincipiante
        val puntosRomasPro = ScoreManager.currentScoreRomasPro
        val puntosAlfaNumeros = ScoreManager.currentScoreAlfaNumeros
        val puntosSumaResta = ScoreManager.currentScoreSumaResta
        val puntosMasPlus = ScoreManager.currentScoreMasPlus
        val puntosGenioPlus = ScoreManager.currentScoreGenioPlus
        val tvGameNameNumerosPlus = btnNumerosPlus.findViewById<TextView>(R.id.tv_game_name_numeros_plus)
        val tvGameNameDeciPlus = btnDeciPlus.findViewById<TextView>(R.id.tv_game_name_deci_plus)
        val tvGameNameRomas = btnRomas.findViewById<TextView>(R.id.tv_game_name_romas)

        tvGameNameNumerosPlus.text = getString(R.string.game_numeros_plus)
        tvGameNameDeciPlus.text = getString(R.string.game_deci_plus)
        tvGameNameRomas.text = getString(R.string.game_romas)

        applyAlfaNumerosColor(btnAlfaNumeros)
        applySumarestaColor(btnSumaresta)
        applyMasPlusColor(btnMasPlus)
        applyGenioPlusColor(btnGenioPlus)

        updateNumerosPlusButton(btnNumerosPlus, puntosNumerosPlus, puntosPrincipiante, puntosPro)
        updateDeciPlusButton(btnDeciPlus, puntosDeciPlus, puntosDeciPlusPrincipiante, puntosDeciPlusPro)
        updateRomasButton(btnRomas, puntosRomas, puntosRomasPrincipiante, puntosRomasPro)
        updateAlfaNumerosButton(btnAlfaNumeros, puntosAlfaNumeros)
        updateSumaRestaButton(btnSumaresta, puntosSumaResta)
        updateMasPlusButton(btnMasPlus, puntosMasPlus)
        updateGenioPlusButton(btnGenioPlus, puntosGenioPlus)

        btnNumerosPlus.setOnClickListener {
            applyBounceEffect(it) {
                val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val hasSeenInstructions = prefs.getBoolean("hasSeenInstructionsNumeros", false)

                if (hasSeenInstructions) {
                    val difficultyKey = "difficulty_numerosplus"
                    val hasDifficulty = prefs.contains(difficultyKey)

                    if (hasDifficulty) {

                        val difficulty = prefs.getString(difficultyKey,
                            DifficultySelectionActivity.DIFFICULTY_AVANZADO)

                        val intent = when (difficulty) {
                            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE ->
                                Intent(this, LevelsActivityPrincipiante::class.java)
                            DifficultySelectionActivity.DIFFICULTY_PRO ->
                                Intent(this, LevelsActivityPro::class.java)
                            else -> Intent(this, LevelsActivity::class.java)
                        }
                        startActivity(intent)
                    } else {
                        startActivity(DifficultySelectionActivity.createIntent(this, "NumerosPlus"))
                    }
                } else {
                    startActivity(Intent(this, TutorialActivityNumeros::class.java))
                }
            }
        }

        btnDeciPlus.setOnClickListener {
            applyBounceEffect(it) {
                val prefs = getSharedPreferences("MyPrefsDeciPlus", Context.MODE_PRIVATE)
                val hasSeenInstructions = prefs.getBoolean("hasSeenInstructionsDeciPlus", false)

                if (hasSeenInstructions) {
                    val difficultyKey = "difficulty_deciplus"
                    val hasDifficulty = prefs.contains(difficultyKey)

                    if (hasDifficulty) {
                        val difficulty = prefs.getString(difficultyKey,
                            DifficultySelectionActivity.DIFFICULTY_AVANZADO)

                        val intent = when (difficulty) {
                            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE ->
                                Intent(this, LevelsActivityDeciPlusPrincipiante::class.java)
                            DifficultySelectionActivity.DIFFICULTY_PRO ->
                                Intent(this, LevelsActivityDeciPlusPro::class.java)
                            else -> Intent(this, LevelsActivityDeciPlus::class.java)
                        }
                        startActivity(intent)
                    } else {
                        startActivity(DifficultySelectionActivity.createIntent(this, "DeciPlus"))
                    }
                } else {
                    startActivity(Intent(this, TutorialActivityDeciPlus::class.java))
                }
            }
        }

        btnRomas.setOnClickListener {
            applyBounceEffect(it) {
                val prefs = getSharedPreferences("MyPrefsRomas", Context.MODE_PRIVATE)
                val hasSeenInstructions = prefs.getBoolean("hasSeenInstructionsRomas", false)

                if (hasSeenInstructions) {
                    val difficultyKey = "difficulty_romas"
                    val hasDifficulty = prefs.contains(difficultyKey)

                    if (hasDifficulty) {
                        val difficulty = prefs.getString(difficultyKey,
                            DifficultySelectionActivity.DIFFICULTY_AVANZADO)

                        val intent = when (difficulty) {
                            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE ->
                                Intent(this, LevelsActivityRomasPrincipiante::class.java)
                            DifficultySelectionActivity.DIFFICULTY_PRO ->
                                Intent(this, LevelsActivityRomasPro::class.java)
                            else -> Intent(this, LevelsActivityRomas::class.java)
                        }
                        startActivity(intent)
                    } else {
                        startActivity(DifficultySelectionActivity.createIntent(this, "Romas"))
                    }
                } else {
                    startActivity(Intent(this, TutorialActivityRomas::class.java))
                }
            }
        }

        btnAlfaNumeros.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, TutorialActivityAlfaNumeros::class.java))
            }
        }

        btnSumaresta.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, TutorialActivitySumaResta::class.java))
            }
        }

        btnMasPlus.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, TutorialActivityMasPlus::class.java))
            }
        }

        btnGenioPlus.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, TutorialActivityGenioPlus::class.java))
            }
        }

        closeButton.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, MainGameActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()

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
        ScoreManager.initSumaResta(this)
        ScoreManager.initMasPlus(this)
        ScoreManager.initGenioPlus(this)

        val puntosNumerosPlus = ScoreManager.currentScore
        val puntosPrincipiante = ScoreManager.currentScorePrincipiante
        val puntosPro = ScoreManager.currentScorePro
        val puntosDeciPlus = ScoreManager.currentScoreDeciPlus
        val puntosDeciPlusPrincipiante = ScoreManager.currentScoreDeciPlusPrincipiante
        val puntosDeciPlusPro = ScoreManager.currentScoreDeciPlusPro
        val puntosRomas = ScoreManager.currentScoreRomas
        val puntosRomasPrincipiante = ScoreManager.currentScoreRomasPrincipiante
        val puntosRomasPro = ScoreManager.currentScoreRomasPro
        val puntosAlfaNumeros = ScoreManager.currentScoreAlfaNumeros
        val puntosSumaResta = ScoreManager.currentScoreSumaResta
        val puntosMasPlus = ScoreManager.currentScoreMasPlus
        val puntosGenioPlus = ScoreManager.currentScoreGenioPlus

        val btnNumerosPlus = findViewById<RelativeLayout>(R.id.btn_numeros_plus)
        val btnDeciPlus = findViewById<RelativeLayout>(R.id.btn_deci_plus)
        val btnRomas = findViewById<RelativeLayout>(R.id.btn_romas)
        val btnAlfaNumeros = findViewById<RelativeLayout>(R.id.btn_alfa_numeros)
        val btnSumaresta = findViewById<RelativeLayout>(R.id.btn_sumaresta)
        val btnMasPlus = findViewById<RelativeLayout>(R.id.btn_mas_plus)
        val btnGenioPlus = findViewById<RelativeLayout>(R.id.btn_genio_plus)

        applyAlfaNumerosColor(btnAlfaNumeros)
        applySumarestaColor(btnSumaresta)
        applyMasPlusColor(btnMasPlus)
        applyGenioPlusColor(btnGenioPlus)

        updateNumerosPlusButton(btnNumerosPlus, puntosNumerosPlus, puntosPrincipiante, puntosPro)
        updateDeciPlusButton(btnDeciPlus, puntosDeciPlus, puntosDeciPlusPrincipiante, puntosDeciPlusPro)
        updateRomasButton(btnRomas, puntosRomas, puntosRomasPrincipiante, puntosRomasPro)
        updateAlfaNumerosButton(btnAlfaNumeros, puntosAlfaNumeros)
        updateSumaRestaButton(btnSumaresta, puntosSumaResta)
        updateMasPlusButton(btnMasPlus, puntosMasPlus)
        updateGenioPlusButton(btnGenioPlus, puntosGenioPlus)
    }

    private fun updateNumerosPlusButton(button: RelativeLayout, score: Int, principianteScore: Int, proScore: Int) {
        val gameNameTextView = button.findViewById<TextView>(R.id.tv_game_name_numeros_plus)
        val pointsTextView = button.findViewById<TextView>(R.id.tv_points_numeros_plus)
        val starIcon = button.findViewById<ImageView>(R.id.icon_star_numeros_plus)
        val totalScore = score + principianteScore + proScore

        if (totalScore > 0) {
            gameNameTextView.text = getString(R.string.game_numeros_plus)
            pointsTextView.text = getString(R.string.score_format, totalScore)
            pointsTextView.visibility = View.VISIBLE
            starIcon.visibility = View.VISIBLE
        } else {
            gameNameTextView.text = getString(R.string.game_numeros_plus)
            pointsTextView.visibility = View.GONE
            starIcon.visibility = View.GONE
        }
    }

    private fun updateDeciPlusButton(button: RelativeLayout, score: Int, principianteScore: Int, proScore: Int) {
        val gameNameTextView = button.findViewById<TextView>(R.id.tv_game_name_deci_plus)
        val pointsTextView = button.findViewById<TextView>(R.id.tv_points_deci_plus)
        val starIcon = button.findViewById<ImageView>(R.id.icon_star_deci_plus)
        val totalScore = score + principianteScore + proScore

        if (totalScore > 0) {
            gameNameTextView.text = getString(R.string.game_deci_plus)
            pointsTextView.text = getString(R.string.score_format, totalScore)
            pointsTextView.visibility = View.VISIBLE
            starIcon.visibility = View.VISIBLE
        } else {
            gameNameTextView.text = getString(R.string.game_deci_plus)
            pointsTextView.visibility = View.GONE
            starIcon.visibility = View.GONE
        }
    }

    private fun updateRomasButton(button: RelativeLayout, score: Int, principianteScore: Int, proScore: Int) {
        val gameNameTextView = button.findViewById<TextView>(R.id.tv_game_name_romas)
        val pointsTextView = button.findViewById<TextView>(R.id.tv_points_romas)
        val starIcon = button.findViewById<ImageView>(R.id.icon_star_romas)
        val totalScore = score + principianteScore + proScore

        if (totalScore > 0) {
            gameNameTextView.text = getString(R.string.game_romas)
            pointsTextView.text = getString(R.string.score_format, totalScore)
            pointsTextView.visibility = View.VISIBLE
            starIcon.visibility = View.VISIBLE
        } else {
            gameNameTextView.text = getString(R.string.game_romas)
            pointsTextView.visibility = View.GONE
            starIcon.visibility = View.GONE
        }
    }

    private fun updateAlfaNumerosButton(button: RelativeLayout, score: Int) {

        val pointsTextView = button.findViewById<TextView>(R.id.tv_points_alfa_numeros)
        val starIcon = button.findViewById<ImageView>(R.id.icon_star_alfa_numeros)

        if (score > 0) {
            pointsTextView.text = getString(R.string.score_format, score)
            pointsTextView.visibility = View.VISIBLE
            starIcon.visibility = View.VISIBLE
        } else {
            pointsTextView.visibility = View.GONE
            starIcon.visibility = View.GONE
        }
    }

    private fun updateSumaRestaButton(button: RelativeLayout, score: Int) {

        val pointsTextView = button.findViewById<TextView>(R.id.tv_points_sumaresta)
        val starIcon = button.findViewById<ImageView>(R.id.icon_star_sumaresta)

        if (score > 0) {
            pointsTextView.text = getString(R.string.score_format, score)
            pointsTextView.visibility = View.VISIBLE
            starIcon.visibility = View.VISIBLE
        } else {
            pointsTextView.visibility = View.GONE
            starIcon.visibility = View.GONE
        }
    }

    private fun updateMasPlusButton(button: RelativeLayout, score: Int) {

        val pointsTextView = button.findViewById<TextView>(R.id.tv_points_mas_plus)
        val starIcon = button.findViewById<ImageView>(R.id.icon_star_mas_plus)

        if (score > 0) {
            pointsTextView.text = getString(R.string.score_format, score)
            pointsTextView.visibility = View.VISIBLE
            starIcon.visibility = View.VISIBLE
        } else {
            pointsTextView.visibility = View.GONE
            starIcon.visibility = View.GONE
        }
    }

    private fun updateGenioPlusButton(button: RelativeLayout, score: Int) {

        val pointsTextView = button.findViewById<TextView>(R.id.tv_points_genio_plus)
        val starIcon = button.findViewById<ImageView>(R.id.icon_star_genio_plus)

        if (score > 0) {
            pointsTextView.text = getString(R.string.score_format, score)
            pointsTextView.visibility = View.VISIBLE
            starIcon.visibility = View.VISIBLE
        } else {
            pointsTextView.visibility = View.GONE
            starIcon.visibility = View.GONE
        }
    }

    private fun applyAlfaNumerosColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_alfa_numeros)

        val alfaText = getString(R.string.text_alfa)
        val numerosText = getString(R.string.text_numeros)
        val alfaNumerosText = "$alfaText$numerosText"
        val spannableAlfaNumeros = SpannableString(alfaNumerosText)

        spannableAlfaNumeros.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)),
            0, alfaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableAlfaNumeros.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)),
            alfaText.length, alfaNumerosText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableAlfaNumeros
    }

    private fun applySumarestaColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_sumaresta)

        val sumaText = getString(R.string.text_suma)
        val restaText = getString(R.string.text_resta)
        val sumarestaText = "$sumaText$restaText"
        val spannableSumaresta = SpannableString(sumarestaText)

        spannableSumaresta.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)),
            0, sumaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableSumaresta.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
            sumaText.length, sumarestaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableSumaresta
    }

    private fun applyMasPlusColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_mas_plus)
        textView.setTextColor(ContextCompat.getColor(this, R.color.grey_light))
    }

    private fun applyGenioPlusColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_genio_plus)
        textView.setTextColor(ContextCompat.getColor(this, R.color.blue_pressed))
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
