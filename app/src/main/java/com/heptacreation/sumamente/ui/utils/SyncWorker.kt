package com.heptacreation.sumamente.ui.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.heptacreation.sumamente.ui.CondecoracionTracker
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return suspendCancellableCoroutine { continuation ->
            DataSyncManager.syncDataToCloud(
                applicationContext,
                validateReferralIfNeeded = true
            ) { success, _ ->
                if (success) {
                    CondecoracionTracker.verificarYOtorgarInsigniaRIPlusInmediato()
                    continuation.resume(Result.success())
                } else {
                    continuation.resume(Result.retry())
                }
            }
        }
    }
}