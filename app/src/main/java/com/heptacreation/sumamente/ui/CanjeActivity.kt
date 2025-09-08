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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.DataSyncManager
import com.heptacreation.sumamente.ui.utils.ReferralManager
import kotlinx.coroutines.runBlocking

class CanjeActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvSaldoReferidos: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btn1Semana: AppCompatButton
    private lateinit var btn2Semanas: AppCompatButton
    private lateinit var btn4Semanas: AppCompatButton
    private lateinit var tvTitle: View

    // Estado
    private var referidosDisponibles: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canje)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        inicializarComponentes()
        configurarListeners()
        configurarBackPressedCallback()

        actualizarTextoSaldo()
        iniciarAnimacionesEntrada()
    }

    private fun inicializarComponentes() {
        tvSaldoReferidos = findViewById(R.id.tv_saldo_referidos)
        btnBack = findViewById(R.id.btn_back)
        btn1Semana = findViewById(R.id.btn_1_semana)
        btn2Semanas = findViewById(R.id.btn_2_semanas)
        btn4Semanas = findViewById(R.id.btn_4_semanas)
        tvTitle = findViewById(R.id.tv_title)

        referidosDisponibles = intent.getIntExtra("referidos_disponibles", 0)
    }

    private fun configurarListeners() {
        btnBack.setOnClickListener { finish() }

        btn1Semana.setOnClickListener {
            applyBounceEffect(it) { procesarCanje(4, getString(R.string.embajador_canje_4)) }
        }
        btn2Semanas.setOnClickListener {
            applyBounceEffect(it) { procesarCanje(7, getString(R.string.embajador_canje_7)) }
        }
        btn4Semanas.setOnClickListener {
            applyBounceEffect(it) { procesarCanje(12, getString(R.string.embajador_canje_12)) }
        }
    }

    private fun configurarBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finish()
        })
    }

    private fun actualizarTextoSaldo() {
        tvSaldoReferidos.text = getString(R.string.saldo_referidos, referidosDisponibles)
    }


    private fun actualizarSaldoReferidos() {

        val prev = sharedPreferences.getInt("referidos_validados", 0)

        val latest = runBlocking { ReferralManager.getReferralsCount() }

        if (latest != referidosDisponibles) {
            referidosDisponibles = latest
            sharedPreferences.edit { putInt("referidos_validados", latest) }
            actualizarTextoSaldo()
        } else {

            actualizarTextoSaldo()
        }

        val shared = sharedPreferences.getBoolean("has_shared_referral", false)
        if (prev == 0 && latest >= 1 && shared) {
            com.heptacreation.sumamente.ui.utils.MessagesStateManager
                .markActionCompleted(this, com.heptacreation.sumamente.ui.utils.MessagesStateManager.MSG_AMBASSADOR)

            sharedPreferences.edit { putBoolean("has_shared_referral", false) }
        }
    }


    private fun procesarCanje(referidosRequeridos: Int, tiempoPremium: String) {
        if (!sharedPreferences.getBoolean("canje_enabled", false)) {
            mostrarDialogoMensaje(getString(R.string.canje_no_disponible_temporalmente))
            return
        }

        if (referidosDisponibles < referidosRequeridos) {
            mostrarDialogoMensaje(getString(R.string.referidos_insuficientes))
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            mostrarDialogoMensaje(getString(R.string.error_canje))
            return
        }

        currentUser.getIdToken(true)
            .addOnSuccessListener { tokenResult ->
                val idToken = tokenResult.token
                val data = hashMapOf(
                    "idToken" to idToken,
                    "referidosRequeridos" to referidosRequeridos,
                    "tiempoPremium" to tiempoPremium
                )

                val functions = FirebaseFunctions.getInstance("us-central1")
                functions
                    .getHttpsCallable("redeemReferrals")
                    .call(data)
                    .addOnSuccessListener {
                        val resultado = it.data as? Map<*, *> ?: return@addOnSuccessListener
                        val nuevoSaldo = (resultado["nuevoSaldo"] as? Number)?.toInt()
                        val premiumHasta = (resultado["premiumHasta"] as? Number)?.toLong()
                        val mensaje = resultado["message"] as? String

                        if (nuevoSaldo != null) {
                            referidosDisponibles = nuevoSaldo
                            sharedPreferences.edit { putInt("referidos_validados", nuevoSaldo) }
                        }
                        if (premiumHasta != null) {
                            sharedPreferences.edit { putLong("premium_hasta", premiumHasta) }
                        }

                        if (!mensaje.isNullOrBlank()) {
                            mostrarDialogoMensaje(mensaje)
                        } else {
                            mostrarDialogoMensaje(getString(R.string.error_canje))
                        }

                        actualizarTextoSaldo()
                        DataSyncManager.updateCanjeStatus(true)
                    }
                    .addOnFailureListener { e ->
                        mostrarDialogoMensaje(e.message ?: getString(R.string.error_canje))
                    }
            }
            .addOnFailureListener { e ->
                mostrarDialogoMensaje(e.message ?: getString(R.string.error_canje))
            }
    }


    private fun mostrarDialogoMensaje(mensaje: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_canje_exitoso_custom, null)
        dialogView.findViewById<TextView>(R.id.tv_mensaje_canje).text = mensaje

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<AppCompatButton>(R.id.btn_entendido)
            .setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<ImageView>(R.id.btn_cerrar_dialog)
            .setOnClickListener { dialog.dismiss() }
        dialogView.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun iniciarAnimacionesEntrada() {
        val elementos = arrayOf(
            tvTitle,
            btn1Semana,
            btn2Semanas,
            btn4Semanas,
            tvSaldoReferidos
        )
        elementos.forEachIndexed { index, elemento ->
            elemento.alpha = 0f
            elemento.translationY = 50f
            elemento.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((120L * index))
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

        AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd()
                }
            })
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarSaldoReferidos()
    }
}
