package com.omada.sample.search

import com.omada.sample.data.PhotosResponse
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun getRecentPhotos(): Flow<ApiResult>
    suspend fun getPhotosByQuery(query: String): Flow<ApiResult>
}

sealed class ApiResult {
    data class Success(val response: PhotosResponse): ApiResult()
    data class Error(val error: String): ApiResult()
    object Loading: ApiResult()
}