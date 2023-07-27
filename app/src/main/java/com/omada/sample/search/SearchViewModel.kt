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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchRepository = SearchRepositoryImpl()): ViewModel() {

    private val _searchState: MutableStateFlow<ApiResult> = MutableStateFlow(ApiResult.Loading)
    val searchState: StateFlow<ApiResult>
        get() = _searchState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiResult.Loading)

    private val _canPaginate = MutableStateFlow(false)
    val canPaginate: StateFlow<Boolean>
        get() = _canPaginate.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private var currentPage = 1

    private val photosList = mutableListOf<Photo>()

    //Would like to use a SaveStateHandle for this
    private var currentQuery: String = ""

    init {
        launchSearch()
    }

    fun launchSearch(
        searchText: String = "",
        onGridItemsUpdated: suspend () -> Unit = {}
    ) {
        viewModelScope.launch {
            currentPage = 1
            if (searchText.isNotEmpty()) {
                currentQuery = searchText
                repository.getPhotosByQuery(currentQuery, 1).collectResults()
            } else {
                repository.getRecentPhotos(1).collectResults()
            }
            onGridItemsUpdated()
        }
    }

    fun fetchNextPage() {
        viewModelScope.launch {
            if (currentQuery.isNotEmpty()) {
                repository.getPhotosByQuery(currentQuery, currentPage).collectResults()
            } else {
                repository.getRecentPhotos(currentPage).collectResults()
            }
        }
    }

    private suspend fun Flow<ApiResult>.collectResults() {
        collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    if (photosList.isEmpty()) {
                        _searchState.value = result
                    }
                }
                is ApiResult.Success -> {
                    _canPaginate.value = result.response.page >= 1 && result.response.page <= result.response.pages

                    if (result.response.page == 1) {
                        photosList.clear()
                        photosList.addAll(result.response.photoList)
                    } else {
                        photosList.addAll(result.response.photoList)
                    }
                    val response = result.response.copy(
                        page = currentPage,
                        photoList = photosList
                    )
                    if(_canPaginate.value) {
                        currentPage++
                    }
                    _searchState.value = ApiResult.Success(response)
                }
                is ApiResult.Error -> _searchState.value = result
            }
        }
    }
}