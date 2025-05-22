package com.gvnjv.swiftycompanion.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircularProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var progress: Float = 0f  // 0 a 100+
    private val maxProgress = 100f

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.GREEN
    }

    private val bonusCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.GREEN
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
        color = Color.GREEN
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 20f * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val rectF = RectF()
    private val padding = 12f * resources.displayMetrics.density

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width.toFloat(), height.toFloat())
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (size / 2f) - padding

        // Círculo base gris claro
        circlePaint.color = Color.LTGRAY
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        // Círculo bonus si aplica
        if (progress > maxProgress) {
            val bonusRadius = radius + 10f
            canvas.drawCircle(centerX, centerY, bonusRadius, bonusCirclePaint)
        }

        // Color del arco
        arcPaint.color = when {
            progress >= 100f -> Color.GREEN
            progress >= 80f -> Color.YELLOW
            else -> Color.RED
        }

        val sweepAngle = (min(progress, maxProgress) / maxProgress) * 360f
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(rectF, -90f, sweepAngle, false, arcPaint)

        // Dibuja el número centrado, sin animación
        val displayText = progress.toInt().toString()
        val textY = centerY - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(displayText, centerX, textY, textPaint)
    }


    fun setProgressWithAnimation(targetProgress: Float, duration: Long = 1000L) {
        val animator = ValueAnimator.ofFloat(0f, targetProgress)
        animator.duration = duration
        animator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

}
