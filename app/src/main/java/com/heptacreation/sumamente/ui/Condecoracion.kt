package com.heptacreation.sumamente.ui

data class Condecoracion(
    val tipo: TipoCondecoracion,
    val nombre: String,
    val descripcion: String,
    val imagen: Int,
    val esNuevo: Boolean = false,
    val fechaObtencion: String? = null
)

enum class TipoCondecoracion {
    PIN, MEDALLA, TROFEO, CORONA, TOP10,
    DOBLE_CELEBRACION, DOBLE_CELEBRACION_APEX, APEX, IQ7, TOP5_INTEGRAL, INSIGNIA_RI_PLUS
}
