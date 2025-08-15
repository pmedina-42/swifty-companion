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

class SkillsFragment : Fragment() {

    private lateinit var chart: RadarChart
    private var skills: List<SkillsResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        skills = arguments
            ?.getParcelableArrayList<SkillsResponse>(ARG_SKILLS)
            ?.sortedBy { it.name.lowercase() }
            ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_skills, container, false)
        chart = view.findViewById(R.id.radarChart)
        setupRadarChart()
        return view
    }

    private fun setupRadarChart() {
        val entries = skills.map { RadarEntry(it.level) }
        val labels = skills.map { it.name }

        val dataSet = RadarDataSet(entries, "Skills").apply {
            color = Color.BLUE
            fillColor = Color.parseColor("#80C8FF")
            setDrawFilled(true)
            setDrawHighlightCircleEnabled(true)
            setDrawHighlightIndicators(false)
            lineWidth = 2f
            valueTextSize = 0f
        }

        chart.apply {
            data = RadarData(dataSet)
            description.isEnabled = false
            isRotationEnabled = false
            setTouchEnabled(true)
            setBackgroundColor(Color.TRANSPARENT)

            xAxis.apply {
                setDrawLabels(false)
                valueFormatter = IndexAxisValueFormatter(labels)
                textSize = 14f
                textColor = Color.DKGRAY
            }

            yAxis.apply {
                axisMinimum = 0f
                axisMaximum = 21f
                setDrawLabels(false)
            }

            marker = SkillMarkerView(requireContext(), labels, R.layout.marker_view, chart)
            chart.animateXY(1000, 1000)
            chart.legend.isEnabled = false
            chart.description.isEnabled = false
            invalidate()
        }
    }


    companion object {
        private const val ARG_SKILLS = "skills"

        fun newInstance(skills: ArrayList<SkillsResponse>): SkillsFragment {
            val fragment = SkillsFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_SKILLS, skills)
            fragment.arguments = bundle
            return fragment
        }
    }
}
