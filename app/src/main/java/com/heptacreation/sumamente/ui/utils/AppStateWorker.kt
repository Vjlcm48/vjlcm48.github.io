package com.heptacreation.sumamente.ui.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AppStateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return suspendCancellableCoroutine { continuation ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                continuation.resume(Result.failure())
                return@suspendCancellableCoroutine
            }
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .set(mapOf("appState" to "cerrada", "lastActive" to com.google.firebase.firestore.FieldValue.serverTimestamp()), SetOptions.merge())
                .addOnSuccessListener { continuation.resume(Result.success()) }
                .addOnFailureListener { continuation.resume(Result.retry()) }
        }
    }
}
