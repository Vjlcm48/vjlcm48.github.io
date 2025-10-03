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
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.PlayStoreReferrerReceiver
import com.heptacreation.sumamente.ui.utils.ReferralManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class EmbajadorActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvReferidosContador: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnReglas: AppCompatButton
    private lateinit var btnCompartir: AppCompatButton
    private lateinit var btnCanjear: AppCompatButton
    private lateinit var tvTitle: View

    private var referidosValidados: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        setContentView(R.layout.activity_embajador)

        // Fuente de verdad local unificada
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        android.util.Log.d("Embajador", "onCreate → usando prefs unificadas: MyPrefs")

        asegurarInstallReferrerCapturado()

        inicializarComponentes()
        configurarListeners()
        actualizarContadorReferidos()
        configurarBackPressedCallback()
        iniciarAnimacionesEntrada()

        // Prefetch NO bloqueante del código de referido (se guarda en prefs si llega)
        lifecycleScope.launch {
            try {
                android.util.Log.d("Embajador", "Prefetch referral: start")
                withTimeout(8_000) {
                    ReferralManager.getOrGenerateReferralCode(this@EmbajadorActivity)
                }
                android.util.Log.d("Embajador", "Prefetch referral: done (si llegó, quedó cacheado en MyPrefs.referral_code)")
            } catch (e: Exception) {
                android.util.Log.e("Embajador", "Prefetch referral: error=${e.message}")
            }
        }
    }

    private fun asegurarInstallReferrerCapturado() {
        // Conservamos esta prefs auxiliar solo para el flag del Install Referrer
        val prefsReferral = getSharedPreferences("ReferralPrefs", MODE_PRIVATE)
        val captured = prefsReferral.getBoolean("install_referrer_captured", false)
        android.util.Log.d("Embajador", "InstallReferrer captured? $captured")
        if (!captured) {
            PlayStoreReferrerReceiver.captureInstallReferrer(applicationContext)
            prefsReferral.edit { putBoolean("install_referrer_captured", true) }
            android.util.Log.d("Embajador", "InstallReferrer marcado como capturado")
        }
    }

    private fun inicializarComponentes() {
        tvReferidosContador = findViewById(R.id.tv_referidos_contador)
        btnBack = findViewById(R.id.btn_back)
        btnReglas = findViewById(R.id.btn_reglas)
        btnCompartir = findViewById(R.id.btn_compartir_codigo)
        btnCanjear = findViewById(R.id.btn_canjear_premium)
        tvTitle = findViewById(R.id.tv_title)

        referidosValidados = sharedPreferences.getInt("referidos_validados", 0)
        android.util.Log.d("Embajador", "Inicializar: referidos_validados=$referidosValidados")
    }

    private fun configurarListeners() {
        btnBack.setOnClickListener { finish() }

        btnReglas.setOnClickListener {
            applyBounceEffect(it) { mostrarDialogReglas() }
        }

        btnCompartir.setOnClickListener {
            applyBounceEffect(it) { compartirCodigoReferido() }
        }

        btnCanjear.setOnClickListener {
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
        tvReferidosContador.text =
            getString(R.string.referidos_activos_validados, referidosValidados)
    }

    private fun mostrarDialogReglas() {
        ReglasEmbajadorDialogFragment().show(supportFragmentManager, "ReglasDialog")
    }

    private fun compartirCodigoReferido() {
        // ✅ Unificado: leemos del mismo origen local que usa ReferralManager
        val cached = sharedPreferences.getString("referral_code", null)
        android.util.Log.d("Embajador", "Cache MyPrefs.referral_code = ${cached ?: "null"}")

        fun shareNow(code: String) {
            val deepLink = ReferralManager.createInvitationLink(code)
            android.util.Log.d("Embajador", "DeepLink generado: $deepLink")

            val mensaje = getString(R.string.mensaje_compartir, code) + "\n\n" + deepLink
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, mensaje)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.compartir_codigo_referido)))
            // Marcamos que el usuario ya compartió (para lógica de cierre de mensaje Embajador)
            sharedPreferences.edit { putBoolean("has_shared_referral", true) }
            android.util.Log.d("Embajador", "shareNow → has_shared_referral=true")
        }

        if (!cached.isNullOrEmpty()) {
            android.util.Log.d("Embajador", "Compartir desde cache inmediata")
            shareNow(cached)
            return
        }

        // Sin cache: lo pedimos sin bloquear la UI (con timeout defensivo)
        lifecycleScope.launch {
            try {
                val code = withTimeout(8_000) {
                    ReferralManager.getOrGenerateReferralCode(this@EmbajadorActivity)
                }
                android.util.Log.d("Embajador", "Referral obtenido de red: ${code ?: "null"}")
                if (!code.isNullOrEmpty()) {
                    // Guardado lo hace ReferralManager; de igual modo lo leemos de vuelta
                    sharedPreferences.edit { putString("referral_code", code) } // refuerzo de cache unificada
                    shareNow(code)
                } else {
                    android.widget.Toast
                        .makeText(this@EmbajadorActivity, R.string.error_canje, android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                android.util.Log.e("Embajador", "Error obteniendo referral para compartir", e)
                android.widget.Toast
                    .makeText(this@EmbajadorActivity, R.string.error_canje, android.widget.Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun abrirPantallaCanje() {
        android.util.Log.d("Embajador", "Abrir Canje con referidos=$referidosValidados")
        startActivity(
            Intent(this, CanjeActivity::class.java).putExtra(
                "referidos_disponibles",
                referidosValidados
            )
        )
    }

    private fun iniciarAnimacionesEntrada() {
        val elementos = arrayOf(
            tvTitle,
            tvReferidosContador,
            btnReglas,
            btnCompartir,
            btnCanjear
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
        lifecycleScope.launch {
            // Valor previo (para detectar transición 0 → ≥1)
            val prev = sharedPreferences.getInt("referidos_validados", 0)
            android.util.Log.d("Embajador", "onResume prev=$prev → consultando referrals...")

            // Lectura fresca (timeout defensivo)
            val count = try {
                withTimeout(8_000) {
                    ReferralManager.getReferralsCount()
                }
            } catch (e: Exception) {
                android.util.Log.e("Embajador", "getReferralsCount error=${e.message}, mantengo prev=$prev")
                prev // si falla, mantenemos el previo para no romper UI
            }

            android.util.Log.d("Embajador", "onResume count=$count (actualizaUI? ${count != referidosValidados})")

            if (count != referidosValidados) {
                referidosValidados = count
                sharedPreferences.edit { putInt("referidos_validados", count) }
                actualizarContadorReferidos()
            }

            // Si ya compartió y apareció el primer referido validado → cerrar mensaje Embajador
            val shared = sharedPreferences.getBoolean("has_shared_referral", false)
            if (prev == 0 && count >= 1 && shared) {
                com.heptacreation.sumamente.ui.utils.MessagesStateManager
                    .markActionCompleted(
                        this@EmbajadorActivity,
                        com.heptacreation.sumamente.ui.utils.MessagesStateManager.MSG_AMBASSADOR
                    )
                sharedPreferences.edit { putBoolean("has_shared_referral", false) }
                android.util.Log.d("Embajador", "Primer referido validado tras compartir → cerrar MSG_AMBASSADOR")
            }
        }
    }
}
