package com.example.sumamente.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class Top10PagerAdapter(
    private val context: Context,
    private val condecoraciones: List<Top10Condecoracion>
) : RecyclerView.Adapter<Top10PagerAdapter.Top10ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Top10ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_top10, parent, false)
        return Top10ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Top10ViewHolder, position: Int) {
        val condecoracion = condecoraciones[position]
        holder.bind(condecoracion)
    }

    override fun getItemCount(): Int = condecoraciones.size

    inner class Top10ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val condecoracionImage: ImageView = itemView.findViewById(R.id.img_condecoracion)
        private val condecoracionDescription: TextView = itemView.findViewById(R.id.tv_condecoracion_description)
        private val condecoracionPosition: TextView = itemView.findViewById(R.id.tv_condecoracion_position)

        fun bind(condecoracion: Top10Condecoracion) {
            val resourceId = when(condecoracion.imageName) {
                "ic_estrella_honorabilis" -> R.drawable.ic_estrella_honorabilis
                "ic_estrella_virtuosus" -> R.drawable.ic_estrella_virtuosus
                "ic_estrella_insignis" -> R.drawable.ic_estrella_insignis
                "ic_estrella_praestans" -> R.drawable.ic_estrella_praestans
                "ic_estrella_illustris" -> R.drawable.ic_estrella_illustris
                "ic_antorcha_gloriosus" -> R.drawable.ic_antorcha_gloriosus
                "ic_antorcha_venerabilis" -> R.drawable.ic_antorcha_venerabilis
                "ic_antorcha_magnanimous" -> R.drawable.ic_antorcha_magnanimous
                "ic_corona_summum" -> R.drawable.ic_corona_summum
                "ic_corona_excelsitur" -> R.drawable.ic_corona_excelsitur
                else -> R.drawable.ic_estrella_honorabilis
            }

            condecoracionImage.setImageResource(resourceId)
            condecoracionDescription.text = condecoracion.description

            val positionStringId = when(condecoracion.position) {
                1 -> R.string.position_1
                2 -> R.string.position_2
                3 -> R.string.position_3
                4 -> R.string.position_4
                5 -> R.string.position_5
                6 -> R.string.position_6
                7 -> R.string.position_7
                8 -> R.string.position_8
                9 -> R.string.position_9
                10 -> R.string.position_10
                else -> R.string.position_10
            }
            condecoracionPosition.text = context.getString(positionStringId)
        }
    }
}
