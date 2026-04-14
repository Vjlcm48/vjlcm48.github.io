package com.heptacreation.sumamente.ui

import android.content.Context
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CoinManager {

    private const val PREFS_COINS = "MyPrefs"

    private const val KEY_BALANCE              = "coins_balance"
    private const val KEY_FIRST_PURCHASE_USED  = "coins_first_purchase_used"
    private const val KEY_STREAK               = "coins_streak_count"
    private const val KEY_LAST_BONUS_DATE      = "coins_last_bonus_date"

    const val MAX_COINS = 100

    // ── Packs ──────────────────────────────────────────────────────────────
    data class CoinPack(
        val id: String,
        val priceLabel: String,
        val baseCoins: Int,
        val bonusCoins: Int,
        val badge: String = ""
    )

    val PACKS = listOf(
        CoinPack("pack_1", "$0.99",  15, 10),
        CoinPack("pack_2", "$1.99",  35, 15, "⭐"),
        CoinPack("pack_3", "$2.99",  55, 15),
        CoinPack("pack_4", "$3.99",  80, 20, "🔥")
    )

    // ── Consultas ──────────────────────────────────────────────────────────
    fun getBalance(context: Context): Int =
        prefs(context).getInt(KEY_BALANCE, 0)

    fun isFirstPurchaseAvailable(context: Context): Boolean =
        !prefs(context).getBoolean(KEY_FIRST_PURCHASE_USED, false)

    fun isPremium(context: Context): Boolean =
        context.getSharedPreferences(PREFS_COINS, Context.MODE_PRIVATE)
            .getBoolean("isPremium", false)

    fun coinsAfterPurchase(context: Context, pack: CoinPack): Int {
        val bonus = if (isFirstPurchaseAvailable(context)) pack.bonusCoins else 0
        return getBalance(context) + pack.baseCoins + bonus
    }

    fun canPurchase(context: Context, pack: CoinPack): Boolean =
        coinsAfterPurchase(context, pack) <= MAX_COINS

    // ── Compra (modo prueba: adjudica sin cobrar) ──────────────────────────
    fun executePurchase(context: Context, pack: CoinPack): Int {
        val isFirst  = isFirstPurchaseAvailable(context)
        val bonus    = if (isFirst) pack.bonusCoins else 0
        val current  = getBalance(context)
        val newBal   = minOf(current + pack.baseCoins + bonus, MAX_COINS)
        prefs(context).edit {
            putInt(KEY_BALANCE, newBal)
            if (isFirst) putBoolean(KEY_FIRST_PURCHASE_USED, true)
        }
        return newBal
    }

    // ── Gasto ──────────────────────────────────────────────────────────────
    fun spendCoins(context: Context, amount: Int): Boolean {
        val balance = getBalance(context)
        if (balance < amount) return false
        prefs(context).edit { putInt(KEY_BALANCE, balance - amount) }
        return true
    }

    // ── Bono diario + racha ────────────────────────────────────────────────
    fun claimDailyBonus(context: Context): DailyBonusResult {
        val today  = dateStr(Date())
        val p      = prefs(context)
        val last   = p.getString(KEY_LAST_BONUS_DATE, "") ?: ""
        val streak = p.getInt(KEY_STREAK, 0)

        if (last == today) return DailyBonusResult.AlreadyClaimed

        val yesterday  = dateStr(Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")).apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }.time)

        val newStreak  = if (last == yesterday) streak + 1 else 1
        val premium    = isPremium(context)
        val rawBonus   = if (premium) 7 + ((newStreak - 1) % 8)
        else                  5 + ((newStreak - 1) % 8)

        val current    = getBalance(context)
        val space      = MAX_COINS - current

        if (space <= 0) {
            p.edit {
                putString(KEY_LAST_BONUS_DATE, today)
                putInt(KEY_STREAK, 1)
            }
            return DailyBonusResult.LimitReached
        }

        val actual       = minOf(rawBonus, space)
        val streakBroken = actual < rawBonus
        val finalStreak  = if (streakBroken) 1 else newStreak

        p.edit {
            putInt(KEY_BALANCE, current + actual)
            putString(KEY_LAST_BONUS_DATE, today)
            putInt(KEY_STREAK, finalStreak)
        }

        return DailyBonusResult.Success(actual, finalStreak, streakBroken)
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private fun dateStr(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
        }.format(date)

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_COINS, Context.MODE_PRIVATE)

    // ── Resultados ─────────────────────────────────────────────────────────
    sealed class DailyBonusResult {
        object AlreadyClaimed : DailyBonusResult()
        object LimitReached   : DailyBonusResult()
        data class Success(
            val coinsAdded:   Int,
            val streak:       Int,
            val streakBroken: Boolean
        ) : DailyBonusResult()
    }
}