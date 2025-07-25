package com.example.sumamente.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.example.sumamente.R


class ResponseModeDialogAlfaNumerosPro(context: Context) : AppCompatDialog(context) {

    private var listener: OnResponseModeSelectedListenerAlfaNumerosPro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.response_mode_dialog_alfanumeros)


        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        window?.attributes = WindowManager.LayoutParams().apply {
            copyFrom(window?.attributes)
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        adjustElements()

        val btnSeleccionSimple      = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_seleccion_simple)
        val btnEscribeRespuesta     = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_escribe_respuesta)
        val checkSeleccionSimple    = findViewById<ImageView>(R.id.check_seleccion_simple)
        val checkEscribeRespuesta   = findViewById<ImageView>(R.id.check_escribe_respuesta)
        val infoSeleccionSimple     = findViewById<ImageButton>(R.id.info_seleccion_simple)
        val infoEscribeRespuesta    = findViewById<ImageButton>(R.id.info_escribe_respuesta)


        val buttonBackground = GradientDrawable().apply {
            setColor(ContextCompat.getColor(context, R.color.blue_light))
            cornerRadius = 30f
            setStroke(2, Color.BLACK)
        }
        btnSeleccionSimple?.background  = buttonBackground
        btnEscribeRespuesta?.background = buttonBackground
        btnSeleccionSimple?.elevation   = 8f
        btnEscribeRespuesta?.elevation  = 8f
        btnSeleccionSimple?.setPadding(50, 20, 50, 20)
        btnEscribeRespuesta?.setPadding(50, 20, 50, 20)


        btnSeleccionSimple?.setOnClickListener {
            animateCheck(checkSeleccionSimple!!)
            checkEscribeRespuesta?.alpha = 0f
            Handler(Looper.getMainLooper()).postDelayed({
                sendResponseModeToCaller(ResponseModeAlfaNumeros.SIMPLE_SELECTION)
            }, 1000)
        }


        btnEscribeRespuesta?.setOnClickListener {
            animateCheck(checkEscribeRespuesta!!)
            checkSeleccionSimple?.alpha = 0f
            Handler(Looper.getMainLooper()).postDelayed({
                sendResponseModeToCaller(ResponseModeAlfaNumeros.TYPE_ANSWER)
            }, 1000)
        }


        infoSeleccionSimple?.setOnClickListener {
            showTooltip(it, R.string.selection_simple_title, R.string.selection_simple_message)
        }
        infoEscribeRespuesta?.setOnClickListener {
            showTooltip(it, R.string.write_answer_title, R.string.write_answer_message)
        }
    }


    private fun animateCheck(view: ImageView) {
        view.apply {
            scaleX = 0f
            scaleY = 0f
            alpha  = 0f
            visibility = View.VISIBLE
        }
        val animatorX   = ObjectAnimator.ofFloat(view, "scaleX", 1f).setDuration(600)
        val animatorY   = ObjectAnimator.ofFloat(view, "scaleY", 1f).setDuration(600)
        val animatorA   = ObjectAnimator.ofFloat(view, "alpha" , 1f).setDuration(600)
        listOf(animatorX, animatorY, animatorA).forEach {
            it.interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply { playTogether(animatorX, animatorY, animatorA); start() }
    }


    private fun sendResponseModeToCaller(mode: ResponseModeAlfaNumeros) {
        listener?.onResponseModeSelected(mode)
        dismiss()
    }


    private fun showTooltip(anchor: View, titleRes: Int, messageRes: Int) {
        val popupView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_tooltip, anchor.parent as ViewGroup, false)
        popupView.findViewById<TextView>(R.id.dialog_title ).text = context.getString(titleRes)
        popupView.findViewById<TextView>(R.id.dialog_message).text = context.getString(messageRes)

        val pop = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        popupView.findViewById<ImageView>(R.id.close_button).setOnClickListener { pop.dismiss() }
        pop.showAsDropDown(anchor, 0, -anchor.height)
    }


    private fun adjustElements() {
        findViewById<TextView>(R.id.dialog_title)?.apply {
            textSize = 20f
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    fun setOnResponseModeSelectedListener(listener: OnResponseModeSelectedListenerAlfaNumerosPro) {
        this.listener = listener
    }

    interface OnResponseModeSelectedListenerAlfaNumerosPro {
        fun onResponseModeSelected(mode: ResponseModeAlfaNumeros)
    }
}
