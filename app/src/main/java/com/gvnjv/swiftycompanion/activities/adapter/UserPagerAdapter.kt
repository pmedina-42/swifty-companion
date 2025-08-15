package com.gvnjv.swiftycompanion.activities.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gvnjv.swiftycompanion.activities.skills.SkillsFragment
import com.gvnjv.swiftycompanion.model.FullCursusInfo
import com.gvnjv.swiftycompanion.ui.ProjectsFragment

class UserPagerAdapter(
    activity: FragmentActivity,
    private var cursus: FullCursusInfo
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProjectsFragment.newInstance(ArrayList(cursus.projects))
            1 -> SkillsFragment.newInstance(ArrayList(cursus.skills))
            else -> throw IndexOutOfBoundsException("Invalid tab index")
        }
    }

    fun updateCursus(newCursus: FullCursusInfo) {
        cursus = newCursus
        notifyDataSetChanged()
    }
}
