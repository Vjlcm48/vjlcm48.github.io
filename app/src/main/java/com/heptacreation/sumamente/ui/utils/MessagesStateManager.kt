package com.heptacreation.sumamente.ui.utils

import android.content.Context
import androidx.core.content.edit
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.ScoreManager

object MessagesStateManager {

    // IDs de mensajes
    const val MSG_RATE = "msg_rate"          // 12 niveles
    const val MSG_AMBASSADOR = "msg_amb"     // 24 niveles

    private const val PREFS = "MyPrefs"

    private fun kUnread(id: String) = "${id}_unread"
    private fun kFirstShown(id: String) = "${id}_first_shown"
    private fun kLastRead(id: String) = "${id}_last_read"
    private fun kLastIgnored(id: String) = "${id}_last_ignored"
    private fun kLastAction(id: String) = "${id}_last_action"
    private fun kCooldownUntil(id: String) = "${id}_cooldown_until"

    private const val DAYS_7 = 7L * 24 * 60 * 60 * 1000
    private const val DAYS_20 = 20L * 24 * 60 * 60 * 1000

    data class MessageItem(
        val id: String,
        val titleRes: Int,
        val bodyRes: Int,
        val unread: Boolean
    )

    fun ensureActivationByThresholds(context: Context) {

        val total = ScoreManager.getTotalUniqueLevelsCompletedAllGames()
        if (total >= 2) maybeActivate(context, MSG_RATE)
        if (total >= 3) maybeActivate(context, MSG_AMBASSADOR)
    }

    private fun maybeActivate(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val hasAction = prefs.getLong(kLastAction(id), 0L) > 0L
        if (hasAction) return

        val now = System.currentTimeMillis()
        val cooldown = prefs.getLong(kCooldownUntil(id), 0L)
        if (cooldown > 0L && now < cooldown) return

        val firstShown = prefs.getLong(kFirstShown(id), 0L)
        val isExpired = firstShown > 0L && now >= (firstShown + DAYS_7)

        prefs.edit {

            if (!prefs.contains(kFirstShown(id)) || isExpired) {
                putLong(kFirstShown(id), now)
                putLong(kLastRead(id), 0L)
                putLong(kLastIgnored(id), 0L)
            }
            putBoolean(kUnread(id), true)

        }

    }

    fun getActiveMessages(context: Context): List<MessageItem> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val result = mutableListOf<MessageItem>()
        val now = System.currentTimeMillis()

        fun addIfActive(id: String, titleRes: Int, bodyRes: Int) {
            val hasAction = prefs.getLong(kLastAction(id), 0L) > 0L
            if (hasAction) return

            val firstShown = prefs.getLong(kFirstShown(id), 0L)
            if (firstShown == 0L) return

            val expiredBy7Days = now >= (firstShown + DAYS_7)
            if (expiredBy7Days) return


            val unread = prefs.getBoolean(kUnread(id), false)
            result.add(MessageItem(id, titleRes, bodyRes, unread))
        }

        addIfActive(MSG_RATE, R.string.rate_title, R.string.rate_body)
        addIfActive(MSG_AMBASSADOR, R.string.ambassador_title, R.string.ambassador_body)

        return result
    }

    fun hasGlobalRedDot(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val ids = listOf(MSG_RATE, MSG_AMBASSADOR)

        var anyUnread = false
        var anyNeverOpenedIgnored = false
        var lastIgnoredMax = 0L

        for (id in ids) {
            val hasAction = prefs.getLong(kLastAction(id), 0L) > 0L
            if (hasAction) continue

            val firstShown = prefs.getLong(kFirstShown(id), 0L)
            if (firstShown == 0L) continue

            if (now >= (firstShown + DAYS_7)) continue

            val cooldown = prefs.getLong(kCooldownUntil(id), 0L)
            if (cooldown > 0L && now < cooldown) continue

            val unread = prefs.getBoolean(kUnread(id), false)
            val lastRead = prefs.getLong(kLastRead(id), 0L)
            val lastIgnored = prefs.getLong(kLastIgnored(id), 0L)
            val neverOpened = lastRead == 0L

            if (unread) anyUnread = true

            if (neverOpened && lastIgnored > 0L) {
                anyNeverOpenedIgnored = true
                if (lastIgnored > lastIgnoredMax) lastIgnoredMax = lastIgnored
            }
        }

        val sevenDaysPassedFromLastIgnored = lastIgnoredMax > 0L && now >= (lastIgnoredMax + DAYS_7)
        val allOpened = !anyUnread
        return !(allOpened || (anyNeverOpenedIgnored && sevenDaysPassedFromLastIgnored))
    }

    fun markAsRead(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        prefs.edit {
            putBoolean(kUnread(id), false)
            putLong(kLastRead(id), now)

            putLong(kCooldownUntil(id), now + DAYS_20)
        }
    }

    fun markIgnoredNow(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        prefs.edit {
            putLong(kLastIgnored(id), now)

            putLong(kCooldownUntil(id), now + DAYS_20)
        }
    }

    fun markActionCompleted(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit {
            putLong(kLastAction(id), System.currentTimeMillis())
            putBoolean(kUnread(id), false)
        }
    }
}
