package com.gvnjv.swiftycompanion.model

data class ImageResponse(
    var versions: VersionsResponse
)

data class VersionsResponse(
    var small: String
)
