package com.heptacreation.sumamente.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R

class MedallaPagerAdapter(
    private val context: Context,
    private val medallas: List<Medalla>
) : RecyclerView.Adapter<MedallaPagerAdapter.MedallaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedallaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_medalla, parent, false)
        return MedallaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedallaViewHolder, position: Int) {
        val medalla = medallas[position]
        holder.bind(medalla)
    }

    override fun getItemCount(): Int = medallas.size

    inner class MedallaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medallaImage: ImageView = itemView.findViewById(R.id.img_medalla)
        private val medallaDescription: TextView = itemView.findViewById(R.id.tv_medalla_description)

        fun bind(medalla: Medalla) {
            val resourceId = when(medalla.imageName) {
                "ic_medalla_initium_cintas" -> R.drawable.ic_medalla_initium_cintas
                "ic_medalla_fidelis_cintas" -> R.drawable.ic_medalla_fidelis_cintas
                "ic_medalla_virtus_cintas" -> R.drawable.ic_medalla_virtus_cintas
                "ic_medalla_audax_cintas" -> R.drawable.ic_medalla_audax_cintas
                "ic_medalla_fortis_cintas" -> R.drawable.ic_medalla_fortis_cintas
                "ic_medalla_tenax_cintas" -> R.drawable.ic_medalla_tenax_cintas
                "ic_medalla_intrepidus_cintas" -> R.drawable.ic_medalla_intrepidus_cintas
                "ic_medalla_sapiens_cintas" -> R.drawable.ic_medalla_sapiens_cintas
                "ic_medalla_exemplar_cintas" -> R.drawable.ic_medalla_exemplar_cintas
                "ic_medalla_gloriam_cintas" -> R.drawable.ic_medalla_gloriam_cintas
                "ic_medalla_magnus_cintas" -> R.drawable.ic_medalla_magnus_cintas
                "ic_medalla_immortalis_cintas" -> R.drawable.ic_medalla_immortalis_cintas
                else -> R.drawable.ic_medalla_initium_cintas
            }
            medallaImage.setImageResource(resourceId)
            medallaDescription.text = medalla.description
        }
    }
}
