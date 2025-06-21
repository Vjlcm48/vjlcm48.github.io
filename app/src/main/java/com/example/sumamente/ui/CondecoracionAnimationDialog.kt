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

class CondecoracionAnimationDialog(
    context: Context,
    private val tipoCondecoracion: TipoCondecoracion = TipoCondecoracion.MEDALLA, // <--- NUEVO
    private val medallaTipo: String = "",
    private val medallasObtenidas: Int = 0,
    private val medallasRestantes: Int = 0,
    private val nombreTrofeo: String = "", // <--- NUEVO: para trofeos
    private val onAnimationComplete: () -> Unit
) : Dialog(context) {
    // ... resto igual


    private lateinit var ivMedalla: ImageView
    private lateinit var ivCirculo: ImageView
    private lateinit var tvTitulo: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvContador: TextView
    private lateinit var tvMotivacion: TextView
    private lateinit var btnEntendido: View
    private lateinit var btnCerrar: ImageView
    private lateinit var layoutDobleCelebracion: View
    private lateinit var tvTituloDoble: TextView
    private lateinit var tvDescripcionDoble: TextView
    private lateinit var layoutMedallaContainer: View


    private val handler = Handler(Looper.getMainLooper())
    private var mediaPlayerTrompeta: MediaPlayer? = null
    private var mediaPlayerVoz: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_condecoracion_animation)

        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        initViews()
        configurarEventos()

        when (tipoCondecoracion) {
            TipoCondecoracion.MEDALLA -> configurarMedalla()
            TipoCondecoracion.TROFEO -> configurarTrofeo()
            TipoCondecoracion.DOBLE_CELEBRACION -> configurarParaDobleCelebracion()
            else -> configurarMedalla()
        }

        if (tipoCondecoracion != TipoCondecoracion.DOBLE_CELEBRACION) {
            iniciarAnimacion()
        }

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

        layoutDobleCelebracion = findViewById(R.id.layout_doble_celebracion)
        tvTituloDoble        = findViewById(R.id.tv_titulo_doble)
        tvDescripcionDoble   = findViewById(R.id.tv_descripcion_doble)
        layoutMedallaContainer = findViewById(R.id.layout_medalla_container)

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

        layoutDobleCelebracion.visibility = View.GONE   // ► ocultar bloque especial

        ivMedalla.setImageResource(recursos.imagenId)
        tvTitulo.text = context.getString(recursos.tituloId)
        tvDescripcion.text = context.getString(recursos.descripcionId)
        tvContador.text = context.getString(R.string.medalla_contador_progreso, medallasObtenidas, medallasRestantes)
        tvMotivacion.text = context.getString(recursos.motivacionId)
    }

    private fun configurarTrofeo() {
        val recursos = obtenerRecursosTrofeo(nombreTrofeo)

        layoutDobleCelebracion.visibility = View.GONE   // ► ocultar bloque especial

        ivMedalla.setImageResource(recursos.imagenId)
        ivCirculo.setImageResource(R.drawable.circle_silver_trophy)

        tvTitulo.text = context.getString(recursos.tituloId)
        tvDescripcion.text = context.getString(recursos.descripcionId)
        val trofeosObtenidos = CondecoracionTracker.getTrofeosObtenidos().size
        val trofeosFaltantes = 21 - trofeosObtenidos
        tvContador.text = context.getString(R.string.trofeo_contador_progreso, trofeosObtenidos, trofeosFaltantes)
        tvMotivacion.text = generarMensajeMotivacionTrofeo()
    }

    private fun generarMensajeMotivacionTrofeo(): String {
        val trofeosObtenidos = CondecoracionTracker.getTrofeosObtenidos().size
        val trofeosFaltantes = 21 - trofeosObtenidos

        return when (trofeosObtenidos) {
            1 -> context.getString(R.string.trofeo_motiv_1)
            21 -> context.getString(R.string.trofeo_motiv_21)
            else -> context.getString(R.string.trofeo_contador_progreso, trofeosObtenidos, trofeosFaltantes)
        }
    }

    private fun configurarParaDobleCelebracion() {

        // ► mostrar solo el bloque especial
        layoutDobleCelebracion.visibility = View.VISIBLE

        layoutMedallaContainer.visibility = View.GONE   // oculta el bloque de 160 dp
        tvTitulo.visibility = View.GONE          // texto normal oculto
        tvDescripcion.visibility = View.GONE
        tvContador.visibility = View.GONE
        tvMotivacion.visibility = View.GONE
        btnEntendido.visibility = View.GONE

        // ------- textos de doble celebración -------
        tvTituloDoble.text = context.getString(R.string.doble_celebracion_titulo)
        tvTituloDoble.alpha = 0f

        tvDescripcionDoble.text = context.getString(R.string.doble_celebracion_mensaje)
        tvDescripcionDoble.alpha = 0f
        // -------------------------------------------

        btnCerrar.alpha = 1f
        btnCerrar.visibility = View.VISIBLE

        mediaPlayerTrompeta?.release()
        mediaPlayerTrompeta = MediaPlayer.create(context, R.raw.trompeta5)
        mediaPlayerTrompeta?.start()

        // Animaciones de aparición
        tvTituloDoble.animate().alpha(1f).setDuration(350).start()
        handler.postDelayed({
            tvDescripcionDoble.animate().alpha(1f).setDuration(350).start()
        }, 1000)

        // Desvanecer y cerrar
        handler.postDelayed({
            tvTituloDoble.animate().alpha(0f).setDuration(200).start()
            tvDescripcionDoble.animate().alpha(0f).setDuration(200).withEndAction {
                cerrarDialog()
            }.start()
        }, 2900)
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
        if (tipoCondecoracion == TipoCondecoracion.DOBLE_CELEBRACION) return

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

    private fun obtenerRecursosTrofeo(nombreTrofeo: String): RecursosTrofeo {
        return when (nombreTrofeo) {
            // NÚMEROS⁺
            "INITIA" -> RecursosTrofeo(
                R.drawable.ic_trofeo_initia,
                R.string.trofeo_titulo_initia,
                R.string.logro_trofeo_initia
                )
            "CONSTANTIA" -> RecursosTrofeo(
                R.drawable.ic_trofeo_constantia,
                R.string.trofeo_titulo_constantia,
                R.string.logro_trofeo_constantia
            )
            "CONFECTUS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_confectus,
                R.string.trofeo_titulo_confectus,
                R.string.logro_trofeo_confectus
            )
            // DECI⁺
            "VIA" -> RecursosTrofeo(
                R.drawable.ic_trofeo_via,
                R.string.trofeo_titulo_via,
                R.string.logro_trofeo_via
            )
            "ALTUS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_altus,
                R.string.trofeo_titulo_altus,
                R.string.logro_trofeo_altus
            )
            "PERSEVERANTIA" -> RecursosTrofeo(
                R.drawable.ic_trofeo_perseverantia,
                R.string.trofeo_titulo_perseverantia,
                R.string.logro_trofeo_perseverantia
            )
            // ROMAS
            "GRADUS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_gradus,
                R.string.trofeo_titulo_gradus,
                R.string.logro_trofeo_gradus
            )
            "FORTITUDO" -> RecursosTrofeo(
                R.drawable.ic_trofeo_fortitudo,
                R.string.trofeo_titulo_fortitudo,
                R.string.logro_trofeo_fortitudo
            )
            "METAM" -> RecursosTrofeo(
                R.drawable.ic_trofeo_metam,
                R.string.trofeo_titulo_metam,
                R.string.logro_trofeo_metam
            )
            // ALFANÚMEROS
            "FUNDAMENTUM" -> RecursosTrofeo(
                R.drawable.ic_trofeo_fundamentum,
                R.string.trofeo_titulo_fundamentum,
                R.string.logro_trofeo_fundamentum
            )
            "PRAEMIUM" -> RecursosTrofeo(
                R.drawable.ic_trofeo_praemium,
                R.string.trofeo_titulo_praemium,
                R.string.logro_trofeo_praemium
            )
            "GLORIFICUS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_glorificus,
                R.string.trofeo_titulo_glorificus,
                R.string.logro_trofeo_glorificus
            )
            // SUMARESTA
            "SCALA" -> RecursosTrofeo(
                R.drawable.ic_trofeo_scala,
                R.string.trofeo_titulo_scala,
                R.string.logro_trofeo_scala
            )
            "TENACITAS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_tenacitas,
                R.string.trofeo_titulo_tenacitas,
                R.string.logro_trofeo_tenacitas
            )
            "PERFECTUS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_perfectus,
                R.string.trofeo_titulo_perfectus,
                R.string.logro_trofeo_perfectus
            )
            // MÁS⁺
            "ORIGO" -> RecursosTrofeo(
                R.drawable.ic_trofeo_origo,
                R.string.trofeo_titulo_origo,
                R.string.logro_trofeo_origo
            )
            "PROFICIUM" -> RecursosTrofeo(
                R.drawable.ic_trofeo_proficium,
                R.string.trofeo_titulo_proficium,
                R.string.logro_trofeo_proficium
            )
            "EXEMPLARITAS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_exemplaritas,
                R.string.trofeo_titulo_exemplaritas,
                R.string.logro_trofeo_exemplaritas
            )
            // GENIO⁺
            "ASCENSUS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_ascensus,
                R.string.trofeo_titulo_ascensus,
                R.string.logro_trofeo_ascensus
            )
            "MAGNIFICUS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_magnificus,
                R.string.trofeo_titulo_magnificus,
                R.string.logro_trofeo_magnificus
            )
            "POTENS" -> RecursosTrofeo(
                R.drawable.ic_trofeo_potens,
                R.string.trofeo_titulo_potens,
                R.string.logro_trofeo_potens
            )
            // Fallback
            else -> RecursosTrofeo(
                R.drawable.ic_trofeo_initia,
                R.string.trofeo_titulo_initia,
                R.string.logro_trofeo_initia
            )
        }
    }

    private data class RecursosTrofeo(
        val imagenId: Int,
        val tituloId: Int,
        val descripcionId: Int
    )
}