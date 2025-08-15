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

/**
 * Custom marker view for displaying skill name and level
 * when the user taps a point on the RadarChart.
 *
 * Extends MPAndroidChart's MarkerView to create a floating
 * label that shows contextual information about the selected entry.
 */
class SkillMarkerView(
    context: Context,
    private val labels: List<String>,   // Skill names in order of the chart entries
    layoutResource: Int,                // Layout resource for the marker view UI
    private val chart: RadarChart       // The RadarChart this marker is attached to
) : MarkerView(context, layoutResource) {

    // TextView in the marker layout where skill name & level will be shown
    private val textView: TextView = findViewById(R.id.markerText)

    /**
     * Called every time the marker is redrawn after a highlight.
     * Updates the marker text with the skill name and formatted level.
     *
     * @param e The chart entry being highlighted
     * @param highlight The highlight object containing touch position and index
     */
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val index = highlight?.x?.toInt() ?: 0
        val name = labels.getOrNull(index) ?: "Unknown"
        val level = e?.y ?: 0f
        // Display "SkillName: 12.34" with two decimal places
        textView.text = "$name: ${"%.2f".format(level)}"

        super.refreshContent(e, highlight)
    }

    /**
     * Calculates the offset so the marker appears above the touched point
     * without going off-screen horizontally.
     *
     * @param posX The X coordinate of the highlighted entry
     * @param posY The Y coordinate of the highlighted entry
     * @return MPPointF with the adjusted offset values
     */
    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        val marginDp = 10f
        // Convert margin from dp to pixels
        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            marginDp,
            context.resources.displayMetrics
        )

        // Ensure marker view is measured before calculating offset
        if (width == 0 || height == 0) {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        }

        // Center horizontally over the point
        var offsetX = -width / 2f
        // Place above the point with some margin
        val offsetY = -height.toFloat() - marginPx

        val finalX = posX + offsetX

        // Adjust offset if the marker would go off the left edge
        if (finalX < marginPx) {
            offsetX = marginPx - posX
        }
        // Adjust offset if the marker would go off the right edge
        else if (finalX + width > chart.width - marginPx) {
            offsetX = chart.width - marginPx - posX - width
        }

        return MPPointF(offsetX, offsetY)
    }
}