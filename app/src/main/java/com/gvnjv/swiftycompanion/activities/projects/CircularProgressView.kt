package com.gvnjv.swiftycompanion.activities.projects

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * Custom circular progress indicator view.
 *
 * Features:
 * - Draws a circular progress arc from 0 to 100% (and beyond).
 * - Changes arc color based on progress thresholds (red <80%, yellow 80–99%, green 100%+).
 * - Displays the numeric progress value in the center.
 * - Can show an extra "bonus" circle when progress exceeds 100%.
 * - Supports animated progress updates via ValueAnimator.
 */
class CircularProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /** Current progress value (can be >100 to trigger bonus circle) */
    var progress: Float = 0f
    private val maxProgress = 100f

    /** Paint for the base circle */
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    /** Paint for the bonus circle (when progress > 100%) */
    private val bonusCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.GREEN
    }

    /** Paint for the colored progress arc */
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
    }

    /** Paint for the center text displaying progress value */
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 20f * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    /** Reusable rect defining arc bounds */
    private val rectF = RectF()

    /** Circle values */
    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private val padding = 12f * resources.displayMetrics.density

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val size = min(w.toFloat(), h.toFloat())
        centerX = w / 2f
        centerY = h / 2f
        radius = (size / 2f) - padding
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
    }

    /**
     * Draws the circular progress indicator.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Base circle (light gray background)
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        // Bonus circle when progress exceeds maxProgress
        if (progress > maxProgress) {
            val bonusRadius = radius + 10f
            canvas.drawCircle(centerX, centerY, bonusRadius, bonusCirclePaint)
        }

        // Arc color logic based on thresholds
        arcPaint.color = when {
            progress >= 100f -> Color.GREEN
            progress >= 80f -> Color.YELLOW
            else -> Color.RED
        }

        // Calculate sweep angle (limit to maxProgress for arc length)
        val sweepAngle = (min(progress, maxProgress) / maxProgress) * 360f

        // Draw arc
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(rectF, -90f, sweepAngle, false, arcPaint)

        // Draw centered progress text
        val displayText = progress.toInt().toString()
        val textY = centerY - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(displayText, centerX, textY, textPaint)
    }

    /**
     * Smoothly animates progress from 0 to [targetProgress] over [duration] milliseconds.
     *
     * @param targetProgress The final progress value to animate to.
     * @param duration Duration of the animation in milliseconds (default 1000ms).
     */
    fun setProgressWithAnimation(targetProgress: Float, duration: Long = 1000L) {
        val animator = ValueAnimator.ofFloat(0f, targetProgress)
        animator.duration = duration
        animator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate() // Redraw on each animation frame
        }
        animator.start()
    }
}