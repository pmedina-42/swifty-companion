package com.gvnjv.swiftycompanion


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
import com.gvnjv.swiftycompanion.model.FullCursusInfo
import com.gvnjv.swiftycompanion.model.FullUserInfo


class UserActivity : AppCompatActivity() {

    private lateinit var userInfo: FullUserInfo
    private lateinit var currentCursus: FullCursusInfo
    private lateinit var pagerAdapter: UserPagerAdapter
    private lateinit var dropdown: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val gson = Gson()
        val userJson = intent.getStringExtra("userInfo")
        userInfo = gson.fromJson(userJson, FullUserInfo::class.java)

        var userImage = findViewById<ImageView>(R.id.userImage)
        Glide.with(this)
            .load(userInfo.image)
            .apply(RequestOptions().transform(RoundedCorners(100)))
            .placeholder(R.drawable.swifty_companion_logo)
            .error(R.drawable.swifty_companion_logo)
            .into(userImage)
        findViewById<TextView>(R.id.userLogin).text = userInfo.user.login
        findViewById<TextView>(R.id.userDisplayName).text = userInfo.user.displayname
        findViewById<TextView>(R.id.textWallet).text = String.format(userInfo.user.wallet.toString() + " ₳")

        dropdown = findViewById(R.id.cursusDropdown)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val sortedCursusList = userInfo.cursus.sortedBy { it.grade.lowercase()}

        val cursusNames = sortedCursusList.map { it.grade ?: "Unknown cursus" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cursusNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropdown.adapter = adapter

        currentCursus = sortedCursusList[0]
        var textLevel = findViewById<TextView>(R.id.textLevel)
        textLevel.text = String.format("Level " + currentCursus.level)
        pagerAdapter = UserPagerAdapter(this, currentCursus)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Proyectos" else "Skills"
        }.attach()

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                currentCursus = sortedCursusList[position]
                pagerAdapter.updateCursus(currentCursus)
                textLevel.text = String.format("Level " + currentCursus.level)
                viewPager.adapter = pagerAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
