package com.gvnjv.swiftycompanion.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectListResponse(
    val final_mark: Int?,
    val status: String,
    val validated: Boolean,
    val project: ProjectResponse,
    val cursus_ids: List<Int>
): Parcelable