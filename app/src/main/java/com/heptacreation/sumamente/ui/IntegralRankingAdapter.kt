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

data class IntegralRankingItem(
    val position: Int,
    val username: String,
    val countryCode: String,
    val integralScore: Double,
    val isCurrentUser: Boolean = false,
    val hasInsigniaRIPlus: Boolean = false,
    val topPercentage: String? = null,
    val isPromptRow: Boolean = false
)

class IntegralRankingAdapter(
    private val items: List<IntegralRankingItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onDiscoverClick: (() -> Unit)? = null
    var onNotNowClick: (() -> Unit)? = null

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_PROMPT = 1
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: androidx.cardview.widget.CardView = view.findViewById(R.id.card_integral_ranking_item)
        val container: View = view.findViewById(R.id.integral_ranking_item_container)
        val userStrip: View = view.findViewById(R.id.view_user_strip)
        val positionTextView: TextView = view.findViewById(R.id.tv_position)
        val usernameTextView: TextView = view.findViewById(R.id.tv_username)
        val insigniaImageView: ImageView = view.findViewById(R.id.iv_insignia)
        val countryFlagImageView: ImageView = view.findViewById(R.id.iv_country_flag)
        val integralScoreTextView: TextView = view.findViewById(R.id.tv_integral_score)
    }

    class PromptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_prompt_title)
        val tvMessage: TextView = view.findViewById(R.id.tv_prompt_message)
        val btnDiscover: com.google.android.material.button.MaterialButton = view.findViewById(R.id.btn_discover_position)
        val btnNotNow: com.google.android.material.button.MaterialButton = view.findViewById(R.id.btn_not_now_position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isPromptRow) TYPE_PROMPT else TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_PROMPT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ranking_position_prompt, parent, false)
            PromptViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_integral_ranking, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        if (holder is PromptViewHolder) {
            holder.tvTitle.text = holder.itemView.context.getString(R.string.ranking_position_prompt_title)
            holder.tvMessage.text = holder.itemView.context.getString(
                R.string.ranking_position_prompt_message,
                item.topPercentage ?: ""
            )
            holder.btnDiscover.setOnClickListener { onDiscoverClick?.invoke() }
            holder.btnNotNow.setOnClickListener { onNotNowClick?.invoke() }
            return
        }

        if (holder is ViewHolder) {
            val context = holder.itemView.context

            if (item.topPercentage != null && item.isCurrentUser) {
                holder.positionTextView.text = context.getString(R.string.ranking_top_percent_label, item.topPercentage)
            } else {
                holder.positionTextView.text = item.position.toString()
            }

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

            val scoreWithLabel = context.getString(R.string.integral_score_label, item.integralScore)
            holder.integralScoreTextView.text = scoreWithLabel

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
                holder.usernameTextView.setTypeface(null, Typeface.BOLD)
                holder.usernameTextView.textSize = 17f
                holder.integralScoreTextView.setTextColor(highlightColor)
                holder.integralScoreTextView.textSize = 17f
            } else {
                holder.userStrip.visibility = View.GONE
                holder.card.cardElevation = context.resources.displayMetrics.density * 2
                val backgroundColor = if (position % 2 == 0) R.color.ranking_item_even else R.color.ranking_item_odd
                holder.container.setBackgroundColor(
                    ContextCompat.getColor(context, backgroundColor)
                )
                holder.positionTextView.setTextColor(getColorFromAttr(context, R.attr.colorOnBackground))
                holder.positionTextView.textSize = 16f
                holder.usernameTextView.setTextColor(getColorFromAttr(context, R.attr.colorOnBackground))
                holder.usernameTextView.setTypeface(null, Typeface.NORMAL)
                holder.usernameTextView.textSize = 16f
                holder.integralScoreTextView.setTextColor(getColorFromAttr(context, R.attr.colorOnBackground))
                holder.integralScoreTextView.textSize = 16f
            }

            when (item.position) {
                1 -> {
                    holder.positionTextView.setTextColor(ContextCompat.getColor(context, R.color.gold))
                    holder.integralScoreTextView.setTextColor(ContextCompat.getColor(context, R.color.gold))
                }
                2 -> {
                    holder.positionTextView.setTextColor(ContextCompat.getColor(context, R.color.silver))
                    holder.integralScoreTextView.setTextColor(ContextCompat.getColor(context, R.color.silver))
                }
                3 -> {
                    holder.positionTextView.setTextColor(ContextCompat.getColor(context, R.color.bronze))
                    holder.integralScoreTextView.setTextColor(ContextCompat.getColor(context, R.color.bronze))
                }
            }
        }
    }

    private fun getColorFromAttr(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    override fun getItemCount() = items.size
}