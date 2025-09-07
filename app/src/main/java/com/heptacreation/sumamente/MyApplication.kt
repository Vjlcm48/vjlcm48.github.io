package com.heptacreation.sumamente

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.heptacreation.sumamente.ui.ScoreManager
import com.heptacreation.sumamente.ui.utils.PlayStoreReferrerReceiver
import androidx.core.content.edit


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        com.google.firebase.FirebaseApp.initializeApp(this)

        ScoreManager.init(this)

        val prefsReferral = getSharedPreferences("ReferralPrefs", MODE_PRIVATE)
        if (!prefsReferral.getBoolean("install_referrer_captured", false)) {
            PlayStoreReferrerReceiver.captureInstallReferrer(applicationContext)
            prefsReferral.edit { putBoolean("install_referrer_captured", true) }
        }

        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { userId ->
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .update("lastActive", com.google.firebase.firestore.FieldValue.serverTimestamp())
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    val auth = FirebaseAuth.getInstance()
                    auth.currentUser?.uid?.let { userId ->
                        FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(userId)
                            .update("fcmToken", token)
                    }
                }
            }
    }
}
