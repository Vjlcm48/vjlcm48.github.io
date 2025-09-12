package com.heptacreation.sumamente.ui.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import androidx.core.content.edit

class PlayStoreReferrerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val referrer = intent.getStringExtra("referrer")
        if (referrer != null) {
            processReferrer(context, referrer)
        }
    }

    companion object {
        private const val TAG = "ReferrerReceiver"

        fun captureInstallReferrer(context: Context) {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()

            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            try {
                                val response: ReferrerDetails = referrerClient.installReferrer
                                val referrerUrl = response.installReferrer


                                processReferrer(context, referrerUrl)

                            } catch (e: Exception) {
                                Log.e(TAG, "Error obteniendo referrer", e)
                            }
                        }

                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            Log.w(TAG, "InstallReferrer API no soportada en este dispositivo")
                        }
                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            Log.w(TAG, "Servicio de InstallReferrer no disponible temporalmente")
                        }

                    }


                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {

                }
            })
        }

        private fun processReferrer(context: Context, referrerUrl: String?) {
            if (referrerUrl.isNullOrEmpty()) return

            val pattern = "ref_([A-Z0-9]+)".toRegex(RegexOption.IGNORE_CASE)

            val matchResult = pattern.find(referrerUrl)

            matchResult?.let {
                val referralCode = it.groupValues[1]
                if (referralCode.startsWith("SM")) {

                    ReferralManager.saveReferrerCode(context, referralCode)

                    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    prefs.edit { putString("last_captured_referrer", referralCode) }

                    Log.d(TAG, "Código de referido capturado: $referralCode")
                }
            }
        }
    }
}