package com.heptacreation.sumamente.ui.utils

fun String.isPositiveNumber(): Boolean {
    val number = this.toIntOrNull()
    return number != null && number > 0
}