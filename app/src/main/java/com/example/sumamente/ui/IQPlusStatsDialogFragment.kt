package com.example.sumamente.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
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

        val precision = arguments?.getDouble("precision") ?: 0.0
        val tiempo = arguments?.getDouble("tiempo") ?: 0.0

        tvPrecision.text = String.format(Locale.ROOT, "%.2f%%", precision * 100)
        tvTiempo.text = String.format(Locale.ROOT, "%.2f s", tiempo)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.indice_iqplus))
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .create()
    }
}