package com.heptacreation.sumamente.ui

import android.graphics.Typeface
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

data class IQPlusRankingItem(
    val position: Int,
    val username: String,
    val countryCode: String,
    val iqPlus: Double,
    val isCurrentUser: Boolean = false,
    val hasInsigniaRIPlus: Boolean = false
)

class IQPlusRankingAdapter(
    private val rankingList: List<IQPlusRankingItem>,
    private val onIQPlusClick: (() -> Unit)? = null
) : RecyclerView.Adapter<IQPlusRankingAdapter.RankingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_iqplus_ranking, parent, false)
        return RankingViewHolder(view)
    }


    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val item = rankingList[position]
        val context = holder.itemView.context

        holder.tvPos.text = item.position.toString()


        if (item.hasInsigniaRIPlus) {

            val usernameContainer = LinearLayout(context)
            usernameContainer.orientation = LinearLayout.HORIZONTAL
            usernameContainer.gravity = android.view.Gravity.CENTER_VERTICAL


            (holder.tvPlayerName.parent as ViewGroup).apply {
                val index = indexOfChild(holder.tvPlayerName)
                removeView(holder.tvPlayerName)
                addView(usernameContainer, index, holder.tvPlayerName.layoutParams)
            }


            val usernameView = TextView(context).apply {
                text = item.username
                textSize = 16f
                val textColor = if (item.isCurrentUser) R.color.highlight_user_text else android.R.color.black
                setTextColor(ContextCompat.getColor(context, textColor))
                setTypeface(null, Typeface.BOLD)
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

            holder.tvPlayerName.text = item.username
            val textColor = if (item.isCurrentUser) R.color.highlight_user_text else android.R.color.black
            holder.tvPlayerName.setTextColor(ContextCompat.getColor(context, textColor))

            holder.tvPlayerName.setTypeface(null, if (item.isCurrentUser) Typeface.BOLD else Typeface.NORMAL)
        }


        val countryCode = item.countryCode.lowercase(Locale.ROOT)
        val resId = FlagsAdapter.flagResourceMap[countryCode]

        if (resId != null) {
            holder.ivFlag.setImageResource(resId)
        } else {
            holder.ivFlag.setImageResource(R.drawable.ve)
        }

        val iqPlusWithLabel = context.getString(
            R.string.integral_score_label,
            item.iqPlus
        )
        holder.tvIQPlus.text = iqPlusWithLabel


        if (item.isCurrentUser) {
            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, R.color.highlight_user_background)
            )
            holder.tvPos.setTextColor(
                ContextCompat.getColor(context, R.color.highlight_user_text)
            )
            holder.tvIQPlus.setTextColor(
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
            holder.tvPos.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.tvIQPlus.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        when (item.position) {
            1 -> {
                holder.tvPos.setTextColor(ContextCompat.getColor(context, R.color.gold))
                holder.tvIQPlus.setTextColor(ContextCompat.getColor(context, R.color.gold))
            }
            2 -> {
                holder.tvPos.setTextColor(ContextCompat.getColor(context, R.color.silver))
                holder.tvIQPlus.setTextColor(ContextCompat.getColor(context, R.color.silver))
            }
            3 -> {
                holder.tvPos.setTextColor(ContextCompat.getColor(context, R.color.bronze))
                holder.tvIQPlus.setTextColor(ContextCompat.getColor(context, R.color.bronze))
            }
        }

        holder.tvIQPlus.setOnClickListener {
            if (item.isCurrentUser && onIQPlusClick != null) {
                holder.tvIQPlus.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(100)
                    .withEndAction {
                        holder.tvIQPlus.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction {
                                onIQPlusClick.invoke()
                            }
                            .start()
                    }
                    .start()
            }
        }
    }

    override fun getItemCount() = rankingList.size

    inner class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: LinearLayout = itemView.findViewById(R.id.iqplus_ranking_item_container)
        val tvPos: TextView = itemView.findViewById(R.id.tvPos)
        val tvPlayerName: TextView = itemView.findViewById(R.id.tvPlayerName)
        val ivFlag: ImageView = itemView.findViewById(R.id.ivFlag)
        val tvIQPlus: TextView = itemView.findViewById(R.id.tvIQPlus)
    }
}
