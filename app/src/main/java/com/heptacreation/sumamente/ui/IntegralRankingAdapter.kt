package com.heptacreation.sumamente.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R
import java.util.Locale

data class IntegralRankingItem(
    val position: Int,
    val username: String,
    val countryCode: String,
    val integralScore: Double,
    val isCurrentUser: Boolean = false,
    val hasInsigniaRIPlus: Boolean = false
)

class IntegralRankingAdapter(
    private val items: List<IntegralRankingItem>
) : RecyclerView.Adapter<IntegralRankingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.integral_ranking_item_container)
        val positionTextView: TextView = view.findViewById(R.id.tv_position)
        val usernameTextView: TextView = view.findViewById(R.id.tv_username)
        val countryFlagImageView: ImageView = view.findViewById(R.id.iv_country_flag)
        val integralScoreTextView: TextView = view.findViewById(R.id.tv_integral_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_integral_ranking, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.positionTextView.text = item.position.toString()


        if (item.hasInsigniaRIPlus) {

            val usernameContainer = LinearLayout(holder.itemView.context)
            usernameContainer.orientation = LinearLayout.HORIZONTAL
            usernameContainer.gravity = android.view.Gravity.CENTER_VERTICAL


            (holder.usernameTextView.parent as ViewGroup).apply {
                val index = indexOfChild(holder.usernameTextView)
                removeView(holder.usernameTextView)
                addView(usernameContainer, index, holder.usernameTextView.layoutParams)
            }


            val usernameView = TextView(holder.itemView.context).apply {
                text = item.username
                textSize = 16f
                val textColor = if (item.isCurrentUser) R.color.highlight_user_text else android.R.color.black
                setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            usernameContainer.addView(usernameView)


            val insigniaImageView = ImageView(holder.itemView.context).apply {
                setImageResource(R.drawable.ic_insignia_ri_plus)
                val layoutParams = LinearLayout.LayoutParams(36, 36)
                layoutParams.marginStart = 8
                this.layoutParams = layoutParams
                setOnClickListener { showInsigniaTooltip(it) }
            }
            usernameContainer.addView(insigniaImageView)

        } else {

            holder.usernameTextView.text = item.username
            val textColor = if (item.isCurrentUser) R.color.highlight_user_text else android.R.color.black
            holder.usernameTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))
        }


        val countryCode = item.countryCode.lowercase(Locale.ROOT)
        val resId = FlagsAdapter.flagResourceMap[countryCode]

        if (resId != null) {
            holder.countryFlagImageView.setImageResource(resId)
        } else {
            holder.countryFlagImageView.setImageResource(R.drawable.ve)
        }

        val scoreWithLabel = holder.itemView.context.getString(
            R.string.integral_score_label,
            item.integralScore
        )
        holder.integralScoreTextView.text = scoreWithLabel


        if (item.isCurrentUser) {
            holder.container.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_background)
            )
            holder.positionTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_text)
            )
            holder.integralScoreTextView.setTextColor(
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
            holder.integralScoreTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.black)
            )
        }

        if (!item.isCurrentUser) {
            when (item.position) {
                1 -> {
                    holder.positionTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gold))
                    holder.integralScoreTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gold))
                }
                2 -> {
                    holder.positionTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.silver))
                    holder.integralScoreTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.silver))
                }
                3 -> {
                    holder.positionTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.bronze))
                    holder.integralScoreTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.bronze))
                }
            }
        }

        if (item.isCurrentUser) {
            holder.positionTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_text)
            )
            holder.integralScoreTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_text)
            )
        }
    }

    override fun getItemCount() = items.size

    private fun showInsigniaTooltip(view: View) {
        android.widget.Toast.makeText(view.context, "SUPREMUS INTEGRALIS", android.widget.Toast.LENGTH_SHORT).show()
    }
}
