package com.gvnjv.swiftycompanion.model

data class FullUserInfo(
    val cursus: List<FullCursusInfo>,
    val user: UserResponse,
    val image: String
)
