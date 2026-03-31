package com.heptacreation.sumamente.ui.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import androidx.core.content.edit

class HeavySyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return suspendCancellableCoroutine { continuation ->

            val prefs = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val anchorTime = prefs.getLong("heavy_sync_anchor_time", 0L)

            if (anchorTime == 0L) {
                continuation.resume(Result.success())
                return@suspendCancellableCoroutine
            }

            val currentTime = System.currentTimeMillis()
            val sevenDaysMillis = 7L * 24 * 60 * 60 * 1000

            val shouldRun = (currentTime - anchorTime) >= sevenDaysMillis

            if (!shouldRun) {
                continuation.resume(Result.success())
                return@suspendCancellableCoroutine
            }

            DataSyncManager.syncHeavyDataToCloud(applicationContext) { success, _ ->
                if (success) {
                    prefs.edit { putLong("heavy_sync_anchor_time", currentTime) }
                    continuation.resume(Result.success())
                } else {
                    continuation.resume(Result.retry())
                }
            }
        }
    }
}