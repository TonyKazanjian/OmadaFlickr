package com.omada.sample.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

//    private val _history: MutableStateFlow<List<String>> = MutableStateFlow(SearchHistory.getHistory())
//    val history: StateFlow<List<String>>
//        get() = _history.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchHistory.getHistory())

    init {
        launchSearch()
    }

    fun launchSearch(searchText: String? = null) {
        viewModelScope.launch {
            searchText?.let {
                repository.getPhotosByQuery(it).collectResults()
            } ?: repository.getRecentPhotos().collectResults()
        }
    }

    private suspend fun Flow<ApiResult>.collectResults() {
        collect {
            _searchState.value = it
        }
    }
}