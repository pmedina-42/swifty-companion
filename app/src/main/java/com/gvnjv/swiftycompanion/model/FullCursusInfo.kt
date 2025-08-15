package com.gvnjv.swiftycompanion.model

data class FullCursusInfo(
    val level: String,
    val name: String,
    val grade: String,
    val skills: List<SkillsResponse>,
    val projects: List<ProjectListResponse>,
)
