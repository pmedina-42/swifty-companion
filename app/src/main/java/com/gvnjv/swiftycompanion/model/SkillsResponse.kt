package com.gvnjv.swiftycompanion.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SkillsResponse(
    val name: String,
    val level: Float
) : Parcelable
