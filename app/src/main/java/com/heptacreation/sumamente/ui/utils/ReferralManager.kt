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

        // Si ya se validó antes, no repetir
        if (p.getBoolean(KEY_REFERRAL_VALIDATED, false)) return false

        // Requisito mínimo de niveles (mantener lógica actual)
        val hasCompleted12Levels = ScoreManager.hasCompleted12LevelsInAnyGame()
        if (!hasCompleted12Levels) return false

        // 1) Obtener código del PADRE desde prefs, y si NO existe, inyectar hardcode para pruebas de "usuario hijo"
        val referrerCode: String = run {
            val saved = p.getString(KEY_REFERRER_CODE, null)
            if (!saved.isNullOrBlank()) {
                saved
            } else {
                val debugParent = "SM593686" // <-- CÓDIGO DEL USUARIO PADRE (HARDCODE PARA PRUEBA)
                p.edit { putString(KEY_REFERRER_CODE, debugParent) }
                android.util.Log.d("ReferralTest", "DEBUG hijo: KEY_REFERRER_CODE inyectado = $debugParent")
                debugParent
            }
        }

        // 2) Huella del dispositivo
        val fingerprint = generateDeviceFingerprint(context)

        return try {
            // 3) Usuario actual y token
            val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
                p.edit { putString(KEY_LAST_ERROR, "Usuario no autenticado") }
                return false
            }

            val tokenResult = currentUser.getIdToken(true).await()
            val idToken = tokenResult.token

            android.util.Log.d("ReferralTest", "idToken length = ${idToken?.length} (null? ${idToken==null})")
            android.util.Log.d("ReferralTest", "FirebaseApp projectId = ${com.google.firebase.FirebaseApp.getInstance().options.projectId}")
            android.util.Log.d("ReferralTest", "UID=${currentUser.uid} isAnon=${currentUser.isAnonymous}")
            android.util.Log.d("ReferralTest", "Validando con referrerCode=$referrerCode fingerprint=${fingerprint.take(8)}...")

            // 4) Llamar Cloud Function validateReferral
            val functions = FirebaseFunctions.getInstance("us-central1")
            val data = hashMapOf(
                "idToken" to idToken,
                "referralCode" to referrerCode,
                "deviceFingerprint" to fingerprint
            )

            android.util.Log.d("ReferralTest", "Payload validateReferral keys=${data.keys} hasToken=${data["idToken"]!=null} refCode=${data["referralCode"]}")

            val result = functions
                .getHttpsCallable("validateReferral")
                .call(data)
                .await()

            val response = result.data as? Map<*, *>
            val success = response?.get("success") as? Boolean ?: false

            if (success) {
                // 5) Marcar validación local y guardar fingerprint
                p.edit {
                    putBoolean(KEY_REFERRAL_VALIDATED, true)
                    putString(KEY_DEVICE_FINGERPRINT, fingerprint)
                }
                // 6) (Opcional) Reflejar "referredBy" en Firestore
                try {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("usuarios").document(currentUser.uid)
                        .set(mapOf("referredBy" to referrerCode), com.google.firebase.firestore.SetOptions.merge())
                    android.util.Log.d("ReferralTest", "referredBy sincronizado en Firestore: $referrerCode")
                } catch (e: Exception) {
                    android.util.Log.w("ReferralTest", "No se pudo sincronizar referredBy: ${e.message}")
                }
            } else {
                val errMsg = (response?.get("error") as? String).orEmpty()
                if (errMsg.isNotBlank()) p.edit { putString(KEY_LAST_ERROR, errMsg) }
            }

            success
        } catch (e: Exception) {
            p.edit { putString(KEY_LAST_ERROR, e.message ?: "unknown_error") }
            android.util.Log.e("ReferralTest", "ERROR validateReferral: ${e.message}")
            false
        }
    }



    suspend fun getOrGenerateReferralCode(context: Context): String? {
        android.util.Log.d("ReferralTest", "=== INICIO getOrGenerateReferralCode ===")
        val p = prefs(context)

        p.getString(KEY_REFERRAL_CODE, null)?.let { existing ->
            android.util.Log.d("ReferralTest", "Código existente encontrado: $existing")
            if (existing.isNotEmpty()) return existing
        }

        android.util.Log.d("ReferralTest", "No hay código existente, generando nuevo...")

        return try {
            android.util.Log.d("ReferralTest", "Llamando a Cloud Function generateReferralCode...")


            val currentUser = FirebaseAuth.getInstance().currentUser
            android.util.Log.d("ReferralTest", "Usuario antes de Cloud Function: ${currentUser?.uid}")
            android.util.Log.d("ReferralTest", "Usuario es anónimo: ${currentUser?.isAnonymous}")

            if (currentUser == null) {
                throw Exception("Usuario no autenticado")
            }

            android.util.Log.d("ReferralTest", "Refrescando token de autenticación...")
            val tokenResult = currentUser.getIdToken(true).await() // Fuerza refresh del token
            val idToken = tokenResult.token
            android.util.Log.d("ReferralTest", "idToken length = ${idToken?.length} (null? ${idToken==null})")
            android.util.Log.d("ReferralTest", "FirebaseApp projectId = ${com.google.firebase.FirebaseApp.getInstance().options.projectId}")
            android.util.Log.d("ReferralTest", "UID=${currentUser.uid} isAnon=${currentUser.isAnonymous}")

            android.util.Log.d("ReferralTest", "Token refrescado, llamando Cloud Function...")

            val functions = FirebaseFunctions.getInstance("us-central1")

            val data = hashMapOf(
                "idToken" to idToken,
                "deviceFingerprint" to generateDeviceFingerprint(context)
            )

            android.util.Log.d("ReferralTest", "Payload generateReferralCode keys=${data.keys} hasToken=${data["idToken"]!=null}")


            val result = functions
                .getHttpsCallable("generateReferralCode")
                .call(data)
                .await()

            android.util.Log.d("ReferralTest", "Cloud Function respondió exitosamente")
            val response = result.data as? Map<*, *>
            val newCode = response?.get("referralCode") as? String

            android.util.Log.d("ReferralTest", "Código recibido: $newCode")
            if (!newCode.isNullOrEmpty()) {
                p.edit { putString(KEY_REFERRAL_CODE, newCode) }
                android.util.Log.d("ReferralTest", "Código guardado localmente: $newCode")
                android.util.Log.d("ReferralTest", "Guardado en MyPrefs.referral_code = $newCode")

            }
            newCode
        } catch (e: Exception) {
            android.util.Log.e("ReferralTest", "ERROR Cloud Function: ${e.message}")

            val tempCode = "SM${System.currentTimeMillis().toString().takeLast(6)}"


            p.edit { putString(KEY_REFERRAL_CODE, tempCode) }
            android.util.Log.d("ReferralTest", "Guardado en MyPrefs.referral_code (fallback) = $tempCode")


            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("usuarios").document(userId)
                    .set(mapOf("referralCode" to tempCode), com.google.firebase.firestore.SetOptions.merge())
                db.collection("referral_codes").document(tempCode)
                    .set(mapOf("uid" to userId, "timestamp" to FieldValue.serverTimestamp()))
            }

            android.util.Log.d("ReferralTest", "Código temporal creado y sincronizado: $tempCode")
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
        } catch (_: Exception) {
            0
        }
    }
}
