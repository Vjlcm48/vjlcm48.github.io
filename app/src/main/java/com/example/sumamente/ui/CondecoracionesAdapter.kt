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
    private val onImageClick: (Condecoracion) -> Unit
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

        fun bind(condecoracion: Condecoracion) {
            imgCondecoracion.setImageResource(condecoracion.imagen)
            tvNombre.text = condecoracion.nombre
            tvDescripcion.text = condecoracion.descripcion

            imgCondecoracion.setOnClickListener {
                onImageClick(condecoracion)
            }
        }
    }
}
