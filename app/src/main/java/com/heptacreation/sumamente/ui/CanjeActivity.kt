package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import com.google.firebase.functions.FirebaseFunctions
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.DataSyncManager
import kotlinx.coroutines.runBlocking
import com.heptacreation.sumamente.ui.utils.ReferralManager


class CanjeActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvSaldoReferidos: TextView
    private var referidosDisponibles: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canje)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        inicializarComponentes()
        configurarListeners()
        actualizarSaldoReferidos()
        configurarBackPressedCallback()
        iniciarAnimacionesEntrada()
    }

    private fun inicializarComponentes() {
        tvSaldoReferidos = findViewById(R.id.tv_saldo_referidos)
        referidosDisponibles = intent.getIntExtra("referidos_disponibles", 0)
    }

    private fun configurarListeners() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<AppCompatButton>(R.id.btn_1_semana).setOnClickListener {
            applyBounceEffect(it) { procesarCanje(4, getString(R.string.embajador_canje_4)) }
        }

        findViewById<AppCompatButton>(R.id.btn_2_semanas).setOnClickListener {
            applyBounceEffect(it) { procesarCanje(7, getString(R.string.embajador_canje_7)) }
        }

        findViewById<AppCompatButton>(R.id.btn_4_semanas).setOnClickListener {
            applyBounceEffect(it) { procesarCanje(12, getString(R.string.embajador_canje_12)) }
        }
    }

    private fun configurarBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun actualizarSaldoReferidos() {

        val latest = runBlocking { ReferralManager.getReferralsCount() }
        sharedPreferences.edit { putInt("referidos_validados", latest) }

        referidosDisponibles = sharedPreferences.getInt("referidos_validados", 0)
        tvSaldoReferidos.text = getString(R.string.saldo_referidos, referidosDisponibles)
    }

    private fun procesarCanje(referidosRequeridos: Int, tiempoPremium: String) {
        if (!sharedPreferences.getBoolean("canje_enabled", false)) {
            mostrarDialogoGenerico(getString(R.string.canje_no_disponible_temporalmente))
            return
        }

        if (referidosDisponibles < referidosRequeridos) {
            mostrarDialogoGenerico(getString(R.string.referidos_insuficientes))
            return
        }

        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "referidosRequeridos" to referidosRequeridos,
            "tiempoPremium" to tiempoPremium
        )

        functions.getHttpsCallable("redeemReferrals").call(data)
            .addOnSuccessListener {
                val resultado = it.data as? Map<*, *> ?: return@addOnSuccessListener
                val nuevoSaldo = (resultado["nuevoSaldo"] as? Number)?.toInt()
                val premiumHasta = (resultado["premiumHasta"] as? Number)?.toLong()
                val mensaje = resultado["message"] as? String

                if (nuevoSaldo != null) {
                    sharedPreferences.edit {
                        putInt("referidos_validados", nuevoSaldo)
                    }
                }
                if (premiumHasta != null) {
                    sharedPreferences.edit {
                        putLong("premium_hasta", premiumHasta)
                    }
                }

                if (mensaje != null) {
                    mostrarMensajeCanje(mensaje)
                } else {
                    mostrarDialogoGenerico(getString(R.string.error_canje))
                }

                actualizarSaldoReferidos()
                DataSyncManager.updateCanjeStatus(true)
            }
            .addOnFailureListener { e ->
                mostrarDialogoGenerico(e.message ?: getString(R.string.error_canje))
            }
    }

    private fun mostrarMensajeCanje(mensaje: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_canje_exitoso_custom, null)
        val tvMensaje = dialogView.findViewById<TextView>(R.id.tv_mensaje_canje)
        val btnEntendido = dialogView.findViewById<AppCompatButton>(R.id.btn_entendido)
        val btnCerrar = dialogView.findViewById<ImageView>(R.id.btn_cerrar_dialog)

        tvMensaje.text = mensaje

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnEntendido.setOnClickListener { dialog.dismiss() }
        btnCerrar.setOnClickListener { dialog.dismiss() }
        dialogView.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun mostrarDialogoGenerico(mensaje: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_canje_exitoso_custom, null)
        val tvMensaje = dialogView.findViewById<TextView>(R.id.tv_mensaje_canje)
        val btnEntendido = dialogView.findViewById<AppCompatButton>(R.id.btn_entendido)
        val btnCerrar = dialogView.findViewById<ImageView>(R.id.btn_cerrar_dialog)

        tvMensaje.text = mensaje

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnEntendido.setOnClickListener { dialog.dismiss() }
        btnCerrar.setOnClickListener { dialog.dismiss() }
        dialogView.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun iniciarAnimacionesEntrada() {
        val elementos = arrayOf(
            findViewById<TextView>(R.id.tv_title),
            findViewById<AppCompatButton>(R.id.btn_1_semana),
            findViewById<AppCompatButton>(R.id.btn_2_semanas),
            findViewById<AppCompatButton>(R.id.btn_4_semanas),
            findViewById(R.id.tv_saldo_referidos)
        )

        elementos.forEachIndexed { index, elemento ->
            elemento.alpha = 0f
            elemento.translationY = 50f
            elemento.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((120 * index).toLong())
                .start()
        }
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply { playTogether(scaleDownX, scaleDownY) }
        val scaleUp = AnimatorSet().apply { playTogether(scaleUpX, scaleUpY) }

        val bounceAnimator = AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd()
                }
            })
        }
        bounceAnimator.start()
    }

    override fun onResume() {
        super.onResume()
        actualizarSaldoReferidos()
    }
}