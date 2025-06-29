package com.example.sumamente.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class Los7MejoresPagerAdapter(
    private val context: Context,
    private val condecoraciones: List<Los7MejoresCondecoracion>
) : RecyclerView.Adapter<Los7MejoresPagerAdapter.Los7MejoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Los7MejoresViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_los7mejores, parent, false)
        return Los7MejoresViewHolder(view)
    }

    override fun onBindViewHolder(holder: Los7MejoresViewHolder, position: Int) {
        val condecoracion = condecoraciones[position]
        holder.bind(condecoracion)
    }

    override fun getItemCount(): Int = condecoraciones.size

    inner class Los7MejoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val condecoracionImage: ImageView = itemView.findViewById(R.id.img_condecoracion)
        private val condecoracionName: TextView = itemView.findViewById(R.id.tv_condecoracion_name)
        private val condecoracionMeaning: TextView = itemView.findViewById(R.id.tv_condecoracion_meaning)
        private val condecoracionPosition: TextView = itemView.findViewById(R.id.tv_condecoracion_position)

        fun bind(condecoracion: Los7MejoresCondecoracion) {
            val resourceId = when(condecoracion.imageName) {
                "ic_discipulus_optimus_7" -> R.drawable.ic_discipulus_optimus_7
                "ic_intellectus_primus_6" -> R.drawable.ic_intellectus_primus_6
                "ic_consilium_magnus_5" -> R.drawable.ic_consilium_magnus_5
                "ic_doctrinae_princeps_4" -> R.drawable.ic_doctrinae_princeps_4
                "ic_luminis_rex_3" -> R.drawable.ic_luminis_rex_3
                "ic_mentis_aurea_2" -> R.drawable.ic_mentis_aurea_2
                "ic_sapiens_supremus_1" -> R.drawable.ic_sapiens_supremus_1
                else -> R.drawable.ic_sapiens_supremus_1
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
                6 -> R.string.position_6
                7 -> R.string.position_7
                else -> R.string.position_7
            }
            condecoracionPosition.text = context.getString(positionStringId)
        }
    }
}