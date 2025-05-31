package com.example.sumamente.ui

import android.graphics.Typeface
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

data class IQPlusRankingItem(
    val position: Int,
    val username: String,
    val countryCode: String,
    val iqPlus: Double,
    val isCurrentUser: Boolean = false
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
        holder.tvPlayerName.text = item.username

        val countryCode = item.countryCode.lowercase(Locale.ROOT)
        val resId = countryFlagMap[countryCode]

        if (resId != null) {
            holder.ivFlag.setImageResource(resId)
        } else {
            holder.ivFlag.setImageResource(R.drawable.ve)
        }


        holder.tvIQPlus.text = String.format(Locale.ROOT, "%.3f", item.iqPlus)


        if (item.isCurrentUser) {

            holder.tvPlayerName.setTypeface(null, Typeface.BOLD)
            holder.tvPlayerName.setTextColor(ContextCompat.getColor(context, R.color.blue_primary))
            holder.tvIQPlus.setTextColor(ContextCompat.getColor(context, R.color.yellow_dark))


            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, R.color.highlight_user_background)
            )


            holder.tvPos.setTextColor(
                ContextCompat.getColor(context, R.color.highlight_user_text)
            )
            holder.tvPlayerName.setTextColor(
                ContextCompat.getColor(context, R.color.highlight_user_text)
            )
            holder.tvIQPlus.setTextColor(
                ContextCompat.getColor(context, R.color.highlight_user_text)
            )
        } else {

            holder.tvPlayerName.setTypeface(null, Typeface.NORMAL)
            holder.tvPlayerName.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.tvIQPlus.setTextColor(ContextCompat.getColor(context, R.color.blue_primary_dark))


            val backgroundColor = if (position % 2 == 0)
                R.color.ranking_item_even
            else
                R.color.ranking_item_odd

            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, backgroundColor)
            )


            holder.tvPos.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.tvPlayerName.setTextColor(ContextCompat.getColor(context, android.R.color.black))
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

                holder.tvIQPlus.isPressed = false
                holder.tvIQPlus.refreshDrawableState()

                onIQPlusClick.invoke()
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
