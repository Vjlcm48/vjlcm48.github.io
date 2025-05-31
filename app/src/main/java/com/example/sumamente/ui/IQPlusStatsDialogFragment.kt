package com.example.sumamente.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.sumamente.R
import java.util.Locale

class IQPlusStatsDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(precision: Double, tiempoPromedio: Double): IQPlusStatsDialogFragment {
            val frag = IQPlusStatsDialogFragment()
            val args = Bundle()
            args.putDouble("precision", precision)
            args.putDouble("tiempo", tiempoPromedio)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_iqplus_stats, null)
        val tvPrecision: TextView = view.findViewById(R.id.tvPrecisionValue)
        val tvTiempo: TextView = view.findViewById(R.id.tvTiempoValue)
        val infoPrecision: ImageButton = view.findViewById(R.id.info_precision)
        val infoVelocidad: ImageButton = view.findViewById(R.id.info_velocidad)

        val precision = arguments?.getDouble("precision") ?: 0.0
        val tiempo = arguments?.getDouble("tiempo") ?: 0.0

        tvPrecision.text = String.format(Locale.ROOT, "%.2f%%", precision * 100)
        tvTiempo.text = String.format(Locale.ROOT, "%.2f s", tiempo)

        infoPrecision.setOnClickListener {
            showExplanationDialog(getString(R.string.precision_global_title), getString(R.string.precision_global_explanation))
        }

        infoVelocidad.setOnClickListener {
            showExplanationDialog(getString(R.string.velocidad_promedio_title), getString(R.string.velocidad_promedio_explanation))
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.indice_iqplus))
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .create()


        dialog.setOnShowListener {
            val titleView = dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)
            titleView?.textSize = 21.6f
            titleView?.setTypeface(null, android.graphics.Typeface.BOLD)
        }

        return dialog

    }

    private fun showExplanationDialog(title: String, explanation: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(explanation)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}
