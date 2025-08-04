package com.example.sumamente.ui

import android.content.Context
import androidx.core.content.edit
import com.example.sumamente.R

object LanguageManager {

    val defaultLanguages = listOf(
        LanguageSelectionActivity.LanguageItem("en", R.string.language_english, R.drawable.us),
        LanguageSelectionActivity.LanguageItem("es", R.string.language_spanish, R.drawable.es),
        LanguageSelectionActivity.LanguageItem("hi", R.string.language_hindi, R.drawable.`in`),
        LanguageSelectionActivity.LanguageItem("fr", R.string.language_french, R.drawable.fr),
        LanguageSelectionActivity.LanguageItem("pt", R.string.language_portuguese, R.drawable.br),
        LanguageSelectionActivity.LanguageItem("ar", R.string.language_arabic, R.drawable.sa),
        LanguageSelectionActivity.LanguageItem("bn", R.string.language_bengali, R.drawable.bd),
        LanguageSelectionActivity.LanguageItem("id", R.string.language_indonesian, R.drawable.id),
        LanguageSelectionActivity.LanguageItem("ur", R.string.language_urdu, R.drawable.pk),
        LanguageSelectionActivity.LanguageItem("de", R.string.language_german, R.drawable.de),
        LanguageSelectionActivity.LanguageItem("ja", R.string.language_japanese, R.drawable.jp),
        LanguageSelectionActivity.LanguageItem("ko", R.string.language_korean, R.drawable.kr)
    )

    private const val PREFS_NAME = "MyPrefs"
    private const val KEY_LANGUAGE_ORDER = "language_order"

    fun getOrderedLanguages(context: Context): List<LanguageSelectionActivity.LanguageItem> {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedOrder = preferences.getString(KEY_LANGUAGE_ORDER, null)

        if (savedOrder.isNullOrEmpty()) {
            return defaultLanguages
        }

        val orderedCodes = savedOrder.split(',')
        return defaultLanguages.sortedBy { orderedCodes.indexOf(it.code) }
    }

    fun saveNewLanguageOrder(context: Context, newFirstLanguageCode: String) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val newOrderedList = defaultLanguages.toMutableList()
        val itemToMove = newOrderedList.find { it.code == newFirstLanguageCode }
        if (itemToMove != null) {
            newOrderedList.remove(itemToMove)
            newOrderedList.add(0, itemToMove)
        }

        val newOrderString = newOrderedList.joinToString(",") { it.code }
        preferences.edit {
            putString(KEY_LANGUAGE_ORDER, newOrderString)
            apply()
        }
    }
}