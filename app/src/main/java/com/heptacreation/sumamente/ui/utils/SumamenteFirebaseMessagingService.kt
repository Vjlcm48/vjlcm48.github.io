package com.heptacreation.sumamente.ui.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class SumamenteFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "Nuevo token: $token")

        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { userId ->
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d("FCM Token", "Token actualizado para el usuario: $userId")
                }
                .addOnFailureListener { e ->
                    Log.e("FCM Token", "Error al actualizar token", e)
                }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            if (type == "check_status_request") {
                sendResponseToCloudFunction(remoteMessage.data["originUid"])
            }
        }
    }

    private fun sendResponseToCloudFunction(originUid: String?) {
        if (originUid.isNullOrEmpty()) {
            return
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("status_checks")
            .document(originUid)
            .collection("responses")
            .document(userId)
            .set(mapOf("timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()))
            .addOnSuccessListener {
                Log.d("Status Check", "Respuesta enviada para $originUid")
            }
            .addOnFailureListener { e ->
                Log.e("Status Check", "Error al enviar respuesta", e)
            }
    }
}