package com.example.sumamente.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class PinPagerAdapter(
    private val context: Context,
    private val pines: List<Pin>
) : RecyclerView.Adapter<PinPagerAdapter.PinViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pin, parent, false)
        return PinViewHolder(view)
    }

    override fun onBindViewHolder(holder: PinViewHolder, position: Int) {
        val pin = pines[position]
        holder.bind(pin)
    }

    override fun getItemCount(): Int = pines.size

    inner class PinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pinImage: ImageView = itemView.findViewById(R.id.img_pin)
        private val pinDescription: TextView = itemView.findViewById(R.id.tv_pin_description)

        fun bind(pin: Pin) {
            val resourceId = when(pin.imageName) {
                "ic_pin_victoris" -> R.drawable.ic_pin_victoris
                "ic_pin_optimum" -> R.drawable.ic_pin_optimum
                "ic_pin_invictus" -> R.drawable.ic_pin_invictus
                else -> R.drawable.ic_pin_optimum
            }
            pinImage.setImageResource(resourceId)
            pinDescription.text = pin.description
        }
    }
}
