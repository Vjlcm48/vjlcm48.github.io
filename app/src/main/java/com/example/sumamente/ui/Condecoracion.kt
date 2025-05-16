package com.example.sumamente.ui

data class Condecoracion(
    val tipo: TipoCondecoracion,
    val nombre: String,
    val descripcion: String,
    val imagen: Int
)

enum class TipoCondecoracion {
    PIN, MEDALLA, TROFEO,
    @Suppress("unused") CORONA,
    @Suppress("unused") TOP10,
    @Suppress("unused") APEX
}
