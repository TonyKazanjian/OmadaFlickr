package com.omada.sample.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omada.sample.domain.ApiResult
import com.omada.sample.domain.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchRepository = SearchRepositoryImpl()): ViewModel() {

    private val _searchState: MutableStateFlow<ApiResult> = MutableStateFlow(ApiResult.Loading)
    val searchState: StateFlow<ApiResult>
        get() = _searchState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiResult.Loading)

    private val _canPaginate = MutableStateFlow(false)
    val canPaginate: StateFlow<Boolean>
        get() = _canPaginate.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val currentPage = MutableStateFlow(1)

    private val photosList = mutableListOf<Photo>()

    private var currentQuery: String = ""

    init {
        launchSearch()
    }

    fun launchSearch(searchText: String = "") {
        viewModelScope.launch {
            currentPage.value = 1
            if (searchText.isNotEmpty()) {
                currentQuery = searchText
                repository.getPhotosByQuery(currentQuery, 1).collectResults()
            } else {
                repository.getRecentPhotos(1).collectResults()
            }
        }
    }

    fun fetchNextPage() {
        viewModelScope.launch {
            if (currentQuery.isNotEmpty()) {
                repository.getPhotosByQuery(currentQuery, currentPage.value).collectResults()
            } else {
                repository.getRecentPhotos(currentPage.value).collectResults()
            }
        }
    }

    private suspend fun Flow<ApiResult>.collectResults() {
        collect {
            if (it is ApiResult.Success) {
                _canPaginate.value = it.response.page >= 1 && it.response.page <= it.response.pages

                if(_canPaginate.value) {
                    Log.d("TONY", "increment page")

                    currentPage.value ++
                }
                if (it.response.page == 1) {
                    Log.d("TONY", "first page")
                    photosList.clear()
                    photosList.addAll(it.response.photoList)
                } else {
                    Log.d("TONY", "currentPage: ${currentPage.value}")
                    photosList.addAll(it.response.photoList)
                }

                val result = it.response.copy(
                    page = currentPage.value,
                    photoList = photosList
                )
                _searchState.value = ApiResult.Success(result)
            } else {
                _searchState.value = it
            }
        }
    }
}