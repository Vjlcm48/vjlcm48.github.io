package com.example.sumamente.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.sumamente.R
import com.github.chrisbanes.photoview.PhotoView

class ImagenAmpliadaDialog(
    context: Context,
    private val titulo: String,
    private val imagenResourceId: Int
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_imagen_ampliada)

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        findViewById<TextView>(R.id.tv_titulo_imagen).text = titulo
        val photoView = findViewById<PhotoView>(R.id.img_ampliada)
        photoView.setImageResource(imagenResourceId)

        findViewById<Button>(R.id.btn_cerrar).setOnClickListener {
            dismiss()
        }
    }

}
