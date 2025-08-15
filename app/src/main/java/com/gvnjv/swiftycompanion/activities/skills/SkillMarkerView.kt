package com.gvnjv.swiftycompanion.activities.skills

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.gvnjv.swiftycompanion.R

class SkillMarkerView(
    context: Context,
    private val labels: List<String>,
    layoutResource: Int,
    private val chart: RadarChart
) : MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.markerText)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val index = highlight?.x?.toInt() ?: 0
        val name = labels.getOrNull(index) ?: "Unknown"
        val level = e?.y ?: 0f
        textView.text = "$name: ${"%.2f".format(level)}"
        super.refreshContent(e, highlight)
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        val marginDp = 10f
        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            marginDp,
            context.resources.displayMetrics
        )

        if (width == 0 || height == 0) {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        }

        var offsetX = -width / 2f
        val offsetY = -height.toFloat() - marginPx

        val finalX = posX + offsetX

        if (finalX < marginPx) {
            offsetX = marginPx - posX
        }
        else if (finalX + width > chart.width - marginPx) {
            offsetX = chart.width - marginPx - posX - width
        }

        return MPPointF(offsetX, offsetY)
    }

}