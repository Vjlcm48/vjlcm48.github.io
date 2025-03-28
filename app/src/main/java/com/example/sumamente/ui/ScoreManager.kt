package com.example.sumamente.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object ScoreManager {

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

    private const val PREFS_NAME_ROMAS = "ScorePrefsRomas"
    private const val KEY_CURRENT_SCORE_ROMAS = "current_score_romas"
    private const val KEY_UNLOCKED_LEVELS_ROMAS = "unlocked_levels_romas"
    private const val KEY_COMPLETED_LEVELS_ROMAS = "completed_levels_romas"

    private const val PREFS_NAME_ALFANUMEROS = "ScorePrefsAlfaNumeros"
    private const val KEY_CURRENT_SCORE_ALFANUMEROS = "current_score_alfanumeros"
    private const val KEY_UNLOCKED_LEVELS_ALFANUMEROS = "unlocked_levels_alfanumeros"
    private const val KEY_COMPLETED_LEVELS_ALFANUMEROS = "completed_levels_alfanumeros"

    private const val PREFS_NAME_SUMARESTA = "ScorePrefsSumaResta"
    private const val KEY_CURRENT_SCORE_SUMARESTA = "current_score_sumaresta"
    private const val KEY_UNLOCKED_LEVELS_SUMARESTA = "unlocked_levels_sumaresta"
    private const val KEY_COMPLETED_LEVELS_SUMARESTA = "completed_levels_sumaresta"

    private const val PREFS_NAME_MAS_PLUS = "ScorePrefsMasPlus"
    private const val KEY_CURRENT_SCORE_MAS_PLUS = "current_score_mas_plus"
    private const val KEY_UNLOCKED_LEVELS_MAS_PLUS = "unlocked_levels_mas_plus"
    private const val KEY_COMPLETED_LEVELS_MAS_PLUS = "completed_levels_mas_plus"

    private const val PREFS_NAME_GENIO_PLUS = "ScorePrefsGenioPlus"
    private const val KEY_CURRENT_SCORE_GENIO_PLUS = "current_score_genio_plus"
    private const val KEY_UNLOCKED_LEVELS_GENIO_PLUS = "unlocked_levels_genio_plus"
    private const val KEY_COMPLETED_LEVELS_GENIO_PLUS= "completed_levels_genio_plus"

    var currentScore: Int = 0
    var unlockedLevels: Int = 2
    val levelScores: MutableMap<Int, Int> = mutableMapOf()

    var currentScorePrincipiante: Int = 0
    var unlockedLevelsPrincipiante: Int = 2
    val levelScoresPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScorePro: Int = 0
    var unlockedLevelsPro: Int = 2
    val levelScoresPro: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreDeciPlus: Int = 0
    var unlockedLevelsDeciPlus: Int = 2
    val levelScoresDeciPlus: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreDeciPlusPrincipiante: Int = 0
    var unlockedLevelsDeciPlusPrincipiante: Int = 2
    val levelScoresDeciPlusPrincipiante: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreDeciPlusPro: Int = 0
    var unlockedLevelsDeciPlusPro: Int = 2
    val levelScoresDeciPlusPro: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreRomas: Int = 0
    var unlockedLevelsRomas: Int = 2
    val levelScoresRomas: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreAlfaNumeros: Int = 0
    var unlockedLevelsAlfaNumeros: Int = 2
    val levelScoresAlfaNumeros: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreSumaResta: Int = 0
    var unlockedLevelsSumaResta: Int = 2
    val levelScoresSumaResta: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreMasPlus: Int = 0
    var unlockedLevelsMasPlus: Int = 2
    val levelScoresMasPlus: MutableMap<Int, Int> = mutableMapOf()

    var currentScoreGenioPlus: Int = 0
    var unlockedLevelsGenioPlus: Int = 2
    val levelScoresGenioPlus: MutableMap<Int, Int> = mutableMapOf()

    private const val KEY_CONSECUTIVE_FAILURES = "consecutive_failures"
    private const val KEY_CONSECUTIVE_FAILURES_PRINCIPIANTE = "consecutive_failures_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_PRO = "consecutive_failures_pro"
    private const val KEY_CONSECUTIVE_FAILURES_DECI_PLUS = "consecutive_failures_deci_plus"
    private const val KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRINCIPIANTE = "consecutive_failures_deci_plus_principiante"
    private const val KEY_CONSECUTIVE_FAILURES_DECI_PLUS_PRO = "consecutive_failures_deci_plus_pro"
    private const val KEY_CONSECUTIVE_FAILURES_ROMAS = "consecutive_failures_romas"

    private val consecutiveFailures: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresPro: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresDeciPlus: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresDeciPlusPrincipiante: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresDeciPlusPro: MutableMap<Int, Int> = mutableMapOf()
    private val consecutiveFailuresRomas: MutableMap<Int, Int> = mutableMapOf()

    private lateinit var preferences: SharedPreferences
    private lateinit var preferencesPrincipiante: SharedPreferences
    private lateinit var preferencesPro: SharedPreferences
    private lateinit var preferencesDeciPlus: SharedPreferences
    private lateinit var preferencesDeciPlusPrincipiante: SharedPreferences
    private lateinit var preferencesDeciPlusPro: SharedPreferences
    private lateinit var preferencesRomas: SharedPreferences
    private lateinit var preferencesAlfaNumeros: SharedPreferences
    private lateinit var preferencesSumaResta: SharedPreferences
    private lateinit var preferencesMasPlus: SharedPreferences
    private lateinit var preferencesGenioPlus: SharedPreferences

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
    }

    fun initAlfaNumeros(context: Context) {
        preferencesAlfaNumeros = context.getSharedPreferences(PREFS_NAME_ALFANUMEROS, Context.MODE_PRIVATE)
        currentScoreAlfaNumeros = preferencesAlfaNumeros.getInt(KEY_CURRENT_SCORE_ALFANUMEROS, 0)
        unlockedLevelsAlfaNumeros = preferencesAlfaNumeros.getInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS, 2)
    }

    fun initSumaResta(context: Context) {
        preferencesSumaResta = context.getSharedPreferences(PREFS_NAME_SUMARESTA, Context.MODE_PRIVATE)
        currentScoreSumaResta = preferencesSumaResta.getInt(KEY_CURRENT_SCORE_SUMARESTA, 0)
        unlockedLevelsSumaResta = preferencesSumaResta.getInt(KEY_UNLOCKED_LEVELS_SUMARESTA, 2)
    }

    fun initMasPlus(context: Context) {
        preferencesMasPlus = context.getSharedPreferences(PREFS_NAME_MAS_PLUS, Context.MODE_PRIVATE)
        currentScoreMasPlus = preferencesMasPlus.getInt(KEY_CURRENT_SCORE_MAS_PLUS, 0)
        unlockedLevelsMasPlus = preferencesMasPlus.getInt(KEY_UNLOCKED_LEVELS_MAS_PLUS, 2)
    }

    fun initGenioPlus(context: Context) {
        preferencesGenioPlus = context.getSharedPreferences(PREFS_NAME_GENIO_PLUS, Context.MODE_PRIVATE)
        currentScoreGenioPlus = preferencesGenioPlus.getInt(KEY_CURRENT_SCORE_GENIO_PLUS, 0)
        unlockedLevelsGenioPlus = preferencesGenioPlus.getInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS, 2)
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

    fun saveScoreRomas() {
        preferencesRomas.edit {
            putInt(KEY_CURRENT_SCORE_ROMAS, currentScoreRomas)
                .putInt(KEY_UNLOCKED_LEVELS_ROMAS, unlockedLevelsRomas)
        }
    }

    fun saveScoreAlfaNumeros() {
        preferencesAlfaNumeros.edit {
            putInt(KEY_CURRENT_SCORE_ALFANUMEROS, currentScoreAlfaNumeros)
                .putInt(KEY_UNLOCKED_LEVELS_ALFANUMEROS, unlockedLevelsAlfaNumeros)
        }
    }

    fun saveScoreSumaResta() {
        preferencesSumaResta.edit {
            putInt(KEY_CURRENT_SCORE_SUMARESTA, currentScoreSumaResta)
                .putInt(KEY_UNLOCKED_LEVELS_SUMARESTA, unlockedLevelsSumaResta)
        }
    }

    fun saveScoreMasPlus() {
        preferencesMasPlus.edit {
            putInt(KEY_CURRENT_SCORE_MAS_PLUS, currentScoreMasPlus)
                .putInt(KEY_UNLOCKED_LEVELS_MAS_PLUS, unlockedLevelsMasPlus)
        }
    }

    fun saveScoreGenioPlus() {
        preferencesGenioPlus.edit {
            putInt(KEY_CURRENT_SCORE_GENIO_PLUS, currentScoreGenioPlus)
                .putInt(KEY_UNLOCKED_LEVELS_GENIO_PLUS, unlockedLevelsGenioPlus)
        }
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

    private fun getCompletedLevelsAlfaNumeros(): Set<Int> {
        return preferencesAlfaNumeros.getStringSet(KEY_COMPLETED_LEVELS_ALFANUMEROS, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsSumaResta(): Set<Int> {
        return preferencesSumaResta.getStringSet(KEY_COMPLETED_LEVELS_SUMARESTA, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsMasPlus(): Set<Int> {
        return preferencesMasPlus.getStringSet(KEY_COMPLETED_LEVELS_MAS_PLUS, emptySet())
            ?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    private fun getCompletedLevelsGenioPlus(): Set<Int> {
        return preferencesGenioPlus.getStringSet(KEY_COMPLETED_LEVELS_GENIO_PLUS, emptySet())
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

    fun addCompletedLevelAlfaNumeros(level: Int) {
        val completedLevelsAlfaNumeros = getCompletedLevelsAlfaNumeros().toMutableSet()
        completedLevelsAlfaNumeros.add(level)
        preferencesAlfaNumeros.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_ALFANUMEROS,
                completedLevelsAlfaNumeros.map { it.toString() }.toSet())
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

    fun addCompletedLevelMasPlus(level: Int) {
        val completedLevelsMasPlus = getCompletedLevelsMasPlus().toMutableSet()
        completedLevelsMasPlus.add(level)
        preferencesMasPlus.edit {
            putStringSet(
                KEY_COMPLETED_LEVELS_MAS_PLUS,
                completedLevelsMasPlus.map { it.toString() }.toSet())
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

    fun hasCompletedLevel(level: Int): Boolean {
        return getCompletedLevels().contains(level)
    }

    fun hasCompletedLevelPrincipiante(level: Int): Boolean {
        return getCompletedLevelsPrincipiante().contains(level)
    }

    fun hasCompletedLevelPro(level: Int): Boolean {
        return getCompletedLevelsPro().contains(level)
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

    fun hasCompletedLevelRomas(level: Int): Boolean {
        return getCompletedLevelsRomas().contains(level)
    }

    fun hasCompletedLevelAlfaNumeros(level: Int): Boolean {
        return getCompletedLevelsAlfaNumeros().contains(level)
    }

    fun hasCompletedLevelSumaResta(level: Int): Boolean {
        return getCompletedLevelsSumaResta().contains(level)
    }

    fun hasCompletedLevelMasPlus(level: Int): Boolean {
        return getCompletedLevelsMasPlus().contains(level)
    }

    fun hasCompletedLevelGenioPlus(level: Int): Boolean {
        return getCompletedLevelsGenioPlus().contains(level)
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

}

