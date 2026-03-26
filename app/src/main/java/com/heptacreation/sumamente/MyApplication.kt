package com.heptacreation.sumamente

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.heptacreation.sumamente.ui.ScoreManager
import com.heptacreation.sumamente.ui.utils.AppSecurityManager
import com.heptacreation.sumamente.ui.utils.PlayStoreReferrerReceiver

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        val firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(com.google.firebase.firestore.FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings

        ScoreManager.init(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onStart(owner: LifecycleOwner) {
                AppSecurityManager.verifyActiveDeviceIfNeeded(applicationContext) { authorized ->
                    if (authorized) {
                        setAppState()
                        updateLastActive()
                        resetWelcomeCounterIfNeeded()
                    }
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                val appStateRequest =
                    androidx.work.OneTimeWorkRequestBuilder<com.heptacreation.sumamente.ui.utils.AppStateWorker>()
                        .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .build()
                androidx.work.WorkManager.getInstance(applicationContext).enqueue(appStateRequest)
            }
        })

        inicializarUsuarioUnico()

        val prefsReferral = getSharedPreferences("ReferralPrefs", MODE_PRIVATE)
        if (!prefsReferral.getBoolean("install_referrer_captured", false)) {
            PlayStoreReferrerReceiver.captureInstallReferrer(applicationContext)
            prefsReferral.edit { putBoolean("install_referrer_captured", true) }
        }
    }

    private fun setAppState() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .set(mapOf("appState" to "abierta"), SetOptions.merge())
            .addOnSuccessListener { android.util.Log.d("AppState", "appState=abierta") }
            .addOnFailureListener { e -> android.util.Log.e("AppState", "Error appState", e) }
    }

    private fun updateLastActive() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .set(mapOf("lastActive" to FieldValue.serverTimestamp()), SetOptions.merge())
            .addOnSuccessListener { android.util.Log.d("AppState", "lastActive actualizado") }
            .addOnFailureListener { e -> android.util.Log.e("AppState", "Error lastActive", e) }
    }

    private fun inicializarUsuarioUnico() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            android.util.Log.d(
                "AuthInit",
                "No hay currentUser. Se creará nueva sesión anónima sin borrar datos locales restaurados."
            )
            crearNuevaSesionAnonima()
            return
        }

        val userId = currentUser.uid
        android.util.Log.d("AuthInit", "Usuario existente detectado: $userId")

        val tokenTimeout = android.os.Handler(android.os.Looper.getMainLooper())
        val tokenTimeoutRunnable = Runnable {
            android.util.Log.w(
                "AuthInit",
                "getIdToken timeout — procediendo con sesión actual para UID: $userId"
            )
            actualizarDatosUsuarioExistente(userId)
        }
        tokenTimeout.postDelayed(tokenTimeoutRunnable, 5000)

        currentUser.getIdToken(true)
            .addOnSuccessListener {
                tokenTimeout.removeCallbacks(tokenTimeoutRunnable)
                android.util.Log.d("AuthInit", "Sesión válida confirmada para UID: $userId")
                actualizarDatosUsuarioExistente(userId)
            }
            .addOnFailureListener { e ->
                tokenTimeout.removeCallbacks(tokenTimeoutRunnable)

                val esErrorDeRed =
                    e is com.google.firebase.FirebaseNetworkException ||
                            e.message?.contains("network", ignoreCase = true) == true ||
                            e.message?.contains("timeout", ignoreCase = true) == true

                if (esErrorDeRed) {
                    android.util.Log.w(
                        "AuthInit",
                        "Fallo de red en getIdToken — manteniendo sesión existente para UID: $userId"
                    )
                    actualizarDatosUsuarioExistente(userId)
                } else {
                    android.util.Log.w(
                        "AuthInit",
                        "Sesión inválida detectada. Se limpiará solo rastro de sesión, no progreso local: ${e.message}"
                    )
                    limpiarRastrosLocalesDeSesion()
                    crearNuevaSesionAnonima()
                }
            }
    }

    private fun limpiarRastrosLocalesDeSesion() {
        val authPrefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        authPrefs.edit { remove("saved_uid") }
    }

    private fun crearNuevaSesionAnonima() {
        val auth = FirebaseAuth.getInstance()

        auth.signInAnonymously()
            .addOnSuccessListener { authResult ->
                val newUserId = authResult.user?.uid
                android.util.Log.d("AuthInit", "Nueva sesión anónima creada: $newUserId")
                if (newUserId != null) {
                    crearDocumentoUsuarioCompleto(newUserId)
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AuthInit", "Error creando nueva sesión anónima", e)
            }
    }

    private fun crearDocumentoUsuarioCompleto(userId: String) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                val datosCompletos = mapOf(
                    "createdAt" to FieldValue.serverTimestamp(),
                    "lastActive" to FieldValue.serverTimestamp(),
                    "fcmToken" to token,
                    "appState" to "abierta"
                )

                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(userId)
                    .set(datosCompletos, SetOptions.merge())
                    .addOnSuccessListener {
                        android.util.Log.d("AuthInit", "Usuario completo creado: $userId")
                        guardarUsuarioPersistente(userId)
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("AuthInit", "Error creando usuario completo", e)
                    }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AuthInit", "Error obteniendo token FCM para nuevo usuario", e)
                actualizarDatosUsuarioExistente(userId)
            }
    }

    private fun actualizarDatosUsuarioExistente(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .set(
                mapOf(
                    "lastActive" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .addOnSuccessListener {
                android.util.Log.d("AuthInit", "lastActive/appState actualizados: $userId")
                guardarUsuarioPersistente(userId)
                obtenerYActualizarToken(userId)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AuthInit", "Error actualizando lastActive/appState", e)
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
                        android.util.Log.d("AuthInit", "Token FCM actualizado: $userId")
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("AuthInit", "Error actualizando token FCM", e)
                    }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AuthInit", "Error obteniendo token FCM", e)
            }
    }

    private fun guardarUsuarioPersistente(uid: String) {
        val prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        prefs.edit { putString("saved_uid", uid) }
    }

    private fun resetWelcomeCounterIfNeeded() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val totalLevels = ScoreManager.getTotalUniqueLevelsCompletedAllGames()
        if (totalLevels < 25) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .update(
                    mapOf(
                        "welcomeMissCounter" to 0,
                        "lastWelcomeNotificationSent" to FieldValue.delete()
                    )
                )
                .addOnSuccessListener {
                    android.util.Log.d(
                        "WelcomeReset",
                        "welcomeMissCounter y lastWelcomeNotificationSent reseteados para $userId (niveles: $totalLevels)"
                    )
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("WelcomeReset", "Error reseteando campos welcome", e)
                }
        } else {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .update(
                    mapOf(
                        "welcomeMissCounter" to FieldValue.delete(),
                        "lastWelcomeNotificationSent" to FieldValue.delete()
                    )
                )
                .addOnSuccessListener {
                    android.util.Log.d(
                        "WelcomeReset",
                        "Campos welcome eliminados definitivamente para $userId (niveles: $totalLevels)"
                    )
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("WelcomeReset", "Error eliminando campos welcome", e)
                }
        }
    }
}