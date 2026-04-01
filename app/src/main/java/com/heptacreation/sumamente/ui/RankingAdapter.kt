package com.heptacreation.sumamente.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R

data class GlobalRankingItem(
    val position: Int,
    val username: String,
    val countryCode: String,
    val totalPoints: Long,
    val isCurrentUser: Boolean = false,
    val hasInsigniaRIPlus: Boolean = false
)

data class RankingItem(
    val position: Int,
    val username: String,
    val countryCode: String,
    val score: Int,
    val isCurrentUser: Boolean = false,
    val hasInsigniaRIPlus: Boolean = false,
    val topPercentage: String? = null,
    val isPromptRow: Boolean = false
)

class RankingAdapter(
    private val items: List<RankingItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onDiscoverClick: (() -> Unit)? = null
    var onNotNowClick: (() -> Unit)? = null

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_PROMPT = 1
    }

    class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: androidx.cardview.widget.CardView = view.findViewById(R.id.card_ranking_item)
        val container: View = view.findViewById(R.id.ranking_item_container)
        val userStrip: View = view.findViewById(R.id.view_user_strip)
        val positionTextView: TextView = view.findViewById(R.id.tv_position)
        val usernameTextView: TextView = view.findViewById(R.id.tv_username)
        val insigniaImageView: ImageView = view.findViewById(R.id.iv_insignia)
        val countryFlagImageView: ImageView = view.findViewById(R.id.iv_country_flag)
        val scoreTextView: TextView = view.findViewById(R.id.tv_score)
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
                .inflate(R.layout.item_ranking, parent, false)
            NormalViewHolder(view)
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

        if (holder is NormalViewHolder) {
            if (item.topPercentage != null && item.isCurrentUser) {
                holder.positionTextView.text = holder.itemView.context.getString(
                    R.string.ranking_top_percent_label,
                    item.topPercentage
                )
            } else {
                holder.positionTextView.text = item.position.toString()
            }

            holder.usernameTextView.text = item.username

            holder.insigniaImageView.visibility = if (item.hasInsigniaRIPlus) View.VISIBLE else View.GONE
            holder.insigniaImageView.setOnClickListener {
                val ctx = holder.itemView.context
                val prefs = ctx.getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE)
                if (prefs.getBoolean("insignia_ri_plus_vista", false)) {
                    android.widget.Toast.makeText(ctx, ctx.getString(R.string.insignia_supremus_integralis), android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    val fm = (ctx as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager
                    fm?.let { InsigniaRIPlusBottomSheet().show(it, "InsigniaBottomSheet") }
                }
            }

            val flagResId = FlagsAdapter.flagResourceMap[item.countryCode.lowercase()]
            if (flagResId != null) {
                holder.countryFlagImageView.setImageResource(flagResId)
                holder.countryFlagImageView.visibility = View.VISIBLE
            } else {
                holder.countryFlagImageView.visibility = View.GONE
            }

            val formattedScore = java.text.NumberFormat.getNumberInstance().format(item.score)
            holder.scoreTextView.text = formattedScore
            holder.scoreTextView.visibility = View.VISIBLE

            if (item.isCurrentUser) {
                holder.userStrip.visibility = View.VISIBLE
                holder.card.cardElevation = holder.itemView.context.resources.displayMetrics.density * 8
                holder.container.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_background)
                )
                val highlightColor = ContextCompat.getColor(holder.itemView.context, R.color.highlight_user_text)
                holder.positionTextView.setTextColor(highlightColor)
                holder.positionTextView.textSize = 17f
                holder.usernameTextView.setTextColor(highlightColor)
                holder.usernameTextView.setTypeface(null, android.graphics.Typeface.BOLD)
                holder.usernameTextView.textSize = 17f
                holder.scoreTextView.setTextColor(highlightColor)
                holder.scoreTextView.textSize = 17f
            } else {
                holder.userStrip.visibility = View.GONE
                holder.card.cardElevation = holder.itemView.context.resources.displayMetrics.density * 2
                val backgroundColor = if (position % 2 == 0) R.color.ranking_item_even else R.color.ranking_item_odd
                holder.container.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, backgroundColor)
                )
                holder.positionTextView.setTextColor(
                    getColorFromAttr(holder.itemView.context, R.attr.colorOnBackground)
                )
                holder.positionTextView.textSize = 16f
                holder.usernameTextView.setTextColor(
                    getColorFromAttr(holder.itemView.context, R.attr.colorOnBackground)
                )
                holder.usernameTextView.setTypeface(null, android.graphics.Typeface.NORMAL)
                holder.usernameTextView.textSize = 16f
                holder.scoreTextView.setTextColor(
                    getColorFromAttr(holder.itemView.context, R.attr.colorOnBackground)
                )
                holder.scoreTextView.textSize = 16f
            }
        }
    }

    private fun getColorFromAttr(context: android.content.Context, attrId: Int): Int {
        val typedValue = android.util.TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    override fun getItemCount() = items.size
}