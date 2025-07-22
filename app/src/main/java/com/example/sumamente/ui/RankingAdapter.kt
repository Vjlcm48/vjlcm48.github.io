package com.example.sumamente.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import java.util.Locale

data class RankingItem(
    val position: Int,
    val username: String,
    val countryCode: String,
    val score: Int,
    val isCurrentUser: Boolean = false
)

class RankingAdapter(
    private val items: List<RankingItem>
) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.ranking_item_container)
        val positionTextView: TextView = view.findViewById(R.id.tv_position)
        val usernameTextView: TextView = view.findViewById(R.id.tv_username)
        val countryFlagImageView: ImageView = view.findViewById(R.id.iv_country_flag)
        val scoreTextView: TextView = view.findViewById(R.id.tv_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.positionTextView.text = item.position.toString()
        holder.usernameTextView.text = item.username

        // Verificar si el usuario tiene la insignia RI+
        if (item.isCurrentUser) {
            val tieneInsignia = CondecoracionTracker.getInsigniaRIPlus() != null
            if (tieneInsignia) {
                val usernameContainer = LinearLayout(holder.itemView.context)
                usernameContainer.orientation = LinearLayout.HORIZONTAL
                usernameContainer.gravity = android.view.Gravity.CENTER_VERTICAL

                val usernameView = TextView(holder.itemView.context)
                usernameView.text = item.username
                usernameView.textSize = 16f
                usernameView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_text))
                usernameView.setTypeface(null, android.graphics.Typeface.BOLD)

                val insigniaImageView = ImageView(holder.itemView.context)
                insigniaImageView.setImageResource(R.drawable.ic_insignia_ri_plus)
                val layoutParams = LinearLayout.LayoutParams(24, 24)
                layoutParams.setMargins(8, 0, 0, 0)
                insigniaImageView.layoutParams = layoutParams

                insigniaImageView.setOnClickListener {
                    android.widget.Toast.makeText(it.context, "SUPREMUS INTEGRALIS", android.widget.Toast.LENGTH_SHORT).show()
                }

                usernameContainer.addView(usernameView)
                usernameContainer.addView(insigniaImageView)

                val parent = holder.usernameTextView.parent as ViewGroup
                val index = parent.indexOfChild(holder.usernameTextView)
                parent.removeView(holder.usernameTextView)
                parent.addView(usernameContainer, index)
            }
        }

        val countryCode = item.countryCode.lowercase(Locale.ROOT)
        val resId = countryFlagMap[countryCode]

        if (resId != null) {
            holder.countryFlagImageView.setImageResource(resId)
        } else {

            holder.countryFlagImageView.setImageResource(R.drawable.ve)
        }

        holder.scoreTextView.text = item.score.toString()

        if (item.isCurrentUser) {
            holder.container.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_background)
            )

            holder.positionTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_text)
            )
            holder.usernameTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_text)
            )
            holder.scoreTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_text)
            )
        } else {
            val backgroundColor = if (position % 2 == 0)
                R.color.ranking_item_even
            else
                R.color.ranking_item_odd

            holder.container.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, backgroundColor)
            )

            holder.positionTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.black)
            )
            holder.usernameTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.black)
            )
            holder.scoreTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.black)
            )
        }

        when (item.position) {
            1 -> holder.positionTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.gold)
            )
            2 -> holder.positionTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.silver)
            )
            3 -> holder.positionTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.bronze)
            )
        }
    }

    override fun getItemCount() = items.size
}
