package com.gvnjv.swiftycompanion.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gvnjv.swiftycompanion.model.AccessTokenResponse
import com.gvnjv.swiftycompanion.model.DetailUserResponse
import com.gvnjv.swiftycompanion.model.FullCursusInfo
import com.gvnjv.swiftycompanion.model.FullUserInfo
import com.gvnjv.swiftycompanion.model.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class ApiService(
    private val gson: Gson,
    private val clientId: String,
    private val clientSecret: String,
    private val baseUrl: String = "https://api.intra.42.fr"
) {

    private val client = OkHttpClient()
    suspend fun requestAccessToken(): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val formBody = FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build()

            val request = Request.Builder()
                .url("$baseUrl/oauth/token")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) error("Token request failed: HTTP ${response.code}")
                val body = response.body?.string() ?: error("Empty token response")
                val parsed = gson.fromJson(body, AccessTokenResponse::class.java)
                parsed.access_token
            }
        }
    }

    suspend fun getUserInfo(login: String, accessToken: String): Result<FullUserInfo?> =
        withContext(Dispatchers.IO) {
            runCatching {
                // 1) Search users by login
                val searchReq = Request.Builder()
                    .url("$baseUrl/v2/users?filter[login]=$login")
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()

                client.newCall(searchReq).execute().use { searchResp ->
                    if (searchResp.code != 200) error("User search failed: HTTP ${searchResp.code}")
                    val body = searchResp.body?.string() ?: return@use null
                    val listType = object : TypeToken<List<UserResponse>>() {}.type
                    val users: List<UserResponse> = gson.fromJson(body, listType) ?: emptyList()
                    if (users.size != 1) return@use null

                    // 2) Fetch detail for that user
                    val userId = users.first().id
                    val detailReq = Request.Builder()
                        .url("$baseUrl/v2/users/$userId")
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()

                    client.newCall(detailReq).execute().use { detailResp ->
                        if (detailResp.code != 200) error("User detail failed: HTTP ${detailResp.code}")
                        val detailBody = detailResp.body?.string() ?: return@use null
                        val detail: DetailUserResponse =
                            gson.fromJson(detailBody, DetailUserResponse::class.java)

                        val fullCursusInfoList = detail.cursus_users.mapNotNull { cursus ->
                            if (cursus.level == "0.0") return@mapNotNull null
                            val projects = detail.projects_users.filter { p ->
                                p.cursus_ids.contains(cursus.cursus_id)
                            }
                            FullCursusInfo(
                                level = cursus.level,
                                name = cursus.cursus.name,
                                grade = cursus.grade ?: "Unknown grade",
                                skills = cursus.skills,
                                projects = projects
                            )
                        }.toMutableList()

                        FullUserInfo(
                            cursus = fullCursusInfoList,
                            user = users.first(),
                            image = detail.image.versions.small
                        )
                    }
                }
            }
        }
}