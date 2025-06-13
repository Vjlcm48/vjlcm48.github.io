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
            CondecoracionTracker.verificarYActualizarCoronasDeVelocidad(applicationContext)
            CondecoracionTracker.verificarYActualizarCondecoracionesTop10(applicationContext)

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
