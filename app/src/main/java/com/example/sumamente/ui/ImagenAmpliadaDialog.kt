package com.example.sumamente.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.sumamente.R

class ImagenAmpliadaDialog(
    context: Context,
    private val titulo: String,
    private val imagenResourceId: Int
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_imagen_ampliada)

        window?.setBackgroundDrawableResource(android.R.color.transparent)

        findViewById<TextView>(R.id.tv_titulo_imagen).text = titulo
        findViewById<ImageView>(R.id.img_ampliada).setImageResource(imagenResourceId)

        findViewById<Button>(R.id.btn_cerrar).setOnClickListener {
            dismiss()
        }
    }
}
