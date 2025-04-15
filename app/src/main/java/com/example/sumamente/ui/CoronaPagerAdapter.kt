package com.example.sumamente.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class CoronaPagerAdapter(
    private val context: Context,
    private val coronas: List<Corona>
) : RecyclerView.Adapter<CoronaPagerAdapter.CoronaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoronaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_corona, parent, false)
        return CoronaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoronaViewHolder, position: Int) {
        val corona = coronas[position]
        holder.bind(corona)
    }

    override fun getItemCount(): Int = coronas.size

    inner class CoronaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coronaImage: ImageView = itemView.findViewById(R.id.img_corona)
        private val coronaDescription: TextView = itemView.findViewById(R.id.tv_corona_description)

        fun bind(corona: Corona) {
            val resourceId = when(corona.imageName) {
                "ic_corona_velocitas_alas" -> R.drawable.ic_corona_velocitas_alas
                "ic_corona_celeris_alas" -> R.drawable.ic_corona_celeris_alas
                "ic_corona_volucer_alas" -> R.drawable.ic_corona_volucer_alas
                else -> R.drawable.ic_corona_velocitas_alas
            }
            coronaImage.setImageResource(resourceId)
            coronaDescription.text = corona.description
        }
    }
}