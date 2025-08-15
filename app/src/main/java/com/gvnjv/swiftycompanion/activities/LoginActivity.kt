package com.gvnjv.swiftycompanion.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.gvnjv.swiftycompanion.BuildConfig
import com.gvnjv.swiftycompanion.R
import com.gvnjv.swiftycompanion.service.ApiService
import com.gvnjv.swiftycompanion.model.FullUserInfo
import kotlinx.coroutines.launch

/**
 * LoginActivity:
 * - Allows the user to search for a 42 user by login name.
 * - Requests an API access token on startup.
 * - Once the token is ready, enables the search button.
 * - Fetches user info via ApiService and navigates to UserActivity if valid data is found.
 */
class LoginActivity : ComponentActivity() {

    /** Gson instance for JSON serialization/deserialization */
    private val gson = Gson()

    /** API service for making network requests */
    private lateinit var api: ApiService

    /** Cached API access token */
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Force portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize ApiService with client credentials
        api = ApiService(
            gson = gson,
            clientId = BuildConfig.client_uid,
            clientSecret = BuildConfig.client_secret
        )

        // UI setup
        val inputText = findViewById<EditText>(R.id.inputLogin)
        var typeface = ResourcesCompat.getFont(this, R.font.baloo_bhai)
        inputText.typeface = typeface

        val submitButton = findViewById<Button>(R.id.submitButton).apply {
            isEnabled = false // Disabled until token is fetched
            typeface = typeface
        }

        // Request access token when activity starts
        lifecycleScope.launch {
            api.requestAccessToken()
                .onSuccess { token ->
                    accessToken = token
                    submitButton.isEnabled = true
                }
                .onFailure { e ->
                    Log.e("LoginActivity", "Token error", e)
                    showBlockingErrorAndClose()
                }
        }

        // Handle search button click
        submitButton.setOnClickListener {
            val login = inputText.text.toString().trim()
            val token = accessToken
            if (token.isNullOrEmpty()) return@setOnClickListener

            submitButton.isEnabled = false

            lifecycleScope.launch {
                api.getUserInfo(login, token)
                    .onSuccess { userInfo ->
                        handleUserInfo(userInfo)
                    }
                    .onFailure { e ->
                        Log.e("LoginActivity", "User request failed", e)
                        showInfoDialog(
                            "🤔",
                            "No user was found with that login. Are you sure it's a valid one?"
                        )
                    }

                submitButton.isEnabled = true
            }
        }
    }

    /**
     * Handles what to do when user info is retrieved.
     * Navigates to UserActivity if valid cursus data is present,
     * otherwise shows an informational dialog.
     */
    private fun handleUserInfo(userInfo: FullUserInfo?) {
        if (userInfo == null) {
            showInfoDialog("🤔", "No user was found with that login. Are you sure it's a valid one?")
            return
        }

        if (userInfo.cursus.size >= 1) {
            // Pass user data to UserActivity
            val intent = Intent(this@LoginActivity, UserActivity::class.java)
            val userJson = gson.toJson(userInfo)
            intent.putExtra("userInfo", userJson)
            startActivity(intent)
        } else {
            showInfoDialog(
                "🪹",
                "The user was found but doesn't belong to a valid cursus, so no information can be shown for them."
            )
        }
    }

    /**
     * Shows a simple OK dialog with a title and message.
     */
    private fun showInfoDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Shows an error dialog when the token cannot be retrieved,
     * and closes the app when the user presses OK.
     */
    private fun showBlockingErrorAndClose() {
        AlertDialog.Builder(this)
            .setTitle("💥")
            .setMessage(
                "The 42 API access token couldn't be retrieved with the secrets used. " +
                        "Make sure you're using the latest ones. The app will close so you can check it"
            )
            .setPositiveButton("OK") { _, _ -> finishAffinity() }
            .setCancelable(false)
            .show()
    }
}