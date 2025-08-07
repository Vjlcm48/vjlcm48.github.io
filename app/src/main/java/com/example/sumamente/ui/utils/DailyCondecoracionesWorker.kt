package com.example.sumamente.ui.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sumamente.ui.CondecoracionTracker
import com.example.sumamente.ui.ScoreManager

class DailyCondecoracionesWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            CondecoracionTracker.init(applicationContext)
            initializeAllScoreManagers(applicationContext)

            CondecoracionTracker.verificarYEntregarPines()

            val latch = java.util.concurrent.CountDownLatch(4)

            CondecoracionTracker.verificarYActualizarCoronasDeVelocidadAsync(applicationContext) { latch.countDown() }
            CondecoracionTracker.verificarYActualizarCondecoracionesTop10Async(applicationContext) { latch.countDown() }
            CondecoracionTracker.verificarYActualizarCondecoracionesIQ7Async(applicationContext) { latch.countDown() }
            CondecoracionTracker.verificarYActualizarCondecoracionesTop5IntegralAsync(applicationContext) { latch.countDown() }

            latch.await(45, java.util.concurrent.TimeUnit.SECONDS)

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("DailyCondecoracionesWorker", "Error en verificación diaria", e)
            Result.retry()
        }
    }

    private fun initializeAllScoreManagers(context: Context) {

        ScoreManager.ensurePreferencesInitialized(context)

        ScoreManager.initPrincipiante(context)
        ScoreManager.init(context)
        ScoreManager.initPro(context)

        ScoreManager.initDeciPlusPrincipiante(context)
        ScoreManager.initDeciPlus(context)
        ScoreManager.initDeciPlusPro(context)

        ScoreManager.initRomasPrincipiante(context)
        ScoreManager.initRomas(context)
        ScoreManager.initRomasPro(context)

        ScoreManager.initAlfaNumerosPrincipiante(context)
        ScoreManager.initAlfaNumeros(context)
        ScoreManager.initAlfaNumerosPro(context)

        ScoreManager.initSumaRestaPrincipiante(context)
        ScoreManager.initSumaResta(context)
        ScoreManager.initSumaRestaPro(context)

        ScoreManager.initMasPlusPrincipiante(context)
        ScoreManager.initMasPlus(context)
        ScoreManager.initMasPlusPro(context)

        ScoreManager.initGenioPlusPrincipiante(context)
        ScoreManager.initGenioPlus(context)
        ScoreManager.initGenioPlusPro(context)
    }
}
