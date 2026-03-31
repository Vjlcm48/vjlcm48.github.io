package com.heptacreation.sumamente.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.heptacreation.sumamente.R
import androidx.core.content.edit

class InsigniaRIPlusBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_insignia_ri_plus, container, false)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<ImageView>(R.id.btn_cerrar_bottomsheet).setOnClickListener { dismiss() }
        view.findViewById<AppCompatButton>(R.id.btn_entendido_insignia).setOnClickListener { dismiss() }

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) marcarComoVista()
        }, 2000)

        return view
    }

    private fun marcarComoVista() {
        val prefs = requireContext().getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE)
        prefs.edit { putBoolean("insignia_ri_plus_vista", true) }

        val user = FirebaseAuth.getInstance().currentUser ?: return
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(user.uid)
            .set(mapOf("insignia_ri_plus_vista" to true), SetOptions.merge())
    }
}