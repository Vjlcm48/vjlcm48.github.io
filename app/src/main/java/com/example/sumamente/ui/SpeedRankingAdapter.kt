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

data class SpeedRankingItem(
    val position: Int,
    val username: String,
    val countryCode: String,
    val averageTime: Float,
    val isCurrentUser: Boolean = false,
    val hasInsigniaRIPlus: Boolean = false
)

class SpeedRankingAdapter(
    private val items: List<SpeedRankingItem>
) : RecyclerView.Adapter<SpeedRankingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.speed_ranking_item_container)
        val positionTextView: TextView = view.findViewById(R.id.tv_position)
        val usernameTextView: TextView = view.findViewById(R.id.tv_username)
        val countryFlagImageView: ImageView = view.findViewById(R.id.iv_country_flag)
        val timeTextView: TextView = view.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_speed_ranking, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.positionTextView.text = item.position.toString()

        if (item.hasInsigniaRIPlus) {

            val usernameContainer = LinearLayout(context)
            usernameContainer.orientation = LinearLayout.HORIZONTAL
            usernameContainer.gravity = android.view.Gravity.CENTER_VERTICAL


            (holder.usernameTextView.parent as ViewGroup).apply {
                val index = indexOfChild(holder.usernameTextView)
                removeView(holder.usernameTextView)
                addView(usernameContainer, index, holder.usernameTextView.layoutParams)
            }

            val usernameView = TextView(context).apply {
                text = item.username
                textSize = 16f
                val textColor = if (item.isCurrentUser) R.color.highlight_user_text else android.R.color.black
                setTextColor(ContextCompat.getColor(context, textColor))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            usernameContainer.addView(usernameView)

            val insigniaImageView = ImageView(context).apply {
                setImageResource(R.drawable.ic_insignia_ri_plus)
                val layoutParams = LinearLayout.LayoutParams(36, 36)
                layoutParams.marginStart = 8
                this.layoutParams = layoutParams
                setOnClickListener {
                    android.widget.Toast.makeText(context, "SUPREMUS INTEGRALIS", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            usernameContainer.addView(insigniaImageView)

        } else {

            holder.usernameTextView.text = item.username
            val textColor = if (item.isCurrentUser) R.color.highlight_user_text else android.R.color.black
            holder.usernameTextView.setTextColor(ContextCompat.getColor(context, textColor))
        }

        val countryCode = item.countryCode.lowercase(Locale.ROOT)
        val resId = FlagsAdapter.flagResourceMap[countryCode]

        if (resId != null) {
            holder.countryFlagImageView.setImageResource(resId)
        } else {
            holder.countryFlagImageView.setImageResource(R.drawable.ve)
        }

        val timeFormatted = formatTime(item.averageTime)
        holder.timeTextView.text = context.getString(R.string.time_format_seconds, timeFormatted)

        if (item.isCurrentUser) {
            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, R.color.highlight_user_background)
            )
            holder.positionTextView.setTextColor(
                ContextCompat.getColor(context, R.color.highlight_user_text)
            )
            holder.timeTextView.setTextColor(
                ContextCompat.getColor(context, R.color.highlight_user_text)
            )
        } else {
            val backgroundColor = if (position % 2 == 0)
                R.color.ranking_item_even
            else
                R.color.ranking_item_odd

            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, backgroundColor)
            )
            holder.positionTextView.setTextColor(
                ContextCompat.getColor(context, android.R.color.black)
            )
            holder.timeTextView.setTextColor(
                ContextCompat.getColor(context, android.R.color.black)
            )
        }

        when (item.position) {
            1 -> {
                holder.positionTextView.setTextColor(
                    ContextCompat.getColor(context, R.color.gold)
                )
                holder.timeTextView.setTextColor(
                    ContextCompat.getColor(context, R.color.gold)
                )
            }
            2 -> {
                holder.positionTextView.setTextColor(
                    ContextCompat.getColor(context, R.color.silver)
                )
                holder.timeTextView.setTextColor(
                    ContextCompat.getColor(context, R.color.silver)
                )
            }
            3 -> {
                holder.positionTextView.setTextColor(
                    ContextCompat.getColor(context, R.color.bronze)
                )
                holder.timeTextView.setTextColor(
                    ContextCompat.getColor(context, R.color.bronze)
                )
            }
        }
    }

    override fun getItemCount() = items.size

    private fun formatTime(timeInSeconds: Float): String {
        val seconds = timeInSeconds.toInt()
        val milliseconds = ((timeInSeconds - seconds) * 100).toInt()
        return String.format(Locale.getDefault(), "%02d:%02d", seconds, milliseconds)
    }
}
