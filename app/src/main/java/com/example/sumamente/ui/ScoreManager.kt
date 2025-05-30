package com.example.sumamente.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object ScoreManager {

    lateinit var preferences: SharedPreferences

    fun ensurePreferencesInitialized(context: Context) {
        if (!::preferences.isInitialized) {
            preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        }
    }

    //Constantes globales//
    private const val KEY_TOTAL_GAMES_GLOBAL = "total_games_global"
    private const val KEY_CORRECT_GAMES_GLOBAL = "correct_games_global"


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


    var totalGamesGlobal: Int = 0
    var correctGamesGlobal: Int = 0

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

    // aqui estaba la variable private lateinit var preferences: SharedPreferences //

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


    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentScore = preferences.getInt(KEY_CURRENT_SCORE, 0)
        unlockedLevels = preferences.getInt(KEY_UNLOCKED_LEVELS, 2)

        for (i in 1..70) {
            val failures = preferences.getInt("$KEY_CONSECUTIVE_FAILURES:$i", 0)
            if (failures > 0) {
                consecutiveFailures[i] = failures
            }
        }
        totalGamesNumerosPlusAvanzado = preferences.getInt("total_games_numeros_plus_avanzado", 0)
        totalGamesNumerosPlusPrincipiante = preferences.getInt("total_games_numeros_plus_principiante", 0)
        totalGamesNumerosPlusPro = preferences.getInt("total_games_numeros_plus_pro", 0)

        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesNumerosPlus = preferences.getInt(KEY_TOTAL_GAMES_NUMEROS_PLUS, 0)
        totalTimeNumerosPlus = preferences.getFloat(KEY_TOTAL_TIME_NUMEROS_PLUS, 0f).toDouble()
    }
    fun initPrincipiante(context: Context) {
        preferencesPrincipiante = context.getSharedPreferences(PREFS_NAME_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScorePrincipiante = preferencesPrincipiante.getInt(KEY_CURRENT_SCORE_PRINCIPIANTE, 0)
        unlockedLevelsPrincipiante = preferencesPrincipiante.getInt(KEY_UNLOCKED_LEVELS_PRINCIPIANTE, 2)

        for (i in 1..70) {
            val failures = preferencesPrincipiante.getInt("$KEY_CONSECUTIVE_FAILURES_PRINCIPIANTE:$i", 0)
            if (failures > 0) {
                consecutiveFailuresPrincipiante[i] = failures
            }
        }
    }
    fun initPro(context: Context) {
        preferencesPro = context.getSharedPreferences(PREFS_NAME_PRO, Context.MODE_PRIVATE)
        currentScorePro = preferencesPro.getInt(KEY_CURRENT_SCORE_PRO, 0)
        unlockedLevelsPro = preferencesPro.getInt(KEY_UNLOCKED_LEVELS_PRO, 2)

        for (i in 1..70) {
            val failures = preferencesPro.getInt("$KEY_CONSECUTIVE_FAILURES_PRO:$i", 0)
            if (failures > 0) {
                consecutiveFailuresPro[i] = failures
            }
        }
    }


    fun initDeciPlus(context: Context) {
        preferencesDeciPlus = context.getSharedPreferences(PREFS_NAME_DECI_PLUS, Context.MODE_PRIVATE)
        currentScoreDeciPlus = preferencesDeciPlus.getInt(KEY_CURRENT_SCORE_DECI_PLUS, 0)
        unlockedLevelsDeciPlus = preferencesDeciPlus.getInt(KEY_UNLOCKED_LEVELS_DECI_PLUS, 2)

        for (i in 1..70) {
            val failures = preferencesDeciPlus.getInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS:$i", 0)
            if (failures > 0) {
                consecutiveFailuresDeciPlus[i] = failures
            }
        }
        totalGamesDeciPlusAvanzado = preferences.getInt("total_games_deci_plus_avanzado", 0)
        totalGamesDeciPlusPrincipiante = preferences.getInt("total_games_deci_plus_principiante", 0)
        totalGamesDeciPlusPro = preferences.getInt("total_games_deci_plus_pro", 0)

        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesDeciPlus = preferencesDeciPlus.getInt(KEY_TOTAL_GAMES_DECI_PLUS, 0)
        totalTimeDeciPlus = preferencesDeciPlus.getFloat(KEY_TOTAL_TIME_DECI_PLUS, 0f).toDouble()
    }
    fun initDeciPlusPrincipiante(context: Context) {
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
        preferencesRomas = context.getSharedPreferences(PREFS_NAME_ROMAS, Context.MODE_PRIVATE)
        currentScoreRomas = preferencesRomas.getInt(KEY_CURRENT_SCORE_ROMAS, 0)
        unlockedLevelsRomas = preferencesRomas.getInt(KEY_UNLOCKED_LEVELS_ROMAS, 2)

        for (i in 1..70) {
            val failures = preferencesRomas.getInt("$KEY_CONSECUTIVE_FAILURES_ROMAS:$i", 0)
            if (failures > 0) {
                consecutiveFailuresRomas[i] = failures
            }
        }
        totalGamesRomasAvanzado = preferences.getInt("total_games_romas_avanzado", 0)
        totalGamesRomasPrincipiante = preferences.getInt("total_games_romas_principiante", 0)
        totalGamesRomasPro = preferences.getInt("total_games_romas_pro", 0)

        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesRomas = preferencesRomas.getInt(KEY_TOTAL_GAMES_ROMAS, 0)
        totalTimeRomas = preferencesRomas.getFloat(KEY_TOTAL_TIME_ROMAS, 0f).toDouble()
    }
    fun initRomasPrincipiante(context: Context) {
        preferencesRomasPrincipiante = context.getSharedPreferences(PREFS_NAME_ROMAS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreRomasPrincipiante = preferencesRomasPrincipiante.getInt(KEY_CURRENT_SCORE_ROMAS_PRINCIPIANTE, 0)
        unlockedLevelsRomasPrincipiante = preferencesRomasPrincipiante.getInt(KEY_UNLOCKED_LEVELS_ROMAS_PRINCIPIANTE, 2)

        for (i in 1..70) {
            val failures = preferencesRomasPrincipiante.getInt("$KEY_CONSECUTIVE_FAILURES_ROMAS_PRINCIPIANTE:$i", 0)
            if (failures > 0) {
                consecutiveFailuresRomasPrincipiante[i] = failures
            }
        }
    }
    fun initRomasPro(context: Context) {
        preferencesRomasPro = context.getSharedPreferences(PREFS_NAME_ROMAS_PRO, Context.MODE_PRIVATE)
        currentScoreRomasPro = preferencesRomasPro.getInt(KEY_CURRENT_SCORE_ROMAS_PRO, 0)
        unlockedLevelsRomasPro = preferencesRomasPro.getInt(KEY_UNLOCKED_LEVELS_ROMAS_PRO, 2)

        for (i in 1..70) {
            val failures = preferencesRomasPro.getInt("$KEY_CONSECUTIVE_FAILURES_ROMAS_PRO:$i", 0)
            if (failures > 0) {
                consecutiveFailuresRomasPro[i] = failures
            }
        }
    }


    fun initAlfaNumeros(context: Context) {
        preferencesAlfaNumeros = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS, Context.MODE_PRIVATE)
        currentScoreAlfaNumeros = preferencesAlfaNumeros.getInt(KEY_CURRENT_SCORE_ALFANUMEROS, 0)
        unlockedLevelsAlfaNumeros = preferencesAlfaNumeros.getInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS, 2)

        for (i in 1..70) {
            val failures = preferencesAlfaNumeros.getInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS:$i", 0)
            if (failures > 0) {
                consecutiveFailuresAlfaNumeros[i] = failures
            }
        }
        totalGamesAlfaNumerosAvanzado = preferences.getInt("total_games_alfanumeros_avanzado", 0)
        totalGamesAlfaNumerosPrincipiante = preferences.getInt("total_games_alfanumeros_principiante", 0)
        totalGamesAlfaNumerosPro = preferences.getInt("total_games_alfanumeros_pro", 0)

        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesAlfaNumeros = preferencesAlfaNumeros.getInt(KEY_TOTAL_GAMES_ALFANUMEROS, 0)
        totalTimeAlfaNumeros = preferencesAlfaNumeros.getFloat(KEY_TOTAL_TIME_ALFANUMEROS, 0f).toDouble()
    }
    fun initAlfaNumerosPrincipiante(context: Context) {
        preferencesAlfaNumerosPrincipiante = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreAlfaNumerosPrincipiante = preferencesAlfaNumerosPrincipiante.getInt(KEY_CURRENT_SCORE_ALFANUMEROS_PRINCIPIANTE, 0)
        unlockedLevelsAlfaNumerosPrincipiante = preferencesAlfaNumerosPrincipiante.getInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRINCIPIANTE, 2)

        for (i in 1..70) {
            val failures = preferencesAlfaNumerosPrincipiante.getInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRINCIPIANTE:$i", 0)
            if (failures > 0) {
                consecutiveFailuresAlfaNumerosPrincipiante[i] = failures
            }
        }
    }
    fun initAlfaNumerosPro(context: Context) {
        preferencesAlfaNumerosPro = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS_PRO, Context.MODE_PRIVATE)
        currentScoreAlfaNumerosPro = preferencesAlfaNumerosPro.getInt(KEY_CURRENT_SCORE_ALFANUMEROS_PRO, 0)
        unlockedLevelsAlfaNumerosPro = preferencesAlfaNumerosPro.getInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRO, 2)
        for (i in 1..70) {
            val failures = preferencesAlfaNumerosPro.getInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRO:$i", 0)
            if (failures > 0) consecutiveFailuresAlfaNumerosPro[i] = failures
        }
    }


    fun initSumaResta(context: Context) {
        preferencesSumaResta = context.getSharedPreferences(PREFS_NAME_SUMARESTA, Context.MODE_PRIVATE)
        currentScoreSumaResta = preferencesSumaResta.getInt(KEY_CURRENT_SCORE_SUMARESTA, 0)
        unlockedLevelsSumaResta = preferencesSumaResta.getInt(KEY_UNLOCKED_LEVELS_SUMARESTA, 2)

        for (i in 1..70) {
            val failures = preferencesSumaResta.getInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA:$i", 0)
            if (failures > 0) {
                consecutiveFailuresSumaResta[i] = failures
            }
        }
        totalGamesSumaRestaAvanzado = preferences.getInt("total_games_sumaresta_avanzado", 0)
        totalGamesSumaRestaPrincipiante = preferences.getInt("total_games_sumaresta_principiante", 0)
        totalGamesSumaRestaPro = preferences.getInt("total_games_sumaresta_pro", 0)

        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesSumaResta = preferencesSumaResta.getInt(KEY_TOTAL_GAMES_SUMARESTA, 0)
        totalTimeSumaResta = preferencesSumaResta.getFloat(KEY_TOTAL_TIME_SUMARESTA, 0f).toDouble()
    }

    fun initSumaRestaPrincipiante(context: Context) {
        preferencesSumaRestaPrincipiante = context.getSharedPreferences(PREFS_NAME_SUMARESTA_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreSumaRestaPrincipiante = preferencesSumaRestaPrincipiante.getInt(KEY_CURRENT_SCORE_SUMARESTA_PRINCIPIANTE, 0)
        unlockedLevelsSumaRestaPrincipiante = preferencesSumaRestaPrincipiante.getInt(KEY_UNLOCKED_LEVELS_SUMARESTA_PRINCIPIANTE, 2)

        for (i in 1..70) {
            val failures = preferencesSumaRestaPrincipiante.getInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRINCIPIANTE:$i", 0)
            if (failures > 0) {
                consecutiveFailuresSumaRestaPrincipiante[i] = failures
            }
        }
    }
    fun initSumaRestaPro(context: Context) {
        preferencesSumaRestaPro = context.getSharedPreferences(PREFS_NAME_SUMARESTA_PRO, Context.MODE_PRIVATE)
        currentScoreSumaRestaPro = preferencesSumaRestaPro.getInt(KEY_CURRENT_SCORE_SUMARESTA_PRO, 0)
        unlockedLevelsSumaRestaPro = preferencesSumaRestaPro.getInt(KEY_UNLOCKED_LEVELS_SUMARESTA_PRO, 2)

        for (i in 1..70) {
            val failures = preferencesSumaRestaPro.getInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRO:$i", 0)
            if (failures > 0) {
                consecutiveFailuresSumaRestaPro[i] = failures
            }
        }
    }


    fun initMasPlus(context: Context) {
        preferencesMasPlus = context.getSharedPreferences(PREFS_NAME_MAS_PLUS, Context.MODE_PRIVATE)
        currentScoreMasPlus = preferencesMasPlus.getInt(KEY_CURRENT_SCORE_MAS_PLUS, 0)
        unlockedLevelsMasPlus = preferencesMasPlus.getInt(KEY_UNLOCKED_LEVELS_MAS_PLUS, 2)

        for (i in 1..70) {
            val failures = preferencesMasPlus.getInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS:$i", 0)
            if (failures > 0) {
                consecutiveFailuresMasPlus[i] = failures
            }
        }
        totalGamesMasPlusAvanzado = preferences.getInt("total_games_masplus_avanzado", 0)
        totalGamesMasPlusPrincipiante = preferences.getInt("total_games_masplus_principiante", 0)
        totalGamesMasPlusPro = preferences.getInt("total_games_masplus_pro", 0)

        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesMasPlus = preferencesMasPlus.getInt(KEY_TOTAL_GAMES_MAS_PLUS, 0)
        totalTimeMasPlus = preferencesMasPlus.getFloat(KEY_TOTAL_TIME_MAS_PLUS, 0f).toDouble()
    }
    fun initMasPlusPrincipiante(context: Context) {
        preferencesMasPlusPrincipiante = context.getSharedPreferences(PREFS_NAME_MAS_PLUS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreMasPlusPrincipiante = preferencesMasPlusPrincipiante.getInt(KEY_CURRENT_SCORE_MAS_PLUS_PRINCIPIANTE, 0)
        unlockedLevelsMasPlusPrincipiante = preferencesMasPlusPrincipiante.getInt(KEY_UNLOCKED_LEVELS_MAS_PLUS_PRINCIPIANTE, 2)

        for (i in 1..70) {
            val failures = preferencesMasPlusPrincipiante.getInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRINCIPIANTE:$i", 0)
            if (failures > 0) {
                consecutiveFailuresMasPlusPrincipiante[i] = failures
            }
        }
    }
    fun initMasPlusPro(context: Context) {
        preferencesMasPlusPro = context.getSharedPreferences(PREFS_NAME_MAS_PLUS_PRO, Context.MODE_PRIVATE)
        currentScoreMasPlusPro = preferencesMasPlusPro.getInt(KEY_CURRENT_SCORE_MAS_PLUS_PRO, 0)
        unlockedLevelsMasPlusPro = preferencesMasPlusPro.getInt(KEY_UNLOCKED_LEVELS_MAS_PLUS_PRO, 2)

        for (i in 1..70) {
            val failures = preferencesMasPlusPro.getInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRO:$i", 0)
            if (failures > 0) {
                consecutiveFailuresMasPlusPro[i] = failures
            }
        }
    }


    fun initGenioPlus(context: Context) {
        preferencesGenioPlus = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS, Context.MODE_PRIVATE)
        currentScoreGenioPlus = preferencesGenioPlus.getInt(KEY_CURRENT_SCORE_GENIO_PLUS, 0)
        unlockedLevelsGenioPlus = preferencesGenioPlus.getInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS, 2)

        for (i in 1..70) {
            val failures = preferencesGenioPlus.getInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS:$i", 0)
            if (failures > 0) {
                consecutiveFailuresGenioPlus[i] = failures
            }
        }
        totalGamesGenioPlusAvanzado = preferences.getInt("total_games_genioplus_avanzado", 0)
        totalGamesGenioPlusPrincipiante = preferences.getInt("total_games_genioplus_principiante", 0)
        totalGamesGenioPlusPro = preferences.getInt("total_games_genioplus_pro", 0)

        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalGamesGlobal = preferences.getInt(KEY_TOTAL_GAMES_GLOBAL, 0)
        correctGamesGlobal = preferences.getInt(KEY_CORRECT_GAMES_GLOBAL, 0)
        totalGamesGenioPlus = preferencesGenioPlus.getInt(KEY_TOTAL_GAMES_GENIO_PLUS, 0)
        totalTimeGenioPlus = preferencesGenioPlus.getFloat(KEY_TOTAL_TIME_GENIO_PLUS, 0f).toDouble()
    }
    fun initGenioPlusPrincipiante(context: Context) {
        preferencesGenioPlusPrincipiante = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS_PRINCIPIANTE, Context.MODE_PRIVATE)
        currentScoreGenioPlusPrincipiante = preferencesGenioPlusPrincipiante.getInt(KEY_CURRENT_SCORE_GENIO_PLUS_PRINCIPIANTE, 0)
        unlockedLevelsGenioPlusPrincipiante = preferencesGenioPlusPrincipiante.getInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRINCIPIANTE, 2)

        for (i in 1..70) {
            val failures = preferencesGenioPlusPrincipiante.getInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRINCIPIANTE:$i", 0)
            if (failures > 0) {
                consecutiveFailuresGenioPlusPrincipiante[i] = failures
            }
        }
    }
    fun initGenioPlusPro(context: Context) {
        preferencesGenioPlusPro = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS_PRO, Context.MODE_PRIVATE)
        currentScoreGenioPlusPro = preferencesGenioPlusPro.getInt(KEY_CURRENT_SCORE_GENIO_PLUS_PRO, 0)
        unlockedLevelsGenioPlusPro = preferencesGenioPlusPro.getInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRO, 2)

        for (i in 1..70) {
            val failures = preferencesGenioPlusPro.getInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRO:$i", 0)
            if (failures > 0) {
                consecutiveFailuresGenioPlusPro[i] = failures
            }
        }
    }


    fun saveScore() {
        preferences.edit {
            putInt(KEY_CURRENT_SCORE, currentScore)
                .putInt(KEY_UNLOCKED_LEVELS, unlockedLevels)
        }
    }
    fun saveScorePrincipiante() {
        preferencesPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_PRINCIPIANTE, currentScorePrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_PRINCIPIANTE, unlockedLevelsPrincipiante)
        }
    }
    fun saveScorePro() {
        preferencesPro.edit {
            putInt(KEY_CURRENT_SCORE_PRO, currentScorePro)
                .putInt(KEY_UNLOCKED_LEVELS_PRO, unlockedLevelsPro)
        }
    }
    fun saveStatsGlobalAndNumerosPlus() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
            putInt(KEY_TOTAL_GAMES_NUMEROS_PLUS, totalGamesNumerosPlus)
            putFloat(KEY_TOTAL_TIME_NUMEROS_PLUS, totalTimeNumerosPlus.toFloat())

            putInt("total_games_numeros_plus_avanzado", totalGamesNumerosPlusAvanzado)
            putInt("total_games_numeros_plus_principiante", totalGamesNumerosPlusPrincipiante)
            putInt("total_games_numeros_plus_pro", totalGamesNumerosPlusPro)
        }
    }

    fun getPrecisionGlobal(): Double {
        return if (totalGamesGlobal > 0) correctGamesGlobal.toDouble() / totalGamesGlobal else 1.0
    }
    fun getTiempoPromedioNumerosPlus(): Double {
        return if (totalGamesNumerosPlus > 0) totalTimeNumerosPlus / totalGamesNumerosPlus else 1.0
    }


    fun saveScoreDeciPlus() {
        preferencesDeciPlus.edit {
            putInt(KEY_CURRENT_SCORE_DECI_PLUS, currentScoreDeciPlus)
            putInt(KEY_UNLOCKED_LEVELS_DECI_PLUS, unlockedLevelsDeciPlus)
        }
    }
    fun saveScoreDeciPlusPrincipiante() {
        preferencesDeciPlusPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_DECI_PLUS_PRINCIPIANTE, currentScoreDeciPlusPrincipiante)
            putInt(KEY_UNLOCKED_LEVELS_DECI_PLUS_PRINCIPIANTE, unlockedLevelsDeciPlusPrincipiante)
        }
    }
    fun saveScoreDeciPlusPro() {
        preferencesDeciPlusPro.edit {
            putInt(KEY_CURRENT_SCORE_DECI_PLUS_PRO, currentScoreDeciPlusPro)
            putInt(KEY_UNLOCKED_LEVELS_DECI_PLUS_PRO, unlockedLevelsDeciPlusPro)
        }
    }
    fun saveStatsGlobalAndDeciPlus() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
        }
        preferencesDeciPlus.edit {
            putInt(KEY_TOTAL_GAMES_DECI_PLUS, totalGamesDeciPlus)
            putFloat(KEY_TOTAL_TIME_DECI_PLUS, totalTimeDeciPlus.toFloat())
            putInt("total_games_deci_plus_avanzado", totalGamesDeciPlusAvanzado)
            putInt("total_games_deci_plus_principiante", totalGamesDeciPlusPrincipiante)
            putInt("total_games_deci_plus_pro", totalGamesDeciPlusPro)
        }
    }
    fun getTiempoPromedioDeciPlus(): Double {
        return if (totalGamesDeciPlus > 0) totalTimeDeciPlus / totalGamesDeciPlus else 1.0
    }


    fun saveScoreRomas() {
        preferencesRomas.edit {
            putInt(KEY_CURRENT_SCORE_ROMAS, currentScoreRomas)
                .putInt(KEY_UNLOCKED_LEVELS_ROMAS, unlockedLevelsRomas)
        }
    }
    fun saveScoreRomasPrincipiante() {
        preferencesRomasPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_ROMAS_PRINCIPIANTE, currentScoreRomasPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_ROMAS_PRINCIPIANTE, unlockedLevelsRomasPrincipiante)
        }
    }
    fun saveScoreRomasPro() {
        preferencesRomasPro.edit {
            putInt(KEY_CURRENT_SCORE_ROMAS_PRO, currentScoreRomasPro)
                .putInt(KEY_UNLOCKED_LEVELS_ROMAS_PRO, unlockedLevelsRomasPro)
        }
    }
    fun saveStatsGlobalAndRomas() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
        }
        preferencesRomas.edit {
            putInt(KEY_TOTAL_GAMES_ROMAS, totalGamesRomas)
            putFloat(KEY_TOTAL_TIME_ROMAS, totalTimeRomas.toFloat())
            putInt("total_games_romas_avanzado", totalGamesRomasAvanzado)
            putInt("total_games_romas_principiante", totalGamesRomasPrincipiante)
            putInt("total_games_romas_pro", totalGamesRomasPro)
        }
    }
    fun getTiempoPromedioRomas(): Double {
        return if (totalGamesRomas > 0) totalTimeRomas / totalGamesRomas else 1.0
    }


    fun saveScoreAlfaNumeros() {
        preferencesAlfaNumeros.edit {
            putInt(KEY_CURRENT_SCORE_ALFANUMEROS, currentScoreAlfaNumeros)
                .putInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS, unlockedLevelsAlfaNumeros)
        }
    }
    fun saveScoreAlfaNumerosPrincipiante() {
        preferencesAlfaNumerosPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_ALFANUMEROS_PRINCIPIANTE, currentScoreAlfaNumerosPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRINCIPIANTE, unlockedLevelsAlfaNumerosPrincipiante)
        }
    }
    fun saveScoreAlfaNumerosPro() {
        preferencesAlfaNumerosPro.edit {
            putInt(KEY_CURRENT_SCORE_ALFANUMEROS_PRO, currentScoreAlfaNumerosPro)
            putInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRO, unlockedLevelsAlfaNumerosPro)
        }
    }
    fun saveStatsGlobalAndAlfaNumeros() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
        }
        preferencesAlfaNumeros.edit {
            putInt(KEY_TOTAL_GAMES_ALFANUMEROS, totalGamesAlfaNumeros)
            putFloat(KEY_TOTAL_TIME_ALFANUMEROS, totalTimeAlfaNumeros.toFloat())
            putInt("total_games_alfanumeros_avanzado", totalGamesAlfaNumerosAvanzado)
            putInt("total_games_alfanumeros_principiante", totalGamesAlfaNumerosPrincipiante)
            putInt("total_games_alfanumeros_pro", totalGamesAlfaNumerosPro)
        }
    }
    fun getTiempoPromedioAlfaNumeros(): Double {
        return if (totalGamesAlfaNumeros > 0) totalTimeAlfaNumeros / totalGamesAlfaNumeros else 1.0
    }


    fun saveScoreSumaResta() {
        preferencesSumaResta.edit {
            putInt(KEY_CURRENT_SCORE_SUMARESTA, currentScoreSumaResta)
                .putInt(KEY_UNLOCKED_LEVELS_SUMARESTA, unlockedLevelsSumaResta)
        }
    }
    fun saveScoreSumaRestaPrincipiante() {
        preferencesSumaRestaPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_SUMARESTA_PRINCIPIANTE, currentScoreSumaRestaPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_SUMARESTA_PRINCIPIANTE, unlockedLevelsSumaRestaPrincipiante)
        }
    }
    fun saveScoreSumaRestaPro() {
        preferencesSumaRestaPro.edit {
            putInt(KEY_CURRENT_SCORE_SUMARESTA_PRO, currentScoreSumaRestaPro)
                .putInt(KEY_UNLOCKED_LEVELS_SUMARESTA_PRO, unlockedLevelsSumaRestaPro)
        }
    }
    fun saveStatsGlobalAndSumaResta() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
        }
        preferencesSumaResta.edit {
            putInt(KEY_TOTAL_GAMES_SUMARESTA, totalGamesSumaResta)
            putFloat(KEY_TOTAL_TIME_SUMARESTA, totalTimeSumaResta.toFloat())
            putInt("total_games_sumaresta_avanzado", totalGamesSumaRestaAvanzado)
            putInt("total_games_sumaresta_principiante", totalGamesSumaRestaPrincipiante)
            putInt("total_games_sumaresta_pro", totalGamesSumaRestaPro)
        }
    }
    fun getTiempoPromedioSumaResta(): Double {
        return if (totalGamesSumaResta > 0) totalTimeSumaResta / totalGamesSumaResta else 1.0
    }


    fun saveScoreMasPlus() {
        preferencesMasPlus.edit {
            putInt(KEY_CURRENT_SCORE_MAS_PLUS, currentScoreMasPlus)
                .putInt(KEY_UNLOCKED_LEVELS_MAS_PLUS, unlockedLevelsMasPlus)
        }
    }
    fun saveScoreMasPlusPrincipiante() {
        preferencesMasPlusPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_MAS_PLUS_PRINCIPIANTE, currentScoreMasPlusPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_MAS_PLUS_PRINCIPIANTE, unlockedLevelsMasPlusPrincipiante)
        }
    }
    fun saveScoreMasPlusPro() {
        preferencesMasPlusPro.edit {
            putInt(KEY_CURRENT_SCORE_MAS_PLUS_PRO, currentScoreMasPlusPro)
                .putInt(KEY_UNLOCKED_LEVELS_MAS_PLUS_PRO, unlockedLevelsMasPlusPro)
        }
    }
    fun saveStatsGlobalAndMasPlus() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
        }
        preferencesMasPlus.edit {
            putInt(KEY_TOTAL_GAMES_MAS_PLUS, totalGamesMasPlus)
            putFloat(KEY_TOTAL_TIME_MAS_PLUS, totalTimeMasPlus.toFloat())
            putInt("total_games_masplus_avanzado", totalGamesMasPlusAvanzado)
            putInt("total_games_masplus_principiante", totalGamesMasPlusPrincipiante)
            putInt("total_games_masplus_pro", totalGamesMasPlusPro)
        }
    }
    fun getTiempoPromedioMasPlus(): Double {
        return if (totalGamesMasPlus > 0) totalTimeMasPlus / totalGamesMasPlus else 1.0
    }


    fun saveScoreGenioPlus() {
        preferencesGenioPlus.edit {
            putInt(KEY_CURRENT_SCORE_GENIO_PLUS, currentScoreGenioPlus)
                .putInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS, unlockedLevelsGenioPlus)
        }
    }
    fun saveScoreGenioPlusPrincipiante() {
        preferencesGenioPlusPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_GENIO_PLUS_PRINCIPIANTE, currentScoreGenioPlusPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRINCIPIANTE, unlockedLevelsGenioPlusPrincipiante)
        }
    }
    fun saveScoreGenioPlusPro() {
        preferencesGenioPlusPro.edit {
            putInt(KEY_CURRENT_SCORE_GENIO_PLUS_PRO, currentScoreGenioPlusPro)
                .putInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRO, unlockedLevelsGenioPlusPro)
        }
    }
    fun saveStatsGlobalAndGenioPlus() {
        preferences.edit {
            putInt(KEY_TOTAL_GAMES_GLOBAL, totalGamesGlobal)
            putInt(KEY_CORRECT_GAMES_GLOBAL, correctGamesGlobal)
        }
        preferencesGenioPlus.edit {
            putInt(KEY_TOTAL_GAMES_GENIO_PLUS, totalGamesGenioPlus)
            putFloat(KEY_TOTAL_TIME_GENIO_PLUS, totalTimeGenioPlus.toFloat())
            putInt("total_games_genioplus_avanzado", totalGamesGenioPlusAvanzado)
            putInt("total_games_genioplus_principiante", totalGamesGenioPlusPrincipiante)
            putInt("total_games_genioplus_pro", totalGamesGenioPlusPro)
        }
    }
    fun getTiempoPromedioGenioPlus(): Double {
        return if (totalGamesGenioPlus > 0) totalTimeGenioPlus / totalGamesGenioPlus else 1.0
    }


    private fun getCompletedLevels(): Set<Int> {
        return preferences.getStringSet(KEY_COMPLETED_LEVELS, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsPrincipiante(): Set<Int> {
        return preferencesPrincipiante.getStringSet(KEY_COMPLETED_LEVELS_PRINCIPIANTE, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsPro(): Set<Int> {
        return preferencesPro.getStringSet(KEY_COMPLETED_LEVELS_PRO, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsDeciPlus(): Set<Int> {
        return preferencesDeciPlus.getStringSet(KEY_COMPLETED_LEVELS_DECI_PLUS, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsDeciPlusPrincipiante(): Set<Int> {
        return preferencesDeciPlusPrincipiante.getStringSet(KEY_COMPLETED_LEVELS_DECI_PLUS_PRINCIPIANTE, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsDeciPlusPro(): Set<Int> {
        return preferencesDeciPlusPro.getStringSet(KEY_COMPLETED_LEVELS_DECI_PLUS_PRO, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsRomas(): Set<Int> {
        return preferencesRomas.getStringSet(KEY_COMPLETED_LEVELS_ROMAS, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsRomasPrincipiante(): Set<Int> {
        return preferencesRomasPrincipiante.getStringSet(KEY_COMPLETED_LEVELS_ROMAS_PRINCIPIANTE, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsRomasPro(): Set<Int> {
        return preferencesRomasPro.getStringSet(KEY_COMPLETED_LEVELS_ROMAS_PRO, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsAlfaNumeros(): Set<Int> {
        return preferencesAlfaNumeros.getStringSet(KEY_COMPLETED_LEVELS_ALFANUMEROS, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsAlfaNumerosPrincipiante(): Set<Int> {
        return preferencesAlfaNumerosPrincipiante.getStringSet(KEY_COMPLETED_LEVELS_ALFANUMEROS_PRINCIPIANTE, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsAlfaNumerosPro(): Set<Int> {
        return preferencesAlfaNumerosPro.getStringSet(KEY_COMPLETED_LEVELS_ALFANUMEROS_PRO, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsSumaResta(): Set<Int> {
        return preferencesSumaResta.getStringSet(KEY_COMPLETED_LEVELS_SUMARESTA, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsSumaRestaPrincipiante(): Set<Int> {
        return preferencesSumaRestaPrincipiante.getStringSet(KEY_COMPLETED_LEVELS_SUMARESTA_PRINCIPIANTE, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsSumaRestaPro(): Set<Int> {
        return preferencesSumaRestaPro.getStringSet(KEY_COMPLETED_LEVELS_SUMARESTA_PRO, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsMasPlus(): Set<Int> {
        return preferencesMasPlus.getStringSet(KEY_COMPLETED_LEVELS_MAS_PLUS, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsMasPlusPrincipiante(): Set<Int> {
        return preferencesMasPlusPrincipiante.getStringSet(KEY_COMPLETED_LEVELS_MAS_PLUS_PRINCIPIANTE, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsMasPlusPro(): Set<Int> {
        return preferencesMasPlusPro.getStringSet(KEY_COMPLETED_LEVELS_MAS_PLUS_PRO, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsGenioPlus(): Set<Int> {
        return preferencesGenioPlus.getStringSet(KEY_COMPLETED_LEVELS_GENIO_PLUS, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsGenioPlusPrincipiante(): Set<Int> {
        return preferencesGenioPlusPrincipiante.getStringSet(KEY_COMPLETED_LEVELS_GENIO_PLUS_PRINCIPIANTE, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsGenioPlusPro(): Set<Int> {
        return preferencesGenioPlusPro.getStringSet(KEY_COMPLETED_LEVELS_GENIO_PLUS_PRO, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    fun addCompletedLevel(level: Int) {
        val completedLevels = getCompletedLevels().toMutableSet()
        completedLevels.add(level)
        preferences.edit {
            putStringSet(KEY_COMPLETED_LEVELS, completedLevels.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelPrincipiante(level: Int) {
        val completedLevelsPrincipiante = getCompletedLevelsPrincipiante().toMutableSet()
        completedLevelsPrincipiante.add(level)
        preferencesPrincipiante.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_PRINCIPIANTE,
                completedLevelsPrincipiante.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelPro(level: Int) {
        val completedLevelsPro = getCompletedLevelsPro().toMutableSet()
        completedLevelsPro.add(level)
        preferencesPro.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_PRO,
                completedLevelsPro.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelDeciPlus(level: Int) {
        val completedLevelsDeciPlus = getCompletedLevelsDeciPlus().toMutableSet()
        completedLevelsDeciPlus.add(level)
        preferencesDeciPlus.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_DECI_PLUS, completedLevelsDeciPlus.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelDeciPlusPrincipiante(level: Int) {
        val completedLevelsDeciPlusPrincipiante = getCompletedLevelsDeciPlusPrincipiante().toMutableSet()
        completedLevelsDeciPlusPrincipiante.add(level)
        preferencesDeciPlusPrincipiante.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_DECI_PLUS_PRINCIPIANTE, completedLevelsDeciPlusPrincipiante.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelDeciPlusPro(level: Int) {
        val completedLevelsDeciPlusPro = getCompletedLevelsDeciPlusPro().toMutableSet()
        completedLevelsDeciPlusPro.add(level)
        preferencesDeciPlusPro.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_DECI_PLUS_PRO, completedLevelsDeciPlusPro.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelRomas(level: Int) {
        val completedLevelsRomas = getCompletedLevelsRomas().toMutableSet()
        completedLevelsRomas.add(level)
        preferencesRomas.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_ROMAS,
                completedLevelsRomas.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelRomasPrincipiante(level: Int) {
        val completedLevelsRomasPrincipiante = getCompletedLevelsRomasPrincipiante().toMutableSet()
        completedLevelsRomasPrincipiante.add(level)
        preferencesRomasPrincipiante.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_ROMAS_PRINCIPIANTE,
                completedLevelsRomasPrincipiante.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelRomasPro(level: Int) {
        val completedLevelsRomasPro = getCompletedLevelsRomasPro().toMutableSet()
        completedLevelsRomasPro.add(level)
        preferencesRomasPro.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_ROMAS_PRO,
                completedLevelsRomasPro.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelAlfaNumeros(level: Int) {
        val completedLevelsAlfaNumeros = getCompletedLevelsAlfaNumeros().toMutableSet()
        completedLevelsAlfaNumeros.add(level)
        preferencesAlfaNumeros.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_ALFANUMEROS,
                completedLevelsAlfaNumeros.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelAlfaNumerosPrincipiante(level: Int) {
        val completedLevelsAlfaNumerosPrincipiante = getCompletedLevelsAlfaNumerosPrincipiante().toMutableSet()
        completedLevelsAlfaNumerosPrincipiante.add(level)
        preferencesAlfaNumerosPrincipiante.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_ALFANUMEROS_PRINCIPIANTE,
                completedLevelsAlfaNumerosPrincipiante.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelAlfaNumerosPro(level: Int) {
        val completedLevels = getCompletedLevelsAlfaNumerosPro().toMutableSet()
        completedLevels.add(level)
        preferencesAlfaNumerosPro.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_ALFANUMEROS_PRO,
                completedLevels.map { it.toString() }.toSet()
            )
        }
    }

    fun addCompletedLevelSumaResta(level: Int) {
        val completedLevelsSumaResta = getCompletedLevelsSumaResta().toMutableSet()
        completedLevelsSumaResta.add(level)
        preferencesSumaResta.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_SUMARESTA,
                completedLevelsSumaResta.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelSumaRestaPrincipiante(level: Int) {
        val completedLevelsSumaRestaPrincipiante = getCompletedLevelsSumaRestaPrincipiante().toMutableSet()
        completedLevelsSumaRestaPrincipiante.add(level)
        preferencesSumaRestaPrincipiante.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_SUMARESTA_PRINCIPIANTE,
                completedLevelsSumaRestaPrincipiante.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelSumaRestaPro(level: Int) {
        val completedLevelsSumaRestaPro = getCompletedLevelsSumaRestaPro().toMutableSet()
        completedLevelsSumaRestaPro.add(level)
        preferencesSumaRestaPro.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_SUMARESTA_PRO,
                completedLevelsSumaRestaPro.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelMasPlus(level: Int) {
        val completedLevelsMasPlus = getCompletedLevelsMasPlus().toMutableSet()
        completedLevelsMasPlus.add(level)
        preferencesMasPlus.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_MAS_PLUS,
                completedLevelsMasPlus.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelMasPlusPrincipiante(level: Int) {
        val completedLevelsMasPlusPrincipiante = getCompletedLevelsMasPlusPrincipiante().toMutableSet()
        completedLevelsMasPlusPrincipiante.add(level)
        preferencesMasPlusPrincipiante.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_MAS_PLUS_PRINCIPIANTE,
                completedLevelsMasPlusPrincipiante.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelMasPlusPro(level: Int) {
        val completedLevelsMasPlusPro = getCompletedLevelsMasPlusPro().toMutableSet()
        completedLevelsMasPlusPro.add(level)
        preferencesMasPlusPro.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_MAS_PLUS_PRO,
                completedLevelsMasPlusPro.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelGenioPlus(level: Int) {
        val completedLevelsGenioPlus = getCompletedLevelsGenioPlus().toMutableSet()
        completedLevelsGenioPlus.add(level)
        preferencesGenioPlus.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_GENIO_PLUS,
                completedLevelsGenioPlus.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelGenioPlusPrincipiante(level: Int) {
        val completedLevelsGenioPlusPrincipiante = getCompletedLevelsGenioPlusPrincipiante().toMutableSet()
        completedLevelsGenioPlusPrincipiante.add(level)
        preferencesGenioPlusPrincipiante.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_GENIO_PLUS_PRINCIPIANTE,
                completedLevelsGenioPlusPrincipiante.map { it.toString() }.toSet())
        }
    }

    fun addCompletedLevelGenioPlusPro(level: Int) {
        val completedLevelsGenioPlusPro = getCompletedLevelsGenioPlusPro().toMutableSet()
        completedLevelsGenioPlusPro.add(level)
        preferencesGenioPlusPro.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_GENIO_PLUS_PRO,
                completedLevelsGenioPlusPro.map { it.toString() }.toSet())
        }
    }

    fun hasCompletedLevel(level: Int): Boolean {
        return getCompletedLevels().contains(level)
    }

    fun hasCompletedLevelPrincipiante(level: Int): Boolean {
        return getCompletedLevelsPrincipiante().contains(level)
    }

    fun hasCompletedLevelPro(level: Int): Boolean {
        return getCompletedLevelsPro().contains(level)
    }

    // --- NÚMEROS PLUS ---

    fun getUniqueLevelsPlayedNumerosPlusPrincipiante(): Int {
        return getCompletedLevelsPrincipiante().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedNumerosPlusAvanzado(): Int {
        return getCompletedLevels().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedNumerosPlusPro(): Int {
        return getCompletedLevelsPro().size.coerceAtMost(12)
    }

    fun getMissingLevelsNumerosPlusPrincipiante(): Int {
        return (12 - getUniqueLevelsPlayedNumerosPlusPrincipiante()).coerceAtLeast(0)
    }

    fun getMissingLevelsNumerosPlusAvanzado(): Int {
        return (12 - getUniqueLevelsPlayedNumerosPlusAvanzado()).coerceAtLeast(0)
    }

    fun getMissingLevelsNumerosPlusPro(): Int {
        return (12 - getUniqueLevelsPlayedNumerosPlusPro()).coerceAtLeast(0)
    }

    fun isEligibleForSpeedRankingNumerosPlus(): Boolean {
        return getUniqueLevelsPlayedNumerosPlusPrincipiante() >= 12 &&
                getUniqueLevelsPlayedNumerosPlusAvanzado() >= 12 &&
                getUniqueLevelsPlayedNumerosPlusPro() >= 12
    }



    fun hasCompletedLevelDeciPlus(level: Int): Boolean {
        return getCompletedLevelsDeciPlus().contains(level)
    }

    fun hasCompletedLevelDeciPlusPrincipiante(level: Int): Boolean {
        return getCompletedLevelsDeciPlusPrincipiante().contains(level)
    }

    fun hasCompletedLevelDeciPlusPro(level: Int): Boolean {
        return getCompletedLevelsDeciPlusPro().contains(level)
    }
    // --- DECI PLUS ---

    fun getUniqueLevelsPlayedDeciPlusPrincipiante(): Int {
        return getCompletedLevelsDeciPlusPrincipiante().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedDeciPlusAvanzado(): Int {
        return getCompletedLevelsDeciPlus().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedDeciPlusPro(): Int {
        return getCompletedLevelsDeciPlusPro().size.coerceAtMost(12)
    }

    fun getMissingLevelsDeciPlusPrincipiante(): Int {
        return (12 - getUniqueLevelsPlayedDeciPlusPrincipiante()).coerceAtLeast(0)
    }

    fun getMissingLevelsDeciPlusAvanzado(): Int {
        return (12 - getUniqueLevelsPlayedDeciPlusAvanzado()).coerceAtLeast(0)
    }

    fun getMissingLevelsDeciPlusPro(): Int {
        return (12 - getUniqueLevelsPlayedDeciPlusPro()).coerceAtLeast(0)
    }

    fun isEligibleForSpeedRankingDeciPlus(): Boolean {
        return getUniqueLevelsPlayedDeciPlusPrincipiante() >= 12 &&
                getUniqueLevelsPlayedDeciPlusAvanzado() >= 12 &&
                getUniqueLevelsPlayedDeciPlusPro() >= 12
    }


    fun hasCompletedLevelRomas(level: Int): Boolean {
        return getCompletedLevelsRomas().contains(level)
    }

    fun hasCompletedLevelRomasPrincipiante(level: Int): Boolean {
        return getCompletedLevelsRomasPrincipiante().contains(level)
    }

    fun hasCompletedLevelRomasPro(level: Int): Boolean {
        return getCompletedLevelsRomasPro().contains(level)
    }
    // --- ROMAS ---

    fun getUniqueLevelsPlayedRomasPrincipiante(): Int {
        return getCompletedLevelsRomasPrincipiante().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedRomasAvanzado(): Int {
        return getCompletedLevelsRomas().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedRomasPro(): Int {
        return getCompletedLevelsRomasPro().size.coerceAtMost(12)
    }

    fun getMissingLevelsRomasPrincipiante(): Int {
        return (12 - getUniqueLevelsPlayedRomasPrincipiante()).coerceAtLeast(0)
    }

    fun getMissingLevelsRomasAvanzado(): Int {
        return (12 - getUniqueLevelsPlayedRomasAvanzado()).coerceAtLeast(0)
    }

    fun getMissingLevelsRomasPro(): Int {
        return (12 - getUniqueLevelsPlayedRomasPro()).coerceAtLeast(0)
    }

    fun isEligibleForSpeedRankingRomas(): Boolean {
        return getUniqueLevelsPlayedRomasPrincipiante() >= 12 &&
                getUniqueLevelsPlayedRomasAvanzado() >= 12 &&
                getUniqueLevelsPlayedRomasPro() >= 12
    }


    fun hasCompletedLevelAlfaNumeros(level: Int): Boolean {
        return getCompletedLevelsAlfaNumeros().contains(level)
    }

    fun hasCompletedLevelAlfaNumerosPrincipiante(level: Int): Boolean {
        return getCompletedLevelsAlfaNumerosPrincipiante().contains(level)
    }

    fun hasCompletedLevelAlfaNumerosPro(level: Int): Boolean {
        return getCompletedLevelsAlfaNumerosPro().contains(level)
    }
    // --- ALFANUMEROS ---

    fun getUniqueLevelsPlayedAlfaNumerosPrincipiante(): Int {
        return getCompletedLevelsAlfaNumerosPrincipiante().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedAlfaNumerosAvanzado(): Int {
        return getCompletedLevelsAlfaNumeros().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedAlfaNumerosPro(): Int {
        return getCompletedLevelsAlfaNumerosPro().size.coerceAtMost(12)
    }

    fun getMissingLevelsAlfaNumerosPrincipiante(): Int {
        return (12 - getUniqueLevelsPlayedAlfaNumerosPrincipiante()).coerceAtLeast(0)
    }

    fun getMissingLevelsAlfaNumerosAvanzado(): Int {
        return (12 - getUniqueLevelsPlayedAlfaNumerosAvanzado()).coerceAtLeast(0)
    }

    fun getMissingLevelsAlfaNumerosPro(): Int {
        return (12 - getUniqueLevelsPlayedAlfaNumerosPro()).coerceAtLeast(0)
    }

    fun isEligibleForSpeedRankingAlfaNumeros(): Boolean {
        return getUniqueLevelsPlayedAlfaNumerosPrincipiante() >= 12 &&
                getUniqueLevelsPlayedAlfaNumerosAvanzado() >= 12 &&
                getUniqueLevelsPlayedAlfaNumerosPro() >= 12
    }


    fun hasCompletedLevelSumaResta(level: Int): Boolean {
        return getCompletedLevelsSumaResta().contains(level)
    }

    fun hasCompletedLevelSumaRestaPrincipiante(level: Int): Boolean {
        return getCompletedLevelsSumaRestaPrincipiante().contains(level)
    }

    fun hasCompletedLevelSumaRestaPro(level: Int): Boolean {
        return getCompletedLevelsSumaRestaPro().contains(level)
    }
    // --- SUMARESTA ---

    fun getUniqueLevelsPlayedSumaRestaPrincipiante(): Int {
        return getCompletedLevelsSumaRestaPrincipiante().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedSumaRestaAvanzado(): Int {
        return getCompletedLevelsSumaResta().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedSumaRestaPro(): Int {
        return getCompletedLevelsSumaRestaPro().size.coerceAtMost(12)
    }

    fun getMissingLevelsSumaRestaPrincipiante(): Int {
        return (12 - getUniqueLevelsPlayedSumaRestaPrincipiante()).coerceAtLeast(0)
    }

    fun getMissingLevelsSumaRestaAvanzado(): Int {
        return (12 - getUniqueLevelsPlayedSumaRestaAvanzado()).coerceAtLeast(0)
    }

    fun getMissingLevelsSumaRestaPro(): Int {
        return (12 - getUniqueLevelsPlayedSumaRestaPro()).coerceAtLeast(0)
    }

    fun isEligibleForSpeedRankingSumaResta(): Boolean {
        return getUniqueLevelsPlayedSumaRestaPrincipiante() >= 12 &&
                getUniqueLevelsPlayedSumaRestaAvanzado() >= 12 &&
                getUniqueLevelsPlayedSumaRestaPro() >= 12
    }


    fun hasCompletedLevelMasPlus(level: Int): Boolean {
        return getCompletedLevelsMasPlus().contains(level)
    }

    fun hasCompletedLevelMasPlusPrincipiante(level: Int): Boolean {
        return getCompletedLevelsMasPlusPrincipiante().contains(level)
    }

    fun hasCompletedLevelMasPlusPro(level: Int): Boolean {
        return getCompletedLevelsMasPlusPro().contains(level)
    }
    // --- MASPLUS ---

    fun getUniqueLevelsPlayedMasPlusPrincipiante(): Int {
        return getCompletedLevelsMasPlusPrincipiante().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedMasPlusAvanzado(): Int {
        return getCompletedLevelsMasPlus().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedMasPlusPro(): Int {
        return getCompletedLevelsMasPlusPro().size.coerceAtMost(12)
    }

    fun getMissingLevelsMasPlusPrincipiante(): Int {
        return (12 - getUniqueLevelsPlayedMasPlusPrincipiante()).coerceAtLeast(0)
    }

    fun getMissingLevelsMasPlusAvanzado(): Int {
        return (12 - getUniqueLevelsPlayedMasPlusAvanzado()).coerceAtLeast(0)
    }

    fun getMissingLevelsMasPlusPro(): Int {
        return (12 - getUniqueLevelsPlayedMasPlusPro()).coerceAtLeast(0)
    }

    fun isEligibleForSpeedRankingMasPlus(): Boolean {
        return getUniqueLevelsPlayedMasPlusPrincipiante() >= 12 &&
                getUniqueLevelsPlayedMasPlusAvanzado() >= 12 &&
                getUniqueLevelsPlayedMasPlusPro() >= 12
    }


    fun hasCompletedLevelGenioPlus(level: Int): Boolean {
        return getCompletedLevelsGenioPlus().contains(level)
    }

    fun hasCompletedLevelGenioPlusPrincipiante(level: Int): Boolean {
        return getCompletedLevelsGenioPlusPrincipiante().contains(level)
    }

    fun hasCompletedLevelGenioPlusPro(level: Int): Boolean {
        return getCompletedLevelsGenioPlusPro().contains(level)
    }
    // --- GENIOPLUS ---

    fun getUniqueLevelsPlayedGenioPlusPrincipiante(): Int {
        return getCompletedLevelsGenioPlusPrincipiante().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedGenioPlusAvanzado(): Int {
        return getCompletedLevelsGenioPlus().size.coerceAtMost(12)
    }

    fun getUniqueLevelsPlayedGenioPlusPro(): Int {
        return getCompletedLevelsGenioPlusPro().size.coerceAtMost(12)
    }

    fun getMissingLevelsGenioPlusPrincipiante(): Int {
        return (12 - getUniqueLevelsPlayedGenioPlusPrincipiante()).coerceAtLeast(0)
    }

    fun getMissingLevelsGenioPlusAvanzado(): Int {
        return (12 - getUniqueLevelsPlayedGenioPlusAvanzado()).coerceAtLeast(0)
    }

    fun getMissingLevelsGenioPlusPro(): Int {
        return (12 - getUniqueLevelsPlayedGenioPlusPro()).coerceAtLeast(0)
    }

    fun isEligibleForSpeedRankingGenioPlus(): Boolean {
        return getUniqueLevelsPlayedGenioPlusPrincipiante() >= 12 &&
                getUniqueLevelsPlayedGenioPlusAvanzado() >= 12 &&
                getUniqueLevelsPlayedGenioPlusPro() >= 12
    }


    fun reset() {
        currentScore = 0
        unlockedLevels = 2
        levelScores.clear()
        preferences.edit {
            putInt(KEY_CURRENT_SCORE, currentScore)
                .putInt(KEY_UNLOCKED_LEVELS, unlockedLevels)
                .putStringSet(KEY_COMPLETED_LEVELS, emptySet())
        }
    }

    fun resetPrincipiante() {
        currentScorePrincipiante = 0
        unlockedLevelsPrincipiante = 2
        levelScoresPrincipiante.clear()
        preferencesPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_PRINCIPIANTE, currentScorePrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_PRINCIPIANTE, unlockedLevelsPrincipiante)
                .putStringSet(KEY_COMPLETED_LEVELS_PRINCIPIANTE, emptySet())
        }
    }

    fun resetPro() {
        currentScorePro = 0
        unlockedLevelsPro = 2
        levelScoresPro.clear()
        preferencesPro.edit {
            putInt(KEY_CURRENT_SCORE_PRO, currentScorePro)
                .putInt(KEY_UNLOCKED_LEVELS_PRO, unlockedLevelsPro)
                .putStringSet(KEY_COMPLETED_LEVELS_PRO, emptySet())
        }
    }

    fun resetDeciPlus() {
        currentScoreDeciPlus = 0
        unlockedLevelsDeciPlus = 2
        levelScoresDeciPlus.clear()
        preferencesDeciPlus.edit {
            putInt(KEY_CURRENT_SCORE_DECI_PLUS, currentScoreDeciPlus)
                .putInt(KEY_UNLOCKED_LEVELS_DECI_PLUS, unlockedLevelsDeciPlus)
                .putStringSet(KEY_COMPLETED_LEVELS_DECI_PLUS, emptySet())
        }
    }

    fun resetDeciPlusPrincipiante() {
        currentScoreDeciPlusPrincipiante = 0
        unlockedLevelsDeciPlusPrincipiante = 2
        levelScoresDeciPlusPrincipiante.clear()
        preferencesDeciPlusPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_DECI_PLUS_PRINCIPIANTE, currentScoreDeciPlusPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_DECI_PLUS_PRINCIPIANTE, unlockedLevelsDeciPlusPrincipiante)
                .putStringSet(KEY_COMPLETED_LEVELS_DECI_PLUS_PRINCIPIANTE, emptySet())
        }
    }

    fun resetDeciPlusPro() {
        currentScoreDeciPlusPro = 0
        unlockedLevelsDeciPlusPro = 2
        levelScoresDeciPlusPro.clear()
        preferencesDeciPlusPro.edit {
            putInt(KEY_CURRENT_SCORE_DECI_PLUS_PRO, currentScoreDeciPlusPro)
                .putInt(KEY_UNLOCKED_LEVELS_DECI_PLUS_PRO, unlockedLevelsDeciPlusPro)
                .putStringSet(KEY_COMPLETED_LEVELS_DECI_PLUS_PRO, emptySet())
        }
    }

    fun resetRomas() {
        currentScoreRomas = 0
        unlockedLevelsRomas = 2
        levelScoresRomas.clear()
        preferencesRomas.edit {
            putInt(KEY_CURRENT_SCORE_ROMAS, currentScoreRomas)
                .putInt(KEY_UNLOCKED_LEVELS_ROMAS, unlockedLevelsRomas)
                .putStringSet(KEY_COMPLETED_LEVELS_ROMAS, emptySet())
        }
    }

    fun resetRomasPrincipiante() {
        currentScoreRomasPrincipiante = 0
        unlockedLevelsRomasPrincipiante = 2
        levelScoresRomasPrincipiante.clear()
        preferencesRomasPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_ROMAS_PRINCIPIANTE, currentScoreRomasPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_ROMAS_PRINCIPIANTE, unlockedLevelsRomasPrincipiante)
                .putStringSet(KEY_COMPLETED_LEVELS_ROMAS_PRINCIPIANTE, emptySet())
        }
    }

    fun resetRomasPro() {
        currentScoreRomasPro = 0
        unlockedLevelsRomasPro = 2
        levelScoresRomasPro.clear()
        preferencesRomasPro.edit {
            putInt(KEY_CURRENT_SCORE_ROMAS_PRO, currentScoreRomasPro)
                .putInt(KEY_UNLOCKED_LEVELS_ROMAS_PRO, unlockedLevelsRomasPro)
                .putStringSet(KEY_COMPLETED_LEVELS_ROMAS_PRO, emptySet())
        }
    }

    fun resetAlfaNumeros() {
        currentScoreAlfaNumeros = 0
        unlockedLevelsAlfaNumeros = 2
        levelScoresAlfaNumeros.clear()
        preferencesAlfaNumeros.edit {
            putInt(KEY_CURRENT_SCORE_ALFANUMEROS, currentScoreAlfaNumeros)
                .putInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS, unlockedLevelsAlfaNumeros)
                .putStringSet(KEY_COMPLETED_LEVELS_ALFANUMEROS, emptySet())
        }
    }

    fun resetAlfaNumerosPrincipiante() {
        currentScoreAlfaNumerosPrincipiante = 0
        unlockedLevelsAlfaNumerosPrincipiante = 2
        levelScoresAlfaNumerosPrincipiante.clear()
        preferencesAlfaNumerosPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_ALFANUMEROS_PRINCIPIANTE, currentScoreAlfaNumerosPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRINCIPIANTE, unlockedLevelsAlfaNumerosPrincipiante)
                .putStringSet(KEY_COMPLETED_LEVELS_ALFANUMEROS_PRINCIPIANTE, emptySet())
        }
    }

    fun resetAlfaNumerosPro() {
        currentScoreAlfaNumerosPro = 0
        unlockedLevelsAlfaNumerosPro = 2
        levelScoresAlfaNumerosPro.clear()
        preferencesAlfaNumerosPro.edit {
            putInt(KEY_CURRENT_SCORE_ALFANUMEROS_PRO, currentScoreAlfaNumerosPro)
                .putInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS_PRO, unlockedLevelsAlfaNumerosPro)
                .putStringSet(KEY_COMPLETED_LEVELS_ALFANUMEROS_PRO, emptySet())
        }
    }

    fun resetSumaResta() {
        currentScoreSumaResta = 0
        unlockedLevelsSumaResta = 2
        levelScoresSumaResta.clear()
        preferencesSumaResta.edit {
            putInt(KEY_CURRENT_SCORE_SUMARESTA, currentScoreSumaResta)
                .putInt(KEY_UNLOCKED_LEVELS_SUMARESTA, unlockedLevelsSumaResta)
                .putStringSet(KEY_COMPLETED_LEVELS_SUMARESTA, emptySet())
        }
    }

    fun resetSumaRestaPrincipiante() {
        currentScoreSumaRestaPrincipiante = 0
        unlockedLevelsSumaRestaPrincipiante = 2
        levelScoresSumaRestaPrincipiante.clear()
        preferencesSumaRestaPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_SUMARESTA_PRINCIPIANTE, currentScoreSumaRestaPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_SUMARESTA_PRINCIPIANTE, unlockedLevelsSumaRestaPrincipiante)
                .putStringSet(KEY_COMPLETED_LEVELS_SUMARESTA_PRINCIPIANTE, emptySet())
        }
    }

    fun resetSumaRestaPro() {
        currentScoreSumaRestaPro = 0
        unlockedLevelsSumaRestaPro = 2
        levelScoresSumaRestaPro.clear()
        preferencesSumaRestaPro.edit {
            putInt(KEY_CURRENT_SCORE_SUMARESTA_PRO, currentScoreSumaRestaPro)
                .putInt(KEY_UNLOCKED_LEVELS_SUMARESTA_PRO, unlockedLevelsSumaRestaPro)
                .putStringSet(KEY_COMPLETED_LEVELS_SUMARESTA_PRO, emptySet())
        }
    }

    fun resetMasPlus() {
        currentScoreMasPlus = 0
        unlockedLevelsMasPlus = 2
        levelScoresMasPlus.clear()
        preferencesMasPlus.edit {
            putInt(KEY_CURRENT_SCORE_MAS_PLUS, currentScoreMasPlus)
                .putInt(KEY_UNLOCKED_LEVELS_MAS_PLUS, unlockedLevelsMasPlus)
                .putStringSet(KEY_COMPLETED_LEVELS_MAS_PLUS, emptySet())
        }
    }

    fun resetMasPlusPrincipiante() {
        currentScoreMasPlusPrincipiante = 0
        unlockedLevelsMasPlusPrincipiante = 2
        levelScoresMasPlusPrincipiante.clear()
        preferencesMasPlusPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_MAS_PLUS_PRINCIPIANTE, currentScoreMasPlusPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_MAS_PLUS_PRINCIPIANTE, unlockedLevelsMasPlusPrincipiante)
                .putStringSet(KEY_COMPLETED_LEVELS_MAS_PLUS_PRINCIPIANTE, emptySet())
        }
    }

    fun resetMasPlusPro() {
        currentScoreMasPlusPro = 0
        unlockedLevelsMasPlusPro = 2
        levelScoresMasPlusPro.clear()
        preferencesMasPlusPro.edit {
            putInt(KEY_CURRENT_SCORE_MAS_PLUS_PRO, currentScoreMasPlusPro)
                .putInt(KEY_UNLOCKED_LEVELS_MAS_PLUS_PRO, unlockedLevelsMasPlusPro)
                .putStringSet(KEY_COMPLETED_LEVELS_MAS_PLUS_PRO, emptySet())
        }
    }

    fun resetGenioPlus() {
        currentScoreGenioPlus = 0
        unlockedLevelsGenioPlus = 2
        levelScoresGenioPlus.clear()
        preferencesGenioPlus.edit {
            putInt(KEY_CURRENT_SCORE_GENIO_PLUS, currentScoreGenioPlus)
                .putInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS, unlockedLevelsGenioPlus)
                .putStringSet(KEY_COMPLETED_LEVELS_GENIO_PLUS, emptySet())
        }
    }

    fun resetGenioPlusPrincipiante() {
        currentScoreGenioPlusPrincipiante = 0
        unlockedLevelsGenioPlusPrincipiante = 2
        levelScoresGenioPlusPrincipiante.clear()
        preferencesGenioPlusPrincipiante.edit {
            putInt(KEY_CURRENT_SCORE_GENIO_PLUS_PRINCIPIANTE, currentScoreGenioPlusPrincipiante)
                .putInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRINCIPIANTE, unlockedLevelsGenioPlusPrincipiante)
                .putStringSet(KEY_COMPLETED_LEVELS_GENIO_PLUS_PRINCIPIANTE, emptySet())
        }
    }

    fun resetGenioPlusPro() {
        currentScoreGenioPlusPro = 0
        unlockedLevelsGenioPlusPro = 2
        levelScoresGenioPlusPro.clear()
        preferencesGenioPlusPro.edit {
            putInt(KEY_CURRENT_SCORE_GENIO_PLUS_PRO, currentScoreGenioPlusPro)
                .putInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS_PRO, unlockedLevelsGenioPlusPro)
                .putStringSet(KEY_COMPLETED_LEVELS_GENIO_PLUS_PRO, emptySet())
        }
    }

    fun incrementConsecutiveFailures(level: Int) {
        val currentFailures = consecutiveFailures[level] ?: 0
        consecutiveFailures[level] = currentFailures + 1
        preferences.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES:$level", consecutiveFailures[level] ?: 0)
        }
    }

    fun resetConsecutiveFailures(level: Int) {
        consecutiveFailures[level] = 0
        preferences.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES:$level", 0)
        }
    }

    fun getConsecutiveFailures(level: Int): Int {
        return consecutiveFailures[level] ?: 0
    }

    fun isLevelBlockedByFailures(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailures(level) >= 12
    }

    fun incrementConsecutiveFailuresPrincipiante(level: Int) {
        val currentFailures = consecutiveFailuresPrincipiante[level] ?: 0
        consecutiveFailuresPrincipiante[level] = currentFailures + 1
        preferencesPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_PRINCIPIANTE:$level", consecutiveFailuresPrincipiante[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresPrincipiante(level: Int) {
        consecutiveFailuresPrincipiante[level] = 0
        preferencesPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_PRINCIPIANTE:$level", 0)
        }
    }

    fun getConsecutiveFailuresPrincipiante(level: Int): Int {
        return consecutiveFailuresPrincipiante[level] ?: 0
    }

    fun isLevelBlockedByFailuresPrincipiante(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresPrincipiante(level) >= 12
    }

    fun incrementConsecutiveFailuresPro(level: Int) {
        val currentFailures = consecutiveFailuresPro[level] ?: 0
        consecutiveFailuresPro[level] = currentFailures + 1
        preferencesPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_PRO:$level", consecutiveFailuresPro[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresPro(level: Int) {
        consecutiveFailuresPro[level] = 0
        preferencesPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_PRO:$level", 0)
        }
    }

    fun getConsecutiveFailuresPro(level: Int): Int {
        return consecutiveFailuresPro[level] ?: 0
    }

    fun isLevelBlockedByFailuresPro(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresPro(level) >= 12
    }

    fun incrementConsecutiveFailuresDeciPlus(level: Int) {
        val currentFailures = consecutiveFailuresDeciPlus[level] ?: 0
        consecutiveFailuresDeciPlus[level] = currentFailures + 1
        preferencesDeciPlus.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS:$level", consecutiveFailuresDeciPlus[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresDeciPlus(level: Int) {
        consecutiveFailuresDeciPlus[level] = 0
        preferencesDeciPlus.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS:$level", 0)
        }
    }

    fun getConsecutiveFailuresDeciPlus(level: Int): Int {
        return consecutiveFailuresDeciPlus[level] ?: 0
    }

    fun isLevelBlockedByFailuresDeciPlus(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresDeciPlus(level) >= 12
    }

    fun incrementConsecutiveFailuresDeciPlusPrincipiante(level: Int) {
        val currentFailures = consecutiveFailuresDeciPlusPrincipiante[level] ?: 0
        consecutiveFailuresDeciPlusPrincipiante[level] = currentFailures + 1
        preferencesDeciPlusPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRINCIPIANTE:$level", consecutiveFailuresDeciPlusPrincipiante[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresDeciPlusPrincipiante(level: Int) {
        consecutiveFailuresDeciPlusPrincipiante[level] = 0
        preferencesDeciPlusPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRINCIPIANTE:$level", 0)
        }
    }

    fun getConsecutiveFailuresDeciPlusPrincipiante(level: Int): Int {
        return consecutiveFailuresDeciPlusPrincipiante[level] ?: 0
    }

    fun isLevelBlockedByFailuresDeciPlusPrincipiante(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresDeciPlusPrincipiante(level) >= 12
    }

    fun incrementConsecutiveFailuresDeciPlusPro(level: Int) {
        val currentFailures = consecutiveFailuresDeciPlusPro[level] ?: 0
        consecutiveFailuresDeciPlusPro[level] = currentFailures + 1
        preferencesDeciPlusPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRO:$level", consecutiveFailuresDeciPlusPro[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresDeciPlusPro(level: Int) {
        consecutiveFailuresDeciPlusPro[level] = 0
        preferencesDeciPlusPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRO:$level", 0)
        }
    }

    fun getConsecutiveFailuresDeciPlusPro(level: Int): Int {
        return consecutiveFailuresDeciPlusPro[level] ?: 0
    }

    fun isLevelBlockedByFailuresDeciPlusPro(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresDeciPlusPro(level) >= 12
    }

    fun incrementConsecutiveFailuresRomas(level: Int) {
        val currentFailures = consecutiveFailuresRomas[level] ?: 0
        consecutiveFailuresRomas[level] = currentFailures + 1
        preferencesRomas.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ROMAS:$level", consecutiveFailuresRomas[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresRomas(level: Int) {
        consecutiveFailuresRomas[level] = 0
        preferencesRomas.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ROMAS:$level", 0)
        }
    }

    fun getConsecutiveFailuresRomas(level: Int): Int {
        return consecutiveFailuresRomas[level] ?: 0
    }

    fun isLevelBlockedByFailuresRomas(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresRomas(level) >= 12
    }

    fun incrementConsecutiveFailuresRomasPrincipiante(level: Int) {
        val currentFailures = consecutiveFailuresRomasPrincipiante[level] ?: 0
        consecutiveFailuresRomasPrincipiante[level] = currentFailures + 1
        preferencesRomasPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ROMAS_PRINCIPIANTE:$level", consecutiveFailuresRomasPrincipiante[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresRomasPrincipiante(level: Int) {
        consecutiveFailuresRomasPrincipiante[level] = 0
        preferencesRomasPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ROMAS_PRINCIPIANTE:$level", 0)
        }
    }

    fun getConsecutiveFailuresRomasPrincipiante(level: Int): Int {
        return consecutiveFailuresRomasPrincipiante[level] ?: 0
    }

    fun isLevelBlockedByFailuresRomasPrincipiante(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresRomasPrincipiante(level) >= 12
    }

    fun incrementConsecutiveFailuresRomasPro(level: Int) {
        val currentFailures = consecutiveFailuresRomasPro[level] ?: 0
        consecutiveFailuresRomasPro[level] = currentFailures + 1
        preferencesRomasPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ROMAS_PRO:$level", consecutiveFailuresRomasPro[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresRomasPro(level: Int) {
        consecutiveFailuresRomasPro[level] = 0
        preferencesRomasPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ROMAS_PRO:$level", 0)
        }
    }

    fun getConsecutiveFailuresRomasPro(level: Int): Int {
        return consecutiveFailuresRomasPro[level] ?: 0
    }

    fun isLevelBlockedByFailuresRomasPro(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresRomasPro(level) >= 12
    }

    fun incrementConsecutiveFailuresAlfaNumeros(level: Int) {
        val currentFailures = consecutiveFailuresAlfaNumeros[level] ?: 0
        consecutiveFailuresAlfaNumeros[level] = currentFailures + 1
        preferencesAlfaNumeros.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS:$level", consecutiveFailuresAlfaNumeros[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresAlfaNumeros(level: Int) {
        consecutiveFailuresAlfaNumeros[level] = 0
        preferencesAlfaNumeros.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS:$level", 0)
        }
    }

    fun getConsecutiveFailuresAlfaNumeros(level: Int): Int {
        return consecutiveFailuresAlfaNumeros[level] ?: 0
    }

    fun isLevelBlockedByFailuresAlfaNumeros(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresAlfaNumeros(level) >= 12
    }

    fun incrementConsecutiveFailuresAlfaNumerosPrincipiante(level: Int) {
        val currentFailures = consecutiveFailuresAlfaNumerosPrincipiante[level] ?: 0
        consecutiveFailuresAlfaNumerosPrincipiante[level] = currentFailures + 1
        preferencesAlfaNumerosPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRINCIPIANTE:$level", consecutiveFailuresAlfaNumerosPrincipiante[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresAlfaNumerosPrincipiante(level: Int) {
        consecutiveFailuresAlfaNumerosPrincipiante[level] = 0
        preferencesAlfaNumerosPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRINCIPIANTE:$level", 0)
        }
    }

    fun getConsecutiveFailuresAlfaNumerosPrincipiante(level: Int): Int {
        return consecutiveFailuresAlfaNumerosPrincipiante[level] ?: 0
    }

    fun isLevelBlockedByFailuresAlfaNumerosPrincipiante(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresAlfaNumerosPrincipiante(level) >= 12
    }

    fun incrementConsecutiveFailuresAlfaNumerosPro(level: Int) {
        val currentFailures = consecutiveFailuresAlfaNumerosPro[level] ?: 0
        consecutiveFailuresAlfaNumerosPro[level] = currentFailures + 1
        preferencesAlfaNumerosPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRO:$level", consecutiveFailuresAlfaNumerosPro[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresAlfaNumerosPro(level: Int) {
        consecutiveFailuresAlfaNumerosPro[level] = 0
        preferencesAlfaNumerosPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_ALFANUMEROS_PRO:$level", 0)
        }
    }

    fun getConsecutiveFailuresAlfaNumerosPro(level: Int): Int {
        return consecutiveFailuresAlfaNumerosPro[level] ?: 0
    }

    fun isLevelBlockedByFailuresAlfaNumerosPro(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresAlfaNumerosPro(level) >= 12
    }

    fun incrementConsecutiveFailuresSumaResta(level: Int) {
        val currentFailures = consecutiveFailuresSumaResta[level] ?: 0
        consecutiveFailuresSumaResta[level] = currentFailures + 1
        preferencesSumaResta.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA:$level", consecutiveFailuresSumaResta[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresSumaResta(level: Int) {
        consecutiveFailuresSumaResta[level] = 0
        preferencesSumaResta.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA:$level", 0)
        }
    }

    fun getConsecutiveFailuresSumaResta(level: Int): Int {
        return consecutiveFailuresSumaResta[level] ?: 0
    }

    fun isLevelBlockedByFailuresSumaResta(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresSumaResta(level) >= 12
    }

    fun incrementConsecutiveFailuresSumaRestaPrincipiante(level: Int) {
        val currentFailures = consecutiveFailuresSumaRestaPrincipiante[level] ?: 0
        consecutiveFailuresSumaRestaPrincipiante[level] = currentFailures + 1
        preferencesSumaRestaPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRINCIPIANTE:$level", consecutiveFailuresSumaRestaPrincipiante[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresSumaRestaPrincipiante(level: Int) {
        consecutiveFailuresSumaRestaPrincipiante[level] = 0
        preferencesSumaRestaPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRINCIPIANTE:$level", 0)
        }
    }

    fun getConsecutiveFailuresSumaRestaPrincipiante(level: Int): Int {
        return consecutiveFailuresSumaRestaPrincipiante[level] ?: 0
    }

    fun isLevelBlockedByFailuresSumaRestaPrincipiante(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresSumaRestaPrincipiante(level) >= 12
    }

    fun incrementConsecutiveFailuresSumaRestaPro(level: Int) {
        val currentFailures = consecutiveFailuresSumaRestaPro[level] ?: 0
        consecutiveFailuresSumaRestaPro[level] = currentFailures + 1
        preferencesSumaRestaPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRO:$level", consecutiveFailuresSumaRestaPro[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresSumaRestaPro(level: Int) {
        consecutiveFailuresSumaRestaPro[level] = 0
        preferencesSumaRestaPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_SUMARESTA_PRO:$level", 0)
        }
    }

    fun getConsecutiveFailuresSumaRestaPro(level: Int): Int {
        return consecutiveFailuresSumaRestaPro[level] ?: 0
    }

    fun isLevelBlockedByFailuresSumaRestaPro(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresSumaRestaPro(level) >= 12
    }

    fun incrementConsecutiveFailuresMasPlus(level: Int) {
        val currentFailures = consecutiveFailuresMasPlus[level] ?: 0
        consecutiveFailuresMasPlus[level] = currentFailures + 1
        preferencesMasPlus.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS:$level", consecutiveFailuresMasPlus[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresMasPlus(level: Int) {
        consecutiveFailuresMasPlus[level] = 0
        preferencesMasPlus.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS:$level", 0)
        }
    }

    fun getConsecutiveFailuresMasPlus(level: Int): Int {
        return consecutiveFailuresMasPlus[level] ?: 0
    }

    fun isLevelBlockedByFailuresMasPlus(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresMasPlus(level) >= 12
    }

    fun incrementConsecutiveFailuresMasPlusPrincipiante(level: Int) {
        val currentFailures = consecutiveFailuresMasPlusPrincipiante[level] ?: 0
        consecutiveFailuresMasPlusPrincipiante[level] = currentFailures + 1
        preferencesMasPlusPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRINCIPIANTE:$level", consecutiveFailuresMasPlusPrincipiante[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresMasPlusPrincipiante(level: Int) {
        consecutiveFailuresMasPlusPrincipiante[level] = 0
        preferencesMasPlusPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRINCIPIANTE:$level", 0)
        }
    }

    fun getConsecutiveFailuresMasPlusPrincipiante(level: Int): Int {
        return consecutiveFailuresMasPlusPrincipiante[level] ?: 0
    }

    fun isLevelBlockedByFailuresMasPlusPrincipiante(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresMasPlusPrincipiante(level) >= 12
    }

    fun incrementConsecutiveFailuresMasPlusPro(level: Int) {
        val currentFailures = consecutiveFailuresMasPlusPro[level] ?: 0
        consecutiveFailuresMasPlusPro[level] = currentFailures + 1
        preferencesMasPlusPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRO:$level", consecutiveFailuresMasPlusPro[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresMasPlusPro(level: Int) {
        consecutiveFailuresMasPlusPro[level] = 0
        preferencesMasPlusPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_MAS_PLUS_PRO:$level", 0)
        }
    }

    fun getConsecutiveFailuresMasPlusPro(level: Int): Int {
        return consecutiveFailuresMasPlusPro[level] ?: 0
    }

    fun isLevelBlockedByFailuresMasPlusPro(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresMasPlusPro(level) >= 12
    }

    fun incrementConsecutiveFailuresGenioPlus(level: Int) {
        val currentFailures = consecutiveFailuresGenioPlus[level] ?: 0
        consecutiveFailuresGenioPlus[level] = currentFailures + 1
        preferencesGenioPlus.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS:$level", consecutiveFailuresGenioPlus[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresGenioPlus(level: Int) {
        val prefs = preferencesGenioPlus
        prefs.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS:$level", 0)
        }
    }

    fun getConsecutiveFailuresGenioPlus(level: Int): Int {
        val prefs = preferencesGenioPlus
        return prefs.getInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS:$level", 0)
    }

    fun isLevelBlockedByFailuresGenioPlus(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresGenioPlus(level) >= 12
    }

    fun incrementConsecutiveFailuresGenioPlusPrincipiante(level: Int) {
        val currentFailures = consecutiveFailuresGenioPlusPrincipiante[level] ?: 0
        consecutiveFailuresGenioPlusPrincipiante[level] = currentFailures + 1
        preferencesGenioPlusPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRINCIPIANTE:$level", consecutiveFailuresGenioPlusPrincipiante[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresGenioPlusPrincipiante(level: Int) {
        consecutiveFailuresGenioPlusPrincipiante[level] = 0
        preferencesGenioPlusPrincipiante.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRINCIPIANTE:$level", 0)
        }
    }

    fun getConsecutiveFailuresGenioPlusPrincipiante(level: Int): Int {
        return consecutiveFailuresGenioPlusPrincipiante[level] ?: 0
    }

    fun isLevelBlockedByFailuresGenioPlusPrincipiante(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresGenioPlusPrincipiante(level) >= 12
    }

    fun incrementConsecutiveFailuresGenioPlusPro(level: Int) {
        val currentFailures = consecutiveFailuresGenioPlusPro[level] ?: 0
        consecutiveFailuresGenioPlusPro[level] = currentFailures + 1
        preferencesGenioPlusPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRO:$level", consecutiveFailuresGenioPlusPro[level] ?: 0)
        }
    }

    fun resetConsecutiveFailuresGenioPlusPro(level: Int) {
        consecutiveFailuresGenioPlusPro[level] = 0
        preferencesGenioPlusPro.edit {
            putInt("$KEY_CONSECUTIVE_FAILURES_GENIO_PLUS_PRO:$level", 0)
        }
    }

    fun getConsecutiveFailuresGenioPlusPro(level: Int): Int {
        return consecutiveFailuresGenioPlusPro[level] ?: 0
    }

    fun isLevelBlockedByFailuresGenioPlusPro(level: Int): Boolean {
        if (level == 1) return false
        return getConsecutiveFailuresGenioPlusPro(level) >= 12
    }

    fun resetStatsAndTimes() {
        totalGamesGlobal = 0
        correctGamesGlobal = 0

        totalGamesNumerosPlus = 0
        totalTimeNumerosPlus = 0.0

        totalGamesDeciPlus = 0
        totalTimeDeciPlus = 0.0

        totalGamesRomas = 0
        totalTimeRomas = 0.0

        totalGamesAlfaNumeros = 0
        totalTimeAlfaNumeros = 0.0

        totalGamesSumaResta = 0
        totalTimeSumaResta = 0.0

        totalGamesMasPlus = 0
        totalTimeMasPlus = 0.0

        totalGamesGenioPlus = 0
        totalTimeGenioPlus = 0.0
    }

    fun getTotalUniqueLevelsCompletedAllGames(): Int {
        return getCompletedLevelsPrincipiante().size +
                getCompletedLevels().size +
                getCompletedLevelsPro().size +
                getCompletedLevelsDeciPlusPrincipiante().size +
                getCompletedLevelsDeciPlus().size +
                getCompletedLevelsDeciPlusPro().size +
                getCompletedLevelsRomasPrincipiante().size +
                getCompletedLevelsRomas().size +
                getCompletedLevelsRomasPro().size +
                getCompletedLevelsAlfaNumerosPrincipiante().size +
                getCompletedLevelsAlfaNumeros().size +
                getCompletedLevelsAlfaNumerosPro().size +
                getCompletedLevelsSumaRestaPrincipiante().size +
                getCompletedLevelsSumaResta().size +
                getCompletedLevelsSumaRestaPro().size +
                getCompletedLevelsMasPlusPrincipiante().size +
                getCompletedLevelsMasPlus().size +
                getCompletedLevelsMasPlusPro().size +
                getCompletedLevelsGenioPlusPrincipiante().size +
                getCompletedLevelsGenioPlus().size +
                getCompletedLevelsGenioPlusPro().size
    }

}


