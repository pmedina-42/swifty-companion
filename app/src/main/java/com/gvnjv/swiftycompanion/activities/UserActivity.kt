package com.gvnjv.swiftycompanion.activities


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.gvnjv.swiftycompanion.R
import com.gvnjv.swiftycompanion.activities.adapter.UserPagerAdapter
import com.gvnjv.swiftycompanion.model.FullCursusInfo
import com.gvnjv.swiftycompanion.model.FullUserInfo


/**
 * UserActivity:
 * - Displays detailed information about a 42 user.
 * - Shows their profile picture, login, display name, wallet, grade, and level.
 * - Allows switching between different cursus (tracks) using a dropdown (Spinner).
 * - Uses a ViewPager2 + TabLayout to switch between:
 *   1. ProjectsFragment
 *   2. SkillsFragment
 */
class UserActivity : AppCompatActivity() {

    /** Full user information received from LoginActivity */
    private lateinit var userInfo: FullUserInfo

    /** Currently selected cursus (track) */
    private lateinit var currentCursus: FullCursusInfo

    /** ViewPager2 adapter for Projects and Skills tabs */
    private lateinit var pagerAdapter: UserPagerAdapter

    /** Dropdown for selecting cursus */
    private lateinit var dropdown: Spinner

    /** Gson instance for JSON parsing */
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Lock activity orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Retrieve user data passed from LoginActivity
        val userJson = intent.getStringExtra("userInfo")
        userInfo = gson.fromJson(userJson, FullUserInfo::class.java)

        // Load and display user profile image with rounded corners
        val userImage = findViewById<ImageView>(R.id.userImage)
        Glide.with(this)
            .load(userInfo.image)
            .apply(RequestOptions().transform(RoundedCorners(100)))
            .placeholder(R.drawable.swifty_companion_logo)
            .error(R.drawable.swifty_companion_logo)
            .into(userImage)

        // Set basic user details
        findViewById<TextView>(R.id.userLogin).text = userInfo.user.login
        findViewById<TextView>(R.id.userDisplayName).text = userInfo.user.displayname
        findViewById<TextView>(R.id.textWallet).text =
            String.format("%s ₳", userInfo.user.wallet.toString())

        // UI elements for cursus selection and tab navigation
        dropdown = findViewById(R.id.cursusDropdown)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // Sort cursus list alphabetically by name
        val sortedCursusList = userInfo.cursus.sortedBy { it.name.lowercase() }

        // Create a list of cursus names for the dropdown
        val cursusNames = sortedCursusList.map { it.name ?: "Unknown cursus" }

        // Configure the Spinner adapter
        val adapter = ArrayAdapter(this, R.layout.spinner_item, cursusNames)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        dropdown.adapter = adapter

        // Initialize with the first cursus in the sorted list
        currentCursus = sortedCursusList[0]
        findViewById<TextView>(R.id.userGrade).text = currentCursus.grade
        val textLevel = findViewById<TextView>(R.id.textLevel)
        textLevel.text = String.format("Level %s", currentCursus.level)

        // Set up ViewPager2 with the current cursus
        pagerAdapter = UserPagerAdapter(this, currentCursus)
        viewPager.adapter = pagerAdapter

        // Link TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Projects" else "Skills"
        }.attach()

        // Handle cursus selection changes
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Update current cursus and refresh displayed data
                currentCursus = sortedCursusList[position]
                findViewById<TextView>(R.id.userGrade).text = currentCursus.grade
                pagerAdapter.updateCursus(currentCursus)
                textLevel.text = String.format("Level %s", currentCursus.level)
                // Re-attach adapter to refresh fragments
                viewPager.adapter = pagerAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }
    }
}