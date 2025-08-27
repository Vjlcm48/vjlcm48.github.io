package com.heptacreation.sumamente.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

abstract class BaseActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySelectedTheme()
    }

    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = getSavedLanguage(newBase)
        val updatedContext = updateContextWithLanguage(newBase, savedLanguage)
        super.attachBaseContext(updatedContext)
    }

    private fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        prefs.getString("selected_language", null)?.let { return it }
        val isFirstRun = prefs.getBoolean("is_first_run", true)
        if (isFirstRun) {

            val deviceLang = context.resources.configuration.locales[0]
                .language.lowercase(Locale.getDefault())
            val isSupported = LanguageManager.defaultLanguages.any {
                it.code.equals(deviceLang, ignoreCase = true)
            }

            if (isSupported) {
                return deviceLang
            }
        }
        return "en"
    }

    private fun updateContextWithLanguage(context: Context, languageCode: String): Context {
        val locale = Locale.Builder().setLanguage(languageCode).build()
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun applySelectedTheme() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        when (prefs.getString("selected_theme", "device")) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "device" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}