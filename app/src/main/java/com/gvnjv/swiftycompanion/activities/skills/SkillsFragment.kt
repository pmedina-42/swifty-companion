package com.gvnjv.swiftycompanion.activities.skills

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.gvnjv.swiftycompanion.R
import com.gvnjv.swiftycompanion.model.SkillsResponse

/**
 * A Fragment that displays a radar chart representing a user's skills.
 * Uses MPAndroidChart's RadarChart to visualize skill names and their levels.
 */
class SkillsFragment : Fragment() {

    // The chart view for displaying the skills
    private lateinit var chart: RadarChart

    // List of skills to display (each with a name and level)
    private var skills: List<SkillsResponse> = emptyList()

    /**
     * Called when the fragment is created.
     * Retrieves the skills list from the fragment arguments and sorts them alphabetically.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        skills = arguments
            ?.getParcelableArrayList<SkillsResponse>(ARG_SKILLS)
            ?.sortedBy { it.name.lowercase() }
            ?: emptyList()
    }

    /**
     * Inflates the fragment's layout and initializes the radar chart.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_skills, container, false)
        chart = view.findViewById(R.id.radarChart)
        setupRadarChart()
        return view
    }

    /**
     * Configures the radar chart with skill data, appearance, and interaction settings.
     */
    private fun setupRadarChart() {
        // Convert skill levels to chart entries
        val entries = skills.map { RadarEntry(it.level) }
        // Extract skill names for the X-axis labels
        val labels = skills.map { it.name }

        // Create the dataset for the radar chart
        val dataSet = RadarDataSet(entries, "Skills").apply {
            color = Color.BLUE
            fillColor = Color.parseColor("#80C8FF") // Light blue fill
            setDrawFilled(true)
            isDrawHighlightCircleEnabled = true
            setDrawHighlightIndicators(false)
            lineWidth = 2f
            valueTextSize = 0f // Hide value labels
        }

        // Apply configuration to the chart
        chart.apply {
            data = RadarData(dataSet)
            description.isEnabled = false
            isRotationEnabled = false
            setTouchEnabled(true)
            setBackgroundColor(Color.TRANSPARENT)

            // Configure X-axis (skill names)
            xAxis.apply {
                setDrawLabels(false) // Labels are handled by custom marker
                valueFormatter = IndexAxisValueFormatter(labels)
                textSize = 14f
                textColor = Color.DKGRAY
            }

            // Configure Y-axis (skill levels)
            yAxis.apply {
                axisMinimum = 0f
                axisMaximum = 26f // Max level range
                setDrawLabels(false)
            }

            // Custom marker to show skill name & value on tap
            marker = SkillMarkerView(requireContext(), labels, R.layout.marker_view, chart)

            // Animation and final tweaks
            chart.animateXY(1000, 1000) // Animate both axes
            chart.legend.isEnabled = false
            chart.description.isEnabled = false
            invalidate() // Refresh chart
        }
    }

    companion object {
        private const val ARG_SKILLS = "skills"

        /**
         * Factory method to create a new instance of SkillsFragment with skills as arguments.
         * @param skills The list of skills to display in the radar chart.
         */
        fun newInstance(skills: ArrayList<SkillsResponse>): SkillsFragment {
            val fragment = SkillsFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_SKILLS, skills)
            fragment.arguments = bundle
            return fragment
        }
    }
}