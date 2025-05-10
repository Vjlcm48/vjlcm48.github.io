package com.example.sumamente.ui.utils

fun String.isPositiveNumber(): Boolean {
    val number = this.toIntOrNull()
    return number != null && number > 0
}