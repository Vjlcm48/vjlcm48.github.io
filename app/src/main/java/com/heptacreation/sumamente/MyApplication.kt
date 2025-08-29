package com.heptacreation.sumamente

import android.app.Application
import com.heptacreation.sumamente.ui.ScoreManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        com.google.firebase.FirebaseApp.initializeApp(this)

        ScoreManager.init(this)
    }
}
