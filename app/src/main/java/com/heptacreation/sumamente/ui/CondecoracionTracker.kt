package com.heptacreation.sumamente.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.DataSyncManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.TimeZone

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
    private const val KEY_INSIGNIA_RI_PLUS = "insignia_ri_plus_obtenida"
    private const val KEY_LAST_RI_PLUS_CHECK_DATE = "last_ri_plus_check_date"

    private val TOP10_CONFIG = mapOf(
        1 to "EXCELSITUR",
        2 to "SUMMUM",
        3 to "MAGNANIMOUS",
        4 to "VENERABILIS",
        5 to "GLORIOSUS",
        6 to "ILLUSTRIS",
        7 to "PRAESTANS",
        8 to "INSIGNIS",
        9 to "VIRTUOSUS",
        10 to "HONORABILIS"
    )

    private val IQ7_CONFIG = mapOf(
        1 to "SAPIENS_SUPREMUS",
        2 to "MENTIS_AUREA",
        3 to "LUMINIS_REX",
        4 to "DOCTRINAE_PRINCEPS",
        5 to "CONSILIUM_MAGNUS",
        6 to "INTELLECTUS_PRIMUS",
        7 to "DISCIPULUS_OPTIMUS"
    )

    private val TOP5_INTEGRAL_CONFIG = mapOf(
        1 to "IMPERIUM_SUPREMUS",
        2 to "MAGNUS_HONOR",
        3 to "VIRTUS_TOTALIS",
        4 to "EXCELLENTIA_SINGULARI",
        5 to "GLORIA_INTEGRALIS"
    )

    private val GAME_NAME_MAP = mapOf(
        "NumerosPlus" to R.string.game_numeros_plus,
        "DeciPlus" to R.string.game_deci_plus,
        "Romas" to R.string.game_romas,
        "AlfaNumeros" to R.string.game_alfa_numeros,
        "SumaResta" to R.string.game_sumaresta,
        "MasPlus" to R.string.game_mas_plus,
        "GenioPlus" to R.string.game_genio_plus
    )

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

    data class InsigniaRIPlus(
        val fechaObtencion: Long,
        var vista: Boolean = false
    )

    enum class MedallaType(val requiredLevels: Int, val nombreMedalla: String) {
        INITIUM(100, "INITIUM"),
        FIDELIS(200, "FIDELIS"),
        VIRTUS(300, "VIRTUS"),
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
    private var insigniaRIPlus: InsigniaRIPlus? = null
    private var lastRIPlusCheckDate: String = ""
    private var trophyRedDotVisible: Boolean = false
    private var misCondecoracionesRedDotVisible: Boolean = false

    private inline fun <reified T> saveList(key: String, list: List<T>) {
        val jsonString = gson.toJson(list)
        preferences.edit {
            putString(key, jsonString)
        }
    }

    private inline fun <reified T> loadList(key: String): MutableList<T> {
        val jsonString = preferences.getString(key, null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<MutableList<T>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private inline fun <reified T> saveObject(key: String, obj: T?) {
        val jsonString = gson.toJson(obj)
        preferences.edit {
            putString(key, jsonString)
        }
    }

    private inline fun <reified T> loadObject(key: String, clazz: Class<T>): T? {
        val jsonString = preferences.getString(key, null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, clazz)
        } else {
            null
        }
    }

    private inline fun <reified T> marcarComoVisto(
        lista: MutableList<T>,
        condicion: (T) -> Boolean,
        transformar: (T) -> T,
        guardar: () -> Unit
    ) {
        var cambiosRealizados = false

        lista.forEachIndexed { index, item ->
            if (condicion(item)) {
                lista[index] = transformar(item)
                cambiosRealizados = true
            }
        }

        if (cambiosRealizados) {
            guardar()
            updateRedDotsStatus()
        }
    }

    private inline fun verificarDiariamente(
        lastCheckDateKey: String,
        lastCheckDate: String,
        updateLastCheckDate: (String) -> Unit,
        procesarDia: () -> Unit
    ) {
        val currentDate = getCurrentDateChicago()
        if (lastCheckDate != currentDate) {
            procesarDia()
            updateLastCheckDate(currentDate)
            preferences.edit {
                putString(lastCheckDateKey, currentDate)
            }
        }
    }

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        completedLevelsUnified = loadList(KEY_COMPLETED_LEVELS_JSON)
        pinesObtenidos = loadList(KEY_PINS_OBTAINED)
        carryOverLevels = loadList(KEY_CARRY_OVER_LEVELS)
        coronasActivas = loadList(KEY_CORONAS_ACTIVAS)
        condecoracionesTop10 = loadList(KEY_CONDECORACIONES_TOP10)
        condecoracionesIQ7 = loadList(KEY_CONDECORACIONES_IQ7)
        condecoracionesTop5Integral = loadList(KEY_CONDECORACIONES_TOP5_INTEGRAL)
        medallasObtenidas = loadList(KEY_MEDALLAS_OBTENIDAS)
        trofeosObtenidos = loadList(KEY_TROFEOS_OBTENIDOS)

        apexSupremusObtenida = loadObject(KEY_APEX_SUPREMUS_OBTENIDA, ApexSupremusObtenida::class.java)
        insigniaRIPlus = loadObject(KEY_INSIGNIA_RI_PLUS, InsigniaRIPlus::class.java)

        lastPinCheckDate = preferences.getString(KEY_LAST_PIN_CHECK_DATE, "") ?: ""
        lastCoronaCheckDate = preferences.getString(KEY_LAST_CORONA_CHECK_DATE, "") ?: ""
        lastTop10CheckDate = preferences.getString(KEY_LAST_TOP10_CHECK_DATE, "") ?: ""
        lastIQ7CheckDate = preferences.getString(KEY_LAST_IQ7_CHECK_DATE, "") ?: ""
        lastTop5IntegralCheckDate = preferences.getString(KEY_LAST_TOP5_INTEGRAL_CHECK_DATE, "") ?: ""
        lastRIPlusCheckDate = preferences.getString(KEY_LAST_RI_PLUS_CHECK_DATE, "") ?: ""

        trophyRedDotVisible = preferences.getBoolean(KEY_TROPHY_RED_DOT, false)
        misCondecoracionesRedDotVisible = preferences.getBoolean(KEY_MIS_CONDECORACIONES_RED_DOT, false)
    }

    fun clearGlobalRedDotFlags() {
        trophyRedDotVisible = false
        misCondecoracionesRedDotVisible = false
        preferences.edit {
            putBoolean(KEY_TROPHY_RED_DOT, false)
            putBoolean(KEY_MIS_CONDECORACIONES_RED_DOT, false)
        }
    }

    private fun saveCompletedLevelsUnified() = saveList(KEY_COMPLETED_LEVELS_JSON, completedLevelsUnified)
    private fun savePinesObtenidos() = saveList(KEY_PINS_OBTAINED, pinesObtenidos)
    private fun saveCarryOverLevels() = saveList(KEY_CARRY_OVER_LEVELS, carryOverLevels)
    private fun saveCoronasActivas() = saveList(KEY_CORONAS_ACTIVAS, coronasActivas)
    private fun saveCondecoracionesTop10() = saveList(KEY_CONDECORACIONES_TOP10, condecoracionesTop10)
    private fun saveCondecoracionesIQ7() = saveList(KEY_CONDECORACIONES_IQ7, condecoracionesIQ7)
    private fun saveCondecoracionesTop5Integral() = saveList(KEY_CONDECORACIONES_TOP5_INTEGRAL, condecoracionesTop5Integral)
    private fun saveMedallas() = saveList(KEY_MEDALLAS_OBTENIDAS, medallasObtenidas)
    private fun saveTrofeos() = saveList(KEY_TROFEOS_OBTENIDOS, trofeosObtenidos)
    private fun saveApexSupremus() = saveObject(KEY_APEX_SUPREMUS_OBTENIDA, apexSupremusObtenida)
    private fun saveInsigniaRIPlus() = saveObject(KEY_INSIGNIA_RI_PLUS, insigniaRIPlus)

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
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_PIN_CHECK_DATE,
            lastCheckDate = lastPinCheckDate,
            updateLastCheckDate = { lastPinCheckDate = it },
            procesarDia = {
                val yesterday = getYesterdayDateChicago()
                procesarPinesDelDia(yesterday)
            }
        )
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

    fun marcarPinIndividualComoVisto(pinTipo: String, fechaObtencion: Long) {
        marcarComoVisto(
            lista = pinesObtenidos,
            condicion = { pin ->
                pin.tipo == pinTipo && pin.fechaObtencion == fechaObtencion && !pin.visto
            },
            transformar = { pin ->
                pin.copy(visto = true)
            },
            guardar = ::savePinesObtenidos
        )
    }

    fun getAllPines(): List<PinObtained> = pinesObtenidos.toList()

    fun verificarYActualizarCoronasDeVelocidad(context: Context) {
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_CORONA_CHECK_DATE,
            lastCheckDate = lastCoronaCheckDate,
            updateLastCheckDate = { lastCoronaCheckDate = it },
            procesarDia = { procesarCoronasDelDia(context) }
        )
    }

    fun verificarYActualizarCondecoracionesTop10(context: Context) {
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_TOP10_CHECK_DATE,
            lastCheckDate = lastTop10CheckDate,
            updateLastCheckDate = { lastTop10CheckDate = it },
            procesarDia = { procesarCondecoracionesTop10DelDia(context) }
        )
    }

    private fun procesarCondecoracionesTop10DelDia(context: Context) {
        consultarPosicionRankingGlobalAsync(context) { posicionUsuario ->
            actualizarCondecoracionTop10DelUsuario(posicionUsuario, context)
        }
    }

    private fun consultarPosicionRankingGlobalAsync(
        context: Context,
        callback: (Int) -> Unit
    ) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"
        val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"
        val totalPoints = calculateTotalGlobalPoints()

        DataSyncManager.getTopGlobalRanking(
            userId = getUserId(context),
            userName = username,
            country = countryCode,
            totalPoints = totalPoints
        ) { _, userPosition, _ ->
            callback(if (userPosition > 0) userPosition else -1)
        }
    }

    private fun actualizarCondecoracionTop10DelUsuario(posicion: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioActual = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"

        if (posicion in 1..10) {
            val yaExiste = condecoracionesTop10.any { it.posicion == posicion }

            if (!yaExiste) {
                val tipoCondecoracion = TOP10_CONFIG[posicion] ?: ""
                val mensaje = generarMensajeTop10(tipoCondecoracion, usuarioActual, context)

                condecoracionesTop10.add(
                    CondecoracionTop10(
                        posicion = posicion,
                        tipoCondecoracion = tipoCondecoracion,
                        fechaAsignacion = System.currentTimeMillis(),
                        esNueva = true,
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
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_IQ7_CHECK_DATE,
            lastCheckDate = lastIQ7CheckDate,
            updateLastCheckDate = { lastIQ7CheckDate = it },
            procesarDia = { procesarCondecoracionesIQ7DelDia(context) }
        )
    }

    private fun procesarCondecoracionesIQ7DelDia(context: Context) {
        consultarPosicionRankingIQ7Async(context) { posicionUsuario ->
            actualizarCondecoracionIQ7DelUsuario(posicionUsuario, context)
        }
    }

    private fun consultarPosicionRankingIQ7Async(
        context: Context,
        callback: (Int) -> Unit
    ) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"
        val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"
        val iqPlus = ScoreManager.lastIqComponentByGame.values.sum()

        DataSyncManager.getTopIQPlusRanking(
            userId = getUserId(context),
            userName = username,
            country = countryCode,
            iqPlus = iqPlus
        ) { _, userPosition, _ ->
            callback(if (userPosition > 0) userPosition else -1)
        }
    }

    private fun actualizarCondecoracionIQ7DelUsuario(posicion: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioActual = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"

        if (posicion in 1..7) {
            val yaExiste = condecoracionesIQ7.any { it.posicion == posicion }

            if (!yaExiste) {
                val tipoCondecoracion = IQ7_CONFIG[posicion] ?: ""
                val mensaje = generarMensajeIQ7(tipoCondecoracion, usuarioActual, posicion, context)

                condecoracionesIQ7.add(
                    CondecoracionIQ7(
                        posicion = posicion,
                        tipoCondecoracion = tipoCondecoracion,
                        fechaAsignacion = System.currentTimeMillis(),
                        esNueva = true,
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

    fun marcarCondecoracionIQ7IndividualComoVista(posicion: Int, tipoCondecoracion: String, fechaAsignacion: Long) {
        marcarComoVisto(
            lista = condecoracionesIQ7,
            condicion = { condecoracion ->
                condecoracion.posicion == posicion &&
                        condecoracion.tipoCondecoracion == tipoCondecoracion &&
                        condecoracion.fechaAsignacion == fechaAsignacion &&
                        condecoracion.esNueva
            },
            transformar = { condecoracion ->
                condecoracion.copy(esNueva = false)
            },
            guardar = ::saveCondecoracionesIQ7
        )
    }

    fun verificarYActualizarCondecoracionesTop5Integral(context: Context) {
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_TOP5_INTEGRAL_CHECK_DATE,
            lastCheckDate = lastTop5IntegralCheckDate,
            updateLastCheckDate = { lastTop5IntegralCheckDate = it },
            procesarDia = { procesarCondecoracionesTop5IntegralDelDia(context) }
        )
    }

    private fun procesarCondecoracionesTop5IntegralDelDia(context: Context) {
        consultarPosicionRankingIntegralAsync(context) { posicionUsuario ->
            actualizarCondecoracionTop5IntegralDelUsuario(posicionUsuario, context)
        }
    }

    private fun consultarPosicionRankingIntegralAsync(
        context: Context,
        callback: (Int) -> Unit
    ) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"
        val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"
        val averagePosition = calculateUserAveragePosition()

        DataSyncManager.getTopIntegralRanking(
            userId = getUserId(context),
            userName = username,
            country = countryCode,
            averagePosition = averagePosition
        ) { _, userPosition, _ ->
            callback(if (userPosition > 0) userPosition else -1)
        }
    }

    private fun actualizarCondecoracionTop5IntegralDelUsuario(posicion: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioActual = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"

        if (posicion in 1..5) {
            val yaExiste = condecoracionesTop5Integral.any { it.posicion == posicion }

            if (!yaExiste) {
                val tipoCondecoracion = TOP5_INTEGRAL_CONFIG[posicion] ?: ""
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

    fun marcarCondecoracionTop5IntegralIndividualComoVista(posicion: Int, tipoCondecoracion: String, fechaAsignacion: Long) {
        marcarComoVisto(
            lista = condecoracionesTop5Integral,
            condicion = { condecoracion ->
                condecoracion.posicion == posicion &&
                        condecoracion.tipoCondecoracion == tipoCondecoracion &&
                        condecoracion.fechaAsignacion == fechaAsignacion &&
                        condecoracion.esNueva
            },
            transformar = { condecoracion ->
                condecoracion.copy(esNueva = false)
            },
            guardar = ::saveCondecoracionesTop5Integral
        )
    }

    fun verificarYActualizarInsigniaRIPlus(context: Context) {
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_RI_PLUS_CHECK_DATE,
            lastCheckDate = lastRIPlusCheckDate,
            updateLastCheckDate = { lastRIPlusCheckDate = it },
            procesarDia = { procesarInsigniaRIPlusDelDia(context) }
        )
    }

    private fun procesarInsigniaRIPlusDelDia(context: Context) {
        consultarPosicionRankingIntegralAsync(context) { posicionUsuario ->
            actualizarInsigniaRIPlusDelUsuario(posicionUsuario)
        }
    }

    private fun actualizarInsigniaRIPlusDelUsuario(posicion: Int) {

        if (posicion > 0) {
            val yaObtenida = insigniaRIPlus != null
            if (!yaObtenida) {
                val nuevaInsignia = InsigniaRIPlus(
                    fechaObtencion = System.currentTimeMillis(),
                    vista = false
                )

                insigniaRIPlus = nuevaInsignia
                saveInsigniaRIPlus()
                updateRedDotsStatus()
            }
        }

    }

    fun getInsigniaRIPlus(): InsigniaRIPlus? = insigniaRIPlus

    fun marcarInsigniaRIPlusComoVista() {
        insigniaRIPlus?.let { insignia ->
            if (!insignia.vista) {
                insigniaRIPlus = insignia.copy(vista = true)
                saveInsigniaRIPlus()
                updateRedDotsStatus()
            }
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
        marcarComoVisto(
            lista = medallasObtenidas,
            condicion = { medalla ->
                medalla.tipo == tipo && medalla.fechaObtencion == fechaObtencion && !medalla.vista
            },
            transformar = { medalla ->
                medalla.copy(vista = true)
            },
            guardar = ::saveMedallas
        )
    }

    fun verificarYEntregarTrofeos(juego: String, grado: String, callback: (TrofeoObtenido?) -> Unit) {
        val nuevoTrofeo = procesarTrofeoDelUsuario(juego, grado)
        callback(nuevoTrofeo)
    }

    private fun procesarTrofeoDelUsuario(juego: String, grado: String): TrofeoObtenido? {
        val nivelesCompletados = obtenerNivelesCompletadosPorJuegoGrado(juego, grado)

        if (nivelesCompletados >= 70) {
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
        marcarComoVisto(
            lista = trofeosObtenidos,
            condicion = { trofeo ->
                trofeo.juego == juego &&
                        trofeo.grado == grado &&
                        trofeo.fechaObtencion == fechaObtencion &&
                        !trofeo.visto
            },
            transformar = { trofeo ->
                trofeo.copy(visto = true)
            },
            guardar = ::saveTrofeos
        )
    }

    fun verificarYEntregarApexSupremus(callback: (ApexSupremusObtenida?) -> Unit) {
        val nuevaApex = procesarApexDelUsuario()
        callback(nuevaApex)
    }

    private fun procesarApexDelUsuario(): ApexSupremusObtenida? {
        val totalNivelesUnicos = ScoreManager.getTotalUniqueLevelsCompletedAllGames()
        val totalTrofeos = trofeosObtenidos.size

        if (totalNivelesUnicos >= 1470 && totalTrofeos >= 1) {
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

    private fun procesarCoronasDelDia(context: Context) {
        val juegos = listOf(
            "NumerosPlus", "DeciPlus", "Romas",
            "AlfaNumeros", "SumaResta", "MasPlus", "GenioPlus"
        )

        val latch = java.util.concurrent.CountDownLatch(juegos.size)

        juegos.forEach { juego ->
            consultarRankingVelocidadAsync(juego, context) { top3Users ->
                actualizarCoronasDelJuego(juego, top3Users, context)
                latch.countDown()
            }
        }

        latch.await(45, java.util.concurrent.TimeUnit.SECONDS)
    }

    private fun consultarRankingVelocidadAsync(
        juego: String,
        context: Context,
        callback: (List<String>) -> Unit
    ) {

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
            callback(emptyList())
            return
        }

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("savedUserName", "Usuario") ?: "Usuario"
        val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"
        val averageTime = when (juego) {
            "NumerosPlus" -> ScoreManager.getTiempoPromedioNumerosPlus()
            "DeciPlus" -> ScoreManager.getTiempoPromedioDeciPlus()
            "Romas" -> ScoreManager.getTiempoPromedioRomas()
            "AlfaNumeros" -> ScoreManager.getTiempoPromedioAlfaNumeros()
            "SumaResta" -> ScoreManager.getTiempoPromedioSumaResta()
            "MasPlus" -> ScoreManager.getTiempoPromedioMasPlus()
            "GenioPlus" -> ScoreManager.getTiempoPromedioGenioPlus()
            else -> 999.0
        }

        DataSyncManager.getTopSpeedRanking(
            userId = getUserId(context),
            userName = username,
            country = countryCode,
            gameType = juego,
            averageTime = averageTime
        ) { rankingList, _, _ ->

            val top3 = rankingList.take(3).map { it.username }
            callback(top3)
        }
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
        return GAME_NAME_MAP[juegoInterno]?.let { context.getString(it) } ?: juegoInterno
    }

    private fun hasNewPins(): Boolean = pinesObtenidos.any { !it.visto }
    private fun hasNewCrowns(): Boolean = coronasActivas.any { it.esNueva }
    private fun hasNewTop10(): Boolean = condecoracionesTop10.any { it.esNueva }
    private fun hasNewMedals(): Boolean = medallasObtenidas.any { !it.vista }
    private fun hasNewIQ7(): Boolean = condecoracionesIQ7.any { it.esNueva }
    private fun hasNewTop5Integral(): Boolean = condecoracionesTop5Integral.any { it.esNueva }
    private fun hasNewInsigniaRIPlus(): Boolean = insigniaRIPlus?.vista == false

    private fun hasNewTrophies(): Boolean {
        val hasNewRegularTrophies = trofeosObtenidos.any { !it.visto }
        val hasNewApex = apexSupremusObtenida?.vista == false
        return hasNewRegularTrophies || hasNewApex
    }

    private fun updateRedDotsStatus() {
        val hasAnyNew = hasNewPins() ||
                hasNewCrowns() ||
                hasNewTop10() ||
                hasNewMedals() ||
                hasNewTrophies() ||
                hasNewIQ7() ||
                hasNewTop5Integral() ||
                hasNewInsigniaRIPlus()

        if (trophyRedDotVisible != hasAnyNew || misCondecoracionesRedDotVisible != hasAnyNew) {
            trophyRedDotVisible = hasAnyNew
            misCondecoracionesRedDotVisible = hasAnyNew

            preferences.edit {
                putBoolean(KEY_TROPHY_RED_DOT, hasAnyNew)
                putBoolean(KEY_MIS_CONDECORACIONES_RED_DOT, hasAnyNew)
            }
        }
    }

    fun marcarCoronaIndividualComoVista(juego: String, tipoCorona: String, fechaAsignacion: Long) {
        marcarComoVisto(
            lista = coronasActivas,
            condicion = { corona ->
                corona.juego == juego &&
                        corona.tipoCorona == tipoCorona &&
                        corona.fechaAsignacion == fechaAsignacion &&
                        corona.esNueva
            },
            transformar = { corona ->
                corona.copy(esNueva = false)
            },
            guardar = ::saveCoronasActivas
        )
    }

    fun marcarCondecoracionTop10IndividualComoVista(posicion: Int, tipoCondecoracion: String, fechaAsignacion: Long) {
        marcarComoVisto(
            lista = condecoracionesTop10,
            condicion = { condecoracion ->
                condecoracion.posicion == posicion &&
                        condecoracion.tipoCondecoracion == tipoCondecoracion &&
                        condecoracion.fechaAsignacion == fechaAsignacion &&
                        condecoracion.esNueva
            },
            transformar = { condecoracion ->
                condecoracion.copy(esNueva = false)
            },
            guardar = ::saveCondecoracionesTop10
        )
    }

    fun shouldShowTrophyRedDot(): Boolean = trophyRedDotVisible
    fun shouldShowMisCondecoracionesRedDot(): Boolean = misCondecoracionesRedDotVisible

    // ====== INICIO: SYNC CONDECORACIONES (EXPORT / IMPORT) ======

    private data class CondecoracionData(
        val schemaVersion: Int = 2,

        val completedLevelsUnified: List<CompletedLevel> = emptyList(),
        val pinesObtenidos: List<PinObtained> = emptyList(),
        val carryOverLevels: List<CompletedLevel> = emptyList(),

        val coronasActivas: List<CoronaActiva> = emptyList(),
        val condecoracionesTop10: List<CondecoracionTop10> = emptyList(),
        val condecoracionesIQ7: List<CondecoracionIQ7> = emptyList(),
        val condecoracionesTop5Integral: List<CondecoracionTop5Integral> = emptyList(),

        val medallasObtenidas: List<MedallaObtenida> = emptyList(),
        val trofeosObtenidos: List<TrofeoObtenido> = emptyList(),
        val apexSupremusObtenida: ApexSupremusObtenida? = null,
        val insigniaRIPlus: InsigniaRIPlus? = null,

        val lastPinCheckDate: String = "",
        val lastCoronaCheckDate: String = "",
        val lastTop10CheckDate: String = "",
        val lastIQ7CheckDate: String = "",
        val lastTop5IntegralCheckDate: String = "",
        val lastRIPlusCheckDate: String = ""
    )

    fun exportAllDataAsJson(context: Context): String {

        if (!::preferences.isInitialized) {
            init(context)
        }

        val box = CondecoracionData(
            completedLevelsUnified = completedLevelsUnified,
            pinesObtenidos = pinesObtenidos,
            carryOverLevels = carryOverLevels,

            coronasActivas = coronasActivas,
            condecoracionesTop10 = condecoracionesTop10,
            condecoracionesIQ7 = condecoracionesIQ7,
            condecoracionesTop5Integral = condecoracionesTop5Integral,

            medallasObtenidas = medallasObtenidas,
            trofeosObtenidos = trofeosObtenidos,
            apexSupremusObtenida = apexSupremusObtenida,
            insigniaRIPlus = insigniaRIPlus,

            lastPinCheckDate = lastPinCheckDate,
            lastCoronaCheckDate = lastCoronaCheckDate,
            lastTop10CheckDate = lastTop10CheckDate,
            lastIQ7CheckDate = lastIQ7CheckDate,
            lastTop5IntegralCheckDate = lastTop5IntegralCheckDate,
            lastRIPlusCheckDate = lastRIPlusCheckDate
        )

        return gson.toJson(box)
    }

    fun importAllDataFromJson(context: Context, json: String) {
        if (!::preferences.isInitialized) {
            init(context)
        }

        val restored = runCatching { gson.fromJson(json, CondecoracionData::class.java) }.getOrNull()
            ?: return

        if (restored.schemaVersion != 2) {
            return
        }

        completedLevelsUnified = restored.completedLevelsUnified.toMutableList()
        pinesObtenidos = restored.pinesObtenidos.toMutableList()
        carryOverLevels = restored.carryOverLevels.toMutableList()

        coronasActivas = restored.coronasActivas.toMutableList()
        condecoracionesTop10 = restored.condecoracionesTop10.toMutableList()
        condecoracionesIQ7 = restored.condecoracionesIQ7.toMutableList()
        condecoracionesTop5Integral = restored.condecoracionesTop5Integral.toMutableList()

        medallasObtenidas = restored.medallasObtenidas.toMutableList()
        trofeosObtenidos = restored.trofeosObtenidos.toMutableList()
        apexSupremusObtenida = restored.apexSupremusObtenida
        insigniaRIPlus = restored.insigniaRIPlus

        lastPinCheckDate = restored.lastPinCheckDate
        lastCoronaCheckDate = restored.lastCoronaCheckDate
        lastTop10CheckDate = restored.lastTop10CheckDate
        lastIQ7CheckDate = restored.lastIQ7CheckDate
        lastTop5IntegralCheckDate = restored.lastTop5IntegralCheckDate
        lastRIPlusCheckDate = restored.lastRIPlusCheckDate

        saveCompletedLevelsUnified()
        savePinesObtenidos()
        saveCarryOverLevels()

        saveCoronasActivas()
        saveCondecoracionesTop10()
        saveCondecoracionesIQ7()
        saveCondecoracionesTop5Integral()

        saveMedallas()
        saveTrofeos()
        saveApexSupremus()
        saveInsigniaRIPlus()

        preferences.edit {
            putString(KEY_LAST_PIN_CHECK_DATE, lastPinCheckDate)
            putString(KEY_LAST_CORONA_CHECK_DATE, lastCoronaCheckDate)
            putString(KEY_LAST_TOP10_CHECK_DATE, lastTop10CheckDate)
            putString(KEY_LAST_IQ7_CHECK_DATE, lastIQ7CheckDate)
            putString(KEY_LAST_TOP5_INTEGRAL_CHECK_DATE, lastTop5IntegralCheckDate)
            putString(KEY_LAST_RI_PLUS_CHECK_DATE, lastRIPlusCheckDate)
        }

        updateRedDotsStatus()
    }

    private fun calculateTotalGlobalPoints(): Long {
        return (ScoreManager.currentScore + ScoreManager.currentScorePrincipiante +
                ScoreManager.currentScorePro + ScoreManager.currentScoreDeciPlus +
                ScoreManager.currentScoreDeciPlusPrincipiante + ScoreManager.currentScoreDeciPlusPro +
                ScoreManager.currentScoreRomas + ScoreManager.currentScoreRomasPrincipiante +
                ScoreManager.currentScoreRomasPro + ScoreManager.currentScoreAlfaNumeros +
                ScoreManager.currentScoreAlfaNumerosPrincipiante + ScoreManager.currentScoreAlfaNumerosPro +
                ScoreManager.currentScoreSumaResta + ScoreManager.currentScoreSumaRestaPrincipiante +
                ScoreManager.currentScoreSumaRestaPro + ScoreManager.currentScoreMasPlus +
                ScoreManager.currentScoreMasPlusPrincipiante + ScoreManager.currentScoreMasPlusPro +
                ScoreManager.currentScoreGenioPlus + ScoreManager.currentScoreGenioPlusPrincipiante +
                ScoreManager.currentScoreGenioPlusPro).toLong()
    }

    private fun calculateUserAveragePosition(): Double {
        val positions = listOf(
            ScoreManager.getUserPositionInRanking("GLOBAL"),
            ScoreManager.getUserPositionInRanking("VEL_NUMEROS"),
            ScoreManager.getUserPositionInRanking("VEL_DECI"),
            ScoreManager.getUserPositionInRanking("VEL_ALFANUM"),
            ScoreManager.getUserPositionInRanking("VEL_ROMAS"),
            ScoreManager.getUserPositionInRanking("VEL_SUMARESTA"),
            ScoreManager.getUserPositionInRanking("VEL_MAS"),
            ScoreManager.getUserPositionInRanking("VEL_GENIOS"),
            ScoreManager.getUserPositionInRanking("IQ_PLUS")
        )
        return positions.average()
    }

    private fun getUserId(context: Context): String {
        val user = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        } catch (_: Exception) {
            null
        }
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return user?.uid
            ?: sharedPreferences.getString("anonymous_user_id", null)
            ?: generateAnonymousUserId(context)
    }

    private fun generateAnonymousUserId(context: Context): String {
        val id = java.util.UUID.randomUUID().toString()
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit { putString("anonymous_user_id", id) }
        return id
    }

    fun verificarYActualizarCondecoracionesTop10Async(context: Context, onComplete: () -> Unit) {
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_TOP10_CHECK_DATE,
            lastCheckDate = lastTop10CheckDate,
            updateLastCheckDate = { lastTop10CheckDate = it },
            procesarDia = {
                procesarCondecoracionesTop10DelDia(context)
                onComplete()
            }
        )
    }

    fun verificarYActualizarCondecoracionesIQ7Async(context: Context, onComplete: () -> Unit) {
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_IQ7_CHECK_DATE,
            lastCheckDate = lastIQ7CheckDate,
            updateLastCheckDate = { lastIQ7CheckDate = it },
            procesarDia = {
                procesarCondecoracionesIQ7DelDia(context)
                onComplete()
            }
        )
    }

    fun verificarYActualizarCondecoracionesTop5IntegralAsync(context: Context, onComplete: () -> Unit) {
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_TOP5_INTEGRAL_CHECK_DATE,
            lastCheckDate = lastTop5IntegralCheckDate,
            updateLastCheckDate = { lastTop5IntegralCheckDate = it },
            procesarDia = {
                procesarCondecoracionesTop5IntegralDelDia(context)
                onComplete()
            }
        )
    }

    fun verificarYActualizarCoronasDeVelocidadAsync(context: Context, onComplete: () -> Unit) {
        verificarDiariamente(
            lastCheckDateKey = KEY_LAST_CORONA_CHECK_DATE,
            lastCheckDate = lastCoronaCheckDate,
            updateLastCheckDate = { lastCoronaCheckDate = it },
            procesarDia = {
                procesarCoronasDelDia(context)
                onComplete()
            }
        )
    }

}
