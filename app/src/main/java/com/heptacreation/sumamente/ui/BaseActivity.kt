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

    protected fun checkAndUpdateBestGame(juego: String, grado: String, nivelCompletado: Int) {
        val totalLevels = ScoreManager.getTotalUniqueLevelsCompletedAllGames()

        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            if (totalLevels >= 13) {
                // Cancelar notificaciones
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(userId)
                    .update(mapOf(
                        "mindMissCounter" to 0,
                        "cancelMindMissNotifications" to true
                    ))
            } else if (totalLevels >= 5) {
                // Resetear contador si regresó
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(userId)
                    .update("mindMissCounter", 0)
            }
        }

        if (totalLevels < 5 || totalLevels >= 13) return

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val currentBestGame = prefs.getString("cached_best_game", "")
        val currentBestLevel = prefs.getInt("cached_best_level", 0)

        val fsGame = when (juego) {
            "NumerosPlus" -> "NUMEROS_PLUS"
            "DeciPlus" -> "DECI_PLUS"
            "Romas" -> "ROMAS"
            "AlfaNumeros" -> "ALFA_NUMEROS"
            "SumaResta" -> "SUMA_RESTA"
            "MasPlus" -> "MAS_PLUS"
            "GenioPlus" -> "GENIO_PLUS"
            else -> return
        }

        val fsDiff = when (grado) {
            "Avanzado" -> "AVANZADO"
            "Principiante" -> "PRINCIPIANTE"
            "Pro" -> "PRO"
            else -> return
        }

        val siguienteNivel = nivelCompletado + 1


        if (currentBestGame?.isEmpty() == true || currentBestLevel == 0) {
            data class Combo(val smJuego: String, val smGrado: String, val fsGame: String, val fsDiff: String)

            val combos: List<Combo> = listOf(
                Combo("AlfaNumeros","Avanzado","ALFA_NUMEROS","AVANZADO"),
                Combo("AlfaNumeros","Principiante","ALFA_NUMEROS","PRINCIPIANTE"),
                Combo("AlfaNumeros","Pro","ALFA_NUMEROS","PRO"),
                Combo("DeciPlus","Avanzado","DECI_PLUS","AVANZADO"),
                Combo("DeciPlus","Principiante","DECI_PLUS","PRINCIPIANTE"),
                Combo("DeciPlus","Pro","DECI_PLUS","PRO"),
                Combo("GenioPlus","Avanzado","GENIO_PLUS","AVANZADO"),
                Combo("GenioPlus","Principiante","GENIO_PLUS","PRINCIPIANTE"),
                Combo("GenioPlus","Pro","GENIO_PLUS","PRO"),
                Combo("MasPlus","Avanzado","MAS_PLUS","AVANZADO"),
                Combo("MasPlus","Principiante","MAS_PLUS","PRINCIPIANTE"),
                Combo("MasPlus","Pro","MAS_PLUS","PRO"),
                Combo("NumerosPlus","Avanzado","NUMEROS_PLUS","AVANZADO"),
                Combo("NumerosPlus","Principiante","NUMEROS_PLUS","PRINCIPIANTE"),
                Combo("NumerosPlus","Pro","NUMEROS_PLUS","PRO"),
                Combo("Romas","Avanzado","ROMAS","AVANZADO"),
                Combo("Romas","Principiante","ROMAS","PRINCIPIANTE"),
                Combo("Romas","Pro","ROMAS","PRO"),
                Combo("SumaResta","Avanzado","SUMA_RESTA","AVANZADO"),
                Combo("SumaResta","Principiante","SUMA_RESTA","PRINCIPIANTE"),
                Combo("SumaResta","Pro","SUMA_RESTA","PRO")
            )

            var bestFsGame = ""
            var bestFsDiff = ""
            var bestUnlocked = 0

            for (c in combos) {
                val maxCompleted = ScoreManager.getMaxLevelForCombo(c.smJuego, c.smGrado)
                if (maxCompleted <= 0) continue
                val unlocked = maxCompleted + 1

                if (unlocked > bestUnlocked) {
                    bestUnlocked = unlocked
                    bestFsGame = c.fsGame
                    bestFsDiff = c.fsDiff
                } else if (unlocked == bestUnlocked) {

                    val currentKey = "${bestFsGame}_${bestFsDiff}"
                    val newKey = "${c.fsGame}_${c.fsDiff}"
                    if (newKey < currentKey) {
                        bestFsGame = c.fsGame
                        bestFsDiff = c.fsDiff
                    }
                }
            }

            if (bestUnlocked > 0) {
                prefs.edit {
                    putString("cached_best_game", bestFsGame)
                    putString("cached_best_difficulty", bestFsDiff)
                    putInt("cached_best_level", bestUnlocked)
                }
                uploadBestGameToFirebase(bestFsGame, bestFsDiff, bestUnlocked)
            }
            return
        }

        if (siguienteNivel >= currentBestLevel) {
            prefs.edit {
                putString("cached_best_game", fsGame)
                putString("cached_best_difficulty", fsDiff)
                putInt("cached_best_level", siguienteNivel)
            }
            uploadBestGameToFirebase(fsGame, fsDiff, siguienteNivel)
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