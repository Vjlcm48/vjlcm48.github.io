package com.heptacreation.sumamente.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.heptacreation.sumamente.ui.ScoreManager
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

object ReferralManager {

    private const val PREFS_NAME = "MyPrefs"
    private const val KEY_REFERRAL_CODE = "referral_code"
    private const val KEY_REFERRER_CODE = "referrer_code"
    private const val KEY_REFERRAL_VALIDATED = "referral_validated"
    private const val KEY_DEVICE_FINGERPRINT = "device_fingerprint"
    private const val KEY_LAST_ERROR = "last_referral_error"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @SuppressLint("HardwareIds")
    fun generateDeviceFingerprint(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val deviceModel = "${Build.MANUFACTURER}_${Build.MODEL}"
        val osVersion = Build.VERSION.RELEASE
        val installTime = context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
        val combined = "$androidId:$deviceModel:$osVersion:$installTime"

        val digest = MessageDigest.getInstance("SHA-256").digest(combined.toByteArray())
        val sb = StringBuilder(digest.size * 2)
        for (b in digest) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString().take(32)
    }

    fun saveReferrerCode(context: Context, referrerCode: String) {
        val p = prefs(context)
        p.edit { putString(KEY_REFERRER_CODE, referrerCode) }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val userDoc = db.collection("usuarios").document(userId)
        val linkDoc = db.collection("referral_links").document(userId)

        val batch = db.batch()
        batch.update(
            userDoc,
            mapOf(
                "referredBy" to referrerCode,
                "referralDate" to FieldValue.serverTimestamp()
            )
        )
        batch.set(
            linkDoc,
            mapOf(
                "childUid" to userId,
                "parentCode" to referrerCode,
                "createdAt" to FieldValue.serverTimestamp()
            )
        )
        batch.commit()
    }

    suspend fun checkAndValidateReferral(context: Context): Boolean {
        val p = prefs(context)

        if (p.getBoolean(KEY_REFERRAL_VALIDATED, false)) return false

        val hasCompleted12Levels = ScoreManager.hasCompleted12LevelsInAnyGame()
        if (!hasCompleted12Levels) return false

        val referrerCode = p.getString(KEY_REFERRER_CODE, null)
        if (referrerCode.isNullOrBlank()) return false

        val fingerprint = generateDeviceFingerprint(context)

        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
                p.edit { putString(KEY_LAST_ERROR, "Usuario no autenticado") }
                return false
            }

            val tokenResult = currentUser.getIdToken(true).await()
            val idToken = tokenResult.token

            val functions = FirebaseFunctions.getInstance("us-central1")
            val data = hashMapOf(
                "idToken" to idToken,
                "referralCode" to referrerCode,
                "deviceFingerprint" to fingerprint
            )

            val result = functions
                .getHttpsCallable("validateReferral")
                .call(data)
                .await()

            val response = result.data as? Map<*, *>
            val success = response?.get("success") as? Boolean ?: false

            if (success) {
                p.edit {
                    putBoolean(KEY_REFERRAL_VALIDATED, true)
                    putString(KEY_DEVICE_FINGERPRINT, fingerprint)
                }

                try {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("usuarios").document(currentUser.uid)
                        .set(mapOf("referredBy" to referrerCode), SetOptions.merge())
                } catch (e: Exception) {
                    Log.w("ReferralManager", "No se pudo sincronizar referredBy: ${e.message}")
                }
            } else {
                val errMsg = (response?.get("message") as? String).orEmpty()
                if (errMsg.isNotBlank()) p.edit { putString(KEY_LAST_ERROR, errMsg) }
            }

            success
        } catch (e: Exception) {
            p.edit { putString(KEY_LAST_ERROR, e.message ?: "Error de validación") }
            Log.e("ReferralManager", "Error validando referido: ${e.message}")
            false
        }
    }

    suspend fun getOrGenerateReferralCode(context: Context): String? {
        val p = prefs(context)

        p.getString(KEY_REFERRAL_CODE, null)?.let { existing ->
            if (existing.isNotEmpty()) return existing
        }

        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
                ?: throw Exception("Usuario no autenticado")

            val tokenResult = currentUser.getIdToken(true).await()
            val idToken = tokenResult.token

            val functions = FirebaseFunctions.getInstance("us-central1")
            val data = hashMapOf(
                "idToken" to idToken,
                "deviceFingerprint" to generateDeviceFingerprint(context)
            )

            val result = functions
                .getHttpsCallable("generateReferralCode")
                .call(data)
                .await()

            val response = result.data as? Map<*, *>
            val newCode = response?.get("referralCode") as? String

            if (!newCode.isNullOrEmpty()) {
                p.edit { putString(KEY_REFERRAL_CODE, newCode) }
            }
            newCode
        } catch (e: Exception) {
            Log.e("ReferralManager", "Error generando código: ${e.message}")

            val tempCode = "SM${System.currentTimeMillis().toString().takeLast(6)}"
            p.edit { putString(KEY_REFERRAL_CODE, tempCode) }

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("usuarios").document(userId)
                    .set(mapOf("referralCode" to tempCode), SetOptions.merge())
                db.collection("referral_codes").document(tempCode)
                    .set(mapOf("uid" to userId, "timestamp" to FieldValue.serverTimestamp()))
            }

            tempCode
        }
    }

    fun createInvitationLink(referralCode: String): String {
        return "https://play.google.com/store/apps/details?id=com.heptacreation.sumamente&referrer=ref_$referralCode"
    }

    suspend fun getReferralsCount(): Int {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return 0
        val db = FirebaseFirestore.getInstance()

        return try {
            val doc = db.collection("usuarios").document(userId).get().await()
            (doc.getLong("referidosValidados") ?: 0).toInt()
        } catch (e: Exception) {
            Log.e("ReferralManager", "Error obteniendo conteo: ${e.message}")
            0
        }
    }

    suspend fun syncReferralDataIfNeeded(context: Context): Boolean {
        return try {
            val user = FirebaseAuth.getInstance().currentUser ?: return false
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()

            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            val referralCode = sharedPreferences.getString("referral_code", null)
            val referidosValidados = sharedPreferences.getInt("referidos_validados", 0)

            val data = hashMapOf<String, Any>(
                "referidos_validados" to referidosValidados
            )

            if (!referralCode.isNullOrEmpty()) {
                data["referral_code"] = referralCode
            }

            db.collection("usuarios")
                .document(uid)
                .set(data, SetOptions.merge())
                .await()

            true

        } catch (_: Exception) {
            false
        }
    }
}