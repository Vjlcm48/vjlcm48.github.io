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
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import java.util.Calendar
import java.util.Locale
import com.example.sumamente.ui.utils.MusicManager

class MisCondecoracionesActivity : BaseActivity()  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateTextView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnClose: ImageView
    private lateinit var adapter: CondecoracionesAdapter
    private val condecoraciones = mutableListOf<Condecoracion>()
    private var isFinishingByBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_condecoraciones)

        CondecoracionTracker.init(this)

        CondecoracionTracker.clearGlobalRedDotFlags()

        initViews()
        setupButtons()
        loadCondecoraciones()
        setupRecyclerView()

        // Inicio del cambio flecha de regresar del celular
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                TrofeosActivity.finishTrofeosActivity()

                val intent = Intent(this@MisCondecoracionesActivity, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)

                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
// Fin del código de flecha de regresar del celular
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

                TrofeosActivity.finishTrofeosActivity()

                val intent = Intent(this, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)

                finish()
            }
        }

        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
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

        val condecoracionesIQ7 = CondecoracionTracker.getCondecoracionesIQ7()
        condecoracionesIQ7.forEach { condecoracionIQ7 ->
            val (nombre, descripcion, imagen) = mapearCondecoracionIQ7(condecoracionIQ7.posicion)
            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.IQ7,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    esNuevo = condecoracionIQ7.esNueva,
                    fechaObtencion = formatearFecha(condecoracionIQ7.fechaAsignacion)
                )
            )
        }

        val condecoracionesTop5Integral = CondecoracionTracker.getCondecoracionesTop5Integral()
        condecoracionesTop5Integral.forEach { condecoracionTop5 ->
            val (nombre, descripcion, imagen) = mapearCondecoracionTop5Integral(condecoracionTop5.posicion)
            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.TOP5_INTEGRAL,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    esNuevo = condecoracionTop5.esNueva,
                    fechaObtencion = formatearFecha(condecoracionTop5.fechaAsignacion)
                )
            )
        }

        val insigniaRIPlus = CondecoracionTracker.getInsigniaRIPlus()
        insigniaRIPlus?.let {
            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.INSIGNIA_RI_PLUS,
                    nombre = getString(R.string.insignia_supremus_integralis),
                    descripcion = getString(R.string.desc_insignia_supremus_integralis),
                    imagen = R.drawable.ic_insignia_ri_plus,
                    esNuevo = !it.vista,
                    fechaObtencion = formatearFecha(it.fechaObtencion)
                )
            )
        }

        val medallas = CondecoracionTracker.getMedallasObtenidas()
        medallas.forEach { medalla ->
            val (nombre, descripcion, imagen) = mapearMedalla(medalla.tipo)

            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.MEDALLA,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    esNuevo = !medalla.vista,
                    fechaObtencion = formatearFecha(medalla.fechaObtencion)
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

        val trofeos = CondecoracionTracker.getTrofeosObtenidos()
        trofeos.forEach { trofeo ->
            val (nombre, descripcion, imagen) = mapearTrofeo(trofeo.nombreTrofeo)
            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.TROFEO,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    esNuevo = !trofeo.visto,
                    fechaObtencion = formatearFecha(trofeo.fechaObtencion)
                )
            )
        }

        val apex = CondecoracionTracker.getApexSupremus()
        apex?.let {
            condecoraciones.add(
                Condecoracion(
                    tipo = TipoCondecoracion.APEX,
                    nombre = getString(R.string.apex_titulo),
                    descripcion = getString(R.string.apex_descripcion),
                    imagen = R.drawable.ic_trofeo_apex_mobius,
                    esNuevo = !it.vista,
                    fechaObtencion = formatearFecha(it.fechaObtencion)
                )
            )
        }

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

    private fun mapearMedalla(tipoMedalla: String): Triple<String, String, Int> {
        return when (tipoMedalla) {
            "INITIUM" -> Triple(
                "INITIUM",
                getString(R.string.medalla_desc_initium),
                R.drawable.ic_medalla_initium_cintas
            )
            "FIDELIS" -> Triple(
                "FIDELIS",
                getString(R.string.medalla_desc_fidelis),
                R.drawable.ic_medalla_fidelis_cintas
            )
            "VIRTUS" -> Triple(
                "VIRTUS",
                getString(R.string.medalla_desc_virtus),
                R.drawable.ic_medalla_virtus_cintas
            )
            "AUDAX" -> Triple(
                "AUDAX",
                getString(R.string.medalla_desc_audax),
                R.drawable.ic_medalla_audax_cintas
            )
            "FORTIS" -> Triple(
                "FORTIS",
                getString(R.string.medalla_desc_fortis),
                R.drawable.ic_medalla_fortis_cintas
            )
            "TENAX" -> Triple(
                "TENAX",
                getString(R.string.medalla_desc_tenax),
                R.drawable.ic_medalla_tenax_cintas
            )
            "INTREPIDUS" -> Triple(
                "INTREPIDUS",
                getString(R.string.medalla_desc_intrepidus),
                R.drawable.ic_medalla_intrepidus_cintas
            )
            "SAPIENS" -> Triple(
                "SAPIENS",
                getString(R.string.medalla_desc_sapiens),
                R.drawable.ic_medalla_sapiens_cintas
            )
            "EXEMPLAR" -> Triple(
                "EXEMPLAR",
                getString(R.string.medalla_desc_exemplar),
                R.drawable.ic_medalla_exemplar_cintas
            )
            "GLORIAM" -> Triple(
                "GLORIAM",
                getString(R.string.medalla_desc_gloriam),
                R.drawable.ic_medalla_gloriam_cintas
            )
            "MAGNUS" -> Triple(
                "MAGNUS",
                getString(R.string.medalla_desc_magnus),
                R.drawable.ic_medalla_magnus_cintas
            )
            "IMMORTALIS" -> Triple(
                "IMMORTALIS",
                getString(R.string.medalla_desc_immortalis),
                R.drawable.ic_medalla_immortalis_cintas
            )
            else -> Triple(
                "MEDALLA",
                "Medalla de reconocimiento obtenida",
                R.drawable.ic_medalla_initium_cintas
            )
        }
    }

    private fun mapearTrofeo(nombreTrofeo: String): Triple<String, String, Int> {
        return when (nombreTrofeo) {
            // NUMEROS+
            "INITIA" -> Triple("INITIA", getString(R.string.logro_trofeo_initia), R.drawable.ic_trofeo_initia)
            "CONSTANTIA" -> Triple("CONSTANTIA", getString(R.string.logro_trofeo_constantia), R.drawable.ic_trofeo_constantia)
            "CONFECTUS" -> Triple("CONFECTUS", getString(R.string.logro_trofeo_confectus), R.drawable.ic_trofeo_confectus)
            // DECIPLUS
            "VIA" -> Triple("VIA", getString(R.string.logro_trofeo_via), R.drawable.ic_trofeo_via)
            "ALTUS" -> Triple("ALTUS", getString(R.string.logro_trofeo_altus), R.drawable.ic_trofeo_altus)
            "PERSEVERANTIA" -> Triple("PERSEVERANTIA", getString(R.string.logro_trofeo_perseverantia), R.drawable.ic_trofeo_perseverantia)
            // ROMAS
            "GRADUS" -> Triple("GRADUS", getString(R.string.logro_trofeo_gradus), R.drawable.ic_trofeo_gradus)
            "FORTITUDO" -> Triple("FORTITUDO", getString(R.string.logro_trofeo_fortitudo), R.drawable.ic_trofeo_fortitudo)
            "METAM" -> Triple("METAM", getString(R.string.logro_trofeo_metam), R.drawable.ic_trofeo_metam)
            // ALFANUMEROS
            "FUNDAMENTUM" -> Triple("FUNDAMENTUM", getString(R.string.logro_trofeo_fundamentum), R.drawable.ic_trofeo_fundamentum)
            "PRAEMIUM" -> Triple("PRAEMIUM", getString(R.string.logro_trofeo_praemium), R.drawable.ic_trofeo_praemium)
            "GLORIFICUS" -> Triple("GLORIFICUS", getString(R.string.logro_trofeo_glorificus), R.drawable.ic_trofeo_glorificus)
            // SUMARESTA
            "SCALA" -> Triple("SCALA", getString(R.string.logro_trofeo_scala), R.drawable.ic_trofeo_scala)
            "TENACITAS" -> Triple("TENACITAS", getString(R.string.logro_trofeo_tenacitas), R.drawable.ic_trofeo_tenacitas)
            "PERFECTUS" -> Triple("PERFECTUS", getString(R.string.logro_trofeo_perfectus), R.drawable.ic_trofeo_perfectus)
            // MASPLUS
            "ORIGO" -> Triple("ORIGO", getString(R.string.logro_trofeo_origo), R.drawable.ic_trofeo_origo)
            "PROFICIUM" -> Triple("PROFICIUM", getString(R.string.logro_trofeo_proficium), R.drawable.ic_trofeo_proficium)
            "EXEMPLARITAS" -> Triple("EXEMPLARITAS", getString(R.string.logro_trofeo_exemplaritas), R.drawable.ic_trofeo_exemplaritas)
            // GENIOPLUS
            "ASCENSUS" -> Triple("ASCENSUS", getString(R.string.logro_trofeo_ascensus), R.drawable.ic_trofeo_ascensus)
            "MAGNIFICUS" -> Triple("MAGNIFICUS", getString(R.string.logro_trofeo_magnificus), R.drawable.ic_trofeo_magnificus)
            "POTENS" -> Triple("POTENS", getString(R.string.logro_trofeo_potens), R.drawable.ic_trofeo_potens)
            // Fallback
            else -> Triple("TROFEO", "Trofeo de reconocimiento obtenido", R.drawable.ic_trofeo_initia)
        }
    }

    private fun mapearCondecoracionIQ7(posicion: Int): Triple<String, String, Int> {
        return when (posicion) {
            1 -> Triple(
                getString(R.string.condecoracion_sapiens_supremus),
                getString(R.string.logro_iq7_sapiens_supremus),
                R.drawable.ic_sapiens_supremus_1
            )
            2 -> Triple(
                getString(R.string.condecoracion_mentis_aurea),
                getString(R.string.logro_iq7_mentis_aurea),
                R.drawable.ic_mentis_aurea_2
            )
            3 -> Triple(
                getString(R.string.condecoracion_luminis_rex),
                getString(R.string.logro_iq7_luminis_rex),
                R.drawable.ic_luminis_rex_3
            )
            4 -> Triple(
                getString(R.string.condecoracion_doctrinae_princeps),
                getString(R.string.logro_iq7_doctrinae_princeps),
                R.drawable.ic_doctrinae_princeps_4
            )
            5 -> Triple(
                getString(R.string.condecoracion_consilium_magnus),
                getString(R.string.logro_iq7_consilium_magnus),
                R.drawable.ic_consilium_magnus_5
            )
            6 -> Triple(
                getString(R.string.condecoracion_intellectus_primus),
                getString(R.string.logro_iq7_intellectus_primus),
                R.drawable.ic_intellectus_primus_6
            )
            7 -> Triple(
                getString(R.string.condecoracion_discipulus_optimus),
                getString(R.string.logro_iq7_discipulus_optimus),
                R.drawable.ic_discipulus_optimus_7
            )
            else -> Triple(
                "",
                "",
                R.drawable.ic_discipulus_optimus_7
            )
        }
    }

    private fun mapearCondecoracionTop5Integral(posicion: Int): Triple<String, String, Int> {
        return when (posicion) {
            1 -> Triple(
                getString(R.string.condecoracion_imperium_supremus),
                getString(R.string.logro_imperium_supremus_1),
                R.drawable.ic_imperium_supremus_i
            )
            2 -> Triple(
                getString(R.string.condecoracion_magnus_honor),
                getString(R.string.logro_magnus_honor_1),
                R.drawable.ic_magnus_honor_ii
            )
            3 -> Triple(
                getString(R.string.condecoracion_virtus_totalis),
                getString(R.string.logro_virtus_totalis_1),
                R.drawable.ic_virtus_totalis_iii
            )
            4 -> Triple(
                getString(R.string.condecoracion_excellentia_singulari),
                getString(R.string.logro_excellentia_singulari_1),
                R.drawable.ic_excellentia_singulari_iv
            )
            5 -> Triple(
                getString(R.string.condecoracion_gloria_integralis),
                getString(R.string.logro_gloria_integralis_1),
                R.drawable.ic_gloria_integralis_v
            )
            else -> Triple(
                "",
                "",
                R.drawable.ic_gloria_integralis_v
            )
        }
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

        if (condecoracion.tipo == TipoCondecoracion.IQ7 && condecoracion.esNuevo) {
            val condecoracionesIQ7 = CondecoracionTracker.getCondecoracionesIQ7()
            val condecoracionCorrespondiente = condecoracionesIQ7.find { iq7 ->
                mapearCondecoracionIQ7(iq7.posicion).first == condecoracion.nombre && iq7.esNueva
            }

            condecoracionCorrespondiente?.let { iq7 ->
                CondecoracionTracker.marcarCondecoracionIQ7IndividualComoVista(
                    iq7.posicion,
                    iq7.tipoCondecoracion,
                    iq7.fechaAsignacion
                )

                val position = condecoraciones.indexOfFirst {
                    it.tipo == TipoCondecoracion.IQ7 && it.nombre == condecoracion.nombre
                }
                if (position != -1) {
                    condecoraciones[position] = condecoraciones[position].copy(esNuevo = false)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        if (condecoracion.tipo == TipoCondecoracion.TOP5_INTEGRAL && condecoracion.esNuevo) {
            val condecoracionesTop5Integral = CondecoracionTracker.getCondecoracionesTop5Integral()
            val condecoracionCorrespondiente = condecoracionesTop5Integral.find { top5 ->
                mapearCondecoracionTop5Integral(top5.posicion).first == condecoracion.nombre && top5.esNueva
            }

            condecoracionCorrespondiente?.let { top5 ->
                CondecoracionTracker.marcarCondecoracionTop5IntegralIndividualComoVista(
                    top5.posicion,
                    top5.tipoCondecoracion,
                    top5.fechaAsignacion
                )

                val position = condecoraciones.indexOfFirst {
                    it.tipo == TipoCondecoracion.TOP5_INTEGRAL && it.nombre == condecoracion.nombre
                }
                if (position != -1) {
                    condecoraciones[position] = condecoraciones[position].copy(esNuevo = false)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        if (condecoracion.tipo == TipoCondecoracion.APEX && condecoracion.esNuevo) {

            CondecoracionTracker.marcarApexComoVista()

            val position = condecoraciones.indexOfFirst {
                it.tipo == TipoCondecoracion.APEX
            }
            if (position != -1) {
                condecoraciones[position] =
                    condecoraciones[position].copy(esNuevo = false)
                adapter.notifyItemChanged(position)
            }
        }

        if (condecoracion.tipo == TipoCondecoracion.INSIGNIA_RI_PLUS && condecoracion.esNuevo) {
            val insigniaRIPlus = CondecoracionTracker.getInsigniaRIPlus()
            if (insigniaRIPlus != null && !insigniaRIPlus.vista) {
                CondecoracionTracker.marcarInsigniaRIPlusComoVista()

                val position = condecoraciones.indexOfFirst {
                    it.tipo == TipoCondecoracion.INSIGNIA_RI_PLUS
                }
                if (position != -1) {
                    condecoraciones[position] = condecoraciones[position].copy(esNuevo = false)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        if (condecoracion.tipo == TipoCondecoracion.MEDALLA && condecoracion.esNuevo) {
            val medallas = CondecoracionTracker.getMedallasObtenidas()
            val medallaCorrespondiente = medallas.find { medalla ->
                medalla.tipo == condecoracion.nombre && !medalla.vista
            }

            medallaCorrespondiente?.let { medalla ->
                CondecoracionTracker.marcarMedallaIndividualComoVista(medalla.tipo, medalla.fechaObtencion)

                val position = condecoraciones.indexOfFirst {
                    it.tipo == TipoCondecoracion.MEDALLA && it.nombre == condecoracion.nombre
                }
                if (position != -1) {
                    condecoraciones[position] = condecoraciones[position].copy(esNuevo = false)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        if (condecoracion.tipo == TipoCondecoracion.TROFEO && condecoracion.esNuevo) {
            val trofeos = CondecoracionTracker.getTrofeosObtenidos()
            val trofeoCorrespondiente = trofeos.find { trofeo ->
                trofeo.nombreTrofeo == condecoracion.nombre && !trofeo.visto
            }
            trofeoCorrespondiente?.let { trofeo ->
                // Marcar como visto
                CondecoracionTracker.marcarTrofeoIndividualComoVisto(trofeo.juego, trofeo.grado, trofeo.fechaObtencion)

                val position = condecoraciones.indexOfFirst {
                    it.tipo == TipoCondecoracion.TROFEO && it.nombre == condecoracion.nombre
                }
                if (position != -1) {
                    condecoraciones[position] = condecoraciones[position].copy(esNuevo = false)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        if (condecoracion.tipo == TipoCondecoracion.APEX && condecoracion.esNuevo) {
            val apexObj = CondecoracionTracker.getApexSupremus()
            if (apexObj != null && !apexObj.vista) {
                CondecoracionTracker.marcarApexComoVista()

                val pos = condecoraciones.indexOfFirst { it.tipo == TipoCondecoracion.APEX }
                if (pos != -1) {
                    condecoraciones[pos] = condecoraciones[pos].copy(esNuevo = false)
                    adapter.notifyItemChanged(pos)
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

    override fun onStart() {
        super.onStart()
        val trofeosSigueViva = TrofeosActivity.instanceRef?.get() != null
        val context = TrofeosActivity.instanceRef?.get()
        val sonidoActivo = context?.let {
            val prefs = it.getSharedPreferences("MyPrefs", MODE_PRIVATE)
            prefs.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        } ?: true

        if (trofeosSigueViva && sonidoActivo) {
            MusicManager.resume()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishingByBack) {
            MusicManager.pause()
        }
        isFinishingByBack = false
    }

}