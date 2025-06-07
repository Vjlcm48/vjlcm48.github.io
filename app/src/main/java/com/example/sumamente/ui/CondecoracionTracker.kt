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

    // Constantes para SharedPreferences
    private const val PREFS_NAME = "CondecoracionPrefs"
    private const val KEY_COMPLETED_LEVELS_JSON = "completed_levels_json"
    private const val KEY_PINS_OBTAINED = "pins_obtained"
    private const val KEY_CARRY_OVER_LEVELS = "carry_over_levels"
    private const val KEY_LAST_PIN_CHECK_DATE = "last_pin_check_date"
    private const val KEY_NEW_PINS_INDICATOR = "new_pins_indicator"

    // 2.1. Nuevas constantes para puntos rojos individuales
    private const val KEY_TROPHY_RED_DOT = "trophy_red_dot_visible"
    private const val KEY_MIS_CONDECORACIONES_RED_DOT = "mis_condecoraciones_red_dot_visible"

    // Data classes
    data class CompletedLevel(
        val game: String,
        val grade: String,
        val level: Int,
        val completedAt: Long,
        var consumidoEnPin: Boolean = false,
        var pinAsignado: String? = null
    )

    data class PinObtained(
        val tipo: String, // "VICTORIS", "OPTIMUM", "INVICTUS"
        val fechaObtencion: Long,
        val nivelesConsumidos: List<CompletedLevel>,
        var visto: Boolean = false // Nuevo campo para controlar si fue visto
    )

    enum class PinType(val requiredLevels: Int) {
        VICTORIS(30),
        OPTIMUM(40),
        INVICTUS(50)
    }

    // Variables globales
    private var completedLevelsUnified: MutableList<CompletedLevel> = mutableListOf()
    private var pinesObtenidos: MutableList<PinObtained> = mutableListOf()
    private var carryOverLevels: MutableList<CompletedLevel> = mutableListOf()
    private var lastPinCheckDate: String = ""
    private var hasNewPinsIndicator: Boolean = false

    // 2.1. Nuevas variables para puntos rojos
    private var trophyRedDotVisible: Boolean = false
    private var misCondecoracionesRedDotVisible: Boolean = false

    // 1.4. Función de inicialización del tracker
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadCompletedLevelsUnified()
        loadPinesObtenidos()
        loadCarryOverLevels()
        lastPinCheckDate = preferences.getString(KEY_LAST_PIN_CHECK_DATE, "") ?: ""
        hasNewPinsIndicator = preferences.getBoolean(KEY_NEW_PINS_INDICATOR, false)

        // 2.1. Cargar estados de puntos rojos
        trophyRedDotVisible = preferences.getBoolean(KEY_TROPHY_RED_DOT, false)
        misCondecoracionesRedDotVisible = preferences.getBoolean(KEY_MIS_CONDECORACIONES_RED_DOT, false)
    }

    // 1.5. Funciones de carga/guardado de datos
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

    // 1.6b. Función para marcar nivel con timestamp (enfoque híbrido)
    fun marcarNivelConTimestamp(game: String, grade: String, level: Int) {
        val currentTime = System.currentTimeMillis()
        val completedLevel = CompletedLevel(game, grade, level, currentTime)

        // Verificar si ya existe para evitar duplicados
        val existe = completedLevelsUnified.any {
            it.game == game && it.grade == grade && it.level == level && !it.consumidoEnPin
        }

        // Verificar que el nivel no esté ya consumido en un pin anterior
        val nivelYaConsumido = completedLevelsUnified.any {
            it.game == game && it.grade == grade && it.level == level && it.consumidoEnPin
        }

        if (nivelYaConsumido) {
            return // No permitir re-registrar un nivel ya consumido
        }

        if (!existe) {
            completedLevelsUnified.add(completedLevel)
            saveCompletedLevelsUnified()
        }
    }

    // 1.7. Función principal de verificación diaria de pines - CORREGIDA
    fun verificarYEntregarPines() {
        val currentDate = getCurrentDateChicago()

        // SOLO procesar si es un nuevo día (no procesar día actual)
        if (lastPinCheckDate != currentDate) {
            val yesterday = getYesterdayDateChicago()
            procesarPinesDelDia(yesterday)
            lastPinCheckDate = currentDate
            preferences.edit {
                putString(KEY_LAST_PIN_CHECK_DATE, currentDate)
            }
        }
    }

    // 1.8. Función de procesamiento y entrega de pines - CORREGIDA
    private fun procesarPinesDelDia(fecha: String) {
        // Obtener niveles del día anterior + carry-over vigente
        val nivelesDelDia = getNivelesDelDia(fecha)
        val carryOverVigente = getCarryOverVigente()
        val nivelesDisponibles = (nivelesDelDia + carryOverVigente).toMutableList()

        // Limpiar carry-over expirado
        limpiarCarryOverExpirado()

        if (nivelesDisponibles.size >= 30) {
            // Procesar entrega por bloques con prioridad
            var pinesEntregados = 0

            // INVICTUS (50+)
            while (nivelesDisponibles.size >= 50) {
                entregarPin(PinType.INVICTUS, nivelesDisponibles)
                pinesEntregados++
            }

            // OPTIMUM (40+)
            while (nivelesDisponibles.size >= 40) {
                entregarPin(PinType.OPTIMUM, nivelesDisponibles)
                pinesEntregados++
            }

            // VICTORIS (30+)
            while (nivelesDisponibles.size >= 30) {
                entregarPin(PinType.VICTORIS, nivelesDisponibles)
                pinesEntregados++
            }

            // Niveles sobrantes van a carry-over
            if (nivelesDisponibles.isNotEmpty()) {
                carryOverLevels.addAll(nivelesDisponibles)
                saveCarryOverLevels()
            }

            // ACTIVAR PUNTOS ROJOS SOLO AQUÍ (después de entregar pines)
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

    // FUNCIÓN CORREGIDA - SIN activación de puntos rojos
    private fun entregarPin(pinType: PinType, nivelesDisponibles: MutableList<CompletedLevel>) {
        val nivelesParaPin = nivelesDisponibles.take(pinType.requiredLevels).toMutableList()

        // Marcar niveles como consumidos
        nivelesParaPin.forEach { nivel ->
            nivel.consumidoEnPin = true
            nivel.pinAsignado = pinType.name

            // Actualizar en la lista principal
            val index = completedLevelsUnified.indexOfFirst {
                it.game == nivel.game && it.grade == nivel.grade && it.level == nivel.level
            }
            if (index != -1) {
                completedLevelsUnified[index] = nivel
            }
        }

        // Crear pin (nuevo pin siempre no visto)
        val pin = PinObtained(
            tipo = pinType.name,
            fechaObtencion = System.currentTimeMillis(),
            nivelesConsumidos = nivelesParaPin,
            visto = false
        )

        pinesObtenidos.add(pin)

        // Remover niveles usados de disponibles
        nivelesDisponibles.removeAll(nivelesParaPin)

        // Guardar cambios - SOLO pines, NO puntos rojos
        saveCompletedLevelsUnified()
        savePinesObtenidos()
    }

    // 1.9. Funciones auxiliares de fecha (zona Chicago)
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

    // 1.10. Funciones para carry-over y limpieza
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

    // 2.2. Funciones para marcar pines como "vistos"
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
        // Marcar todos los pines no vistos como vistos
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

    // 2.3. Funciones para consultar estado de puntos rojos
    fun shouldShowTrophyRedDot(): Boolean = trophyRedDotVisible

    fun shouldShowMisCondecoracionesRedDot(): Boolean = misCondecoracionesRedDotVisible

    fun getAllPines(): List<PinObtained> = pinesObtenidos.toList()
}
