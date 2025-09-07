package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.AppCompatButton
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.ReferralManager
import kotlinx.coroutines.runBlocking
import com.heptacreation.sumamente.ui.utils.PlayStoreReferrerReceiver
import androidx.core.content.edit

class EmbajadorActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvReferidosContador: TextView
    private var referidosValidados: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embajador)

        val prefsReferral = getSharedPreferences("ReferralPrefs", MODE_PRIVATE)
        if (!prefsReferral.getBoolean("install_referrer_captured", false)) {
            PlayStoreReferrerReceiver.captureInstallReferrer(applicationContext)
            prefsReferral.edit { putBoolean("install_referrer_captured", true) }
        }

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        inicializarComponentes()
        configurarListeners()
        actualizarContadorReferidos()
        configurarBackPressedCallback()
        iniciarAnimacionesEntrada()
    }

    private fun inicializarComponentes() {
        tvReferidosContador = findViewById(R.id.tv_referidos_contador)

        referidosValidados = sharedPreferences.getInt("referidos_validados", 0)
    }

    private fun configurarListeners() {

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<AppCompatButton>(R.id.btn_reglas).setOnClickListener {
            applyBounceEffect(it) { mostrarDialogReglas() }
        }

        findViewById<AppCompatButton>(R.id.btn_compartir_codigo).setOnClickListener {
            applyBounceEffect(it) { compartirCodigoReferido() }
        }

        findViewById<AppCompatButton>(R.id.btn_canjear_premium).setOnClickListener {
            applyBounceEffect(it) { abrirPantallaCanje() }
        }
    }

    private fun configurarBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun actualizarContadorReferidos() {
        tvReferidosContador.text = getString(R.string.referidos_activos_validados, referidosValidados)
    }

    private fun mostrarDialogReglas() {
        val dialogFragment = ReglasEmbajadorDialogFragment()
        dialogFragment.show(supportFragmentManager, "ReglasDialog")
    }

    private fun compartirCodigoReferido() {
        val codigoReferido = runBlocking {
            ReferralManager.getOrGenerateReferralCode(this@EmbajadorActivity) ?: ""
        }

        val deepLink = ReferralManager.createInvitationLink(codigoReferido)

        val mensaje = getString(R.string.mensaje_compartir, codigoReferido) + "\n\n" + deepLink

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }

        startActivity(Intent.createChooser(intent, getString(R.string.compartir_codigo_referido)))
    }

    private fun abrirPantallaCanje() {
        val intent = Intent(this, CanjeActivity::class.java)
        intent.putExtra("referidos_disponibles", referidosValidados)
        startActivity(intent)
    }

    private fun iniciarAnimacionesEntrada() {
        val elementos = arrayOf(
            findViewById(R.id.tv_title),
            findViewById<TextView>(R.id.tv_referidos_contador),
            findViewById<AppCompatButton>(R.id.btn_reglas),
            findViewById<AppCompatButton>(R.id.btn_compartir_codigo),
            findViewById<AppCompatButton>(R.id.btn_canjear_premium)
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

        val count = runBlocking { ReferralManager.getReferralsCount() }
        sharedPreferences.edit { putInt("referidos_validados", count) }
        referidosValidados = count

        referidosValidados = sharedPreferences.getInt("referidos_validados", 0)
        actualizarContadorReferidos()
    }
}