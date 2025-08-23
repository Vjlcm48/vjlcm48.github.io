package com.heptacreation.sumamente.ui

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R
import java.util.Locale

class GameProgressAdapter(
    private val context: Context,
    private val progressItems: List<GameProgressItem>
) : RecyclerView.Adapter<GameProgressAdapter.ProgressViewHolder>() {

    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: LinearLayout = itemView.findViewById(R.id.game_progress_container)
        val totalRow: RelativeLayout = itemView.findViewById(R.id.row_total)
        val gameNameTextView: TextView = itemView.findViewById(R.id.tv_game_name)
        val tvProgressTotal: TextView = itemView.findViewById(R.id.tv_progress_total)
        val tvProgressPrincipiante: TextView = itemView.findViewById(R.id.tv_progress_principiante)
        val tvProgressAvanzado: TextView = itemView.findViewById(R.id.tv_progress_avanzado)
        val tvProgressPro: TextView = itemView.findViewById(R.id.tv_progress_pro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_game_progress, parent, false)
        return ProgressViewHolder(view)
    }

    override fun getItemCount(): Int = progressItems.size

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        val item = progressItems[position]

        (holder.container.background as GradientDrawable).setStroke(
            3 * context.resources.displayMetrics.density.toInt(),
            ContextCompat.getColor(context, item.borderColorRes)
        )
        holder.totalRow.setBackgroundResource(item.totalRowBackgroundRes)

        holder.gameNameTextView.setTextColor(
            if (item.gameNameTextColorRes == R.attr.colorOnBackground) {
                getColorFromAttr(R.attr.colorOnBackground)
            } else {
                ContextCompat.getColor(context, item.gameNameTextColorRes)
            }
        )

        if (item.gameNameRes == R.string.game_numeros_plus || item.gameNameRes == R.string.game_mas_plus) {
            holder.tvProgressTotal.setTextColor(
                if (isNightMode()) getColorFromAttr(R.attr.colorOnBackground)
                else ContextCompat.getColor(context, android.R.color.white)
            )
        } else {
            holder.tvProgressTotal.setTextColor(
                if (isNightMode()) getColorFromAttr(R.attr.colorOnBackground)
                else ContextCompat.getColor(context, android.R.color.black)
            )
        }
        holder.tvProgressTotal.setTypeface(null, Typeface.BOLD)

        if (item.gameNameSpannable != null) {
            holder.gameNameTextView.text = item.gameNameSpannable
        } else if (item.gameNameRes != null) {
            holder.gameNameTextView.setText(item.gameNameRes)
        }

        val completedPrincipiante = item.getPrincipianteData()
        val completedAvanzado = item.getAvanzadoData()
        val completedPro = item.getProData()
        val totalCompleted = completedPrincipiante + completedAvanzado + completedPro

        val percPrincipiante = if (completedPrincipiante > 0) (completedPrincipiante / 70.0) * 100 else 0.0
        val percAvanzado = if (completedAvanzado > 0) (completedAvanzado / 70.0) * 100 else 0.0
        val percPro = if (completedPro > 0) (completedPro / 70.0) * 100 else 0.0
        val percTotal = if (totalCompleted > 0) (totalCompleted / 210.0) * 100 else 0.0

        val locale = Locale.getDefault()

        holder.tvProgressTotal.text = context.getString(R.string.game_progress_format, totalCompleted, 210, String.format(locale, "%.2f", percTotal))
        holder.tvProgressPrincipiante.text = context.getString(R.string.game_progress_format, completedPrincipiante, 70, String.format(locale, "%.2f", percPrincipiante))
        holder.tvProgressAvanzado.text = context.getString(R.string.game_progress_format, completedAvanzado, 70, String.format(locale, "%.2f", percAvanzado))
        holder.tvProgressPro.text = context.getString(R.string.game_progress_format, completedPro, 70, String.format(locale, "%.2f", percPro))
    }

    private fun isNightMode(): Boolean {
        return (context.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun getColorFromAttr(attrId: Int): Int {
        val typedValue = android.util.TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }
}
