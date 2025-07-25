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

class ResponseModeDialogDeciPlusPro(context: Context) : AppCompatDialog(context) {

    private var listener: OnResponseModeSelectedListenerDeciPlusPro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.response_mode_dialog_deci_plus)

        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        adjustElements()

        val btnSeleccionSimple = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_seleccion_simple)
        val btnEscribeRespuesta = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_escribe_respuesta)
        val checkSeleccionSimple = findViewById<ImageView>(R.id.check_seleccion_simple)
        val checkEscribeRespuesta = findViewById<ImageView>(R.id.check_escribe_respuesta)
        val infoSeleccionSimple = findViewById<ImageButton>(R.id.info_seleccion_simple)
        val infoEscribeRespuesta = findViewById<ImageButton>(R.id.info_escribe_respuesta)

        val buttonBackground = GradientDrawable().apply {
            setColor(ContextCompat.getColor(context, R.color.orange_dark))
            cornerRadius = 30f
            setStroke(2, Color.BLACK)
        }

        btnSeleccionSimple?.background = buttonBackground
        btnEscribeRespuesta?.background = buttonBackground

        btnSeleccionSimple?.elevation = 8f
        btnEscribeRespuesta?.elevation = 8f

        btnSeleccionSimple?.setPadding(50, 20, 50, 20)
        btnEscribeRespuesta?.setPadding(50, 20, 50, 20)

        btnSeleccionSimple?.setOnClickListener {
            animateCheck(checkSeleccionSimple!!)
            checkEscribeRespuesta?.alpha = 0f
            Handler(Looper.getMainLooper()).postDelayed({
                sendResponseModeToCaller(ResponseModeDeciPlus.SIMPLE_SELECTION)
            }, 1000)
        }

        btnEscribeRespuesta?.setOnClickListener {
            animateCheck(checkEscribeRespuesta!!)
            checkSeleccionSimple?.alpha = 0f
            Handler(Looper.getMainLooper()).postDelayed({
                sendResponseModeToCaller(ResponseModeDeciPlus.TYPE_ANSWER)
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
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f
        view.visibility = View.VISIBLE

        val animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(animatorX, animatorY, animatorAlpha)
            start()
        }
    }

    private fun sendResponseModeToCaller(mode: ResponseModeDeciPlus) {
        listener?.onResponseModeSelected(mode)
        dismiss()
    }

    private fun showTooltip(anchorView: View, titleResId: Int, messageResId: Int) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.dialog_tooltip, anchorView.parent as ViewGroup, false)

        val titleTextView = popupView.findViewById<TextView>(R.id.dialog_title)
        val messageTextView = popupView.findViewById<TextView>(R.id.dialog_message)
        titleTextView.text = context.getString(titleResId)
        messageTextView.text = context.getString(messageResId)

        val popupWindow = PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)

        val closeButton = popupView.findViewById<ImageView>(R.id.close_button)
        closeButton.setOnClickListener { popupWindow.dismiss() }

        popupWindow.showAsDropDown(anchorView, 0, -anchorView.height)
    }

    private fun adjustElements() {
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        dialogTitle?.textSize = 20f
        dialogTitle?.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    fun setOnResponseModeSelectedListener(listener: OnResponseModeSelectedListenerDeciPlusPro) {
        this.listener = listener
    }

    interface OnResponseModeSelectedListenerDeciPlusPro {
        fun onResponseModeSelected(mode: ResponseModeDeciPlus)
    }
}
