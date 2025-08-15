package com.gvnjv.swiftycompanion.activities.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gvnjv.swiftycompanion.activities.skills.SkillsFragment
import com.gvnjv.swiftycompanion.model.FullCursusInfo
import com.gvnjv.swiftycompanion.ui.ProjectsFragment

/**
 * Adapter for a ViewPager2 that displays two tabs:
 *  1. A list of projects (ProjectsFragment)
 *  2. A list of skills (SkillsFragment)
 *
 * The adapter is initialized with a [FullCursusInfo] object, which contains
 * both the list of projects and the list of skills for a user.
 */
class UserPagerAdapter(
    activity: FragmentActivity,
    private var cursus: FullCursusInfo
) : FragmentStateAdapter(activity) {

    /** Number of tabs in the pager (Projects + Skills) */
    override fun getItemCount(): Int = 2

    /**
     * Creates the fragment for the given tab position.
     *
     * @param position Tab index:
     *  - 0 → [ProjectsFragment] with the user's projects
     *  - 1 → [SkillsFragment] with the user's skills
     * @return A new fragment instance for the selected tab.
     * @throws IndexOutOfBoundsException if the position is invalid.
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProjectsFragment.newInstance(ArrayList(cursus.projects))
            1 -> SkillsFragment.newInstance(ArrayList(cursus.skills))
            else -> throw IndexOutOfBoundsException("Invalid tab index")
        }
    }

    /**
     * Updates the adapter's data with a new [FullCursusInfo] and refreshes the UI.
     *
     * @param newCursus The new cursus data to display in both tabs.
     */
    fun updateCursus(newCursus: FullCursusInfo) {
        cursus = newCursus
        notifyDataSetChanged()
    }
}