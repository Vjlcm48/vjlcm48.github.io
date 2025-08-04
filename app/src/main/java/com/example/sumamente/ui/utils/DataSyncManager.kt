package com.example.sumamente.ui.utils

import android.content.Context
import androidx.core.content.edit
import com.example.sumamente.R
import com.example.sumamente.ui.CondecoracionTracker
import com.example.sumamente.ui.ScoreManager
import com.example.sumamente.ui.SettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object DataSyncManager {

    private val auth get() = FirebaseAuth.getInstance()

    private const val SCHEMA_VERSION_PROFILE = 1
    private const val SCHEMA_VERSION_SCORE = 2
    private const val SCHEMA_VERSION_CONDECO = 2


    fun syncDataToCloud(
        context: Context,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance() // AGREGAR ESTA LÍNEA
        val user = auth.currentUser
        if (user == null) {
            onResult(false, context.getString(R.string.user_not_authenticated))
            return
        }

        val profileBox = buildProfilePreferencesBox(context)
        val scoreJson = ScoreManager.exportAllDataAsJson(context)
        val condecoJson = CondecoracionTracker.exportAllDataAsJson(context)

        val data = hashMapOf(

            "profile_preferences" to profileBox,

            "score_data" to scoreJson,
            "score_schema_version" to SCHEMA_VERSION_SCORE,
            "score_updated_at" to FieldValue.serverTimestamp(),

            "condecoracion_data" to condecoJson,
            "condecoracion_schema_version" to SCHEMA_VERSION_CONDECO,
            "condecoracion_updated_at" to FieldValue.serverTimestamp(),

            "updated_at" to FieldValue.serverTimestamp()
        )

        firestore.collection("usuarios")
            .document(user.uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun syncDataFromCloud(
        context: Context,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance() // AGREGAR ESTA LÍNEA
        val user = auth.currentUser
        if (user == null) {
            onResult(false, context.getString(R.string.user_not_authenticated))
            return
        }

        firestore.collection("usuarios")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onResult(true, null)
                    return@addOnSuccessListener
                }
                // 1) Perfil
                (doc.get("profile_preferences") as? Map<*, *>)?.let {
                    applyProfilePreferencesBox(context, it)
                }
                // 2) Progreso
                (doc.getString("score_data"))?.let { json ->
                    ScoreManager.importAllDataFromJson(context, json)
                }
                // 3) Condecoraciones
                (doc.getString("condecoracion_data"))?.let { json ->
                    CondecoracionTracker.importAllDataFromJson(context, json)
                }

                onResult(true, null)
            }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    private fun buildProfilePreferencesBox(context: Context): Map<String, Any> {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val map = hashMapOf<String, Any>(
            "schema_version" to SCHEMA_VERSION_PROFILE,
            "updated_at" to System.currentTimeMillis(),
            "savedUserName" to (prefs.getString("savedUserName", "") ?: ""),
            "savedCountryCode" to (prefs.getString("savedCountryCode", "sumamente") ?: "sumamente"),
            "selected_language" to (prefs.getString("selected_language", "en") ?: "en"),

            SettingsActivity.SOUND_ENABLED to prefs.getBoolean(SettingsActivity.SOUND_ENABLED, true),
            SettingsActivity.NOTIFICATIONS_ENABLED to prefs.getBoolean(SettingsActivity.NOTIFICATIONS_ENABLED, true),
            SettingsActivity.ADS_ENABLED to prefs.getBoolean(SettingsActivity.ADS_ENABLED, true),
            SettingsActivity.ACCOUNT_LINKED to prefs.getBoolean(SettingsActivity.ACCOUNT_LINKED, false)
        )

        return map
    }

    private fun applyProfilePreferencesBox(context: Context, raw: Map<*, *>) {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val username = raw["savedUserName"] as? String
        val country = raw["savedCountryCode"] as? String
        val language = raw["selected_language"] as? String
        val sound = raw[SettingsActivity.SOUND_ENABLED] as? Boolean
        val notif = raw[SettingsActivity.NOTIFICATIONS_ENABLED] as? Boolean
        val ads = raw[SettingsActivity.ADS_ENABLED] as? Boolean
        val linked = raw[SettingsActivity.ACCOUNT_LINKED] as? Boolean ?: true

        prefs.edit {
            username?.let { putString("savedUserName", it) }
            country?.let { putString("savedCountryCode", it) }
            language?.let { putString("selected_language", it) }
            sound?.let { putBoolean(SettingsActivity.SOUND_ENABLED, it) }
            notif?.let { putBoolean(SettingsActivity.NOTIFICATIONS_ENABLED, it) }
            ads?.let { putBoolean(SettingsActivity.ADS_ENABLED, it) }

            putBoolean(SettingsActivity.ACCOUNT_LINKED, linked)
        }
    }
}
