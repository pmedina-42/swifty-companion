package com.gvnjv.swiftycompanion

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gvnjv.swiftycompanion.model.AccessTokenResponse
import com.gvnjv.swiftycompanion.model.DetailUserResponse
import com.gvnjv.swiftycompanion.model.FullCursusInfo
import com.gvnjv.swiftycompanion.model.FullUserInfo
import com.gvnjv.swiftycompanion.model.UserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class LoginActivity : ComponentActivity() {

    private val client = OkHttpClient()

    private var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val inputText = findViewById<EditText>(R.id.inputLogin)
        val typeface = ResourcesCompat.getFont(this, R.font.baloo_bhai)
        inputText.typeface = typeface
        val submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.isEnabled = false
        submitButton.typeface = typeface
        requestAccessToken()

        submitButton.setOnClickListener {
            val login = inputText.text.toString()
            makeApiRequest(login)
        }
    }

    fun requestAccessToken() {
        val clientId = BuildConfig.client_uid
        val clientSecret = BuildConfig.client_secret
        val formBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url("https://api.intra.42.fr/oauth/token")
            .post(formBody)
            .build()

        // Perform the network request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Access token retrieved successfully
                    val body = response.body?.string()
                    val gson = Gson()
                    val accessTokenResponse = gson.fromJson(body, AccessTokenResponse::class.java)
                    // Optionally, extract the access token from the response body (JSON parsing could be done here)
                    accessToken = accessTokenResponse.access_token
                    // Run on the UI thread to enable the button
                    runOnUiThread {
                        val submitButton = findViewById<Button>(R.id.submitButton)
                        submitButton.isEnabled = true  // Enable the button once the token is loaded
                    }
                } else {
                    println("Failed: ${response.code}")
                }
            }
        })
    }

    private fun makeApiRequest(login: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = Request.Builder()
                .url("https://api.intra.42.fr/v2/users?filter[login]=$login")
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

            val response = client.newCall(request).execute()
            if (response.code == 200) {
                // Switch to new activity on main thread
                var userInfo: FullUserInfo? = null
                val body = response.body?.string()
                val gson = Gson()
                val listType = object : TypeToken<List<UserResponse>>() {}.type
                val userResponseList: List<UserResponse>? = gson.fromJson(body, listType)
                if (userResponseList != null && userResponseList.size == 1) {
                    val detailRequest = Request.Builder()
                        .url("https://api.intra.42.fr/v2/users/${userResponseList[0].id}")
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                    val detailResponse = client.newCall(detailRequest).execute()
                    val detailBody = detailResponse.body?.string()
                    val cursusResponse = gson.fromJson(detailBody, DetailUserResponse::class.java)
                    var fullCursusInfoList: MutableList<FullCursusInfo> = mutableListOf()
                    for (cursus in cursusResponse.cursus_users) {
                        if (cursus.level.equals("0.0")) {
                            continue
                        }
                        val cursusProjects = cursusResponse.projects_users.filter { p ->
                            p.cursus_ids.contains(cursus.cursus_id)
                        }
                        var fullCursusInfo = FullCursusInfo(
                            level = cursus.level,
                            grade = cursus.grade ?: "Unknown cursus",
                            skills = cursus.skills,
                            projects = cursusProjects
                        )
                        fullCursusInfoList.add(fullCursusInfo)
                    }
                    userInfo = FullUserInfo(
                        cursus = fullCursusInfoList,
                        user = userResponseList[0],
                        image = cursusResponse.image.versions.small
                    )
                    userInfo.toString()
                }
                runOnUiThread {
                    if (userInfo != null) {
                        if (userInfo.cursus.size >= 1) {
                            val intent = Intent(this@LoginActivity, UserActivity::class.java)
                            val gson = Gson()
                            val userJson = gson.toJson(userInfo)

                            intent.putExtra("userInfo", userJson)
                            startActivity(intent)
                        } else {
                            AlertDialog.Builder(this@LoginActivity)
                                .setTitle("🪹")
                                .setMessage("The user was found but doesn't belong to a valid cursus, so no information can be shown for them.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    } else {
                        AlertDialog.Builder(this@LoginActivity)
                            .setTitle("🤔")
                            .setMessage("No user was found with that login. Are you sure it's a valid one?")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            } else {
                AlertDialog.Builder(this@LoginActivity)
                    .setTitle("\uD83D\uDCA5")
                    .setMessage("The 42 API access token couldn't be retrieved with the secrets used." +
                            "Make sure you're using the latest ones. The app will close so you can check it")
                    .setPositiveButton("OK") { _, _ ->
                        finishAffinity()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}