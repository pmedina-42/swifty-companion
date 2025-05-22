package com.gvnjv.swiftycompanion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.res.ResourcesCompat


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val createdBy = findViewById<TextView>(R.id.createdBy)
        val appTitle = findViewById<TextView>(R.id.appTitle)
        val typeface = ResourcesCompat.getFont(this, R.font.baloo_bhai)
        createdBy.typeface = typeface
        appTitle.typeface = typeface
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

}