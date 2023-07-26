package com.omada.sample.domain

data class PhotoPage(
    val page: Int,
    val pages: Int,
    val photoList: List<Photo>
)

data class Photo(
    val id: String,
    val url: String
)