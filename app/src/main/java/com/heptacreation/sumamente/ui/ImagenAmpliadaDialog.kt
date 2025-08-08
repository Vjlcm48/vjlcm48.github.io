package com.heptacreation.sumamente.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.heptacreation.sumamente.R
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

        window?.let {

            it.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())


            val displayMetrics = context.resources.displayMetrics

            val width = (displayMetrics.widthPixels * 0.90).toInt()

            val height = (displayMetrics.heightPixels * 0.95).toInt()

            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it.attributes)
            layoutParams.width = width
            layoutParams.height = height
            it.attributes = layoutParams
        }

        findViewById<TextView>(R.id.tv_titulo_imagen).text = titulo
        val photoView = findViewById<PhotoView>(R.id.img_ampliada)
        photoView.setImageResource(imagenResourceId)


        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_cerrar).setOnClickListener {
            dismiss()
        }
    }
}