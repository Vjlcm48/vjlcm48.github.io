package com.heptacreation.sumamente.ui

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        holder.tvPlayerName.text = item.username

        holder.ivInsignia.visibility = if (item.hasInsigniaRIPlus) View.VISIBLE else View.GONE
        holder.ivInsignia.setOnClickListener {
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
            holder.ivFlag.setImageResource(resId)
            holder.ivFlag.visibility = View.VISIBLE
        } else {
            holder.ivFlag.visibility = View.GONE
        }

        val iqPlusWithLabel = context.getString(R.string.integral_score_label, item.iqPlus)
        holder.tvIQPlus.text = iqPlusWithLabel

        if (item.isCurrentUser) {
            holder.userStrip.visibility = View.VISIBLE
            holder.card.cardElevation = context.resources.displayMetrics.density * 8
            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, R.color.highlight_user_background)
            )
            val highlightColor = ContextCompat.getColor(context, R.color.highlight_user_text)
            holder.tvPos.setTextColor(highlightColor)
            holder.tvPos.textSize = 17f
            holder.tvPlayerName.setTextColor(highlightColor)
            holder.tvPlayerName.setTypeface(null, Typeface.BOLD)
            holder.tvPlayerName.textSize = 17f
            holder.tvIQPlus.setTextColor(highlightColor)
            holder.tvIQPlus.textSize = 17f
        } else {
            holder.userStrip.visibility = View.GONE
            holder.card.cardElevation = context.resources.displayMetrics.density * 2
            val backgroundColor = if (position % 2 == 0) R.color.ranking_item_even else R.color.ranking_item_odd
            holder.container.setBackgroundColor(
                ContextCompat.getColor(context, backgroundColor)
            )
            holder.tvPos.setTextColor(getColorFromAttr(context, R.attr.colorOnBackground))
            holder.tvPos.textSize = 16f
            holder.tvPlayerName.setTextColor(getColorFromAttr(context, R.attr.colorOnBackground))
            holder.tvPlayerName.setTypeface(null, Typeface.NORMAL)
            holder.tvPlayerName.textSize = 16f
            holder.tvIQPlus.setTextColor(getColorFromAttr(context, R.attr.colorOnBackground))
            holder.tvIQPlus.textSize = 16f
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
                    .scaleX(1.1f).scaleY(1.1f).setDuration(100)
                    .withEndAction {
                        holder.tvIQPlus.animate()
                            .scaleX(1f).scaleY(1f).setDuration(100)
                            .withEndAction { onIQPlusClick.invoke() }
                            .start()
                    }.start()
            }
        }
    }

    private fun getColorFromAttr(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    override fun getItemCount() = rankingList.size

    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: androidx.cardview.widget.CardView = itemView.findViewById(R.id.card_iqplus_ranking_item)
        val container: View = itemView.findViewById(R.id.iqplus_ranking_item_container)
        val userStrip: View = itemView.findViewById(R.id.view_user_strip)
        val tvPos: TextView = itemView.findViewById(R.id.tvPos)
        val tvPlayerName: TextView = itemView.findViewById(R.id.tvPlayerName)
        val ivInsignia: ImageView = itemView.findViewById(R.id.iv_insignia)
        val ivFlag: ImageView = itemView.findViewById(R.id.ivFlag)
        val tvIQPlus: TextView = itemView.findViewById(R.id.tvIQPlus)
    }
}