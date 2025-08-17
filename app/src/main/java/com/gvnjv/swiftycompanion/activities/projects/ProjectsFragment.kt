package com.gvnjv.swiftycompanion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gvnjv.swiftycompanion.R
import com.gvnjv.swiftycompanion.activities.projects.CircularProgressView
import com.gvnjv.swiftycompanion.model.ProjectListResponse

/**
 * Fragment that displays a scrollable list of projects with their names and marks.
 *
 * Each project is shown in a RecyclerView item containing:
 *  - Project name (TextView)
 *  - Circular progress indicator for the final mark (CircularProgressView)
 */
class ProjectsFragment : Fragment() {

    /** RecyclerView for displaying the list of projects */
    private lateinit var recyclerView: RecyclerView

    /** List of projects to display (passed via arguments) */
    private var projects: List<ProjectListResponse> = emptyList()

    /**
     * Retrieves the project list from the fragment arguments.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projects = arguments?.getParcelableArrayList(ARG_PROJECTS) ?: emptyList()
    }

    /**
     * Inflates the layout, initializes the RecyclerView, and sets its adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_projects, container, false)
        recyclerView = view.findViewById(R.id.projectList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ProjectAdapter(projects)
        return view
    }

    /**
     * RecyclerView adapter for displaying project items.
     */
    class ProjectAdapter(private val items: List<ProjectListResponse>) :
        RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

        /**
         * ViewHolder holding references to views in a project item.
         */
        class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.projectName)
            val markCircle: CircularProgressView = view.findViewById(R.id.projectMarkCircle)
        }

        /**
         * Inflates the layout for a project list item.
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_project, parent, false)
            return ProjectViewHolder(view)
        }

        /**
         * Binds project data (name and mark) to the views.
         */
        override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
            val project = items[position]
            holder.name.text = project.project.name

            // Convert final mark to Float, default to 0 if null, and animate
            val mark = project.final_mark?.toFloat() ?: 0f
            holder.markCircle.setProgressWithAnimation(mark)
        }

        /** Returns the number of items in the list */
        override fun getItemCount(): Int = items.size
    }

    companion object {
        private const val ARG_PROJECTS = "projects"

        /**
         * Factory method to create a new instance of ProjectsFragment
         * with the given list of projects as arguments.
         */
        fun newInstance(projects: ArrayList<ProjectListResponse>): ProjectsFragment {
            val fragment = ProjectsFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_PROJECTS, projects)
            fragment.arguments = bundle
            return fragment
        }
    }
}