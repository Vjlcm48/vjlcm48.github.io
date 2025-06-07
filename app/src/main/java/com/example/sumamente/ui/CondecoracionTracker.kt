package com.example.sumamente.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object CondecoracionTracker {

    private val gson = Gson()
    private lateinit var preferences: SharedPreferences

    private const val PREFS_NAME = "CondecoracionPrefs"
    private const val KEY_COMPLETED_LEVELS_JSON = "completed_levels_json"
    private const val KEY_PINS_OBTAINED = "pins_obtained"
    private const val KEY_CARRY_OVER_LEVELS = "carry_over_levels"
    private const val KEY_LAST_PIN_CHECK_DATE = "last_pin_check_date"
    private const val KEY_NEW_PINS_INDICATOR = "new_pins_indicator"
    private const val KEY_TROPHY_RED_DOT = "trophy_red_dot_visible"
    private const val KEY_MIS_CONDECORACIONES_RED_DOT = "mis_condecoraciones_red_dot_visible"


    data class CompletedLevel(
        val game: String,
        val grade: String,
        val level: Int,
        val completedAt: Long,
        var consumidoEnPin: Boolean = false,
        var pinAsignado: String? = null
    )

    data class PinObtained(
        val tipo: String,
        val fechaObtencion: Long,
        val nivelesConsumidos: List<CompletedLevel>,
        var visto: Boolean = false
    )

    enum class PinType(val requiredLevels: Int) {
        VICTORIS(30),
        OPTIMUM(40),
        INVICTUS(50)
    }

    private var completedLevelsUnified: MutableList<CompletedLevel> = mutableListOf()
    private var pinesObtenidos: MutableList<PinObtained> = mutableListOf()
    private var carryOverLevels: MutableList<CompletedLevel> = mutableListOf()
    private var lastPinCheckDate: String = ""
    private var hasNewPinsIndicator: Boolean = false

    private var trophyRedDotVisible: Boolean = false
    private var misCondecoracionesRedDotVisible: Boolean = false


    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadCompletedLevelsUnified()
        loadPinesObtenidos()
        loadCarryOverLevels()
        lastPinCheckDate = preferences.getString(KEY_LAST_PIN_CHECK_DATE, "") ?: ""
        hasNewPinsIndicator = preferences.getBoolean(KEY_NEW_PINS_INDICATOR, false)

        trophyRedDotVisible = preferences.getBoolean(KEY_TROPHY_RED_DOT, false)
        misCondecoracionesRedDotVisible = preferences.getBoolean(KEY_MIS_CONDECORACIONES_RED_DOT, false)
    }

    private fun loadCompletedLevelsUnified() {
        val jsonString = preferences.getString(KEY_COMPLETED_LEVELS_JSON, null)
        completedLevelsUnified = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<CompletedLevel>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun loadPinesObtenidos() {
        val jsonString = preferences.getString(KEY_PINS_OBTAINED, null)
        pinesObtenidos = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<PinObtained>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun loadCarryOverLevels() {
        val jsonString = preferences.getString(KEY_CARRY_OVER_LEVELS, null)
        carryOverLevels = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<CompletedLevel>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun saveCompletedLevelsUnified() {
        val jsonString = gson.toJson(completedLevelsUnified)
        preferences.edit {
            putString(KEY_COMPLETED_LEVELS_JSON, jsonString)
        }
    }

    private fun savePinesObtenidos() {
        val jsonString = gson.toJson(pinesObtenidos)
        preferences.edit {
            putString(KEY_PINS_OBTAINED, jsonString)
        }
    }

    private fun saveCarryOverLevels() {
        val jsonString = gson.toJson(carryOverLevels)
        preferences.edit {
            putString(KEY_CARRY_OVER_LEVELS, jsonString)
        }
    }

    fun marcarNivelConTimestamp(game: String, grade: String, level: Int) {
        val currentTime = System.currentTimeMillis()
        val completedLevel = CompletedLevel(game, grade, level, currentTime)

        val existe = completedLevelsUnified.any {
            it.game == game && it.grade == grade && it.level == level && !it.consumidoEnPin
        }

        val nivelYaConsumido = completedLevelsUnified.any {
            it.game == game && it.grade == grade && it.level == level && it.consumidoEnPin
        }

        if (nivelYaConsumido) {
            return
        }

        if (!existe) {
            completedLevelsUnified.add(completedLevel)
            saveCompletedLevelsUnified()
        }
    }

    fun verificarYEntregarPines() {
        val currentDate = getCurrentDateChicago()

        if (lastPinCheckDate != currentDate) {
            val yesterday = getYesterdayDateChicago()
            procesarPinesDelDia(yesterday)
            lastPinCheckDate = currentDate
            preferences.edit {
                putString(KEY_LAST_PIN_CHECK_DATE, currentDate)
            }
        }
    }


    private fun procesarPinesDelDia(fecha: String) {

        val nivelesDelDia = getNivelesDelDia(fecha)
        val carryOverVigente = getCarryOverVigente()
        val nivelesDisponibles = (nivelesDelDia + carryOverVigente).toMutableList()


        limpiarCarryOverExpirado()

        if (nivelesDisponibles.size >= 30) {

            var pinesEntregados = 0

            while (nivelesDisponibles.size >= 50) {
                entregarPin(PinType.INVICTUS, nivelesDisponibles)
                pinesEntregados++
            }

            while (nivelesDisponibles.size >= 40) {
                entregarPin(PinType.OPTIMUM, nivelesDisponibles)
                pinesEntregados++
            }

            while (nivelesDisponibles.size >= 30) {
                entregarPin(PinType.VICTORIS, nivelesDisponibles)
                pinesEntregados++
            }

            if (nivelesDisponibles.isNotEmpty()) {
                carryOverLevels.addAll(nivelesDisponibles)
                saveCarryOverLevels()
            }

            if (pinesEntregados > 0) {
                hasNewPinsIndicator = true
                trophyRedDotVisible = true
                misCondecoracionesRedDotVisible = true

                preferences.edit {
                    putBoolean(KEY_NEW_PINS_INDICATOR, true)
                    putBoolean(KEY_TROPHY_RED_DOT, true)
                    putBoolean(KEY_MIS_CONDECORACIONES_RED_DOT, true)
                }
            }
        }
    }

    private fun entregarPin(pinType: PinType, nivelesDisponibles: MutableList<CompletedLevel>) {
        val nivelesParaPin = nivelesDisponibles.take(pinType.requiredLevels).toMutableList()


        nivelesParaPin.forEach { nivel ->
            nivel.consumidoEnPin = true
            nivel.pinAsignado = pinType.name

            val index = completedLevelsUnified.indexOfFirst {
                it.game == nivel.game && it.grade == nivel.grade && it.level == nivel.level
            }
            if (index != -1) {
                completedLevelsUnified[index] = nivel
            }
        }

        val pin = PinObtained(
            tipo = pinType.name,
            fechaObtencion = System.currentTimeMillis(),
            nivelesConsumidos = nivelesParaPin,
            visto = false
        )

        pinesObtenidos.add(pin)

        nivelesDisponibles.removeAll(nivelesParaPin)


        saveCompletedLevelsUnified()
        savePinesObtenidos()
    }

    private fun getCurrentDateChicago(): String {
        val chicagoZone = TimeZone.getTimeZone("America/Chicago")
        val calendar = Calendar.getInstance(chicagoZone)
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    private fun getYesterdayDateChicago(): String {
        val chicagoZone = TimeZone.getTimeZone("America/Chicago")
        val calendar = Calendar.getInstance(chicagoZone)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    private fun getNivelesDelDia(fecha: String): List<CompletedLevel> {
        return completedLevelsUnified.filter { nivel ->
            !nivel.consumidoEnPin && isSameDate(nivel.completedAt, fecha)
        }
    }

    private fun isSameDate(timestamp: Long, targetDate: String): Boolean {
        val chicagoZone = TimeZone.getTimeZone("America/Chicago")
        val calendar = Calendar.getInstance(chicagoZone)
        calendar.timeInMillis = timestamp
        val dateStr = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        return dateStr == targetDate
    }

    private fun getCarryOverVigente(): List<CompletedLevel> {
        val yesterday = getYesterdayDateChicago()
        return carryOverLevels.filter { nivel ->
            isSameDate(nivel.completedAt, yesterday)
        }
    }

    private fun limpiarCarryOverExpirado() {
        val yesterday = getYesterdayDateChicago()
        carryOverLevels.removeAll { nivel ->
            !isSameDate(nivel.completedAt, yesterday)
        }
        saveCarryOverLevels()
    }

    fun clearTrophyRedDot() {
        trophyRedDotVisible = false
        preferences.edit {
            putBoolean(KEY_TROPHY_RED_DOT, false)
        }
    }

    fun clearMisCondecoracionesRedDot() {
        misCondecoracionesRedDotVisible = false
        preferences.edit {
            putBoolean(KEY_MIS_CONDECORACIONES_RED_DOT, false)
        }
    }

    fun marcarPinesComoVistos() {

        var cambiosRealizados = false
        pinesObtenidos.forEach { pin ->
            if (!pin.visto) {
                pin.visto = true
                cambiosRealizados = true
            }
        }

        if (cambiosRealizados) {
            savePinesObtenidos()
        }
    }

    fun marcarPinIndividualComoVisto(pinTipo: String, fechaObtencion: Long) {
        var cambiosRealizados = false
        pinesObtenidos.forEach { pin ->
            if (pin.tipo == pinTipo && pin.fechaObtencion == fechaObtencion && !pin.visto) {
                pin.visto = true
                cambiosRealizados = true
            }
        }

        if (cambiosRealizados) {
            savePinesObtenidos()
        }
    }

    fun shouldShowTrophyRedDot(): Boolean = trophyRedDotVisible

    fun shouldShowMisCondecoracionesRedDot(): Boolean = misCondecoracionesRedDotVisible

    fun getAllPines(): List<PinObtained> = pinesObtenidos.toList()
}
