package com.gvnjv.swiftycompanion.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectResponse(
    val name: String
) : Parcelable