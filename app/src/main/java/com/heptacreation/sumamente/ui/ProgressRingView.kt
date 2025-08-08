package com.heptacreation.sumamente.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.heptacreation.sumamente.R

class ProgressRingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = ResourcesCompat.getColor(resources, android.R.color.darker_gray, null)
    }

    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = ResourcesCompat.getColor(resources, R.color.red_primary, null)
    }

    private val rectF = RectF()
    private var progress = 0f

    fun startProgressAnimation(duration: Long) {
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.duration = duration
        animator.interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            progress = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = progressPaint.strokeWidth / 2
        rectF.set(padding, padding, w - padding, h - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startAngle = -90f

        canvas.drawArc(rectF, 0f, 360f, false, backgroundPaint)
        canvas.drawArc(rectF, startAngle, progress, false, progressPaint)
    }
}
