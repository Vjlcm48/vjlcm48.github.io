package com.example.sumamente.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.sumamente.R
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
    private const val KEY_CORONAS_ACTIVAS = "coronas_activas"
    private const val KEY_LAST_CORONA_CHECK_DATE = "last_corona_check_date"
    private const val KEY_CONDECORACIONES_TOP10 = "condecoraciones_top10"
    private const val KEY_LAST_TOP10_CHECK_DATE = "last_top10_check_date"

    private const val KEY_CONDECORACIONES_IQ7 = "condecoraciones_iq7"
    private const val KEY_LAST_IQ7_CHECK_DATE = "last_iq7_check_date"

    private const val KEY_CONDECORACIONES_TOP5_INTEGRAL = "condecoraciones_top5_integral"
    private const val KEY_LAST_TOP5_INTEGRAL_CHECK_DATE = "last_top5_integral_check_date"
    private const val KEY_MEDALLAS_OBTENIDAS = "medallas_obtenidas"
    private const val KEY_TROFEOS_OBTENIDOS = "trofeos_obtenidos"
    private const val KEY_APEX_SUPREMUS_OBTENIDA = "apex_supremus_obtenida"

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

    data class CoronaActiva(
        val juego: String,
        val tipoCorona: String,
        val fechaAsignacion: Long,
        val esNueva: Boolean = true,
        val mensaje: String
    )

    data class CondecoracionTop10(
        val posicion: Int,
        val tipoCondecoracion: String,
        val fechaAsignacion: Long,
        val esNueva: Boolean = true,
        val mensaje: String
    )

    data class CondecoracionIQ7(
        val posicion: Int,
        val tipoCondecoracion: String,
        val fechaAsignacion: Long,
        val esNueva: Boolean = true,
        val mensaje: String
    )

    data class CondecoracionTop5Integral(
        val posicion: Int,
        val tipoCondecoracion: String,
        val fechaAsignacion: Long,
        val esNueva: Boolean = true,
        val mensaje: String
    )

    data class MedallaObtenida(
        val tipo: String,
        val nivelesRequeridos: Int,
        val fechaObtencion: Long,
        val nivelesConsumidos: Int,
        var vista: Boolean = false
    )

    data class TrofeoObtenido(
        val juego: String,
        val grado: String,
        val nombreTrofeo: String,
        val fechaObtencion: Long,
        var visto: Boolean = false
    )

    data class ApexSupremusObtenida(
        val fechaObtencion: Long,
        var vista: Boolean = false
    )

    enum class MedallaType(val requiredLevels: Int, val nombreMedalla: String) {
        INITIUM(2, "INITIUM"),
        FIDELIS(6, "FIDELIS"),
        VIRTUS(8, "VIRTUS"),
        AUDAX(400, "AUDAX"),
        FORTIS(500, "FORTIS"),
        TENAX(600, "TENAX"),
        INTREPIDUS(700, "INTREPIDUS"),
        SAPIENS(800, "SAPIENS"),
        EXEMPLAR(900, "EXEMPLAR"),
        GLORIAM(1000, "GLORIAM"),
        MAGNUS(1100, "MAGNUS"),
        IMMORTALIS(1200, "IMMORTALIS")
    }

    private var completedLevelsUnified: MutableList<CompletedLevel> = mutableListOf()
    private var pinesObtenidos: MutableList<PinObtained> = mutableListOf()
    private var carryOverLevels: MutableList<CompletedLevel> = mutableListOf()
    private var lastPinCheckDate: String = ""
    private var hasNewPinsIndicator: Boolean = false
    private var coronasActivas: MutableList<CoronaActiva> = mutableListOf()
    private var lastCoronaCheckDate: String = ""

    private var condecoracionesTop10: MutableList<CondecoracionTop10> = mutableListOf()
    private var lastTop10CheckDate: String = ""

    private var condecoracionesIQ7: MutableList<CondecoracionIQ7> = mutableListOf()
    private var lastIQ7CheckDate: String = ""

    private var condecoracionesTop5Integral: MutableList<CondecoracionTop5Integral> = mutableListOf()
    private var lastTop5IntegralCheckDate: String = ""
    private var medallasObtenidas: MutableList<MedallaObtenida> = mutableListOf()
    private var trofeosObtenidos: MutableList<TrofeoObtenido> = mutableListOf()
    private var apexSupremusObtenida: ApexSupremusObtenida? = null

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

        loadCoronasActivas()
        lastCoronaCheckDate = preferences.getString(KEY_LAST_CORONA_CHECK_DATE, "") ?: ""

        loadCondecoracionesTop10()
        lastTop10CheckDate = preferences.getString(KEY_LAST_TOP10_CHECK_DATE, "") ?: ""

        loadCondecoracionesIQ7()
        lastIQ7CheckDate = preferences.getString(KEY_LAST_IQ7_CHECK_DATE, "") ?: ""

        loadCondecoracionesTop5Integral()
        lastTop5IntegralCheckDate = preferences.getString(KEY_LAST_TOP5_INTEGRAL_CHECK_DATE, "") ?: ""

        loadMedallas()
        loadTrofeos()
        loadApexSupremus()
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
        if (nivelYaConsumido) return
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
                updateRedDotsStatus()
                preferences.edit {
                    putBoolean(KEY_NEW_PINS_INDICATOR, true)
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
            updateRedDotsStatus()
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
            updateRedDotsStatus()
        }
    }

    fun getAllPines(): List<PinObtained> = pinesObtenidos.toList()


    private fun loadCoronasActivas() {
        val jsonString = preferences.getString(KEY_CORONAS_ACTIVAS, null)
        coronasActivas = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<CoronaActiva>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun saveCoronasActivas() {
        val jsonString = gson.toJson(coronasActivas)
        preferences.edit {
            putString(KEY_CORONAS_ACTIVAS, jsonString)
        }
    }

    private fun loadCondecoracionesTop10() {
        val jsonString = preferences.getString(KEY_CONDECORACIONES_TOP10, null)
        condecoracionesTop10 = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<CondecoracionTop10>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun saveCondecoracionesTop10() {
        val jsonString = gson.toJson(condecoracionesTop10)
        preferences.edit {
            putString(KEY_CONDECORACIONES_TOP10, jsonString)
        }
    }


    private fun loadCondecoracionesIQ7() {
        val jsonString = preferences.getString(KEY_CONDECORACIONES_IQ7, null)
        condecoracionesIQ7 = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<CondecoracionIQ7>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun saveCondecoracionesIQ7() {
        val jsonString = gson.toJson(condecoracionesIQ7)
        preferences.edit {
            putString(KEY_CONDECORACIONES_IQ7, jsonString)
        }
    }

    private fun loadCondecoracionesTop5Integral() {
        val jsonString = preferences.getString(KEY_CONDECORACIONES_TOP5_INTEGRAL, null)
        condecoracionesTop5Integral = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<CondecoracionTop5Integral>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun saveCondecoracionesTop5Integral() {
        val jsonString = gson.toJson(condecoracionesTop5Integral)
        preferences.edit {
            putString(KEY_CONDECORACIONES_TOP5_INTEGRAL, jsonString)
        }
    }

    private fun loadMedallas() {
        val jsonString = preferences.getString(KEY_MEDALLAS_OBTENIDAS, null)
        medallasObtenidas = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<MedallaObtenida>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun saveMedallas() {
        val jsonString = gson.toJson(medallasObtenidas)
        preferences.edit {
            putString(KEY_MEDALLAS_OBTENIDAS, jsonString)
        }
    }

    fun verificarYEntregarMedallas(callback: (MedallaObtenida?) -> Unit) {
        val nuevaMedalla = procesarMedallasDelUsuario()
        callback(nuevaMedalla)
    }

    private fun procesarMedallasDelUsuario(): MedallaObtenida? {
        val totalNivelesUnicos = ScoreManager.getTotalUniqueLevelsCompletedAllGames()

        for (tipoMedalla in MedallaType.entries) {
            if (totalNivelesUnicos >= tipoMedalla.requiredLevels) {
                val yaObtenida = medallasObtenidas.any { it.tipo == tipoMedalla.nombreMedalla }

                if (!yaObtenida) {
                    val nuevaMedalla = MedallaObtenida(
                        tipo = tipoMedalla.nombreMedalla,
                        nivelesRequeridos = tipoMedalla.requiredLevels,
                        fechaObtencion = System.currentTimeMillis(),
                        nivelesConsumidos = totalNivelesUnicos,
                        vista = false
                    )

                    medallasObtenidas.add(nuevaMedalla)
                    saveMedallas()
                    updateRedDotsStatus()

                    return nuevaMedalla
                }
            }
        }

        return null
    }

    fun getMedallasObtenidas(): List<MedallaObtenida> = medallasObtenidas.toList()


    fun marcarMedallaIndividualComoVista(tipo: String, fechaObtencion: Long) {
        var cambiosRealizados = false
        medallasObtenidas.forEach { medalla ->
            if (medalla.tipo == tipo && medalla.fechaObtencion == fechaObtencion && !medalla.vista) {
                medalla.vista = true
                cambiosRealizados = true
            }
        }
        if (cambiosRealizados) {
            saveMedallas()
            updateRedDotsStatus()
        }
    }

    private fun loadTrofeos() {
        val jsonString = preferences.getString(KEY_TROFEOS_OBTENIDOS, null)
        trofeosObtenidos = if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<TrofeoObtenido>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun saveTrofeos() {
        val jsonString = gson.toJson(trofeosObtenidos)
        preferences.edit {
            putString(KEY_TROFEOS_OBTENIDOS, jsonString)
        }
    }

    private fun loadApexSupremus() {
        val jsonString = preferences.getString(KEY_APEX_SUPREMUS_OBTENIDA, null)
        apexSupremusObtenida = if (jsonString != null) {
            gson.fromJson(jsonString, ApexSupremusObtenida::class.java)
        } else {
            null
        }
    }

    private fun saveApexSupremus() {
        val jsonString = gson.toJson(apexSupremusObtenida)
        preferences.edit {
            putString(KEY_APEX_SUPREMUS_OBTENIDA, jsonString)
        }
    }

    fun verificarYEntregarTrofeos(juego: String, grado: String, callback: (TrofeoObtenido?) -> Unit) {
        val nuevoTrofeo = procesarTrofeoDelUsuario(juego, grado)
        callback(nuevoTrofeo)
    }

    private fun procesarTrofeoDelUsuario(juego: String, grado: String): TrofeoObtenido? {
        val nivelesCompletados = obtenerNivelesCompletadosPorJuegoGrado(juego, grado)

        if (nivelesCompletados >= 4) {
            val nombreTrofeo = mapearJuegoGradoATrofeo(juego, grado)
            val yaObtenido = trofeosObtenidos.any {
                it.juego == juego && it.grado == grado
            }

            if (!yaObtenido) {
                val nuevoTrofeo = TrofeoObtenido(
                    juego = juego,
                    grado = grado,
                    nombreTrofeo = nombreTrofeo,
                    fechaObtencion = System.currentTimeMillis(),
                    visto = false
                )

                trofeosObtenidos.add(nuevoTrofeo)
                saveTrofeos()
                updateRedDotsStatus()

                return nuevoTrofeo
            }
        }

        return null
    }

    private fun mapearJuegoGradoATrofeo(juego: String, grado: String): String {
        return when (juego) {
            "NumerosPlus" -> when (grado) {
                "Principiante" -> "INITIA"
                "Avanzado" -> "CONSTANTIA"
                "Pro" -> "CONFECTUS"
                else -> ""
            }
            "DeciPlus" -> when (grado) {
                "Principiante" -> "VIA"
                "Avanzado" -> "ALTUS"
                "Pro" -> "PERSEVERANTIA"
                else -> ""
            }
            "Romas" -> when (grado) {
                "Principiante" -> "GRADUS"
                "Avanzado" -> "FORTITUDO"
                "Pro" -> "METAM"
                else -> ""
            }
            "AlfaNumeros" -> when (grado) {
                "Principiante" -> "FUNDAMENTUM"
                "Avanzado" -> "PRAEMIUM"
                "Pro" -> "GLORIFICUS"
                else -> ""
            }
            "SumaResta" -> when (grado) {
                "Principiante" -> "SCALA"
                "Avanzado" -> "TENACITAS"
                "Pro" -> "PERFECTUS"
                else -> ""
            }
            "MasPlus" -> when (grado) {
                "Principiante" -> "ORIGO"
                "Avanzado" -> "PROFICIUM"
                "Pro" -> "EXEMPLARITAS"
                else -> ""
            }
            "GenioPlus" -> when (grado) {
                "Principiante" -> "ASCENSUS"
                "Avanzado" -> "MAGNIFICUS"
                "Pro" -> "POTENS"
                else -> ""
            }
            else -> ""
        }
    }

    private fun obtenerNivelesCompletadosPorJuegoGrado(juego: String, grado: String): Int {
        return when (juego) {
            "NumerosPlus" -> when (grado) {
                "Principiante" -> ScoreManager.getUniqueLevelsPlayedNumerosPlusPrincipiante()
                "Avanzado" -> ScoreManager.getUniqueLevelsPlayedNumerosPlusAvanzado()
                "Pro" -> ScoreManager.getUniqueLevelsPlayedNumerosPlusPro()
                else -> 0
            }
            "DeciPlus" -> when (grado) {
                "Principiante" -> ScoreManager.getUniqueLevelsPlayedDeciPlusPrincipiante()
                "Avanzado" -> ScoreManager.getUniqueLevelsPlayedDeciPlusAvanzado()
                "Pro" -> ScoreManager.getUniqueLevelsPlayedDeciPlusPro()
                else -> 0
            }
            "Romas" -> when (grado) {
                "Principiante" -> ScoreManager.getUniqueLevelsPlayedRomasPrincipiante()
                "Avanzado" -> ScoreManager.getUniqueLevelsPlayedRomasAvanzado()
                "Pro" -> ScoreManager.getUniqueLevelsPlayedRomasPro()
                else -> 0
            }
            "AlfaNumeros" -> when (grado) {
                "Principiante" -> ScoreManager.getUniqueLevelsPlayedAlfaNumerosPrincipiante()
                "Avanzado" -> ScoreManager.getUniqueLevelsPlayedAlfaNumerosAvanzado()
                "Pro" -> ScoreManager.getUniqueLevelsPlayedAlfaNumerosPro()
                else -> 0
            }
            "SumaResta" -> when (grado) {
                "Principiante" -> ScoreManager.getUniqueLevelsPlayedSumaRestaPrincipiante()
                "Avanzado" -> ScoreManager.getUniqueLevelsPlayedSumaRestaAvanzado()
                "Pro" -> ScoreManager.getUniqueLevelsPlayedSumaRestaPro()
                else -> 0
            }
            "MasPlus" -> when (grado) {
                "Principiante" -> ScoreManager.getUniqueLevelsPlayedMasPlusPrincipiante()
                "Avanzado" -> ScoreManager.getUniqueLevelsPlayedMasPlusAvanzado()
                "Pro" -> ScoreManager.getUniqueLevelsPlayedMasPlusPro()
                else -> 0
            }
            "GenioPlus" -> when (grado) {
                "Principiante" -> ScoreManager.getUniqueLevelsPlayedGenioPlusPrincipiante()
                "Avanzado" -> ScoreManager.getUniqueLevelsPlayedGenioPlusAvanzado()
                "Pro" -> ScoreManager.getUniqueLevelsPlayedGenioPlusPro()
                else -> 0
            }
            else -> 0
        }
    }

    fun getTrofeosObtenidos(): List<TrofeoObtenido> = trofeosObtenidos.toList()


    fun marcarTrofeoIndividualComoVisto(juego: String, grado: String, fechaObtencion: Long) {
        var cambiosRealizados = false
        trofeosObtenidos.forEach { trofeo ->
            if (trofeo.juego == juego &&
                trofeo.grado == grado &&
                trofeo.fechaObtencion == fechaObtencion &&
                !trofeo.visto) {
                trofeo.visto = true
                cambiosRealizados = true
            }
        }
        if (cambiosRealizados) {
            saveTrofeos()
            updateRedDotsStatus()
        }
    }

    fun verificarYEntregarApexSupremus(callback: (ApexSupremusObtenida?) -> Unit) {
        val nuevaApex = procesarApexDelUsuario()
        callback(nuevaApex)
    }

    private fun procesarApexDelUsuario(): ApexSupremusObtenida? {
        val totalNivelesUnicos = ScoreManager.getTotalUniqueLevelsCompletedAllGames()
        val totalTrofeos = trofeosObtenidos.size

        if (totalNivelesUnicos >= 4 && totalTrofeos >= 1) {
            val yaObtenida = apexSupremusObtenida != null

            if (!yaObtenida) {
                val nuevaApex = ApexSupremusObtenida(
                    fechaObtencion = System.currentTimeMillis(),
                    vista = false
                )

                apexSupremusObtenida = nuevaApex
                saveApexSupremus()
                updateRedDotsStatus()

                return nuevaApex
            }
        }

        return null
    }

    fun getApexSupremus(): ApexSupremusObtenida? = apexSupremusObtenida

    fun marcarApexComoVista() {
        apexSupremusObtenida?.let { apex ->
            if (!apex.vista) {
                apexSupremusObtenida = apex.copy(vista = true)
                saveApexSupremus()
                updateRedDotsStatus()
            }
        }
    }

    fun getCondecoracionesTop10(): List<CondecoracionTop10> = condecoracionesTop10.toList()

    fun getCoronasActivas(): List<CoronaActiva> = coronasActivas.toList()

    fun verificarYActualizarCoronasDeVelocidad(context: Context) {
        val currentDate = getCurrentDateChicago()
        if (lastCoronaCheckDate != currentDate) {
            procesarCoronasDelDia(context)
            lastCoronaCheckDate = currentDate
            preferences.edit {
                putString(KEY_LAST_CORONA_CHECK_DATE, currentDate)
            }
        }
    }

    fun verificarYActualizarCondecoracionesTop10(context: Context) {
        val currentDate = getCurrentDateChicago()
        if (lastTop10CheckDate != currentDate) {
            procesarCondecoracionesTop10DelDia(context)
            lastTop10CheckDate = currentDate
            preferences.edit {
                putString(KEY_LAST_TOP10_CHECK_DATE, currentDate)
            }
        }
    }

    private fun procesarCondecoracionesTop10DelDia(context: Context) {
        val posicionUsuario = consultarPosicionRankingGlobal(context)
        actualizarCondecoracionTop10DelUsuario(posicionUsuario, context)
    }

    private fun consultarPosicionRankingGlobal(context: Context): Int {
        ScoreManager.ensurePreferencesInitialized(context)
        ScoreManager.init(context)
        ScoreManager.initPrincipiante(context)
        ScoreManager.initPro(context)
        ScoreManager.initDeciPlus(context)
        ScoreManager.initDeciPlusPrincipiante(context)
        ScoreManager.initDeciPlusPro(context)
        ScoreManager.initRomas(context)
        ScoreManager.initRomasPrincipiante(context)
        ScoreManager.initRomasPro(context)
        ScoreManager.initAlfaNumeros(context)
        ScoreManager.initAlfaNumerosPrincipiante(context)
        ScoreManager.initAlfaNumerosPro(context)
        ScoreManager.initSumaResta(context)
        ScoreManager.initSumaRestaPrincipiante(context)
        ScoreManager.initSumaRestaPro(context)
        ScoreManager.initMasPlus(context)
        ScoreManager.initMasPlusPrincipiante(context)
        ScoreManager.initMasPlusPro(context)
        ScoreManager.initGenioPlus(context)
        ScoreManager.initGenioPlusPrincipiante(context)
        ScoreManager.initGenioPlusPro(context)
        return ScoreManager.getUserPositionInRanking("GLOBAL")
    }

    private fun actualizarCondecoracionTop10DelUsuario(posicion: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioActual = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"

        if (posicion in 1..10) {
            // 🔧 CAMBIO CLAVE: Verificar si ya existe antes de crear
            val yaExiste = condecoracionesTop10.any { it.posicion == posicion }

            if (!yaExiste) {
                val tipoCondecoracion = when (posicion) {
                    1 -> "EXCELSITUR"
                    2 -> "SUMMUM"
                    3 -> "MAGNANIMOUS"
                    4 -> "VENERABILIS"
                    5 -> "GLORIOSUS"
                    6 -> "ILLUSTRIS"
                    7 -> "PRAESTANS"
                    8 -> "INSIGNIS"
                    9 -> "VIRTUOSUS"
                    10 -> "HONORABILIS"
                    else -> ""
                }

                val mensaje = generarMensajeTop10(tipoCondecoracion, usuarioActual, context)

                condecoracionesTop10.add(
                    CondecoracionTop10(
                        posicion = posicion,
                        tipoCondecoracion = tipoCondecoracion,
                        fechaAsignacion = System.currentTimeMillis(),
                        esNueva = true,  // ← Solo se marca como nueva la PRIMERA vez
                        mensaje = mensaje
                    )
                )

                updateRedDotsStatus()
                saveCondecoracionesTop10()
            }


        } else {

            if (condecoracionesTop10.isNotEmpty()) {
                condecoracionesTop10.clear()
                saveCondecoracionesTop10()
                updateRedDotsStatus()
            }
        }
    }

    private fun generarMensajeTop10(tipoCondecoracion: String, nombreUsuario: String, context: Context): String {
        val random = kotlin.random.Random.nextInt(1, 4)

        val stringResId = when (tipoCondecoracion) {
            "EXCELSITUR" -> when (random) {
                1 -> R.string.msg_top10_excelsitur_1
                2 -> R.string.msg_top10_excelsitur_2
                else -> R.string.msg_top10_excelsitur_3
            }
            "SUMMUM" -> when (random) {
                1 -> R.string.msg_top10_summum_1
                2 -> R.string.msg_top10_summum_2
                else -> R.string.msg_top10_summum_3
            }
            "MAGNANIMOUS" -> when (random) {
                1 -> R.string.msg_top10_magnanimous_1
                2 -> R.string.msg_top10_magnanimous_2
                else -> R.string.msg_top10_magnanimous_3
            }
            "VENERABILIS" -> when (random) {
                1 -> R.string.msg_top10_venerabilis_1
                2 -> R.string.msg_top10_venerabilis_2
                else -> R.string.msg_top10_venerabilis_3
            }
            "GLORIOSUS" -> when (random) {
                1 -> R.string.msg_top10_gloriosus_1
                2 -> R.string.msg_top10_gloriosus_2
                else -> R.string.msg_top10_gloriosus_3
            }
            "ILLUSTRIS" -> when (random) {
                1 -> R.string.msg_top10_illustris_1
                2 -> R.string.msg_top10_illustris_2
                else -> R.string.msg_top10_illustris_3
            }
            "PRAESTANS" -> when (random) {
                1 -> R.string.msg_top10_praestans_1
                2 -> R.string.msg_top10_praestans_2
                else -> R.string.msg_top10_praestans_3
            }
            "INSIGNIS" -> when (random) {
                1 -> R.string.msg_top10_insignis_1
                2 -> R.string.msg_top10_insignis_2
                else -> R.string.msg_top10_insignis_3
            }
            "VIRTUOSUS" -> when (random) {
                1 -> R.string.msg_top10_virtuosus_1
                2 -> R.string.msg_top10_virtuosus_2
                else -> R.string.msg_top10_virtuosus_3
            }
            "HONORABILIS" -> when (random) {
                1 -> R.string.msg_top10_honorabilis_1
                2 -> R.string.msg_top10_honorabilis_2
                else -> R.string.msg_top10_honorabilis_3
            }
            else -> R.string.msg_top10_honorabilis_1
        }

        return context.getString(stringResId, nombreUsuario, tipoCondecoracion)
    }

    fun verificarYActualizarCondecoracionesIQ7(context: Context) {
        val currentDate = getCurrentDateChicago()
        if (lastIQ7CheckDate != currentDate) {
            procesarCondecoracionesIQ7DelDia(context)
            lastIQ7CheckDate = currentDate
            preferences.edit {
                putString(KEY_LAST_IQ7_CHECK_DATE, currentDate)
            }
        }
    }

    private fun procesarCondecoracionesIQ7DelDia(context: Context) {
        val posicionUsuario = consultarPosicionRankingIQ7(context)
        actualizarCondecoracionIQ7DelUsuario(posicionUsuario, context)
    }

    private fun consultarPosicionRankingIQ7(context: Context): Int {
        ScoreManager.ensurePreferencesInitialized(context)
        return ScoreManager.getUserPositionInRanking("IQ_PLUS")  // ← REAL
    }

    private fun actualizarCondecoracionIQ7DelUsuario(posicion: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioActual = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"

        if (posicion in 1..7) {
            // 🔧 CAMBIO CLAVE: Verificar si ya existe antes de crear
            val yaExiste = condecoracionesIQ7.any { it.posicion == posicion }

            if (!yaExiste) {
                val tipoCondecoracion = when (posicion) {
                    1 -> "SAPIENS_SUPREMUS"
                    2 -> "MENTIS_AUREA"
                    3 -> "LUMINIS_REX"
                    4 -> "DOCTRINAE_PRINCEPS"
                    5 -> "CONSILIUM_MAGNUS"
                    6 -> "INTELLECTUS_PRIMUS"
                    7 -> "DISCIPULUS_OPTIMUS"
                    else -> ""
                }

                val mensaje = generarMensajeIQ7(tipoCondecoracion, usuarioActual, posicion, context)

                condecoracionesIQ7.add(
                    CondecoracionIQ7(
                        posicion = posicion,
                        tipoCondecoracion = tipoCondecoracion,
                        fechaAsignacion = System.currentTimeMillis(),
                        esNueva = true,  // ← Solo se marca como nueva la PRIMERA vez
                        mensaje = mensaje
                    )
                )

                updateRedDotsStatus()
                saveCondecoracionesIQ7()
            }

        } else {

            if (condecoracionesIQ7.isNotEmpty()) {
                condecoracionesIQ7.clear()
                saveCondecoracionesIQ7()
                updateRedDotsStatus()
            }
        }
    }

    private fun generarMensajeIQ7(
        tipoCondecoracion: String,
        usuarioActual1: String,
        posicion: Int,
        context: Context
    ): String {
        return context.getString(
            when (tipoCondecoracion) {
                "SAPIENS_SUPREMUS" -> R.string.logro_iq7_sapiens_supremus
                "MENTIS_AUREA" -> R.string.logro_iq7_mentis_aurea
                "LUMINIS_REX" -> R.string.logro_iq7_luminis_rex
                "DOCTRINAE_PRINCEPS" -> R.string.logro_iq7_doctrinae_princeps
                "CONSILIUM_MAGNUS" -> R.string.logro_iq7_consilium_magnus
                "INTELLECTUS_PRIMUS" -> R.string.logro_iq7_intellectus_primus
                "DISCIPULUS_OPTIMUS" -> R.string.logro_iq7_discipulus_optimus
                else -> R.string.logro_iq7_discipulus_optimus
            },
            usuarioActual1,
            posicion,
            tipoCondecoracion
        )
    }

    fun getCondecoracionesIQ7(): List<CondecoracionIQ7> = condecoracionesIQ7.toList()

    fun marcarCondecoracionesIQ7ComoVistas() {
        var cambiosRealizados = false
        condecoracionesIQ7.forEach { condecoracion ->
            if (condecoracion.esNueva) {
                val index = condecoracionesIQ7.indexOf(condecoracion)
                if (index != -1) {
                    condecoracionesIQ7[index] = condecoracion.copy(esNueva = false)
                    cambiosRealizados = true
                }
            }
        }
        if (cambiosRealizados) {
            saveCondecoracionesIQ7()
            updateRedDotsStatus()
        }
    }

    fun marcarCondecoracionIQ7IndividualComoVista(posicion: Int, tipoCondecoracion: String, fechaAsignacion: Long) {
        var cambiosRealizados = false
        condecoracionesIQ7.forEach { condecoracion ->
            if (condecoracion.posicion == posicion &&
                condecoracion.tipoCondecoracion == tipoCondecoracion &&
                condecoracion.fechaAsignacion == fechaAsignacion &&
                condecoracion.esNueva
            ) {
                val index = condecoracionesIQ7.indexOf(condecoracion)
                if (index != -1) {
                    condecoracionesIQ7[index] = condecoracion.copy(esNueva = false)
                    cambiosRealizados = true
                }
            }
        }
        if (cambiosRealizados) {
            saveCondecoracionesIQ7()
            updateRedDotsStatus()
        }
    }

    private fun hasNewIQ7(): Boolean {
        return condecoracionesIQ7.any { it.esNueva }
    }

    fun verificarYActualizarCondecoracionesTop5Integral(context: Context) {
        val currentDate = getCurrentDateChicago()
        if (lastTop5IntegralCheckDate != currentDate) {
            procesarCondecoracionesTop5IntegralDelDia(context)
            lastTop5IntegralCheckDate = currentDate
            preferences.edit {
                putString(KEY_LAST_TOP5_INTEGRAL_CHECK_DATE, currentDate)
            }
        }
    }

    private fun procesarCondecoracionesTop5IntegralDelDia(context: Context) {
        val posicionUsuario = consultarPosicionRankingIntegral(context)
        actualizarCondecoracionTop5IntegralDelUsuario(posicionUsuario, context)
    }

    private fun consultarPosicionRankingIntegral(context: Context): Int {
        ScoreManager.ensurePreferencesInitialized(context)
        ScoreManager.init(context)
        ScoreManager.initPrincipiante(context)
        ScoreManager.initPro(context)
        ScoreManager.initDeciPlus(context)
        ScoreManager.initDeciPlusPrincipiante(context)
        ScoreManager.initDeciPlusPro(context)
        ScoreManager.initRomas(context)
        ScoreManager.initRomasPrincipiante(context)
        ScoreManager.initRomasPro(context)
        ScoreManager.initAlfaNumeros(context)
        ScoreManager.initAlfaNumerosPrincipiante(context)
        ScoreManager.initAlfaNumerosPro(context)
        ScoreManager.initSumaResta(context)
        ScoreManager.initSumaRestaPrincipiante(context)
        ScoreManager.initSumaRestaPro(context)
        ScoreManager.initMasPlus(context)
        ScoreManager.initMasPlusPrincipiante(context)
        ScoreManager.initMasPlusPro(context)
        ScoreManager.initGenioPlus(context)
        ScoreManager.initGenioPlusPrincipiante(context)
        ScoreManager.initGenioPlusPro(context)
        return ScoreManager.getUserPositionInRankingIntegral()
    }

    private fun actualizarCondecoracionTop5IntegralDelUsuario(posicion: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioActual = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"

        if (posicion in 1..5) {
            val yaExiste = condecoracionesTop5Integral.any { it.posicion == posicion }

            if (!yaExiste) {
                val tipoCondecoracion = when (posicion) {
                    1 -> "IMPERIUM_SUPREMUS"
                    2 -> "MAGNUS_HONOR"
                    3 -> "VIRTUS_TOTALIS"
                    4 -> "EXCELLENTIA_SINGULARI"
                    5 -> "GLORIA_INTEGRALIS"
                    else -> ""
                }

                val mensaje = generarMensajeTop5Integral(tipoCondecoracion, usuarioActual, context)

                condecoracionesTop5Integral.add(
                    CondecoracionTop5Integral(
                        posicion = posicion,
                        tipoCondecoracion = tipoCondecoracion,
                        fechaAsignacion = System.currentTimeMillis(),
                        esNueva = true,
                        mensaje = mensaje
                    )
                )

                updateRedDotsStatus()
                saveCondecoracionesTop5Integral()
            }
        } else {
            if (condecoracionesTop5Integral.isNotEmpty()) {
                condecoracionesTop5Integral.clear()
                saveCondecoracionesTop5Integral()
                updateRedDotsStatus()
            }
        }
    }

    private fun generarMensajeTop5Integral(tipoCondecoracion: String, nombreUsuario: String, context: Context): String {
        val random = kotlin.random.Random.nextInt(1, 3)

        val stringResId = when (tipoCondecoracion) {
            "IMPERIUM_SUPREMUS" -> when (random) {
                1 -> R.string.logro_imperium_supremus_1
                else -> R.string.logro_imperium_supremus_2
            }
            "MAGNUS_HONOR" -> when (random) {
                1 -> R.string.logro_magnus_honor_1
                else -> R.string.logro_magnus_honor_2
            }
            "VIRTUS_TOTALIS" -> when (random) {
                1 -> R.string.logro_virtus_totalis_1
                else -> R.string.logro_virtus_totalis_2
            }
            "EXCELLENTIA_SINGULARI" -> when (random) {
                1 -> R.string.logro_excellentia_singulari_1
                else -> R.string.logro_excellentia_singulari_2
            }
            "GLORIA_INTEGRALIS" -> when (random) {
                1 -> R.string.logro_gloria_integralis_1
                else -> R.string.logro_gloria_integralis_2
            }
            else -> R.string.logro_gloria_integralis_1
        }

        return context.getString(stringResId, nombreUsuario)
    }

    fun getCondecoracionesTop5Integral(): List<CondecoracionTop5Integral> = condecoracionesTop5Integral.toList()

    private fun hasNewTop5Integral(): Boolean {
        return condecoracionesTop5Integral.any { it.esNueva }
    }

    fun marcarCondecoracionesTop5IntegralComoVistas() {
        var cambiosRealizados = false
        condecoracionesTop5Integral.forEach { condecoracion ->
            if (condecoracion.esNueva) {
                val index = condecoracionesTop5Integral.indexOf(condecoracion)
                if (index != -1) {
                    condecoracionesTop5Integral[index] = condecoracion.copy(esNueva = false)
                    cambiosRealizados = true
                }
            }
        }
        if (cambiosRealizados) {
            saveCondecoracionesTop5Integral()
            updateRedDotsStatus()
        }
    }

    fun marcarCondecoracionTop5IntegralIndividualComoVista(posicion: Int, tipoCondecoracion: String, fechaAsignacion: Long) {
        var cambiosRealizados = false
        condecoracionesTop5Integral.forEach { condecoracion ->
            if (condecoracion.posicion == posicion &&
                condecoracion.tipoCondecoracion == tipoCondecoracion &&
                condecoracion.fechaAsignacion == fechaAsignacion &&
                condecoracion.esNueva
            ) {
                val index = condecoracionesTop5Integral.indexOf(condecoracion)
                if (index != -1) {
                    condecoracionesTop5Integral[index] = condecoracion.copy(esNueva = false)
                    cambiosRealizados = true
                }
            }
        }
        if (cambiosRealizados) {
            saveCondecoracionesTop5Integral()
            updateRedDotsStatus()
        }
    }

    private fun procesarCoronasDelDia(context: Context) {
        val juegos = listOf(
            "NumerosPlus", "DeciPlus", "Romas",
            "AlfaNumeros", "SumaResta", "MasPlus", "GenioPlus"
        )
        juegos.forEach { juego ->
            val top3Ranking = consultarRankingExistente(juego, context)
            actualizarCoronasDelJuego(juego, top3Ranking, context)
        }
    }

    private fun consultarRankingExistente(juego: String, context: Context): List<String> {

        when (juego) {
            "NumerosPlus" -> {
                ScoreManager.ensurePreferencesInitialized(context)
                ScoreManager.initPrincipiante(context)
                ScoreManager.init(context)
                ScoreManager.initPro(context)
            }
            "DeciPlus" -> {
                ScoreManager.ensurePreferencesInitialized(context)
                ScoreManager.initDeciPlusPrincipiante(context)
                ScoreManager.initDeciPlus(context)
                ScoreManager.initDeciPlusPro(context)
            }
            "Romas" -> {
                ScoreManager.ensurePreferencesInitialized(context)
                ScoreManager.initRomasPrincipiante(context)
                ScoreManager.initRomas(context)
                ScoreManager.initRomasPro(context)
            }
            "AlfaNumeros" -> {
                ScoreManager.ensurePreferencesInitialized(context)
                ScoreManager.initAlfaNumerosPrincipiante(context)
                ScoreManager.initAlfaNumeros(context)
                ScoreManager.initAlfaNumerosPro(context)
            }
            "SumaResta" -> {
                ScoreManager.ensurePreferencesInitialized(context)
                ScoreManager.initSumaRestaPrincipiante(context)
                ScoreManager.initSumaResta(context)
                ScoreManager.initSumaRestaPro(context)
            }
            "MasPlus" -> {
                ScoreManager.ensurePreferencesInitialized(context)
                ScoreManager.initMasPlusPrincipiante(context)
                ScoreManager.initMasPlus(context)
                ScoreManager.initMasPlusPro(context)
            }
            "GenioPlus" -> {
                ScoreManager.ensurePreferencesInitialized(context)
                ScoreManager.initGenioPlusPrincipiante(context)
                ScoreManager.initGenioPlus(context)
                ScoreManager.initGenioPlusPro(context)
            }
        }

        val isEligible = when (juego) {
            "NumerosPlus" -> ScoreManager.isEligibleForSpeedRankingNumerosPlus()
            "DeciPlus" -> ScoreManager.isEligibleForSpeedRankingDeciPlus()
            "Romas" -> ScoreManager.isEligibleForSpeedRankingRomas()
            "AlfaNumeros" -> ScoreManager.isEligibleForSpeedRankingAlfaNumeros()
            "SumaResta" -> ScoreManager.isEligibleForSpeedRankingSumaResta()
            "MasPlus" -> ScoreManager.isEligibleForSpeedRankingMasPlus()
            "GenioPlus" -> ScoreManager.isEligibleForSpeedRankingGenioPlus()
            else -> false
        }

        if (!isEligible) {
            return emptyList()
        }

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"

        return listOf(username)
    }



    private fun actualizarCoronasDelJuego(juego: String, top3Ranking: List<String>, context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioActual = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"
        val coronasExistentes = coronasActivas.filter { it.juego == juego }
        val posicionUsuario = top3Ranking.indexOf(usuarioActual)
        val nuevaCorona = when (posicionUsuario) {
            0 -> "VOLUCER"
            1 -> "CELERIS"
            2 -> "VELOCITAS"
            else -> null
        }
        if (nuevaCorona != null) {
            val coronaExistente = coronasExistentes.find { it.tipoCorona == nuevaCorona }
            if (coronaExistente == null) {

                coronasActivas.removeAll { it.juego == juego }

                val mensaje = generarMensajeCorona(
                    tipoCorona = nuevaCorona,
                    nombreUsuario = usuarioActual,
                    nombreJuego = mapearNombreJuego(juego, context),
                    context = context
                )
                coronasActivas.add(
                    CoronaActiva(
                        juego = juego,
                        tipoCorona = nuevaCorona,
                        fechaAsignacion = System.currentTimeMillis(),
                        esNueva = true,
                        mensaje = mensaje
                    )
                )

                updateRedDotsStatus()
                saveCoronasActivas()
            }

        } else {

            if (coronasExistentes.isNotEmpty()) {
                coronasActivas.removeAll { it.juego == juego }
                saveCoronasActivas()
                updateRedDotsStatus()
            }
        }
    }

    private fun generarMensajeCorona(tipoCorona: String, nombreUsuario: String, nombreJuego: String, context: Context): String {
        val random = kotlin.random.Random.nextInt(1, 5)

        val stringResId = when (tipoCorona) {
            "VOLUCER" -> when (random) {
                1 -> R.string.corona_volucer_msg_1
                2 -> R.string.corona_volucer_msg_2
                3 -> R.string.corona_volucer_msg_3
                else -> R.string.corona_volucer_msg_4
            }
            "CELERIS" -> when (random) {
                1 -> R.string.corona_celeris_msg_1
                2 -> R.string.corona_celeris_msg_2
                3 -> R.string.corona_celeris_msg_3
                else -> R.string.corona_celeris_msg_4
            }
            "VELOCITAS" -> when (random) {
                1 -> R.string.corona_velocitas_msg_1
                2 -> R.string.corona_velocitas_msg_2
                3 -> R.string.corona_velocitas_msg_3
                else -> R.string.corona_velocitas_msg_4
            }
            else -> R.string.corona_velocitas_msg_1
        }

        return context.getString(stringResId, nombreUsuario, nombreJuego)
    }

    private fun mapearNombreJuego(juegoInterno: String, context: Context): String {
        return when (juegoInterno) {
            "NumerosPlus" -> context.getString(R.string.game_numeros_plus)
            "DeciPlus" -> context.getString(R.string.game_deci_plus)
            "Romas" -> context.getString(R.string.game_romas)
            "AlfaNumeros" -> context.getString(R.string.game_alfa_numeros)
            "SumaResta" -> context.getString(R.string.game_sumaresta)
            "MasPlus" -> context.getString(R.string.game_mas_plus)
            "GenioPlus" -> context.getString(R.string.game_genio_plus)
            else -> juegoInterno
        }
    }


    private fun hasNewCrowns(): Boolean {
        return coronasActivas.any { it.esNueva }
    }

    private fun hasNewTop10(): Boolean {
        return condecoracionesTop10.any { it.esNueva }
    }

    private fun hasNewMedals(): Boolean {
        return medallasObtenidas.any { !it.vista }
    }

    private fun hasNewTrophies(): Boolean {
        val hasNewRegularTrophies = trofeosObtenidos.any { !it.visto }
        val hasNewApex = apexSupremusObtenida?.let { !it.vista } ?: false
        return hasNewRegularTrophies || hasNewApex
    }

    private fun updateRedDotsStatus() {
        val hasNewPins = pinesObtenidos.any { !it.visto }
        val hasNewCrowns = hasNewCrowns()
        val hasNewTop10 = hasNewTop10()
        val hasNewMedals = hasNewMedals()
        val hasNewTrophies = hasNewTrophies()
        val hasNewIQ7 = hasNewIQ7()
        val hasNewTop5Integral = hasNewTop5Integral()

        trophyRedDotVisible = hasNewPins || hasNewCrowns || hasNewTop10 || hasNewMedals || hasNewTrophies || hasNewIQ7 || hasNewTop5Integral
        misCondecoracionesRedDotVisible = hasNewPins || hasNewCrowns || hasNewTop10 || hasNewMedals || hasNewTrophies || hasNewIQ7 || hasNewTop5Integral

        preferences.edit {
            putBoolean(KEY_TROPHY_RED_DOT, hasNewPins || hasNewCrowns || hasNewTop10 || hasNewMedals || hasNewTrophies || hasNewIQ7 || hasNewTop5Integral)
            putBoolean(KEY_MIS_CONDECORACIONES_RED_DOT, hasNewPins || hasNewCrowns || hasNewTop10 || hasNewMedals || hasNewTrophies || hasNewIQ7 || hasNewTop5Integral)
        }
    }

    fun marcarCoronasComoVistas() {
        var cambiosRealizados = false
        coronasActivas.forEach { corona ->
            if (corona.esNueva) {
                val index = coronasActivas.indexOf(corona)
                if (index != -1) {
                    coronasActivas[index] = corona.copy(esNueva = false)
                    cambiosRealizados = true
                }
            }
        }
        if (cambiosRealizados) {
            saveCoronasActivas()
            updateRedDotsStatus()
        }
    }

    fun marcarCoronaIndividualComoVista(juego: String, tipoCorona: String, fechaAsignacion: Long) {
        var cambiosRealizados = false
        coronasActivas.forEach { corona ->
            if (corona.juego == juego &&
                corona.tipoCorona == tipoCorona &&
                corona.fechaAsignacion == fechaAsignacion &&
                corona.esNueva
            ) {
                val index = coronasActivas.indexOf(corona)
                if (index != -1) {
                    coronasActivas[index] = corona.copy(esNueva = false)
                    cambiosRealizados = true
                }
            }
        }
        if (cambiosRealizados) {
            saveCoronasActivas()
            updateRedDotsStatus()
        }
    }

    fun marcarCondecoracionesTop10ComoVistas() {
        var cambiosRealizados = false
        condecoracionesTop10.forEach { condecoracion ->
            if (condecoracion.esNueva) {
                val index = condecoracionesTop10.indexOf(condecoracion)
                if (index != -1) {
                    condecoracionesTop10[index] = condecoracion.copy(esNueva = false)
                    cambiosRealizados = true
                }
            }
        }
        if (cambiosRealizados) {
            saveCondecoracionesTop10()
            updateRedDotsStatus()
        }
    }

    fun marcarCondecoracionTop10IndividualComoVista(posicion: Int, tipoCondecoracion: String, fechaAsignacion: Long) {
        var cambiosRealizados = false
        condecoracionesTop10.forEach { condecoracion ->
            if (condecoracion.posicion == posicion &&
                condecoracion.tipoCondecoracion == tipoCondecoracion &&
                condecoracion.fechaAsignacion == fechaAsignacion &&
                condecoracion.esNueva
            ) {
                val index = condecoracionesTop10.indexOf(condecoracion)
                if (index != -1) {
                    condecoracionesTop10[index] = condecoracion.copy(esNueva = false)
                    cambiosRealizados = true
                }
            }
        }
        if (cambiosRealizados) {
            saveCondecoracionesTop10()
            updateRedDotsStatus()
        }
    }

    fun shouldShowTrophyRedDot(): Boolean = trophyRedDotVisible || hasNewCrowns() || hasNewTop10() || hasNewMedals() || hasNewTrophies() || hasNewTop5Integral()
    fun shouldShowMisCondecoracionesRedDot(): Boolean = misCondecoracionesRedDotVisible || hasNewCrowns() || hasNewTop10() || hasNewMedals() || hasNewTrophies() || hasNewTop5Integral()
}
