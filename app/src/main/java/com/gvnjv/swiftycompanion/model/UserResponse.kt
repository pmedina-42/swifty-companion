package com.gvnjv.swiftycompanion.model

data class UserResponse(
    val id: Int,
    val login: String,
    val displayname: String,
    val correction_point: Int,
    val pool_month: String,
    val pool_year: Int,
    val wallet: Int
)