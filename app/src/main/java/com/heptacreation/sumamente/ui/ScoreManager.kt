package com.heptacreation.sumamente.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken


object ScoreManager {

    private val gson = Gson()
    private val mapType = object : TypeToken<MutableMap<String, Double>>() {}.type
    private lateinit var appContext: Context


    lateinit var preferences: SharedPreferences

    fun ensurePreferencesInitialized(context: Context) {
        if (!::preferences.isInitialized) {
            preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        }
    }

    private const val KEY_TOTAL_GAMES_GLOBAL = "total_games_global"
    private const val KEY_CORRECT_GAMES_GLOBAL = "correct_games_global"

    private const val KEY_LAST_IQ_COMPONENTS = "last_iq_components"

    private const val PREFS_NAME = "ScorePrefs"
    private const val KEY_CURRENT_SCORE = "current_score"
    private const val KEY_UNLOCKED_LEVELS = "unlocked_levels"
    private const val KEY_COMPLETED_LEVELS = "completed_levels"

    private const val PREFS_NAME_PRINCIPIANTE = "ScorePrefsPrincipiante"
    private const val KEY_CURRENT_SCORE_PRINCIPIANTE = "current_score_principiante"
    private const val KEY_UNLOCKED_LEVELS_PRINCIPIANTE = "unlocked_levels_principiante"
    private const val KEY_COMPLETED_LEVELS_PRINCIPIANTE = "completed_levels_principiante"

    private const val PREFS_NAME_PRO = "ScorePrefsPro"
    private const val KEY_CURRENT_SCORE_PRO = "current_score_pro"
    private const val KEY_UNLOCKED_LEVELS_PRO = "unlocked_levels_pro"
    private const val KEY_COMPLETED_LEVELS_PRO = "completed_levels_pro"

    private const val KEY_TOTAL_GAMES_NUMEROS_PLUS = "total_games_numeros_plus"
    private const val KEY_TOTAL_TIME_NUMEROS_PLUS = "total_time_numeros_plus"
    private const val KEY_TOTAL_GAMES_NUMEROS_PLUS_EXITOS = "total_games_numeros_plus_exitos"
    private const val KEY_TOTAL_TIME_NUMEROS_PLUS_EXITOS = "total_time_numeros_plus_exitos"

    private const val PREFS_NAME_DECI_PLUS = "ScorePrefsDeciPlus"
    private const val KEY_CURRENT_SCORE_DECI_PLUS = "current_score_deci_plus"
    private const val KEY_UNLOCKED_LEVELS_DECI_PLUS = "unlocked_levels_deci_plus"
    private const val KEY_COMPLETED_LEVELS_DECI_PLUS = "completed_levels_deci_plus"

    private const val PREFS_NAME_DECI_PLUS_PRINCIPIANTE = "ScorePrefsDeciPlusPrincipiante"
    private const val KEY_CURRENT_SCORE_DECI_PLUS_PRINCIPIANTE = "current_score_deci_plus_principiante"
    private const val KEY_UNLOCKED_LEVELS_DECI_PLUS_PRINCIPIANTE = "unlocked_levels_deci_plus_principiante"
    private const val KEY_COMPLETED_LEVELS_DECI_PLUS_PRINCIPIANTE = "completed_levels_deci_plus_principiante"

    private const val PREFS_NAME_DECI_PLUS_PRO = "ScorePrefsDeciPlusPro"
    private const val KEY_CURRENT_SCORE_DECI_PLUS_PRO = "current_score_deci_plus_pro"
    private const val KEY_UNLOCKED_LEVELS_DECI_PLUS_PRO = "unlocked_levels_deci_plus_pro"
    private const val KEY_COMPLETED_LEVELS_DECI_PLUS_PRO = "completed_levels_deci_plus_pro"

    private const val KEY_TOTAL_GAMES_DECI_PLUS = "total_games_deci_plus"
    private const val KEY_TOTAL_TIME_DECI_PLUS = "total_time_deci_plus"
    private const val KEY_TOTAL_GAMES_DECI_PLUS_EXITOS = "total_games_deci_plus_exitos"
    private const val KEY_TOTAL_TIME_DECI_PLUS_EXITOS = "total_time_deci_plus_exitos"

    private const val PREFS_NAME_ROMAS = "ScorePrefsRomas"
    private const val KEY_CURRENT_SCORE_ROMAS = "current_score_romas"
    private const val KEY_UNLOCKED_LEVELS_ROMAS = "unlocked_levels_romas"
    private const val KEY_COMPLETED_LEVELS_ROMAS = "completed_levels_romas"

    private const val PREFS_NAME_ROMAS_PRO = "ScorePrefsRomasPro"
    private const val KEY_CURRENT_SCORE_ROMAS_PRO = "current_score_romas_pro"
    private const val KEY_UNLOCKED_LEVELS_ROMAS_PRO = "unlocked_levels_romas_pro"
    private const val KEY_COMPLETED_LEVELS_ROMAS_PRO = "completed_levels_romas_pro"

    private const val PREFS_NAME_ROMAS_PRINCIPIANTE = "ScorePrefsRomasPrincipiante"
    private const val KEY_CURRENT_SCORE_ROMAS_PRINCIPIANTE = "current_score_romas_principiante"
    private const val KEY_UNLOCKED_LEVELS_ROMAS_PRINCIPIANTE = "unlocked_levels_romas_principiante"
    private const val KEY_COMPLETED_LEVELS_ROMAS_PRINCIPIANTE = "completed_levels_romas_principiante"

    private const val KEY_TOTAL_GAMES_ROMAS = "total_games_romas"
    private const val KEY_TOTAL_TIME_ROMAS = "total_time_romas"
    private const val KEY_TOTAL_GAMES_ROMAS_EXITOS = "total_games_romas_exitos"
    private const val KEY_TOTAL_TIME_ROMAS_EXITOS = "total_time_romas_exitos"

    private const val PREFS_NAME_ALFANUMEROS = "ScorePrefsAlfaNumeros"
    private const val KEY_CURRENT_SCORE_ALFANUMEROS = "current_score_alfanumeros"
    private const val KEY_UNLOCKED_LEVELS_ALFANUMEROS = "unlocked_levels_alfanumeros"
    private const val KEY_COMPLETED_LEVELS_ALFANUMEROS = "completed_levels_alfanumeros"

    private const val PREFS_NAME_ALFANUMEROS_PRINCIPIANTE = "ScorePrefsAlfaNumerosPrincipiante"
    private const val KEY_CURRENT_SCORE_ALFANUMEROS_PRINCIPIANTE = "current_score_alfanumeros_principiante"
    private const val KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRINCIPIANTE = "unlocked_levels_alfanumeros_principiante"
    private const val KEY_COMPLETED_LEVELS_ALFANUMEROS_PRINCIPIANTE = "completed_levels_alfanumeros_principiante"

    private const val PREFS_NAME_ALFANUMEROS_PRO = "ScorePrefsAlfaNumerosPro"
    private const val KEY_CURRENT_SCORE_ALFANUMEROS_PRO = "current_score_alfanumeros_pro"
    private const val KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRO = "unlocked_levels_alfanumeros_pro"
    private const val KEY_COMPLETED_LEVELS_ALFANUMEROS_PRO = "completed_levels_alfanumeros_pro"

    private const val KEY_TOTAL_GAMES_ALFANUMEROS = "total_games_alfanumeros"
    private const val KEY_TOTAL_TIME_ALFANUMEROS = "total_time_alfanumeros"
    private const val KEY_TOTAL_GAMES_ALFANUMEROS_EXITOS = "total_games_alfanumeros_exitos"
    private const val KEY_TOTAL_TIME_ALFANUMEROS_EXITOS = "total_time_alfanumeros_exitos"

    private const val PREFS_NAME_SUMARESTA = "ScorePrefsSumaResta"
    private const val KEY_CURRENT_SCORE_SUMARESTA = "current_score_sumaresta"
    private const val KEY_UNLOCKED_LEVELS_SUMARESTA = "unlocked_levels_sumaresta"
    private const val KEY_COMPLETED_LEVELS_SUMARESTA = "completed_levels_sumaresta"

    private const val PREFS_NAME_SUMARESTA_PRINCIPIANTE = "ScorePrefsSumaRestaPrincipiante"
    private const val KEY_CURRENT_SCORE_SUMARESTA_PRINCIPIANTE = "current_score_sumaresta_principiante"
    private const val KEY_UNLOCKED_LEVELS_SUMARESTA_PRINCIPIANTE = "unlocked_levels_sumaresta_principiante"
    private const val KEY_COMPLETED_LEVELS_SUMARESTA_PRINCIPIANTE = "completed_levels_sumaresta_principiante"

    private const val PREFS_NAME_SUMARESTA_PRO = "ScorePrefsSumaRestaPro"
    private const val KEY_CURRENT_SCORE_SUMARESTA_PRO = "current_score_sumaresta_pro"
    private const val KEY_UNLOCKED_LEVELS_SUMARESTA_PRO = "unlocked_levels_sumaresta_pro"
    private const val KEY_COMPLETED_LEVELS_SUMARESTA_PRO = "completed_levels_sumaresta_pro"

    private const val KEY_TOTAL_GAMES_SUMARESTA = "total_games_sumaresta"
    private const val KEY_TOTAL_TIME_SUMARESTA = "total_time_sumaresta"
    private const val KEY_TOTAL_GAMES_SUMARESTA_EXITOS = "total_games_sumaresta_exitos"
    private const val KEY_TOTAL_TIME_SUMARESTA_EXITOS = "total_time_sumaresta_exitos"

    private const val PREFS_NAME_MAS_PLUS = "ScorePrefsMasPlus"
    private const val KEY_CURRENT_SCORE_MAS_PLUS = "current_score_mas_plus"
    private const val KEY_UNLOCKED_LEVELS_MAS_PLUS = "unlocked_levels_mas_plus"
    private const val KEY_COMPLETED_LEVELS_MAS_PLUS = "completed_levels_mas_plus"

    private const val PREFS_NAME_MAS_PLUS_PRINCIPIANTE = "ScorePrefsMasPlusPrincipiante"
    private const val KEY_CURRENT_SCORE_MAS_PLUS_PRINCIPIANTE = "current_score_mas_plus_principiante"
    private const val KEY_UNLOCKED_LEVELS_MAS_PLUS_PRINCIPIANTE = "unlocked_levels_mas_plus_principiante"
    private const val KEY_COMPLETED_LEVELS_MAS_PLUS_PRINCIPIANTE = "completed_levels_mas_plus_principiante"

    private const val PREFS_NAME_MAS_PLUS_PRO = "ScorePrefsMasPlusPro"
    private const val KEY_CURRENT_SCORE_MAS_PLUS_PRO = "current_score_mas_plus_pro"
    private const val KEY_UNLOCKED_LEVELS_MAS_PLUS_PRO = "unlocked_levels_mas_plus_pro"
    private const val KEY_COMPLETED_LEVELS_MAS_PLUS_PRO = "completed_levels_mas_plus_pro"

    private const val KEY_TOTAL_GAMES_MAS_PLUS = "total_games_mas_plus"
    private const val KEY_TOTAL_TIME_MAS_PLUS = "total_time_mas_plus"
    private const val KEY_TOTAL_GAMES_MAS_PLUS_EXITOS = "total_games_mas_plus_exitos"
    private const val KEY_TOTAL_TIME_MAS_PLUS_EXITOS = "total_time_mas_plus_exitos"

    private const val PREFS_NAME_GENIO_PLUS = "ScorePrefsGenioPlus"
    private const val KEY_CURRENT_SCORE_GENIO_PLUS = "current_score_genio_plus"
    private const val KEY_UNLOCKED_LEVELS_GENIO_PLUS = "unlocked_levels_genio_plus"
    private const val KEY_COMPLETED_LEVELS_GENIO_PLUS= "completed_levels_genio_plus"

    private const val PREFS_NAME_GENIO_PLUS_PRINCIPIANTE = "ScorePrefsGenioPlusPrincipiante"
    private const val KEY_CURRENT_SCORE_GENIO_PLUS_PRINCIPIANTE = "current_score_genio_plus_principiante"
    private const val KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRINCIPIANTE = "unlocked_levels_genio_plus_principiante"
    private const val KEY_COMPLETED_LEVELS_GENIO_PLUS_PRINCIPIANTE = "completed_levels_genio_plus_principiante"

    private const val PREFS_NAME_GENIO_PLUS_PRO = "ScorePrefsGenioPlusPro"
    private const val KEY_CURRENT_SCORE_GENIO_PLUS_PRO = "current_score_genio_plus_pro"
    private const val KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRO = "unlocked_levels_genio_plus_pro"
    private const val KEY_COMPLETED_LEVELS_GENIO_PLUS_PRO = "completed_levels_genio_plus_pro"

    private const val KEY_TOTAL_GAMES_GENIO_PLUS = "total_games_genio_plus"
    private const val KEY_TOTAL_TIME_GENIO_PLUS = "total_time_genio_plus"
    private const val KEY_TOTAL_GAMES_GENIO_PLUS_EXITOS = "total_games_genio_plus_exitos"
    private const val KEY_TOTAL_TIME_GENIO_PLUS_EXITOS = "total_time_genio_plus_exitos"

    var totalGamesGlobal: Int = 0
    var correctGamesGlobal: Int = 0

    var lastIqComponentByGame: MutableMap<String, Double> = mutableMapOf()

    var currentScore: Int = 0
    var unlockedLevels: Int = 2
    val levelScores: MutableMap<Int, Int> = mutableMapOf()

    var currentScorePrincipiante: Int = 0
    var unlockedLevelsPrincipiante: Int = 2
    val levelScoresPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScorePro: Int = 0
    var unlockedLevelsPro: Int = 2
    val levelScoresPro: MutableMap<Int, Int> = mutableMapOf()

    var totalGamesNumerosPlusAvanzado: Int = 0
    var totalGamesNumerosPlusPrincipiante: Int = 0
    var totalGamesNumerosPlusPro: Int = 0

    var totalGamesNumerosPlus: Int = 0
    var totalTimeNumerosPlus: Double = 0.0
    var totalGamesNumerosPlusExitos: Int = 0
    var totalTimeNumerosPlusExitos: Double = 0.0

    var currentScoreDeciPlus: Int = 0
    var unlockedLevelsDeciPlus: Int = 2
    val levelScoresDeciPlus: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreDeciPlusPrincipiante: Int = 0
    var unlockedLevelsDeciPlusPrincipiante: Int = 2
    val levelScoresDeciPlusPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreDeciPlusPro: Int = 0
    var unlockedLevelsDeciPlusPro: Int = 2
    val levelScoresDeciPlusPro: MutableMap<Int, Int> = mutableMapOf()

    var totalGamesDeciPlusAvanzado: Int = 0
    var totalGamesDeciPlusPrincipiante: Int = 0
    var totalGamesDeciPlusPro: Int = 0

    var totalGamesDeciPlus: Int = 0
    var totalTimeDeciPlus: Double = 0.0
    var totalGamesDeciPlusExitos: Int = 0
    var totalTimeDeciPlusExitos: Double = 0.0

    var currentScoreRomas: Int = 0
    var unlockedLevelsRomas: Int = 2
    val levelScoresRomas: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreRomasPrincipiante: Int = 0
    var unlockedLevelsRomasPrincipiante: Int = 2
    val levelScoresRomasPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreRomasPro: Int = 0
    var unlockedLevelsRomasPro: Int = 2
    val levelScoresRomasPro: MutableMap<Int, Int> = mutableMapOf()

    var totalGamesRomasAvanzado: Int = 0
    var totalGamesRomasPrincipiante: Int = 0
    var totalGamesRomasPro: Int = 0

    var totalGamesRomas: Int = 0
    var totalTimeRomas: Double = 0.0
    var totalGamesRomasExitos: Int = 0
    var totalTimeRomasExitos: Double = 0.0

    var currentScoreAlfaNumeros: Int = 0
    var unlockedLevelsAlfaNumeros: Int = 2
    val levelScoresAlfaNumeros: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreAlfaNumerosPrincipiante: Int = 0
    var unlockedLevelsAlfaNumerosPrincipiante: Int = 2
    val levelScoresAlfaNumerosPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreAlfaNumerosPro: Int = 0
    var unlockedLevelsAlfaNumerosPro: Int = 2
    val levelScoresAlfaNumerosPro: MutableMap<Int, Int> = mutableMapOf()

    var totalGamesAlfaNumerosAvanzado: Int = 0
    var totalGamesAlfaNumerosPrincipiante: Int = 0
    var totalGamesAlfaNumerosPro: Int = 0

    var totalGamesAlfaNumeros: Int = 0
    var totalTimeAlfaNumeros: Double = 0.0
    var totalGamesAlfaNumerosExitos: Int = 0
    var totalTimeAlfaNumerosExitos: Double = 0.0

    var currentScoreSumaResta: Int = 0
    var unlockedLevelsSumaResta: Int = 2
    val levelScoresSumaResta: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreSumaRestaPrincipiante: Int = 0
    var unlockedLevelsSumaRestaPrincipiante: Int = 2
    val levelScoresSumaRestaPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreSumaRestaPro: Int = 0
    var unlockedLevelsSumaRestaPro: Int = 2
    val levelScoresSumaRestaPro: MutableMap<Int, Int> = mutableMapOf()

    var totalGamesSumaRestaAvanzado: Int = 0
    var totalGamesSumaRestaPrincipiante: Int = 0
    var totalGamesSumaRestaPro: Int = 0

    var totalGamesSumaResta: Int = 0
    var totalTimeSumaResta: Double = 0.0
    var totalGamesSumaRestaExitos: Int = 0
    var totalTimeSumaRestaExitos: Double = 0.0

    var currentScoreMasPlus: Int = 0
    var unlockedLevelsMasPlus: Int = 2
    val levelScoresMasPlus: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreMasPlusPrincipiante: Int = 0
    var unlockedLevelsMasPlusPrincipiante: Int = 2
    val levelScoresMasPlusPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreMasPlusPro: Int = 0
    var unlockedLevelsMasPlusPro: Int = 2
    val levelScoresMasPlusPro: MutableMap<Int, Int> = mutableMapOf()

    var totalGamesMasPlusAvanzado: Int = 0
    var totalGamesMasPlusPrincipiante: Int = 0
    var totalGamesMasPlusPro: Int = 0

    var totalGamesMasPlus: Int = 0
    var totalTimeMasPlus: Double = 0.0
    var totalGamesMasPlusExitos: Int = 0
    var totalTimeMasPlusExitos: Double = 0.0

    var currentScoreGenioPlus: Int = 0
    var unlockedLevelsGenioPlus: Int = 2
    val levelScoresGenioPlus: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreGenioPlusPrincipiante: Int = 0
    var unlockedLevelsGenioPlusPrincipiante: Int = 2
    val levelScoresGenioPlusPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreGenioPlusPro: Int = 0
    var unlockedLevelsGenioPlusPro: Int = 2
    val levelScoresGenioPlusPro: MutableMap<Int, Int> = mutableMapOf()

    var totalGamesGenioPlusAvanzado: Int = 0
    var totalGamesGenioPlusPrincipiante: Int = 0
    var totalGamesGenioPlusPro: Int = 0

    var totalGamesGenioPlus: Int = 0
    var totalTimeGenioPlus: Double = 0.0
    var totalGamesGenioPlusExitos: Int = 0
    var totalTimeGenioPlusExitos: Double = 0.0

    private const val KEY_CONSECUTIVE_FAILURES = "consecutive_failures"
    private const val KEY_CONSECUTIVE_FAILURES_PRINCIPIANTE = "consecutive_failures_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_PRO = "consecutive_failures_pro"
    private const val KEY_CONSECUTIVE_FAILURES_DECI_PLUS = "consecutive_failures_deci_plus"
    private const val KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRINCIPIANTE = "consecutive_failures_deci_plus_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRO = "consecutive_failures_deci_plus_pro"
    private const val KEY_CONSECUTIVE_FAILURES_ROMAS = "consecutive_failures_romas"
    private const val KEY_CONSECUTIVE_FAILURES_ROMAS_PRINCIPIANTE = "consecutive_failures_romas_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_ROMAS_PRO = "consecutive_failures_romas_pro"
    private const val KEY_CONSECUTIVE_FAILURES_ALFANUMEROS = "consecutive_failures_alfanumeros"
    private const val KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRINCIPIANTE = "consecutive_failures_alfanumeros_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRO = "consecutive_failures_alfanumeros_pro"
    private const val KEY_CONSECUTIVE_FAILURES_SUMARESTA = "consecutive_failures_sumaresta"
    private const val KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRINCIPIANTE = "consecutive_failures_sumaresta_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRO = "consecutive_failures_sumaresta_pro"
    private const val KEY_CONSECUTIVE_FAILURES_MAS_PLUS = "consecutive_failures_mas_plus"
    private const val KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRINCIPIANTE = "consecutive_failures_mas_plus_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRO = "consecutive_failures_mas_plus_pro"
    private const val KEY_CONSECUTIVE_FAILURES_GENIO_PLUS = "consecutive_failures_genio_plus"
    private const val KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRINCIPIANTE = "consecutive_failures_genio_plus_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRO = "consecutive_failures_genio_plus_pro"

    private val consecutiveFailures: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresPro: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresDeciPlus: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresDeciPlusPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresDeciPlusPro: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresRomas: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresRomasPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresRomasPro: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresAlfaNumeros: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresAlfaNumerosPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresAlfaNumerosPro: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresSumaResta: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresSumaRestaPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresSumaRestaPro: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresMasPlus: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresMasPlusPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresMasPlusPro: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresGenioPlus: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresGenioPlusPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresGenioPlusPro: MutableMap<Int, Int> = mutableMapOf()

    private lateinit var preferencesPrincipiante: SharedPreferences
    private lateinit var preferencesPro: SharedPreferences
    private lateinit var preferencesDeciPlus: SharedPreferences
    private lateinit var preferencesDeciPlusPrincipiante: SharedPreferences
    private lateinit var preferencesDeciPlusPro: SharedPreferences
    private lateinit var preferencesRomas: SharedPreferences
    private lateinit var preferencesRomasPrincipiante: SharedPreferences
    private lateinit var preferencesRomasPro: SharedPreferences
    private lateinit var preferencesAlfaNumeros: SharedPreferences
    private lateinit var preferencesAlfaNumerosPrincipiante: SharedPreferences
    private lateinit var preferencesAlfaNumerosPro: SharedPreferences
    private lateinit var preferencesSumaResta: SharedPreferences
    private lateinit var preferencesSumaRestaPrincipiante: SharedPreferences
    private lateinit var preferencesSumaRestaPro: SharedPreferences
    private lateinit var preferencesMasPlus: SharedPreferences
    private lateinit var preferencesMasPlusPrincipiante: SharedPreferences
    private lateinit var preferencesMasPlusPro: SharedPreferences
    private lateinit var preferencesGenioPlus: SharedPreferences
    private lateinit var preferencesGenioPlusPrincipiante: SharedPreferences
    private lateinit var preferencesGenioPlusPro: SharedPreferences

    data class RankingEntry(val userName: String, val valor: Double)

    private enum class Game {
        NUMEROS_PLUS,
        DECI_PLUS,
        ROMAS,
        ALFA_NUMEROS,
        SUMA_RESTA,
        MAS_PLUS,
        GENIO_PLUS
    }

    private enum class Difficulty {
        AVANZADO, PRINCIPIANTE, PRO
    }

    private class GameManager(
        val currentScoreVar: () -> Int,
        val setCurrentScore: (Int) -> Unit,
        val unlockedLevelsVar: () -> Int,
        val setUnlockedLevels: (Int) -> Unit,
        val levelScoresMap: MutableMap<Int, Int>,
        val preferences: SharedPreferences,
        val currentScoreKey: String,
        val unlockedLevelsKey: String,
        val completedLevelsKey: String,
        val consecutiveFailuresKey: String,
        val consecutiveFailuresMap: MutableMap<Int, Int>
    ) {
        fun saveScore() {
            preferences.edit {
                putInt(currentScoreKey, currentScoreVar())
                putInt(unlockedLevelsKey, unlockedLevelsVar())
            }
        }

        fun getCompletedLevels(): Set<Int> {
            return preferences.getStringSet(completedLevelsKey, emptySet())
                ?.map { it.toInt() }?.toSet() ?: emptySet()
        }

        fun addCompletedLevel(level: Int) {
            val completedLevels = getCompletedLevels().toMutableSet()
            completedLevels.add(level)
            preferences.edit {
                putStringSet(completedLevelsKey, completedLevels.map { it.toString() }.toSet())
            }
            if (hasCompletedLevel(1) && hasCompletedLevel(2) && unlockedLevelsVar() < 3) {
                setUnlockedLevels(3)
                saveScore()
            } else if (level >= 3 && level + 1 > unlockedLevelsVar()) {
                setUnlockedLevels(level + 1)
                saveScore()
            }
        }

        fun hasCompletedLevel(level: Int): Boolean = getCompletedLevels().contains(level)

        fun reset() {
            setCurrentScore(0)
            setUnlockedLevels(2)
            levelScoresMap.clear()
            preferences.edit {
                putInt(currentScoreKey, 0)
                putInt(unlockedLevelsKey, 2)
                putStringSet(completedLevelsKey, emptySet())
            }
        }

        fun incrementConsecutiveFailures(level: Int) {
            val currentFailures = consecutiveFailuresMap[level] ?: 0
            consecutiveFailuresMap[level] = currentFailures + 1
            preferences.edit {
                putInt("$consecutiveFailuresKey:$level", consecutiveFailuresMap[level] ?: 0)
            }
        }

        fun resetConsecutiveFailures(level: Int) {
            consecutiveFailuresMap[level] = 0
            preferences.edit {
                putInt("$consecutiveFailuresKey:$level", 0)
            }
        }

        fun getConsecutiveFailures(level: Int): Int = consecutiveFailuresMap[level] ?: 0

        fun isLevelBlockedByFailures(level: Int): Boolean {
            if (level == 1) return false
            return getConsecutiveFailures(level) >= 12
        }

        fun loadConsecutiveFailures() {
            for (i in 1..70) {
                val failures = preferences.getInt("$consecutiveFailuresKey:$i", 0)
                if (failures > 0) {
                    consecutiveFailuresMap[i] = failures
                }
            }
        }
    }

    private val gameManagers = mutableMapOf<Triple<Game, Difficulty, String>, GameManager>()

    private fun getOrCreateManager(game: Game, difficulty: Difficulty, suffix: String = ""): GameManager {

        val context = appContext

        if (!::preferencesPrincipiante.isInitialized) {
            preferencesPrincipiante = context.getSharedPreferences(PREFS_NAME_PRINCIPIANTE, Context.MODE_PRIVATE)
        }
        if (!::preferencesPro.isInitialized) {
            preferencesPro = context.getSharedPreferences(PREFS_NAME_PRO, Context.MODE_PRIVATE)
        }
        if (!::preferencesDeciPlus.isInitialized) {
            preferencesDeciPlus = context.getSharedPreferences(PREFS_NAME_DECI_PLUS, Context.MODE_PRIVATE)
        }
        if (!::preferencesDeciPlusPrincipiante.isInitialized) {
            preferencesDeciPlusPrincipiante = context.getSharedPreferences(PREFS_NAME_DECI_PLUS_PRINCIPIANTE, Context.MODE_PRIVATE)
        }
        if (!::preferencesDeciPlusPro.isInitialized) {
            preferencesDeciPlusPro = context.getSharedPreferences(PREFS_NAME_DECI_PLUS_PRO, Context.MODE_PRIVATE)
        }
        if (!::preferencesRomas.isInitialized) {
            preferencesRomas = context.getSharedPreferences(PREFS_NAME_ROMAS, Context.MODE_PRIVATE)
        }
        if (!::preferencesRomasPrincipiante.isInitialized) {
            preferencesRomasPrincipiante = context.getSharedPreferences(PREFS_NAME_ROMAS_PRINCIPIANTE, Context.MODE_PRIVATE)
        }
        if (!::preferencesRomasPro.isInitialized) {
            preferencesRomasPro = context.getSharedPreferences(PREFS_NAME_ROMAS_PRO, Context.MODE_PRIVATE)
        }
        if (!::preferencesAlfaNumeros.isInitialized) {
            preferencesAlfaNumeros = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS, Context.MODE_PRIVATE)
        }
        if (!::preferencesAlfaNumerosPrincipiante.isInitialized) {
            preferencesAlfaNumerosPrincipiante = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS_PRINCIPIANTE, Context.MODE_PRIVATE)
        }
        if (!::preferencesAlfaNumerosPro.isInitialized) {
            preferencesAlfaNumerosPro = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS_PRO, Context.MODE_PRIVATE)
        }
        if (!::preferencesSumaResta.isInitialized) {
            preferencesSumaResta = context.getSharedPreferences(PREFS_NAME_SUMARESTA, Context.MODE_PRIVATE)
        }
        if (!::preferencesSumaRestaPrincipiante.isInitialized) {
            preferencesSumaRestaPrincipiante = context.getSharedPreferences(PREFS_NAME_SUMARESTA_PRINCIPIANTE, Context.MODE_PRIVATE)
        }
        if (!::preferencesSumaRestaPro.isInitialized) {
            preferencesSumaRestaPro = context.getSharedPreferences(PREFS_NAME_SUMARESTA_PRO, Context.MODE_PRIVATE)
        }
        if (!::preferencesMasPlus.isInitialized) {
            preferencesMasPlus = context.getSharedPreferences(PREFS_NAME_MAS_PLUS, Context.MODE_PRIVATE)
        }
        if (!::preferencesMasPlusPrincipiante.isInitialized) {
            preferencesMasPlusPrincipiante = context.getSharedPreferences(PREFS_NAME_MAS_PLUS_PRINCIPIANTE, Context.MODE_PRIVATE)
        }
        if (!::preferencesMasPlusPro.isInitialized) {
            preferencesMasPlusPro = context.getSharedPreferences(PREFS_NAME_MAS_PLUS_PRO, Context.MODE_PRIVATE)
        }
        if (!::preferencesGenioPlus.isInitialized) {
            preferencesGenioPlus = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS, Context.MODE_PRIVATE)
        }
        if (!::preferencesGenioPlusPrincipiante.isInitialized) {
            preferencesGenioPlusPrincipiante = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS_PRINCIPIANTE, Context.MODE_PRIVATE)
        }
        if (!::preferencesGenioPlusPro.isInitialized) {
            preferencesGenioPlusPro = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS_PRO, Context.MODE_PRIVATE)
        }

        val key = Triple(game, difficulty, suffix)
        return gameManagers.getOrPut(key) {
            when (game) {
                Game.NUMEROS_PLUS -> when (difficulty) {
                    Difficulty.AVANZADO -> GameManager(
                        { currentScore }, { currentScore = it },
                        { unlockedLevels }, { unlockedLevels = it },
                        levelScores, preferences,
                        KEY_CURRENT_SCORE, KEY_UNLOCKED_LEVELS, KEY_COMPLETED_LEVELS,
                        KEY_CONSECUTIVE_FAILURES, consecutiveFailures
                    )
                    Difficulty.PRINCIPIANTE -> GameManager(
                        { currentScorePrincipiante }, { currentScorePrincipiante = it },
                        { unlockedLevelsPrincipiante }, { unlockedLevelsPrincipiante = it },
                        levelScoresPrincipiante, preferencesPrincipiante,
                        KEY_CURRENT_SCORE_PRINCIPIANTE, KEY_UNLOCKED_LEVELS_PRINCIPIANTE, KEY_COMPLETED_LEVELS_PRINCIPIANTE,
                        KEY_CONSECUTIVE_FAILURES_PRINCIPIANTE, consecutiveFailuresPrincipiante
                    )
                    Difficulty.PRO -> GameManager(
                        { currentScorePro }, { currentScorePro = it },
                        { unlockedLevelsPro }, { unlockedLevelsPro = it },
                        levelScoresPro, preferencesPro,
                        KEY_CURRENT_SCORE_PRO, KEY_UNLOCKED_LEVELS_PRO, KEY_COMPLETED_LEVELS_PRO,
                        KEY_CONSECUTIVE_FAILURES_PRO, consecutiveFailuresPro
                    )
                }
                Game.DECI_PLUS -> when (difficulty) {
                    Difficulty.AVANZADO -> GameManager(
                        { currentScoreDeciPlus }, { currentScoreDeciPlus = it },
                        { unlockedLevelsDeciPlus }, { unlockedLevelsDeciPlus = it },
                        levelScoresDeciPlus, preferencesDeciPlus,
                        KEY_CURRENT_SCORE_DECI_PLUS, KEY_UNLOCKED_LEVELS_DECI_PLUS, KEY_COMPLETED_LEVELS_DECI_PLUS,
                        KEY_CONSECUTIVE_FAILURES_DECI_PLUS, consecutiveFailuresDeciPlus
                    )
                    Difficulty.PRINCIPIANTE -> GameManager(
                        { currentScoreDeciPlusPrincipiante }, { currentScoreDeciPlusPrincipiante = it },
                        { unlockedLevelsDeciPlusPrincipiante }, { unlockedLevelsDeciPlusPrincipiante = it },
                        levelScoresDeciPlusPrincipiante, preferencesDeciPlusPrincipiante,
                        KEY_CURRENT_SCORE_DECI_PLUS_PRINCIPIANTE, KEY_UNLOCKED_LEVELS_DECI_PLUS_PRINCIPIANTE, KEY_COMPLETED_LEVELS_DECI_PLUS_PRINCIPIANTE,
                        KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRINCIPIANTE, consecutiveFailuresDeciPlusPrincipiante
                    )
                    Difficulty.PRO -> GameManager(
                        { currentScoreDeciPlusPro }, { currentScoreDeciPlusPro = it },
                        { unlockedLevelsDeciPlusPro }, { unlockedLevelsDeciPlusPro = it },
                        levelScoresDeciPlusPro, preferencesDeciPlusPro,
                        KEY_CURRENT_SCORE_DECI_PLUS_PRO, KEY_UNLOCKED_LEVELS_DECI_PLUS_PRO, KEY_COMPLETED_LEVELS_DECI_PLUS_PRO,
                        KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRO, consecutiveFailuresDeciPlusPro
                    )
                }
                Game.ROMAS -> when (difficulty) {
                    Difficulty.AVANZADO -> GameManager(
                        { currentScoreRomas }, { currentScoreRomas = it },
                        { unlockedLevelsRomas }, { unlockedLevelsRomas = it },
                        levelScoresRomas, preferencesRomas,
                        KEY_CURRENT_SCORE_ROMAS, KEY_UNLOCKED_LEVELS_ROMAS, KEY_COMPLETED_LEVELS_ROMAS,
                        KEY_CONSECUTIVE_FAILURES_ROMAS, consecutiveFailuresRomas
                    )
                    Difficulty.PRINCIPIANTE -> GameManager(
                        { currentScoreRomasPrincipiante }, { currentScoreRomasPrincipiante = it },
                        { unlockedLevelsRomasPrincipiante }, { unlockedLevelsRomasPrincipiante = it },
                        levelScoresRomasPrincipiante, preferencesRomasPrincipiante,
                        KEY_CURRENT_SCORE_ROMAS_PRINCIPIANTE, KEY_UNLOCKED_LEVELS_ROMAS_PRINCIPIANTE, KEY_COMPLETED_LEVELS_ROMAS_PRINCIPIANTE,
                        KEY_CONSECUTIVE_FAILURES_ROMAS_PRINCIPIANTE, consecutiveFailuresRomasPrincipiante
                    )
                    Difficulty.PRO -> GameManager(
                        { currentScoreRomasPro }, { currentScoreRomasPro = it },
                        { unlockedLevelsRomasPro }, { unlockedLevelsRomasPro = it },
                        levelScoresRomasPro, preferencesRomasPro,
                        KEY_CURRENT_SCORE_ROMAS_PRO, KEY_UNLOCKED_LEVELS_ROMAS_PRO, KEY_COMPLETED_LEVELS_ROMAS_PRO,
                        KEY_CONSECUTIVE_FAILURES_ROMAS_PRO, consecutiveFailuresRomasPro
                    )
                }
                Game.ALFA_NUMEROS -> when (difficulty) {
                    Difficulty.AVANZADO -> GameManager(
                        { currentScoreAlfaNumeros }, { currentScoreAlfaNumeros = it },
                        { unlockedLevelsAlfaNumeros }, { unlockedLevelsAlfaNumeros = it },
                        levelScoresAlfaNumeros, preferencesAlfaNumeros,
                        KEY_CURRENT_SCORE_ALFANUMEROS, KEY_UNLOCKED_LEVELS_ALFANUMEROS, KEY_COMPLETED_LEVELS_ALFANUMEROS,
                        KEY_CONSECUTIVE_FAILURES_ALFANUMEROS, consecutiveFailuresAlfaNumeros
                    )
                    Difficulty.PRINCIPIANTE -> GameManager(
                        { currentScoreAlfaNumerosPrincipiante }, { currentScoreAlfaNumerosPrincipiante = it },
                        { unlockedLevelsAlfaNumerosPrincipiante }, { unlockedLevelsAlfaNumerosPrincipiante = it },
                        levelScoresAlfaNumerosPrincipiante, preferencesAlfaNumerosPrincipiante,
                        KEY_CURRENT_SCORE_ALFANUMEROS_PRINCIPIANTE, KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRINCIPIANTE, KEY_COMPLETED_LEVELS_ALFANUMEROS_PRINCIPIANTE,
                        KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRINCIPIANTE, consecutiveFailuresAlfaNumerosPrincipiante
                    )
                    Difficulty.PRO -> GameManager(
                        { currentScoreAlfaNumerosPro }, { currentScoreAlfaNumerosPro = it },
                        { unlockedLevelsAlfaNumerosPro }, { unlockedLevelsAlfaNumerosPro = it },
                        levelScoresAlfaNumerosPro, preferencesAlfaNumerosPro,
                        KEY_CURRENT_SCORE_ALFANUMEROS_PRO, KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRO, KEY_COMPLETED_LEVELS_ALFANUMEROS_PRO,
                        KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRO, consecutiveFailuresAlfaNumerosPro
                    )
                }
                Game.SUMA_RESTA -> when (difficulty) {
                    Difficulty.AVANZADO -> GameManager(
                        { currentScoreSumaResta }, { currentScoreSumaResta = it },
                        { unlockedLevelsSumaResta }, { unlockedLevelsSumaResta = it },
                        levelScoresSumaResta, preferencesSumaResta,
                        KEY_CURRENT_SCORE_SUMARESTA, KEY_UNLOCKED_LEVELS_SUMARESTA, KEY_COMPLETED_LEVELS_SUMARESTA,
                        KEY_CONSECUTIVE_FAILURES_SUMARESTA, consecutiveFailuresSumaResta
                    )
                    Difficulty.PRINCIPIANTE -> GameManager(
                        { currentScoreSumaRestaPrincipiante }, { currentScoreSumaRestaPrincipiante = it },
                        { unlockedLevelsSumaRestaPrincipiante }, { unlockedLevelsSumaRestaPrincipiante = it },
                        levelScoresSumaRestaPrincipiante, preferencesSumaRestaPrincipiante,
                        KEY_CURRENT_SCORE_SUMARESTA_PRINCIPIANTE, KEY_UNLOCKED_LEVELS_SUMARESTA_PRINCIPIANTE, KEY_COMPLETED_LEVELS_SUMARESTA_PRINCIPIANTE,
                        KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRINCIPIANTE, consecutiveFailuresSumaRestaPrincipiante
                    )
                    Difficulty.PRO -> GameManager(
                        { currentScoreSumaRestaPro }, { currentScoreSumaRestaPro = it },
                        { unlockedLevelsSumaRestaPro }, { unlockedLevelsSumaRestaPro = it },
                        levelScoresSumaRestaPro, preferencesSumaRestaPro,
                        KEY_CURRENT_SCORE_SUMARESTA_PRO, KEY_UNLOCKED_LEVELS_SUMARESTA_PRO, KEY_COMPLETED_LEVELS_SUMARESTA_PRO,
                        KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRO, consecutiveFailuresSumaRestaPro
                    )
                }
                Game.MAS_PLUS -> when (difficulty) {
                    Difficulty.AVANZADO -> GameManager(
                        { currentScoreMasPlus }, { currentScoreMasPlus = it },
                        { unlockedLevelsMasPlus }, { unlockedLevelsMasPlus = it },
                        levelScoresMasPlus, preferencesMasPlus,
                        KEY_CURRENT_SCORE_MAS_PLUS, KEY_UNLOCKED_LEVELS_MAS_PLUS, KEY_COMPLETED_LEVELS_MAS_PLUS,
                        KEY_CONSECUTIVE_FAILURES_MAS_PLUS, consecutiveFailuresMasPlus
                    )
                    Difficulty.PRINCIPIANTE -> GameManager(
                        { currentScoreMasPlusPrincipiante }, { currentScoreMasPlusPrincipiante = it },
                        { unlockedLevelsMasPlusPrincipiante }, { unlockedLevelsMasPlusPrincipiante = it },
                        levelScoresMasPlusPrincipiante, preferencesMasPlusPrincipiante,
                        KEY_CURRENT_SCORE_MAS_PLUS_PRINCIPIANTE, KEY_UNLOCKED_LEVELS_MAS_PLUS_PRINCIPIANTE, KEY_COMPLETED_LEVELS_MAS_PLUS_PRINCIPIANTE,
                        KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRINCIPIANTE, consecutiveFailuresMasPlusPrincipiante
                    )
                    Difficulty.PRO -> GameManager(
                        { currentScoreMasPlusPro }, { currentScoreMasPlusPro = it },
                        { unlockedLevelsMasPlusPro }, { unlockedLevelsMasPlusPro = it },
                        levelScoresMasPlusPro, preferencesMasPlusPro,
                        KEY_CURRENT_SCORE_MAS_PLUS_PRO, KEY_UNLOCKED_LEVELS_MAS_PLUS_PRO, KEY_COMPLETED_LEVELS_MAS_PLUS_PRO,
                        KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRO, consecutiveFailuresMasPlusPro
                    )
                }
                Game.GENIO_PLUS -> when (difficulty) {
                    Difficulty.AVANZADO -> GameManager(
                        { currentScoreGenioPlus }, { currentScoreGenioPlus = it },
                        { unlockedLevelsGenioPlus }, { unlockedLevelsGenioPlus = it },
                        levelScoresGenioPlus, preferencesGenioPlus,
                        KEY_CURRENT_SCORE_GENIO_PLUS, KEY_UNLOCKED_LEVELS_GENIO_PLUS, KEY_COMPLETED_LEVELS_GENIO_PLUS,
                        KEY_CONSECUTIVE_FAILURES_GENIO_PLUS, consecutiveFailuresGenioPlus
                    )
                    Difficulty.PRINCIPIANTE -> GameManager(
                        { currentScoreGenioPlusPrincipiante }, { currentScoreGenioPlusPrincipiante = it },
                        { unlockedLevelsGenioPlusPrincipiante }, { unlockedLevelsGenioPlusPrincipiante = it },
                        levelScoresGenioPlusPrincipiante, preferencesGenioPlusPrincipiante,
                        KEY_CURRENT_SCORE_GENIO_PLUS_PRINCIPIANTE, KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRINCIPIANTE, KEY_COMPLETED_LEVELS_GENIO_PLUS_PRINCIPIANTE,
                        KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRINCIPIANTE, consecutiveFailuresGenioPlusPrincipiante
                    )
                    Difficulty.PRO -> GameManager(
                        { currentScoreGenioPlusPro }, { currentScoreGenioPlusPro = it },
                        { unlockedLevelsGenioPlusPro }, { unlockedLevelsGenioPlusPro = it },
                        levelScoresGenioPlusPro, preferencesGenioPlusPro,
                        KEY_CURRENT_SCORE_GENIO_PLUS_PRO, KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRO, KEY_COMPLETED_LEVELS_GENIO_PLUS_PRO,
                        KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRO, consecutiveFailuresGenioPlusPro
                    )
                }
            }
        }
    }

    fun init(context: Context) {
        ensurePreferencesInitialized(context)
        appContext = context.applicationContext

        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (!::preferencesPrincipiante.isInitialized) {
            preferencesPrincipiante = context.getSharedPreferences(PREFS_NAME_PRINCIPIANTE, Context.MODE_PRIVATE)
        }
        if (!::preferencesPro.isInitialized) {
            preferencesPro = context.getSharedPreferences(PREFS_NAME_PRO, Context.MODE_PRIVATE)
        }

        currentScore = preferences.getInt(KEY_CURRENT_SCORE, 0)
        unlockedLevels = preferences.getInt(KEY_UNLOCKED_LEVELS, 2)

        getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).loadConsecutiveFailures()

        totalGamesNumerosPlusAvanzado = preferences.getInt("total_games_numeros_plus_avanzado", 0)
        totalGamesNumerosPlusPrincipiante = preferences.getInt("total_games_numeros_plus_principiante", 0)
        totalGamesNumerosPlusPro = preferences.getInt("total_games_numeros_plus_pro", 0)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesNumerosPlus = preferences.getInt(KEY_TOTAL_GAMES_NUMEROS_PLUS, 0)
        totalTimeNumerosPlus = preferences.getFloat(KEY_TOTAL_TIME_NUMEROS_PLUS, 0f).toDouble()
        totalGamesNumerosPlusExitos = preferences.getInt(KEY_TOTAL_GAMES_NUMEROS_PLUS_EXITOS, 0)
        totalTimeNumerosPlusExitos = preferences.getFloat(KEY_TOTAL_TIME_NUMEROS_PLUS_EXITOS, 0f).toDouble()

        val storedMap = preferences.getString(KEY_LAST_IQ_COMPONENTS, null)
        lastIqComponentByGame = if (storedMap != null) gson.fromJson(storedMap, mapType) else mutableMapOf()
    }

    fun initLight(context: Context) {
        ensurePreferencesInitialized(context)
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        currentScore = preferences.getInt(KEY_CURRENT_SCORE, 0)
        unlockedLevels = preferences.getInt(KEY_UNLOCKED_LEVELS, 2)
        totalGamesNumerosPlus = preferences.getInt(KEY_TOTAL_GAMES_NUMEROS_PLUS, 0)
        totalTimeNumerosPlus = preferences.getFloat(KEY_TOTAL_TIME_NUMEROS_PLUS, 0f).toDouble()
        totalGamesNumerosPlusExitos = preferences.getInt(KEY_TOTAL_GAMES_NUMEROS_PLUS_EXITOS, 0)
        totalTimeNumerosPlusExitos = preferences.getFloat(KEY_TOTAL_TIME_NUMEROS_PLUS_EXITOS, 0f).toDouble()
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
    }

    fun initPrincipiante(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesPrincipiante = context.getSharedPreferences(PREFS_NAME_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScorePrincipiante = preferencesPrincipiante.getInt(KEY_CURRENT_SCORE_PRINCIPIANTE, 0)
        unlockedLevelsPrincipiante = preferencesPrincipiante.getInt(KEY_UNLOCKED_LEVELS_PRINCIPIANTE, 2)
        getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).loadConsecutiveFailures()
    }

    fun initPro(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesPro = context.getSharedPreferences(PREFS_NAME_PRO, Context.MODE_PRIVATE)
        currentScorePro = preferencesPro.getInt(KEY_CURRENT_SCORE_PRO, 0)
        unlockedLevelsPro = preferencesPro.getInt(KEY_UNLOCKED_LEVELS_PRO, 2)
        getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).loadConsecutiveFailures()
    }

    fun initDeciPlus(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesDeciPlus = context.getSharedPreferences(PREFS_NAME_DECI_PLUS, Context.MODE_PRIVATE)
        currentScoreDeciPlus = preferencesDeciPlus.getInt(KEY_CURRENT_SCORE_DECI_PLUS, 0)
        unlockedLevelsDeciPlus = preferencesDeciPlus.getInt(KEY_UNLOCKED_LEVELS_DECI_PLUS, 2)
        getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).loadConsecutiveFailures()

        totalGamesDeciPlusAvanzado = preferences.getInt("total_games_deci_plus_avanzado", 0)
        totalGamesDeciPlusPrincipiante = preferences.getInt("total_games_deci_plus_principiante", 0)
        totalGamesDeciPlusPro = preferences.getInt("total_games_deci_plus_pro", 0)
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesDeciPlus = preferencesDeciPlus.getInt(KEY_TOTAL_GAMES_DECI_PLUS, 0)
        totalTimeDeciPlus = preferencesDeciPlus.getFloat(KEY_TOTAL_TIME_DECI_PLUS, 0f).toDouble()
        totalGamesDeciPlusExitos = preferences.getInt(KEY_TOTAL_GAMES_DECI_PLUS_EXITOS, 0)
        totalTimeDeciPlusExitos = preferences.getFloat(KEY_TOTAL_TIME_DECI_PLUS_EXITOS, 0f).toDouble()

        val storedMap = preferences.getString(KEY_LAST_IQ_COMPONENTS, null)
        lastIqComponentByGame = if (storedMap != null) gson.fromJson(storedMap, mapType) else mutableMapOf()
    }

    fun initDeciPlusPrincipiante(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesDeciPlusPrincipiante = context.getSharedPreferences(PREFS_NAME_DECI_PLUS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreDeciPlusPrincipiante = preferencesDeciPlusPrincipiante.getInt(KEY_CURRENT_SCORE_DECI_PLUS_PRINCIPIANTE, 0)
        unlockedLevelsDeciPlusPrincipiante = preferencesDeciPlusPrincipiante.getInt(KEY_UNLOCKED_LEVELS_DECI_PLUS_PRINCIPIANTE, 2)

        for (i in 1..70) {
            val failures = preferencesDeciPlusPrincipiante.getInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRINCIPIANTE:$i-principiante", 0)
            if (failures > 0) {
                consecutiveFailuresDeciPlusPrincipiante[i] = failures
            }
        }
    }

    fun initDeciPlusPro(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesDeciPlusPro = context.getSharedPreferences(PREFS_NAME_DECI_PLUS_PRO, Context.MODE_PRIVATE)
        currentScoreDeciPlusPro = preferencesDeciPlusPro.getInt(KEY_CURRENT_SCORE_DECI_PLUS_PRO, 0)
        unlockedLevelsDeciPlusPro = preferencesDeciPlusPro.getInt(KEY_UNLOCKED_LEVELS_DECI_PLUS_PRO, 2)

        for (i in 1..70) {
            val failures = preferencesDeciPlusPro.getInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRO:$i-pro", 0)
            if (failures > 0) {
                consecutiveFailuresDeciPlusPro[i] = failures
            }
        }
    }

    fun initRomas(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesRomas = context.getSharedPreferences(PREFS_NAME_ROMAS, Context.MODE_PRIVATE)
        currentScoreRomas = preferencesRomas.getInt(KEY_CURRENT_SCORE_ROMAS, 0)
        unlockedLevelsRomas = preferencesRomas.getInt(KEY_UNLOCKED_LEVELS_ROMAS, 2)
        getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).loadConsecutiveFailures()

        totalGamesRomasAvanzado = preferences.getInt("total_games_romas_avanzado", 0)
        totalGamesRomasPrincipiante = preferences.getInt("total_games_romas_principiante", 0)
        totalGamesRomasPro = preferences.getInt("total_games_romas_pro", 0)
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesRomas = preferencesRomas.getInt(KEY_TOTAL_GAMES_ROMAS, 0)
        totalTimeRomas = preferencesRomas.getFloat(KEY_TOTAL_TIME_ROMAS, 0f).toDouble()
        totalGamesRomasExitos = preferences.getInt(KEY_TOTAL_GAMES_ROMAS_EXITOS, 0)
        totalTimeRomasExitos = preferences.getFloat(KEY_TOTAL_TIME_ROMAS_EXITOS, 0f).toDouble()

        val storedMap = preferences.getString(KEY_LAST_IQ_COMPONENTS, null)
        lastIqComponentByGame = if (storedMap != null) gson.fromJson(storedMap, mapType) else mutableMapOf()
    }

    fun initRomasPrincipiante(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesRomasPrincipiante = context.getSharedPreferences(PREFS_NAME_ROMAS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreRomasPrincipiante = preferencesRomasPrincipiante.getInt(KEY_CURRENT_SCORE_ROMAS_PRINCIPIANTE, 0)
        unlockedLevelsRomasPrincipiante = preferencesRomasPrincipiante.getInt(KEY_UNLOCKED_LEVELS_ROMAS_PRINCIPIANTE, 2)
        getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).loadConsecutiveFailures()
    }

    fun initRomasPro(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesRomasPro = context.getSharedPreferences(PREFS_NAME_ROMAS_PRO, Context.MODE_PRIVATE)
        currentScoreRomasPro = preferencesRomasPro.getInt(KEY_CURRENT_SCORE_ROMAS_PRO, 0)
        unlockedLevelsRomasPro = preferencesRomasPro.getInt(KEY_UNLOCKED_LEVELS_ROMAS_PRO, 2)
        getOrCreateManager(Game.ROMAS, Difficulty.PRO).loadConsecutiveFailures()
    }

    fun initAlfaNumeros(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesAlfaNumeros = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS, Context.MODE_PRIVATE)
        currentScoreAlfaNumeros = preferencesAlfaNumeros.getInt(KEY_CURRENT_SCORE_ALFANUMEROS, 0)
        unlockedLevelsAlfaNumeros = preferencesAlfaNumeros.getInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS, 2)
        getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).loadConsecutiveFailures()

        totalGamesAlfaNumerosAvanzado = preferences.getInt("total_games_alfanumeros_avanzado", 0)
        totalGamesAlfaNumerosPrincipiante = preferences.getInt("total_games_alfanumeros_principiante", 0)
        totalGamesAlfaNumerosPro = preferences.getInt("total_games_alfanumeros_pro", 0)
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesAlfaNumeros = preferencesAlfaNumeros.getInt(KEY_TOTAL_GAMES_ALFANUMEROS, 0)
        totalTimeAlfaNumeros = preferencesAlfaNumeros.getFloat(KEY_TOTAL_TIME_ALFANUMEROS, 0f).toDouble()
        totalGamesAlfaNumerosExitos = preferences.getInt(KEY_TOTAL_GAMES_ALFANUMEROS_EXITOS, 0)
        totalTimeAlfaNumerosExitos = preferences.getFloat(KEY_TOTAL_TIME_ALFANUMEROS_EXITOS, 0f).toDouble()

        val storedMap = preferences.getString(KEY_LAST_IQ_COMPONENTS, null)
        lastIqComponentByGame = if (storedMap != null) gson.fromJson(storedMap, mapType) else mutableMapOf()
    }

    fun initAlfaNumerosPrincipiante(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesAlfaNumerosPrincipiante = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreAlfaNumerosPrincipiante = preferencesAlfaNumerosPrincipiante.getInt(KEY_CURRENT_SCORE_ALFANUMEROS_PRINCIPIANTE, 0)
        unlockedLevelsAlfaNumerosPrincipiante = preferencesAlfaNumerosPrincipiante.getInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRINCIPIANTE, 2)
        getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).loadConsecutiveFailures()
    }

    fun initAlfaNumerosPro(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesAlfaNumerosPro = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS_PRO, Context.MODE_PRIVATE)
        currentScoreAlfaNumerosPro = preferencesAlfaNumerosPro.getInt(KEY_CURRENT_SCORE_ALFANUMEROS_PRO, 0)
        unlockedLevelsAlfaNumerosPro = preferencesAlfaNumerosPro.getInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRO, 2)
        getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).loadConsecutiveFailures()
    }

    fun initSumaResta(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesSumaResta = context.getSharedPreferences(PREFS_NAME_SUMARESTA, Context.MODE_PRIVATE)
        currentScoreSumaResta = preferencesSumaResta.getInt(KEY_CURRENT_SCORE_SUMARESTA, 0)
        unlockedLevelsSumaResta = preferencesSumaResta.getInt(KEY_UNLOCKED_LEVELS_SUMARESTA, 2)
        getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).loadConsecutiveFailures()

        totalGamesSumaRestaAvanzado = preferences.getInt("total_games_sumaresta_avanzado", 0)
        totalGamesSumaRestaPrincipiante = preferences.getInt("total_games_sumaresta_principiante", 0)
        totalGamesSumaRestaPro = preferences.getInt("total_games_sumaresta_pro", 0)
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesSumaResta = preferencesSumaResta.getInt(KEY_TOTAL_GAMES_SUMARESTA, 0)
        totalTimeSumaResta = preferencesSumaResta.getFloat(KEY_TOTAL_TIME_SUMARESTA, 0f).toDouble()
        totalGamesSumaRestaExitos = preferences.getInt(KEY_TOTAL_GAMES_SUMARESTA_EXITOS, 0)
        totalTimeSumaRestaExitos = preferences.getFloat(KEY_TOTAL_TIME_SUMARESTA_EXITOS, 0f).toDouble()

        val storedMap = preferences.getString(KEY_LAST_IQ_COMPONENTS, null)
        lastIqComponentByGame = if (storedMap != null) gson.fromJson(storedMap, mapType) else mutableMapOf()
    }

    fun initSumaRestaPrincipiante(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesSumaRestaPrincipiante = context.getSharedPreferences(PREFS_NAME_SUMARESTA_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreSumaRestaPrincipiante = preferencesSumaRestaPrincipiante.getInt(KEY_CURRENT_SCORE_SUMARESTA_PRINCIPIANTE, 0)
        unlockedLevelsSumaRestaPrincipiante = preferencesSumaRestaPrincipiante.getInt(KEY_UNLOCKED_LEVELS_SUMARESTA_PRINCIPIANTE, 2)
        getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).loadConsecutiveFailures()
    }

    fun initSumaRestaPro(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesSumaRestaPro = context.getSharedPreferences(PREFS_NAME_SUMARESTA_PRO, Context.MODE_PRIVATE)
        currentScoreSumaRestaPro = preferencesSumaRestaPro.getInt(KEY_CURRENT_SCORE_SUMARESTA_PRO, 0)
        unlockedLevelsSumaRestaPro = preferencesSumaRestaPro.getInt(KEY_UNLOCKED_LEVELS_SUMARESTA_PRO, 2)
        getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).loadConsecutiveFailures()
    }

    fun initMasPlus(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesMasPlus = context.getSharedPreferences(PREFS_NAME_MAS_PLUS, Context.MODE_PRIVATE)
        currentScoreMasPlus = preferencesMasPlus.getInt(KEY_CURRENT_SCORE_MAS_PLUS, 0)
        unlockedLevelsMasPlus = preferencesMasPlus.getInt(KEY_UNLOCKED_LEVELS_MAS_PLUS, 2)
        getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).loadConsecutiveFailures()

        totalGamesMasPlusAvanzado = preferences.getInt("total_games_masplus_avanzado", 0)
        totalGamesMasPlusPrincipiante = preferences.getInt("total_games_masplus_principiante", 0)
        totalGamesMasPlusPro = preferences.getInt("total_games_masplus_pro", 0)
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesMasPlus = preferencesMasPlus.getInt(KEY_TOTAL_GAMES_MAS_PLUS, 0)
        totalTimeMasPlus = preferencesMasPlus.getFloat(KEY_TOTAL_TIME_MAS_PLUS, 0f).toDouble()
        totalGamesMasPlusExitos = preferences.getInt(KEY_TOTAL_GAMES_MAS_PLUS_EXITOS, 0)
        totalTimeMasPlusExitos = preferences.getFloat(KEY_TOTAL_TIME_MAS_PLUS_EXITOS, 0f).toDouble()

        val storedMap = preferences.getString(KEY_LAST_IQ_COMPONENTS, null)
        lastIqComponentByGame = if (storedMap != null) gson.fromJson(storedMap, mapType) else mutableMapOf()
    }

    fun initMasPlusPrincipiante(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesMasPlusPrincipiante = context.getSharedPreferences(PREFS_NAME_MAS_PLUS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreMasPlusPrincipiante = preferencesMasPlusPrincipiante.getInt(KEY_CURRENT_SCORE_MAS_PLUS_PRINCIPIANTE, 0)
        unlockedLevelsMasPlusPrincipiante = preferencesMasPlusPrincipiante.getInt(KEY_UNLOCKED_LEVELS_MAS_PLUS_PRINCIPIANTE, 2)
        getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).loadConsecutiveFailures()
    }

    fun initMasPlusPro(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesMasPlusPro = context.getSharedPreferences(PREFS_NAME_MAS_PLUS_PRO, Context.MODE_PRIVATE)
        currentScoreMasPlusPro = preferencesMasPlusPro.getInt(KEY_CURRENT_SCORE_MAS_PLUS_PRO, 0)
        unlockedLevelsMasPlusPro = preferencesMasPlusPro.getInt(KEY_UNLOCKED_LEVELS_MAS_PLUS_PRO, 2)
        getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).loadConsecutiveFailures()
    }

    fun initGenioPlus(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesGenioPlus = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS, Context.MODE_PRIVATE)
        currentScoreGenioPlus = preferencesGenioPlus.getInt(KEY_CURRENT_SCORE_GENIO_PLUS, 0)
        unlockedLevelsGenioPlus = preferencesGenioPlus.getInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS, 2)
        getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).loadConsecutiveFailures()

        totalGamesGenioPlusAvanzado = preferences.getInt("total_games_genioplus_avanzado", 0)
        totalGamesGenioPlusPrincipiante = preferences.getInt("total_games_genioplus_principiante", 0)
        totalGamesGenioPlusPro = preferences.getInt("total_games_genioplus_pro", 0)
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesGenioPlus = preferencesGenioPlus.getInt(KEY_TOTAL_GAMES_GENIO_PLUS, 0)
        totalTimeGenioPlus = preferencesGenioPlus.getFloat(KEY_TOTAL_TIME_GENIO_PLUS, 0f).toDouble()
        totalGamesGenioPlusExitos = preferences.getInt(KEY_TOTAL_GAMES_GENIO_PLUS_EXITOS, 0)
        totalTimeGenioPlusExitos = preferences.getFloat(KEY_TOTAL_TIME_GENIO_PLUS_EXITOS, 0f).toDouble()

        val storedMap = preferences.getString(KEY_LAST_IQ_COMPONENTS, null)
        lastIqComponentByGame = if (storedMap != null) gson.fromJson(storedMap, mapType) else mutableMapOf()
    }

    fun initGenioPlusPrincipiante(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesGenioPlusPrincipiante = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreGenioPlusPrincipiante = preferencesGenioPlusPrincipiante.getInt(KEY_CURRENT_SCORE_GENIO_PLUS_PRINCIPIANTE, 0)
        unlockedLevelsGenioPlusPrincipiante = preferencesGenioPlusPrincipiante.getInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRINCIPIANTE, 2)
        getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).loadConsecutiveFailures()
    }

    fun initGenioPlusPro(context: Context) {
        ensurePreferencesInitialized(context)
        preferencesGenioPlusPro = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS_PRO, Context.MODE_PRIVATE)
        currentScoreGenioPlusPro = preferencesGenioPlusPro.getInt(KEY_CURRENT_SCORE_GENIO_PLUS_PRO, 0)
        unlockedLevelsGenioPlusPro = preferencesGenioPlusPro.getInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRO, 2)
        getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).loadConsecutiveFailures()
    }

    fun saveScore() = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).saveScore()
    fun saveScorePrincipiante() = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).saveScore()
    fun saveScorePro() = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).saveScore()
    fun saveScoreDeciPlus() = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).saveScore()
    fun saveScoreDeciPlusPrincipiante() = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).saveScore()
    fun saveScoreDeciPlusPro() = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).saveScore()
    fun saveScoreRomas() = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).saveScore()
    fun saveScoreRomasPrincipiante() = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).saveScore()
    fun saveScoreRomasPro() = getOrCreateManager(Game.ROMAS, Difficulty.PRO).saveScore()
    fun saveScoreAlfaNumeros() = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).saveScore()
    fun saveScoreAlfaNumerosPrincipiante() = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).saveScore()
    fun saveScoreAlfaNumerosPro() = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).saveScore()
    fun saveScoreSumaResta() = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).saveScore()
    fun saveScoreSumaRestaPrincipiante() = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).saveScore()
    fun saveScoreSumaRestaPro() = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).saveScore()
    fun saveScoreMasPlus() = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).saveScore()
    fun saveScoreMasPlusPrincipiante() = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).saveScore()
    fun saveScoreMasPlusPro() = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).saveScore()
    fun saveScoreGenioPlus() = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).saveScore()
    fun saveScoreGenioPlusPrincipiante() = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).saveScore()
    fun saveScoreGenioPlusPro() = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).saveScore()


    fun saveStatsGlobalAndNumerosPlus() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
            putInt(KEY_TOTAL_GAMES_NUMEROS_PLUS, totalGamesNumerosPlus)
            putFloat(KEY_TOTAL_TIME_NUMEROS_PLUS, totalTimeNumerosPlus.toFloat())
            putInt(KEY_TOTAL_GAMES_NUMEROS_PLUS_EXITOS, totalGamesNumerosPlusExitos)
            putFloat(KEY_TOTAL_TIME_NUMEROS_PLUS_EXITOS, totalTimeNumerosPlusExitos.toFloat())
            putInt("total_games_numeros_plus_avanzado", totalGamesNumerosPlusAvanzado)
            putInt("total_games_numeros_plus_principiante", totalGamesNumerosPlusPrincipiante)
            putInt("total_games_numeros_plus_pro", totalGamesNumerosPlusPro)
            putString(KEY_LAST_IQ_COMPONENTS, gson.toJson(lastIqComponentByGame))
        }
    }

    fun saveStatsGlobalAndDeciPlus() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
            putInt(KEY_TOTAL_GAMES_DECI_PLUS, totalGamesDeciPlus)
            putFloat(KEY_TOTAL_TIME_DECI_PLUS, totalTimeDeciPlus.toFloat())
            putInt(KEY_TOTAL_GAMES_DECI_PLUS_EXITOS, totalGamesDeciPlusExitos)
            putFloat(KEY_TOTAL_TIME_DECI_PLUS_EXITOS, totalTimeDeciPlusExitos.toFloat())
            putInt("total_games_deci_plus_avanzado", totalGamesDeciPlusAvanzado)
            putInt("total_games_deci_plus_principiante", totalGamesDeciPlusPrincipiante)
            putInt("total_games_deci_plus_pro", totalGamesDeciPlusPro)
            putString(KEY_LAST_IQ_COMPONENTS, gson.toJson(lastIqComponentByGame))
        }
    }

    fun saveStatsGlobalAndRomas() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
            putInt(KEY_TOTAL_GAMES_ROMAS, totalGamesRomas)
            putFloat(KEY_TOTAL_TIME_ROMAS, totalTimeRomas.toFloat())
            putInt(KEY_TOTAL_GAMES_ROMAS_EXITOS, totalGamesRomasExitos)
            putFloat(KEY_TOTAL_TIME_ROMAS_EXITOS, totalTimeRomasExitos.toFloat())
            putInt("total_games_romas_avanzado", totalGamesRomasAvanzado)
            putInt("total_games_romas_principiante", totalGamesRomasPrincipiante)
            putInt("total_games_romas_pro", totalGamesRomasPro)
            putString(KEY_LAST_IQ_COMPONENTS, gson.toJson(lastIqComponentByGame))
        }
    }

    fun saveStatsGlobalAndAlfaNumeros() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
            putInt(KEY_TOTAL_GAMES_ALFANUMEROS, totalGamesAlfaNumeros)
            putFloat(KEY_TOTAL_TIME_ALFANUMEROS, totalTimeAlfaNumeros.toFloat())
            putInt(KEY_TOTAL_GAMES_ALFANUMEROS_EXITOS, totalGamesAlfaNumerosExitos)
            putFloat(KEY_TOTAL_TIME_ALFANUMEROS_EXITOS, totalTimeAlfaNumerosExitos.toFloat())
            putInt("total_games_alfanumeros_avanzado", totalGamesAlfaNumerosAvanzado)
            putInt("total_games_alfanumeros_principiante", totalGamesAlfaNumerosPrincipiante)
            putInt("total_games_alfanumeros_pro", totalGamesAlfaNumerosPro)
            putString(KEY_LAST_IQ_COMPONENTS, gson.toJson(lastIqComponentByGame))
        }
    }

    fun saveStatsGlobalAndSumaResta() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
            putInt(KEY_TOTAL_GAMES_SUMARESTA, totalGamesSumaResta)
            putFloat(KEY_TOTAL_TIME_SUMARESTA, totalTimeSumaResta.toFloat())
            putInt(KEY_TOTAL_GAMES_SUMARESTA_EXITOS, totalGamesSumaRestaExitos)
            putFloat(KEY_TOTAL_TIME_SUMARESTA_EXITOS, totalTimeSumaRestaExitos.toFloat())
            putInt("total_games_sumaresta_avanzado", totalGamesSumaRestaAvanzado)
            putInt("total_games_sumaresta_principiante", totalGamesSumaRestaPrincipiante)
            putInt("total_games_sumaresta_pro", totalGamesSumaRestaPro)
            putString(KEY_LAST_IQ_COMPONENTS, gson.toJson(lastIqComponentByGame))
        }
    }

    fun saveStatsGlobalAndMasPlus() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
            putInt(KEY_TOTAL_GAMES_MAS_PLUS, totalGamesMasPlus)
            putFloat(KEY_TOTAL_TIME_MAS_PLUS, totalTimeMasPlus.toFloat())
            putInt(KEY_TOTAL_GAMES_MAS_PLUS_EXITOS, totalGamesMasPlusExitos)
            putFloat(KEY_TOTAL_TIME_MAS_PLUS_EXITOS, totalTimeMasPlusExitos.toFloat())
            putInt("total_games_masplus_avanzado", totalGamesMasPlusAvanzado)
            putInt("total_games_masplus_principiante", totalGamesMasPlusPrincipiante)
            putInt("total_games_masplus_pro", totalGamesMasPlusPro)
            putString(KEY_LAST_IQ_COMPONENTS, gson.toJson(lastIqComponentByGame))
        }
    }

    fun saveStatsGlobalAndGenioPlus() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
            putInt(KEY_TOTAL_GAMES_GENIO_PLUS, totalGamesGenioPlus)
            putFloat(KEY_TOTAL_TIME_GENIO_PLUS, totalTimeGenioPlus.toFloat())
            putInt(KEY_TOTAL_GAMES_GENIO_PLUS_EXITOS, totalGamesGenioPlusExitos)
            putFloat(KEY_TOTAL_TIME_GENIO_PLUS_EXITOS, totalTimeGenioPlusExitos.toFloat())
            putInt("total_games_genioplus_avanzado", totalGamesGenioPlusAvanzado)
            putInt("total_games_genioplus_principiante", totalGamesGenioPlusPrincipiante)
            putInt("total_games_genioplus_pro", totalGamesGenioPlusPro)
            putString(KEY_LAST_IQ_COMPONENTS, gson.toJson(lastIqComponentByGame))
        }
    }


    fun getPrecisionGlobal(): Double = if (totalGamesGlobal > 0) correctGamesGlobal.toDouble() / totalGamesGlobal else 1.0
    fun getTiempoPromedioNumerosPlus(): Double = if (totalGamesNumerosPlusExitos > 0) totalTimeNumerosPlusExitos / totalGamesNumerosPlusExitos else 1.0
    fun getTiempoPromedioDeciPlus(): Double = if (totalGamesDeciPlusExitos > 0) totalTimeDeciPlusExitos / totalGamesDeciPlusExitos else 1.0
    fun getTiempoPromedioRomas(): Double = if (totalGamesRomasExitos > 0) totalTimeRomasExitos / totalGamesRomasExitos else 1.0
    fun getTiempoPromedioAlfaNumeros(): Double = if (totalGamesAlfaNumerosExitos > 0) totalTimeAlfaNumerosExitos / totalGamesAlfaNumerosExitos else 1.0
    fun getTiempoPromedioSumaResta(): Double = if (totalGamesSumaRestaExitos > 0) totalTimeSumaRestaExitos / totalGamesSumaRestaExitos else 1.0
    fun getTiempoPromedioMasPlus(): Double = if (totalGamesMasPlusExitos > 0) totalTimeMasPlusExitos / totalGamesMasPlusExitos else 1.0
    fun getTiempoPromedioGenioPlus(): Double = if (totalGamesGenioPlusExitos > 0) totalTimeGenioPlusExitos / totalGamesGenioPlusExitos else 1.0

    fun getTiempoPromedioGlobal(): Double {
        val totalTiempo = totalTimeNumerosPlusExitos + totalTimeDeciPlusExitos + totalTimeRomasExitos +
                totalTimeAlfaNumerosExitos + totalTimeSumaRestaExitos + totalTimeMasPlusExitos + totalTimeGenioPlusExitos
        val totalJuegos = totalGamesNumerosPlusExitos + totalGamesDeciPlusExitos + totalGamesRomasExitos +
                totalGamesAlfaNumerosExitos + totalGamesSumaRestaExitos + totalGamesMasPlusExitos + totalGamesGenioPlusExitos
        return if (totalJuegos > 0) totalTiempo / totalJuegos else 1.0
    }


    fun addCompletedLevel(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).addCompletedLevel(level)
    fun addCompletedLevelPrincipiante(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).addCompletedLevel(level)
    fun addCompletedLevelPro(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).addCompletedLevel(level)
    fun addCompletedLevelDeciPlus(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).addCompletedLevel(level)
    fun addCompletedLevelDeciPlusPrincipiante(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).addCompletedLevel(level)
    fun addCompletedLevelDeciPlusPro(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).addCompletedLevel(level)
    fun addCompletedLevelRomas(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).addCompletedLevel(level)
    fun addCompletedLevelRomasPrincipiante(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).addCompletedLevel(level)
    fun addCompletedLevelRomasPro(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.PRO).addCompletedLevel(level)
    fun addCompletedLevelAlfaNumeros(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).addCompletedLevel(level)
    fun addCompletedLevelAlfaNumerosPrincipiante(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).addCompletedLevel(level)
    fun addCompletedLevelAlfaNumerosPro(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).addCompletedLevel(level)
    fun addCompletedLevelSumaResta(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).addCompletedLevel(level)
    fun addCompletedLevelSumaRestaPrincipiante(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).addCompletedLevel(level)
    fun addCompletedLevelSumaRestaPro(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).addCompletedLevel(level)
    fun addCompletedLevelMasPlus(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).addCompletedLevel(level)
    fun addCompletedLevelMasPlusPrincipiante(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).addCompletedLevel(level)
    fun addCompletedLevelMasPlusPro(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).addCompletedLevel(level)
    fun addCompletedLevelGenioPlus(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).addCompletedLevel(level)
    fun addCompletedLevelGenioPlusPrincipiante(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).addCompletedLevel(level)
    fun addCompletedLevelGenioPlusPro(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).addCompletedLevel(level)


    fun hasCompletedLevel(level: Int): Boolean = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).hasCompletedLevel(level)
    fun hasCompletedLevelPrincipiante(level: Int): Boolean = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).hasCompletedLevel(level)
    fun hasCompletedLevelPro(level: Int): Boolean = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).hasCompletedLevel(level)
    fun hasCompletedLevelDeciPlus(level: Int): Boolean = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).hasCompletedLevel(level)
    fun hasCompletedLevelDeciPlusPrincipiante(level: Int): Boolean = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).hasCompletedLevel(level)
    fun hasCompletedLevelDeciPlusPro(level: Int): Boolean = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).hasCompletedLevel(level)
    fun hasCompletedLevelRomas(level: Int): Boolean = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).hasCompletedLevel(level)
    fun hasCompletedLevelRomasPrincipiante(level: Int): Boolean = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).hasCompletedLevel(level)
    fun hasCompletedLevelRomasPro(level: Int): Boolean = getOrCreateManager(Game.ROMAS, Difficulty.PRO).hasCompletedLevel(level)
    fun hasCompletedLevelAlfaNumeros(level: Int): Boolean = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).hasCompletedLevel(level)
    fun hasCompletedLevelAlfaNumerosPrincipiante(level: Int): Boolean = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).hasCompletedLevel(level)
    fun hasCompletedLevelAlfaNumerosPro(level: Int): Boolean = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).hasCompletedLevel(level)
    fun hasCompletedLevelSumaResta(level: Int): Boolean = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).hasCompletedLevel(level)
    fun hasCompletedLevelSumaRestaPrincipiante(level: Int): Boolean = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).hasCompletedLevel(level)
    fun hasCompletedLevelSumaRestaPro(level: Int): Boolean = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).hasCompletedLevel(level)
    fun hasCompletedLevelMasPlus(level: Int): Boolean = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).hasCompletedLevel(level)
    fun hasCompletedLevelMasPlusPrincipiante(level: Int): Boolean = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).hasCompletedLevel(level)
    fun hasCompletedLevelMasPlusPro(level: Int): Boolean = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).hasCompletedLevel(level)
    fun hasCompletedLevelGenioPlus(level: Int): Boolean = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).hasCompletedLevel(level)
    fun hasCompletedLevelGenioPlusPrincipiante(level: Int): Boolean = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).hasCompletedLevel(level)
    fun hasCompletedLevelGenioPlusPro(level: Int): Boolean = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).hasCompletedLevel(level)


    fun getUniqueLevelsPlayedNumerosPlusPrincipiante(): Int = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).getCompletedLevels().size
    fun getUniqueLevelsPlayedNumerosPlusAvanzado(): Int = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).getCompletedLevels().size
    fun getUniqueLevelsPlayedNumerosPlusPro(): Int = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).getCompletedLevels().size
    fun getUniqueLevelsPlayedDeciPlusPrincipiante(): Int = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).getCompletedLevels().size
    fun getUniqueLevelsPlayedDeciPlusAvanzado(): Int = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).getCompletedLevels().size
    fun getUniqueLevelsPlayedDeciPlusPro(): Int = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).getCompletedLevels().size
    fun getUniqueLevelsPlayedRomasPrincipiante(): Int = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).getCompletedLevels().size
    fun getUniqueLevelsPlayedRomasAvanzado(): Int = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).getCompletedLevels().size
    fun getUniqueLevelsPlayedRomasPro(): Int = getOrCreateManager(Game.ROMAS, Difficulty.PRO).getCompletedLevels().size
    fun getUniqueLevelsPlayedAlfaNumerosPrincipiante(): Int = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).getCompletedLevels().size
    fun getUniqueLevelsPlayedAlfaNumerosAvanzado(): Int = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).getCompletedLevels().size
    fun getUniqueLevelsPlayedAlfaNumerosPro(): Int = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).getCompletedLevels().size
    fun getUniqueLevelsPlayedSumaRestaPrincipiante(): Int = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).getCompletedLevels().size
    fun getUniqueLevelsPlayedSumaRestaAvanzado(): Int = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).getCompletedLevels().size
    fun getUniqueLevelsPlayedSumaRestaPro(): Int = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).getCompletedLevels().size
    fun getUniqueLevelsPlayedMasPlusPrincipiante(): Int = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).getCompletedLevels().size
    fun getUniqueLevelsPlayedMasPlusAvanzado(): Int = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).getCompletedLevels().size
    fun getUniqueLevelsPlayedMasPlusPro(): Int = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).getCompletedLevels().size
    fun getUniqueLevelsPlayedGenioPlusPrincipiante(): Int = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).getCompletedLevels().size
    fun getUniqueLevelsPlayedGenioPlusAvanzado(): Int = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).getCompletedLevels().size
    fun getUniqueLevelsPlayedGenioPlusPro(): Int = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).getCompletedLevels().size


    fun getMissingLevelsNumerosPlusPrincipiante(): Int = (12 - getUniqueLevelsPlayedNumerosPlusPrincipiante()).coerceAtLeast(0)
    fun getMissingLevelsNumerosPlusAvanzado(): Int = (12 - getUniqueLevelsPlayedNumerosPlusAvanzado()).coerceAtLeast(0)
    fun getMissingLevelsNumerosPlusPro(): Int = (12 - getUniqueLevelsPlayedNumerosPlusPro()).coerceAtLeast(0)
    fun getMissingLevelsDeciPlusPrincipiante(): Int = (12 - getUniqueLevelsPlayedDeciPlusPrincipiante()).coerceAtLeast(0)
    fun getMissingLevelsDeciPlusAvanzado(): Int = (12 - getUniqueLevelsPlayedDeciPlusAvanzado()).coerceAtLeast(0)
    fun getMissingLevelsDeciPlusPro(): Int = (12 - getUniqueLevelsPlayedDeciPlusPro()).coerceAtLeast(0)
    fun getMissingLevelsRomasPrincipiante(): Int = (12 - getUniqueLevelsPlayedRomasPrincipiante()).coerceAtLeast(0)
    fun getMissingLevelsRomasAvanzado(): Int = (12 - getUniqueLevelsPlayedRomasAvanzado()).coerceAtLeast(0)
    fun getMissingLevelsRomasPro(): Int = (12 - getUniqueLevelsPlayedRomasPro()).coerceAtLeast(0)
    fun getMissingLevelsAlfaNumerosPrincipiante(): Int = (12 - getUniqueLevelsPlayedAlfaNumerosPrincipiante()).coerceAtLeast(0)
    fun getMissingLevelsAlfaNumerosAvanzado(): Int = (12 - getUniqueLevelsPlayedAlfaNumerosAvanzado()).coerceAtLeast(0)
    fun getMissingLevelsAlfaNumerosPro(): Int = (12 - getUniqueLevelsPlayedAlfaNumerosPro()).coerceAtLeast(0)
    fun getMissingLevelsSumaRestaPrincipiante(): Int = (12 - getUniqueLevelsPlayedSumaRestaPrincipiante()).coerceAtLeast(0)
    fun getMissingLevelsSumaRestaAvanzado(): Int = (12 - getUniqueLevelsPlayedSumaRestaAvanzado()).coerceAtLeast(0)
    fun getMissingLevelsSumaRestaPro(): Int = (12 - getUniqueLevelsPlayedSumaRestaPro()).coerceAtLeast(0)
    fun getMissingLevelsMasPlusPrincipiante(): Int = (12 - getUniqueLevelsPlayedMasPlusPrincipiante()).coerceAtLeast(0)
    fun getMissingLevelsMasPlusAvanzado(): Int = (12 - getUniqueLevelsPlayedMasPlusAvanzado()).coerceAtLeast(0)
    fun getMissingLevelsMasPlusPro(): Int = (12 - getUniqueLevelsPlayedMasPlusPro()).coerceAtLeast(0)
    fun getMissingLevelsGenioPlusPrincipiante(): Int = (12 - getUniqueLevelsPlayedGenioPlusPrincipiante()).coerceAtLeast(0)
    fun getMissingLevelsGenioPlusAvanzado(): Int = (12 - getUniqueLevelsPlayedGenioPlusAvanzado()).coerceAtLeast(0)
    fun getMissingLevelsGenioPlusPro(): Int = (12 - getUniqueLevelsPlayedGenioPlusPro()).coerceAtLeast(0)


    fun isEligibleForSpeedRankingNumerosPlus(): Boolean =
        getUniqueLevelsPlayedNumerosPlusPrincipiante() >= 12 &&
                getUniqueLevelsPlayedNumerosPlusAvanzado() >= 12 &&
                getUniqueLevelsPlayedNumerosPlusPro() >= 12

    fun isEligibleForSpeedRankingDeciPlus(): Boolean =
        getUniqueLevelsPlayedDeciPlusPrincipiante() >= 12 &&
                getUniqueLevelsPlayedDeciPlusAvanzado() >= 12 &&
                getUniqueLevelsPlayedDeciPlusPro() >= 12

    fun isEligibleForSpeedRankingRomas(): Boolean =
        getUniqueLevelsPlayedRomasPrincipiante() >= 12 &&
                getUniqueLevelsPlayedRomasAvanzado() >= 12 &&
                getUniqueLevelsPlayedRomasPro() >= 12

    fun isEligibleForSpeedRankingAlfaNumeros(): Boolean =
        getUniqueLevelsPlayedAlfaNumerosPrincipiante() >= 12 &&
                getUniqueLevelsPlayedAlfaNumerosAvanzado() >= 12 &&
                getUniqueLevelsPlayedAlfaNumerosPro() >= 12

    fun isEligibleForSpeedRankingSumaResta(): Boolean =
        getUniqueLevelsPlayedSumaRestaPrincipiante() >= 12 &&
                getUniqueLevelsPlayedSumaRestaAvanzado() >= 12 &&
                getUniqueLevelsPlayedSumaRestaPro() >= 12

    fun isEligibleForSpeedRankingMasPlus(): Boolean =
        getUniqueLevelsPlayedMasPlusPrincipiante() >= 12 &&
                getUniqueLevelsPlayedMasPlusAvanzado() >= 12 &&
                getUniqueLevelsPlayedMasPlusPro() >= 12

    fun isEligibleForSpeedRankingGenioPlus(): Boolean =
        getUniqueLevelsPlayedGenioPlusPrincipiante() >= 12 &&
                getUniqueLevelsPlayedGenioPlusAvanzado() >= 12 &&
                getUniqueLevelsPlayedGenioPlusPro() >= 12


    fun reset() = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).reset()
    fun resetPrincipiante() = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).reset()
    fun resetPro() = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).reset()
    fun resetDeciPlus() = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).reset()
    fun resetDeciPlusPrincipiante() = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).reset()
    fun resetDeciPlusPro() = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).reset()
    fun resetRomas() = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).reset()
    fun resetRomasPrincipiante() = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).reset()
    fun resetRomasPro() = getOrCreateManager(Game.ROMAS, Difficulty.PRO).reset()
    fun resetAlfaNumeros() = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).reset()
    fun resetAlfaNumerosPrincipiante() = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).reset()
    fun resetAlfaNumerosPro() = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).reset()
    fun resetSumaResta() = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).reset()
    fun resetSumaRestaPrincipiante() = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).reset()
    fun resetSumaRestaPro() = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).reset()
    fun resetMasPlus() = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).reset()
    fun resetMasPlusPrincipiante() = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).reset()
    fun resetMasPlusPro() = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).reset()
    fun resetGenioPlus() = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).reset()
    fun resetGenioPlusPrincipiante() = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).reset()
    fun resetGenioPlusPro() = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).reset()


    fun incrementConsecutiveFailures(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailures(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).resetConsecutiveFailures(level)
    fun getConsecutiveFailures(level: Int): Int = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailures(level: Int): Boolean = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.AVANZADO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresPrincipiante(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresPrincipiante(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresPrincipiante(level: Int): Int = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresPrincipiante(level: Int): Boolean = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRINCIPIANTE).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresPro(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresPro(level: Int) = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresPro(level: Int): Int = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresPro(level: Int): Boolean = getOrCreateManager(Game.NUMEROS_PLUS, Difficulty.PRO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresDeciPlus(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresDeciPlus(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresDeciPlus(level: Int): Int = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresDeciPlus(level: Int): Boolean = getOrCreateManager(Game.DECI_PLUS, Difficulty.AVANZADO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresDeciPlusPrincipiante(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresDeciPlusPrincipiante(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresDeciPlusPrincipiante(level: Int): Int = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresDeciPlusPrincipiante(level: Int): Boolean = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRINCIPIANTE).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresDeciPlusPro(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresDeciPlusPro(level: Int) = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresDeciPlusPro(level: Int): Int = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresDeciPlusPro(level: Int): Boolean = getOrCreateManager(Game.DECI_PLUS, Difficulty.PRO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresRomas(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresRomas(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresRomas(level: Int): Int = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresRomas(level: Int): Boolean = getOrCreateManager(Game.ROMAS, Difficulty.AVANZADO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresRomasPrincipiante(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresRomasPrincipiante(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresRomasPrincipiante(level: Int): Int = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresRomasPrincipiante(level: Int): Boolean = getOrCreateManager(Game.ROMAS, Difficulty.PRINCIPIANTE).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresRomasPro(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.PRO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresRomasPro(level: Int) = getOrCreateManager(Game.ROMAS, Difficulty.PRO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresRomasPro(level: Int): Int = getOrCreateManager(Game.ROMAS, Difficulty.PRO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresRomasPro(level: Int): Boolean = getOrCreateManager(Game.ROMAS, Difficulty.PRO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresAlfaNumeros(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresAlfaNumeros(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresAlfaNumeros(level: Int): Int = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresAlfaNumeros(level: Int): Boolean = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.AVANZADO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresAlfaNumerosPrincipiante(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresAlfaNumerosPrincipiante(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresAlfaNumerosPrincipiante(level: Int): Int = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresAlfaNumerosPrincipiante(level: Int): Boolean = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRINCIPIANTE).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresAlfaNumerosPro(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresAlfaNumerosPro(level: Int) = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresAlfaNumerosPro(level: Int): Int = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresAlfaNumerosPro(level: Int): Boolean = getOrCreateManager(Game.ALFA_NUMEROS, Difficulty.PRO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresSumaResta(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresSumaResta(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresSumaResta(level: Int): Int = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresSumaResta(level: Int): Boolean = getOrCreateManager(Game.SUMA_RESTA, Difficulty.AVANZADO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresSumaRestaPrincipiante(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresSumaRestaPrincipiante(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresSumaRestaPrincipiante(level: Int): Int = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresSumaRestaPrincipiante(level: Int): Boolean = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRINCIPIANTE).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresSumaRestaPro(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresSumaRestaPro(level: Int) = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresSumaRestaPro(level: Int): Int = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresSumaRestaPro(level: Int): Boolean = getOrCreateManager(Game.SUMA_RESTA, Difficulty.PRO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresMasPlus(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresMasPlus(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresMasPlus(level: Int): Int = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresMasPlus(level: Int): Boolean = getOrCreateManager(Game.MAS_PLUS, Difficulty.AVANZADO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresMasPlusPrincipiante(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresMasPlusPrincipiante(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresMasPlusPrincipiante(level: Int): Int = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresMasPlusPrincipiante(level: Int): Boolean = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRINCIPIANTE).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresMasPlusPro(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresMasPlusPro(level: Int) = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresMasPlusPro(level: Int): Int = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresMasPlusPro(level: Int): Boolean = getOrCreateManager(Game.MAS_PLUS, Difficulty.PRO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresGenioPlus(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresGenioPlus(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresGenioPlus(level: Int): Int = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresGenioPlus(level: Int): Boolean = getOrCreateManager(Game.GENIO_PLUS, Difficulty.AVANZADO).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresGenioPlusPrincipiante(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresGenioPlusPrincipiante(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresGenioPlusPrincipiante(level: Int): Int = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresGenioPlusPrincipiante(level: Int): Boolean = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRINCIPIANTE).isLevelBlockedByFailures(level)

    fun incrementConsecutiveFailuresGenioPlusPro(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).incrementConsecutiveFailures(level)
    fun resetConsecutiveFailuresGenioPlusPro(level: Int) = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).resetConsecutiveFailures(level)
    fun getConsecutiveFailuresGenioPlusPro(level: Int): Int = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).getConsecutiveFailures(level)
    fun isLevelBlockedByFailuresGenioPlusPro(level: Int): Boolean = getOrCreateManager(Game.GENIO_PLUS, Difficulty.PRO).isLevelBlockedByFailures(level)


    private fun isEligibleForGlobalRanking(): Boolean =
        getTotalUniqueLevelsCompletedAllGames() >= RankingActivity.MIN_LEVELS_REQUIRED

    private fun isEligibleSpeedNumerosPlus() = isEligibleForSpeedRankingNumerosPlus()
    private fun isEligibleSpeedDeciPlus() = isEligibleForSpeedRankingDeciPlus()
    private fun isEligibleSpeedAlfaNumeros() = isEligibleForSpeedRankingAlfaNumeros()
    private fun isEligibleSpeedRomas() = isEligibleForSpeedRankingRomas()
    private fun isEligibleSpeedSumaResta() = isEligibleForSpeedRankingSumaResta()
    private fun isEligibleSpeedMasPlus() = isEligibleForSpeedRankingMasPlus()
    private fun isEligibleSpeedGenioPlus() = isEligibleForSpeedRankingGenioPlus()

    fun isEligibleIQPlusRanking(): Boolean = haJugadoAlMenosUnNivelEnCadaJuegoYGrado()

    fun getRankingList(rankingName: String): List<RankingEntry> {
        val me = preferences.getString("savedUserName", "User")!!
        return when (rankingName) {
            "GLOBAL" -> if (isEligibleForGlobalRanking()) listOf(RankingEntry(me, 1.0)) else emptyList()
            "VEL_NUMEROS" -> if (isEligibleSpeedNumerosPlus()) listOf(RankingEntry(me, 1.0)) else emptyList()
            "VEL_DECI" -> if (isEligibleSpeedDeciPlus()) listOf(RankingEntry(me, 1.0)) else emptyList()
            "VEL_ALFANUM" -> if (isEligibleSpeedAlfaNumeros()) listOf(RankingEntry(me, 1.0)) else emptyList()
            "VEL_ROMAS" -> if (isEligibleSpeedRomas()) listOf(RankingEntry(me, 1.0)) else emptyList()
            "VEL_SUMARESTA" -> if (isEligibleSpeedSumaResta()) listOf(RankingEntry(me, 1.0)) else emptyList()
            "VEL_MAS" -> if (isEligibleSpeedMasPlus()) listOf(RankingEntry(me, 1.0)) else emptyList()
            "VEL_GENIOS" -> if (isEligibleSpeedGenioPlus()) listOf(RankingEntry(me, 1.0)) else emptyList()
            "IQ_PLUS" -> if (isEligibleIQPlusRanking()) listOf(RankingEntry(me, 1.0)) else emptyList()
            else -> emptyList()
        }
    }

    fun getUserPositionInRanking(rankingName: String): Int {
        val me = preferences.getString("savedUserName", "User")!!
        return getRankingList(rankingName).indexOfFirst { it.userName == me }.let { if (it >= 0) it + 1 else -1 }
    }

    fun isUserInRanking(rankingName: String): Boolean = getUserPositionInRanking(rankingName) > 0

    fun getTotalUniqueLevelsCompletedAllGames(): Int {
        return getUniqueLevelsPlayedNumerosPlusPrincipiante() +
                getUniqueLevelsPlayedNumerosPlusAvanzado() +
                getUniqueLevelsPlayedNumerosPlusPro() +
                getUniqueLevelsPlayedDeciPlusPrincipiante() +
                getUniqueLevelsPlayedDeciPlusAvanzado() +
                getUniqueLevelsPlayedDeciPlusPro() +
                getUniqueLevelsPlayedRomasPrincipiante() +
                getUniqueLevelsPlayedRomasAvanzado() +
                getUniqueLevelsPlayedRomasPro() +
                getUniqueLevelsPlayedAlfaNumerosPrincipiante() +
                getUniqueLevelsPlayedAlfaNumerosAvanzado() +
                getUniqueLevelsPlayedAlfaNumerosPro() +
                getUniqueLevelsPlayedSumaRestaPrincipiante() +
                getUniqueLevelsPlayedSumaRestaAvanzado() +
                getUniqueLevelsPlayedSumaRestaPro() +
                getUniqueLevelsPlayedMasPlusPrincipiante() +
                getUniqueLevelsPlayedMasPlusAvanzado() +
                getUniqueLevelsPlayedMasPlusPro() +
                getUniqueLevelsPlayedGenioPlusPrincipiante() +
                getUniqueLevelsPlayedGenioPlusAvanzado() +
                getUniqueLevelsPlayedGenioPlusPro()
    }

    fun haJugadoAlMenosUnNivelEnCadaJuegoYGrado(): Boolean {
        return getUniqueLevelsPlayedNumerosPlusPrincipiante() > 0 &&
                getUniqueLevelsPlayedNumerosPlusAvanzado() > 0 &&
                getUniqueLevelsPlayedNumerosPlusPro() > 0 &&
                getUniqueLevelsPlayedDeciPlusPrincipiante() > 0 &&
                getUniqueLevelsPlayedDeciPlusAvanzado() > 0 &&
                getUniqueLevelsPlayedDeciPlusPro() > 0 &&
                getUniqueLevelsPlayedRomasPrincipiante() > 0 &&
                getUniqueLevelsPlayedRomasAvanzado() > 0 &&
                getUniqueLevelsPlayedRomasPro() > 0 &&
                getUniqueLevelsPlayedAlfaNumerosPrincipiante() > 0 &&
                getUniqueLevelsPlayedAlfaNumerosAvanzado() > 0 &&
                getUniqueLevelsPlayedAlfaNumerosPro() > 0 &&
                getUniqueLevelsPlayedSumaRestaPrincipiante() > 0 &&
                getUniqueLevelsPlayedSumaRestaAvanzado() > 0 &&
                getUniqueLevelsPlayedSumaRestaPro() > 0 &&
                getUniqueLevelsPlayedMasPlusPrincipiante() > 0 &&
                getUniqueLevelsPlayedMasPlusAvanzado() > 0 &&
                getUniqueLevelsPlayedMasPlusPro() > 0 &&
                getUniqueLevelsPlayedGenioPlusPrincipiante() > 0 &&
                getUniqueLevelsPlayedGenioPlusAvanzado() > 0 &&
                getUniqueLevelsPlayedGenioPlusPro() > 0
    }

    fun updateIqComponent(juego: String, grado: String, valor: Double) {
        lastIqComponentByGame["${juego}_${grado}"] = valor
    }

    fun getMaxLevelForCombo(juego: String, grado: String): Int {
        val game = when (juego) {
            "NumerosPlus" -> Game.NUMEROS_PLUS
            "DeciPlus" -> Game.DECI_PLUS
            "Romas" -> Game.ROMAS
            "AlfaNumeros" -> Game.ALFA_NUMEROS
            "SumaResta" -> Game.SUMA_RESTA
            "MasPlus" -> Game.MAS_PLUS
            "GenioPlus" -> Game.GENIO_PLUS
            else -> return 0
        }
        val difficulty = when (grado) {
            "Principiante" -> Difficulty.PRINCIPIANTE
            "Avanzado" -> Difficulty.AVANZADO
            "Pro" -> Difficulty.PRO
            else -> return 0
        }
        return getOrCreateManager(game, difficulty).getCompletedLevels().maxOrNull() ?: 0
    }

    fun hasCompleted12LevelsInAnyGame(): Boolean {
        return getTotalUniqueLevelsCompletedAllGames() >= 12
    }

    fun isRankedInAtLeastOneGame(): Boolean {
        val rankingNames = listOf(
            "GLOBAL", "VEL_NUMEROS", "VEL_DECI", "VEL_ALFANUM", "VEL_ROMAS",
            "VEL_SUMARESTA", "VEL_MAS", "VEL_GENIOS", "IQ_PLUS"
        )
        return rankingNames.any { isUserInRanking(it) }
    }

    // ====== SYNC: EXPORT / IMPORT PARA SCORE MANAGER======

    private data class TypedPrefValue(
        val type: String,
        val value: Any?
    )

    fun exportAllDataAsJson(context: Context): String {
        ensurePreferencesInitialized(context)
        val gson = Gson()

        val scorePrefsNames = listOf(
            // NumerosPlus
            "ScorePrefs", "ScorePrefsPrincipiante", "ScorePrefsPro",
            // DeciPlus
            "ScorePrefsDeciPlus", "ScorePrefsDeciPlusPrincipiante", "ScorePrefsDeciPlusPro",
            // Romas
            "ScorePrefsRomas", "ScorePrefsRomasPrincipiante", "ScorePrefsRomasPro",
            // AlfaNumeros
            "ScorePrefsAlfaNumeros", "ScorePrefsAlfaNumerosPrincipiante", "ScorePrefsAlfaNumerosPro",
            // SumaResta
            "ScorePrefsSumaResta", "ScorePrefsSumaRestaPrincipiante", "ScorePrefsSumaRestaPro",
            // MasPlus
            "ScorePrefsMasPlus", "ScorePrefsMasPlusPrincipiante", "ScorePrefsMasPlusPro",
            // GenioPlus
            "ScorePrefsGenioPlus", "ScorePrefsGenioPlusPrincipiante", "ScorePrefsGenioPlusPro"
        )

        fun dumpPrefs(name: String): Map<String, TypedPrefValue> {
            val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return sp.all.mapNotNull { (k, v) ->
                when (v) {
                    is Int    -> k to TypedPrefValue("int", v)
                    is Long   -> k to TypedPrefValue("long", v)
                    is Float  -> k to TypedPrefValue("float", v)
                    is Boolean-> k to TypedPrefValue("boolean", v)
                    is String -> k to TypedPrefValue("string", v)
                    is Set<*> -> {
                        val onlyStrings = v.all { it is String }
                        if (onlyStrings) k to TypedPrefValue("string_set", v.toList())
                        else null
                    }
                    else -> null
                }
            }.toMap()
        }

        val prefsDump = mutableMapOf<String, Map<String, TypedPrefValue>>()
        scorePrefsNames.forEach { name ->
            prefsDump[name] = dumpPrefs(name)
        }

        val allowedPrefixes = listOf("total_games_", "total_time_")
        val allowedExactKeys = listOf(
            KEY_TOTAL_GAMES_GLOBAL, KEY_CORRECT_GAMES_GLOBAL, KEY_LAST_IQ_COMPONENTS
        )

        val myPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val mySubset = myPrefs.all.mapNotNull { (k, v) ->
            val keep = allowedExactKeys.contains(k) || allowedPrefixes.any { k.startsWith(it) }
            if (!keep) return@mapNotNull null
            when (v) {
                is Int    -> k to TypedPrefValue("int", v)
                is Long   -> k to TypedPrefValue("long", v)
                is Float  -> k to TypedPrefValue("float", v)
                is Boolean-> k to TypedPrefValue("boolean", v)
                is String -> k to TypedPrefValue("string", v)
                is Set<*> -> {
                    val onlyStrings = v.all { it is String }
                    if (onlyStrings) k to TypedPrefValue("string_set", v.toList())
                    else null
                }
                else -> null
            }
        }.toMap()

        val root = mapOf(
            "schema_version" to 2,
            "updated_at" to System.currentTimeMillis(),
            "prefs_dump" to prefsDump,
            "my_prefs_subset" to mySubset
        )

        return gson.toJson(root)
    }

    @Suppress("UNCHECKED_CAST")
    fun importAllDataFromJson(context: Context, json: String) {
        ensurePreferencesInitialized(context)
        val gson = Gson()
        val rootObj = gson.fromJson(json, JsonObject::class.java) ?: return

        fun applyMapToPrefs(prefsName: String, map: Map<String, Any?>) {
            val sp = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            sp.edit {
                map.forEach { (key, tvRaw) ->
                    val typed = tvRaw as? Map<*, *> ?: return@forEach
                    val type = typed["type"] as? String ?: return@forEach
                    val value = typed["value"]

                    when (type) {
                        "int" -> (value as? Number)?.toInt()?.let { putInt(key, it) }
                        "long" -> (value as? Number)?.toLong()?.let { putLong(key, it) }
                        "float" -> (value as? Number)?.toFloat()?.let { putFloat(key, it) }
                        "boolean" -> (value as? Boolean)?.let { putBoolean(key, it) }
                        "string" -> (value as? String)?.let { putString(key, it) }
                        "string_set" -> {
                            val list = (value as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                            putStringSet(key, list.toSet())
                        }
                    }
                }
            }
        }

        rootObj.getAsJsonObject("prefs_dump")?.entrySet()?.forEach { entry ->
            val prefsName = entry.key
            val map = gson.fromJson<Map<String, Any?>>(
                entry.value, object : TypeToken<Map<String, Any?>>() {}.type
            )
            applyMapToPrefs(prefsName, map)
        }

        rootObj.getAsJsonObject("my_prefs_subset")?.let { jo ->
            val map = gson.fromJson<Map<String, Any?>>(
                jo, object : TypeToken<Map<String, Any?>>() {}.type
            )
            applyMapToPrefs("MyPrefs", map)
        }
    }


}