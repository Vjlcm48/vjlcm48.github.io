package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import java.util.Calendar
import java.util.Locale

class MisCondecoracionesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateTextView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnClose: ImageView
    private lateinit var adapter: CondecoracionesAdapter
    private val condecoraciones = mutableListOf<Condecoracion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_condecoraciones)

        CondecoracionTracker.init(this)

        CondecoracionTracker.marcarPinesComoVistos()
        CondecoracionTracker.marcarCoronasComoVistas()
        CondecoracionTracker.marcarCondecoracionesTop10ComoVistas()

        initViews()
        setupButtons()
        loadCondecoraciones()
        setupRecyclerView()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_view_condecoraciones)
        emptyStateTextView = findViewById(R.id.tv_empty_state)
        btnBack = findViewById(R.id.btn_back)
        btnClose = findViewById(R.id.btn_close)
    }

    private fun setupButtons() {
        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, MainGameActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                finish()
            }
        }
    }


    private fun loadCondecoraciones() {
        condecoraciones.clear()

        val pines = CondecoracionTracker.getAllPines()
        pines.forEach { pin ->
            val (nombre, descripcion, imagen) = when (pin.tipo) {
                "VICTORIS" -> Triple(
                    "VICTORIS",
                    "Completaste 30 niveles perfectos en 24 horas",
                    R.drawable.ic_pin_victoris
                )
                "OPTIMUM" -> Triple(
                    "OPTIMUM",
                    "Completaste 40 niveles perfectos en 24 horas",
                    R.drawable.ic_pin_optimum
                )
                "INVICTUS" -> Triple(
                    "INVICTUS",
                    "Completaste 50 niveles perfectos en 24 horas",
                    R.drawable.ic_pin_invictus
                )
                else -> Triple(
                    "PIN",
                    "Pin de reconocimiento obtenido",
                    R.drawable.ic_pin_victoris
                )
            }

            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.PIN,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    esNuevo = !pin.visto,
                    fechaObtencion = formatearFecha(pin.fechaObtencion)
                )
            )
        }

        val coronas = CondecoracionTracker.getCoronasActivas()
        coronas.forEach { corona ->
            val (nombre, descripcion, imagen) = when (corona.tipoCorona) {
                "VOLUCER" -> Triple(
                    getString(R.string.corona_volucer),
                    corona.mensaje,
                    R.drawable.ic_corona_volucer_alas
                )
                "CELERIS" -> Triple(
                    getString(R.string.corona_celeris),
                    corona.mensaje,
                    R.drawable.ic_corona_celeris_alas
                )
                "VELOCITAS" -> Triple(
                    getString(R.string.corona_velocitas),
                    corona.mensaje,
                    R.drawable.ic_corona_velocitas_alas
                )
                else -> Triple(
                    "CORONA",
                    corona.mensaje,
                    R.drawable.ic_corona_velocitas_alas
                )
            }

            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.CORONA,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    esNuevo = corona.esNueva,
                    fechaObtencion = formatearFecha(corona.fechaAsignacion)
                )
            )
        }

        // Cargar condecoraciones Top 10
        val condecoracionesTop10 = CondecoracionTracker.getCondecoracionesTop10()
        condecoracionesTop10.forEach { condecoracion ->
            val (nombre, descripcion, imagen) = when (condecoracion.tipoCondecoracion) {
                "EXCELSITUR" -> Triple(
                    getString(R.string.condecoracion_excelsitur),
                    condecoracion.mensaje,
                    R.drawable.ic_corona_excelsitur
                )
                "SUMMUM" -> Triple(
                    getString(R.string.condecoracion_summum),
                    condecoracion.mensaje,
                    R.drawable.ic_corona_summum
                )
                "MAGNANIMOUS" -> Triple(
                    getString(R.string.condecoracion_magnanimous),
                    condecoracion.mensaje,
                    R.drawable.ic_antorcha_magnanimous
                )
                "VENERABILIS" -> Triple(
                    getString(R.string.condecoracion_venerabilis),
                    condecoracion.mensaje,
                    R.drawable.ic_antorcha_venerabilis
                )
                "GLORIOSUS" -> Triple(
                    getString(R.string.condecoracion_gloriosus),
                    condecoracion.mensaje,
                    R.drawable.ic_antorcha_gloriosus
                )
                "ILLUSTRIS" -> Triple(
                    getString(R.string.condecoracion_illustris),
                    condecoracion.mensaje,
                    R.drawable.ic_estrella_illustris
                )
                "PRAESTANS" -> Triple(
                    getString(R.string.condecoracion_praestans),
                    condecoracion.mensaje,
                    R.drawable.ic_estrella_praestans
                )
                "INSIGNIS" -> Triple(
                    getString(R.string.condecoracion_insignis),
                    condecoracion.mensaje,
                    R.drawable.ic_estrella_insignis
                )
                "VIRTUOSUS" -> Triple(
                    getString(R.string.condecoracion_virtuosus),
                    condecoracion.mensaje,
                    R.drawable.ic_estrella_virtuosus
                )
                "HONORABILIS" -> Triple(
                    getString(R.string.condecoracion_honorabilis),
                    condecoracion.mensaje,
                    R.drawable.ic_estrella_honorabilis
                )
                else -> Triple(
                    "TOP 10",
                    condecoracion.mensaje,
                    R.drawable.ic_estrella_honorabilis
                )
            }

            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.TOP10,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    esNuevo = condecoracion.esNueva,
                    fechaObtencion = formatearFecha(condecoracion.fechaAsignacion)
                )
            )
        }

        condecoraciones.add(
            Condecoracion(
                tipo = TipoCondecoracion.MEDALLA,
                nombre = "INITIUM",
                descripcion = getString(R.string.logro_medalla_initium),
                imagen = R.drawable.ic_medalla_initium_cintas
            )
        )

        condecoraciones.add(
            Condecoracion(
                tipo = TipoCondecoracion.TROFEO,
                nombre = "GRADUS",
                descripcion = getString(R.string.logro_trofeo_gradus),
                imagen = R.drawable.ic_trofeo_gradus
            )
        )


        if (condecoraciones.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }


    }

    private fun formatearFecha(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return String.format(
            Locale.getDefault(), "%02d/%02d/%d",
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.YEAR)
        )
    }

    private fun setupRecyclerView() {
        adapter = CondecoracionesAdapter(condecoraciones) { condecoracion ->
            mostrarImagenAmpliada(condecoracion)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }


    private fun mostrarImagenAmpliada(condecoracion: Condecoracion) {

        if (condecoracion.tipo == TipoCondecoracion.PIN && condecoracion.esNuevo) {

            val pines = CondecoracionTracker.getAllPines()
            val pinCorrespondiente = pines.find { pin ->
                pin.tipo == condecoracion.nombre && !pin.visto
            }

            pinCorrespondiente?.let { pin ->
                CondecoracionTracker.marcarPinIndividualComoVisto(pin.tipo, pin.fechaObtencion)


                val position = condecoraciones.indexOfFirst {
                    it.tipo == TipoCondecoracion.PIN && it.nombre == condecoracion.nombre
                }
                if (position != -1) {
                    condecoraciones[position] = condecoraciones[position].copy(esNuevo = false)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        if (condecoracion.tipo == TipoCondecoracion.CORONA && condecoracion.esNuevo) {
            val coronas = CondecoracionTracker.getCoronasActivas()
            val coronaCorrespondiente = coronas.find { corona ->
                corona.tipoCorona == condecoracion.nombre && corona.esNueva
            }

            coronaCorrespondiente?.let { corona ->
                CondecoracionTracker.marcarCoronaIndividualComoVista(
                    corona.juego,
                    corona.tipoCorona,
                    corona.fechaAsignacion
                )

                val position = condecoraciones.indexOfFirst {
                    it.tipo == TipoCondecoracion.CORONA && it.nombre == condecoracion.nombre
                }
                if (position != -1) {
                    condecoraciones[position] = condecoraciones[position].copy(esNuevo = false)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        if (condecoracion.tipo == TipoCondecoracion.TOP10 && condecoracion.esNuevo) {
            val condecoracionesTop10 = CondecoracionTracker.getCondecoracionesTop10()
            val condecoracionCorrespondiente = condecoracionesTop10.find { top10 ->
                top10.tipoCondecoracion == condecoracion.nombre && top10.esNueva
            }

            condecoracionCorrespondiente?.let { top10 ->
                CondecoracionTracker.marcarCondecoracionTop10IndividualComoVista(
                    top10.posicion,
                    top10.tipoCondecoracion,
                    top10.fechaAsignacion
                )

                val position = condecoraciones.indexOfFirst {
                    it.tipo == TipoCondecoracion.TOP10 && it.nombre == condecoracion.nombre
                }
                if (position != -1) {
                    condecoraciones[position] = condecoraciones[position].copy(esNuevo = false)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        val dialog = ImagenAmpliadaDialog(
            this,
            condecoracion.nombre,
            condecoracion.imagen
        )
        dialog.show()
    }


    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }

}