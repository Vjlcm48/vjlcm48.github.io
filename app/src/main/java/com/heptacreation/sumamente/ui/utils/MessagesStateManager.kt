package com.heptacreation.sumamente.ui.utils

import android.content.Context
import androidx.core.content.edit
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.ScoreManager

object MessagesStateManager {

    const val MSG_RATE = "msg_rate"          // 12 niveles
    const val MSG_AMBASSADOR = "msg_amb"     // 24 niveles
    const val MSG_REFERRAL_VALIDATED = "msg_referral"

    private const val PREFS = "MyPrefs"

    private fun kUnread(id: String) = "${id}_unread"
    private fun kFirstShown(id: String) = "${id}_first_shown"
    private fun kLastRead(id: String) = "${id}_last_read"
    private fun kLastIgnored(id: String) = "${id}_last_ignored"
    private fun kLastAction(id: String) = "${id}_last_action"
    private fun kCooldownUntil(id: String) = "${id}_cooldown_until"

    private const val DAYS_7 = 7L * 24 * 60 * 60 * 1000
    private const val DAYS_20 = 20L * 24 * 60 * 60 * 1000
    private const val DAYS_3 = 3L * 24 * 60 * 60 * 1000

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

        checkReferralUpdates(context)
        cleanupExpiredReferralMessages(context)
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

    private fun checkReferralUpdates(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val storedCount = prefs.getInt("last_referral_count", 0)

        val currentCount = try {
            kotlinx.coroutines.runBlocking {
                ReferralManager.getReferralsCount()
            }
        } catch (_: Exception) {
            storedCount
        }

        if (currentCount > storedCount) {
            val increment = currentCount - storedCount
            repeat(increment) {
                activateReferralMessage(context)
            }
            prefs.edit { putInt("last_referral_count", currentCount) }
        }
    }

    private fun activateReferralMessage(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val messageId = "${MSG_REFERRAL_VALIDATED}_$now"

        prefs.edit {
            putLong("${messageId}_first_shown", now)
            putBoolean("${messageId}_unread", true)
            putLong("${messageId}_last_read", 0L)
        }
    }

    private fun cleanupExpiredReferralMessages(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()

        val allKeys = prefs.all.keys.toList()
        val referralKeys = allKeys.filter { it.startsWith("${MSG_REFERRAL_VALIDATED}_") && it.endsWith("_last_read") }

        prefs.edit {
            referralKeys.forEach { key ->
                val messageId = key.replace("_last_read", "")
                val lastRead = prefs.getLong(key, 0L)

                if (lastRead > 0L && now >= (lastRead + DAYS_3)) {

                    remove("${messageId}_first_shown")
                    remove("${messageId}_unread")
                    remove("${messageId}_last_read")
                }
            }
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

        addReferralMessagesIfActive(result, prefs, now)

        return result
    }

    private fun addReferralMessagesIfActive(result: MutableList<MessageItem>, prefs: android.content.SharedPreferences, now: Long) {
        val allKeys = prefs.all.keys
        val referralKeys = allKeys.filter { it.startsWith("${MSG_REFERRAL_VALIDATED}_") && it.endsWith("_first_shown") }

        referralKeys.forEach { key ->
            val messageId = key.replace("_first_shown", "")
            val firstShown = prefs.getLong(key, 0L)
            val lastRead = prefs.getLong("${messageId}_last_read", 0L)
            val unread = prefs.getBoolean("${messageId}_unread", false)

            val shouldShow = if (lastRead == 0L) {

                firstShown > 0L
            } else {

                now < (lastRead + DAYS_3)
            }

            if (shouldShow) {
                result.add(MessageItem(messageId, R.string.messages_item_unread, R.string.notificacion_nuevo_referido, unread))
            }
        }
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

        val allKeys = prefs.all.keys
        val referralKeys = allKeys.filter { it.startsWith("${MSG_REFERRAL_VALIDATED}_") && it.endsWith("_first_shown") }

        referralKeys.forEach { key ->
            val messageId = key.replace("_first_shown", "")
            val firstShown = prefs.getLong(key, 0L)
            val lastRead = prefs.getLong("${messageId}_last_read", 0L)
            val unread = prefs.getBoolean("${messageId}_unread", false)

            val isValid = if (lastRead == 0L) {
                firstShown > 0L
            } else {
                now < (lastRead + DAYS_3)
            }

            if (isValid && unread) {
                anyUnread = true
            }
        }

        val sevenDaysPassedFromLastIgnored = lastIgnoredMax > 0L && now >= (lastIgnoredMax + DAYS_7)
        val allOpened = !anyUnread
        return !(allOpened || (anyNeverOpenedIgnored && sevenDaysPassedFromLastIgnored))
    }

    fun markAsRead(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()

        if (id.startsWith(MSG_REFERRAL_VALIDATED)) {

            prefs.edit {
                putBoolean("${id}_unread", false)
                putLong("${id}_last_read", now)

            }
        } else {

            prefs.edit {
                putBoolean(kUnread(id), false)
                putLong(kLastRead(id), now)
                putLong(kCooldownUntil(id), now + DAYS_20)
            }
        }
    }

    fun markIgnoredNow(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()

        if (id.startsWith(MSG_REFERRAL_VALIDATED)) {

            prefs.edit {
                putBoolean("${id}_unread", false)
                putLong("${id}_last_read", now)
            }
        } else {

            prefs.edit {
                putLong(kLastIgnored(id), now)
                putLong(kCooldownUntil(id), now + DAYS_20)
            }
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