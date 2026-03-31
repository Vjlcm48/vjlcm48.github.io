package com.heptacreation.sumamente.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R
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
        val card: androidx.cardview.widget.CardView = view.findViewById(R.id.card_speed_ranking_item)
        val container: View = view.findViewById(R.id.speed_ranking_item_container)
        val userStrip: View = view.findViewById(R.id.view_user_strip)
        val positionTextView: TextView = view.findViewById(R.id.tv_position)
        val usernameTextView: TextView = view.findViewById(R.id.tv_username)
        val insigniaImageView: ImageView = view.findViewById(R.id.iv_insignia)
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
        holder.usernameTextView.text = item.username

        holder.insigniaImageView.visibility = if (item.hasInsigniaRIPlus) View.VISIBLE else View.GONE
        holder.insigniaImageView.setOnClickListener {
            val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            if (prefs.getBoolean("insignia_ri_plus_vista", false)) {
                android.widget.Toast.makeText(context, context.getString(R.string.insignia_supremus_integralis), android.widget.Toast.LENGTH_SHORT).show()
            } else {
                val fm = (context as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager
                fm?.let { InsigniaRIPlusBottomSheet().show(it, "InsigniaBottomSheet") }
            }
        }

        val countryCode = item.countryCode.lowercase(Locale.ROOT)
        val resId = FlagsAdapter.flagResourceMap[countryCode]
        if (resId != null) {
            holder.countryFlagImageView.setImageResource(resId)
            holder.countryFlagImageView.visibility = View.VISIBLE
        } else {
            holder.countryFlagImageView.visibility = View.GONE
        }

        val timeFormatted = formatTime(item.averageTime)
        holder.timeTextView.text = context.getString(R.string.time_format_seconds, timeFormatted)

        if (item.isCurrentUser) {
            holder.userStrip.visibility = View.VISIBLE
            holder.card.cardElevation = context.resources.displayMetrics.density * 8

            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, R.color.highlight_user_background)
            )
            val highlightColor = ContextCompat.getColor(context, R.color.highlight_user_text)
            holder.positionTextView.setTextColor(highlightColor)
            holder.positionTextView.textSize = 17f
            holder.usernameTextView.setTextColor(highlightColor)
            holder.usernameTextView.setTypeface(null, android.graphics.Typeface.BOLD)
            holder.usernameTextView.textSize = 17f
            holder.timeTextView.setTextColor(highlightColor)
            holder.timeTextView.textSize = 17f
        } else {
            holder.userStrip.visibility = View.GONE
            holder.card.cardElevation = context.resources.displayMetrics.density * 2

            val backgroundColor = if (position % 2 == 0)
                R.color.ranking_item_even
            else
                R.color.ranking_item_odd

            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, backgroundColor)
            )
            holder.positionTextView.setTextColor(
                getColorFromAttr(context, R.attr.colorOnBackground)
            )
            holder.positionTextView.textSize = 16f
            holder.usernameTextView.setTextColor(
                getColorFromAttr(context, R.attr.colorOnBackground)
            )
            holder.usernameTextView.setTypeface(null, android.graphics.Typeface.NORMAL)
            holder.usernameTextView.textSize = 16f
            holder.timeTextView.setTextColor(
                getColorFromAttr(context, R.attr.colorOnBackground)
            )
            holder.timeTextView.textSize = 16f
        }

        // Colores especiales para el podio (posiciones 1, 2, 3)
        when (item.position) {
            1 -> {
                holder.positionTextView.setTextColor(ContextCompat.getColor(context, R.color.gold))
                holder.timeTextView.setTextColor(ContextCompat.getColor(context, R.color.gold))
            }
            2 -> {
                holder.positionTextView.setTextColor(ContextCompat.getColor(context, R.color.silver))
                holder.timeTextView.setTextColor(ContextCompat.getColor(context, R.color.silver))
            }
            3 -> {
                holder.positionTextView.setTextColor(ContextCompat.getColor(context, R.color.bronze))
                holder.timeTextView.setTextColor(ContextCompat.getColor(context, R.color.bronze))
            }
        }
    }

    private fun getColorFromAttr(context: Context, attrId: Int): Int {
        val typedValue = android.util.TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    override fun getItemCount() = items.size

    private fun formatTime(timeInSeconds: Float): String {
        val seconds = timeInSeconds.toInt()
        val milliseconds = ((timeInSeconds - seconds) * 100).toInt()
        return String.format(Locale.getDefault(), "%02d:%02d", seconds, milliseconds)
    }
}