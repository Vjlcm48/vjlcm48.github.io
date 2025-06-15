package com.example.sumamente.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.example.sumamente.R

class MedallAnimationDialog(
    context: Context,
    private val medallaTipo: String,
    private val medallasObtenidas: Int,
    private val medallasRestantes: Int,
    private val onAnimationComplete: () -> Unit
) : Dialog(context) {

    private lateinit var ivMedalla: ImageView
    private lateinit var ivCirculo: ImageView
    private lateinit var tvTitulo: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvContador: TextView
    private lateinit var tvMotivacion: TextView
    private lateinit var btnEntendido: View
    private lateinit var btnCerrar: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private var mediaPlayerTrompeta: MediaPlayer? = null
    private var mediaPlayerVoz: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_medal_animation)

        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        initViews()
        configurarEventos()
        iniciarAnimacion()
    }

    private fun initViews() {
        ivMedalla = findViewById(R.id.iv_medalla)
        ivCirculo = findViewById(R.id.iv_circulo)
        tvTitulo = findViewById(R.id.tv_titulo_medalla)
        tvDescripcion = findViewById(R.id.tv_descripcion_medalla)
        tvContador = findViewById(R.id.tv_contador_medallas)
        tvMotivacion = findViewById(R.id.tv_motivacion_medalla)
        btnEntendido = findViewById(R.id.btn_entendido)
        btnCerrar = findViewById(R.id.btn_cerrar)

        configurarMedalla()

        ivCirculo.alpha = 0f
        ivMedalla.alpha = 0f
        tvTitulo.alpha = 0f
        tvDescripcion.alpha = 0f
        tvContador.alpha = 0f
        tvMotivacion.alpha = 0f
        btnEntendido.alpha = 0f
        btnCerrar.alpha = 0f

        ivCirculo.scaleX = 0f
        ivCirculo.scaleY = 0f
        ivMedalla.scaleX = 0f
        ivMedalla.scaleY = 0f
    }

    private fun configurarMedalla() {
        val recursos = obtenerRecursosMedalla(medallaTipo)

        ivMedalla.setImageResource(recursos.imagenId)
        tvTitulo.text = context.getString(recursos.tituloId)
        tvDescripcion.text = context.getString(recursos.descripcionId)
        tvContador.text = context.getString(R.string.medalla_contador_progreso, medallasObtenidas, medallasRestantes)
        tvMotivacion.text = context.getString(recursos.motivacionId)
    }

    private fun configurarEventos() {
        btnEntendido.setOnClickListener { cerrarDialog() }
        btnCerrar.setOnClickListener { cerrarDialog() }

        setOnDismissListener {
            liberarMediaPlayers()
            onAnimationComplete()
        }
    }

    private fun iniciarAnimacion() {

        val animacionCirculo = AnimatorSet().apply {
            val scaleX = ObjectAnimator.ofFloat(ivCirculo, "scaleX", 0f, 1.1f, 1f)
            val scaleY = ObjectAnimator.ofFloat(ivCirculo, "scaleY", 0f, 1.1f, 1f)
            val alpha = ObjectAnimator.ofFloat(ivCirculo, "alpha", 0f, 1f)

            playTogether(scaleX, scaleY, alpha)
            duration = 500
        }

        animacionCirculo.start()

        handler.postDelayed({
            val animacionMedalla = AnimatorSet().apply {
                val scaleX = ObjectAnimator.ofFloat(ivMedalla, "scaleX", 0f, 1.2f, 1f)
                val scaleY = ObjectAnimator.ofFloat(ivMedalla, "scaleY", 0f, 1.2f, 1f)
                val alpha = ObjectAnimator.ofFloat(ivMedalla, "alpha", 0f, 1f)
                val rotation = ObjectAnimator.ofFloat(ivMedalla, "rotation", -15f, 15f, 0f)

                playTogether(scaleX, scaleY, alpha, rotation)
                duration = 700
            }

            animacionMedalla.start()

            handler.postDelayed({
                reproducirTrompetaAleatoria()
                mostrarTextos()
            }, 700)

        }, 500)
    }

    private fun mostrarTextos() {

        val animTitulo = ObjectAnimator.ofFloat(tvTitulo, "alpha", 0f, 1f).apply {
            duration = 300
        }
        animTitulo.start()

        handler.postDelayed({
            val animDesc = ObjectAnimator.ofFloat(tvDescripcion, "alpha", 0f, 1f).apply {
                duration = 300
            }
            animDesc.start()
        }, 100)

        handler.postDelayed({
            val animContador = ObjectAnimator.ofFloat(tvContador, "alpha", 0f, 1f).apply {
                duration = 300
            }
            animContador.start()
        }, 200)

        handler.postDelayed({
            val animMotiv = ObjectAnimator.ofFloat(tvMotivacion, "alpha", 0f, 1f).apply {
                duration = 300
            }
            animMotiv.start()
        }, 300)
    }

    private fun reproducirTrompetaAleatoria() {
        val trompetas = arrayOf(
            R.raw.trompeta2, R.raw.trompeta3, R.raw.trompeta4,
            R.raw.trompeta5, R.raw.trompeta6, R.raw.trompeta7, R.raw.trompeta8
        )

        val trompetaAleatoria = trompetas.random()

        try {
            mediaPlayerTrompeta = MediaPlayer.create(context, trompetaAleatoria)
            mediaPlayerTrompeta?.let { mp ->
                mp.setOnCompletionListener { player ->
                    try {
                        player.release()
                        if (mediaPlayerTrompeta == player) {
                            mediaPlayerTrompeta = null
                        }

                        reproducirVozAleatoria()
                    } catch (e: Exception) {
                        android.util.Log.e("MedallDialog", "Error al liberar trompeta", e)
                    }
                }

                mp.setOnErrorListener { player, _, _ ->
                    try {
                        player.release()
                        if (mediaPlayerTrompeta == player) {
                            mediaPlayerTrompeta = null
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MedallDialog", "Error en listener trompeta", e)
                    }
                    true
                }

                mp.start()
            }
        } catch (e: Exception) {
            android.util.Log.e("MedallDialog", "Error reproduciendo trompeta", e)

            handler.postDelayed({ reproducirVozAleatoria() }, 4000)
        }
    }

    private fun reproducirVozAleatoria() {
        val voces = arrayOf(R.raw.vozfelicitaciones, R.raw.vozfelicitaciones2)
        val vozAleatoria = voces.random()

        try {
            mediaPlayerVoz = MediaPlayer.create(context, vozAleatoria)
            mediaPlayerVoz?.let { mp ->
                mp.setOnCompletionListener { player ->
                    try {
                        player.release()
                        if (mediaPlayerVoz == player) {
                            mediaPlayerVoz = null
                        }

                        mostrarBotones()
                    } catch (e: Exception) {
                        android.util.Log.e("MedallDialog", "Error al liberar voz", e)
                    }
                }

                mp.setOnErrorListener { player, _, _ ->
                    try {
                        player.release()
                        if (mediaPlayerVoz == player) {
                            mediaPlayerVoz = null
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MedallDialog", "Error en listener voz", e)
                    }

                    mostrarBotones()
                    true
                }

                mp.start()
            }
        } catch (e: Exception) {
            android.util.Log.e("MedallDialog", "Error reproduciendo voz", e)

            mostrarBotones()
        }
    }

    private fun mostrarBotones() {
        val animBotones = AnimatorSet().apply {
            val animEntendido = ObjectAnimator.ofFloat(btnEntendido, "alpha", 0f, 1f)
            val animCerrar = ObjectAnimator.ofFloat(btnCerrar, "alpha", 0f, 1f)

            playTogether(animEntendido, animCerrar)
            duration = 300
        }
        animBotones.start()
    }

    private fun cerrarDialog() {
        liberarMediaPlayers()
        dismiss()
    }

    private fun liberarMediaPlayers() {
        try {
            mediaPlayerTrompeta?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.release()
                mediaPlayerTrompeta = null
            }

            mediaPlayerVoz?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.release()
                mediaPlayerVoz = null
            }
        } catch (e: Exception) {
            android.util.Log.e("MedallDialog", "Error liberando MediaPlayers", e)
        }
    }

    private fun obtenerRecursosMedalla(tipo: String): RecursosMedalla {
        return when (tipo) {
            "INITIUM" -> RecursosMedalla(
                R.drawable.ic_medalla_initium_cintas,
                R.string.medalla_titulo_initium,
                R.string.medalla_desc_initium,
                R.string.medalla_motiv_initium
            )
            "FIDELIS" -> RecursosMedalla(
                R.drawable.ic_medalla_fidelis_cintas,
                R.string.medalla_titulo_fidelis,
                R.string.medalla_desc_fidelis,
                R.string.medalla_motiv_fidelis
            )
            "VIRTUS" -> RecursosMedalla(
                R.drawable.ic_medalla_virtus_cintas,
                R.string.medalla_titulo_virtus,
                R.string.medalla_desc_virtus,
                R.string.medalla_motiv_virtus
            )
            "AUDAX" -> RecursosMedalla(
                R.drawable.ic_medalla_audax_cintas,
                R.string.medalla_titulo_audax,
                R.string.medalla_desc_audax,
                R.string.medalla_motiv_audax
            )
            "FORTIS" -> RecursosMedalla(
                R.drawable.ic_medalla_fortis_cintas,
                R.string.medalla_titulo_fortis,
                R.string.medalla_desc_fortis,
                R.string.medalla_motiv_fortis
            )
            "TENAX" -> RecursosMedalla(
                R.drawable.ic_medalla_tenax_cintas,
                R.string.medalla_titulo_tenax,
                R.string.medalla_desc_tenax,
                R.string.medalla_motiv_tenax
            )
            "INTREPIDUS" -> RecursosMedalla(
                R.drawable.ic_medalla_intrepidus_cintas,
                R.string.medalla_titulo_intrepidus,
                R.string.medalla_desc_intrepidus,
                R.string.medalla_motiv_intrepidus
            )
            "SAPIENS" -> RecursosMedalla(
                R.drawable.ic_medalla_sapiens_cintas,
                R.string.medalla_titulo_sapiens,
                R.string.medalla_desc_sapiens,
                R.string.medalla_motiv_sapiens
            )
            "EXEMPLAR" -> RecursosMedalla(
                R.drawable.ic_medalla_exemplar_cintas,
                R.string.medalla_titulo_exemplar,
                R.string.medalla_desc_exemplar,
                R.string.medalla_motiv_exemplar
            )
            "GLORIAM" -> RecursosMedalla(
                R.drawable.ic_medalla_gloriam_cintas,
                R.string.medalla_titulo_gloriam,
                R.string.medalla_desc_gloriam,
                R.string.medalla_motiv_gloriam
            )
            "MAGNUS" -> RecursosMedalla(
                R.drawable.ic_medalla_magnus_cintas,
                R.string.medalla_titulo_magnus,
                R.string.medalla_desc_magnus,
                R.string.medalla_motiv_magnus
            )
            "IMMORTALIS" -> RecursosMedalla(
                R.drawable.ic_medalla_immortalis_cintas,
                R.string.medalla_titulo_immortalis,
                R.string.medalla_desc_immortalis,
                R.string.medalla_motiv_immortalis
            )
            else -> RecursosMedalla(
                R.drawable.ic_medalla_initium_cintas,
                R.string.medalla_titulo_initium,
                R.string.medalla_desc_initium,
                R.string.medalla_motiv_initium
            )
        }
    }

    private data class RecursosMedalla(
        val imagenId: Int,
        val tituloId: Int,
        val descripcionId: Int,
        val motivacionId: Int
    )
}