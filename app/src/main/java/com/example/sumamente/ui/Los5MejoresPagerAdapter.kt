package com.example.sumamente.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class Los5MejoresPagerAdapter(
    private val context: Context,
    private val condecoraciones: List<Los5MejoresCondecoracion>
) : RecyclerView.Adapter<Los5MejoresPagerAdapter.Los5MejoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Los5MejoresViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_los5mejores, parent, false)
        return Los5MejoresViewHolder(view)
    }

    override fun onBindViewHolder(holder: Los5MejoresViewHolder, position: Int) {
        val condecoracion = condecoraciones[position]
        holder.bind(condecoracion)
    }

    override fun getItemCount(): Int = condecoraciones.size

    inner class Los5MejoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val condecoracionImage: ImageView = itemView.findViewById(R.id.img_condecoracion)
        private val condecoracionName: TextView = itemView.findViewById(R.id.tv_condecoracion_name)
        private val condecoracionMeaning: TextView = itemView.findViewById(R.id.tv_condecoracion_meaning)
        private val condecoracionPosition: TextView = itemView.findViewById(R.id.tv_condecoracion_position)

        fun bind(condecoracion: Los5MejoresCondecoracion) {
            val resourceId = when(condecoracion.imageName) {
                "ic_imperium_supremus_i" -> R.drawable.ic_imperium_supremus_i
                "ic_magnus_honor_ii" -> R.drawable.ic_magnus_honor_ii
                "ic_virtus_totalis_iii" -> R.drawable.ic_virtus_totalis_iii
                "ic_excellentia_singulari_iv" -> R.drawable.ic_excellentia_singulari_iv
                "ic_gloria_integralis_v" -> R.drawable.ic_gloria_integralis_v
                else -> R.drawable.ic_imperium_supremus_i
            }

            condecoracionImage.setImageResource(resourceId)
            condecoracionName.text = condecoracion.name
            condecoracionMeaning.text = condecoracion.meaning

            val positionStringId = when(condecoracion.position) {
                1 -> R.string.position_1
                2 -> R.string.position_2
                3 -> R.string.position_3
                4 -> R.string.position_4
                5 -> R.string.position_5
                else -> R.string.position_5
            }
            condecoracionPosition.text = context.getString(positionStringId)
        }
    }
}