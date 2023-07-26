package com.omada.sample.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotosResponse(
    val photos: Photos
)

@JsonClass(generateAdapter = true)
data class Photos(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<PhotoData>
)

@JsonClass(generateAdapter = true)
data class PhotoData(
    val id: String,
    val server: String,
    val secret: String,
    val title: String
)





