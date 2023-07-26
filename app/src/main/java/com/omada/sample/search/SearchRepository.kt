package com.omada.sample.search

import com.omada.sample.data.PhotosResponse
import com.omada.sample.domain.ApiResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun getRecentPhotos(): Flow<ApiResult>
    suspend fun getPhotosByQuery(query: String): Flow<ApiResult>
}