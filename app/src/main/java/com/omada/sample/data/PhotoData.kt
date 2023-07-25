package com.omada.sample.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoData(
    val id: String,
    val title: String
)
