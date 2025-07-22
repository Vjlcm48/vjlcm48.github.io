package com.example.sumamente.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R


class CondecoracionesAdapter(
    private val condecoraciones: List<Condecoracion>,
    private val onImageClick: (Condecoracion) -> Unit,

    ) : RecyclerView.Adapter<CondecoracionesAdapter.CondecoracionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CondecoracionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_condecoracion, parent, false)
        return CondecoracionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CondecoracionViewHolder, position: Int) {
        val condecoracion = condecoraciones[position]
        holder.bind(condecoracion)
    }

    override fun getItemCount(): Int = condecoraciones.size

    inner class CondecoracionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgCondecoracion: ImageView = itemView.findViewById(R.id.img_condecoracion)
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_condecoracion_nombre)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tv_condecoracion_descripcion)
        private val redDotSmall: View = itemView.findViewById(R.id.red_dot_small)
        private val tvFecha: TextView = itemView.findViewById(R.id.tv_condecoracion_fecha)
        private val redDotTitulo: View = itemView.findViewById(R.id.red_dot_titulo)

        fun bind(condecoracion: Condecoracion) {
            imgCondecoracion.setImageResource(condecoracion.imagen)
            tvNombre.text = condecoracion.nombre
            tvDescripcion.text = condecoracion.descripcion

            val deberiasMostrarPuntoRojo = when (condecoracion.tipo) {
                TipoCondecoracion.PIN -> {
                    val pines = CondecoracionTracker.getAllPines()
                    pines.any { it.tipo == condecoracion.nombre && !it.visto }
                }
                TipoCondecoracion.CORONA -> {
                    val coronas = CondecoracionTracker.getCoronasActivas()
                    coronas.any { corona ->
                        val nombreCorona = when (corona.tipoCorona) {
                            "VOLUCER" -> itemView.context.getString(R.string.corona_volucer)
                            "CELERIS" -> itemView.context.getString(R.string.corona_celeris)
                            "VELOCITAS" -> itemView.context.getString(R.string.corona_velocitas)
                            else -> corona.tipoCorona
                        }
                        nombreCorona == condecoracion.nombre && corona.esNueva
                    }
                }
                TipoCondecoracion.TOP10 -> {
                    val condecoracionesTop10 = CondecoracionTracker.getCondecoracionesTop10()
                    condecoracionesTop10.any { top10 ->
                        val nombreTop10 = when (top10.tipoCondecoracion) {
                            "EXCELSITUR" -> itemView.context.getString(R.string.condecoracion_excelsitur)
                            "SUMMUM" -> itemView.context.getString(R.string.condecoracion_summum)
                            "MAGNANIMOUS" -> itemView.context.getString(R.string.condecoracion_magnanimous)
                            "VENERABILIS" -> itemView.context.getString(R.string.condecoracion_venerabilis)
                            "GLORIOSUS" -> itemView.context.getString(R.string.condecoracion_gloriosus)
                            "ILLUSTRIS" -> itemView.context.getString(R.string.condecoracion_illustris)
                            "PRAESTANS" -> itemView.context.getString(R.string.condecoracion_praestans)
                            "INSIGNIS" -> itemView.context.getString(R.string.condecoracion_insignis)
                            "VIRTUOSUS" -> itemView.context.getString(R.string.condecoracion_virtuosus)
                            "HONORABILIS" -> itemView.context.getString(R.string.condecoracion_honorabilis)
                            else -> top10.tipoCondecoracion
                        }
                        nombreTop10 == condecoracion.nombre && top10.esNueva
                    }
                }

                TipoCondecoracion.IQ7 -> {
                    val condecoracionesIQ7 = CondecoracionTracker.getCondecoracionesIQ7()
                    condecoracionesIQ7.any { iq7 ->
                        val nombreIQ7 = when (iq7.tipoCondecoracion) {
                            "SAPIENS_SUPREMUS" -> itemView.context.getString(R.string.condecoracion_sapiens_supremus)
                            "MENTIS_AUREA" -> itemView.context.getString(R.string.condecoracion_mentis_aurea)
                            "LUMINIS_REX" -> itemView.context.getString(R.string.condecoracion_luminis_rex)
                            "DOCTRINAE_PRINCEPS" -> itemView.context.getString(R.string.condecoracion_doctrinae_princeps)
                            "CONSILIUM_MAGNUS" -> itemView.context.getString(R.string.condecoracion_consilium_magnus)
                            "INTELLECTUS_PRIMUS" -> itemView.context.getString(R.string.condecoracion_intellectus_primus)
                            "DISCIPULUS_OPTIMUS" -> itemView.context.getString(R.string.condecoracion_discipulus_optimus)
                            else -> iq7.tipoCondecoracion
                        }
                        nombreIQ7 == condecoracion.nombre && iq7.esNueva
                    }
                }

                TipoCondecoracion.TOP5_INTEGRAL -> {
                    val condecoracionesTop5Integral = CondecoracionTracker.getCondecoracionesTop5Integral()
                    condecoracionesTop5Integral.any { top5 ->
                        val nombreTop5 = when (top5.tipoCondecoracion) {
                            "IMPERIUM_SUPREMUS" -> itemView.context.getString(R.string.condecoracion_imperium_supremus)
                            "MAGNUS_HONOR" -> itemView.context.getString(R.string.condecoracion_magnus_honor)
                            "VIRTUS_TOTALIS" -> itemView.context.getString(R.string.condecoracion_virtus_totalis)
                            "EXCELLENTIA_SINGULARI" -> itemView.context.getString(R.string.condecoracion_excellentia_singulari)
                            "GLORIA_INTEGRALIS" -> itemView.context.getString(R.string.condecoracion_gloria_integralis)
                            else -> top5.tipoCondecoracion
                        }
                        nombreTop5 == condecoracion.nombre && top5.esNueva
                    }
                }

                TipoCondecoracion.MEDALLA -> {
                    val medallas = CondecoracionTracker.getMedallasObtenidas()
                    medallas.any { it.tipo == condecoracion.nombre && !it.vista }
                }
                TipoCondecoracion.TROFEO -> {
                    val trofeos = CondecoracionTracker.getTrofeosObtenidos()
                    trofeos.any { it.nombreTrofeo == condecoracion.nombre && !it.visto }
                }
                TipoCondecoracion.APEX -> {
                    val apex = CondecoracionTracker.getApexSupremus()
                    apex?.let { !it.vista } ?: false
                }
                TipoCondecoracion.INSIGNIA_RI_PLUS -> {
                    val insignia = CondecoracionTracker.getInsigniaRIPlus()
                    insignia?.let { !it.vista } ?: false
                }
                else -> false
            }

            if (deberiasMostrarPuntoRojo) {
                redDotSmall.visibility = View.VISIBLE
                redDotTitulo.visibility = View.VISIBLE
            } else {
                redDotSmall.visibility = View.GONE
                redDotTitulo.visibility = View.GONE
            }

            if ((condecoracion.tipo == TipoCondecoracion.PIN || condecoracion.tipo == TipoCondecoracion.CORONA || condecoracion.tipo == TipoCondecoracion.TOP10 || condecoracion.tipo == TipoCondecoracion.IQ7 || condecoracion.tipo == TipoCondecoracion.TOP5_INTEGRAL || condecoracion.tipo == TipoCondecoracion.MEDALLA|| condecoracion.tipo == TipoCondecoracion.TROFEO || condecoracion.tipo == TipoCondecoracion.APEX) && !condecoracion.fechaObtencion.isNullOrEmpty()) {
                tvFecha.text = itemView.context.getString(R.string.date_obtained, condecoracion.fechaObtencion)
                tvFecha.visibility = View.VISIBLE
            } else {
                tvFecha.visibility = View.GONE
            }

            imgCondecoracion.setOnClickListener {

                when (condecoracion.tipo) {
                    TipoCondecoracion.APEX -> {
                        val apex = CondecoracionTracker.getApexSupremus()
                        if (apex != null && !apex.vista) {
                            CondecoracionTracker.marcarApexComoVista()
                            val pos = adapterPosition
                            if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                        }
                    }

                    else -> {}
                }
                onImageClick(condecoracion)
            }
        }
    }
}
