package com.omada.sample.search

import com.omada.sample.network.FlickrApi
import com.omada.sample.network.FlickrClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class SearchRepositoryImpl(
    private val api: FlickrApi = FlickrClient.flickerApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): SearchRepository {
    override suspend fun getRecentPhotos() = fetchPhotos { ApiResult.Success(api.getRecentPhotos()) }

    override suspend fun getPhotosByQuery(query: String) = fetchPhotos { ApiResult.Success(api.search(query)) }

    private fun fetchPhotos(onExecute: suspend () -> ApiResult): Flow<ApiResult> =
        flow {
            emit(onExecute())
        }
            .onStart { emit(ApiResult.Loading) }
            .catch { emit(ApiResult.Error(it.message ?: "An error happened")) }
            .flowOn(dispatcher)
}