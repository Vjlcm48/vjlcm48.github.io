package com.example.sumamente.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class TrofeoPagerAdapter(
    private val context: Context,
    private val trofeos: List<Trofeo>
) : RecyclerView.Adapter<TrofeoPagerAdapter.TrofeoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrofeoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_trofeo, parent, false)
        return TrofeoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrofeoViewHolder, position: Int) {
        val trofeo = trofeos[position]
        holder.bind(trofeo)
    }

    override fun getItemCount(): Int = trofeos.size

    inner class TrofeoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trofeoImage: ImageView = itemView.findViewById(R.id.img_trofeo)
        private val trofeoDescription: TextView = itemView.findViewById(R.id.tv_trofeo_description)

        fun bind(trofeo: Trofeo) {
            val resourceId = when(trofeo.imageName) {
                "ic_trofeo_initia" -> R.drawable.ic_trofeo_initia
                "ic_trofeo_constantia" -> R.drawable.ic_trofeo_constantia
                "ic_trofeo_confectus" -> R.drawable.ic_trofeo_confectus
                "ic_trofeo_via" -> R.drawable.ic_trofeo_via
                "ic_trofeo_altus" -> R.drawable.ic_trofeo_altus
                "ic_trofeo_perseverantia" -> R.drawable.ic_trofeo_perseverantia
                "ic_trofeo_gradus" -> R.drawable.ic_trofeo_gradus
                "ic_trofeo_fortitudo" -> R.drawable.ic_trofeo_fortitudo
                "ic_trofeo_metam" -> R.drawable.ic_trofeo_metam
                "ic_trofeo_fundamentum" -> R.drawable.ic_trofeo_fundamentum
                "ic_trofeo_praemium" -> R.drawable.ic_trofeo_praemium
                "ic_trofeo_glorificus" -> R.drawable.ic_trofeo_glorificus
                "ic_trofeo_scala" -> R.drawable.ic_trofeo_scala
                "ic_trofeo_tenacitas" -> R.drawable.ic_trofeo_tenacitas
                "ic_trofeo_perfectus" -> R.drawable.ic_trofeo_perfectus
                "ic_trofeo_origo" -> R.drawable.ic_trofeo_origo
                "ic_trofeo_proficium" -> R.drawable.ic_trofeo_proficium
                "ic_trofeo_exemplaritas" -> R.drawable.ic_trofeo_exemplaritas
                "ic_trofeo_ascensus" -> R.drawable.ic_trofeo_ascensus
                "ic_trofeo_magnificus" -> R.drawable.ic_trofeo_magnificus
                "ic_trofeo_potens" -> R.drawable.ic_trofeo_potens
                else -> R.drawable.ic_trofeo_initia
            }
            trofeoImage.setImageResource(resourceId)
            trofeoDescription.text = trofeo.description
        }
    }
}
