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
import com.gvnjv.swiftycompanion.model.ProjectListResponse

class ProjectsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var projects: List<ProjectListResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projects = arguments?.getParcelableArrayList(ARG_PROJECTS) ?: emptyList()
    }

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

    class ProjectAdapter(private val items: List<ProjectListResponse>) :
        RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

        class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.projectName)
            val markCircle: CircularProgressView = view.findViewById(R.id.projectMarkCircle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_project, parent, false)
            return ProjectViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
            val project = items[position]
            holder.name.text = project.project.name

            val mark = project.final_mark?.toFloat() ?: 0f
            holder.markCircle.setProgressWithAnimation(mark)
        }

        override fun getItemCount(): Int = items.size
    }

    companion object {
        private const val ARG_PROJECTS = "projects"

        fun newInstance(projects: ArrayList<ProjectListResponse>): ProjectsFragment {
            val fragment = ProjectsFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_PROJECTS, projects)
            fragment.arguments = bundle
            return fragment
        }
    }
}
