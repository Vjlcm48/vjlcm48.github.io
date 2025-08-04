package com.example.sumamente.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

abstract class BaseActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = getSavedLanguage(newBase)
        val updatedContext = updateContextWithLanguage(newBase, savedLanguage)
        super.attachBaseContext(updatedContext)
    }

    private fun getSavedLanguage(context: Context): String {
        val preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return preferences.getString("app_display_language", "es") ?: "es"
    }

    private fun updateContextWithLanguage(context: Context, languageCode: String): Context {
        val locale = Locale.Builder().setLanguage(languageCode).build()
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        return context.createConfigurationContext(configuration)
    }
}