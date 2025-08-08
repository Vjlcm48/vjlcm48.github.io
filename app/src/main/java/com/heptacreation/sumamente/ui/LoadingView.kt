package com.heptacreation.sumamente.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.heptacreation.sumamente.R

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val colors = intArrayOf(
        ResourcesCompat.getColor(resources, R.color.blue_primary, null),
        ResourcesCompat.getColor(resources, R.color.cyan_dark, null),
        ResourcesCompat.getColor(resources, R.color.green_accent, null),
        ResourcesCompat.getColor(resources, R.color.green_light, null),
        ResourcesCompat.getColor(resources, R.color.green_medium, null),
        ResourcesCompat.getColor(resources, R.color.green_dark, null),
        ResourcesCompat.getColor(resources, R.color.yellow, null),
        ResourcesCompat.getColor(resources, R.color.yellow_dark, null),
        ResourcesCompat.getColor(resources, R.color.orange, null),
        ResourcesCompat.getColor(resources, R.color.orange_dark, null),
        ResourcesCompat.getColor(resources, R.color.red_primary, null),
        ResourcesCompat.getColor(resources, R.color.red_secondary, null),
        ResourcesCompat.getColor(resources, R.color.teal_200, null),
        ResourcesCompat.getColor(resources, R.color.blue_secondary, null),
        ResourcesCompat.getColor(resources, R.color.blue_light, null),
        ResourcesCompat.getColor(resources, R.color.cyan, null)
     )

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 37f
    }

    private val rectF = RectF()
    private var progress = 0f
    private var gradient: SweepGradient? = null

    init {
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.duration = 4500
        animator.repeatCount = 0
        animator.addUpdateListener { animation ->
            progress = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = paint.strokeWidth / 2
        rectF.set(padding, padding, w - padding, h - padding)
        gradient = SweepGradient(width / 2f, height / 2f, colors, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startAngle = -90f
        val sweepAngle = progress

        paint.shader = gradient
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)

        if (progress >= 360f) {
            canvas.drawArc(rectF, startAngle, 360f, false, paint)
        }
    }
}
