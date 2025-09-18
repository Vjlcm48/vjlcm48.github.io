package com.heptacreation.sumamente.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale
import androidx.core.content.edit

abstract class BaseActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySelectedTheme()

    }

    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = getSavedLanguage(newBase)
        val updatedContext = updateContextWithLanguage(newBase, savedLanguage)
        super.attachBaseContext(updatedContext)
    }

    private fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        prefs.getString("selected_language", null)?.let { return it }
        val isFirstRun = prefs.getBoolean("is_first_run", true)
        if (isFirstRun) {

            val deviceLang = context.resources.configuration.locales[0]
                .language.lowercase(Locale.getDefault())
            val isSupported = LanguageManager.defaultLanguages.any {
                it.code.equals(deviceLang, ignoreCase = true)
            }

            if (isSupported) {
                return deviceLang
            }
        }
        return "en"
    }

    private fun updateContextWithLanguage(context: Context, languageCode: String): Context {
        val locale = Locale.Builder().setLanguage(languageCode).build()
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun applySelectedTheme() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        when (prefs.getString("selected_theme", "device")) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "device" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    protected fun checkAndUpdateBestGame() {

        val totalLevels = ScoreManager.getTotalUniqueLevelsCompletedAllGames()
        if (totalLevels < 5) return

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val currentMaxLevel = prefs.getInt("cached_best_level", 0)

        data class Combo(val smJuego: String, val smGrado: String, val fsGame: String, val fsDiff: String)


        val combos: List<Combo> = listOf(
            Combo("NumerosPlus","Principiante","NUMEROS_PLUS","PRINCIPIANTE"),
            Combo("NumerosPlus","Avanzado","NUMEROS_PLUS","AVANZADO"),
            Combo("NumerosPlus","Pro","NUMEROS_PLUS","PRO"),
            Combo("DeciPlus","Principiante","DECI_PLUS","PRINCIPIANTE"),
            Combo("DeciPlus","Avanzado","DECI_PLUS","AVANZADO"),
            Combo("DeciPlus","Pro","DECI_PLUS","PRO"),
            Combo("Romas","Principiante","ROMAS","PRINCIPIANTE"),
            Combo("Romas","Avanzado","ROMAS","AVANZADO"),
            Combo("Romas","Pro","ROMAS","PRO"),
            Combo("AlfaNumeros","Principiante","ALFA_NUMEROS","PRINCIPIANTE"),
            Combo("AlfaNumeros","Avanzado","ALFA_NUMEROS","AVANZADO"),
            Combo("AlfaNumeros","Pro","ALFA_NUMEROS","PRO"),
            Combo("SumaResta","Principiante","SUMA_RESTA","PRINCIPIANTE"),
            Combo("SumaResta","Avanzado","SUMA_RESTA","AVANZADO"),
            Combo("SumaResta","Pro","SUMA_RESTA","PRO"),
            Combo("MasPlus","Principiante","MAS_PLUS","PRINCIPIANTE"),
            Combo("MasPlus","Avanzado","MAS_PLUS","AVANZADO"),
            Combo("MasPlus","Pro","MAS_PLUS","PRO"),
            Combo("GenioPlus","Principiante","GENIO_PLUS","PRINCIPIANTE"),
            Combo("GenioPlus","Avanzado","GENIO_PLUS","AVANZADO"),
            Combo("GenioPlus","Pro","GENIO_PLUS","PRO")
        )

        var bestFsGame = ""
        var bestFsDiff = ""
        var bestUnlocked = 0

        for (c in combos) {
            val maxCompleted = ScoreManager.getMaxLevelForCombo(c.smJuego, c.smGrado)
            if (maxCompleted <= 0) continue
            val unlocked = maxCompleted + 1  // +1: nivel recién desbloqueado (tu regla)

            if (unlocked > bestUnlocked) {
                bestUnlocked = unlocked
                bestFsGame = c.fsGame
                bestFsDiff = c.fsDiff
            } else if (unlocked == bestUnlocked) {

                val currKey = "$bestFsGame#$bestFsDiff"
                val newKey = "${c.fsGame}#${c.fsDiff}"
                if (newKey > currKey) {
                    bestFsGame = c.fsGame
                    bestFsDiff = c.fsDiff
                }
            }
        }

        if (bestUnlocked == 0) return

        if (bestUnlocked >= currentMaxLevel) {
            prefs.edit {
                putString("cached_best_game", bestFsGame)
                    .putString("cached_best_difficulty", bestFsDiff)
                    .putInt("cached_best_level", bestUnlocked)
            }
            uploadBestGameToFirebase(bestFsGame, bestFsDiff, bestUnlocked)
        }
    }

    private fun uploadBestGameToFirebase(game: String, difficulty: String, level: Int) {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return

        val data = mapOf(
            "lastBestGame" to mapOf(
                "game" to game,
                "difficulty" to difficulty,
                "level" to level,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            ),
            "lastActive" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                android.util.Log.d("BestGame", "Mejor juego actualizado: $game $difficulty $level")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("BestGame", "Error actualizando mejor juego", e)
            }
    }


}