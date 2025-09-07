package com.heptacreation.sumamente.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.heptacreation.sumamente.ui.ScoreManager
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

object ReferralManager {

    private const val PREFS_NAME = "ReferralPrefs"
    private const val KEY_REFERRAL_CODE = "referral_code"
    private const val KEY_REFERRER_CODE = "referrer_code"
    private const val KEY_REFERRAL_VALIDATED = "referral_validated"
    private const val KEY_DEVICE_FINGERPRINT = "device_fingerprint"

    // Generar huella digital única del dispositivo (sin Advertising ID)
    @SuppressLint("HardwareIds")
    fun generateDeviceFingerprint(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val deviceModel = "${Build.MANUFACTURER}_${Build.MODEL}"
        val osVersion = Build.VERSION.RELEASE
        val installTime = context.packageManager
            .getPackageInfo(context.packageName, 0)
            .firstInstallTime

        val combined = "$androidId:$deviceModel:$osVersion:$installTime"

        return MessageDigest.getInstance("SHA-256")
            .digest(combined.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
            .take(32)
    }

    fun saveReferrerCode(context: Context, referrerCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_REFERRER_CODE, referrerCode)
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .update(mapOf(
                "referredBy" to referrerCode,
                "referralDate" to FieldValue.serverTimestamp()
            ))

        FirebaseFirestore.getInstance()
            .collection("referral_links")
            .document(userId)
            .set(
                mapOf(
                    "childUid" to userId,
                    "parentCode" to referrerCode,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
    }

    suspend fun checkAndValidateReferral(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (prefs.getBoolean(KEY_REFERRAL_VALIDATED, false)) {
            return false
        }

        val totalLevels = ScoreManager.getTotalUniqueLevelsCompletedAllGames()
        if (totalLevels < 12) {
            return false
        }

        val referrerCode = prefs.getString(KEY_REFERRER_CODE, null) ?: return false

        val fingerprint = generateDeviceFingerprint(context)

        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "referralCode" to referrerCode,
            "deviceFingerprint" to fingerprint
        )

        return try {
            val result = functions
                .getHttpsCallable("validateReferral")
                .call(data)
                .await()

            val response = result.data as? Map<*, *>
            val success = response?.get("success") as? Boolean ?: false

            if (success) {
                prefs.edit {
                    putBoolean(KEY_REFERRAL_VALIDATED, true)
                    putString(KEY_DEVICE_FINGERPRINT, fingerprint)
                }
            }

            success
        } catch (e: Exception) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit {
                putString("last_referral_error", e.message ?: "unknown_error")
            }
            false
        }

    }

    suspend fun getOrGenerateReferralCode(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val existingCode = prefs.getString(KEY_REFERRAL_CODE, null)
        if (!existingCode.isNullOrEmpty()) {
            return existingCode
        }

        val functions = FirebaseFunctions.getInstance()

        return try {
            val result = functions
                .getHttpsCallable("generateReferralCode")
                .call()
                .await()

            val response = result.data as? Map<*, *>
            val newCode = response?.get("referralCode") as? String

            if (!newCode.isNullOrEmpty()) {
                prefs.edit {
                    putString(KEY_REFERRAL_CODE, newCode)
                }
            }

            newCode
        } catch (_: Exception) {
            null
        }
    }

    fun createInvitationLink(referralCode: String): String {

        return "https://play.google.com/store/apps/details?id=com.heptacreation.sumamente&referrer=ref_$referralCode"
    }

    suspend fun getReferralsCount(): Int {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return 0

        return try {
            val doc = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .await()

            (doc.getLong("referidosValidados") ?: 0).toInt()
        } catch (_: Exception) {
            0
        }
    }
}
