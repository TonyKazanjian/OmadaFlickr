package com.omada.sample.network

import com.omada.sample.data.PhotosResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val FORMAT = "format=json&nojsoncallback=1"

interface FlickrApi {

    @GET(value = "?method=flickr.photos.getRecent&$FORMAT")
    suspend fun getRecentPhotos() : PhotosResponse

    @GET(value = "?method=flickr.photos.search&$FORMAT")
    suspend fun search(@Query("text") query: String) : PhotosResponse
}