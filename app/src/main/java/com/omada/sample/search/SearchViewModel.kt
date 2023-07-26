package com.omada.sample.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omada.sample.domain.ApiResult
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

    init {
        launchSearch()
    }

    fun launchSearch(searchText: String = "") {
        viewModelScope.launch {
            if (searchText.isNotEmpty()) {
                repository.getPhotosByQuery(searchText).collectResults()
            } else {
                repository.getRecentPhotos().collectResults()
            }
        }
    }

    private suspend fun Flow<ApiResult>.collectResults() {
        collect {
            _searchState.value = it
        }
    }
}