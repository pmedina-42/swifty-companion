package com.gvnjv.swiftycompanion.model

data class CursusResponse(
    val level: String,
    val grade: String,
    val skills: List<SkillsResponse>,
    val cursus_id: Int,
    val cursus: CursusNameResponse
)