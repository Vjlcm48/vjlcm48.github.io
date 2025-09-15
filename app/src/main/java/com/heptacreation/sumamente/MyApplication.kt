package com.heptacreation.sumamente

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.heptacreation.sumamente.ui.ScoreManager
import com.heptacreation.sumamente.ui.utils.PlayStoreReferrerReceiver
import androidx.core.content.edit
import com.google.firebase.firestore.SetOptions

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        com.google.firebase.FirebaseApp.initializeApp(this)

        ScoreManager.init(this)

        inicializarUsuarioUnico()

        val prefsReferral = getSharedPreferences("ReferralPrefs", MODE_PRIVATE)
        if (!prefsReferral.getBoolean("install_referrer_captured", false)) {
            PlayStoreReferrerReceiver.captureInstallReferrer(applicationContext)
            prefsReferral.edit { putBoolean("install_referrer_captured", true) }
        }
    }

    private fun inicializarUsuarioUnico() {
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {

            auth.signInAnonymously()
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid
                    android.util.Log.d("AuthInit", "Usuario anónimo creado: $userId")

                    if (userId != null) {

                        crearDocumentoUsuarioCompleto(userId)
                    } else {
                        android.util.Log.e("AuthInit", "Usuario creado pero UID es nulo")
                    }
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("AuthInit", "Error creando usuario anónimo", exception)
                }
        } else {
            val userId = auth.currentUser?.uid
            android.util.Log.d("AuthInit", "Usuario anónimo existente: $userId")
            if (userId != null) {
                actualizarDatosUsuarioExistente(userId)
            }
        }
    }

    private fun crearDocumentoUsuarioCompleto(userId: String) {

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->

                val datosCompletos = mapOf(
                    "lastActive" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                    "fcmToken" to token,
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )

                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(userId)
                    .set(datosCompletos, SetOptions.merge())
                    .addOnSuccessListener {
                        android.util.Log.d("AuthInit", "Usuario completo creado con token: $userId")
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("AuthInit", "Error creando usuario completo", e)
                    }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AuthInit", "Error obteniendo token FCM para nuevo usuario", e)
                // Crear usuario sin token por ahora
                actualizarDatosUsuarioExistente(userId)
            }
    }

    private fun actualizarDatosUsuarioExistente(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .set(
                mapOf("lastActive" to com.google.firebase.firestore.FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
            .addOnSuccessListener {
                android.util.Log.d("AuthInit", "lastActive actualizado para: $userId")
                // Solo después de actualizar datos, obtener/actualizar token
                obtenerYActualizarToken(userId)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AuthInit", "Error actualizando lastActive", e)
            }
    }

    private fun obtenerYActualizarToken(userId: String) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(userId)
                    .set(mapOf("fcmToken" to token), SetOptions.merge())
                    .addOnSuccessListener {
                        android.util.Log.d("AuthInit", "Token actualizado para usuario existente: $userId")
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("AuthInit", "Error actualizando token", e)
                    }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AuthInit", "Error obteniendo token FCM", e)
            }
    }
}
