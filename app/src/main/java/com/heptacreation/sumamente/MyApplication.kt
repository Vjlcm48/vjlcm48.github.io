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

        // Asegurar que exista un usuario anónimo antes de cualquier otra operación
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        android.util.Log.d("AuthInit", "Usuario anónimo creado: ${auth.currentUser?.uid}")
                        actualizarDatosUsuario(auth.currentUser?.uid)
                    } else {
                        android.util.Log.e("AuthInit", "Error creando usuario anónimo", task.exception)
                    }
                }
        } else {
            actualizarDatosUsuario(auth.currentUser?.uid)
        }

        // Capturar install referrer si aún no se hizo
        val prefsReferral = getSharedPreferences("ReferralPrefs", MODE_PRIVATE)
        if (!prefsReferral.getBoolean("install_referrer_captured", false)) {
            PlayStoreReferrerReceiver.captureInstallReferrer(applicationContext)
            prefsReferral.edit { putBoolean("install_referrer_captured", true) }
        }

        // Actualizar token FCM
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(userId)
                            .update("fcmToken", token)
                    }
                }
            }
    }

    private fun actualizarDatosUsuario(userId: String?) {
        if (userId == null) return
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .update("lastActive", com.google.firebase.firestore.FieldValue.serverTimestamp())
    }
}
