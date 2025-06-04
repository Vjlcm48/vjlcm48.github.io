package com.example.sumamente.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Button
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.dialog_iqplus_stats, container, false)


        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvPrecision: TextView = view.findViewById(R.id.tvPrecisionValue)
        val tvTiempo: TextView = view.findViewById(R.id.tvTiempoValue)
        val infoPrecision: ImageButton = view.findViewById(R.id.info_precision)
        val infoVelocidad: ImageButton = view.findViewById(R.id.info_velocidad)
        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)

        val precision = arguments?.getDouble("precision") ?: 0.0
        val tiempo = arguments?.getDouble("tiempo") ?: 0.0

        val aciertos = ScoreManager.correctGamesGlobal
        val jugados = ScoreManager.totalGamesGlobal
        tvPrecision.text = String.format(Locale.ROOT, "%.2f%% (%d/%d)", precision * 100, aciertos, jugados)

        tvTiempo.text = String.format(Locale.ROOT, "%.2f s", tiempo)

        infoPrecision.setOnClickListener {
            showExplanationDialog(
                getString(R.string.precision_global_title),
                getString(R.string.precision_global_explanation)
            )
        }

        infoVelocidad.setOnClickListener {
            showExplanationDialog(
                getString(R.string.velocidad_promedio_title),
                getString(R.string.velocidad_promedio_explanation)
            )
        }

        btnAceptar.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun showExplanationDialog(title: String, explanation: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(explanation)
            .setPositiveButton(R.string.btn_accept, null)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background_with_border)

        dialog.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTypeface(null, android.graphics.Typeface.BOLD)
    }


}
