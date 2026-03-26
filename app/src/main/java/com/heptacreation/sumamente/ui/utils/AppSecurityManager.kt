package com.heptacreation.sumamente.ui.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.heptacreation.sumamente.ui.FirebaseAuthManager
import com.heptacreation.sumamente.ui.SettingsActivity
import com.heptacreation.sumamente.ui.SplashScreenActivity

object AppSecurityManager {
    @Volatile private var lastVerificationTime = 0L

    private val scorePrefsNames = listOf(
        "ScorePrefs", "ScorePrefsPrincipiante", "ScorePrefsPro",
        "ScorePrefsDeciPlus", "ScorePrefsDeciPlusPrincipiante", "ScorePrefsDeciPlusPro",
        "ScorePrefsRomas", "ScorePrefsRomasPrincipiante", "ScorePrefsRomasPro",
        "ScorePrefsAlfaNumeros", "ScorePrefsAlfaNumerosPrincipiante", "ScorePrefsAlfaNumerosPro",
        "ScorePrefsSumaResta", "ScorePrefsSumaRestaPrincipiante", "ScorePrefsSumaRestaPro",
        "ScorePrefsMasPlus", "ScorePrefsMasPlusPrincipiante", "ScorePrefsMasPlusPro",
        "ScorePrefsGenioPlus", "ScorePrefsGenioPlusPrincipiante", "ScorePrefsGenioPlusPro",
        "ScorePrefsFocoPlus", "ScorePrefsFocoPlusPrincipiante", "ScorePrefsFocoPlusPro"
    )

    fun hasValidLocalRestoration(context: Context): Boolean {
        val mainPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedUserName = mainPrefs.getString("savedUserName", null)

        if (!savedUserName.isNullOrBlank()) {
            return true
        }

        return scorePrefsNames.any { prefName ->
            val prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            prefs.all.isNotEmpty()
        }
    }

    fun shouldContinueAsExistingUser(context: Context): Boolean {
        val authUser = FirebaseAuth.getInstance().currentUser
        val hasLocalRestoration = hasValidLocalRestoration(context)

        if (hasLocalRestoration) {
            return true
        }

        val mainPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedUserName = mainPrefs.getString("savedUserName", null)

        return authUser != null && !savedUserName.isNullOrBlank()
    }

    @android.annotation.SuppressLint("HardwareIds")
    fun verifyActiveDeviceIfNeeded(
        context: Context,
        onResult: (authorized: Boolean) -> Unit
    ) {

        val now = System.currentTimeMillis()
        if (now - lastVerificationTime < 10_000L) {
            onResult(true)
            return
        }
        lastVerificationTime = now

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user == null) {
            onResult(true)
            return
        }

        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLinked = prefs.getBoolean(SettingsActivity.ACCOUNT_LINKED, false)

        if (!isLinked) {
            onResult(true)
            return
        }

        val currentDeviceId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: ""

        if (currentDeviceId.isBlank()) {
            onResult(true)
            return
        }

        val timeoutHandler = android.os.Handler(android.os.Looper.getMainLooper())
        var resultDelivered = false
        timeoutHandler.postDelayed({
            if (!resultDelivered) {
                resultDelivered = true
                Log.w("AppSecurity", "Timeout verificando dispositivo — continuando normalmente")
                onResult(true)
            }
        }, 7000)

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val storedDeviceId = doc.getString("activeDeviceId").orEmpty()

                if (storedDeviceId.isBlank() || storedDeviceId == currentDeviceId) {
                    if (!resultDelivered) { resultDelivered = true; onResult(true) }
                } else {
                    Log.w("AppSecurity", "Dispositivo no autorizado detectado. Se expulsará la sesión local.")
                    if (!resultDelivered) { resultDelivered = true; forceLogoutAndWipe(context); onResult(false) }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AppSecurity", "Error validando dispositivo activo: ${e.message}")
                if (!resultDelivered) { resultDelivered = true; onResult(true) }
            }
    }

    fun forceLogoutAndWipe(context: Context) {
        FirebaseAuthManager.signOutCompletely(context)
        DataSyncManager.clearAllLocalData(context)

        val intent = Intent(context, SplashScreenActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }
}