package com.omada.sample.search

import com.omada.sample.data.PhotosResponse
import com.omada.sample.domain.ApiResult
import com.omada.sample.domain.SizeSuffix
import com.omada.sample.domain.map
import com.omada.sample.network.FlickrApi
import com.omada.sample.network.FlickrClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class SearchRepositoryImpl(
    private val api: FlickrApi = FlickrClient.flickerApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): SearchRepository {
    override suspend fun getRecentPhotos(page: Int) = fetchPhotos {
        ApiResult.Success(api.getRecentPhotos(page).map(SizeSuffix.Thumbnail))
    }

    override suspend fun getPhotosByQuery(query: String, page: Int) = fetchPhotos {
        SearchHistory.add(query)
        ApiResult.Success(api.search(query, page).map(SizeSuffix.Thumbnail))
    }

    private fun fetchPhotos(onExecute: suspend () -> ApiResult): Flow<ApiResult> =
        flow {
            emit(onExecute())
        }
            .onStart { emit(ApiResult.Loading) }
            .catch { emit(ApiResult.Error(it.message ?: "An error happened")) }
            .flowOn(dispatcher)
}