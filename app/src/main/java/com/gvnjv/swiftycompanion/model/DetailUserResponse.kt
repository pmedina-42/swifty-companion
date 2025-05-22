package com.gvnjv.swiftycompanion.model

data class DetailUserResponse(
    val image: ImageResponse,
    val cursus_users: List<CursusResponse>,
    val projects_users: List<ProjectListResponse>
)
