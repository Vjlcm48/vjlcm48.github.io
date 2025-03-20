package com.example.sumamente.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt

class RadiusBackgroundSpan(
    @ColorInt private val backgroundColor: Int,
    @ColorInt private val textColor: Int,
    private val cornerRadius: Int
) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val width = paint.measureText(text, start, end)
        val rect = RectF(x, top.toFloat(), x + width, bottom.toFloat())

        val paintBackground = Paint(paint)
        paintBackground.color = backgroundColor
        canvas.drawRoundRect(rect, cornerRadius.toFloat(), cornerRadius.toFloat(), paintBackground)

        val paintText = Paint(paint)
        paintText.color = textColor
        canvas.drawText(text, start, end, x, y.toFloat(), paintText)
    }
}
