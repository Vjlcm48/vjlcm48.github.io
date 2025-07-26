package com.example.sumamente.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class GatewayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("savedUserName", null)

        val targetActivity = if (username == null) {

            LanguageSelectionActivity::class.java
        } else {

            MainGameActivity::class.java
        }

        startActivity(Intent(this, targetActivity))
        finish()
    }
}