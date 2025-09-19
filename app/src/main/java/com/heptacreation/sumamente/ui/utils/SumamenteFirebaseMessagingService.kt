package com.heptacreation.sumamente.ui.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.MainGameActivity
import com.heptacreation.sumamente.ui.SettingsActivity
import java.util.Locale

class SumamenteFirebaseMessagingService : FirebaseMessagingService() {

    private val channelId = "sumamente_channel"
    private val channelName = "Notificaciones SumaMente"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "Nuevo token: $token")

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .addOnSuccessListener { Log.d("FCM Token", "Token guardado (merge) para $userId") }
            .addOnFailureListener { e -> Log.e("FCM Token", "Error al guardar token (merge)", e) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val updatedContext = updateContextWithLanguage(this, getSavedLanguage())

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val enabled = prefs.getBoolean(SettingsActivity.NOTIFICATIONS_ENABLED, true)
        if (!enabled) {
            Log.d("FCM", "Notificaciones desactivadas por el usuario.")
            return
        }

        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            if (type == "check_status_request") {
                sendResponseToCloudFunction(remoteMessage.data["originUid"])
                return
            }
        }

        val key = remoteMessage.data["key"] ?: return
        val title = updatedContext.resources.getString(R.string.app_name)

        val message = when (key) {
            "notif_inactive_21days" ->
                updatedContext.resources.getString(R.string.notif_inactive_21days)

            "notif_inactive_7days" ->
                updatedContext.resources.getString(R.string.notif_inactive_7days)

            "notif_first_days_welcome" ->
                updatedContext.resources.getString(R.string.notif_first_days_welcome)

            "notif_mind_miss" -> {
                Log.d("FCM_DEBUG", "Raw data: ${remoteMessage.data}")
                val levelNum = remoteMessage.data["level"]?.toIntOrNull() ?: 1
                val game = remoteMessage.data["game"] ?: ""
                val difficulty = remoteMessage.data["difficulty"] ?: ""

                val difficultyLabel = mapDifficultyToLabel(updatedContext, difficulty)

                Log.d("FCM_DEBUG", "Parsed: level=$levelNum, game='$game', difficulty='$difficulty'")

                val finalMessage = getRandomMindMissMessage(updatedContext, levelNum, game, difficultyLabel)

                Log.d("FCM_DEBUG", "Final message: '$finalMessage'")
                finalMessage
            }

            "notif_level_unlocked" -> {
                val levelNum = remoteMessage.data["level"]?.toIntOrNull() ?: 1
                val game = remoteMessage.data["game"] ?: ""
                val difficulty = remoteMessage.data["difficulty"] ?: ""
                updatedContext.resources.getString(
                    R.string.notif_level_unlocked,
                    levelNum,
                    game,
                    difficulty
                )
            }

            "notif_ranking_position" -> {
                val posNum = remoteMessage.data["position"]?.toIntOrNull() ?: 0
                updatedContext.resources.getString(R.string.notif_ranking_position, posNum)
            }

            else -> return
        }

        showNotification(title, message)
    }

    private fun mapDifficultyToLabel(context: Context, code: String): String {
        return when (code.uppercase(Locale.getDefault())) {
            "PRINCIPIANTE" -> context.resources.getString(R.string.difficulty_principiante)
            "AVANZADO"     -> context.resources.getString(R.string.difficulty_avanzado)
            "PRO"          -> context.resources.getString(R.string.difficulty_pro)
            else           -> code
        }
    }

    private fun getRandomMindMissMessage(context: Context, levelNum: Int, game: String, difficultyLabel: String): String {
        val messages = arrayOf(
            R.string.notif_mind_miss,
            R.string.notif_mind_miss_alt_1,
            R.string.notif_mind_miss_alt_2,
            R.string.notif_mind_miss_alt_3,
            R.string.notif_mind_miss_alt_4,
            R.string.notif_mind_miss_alt_5,
            R.string.notif_mind_miss_alt_6,
            R.string.notif_mind_miss_alt_7
        )

        val randomIndex = kotlin.random.Random.nextInt(messages.size)
        return context.resources.getString(messages[randomIndex], levelNum, game, difficultyLabel)
    }


    private fun sendResponseToCloudFunction(originUid: String?) {
        if (originUid.isNullOrEmpty()) return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("status_checks")
            .document(originUid)
            .collection("responses")
            .document(userId)
            .set(mapOf("timestamp" to FieldValue.serverTimestamp()))
            .addOnSuccessListener { Log.d("Status Check", "Respuesta enviada para $originUid") }
            .addOnFailureListener { e -> Log.e("Status Check", "Error al enviar respuesta", e) }
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainGameActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_sumamente)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(sound)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        try {
            val large = BitmapFactory.decodeResource(resources, R.drawable.ic_notification_large)
            if (large != null) builder.setLargeIcon(large)
        } catch (_: Exception) { /* opcional */ }

        nm.notify(System.currentTimeMillis().toInt(), builder.build())
    }


    private fun getSavedLanguage(context: Context = this): String {
        val prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return prefs.getString("selected_language", null) ?: "en"
    }

    private fun updateContextWithLanguage(context: Context, languageCode: String): Context {
        val locale = Locale.Builder().setLanguage(languageCode).build()
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}
