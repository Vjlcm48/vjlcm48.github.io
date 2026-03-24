package com.heptacreation.sumamente.ui.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return suspendCancellableCoroutine { continuation ->
            DataSyncManager.syncDataToCloud(applicationContext) { success, _ ->
                if (success) continuation.resume(Result.success())
                else continuation.resume(Result.retry())
            }
        }
    }
}