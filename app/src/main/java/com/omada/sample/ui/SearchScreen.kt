@file:OptIn(ExperimentalMaterial3Api::class)

package com.omada.sample.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omada.sample.domain.ApiResult
import com.omada.sample.domain.Photo
import com.omada.sample.search.SearchHistory
import com.omada.sample.search.SearchViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onError: (String) -> Unit
) {
    val searchViewModel: SearchViewModel = viewModel()
    val searchState = searchViewModel.searchState.collectAsState().value
    val canPaginate = searchViewModel.canPaginate.collectAsState().value
    val lazyGridState = rememberLazyGridState()

    val shouldStartPaginate by remember {
        derivedStateOf {
            val itemIndexPagingThreshold = lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -10
            val totalItemsCount = lazyGridState.layoutInfo.totalItemsCount - 10
            canPaginate && itemIndexPagingThreshold >= totalItemsCount
        }
    }

    Scaffold(
        topBar = {
            FlickrSearchBar(
                modifier = modifier.padding(4.dp),
                onSearch = {
                    searchViewModel.launchSearch(it)
                }
            )
        }
    ) { padding ->
        when(searchState) {
            is ApiResult.Success -> {
                ImageGrid(
                    photos = searchState.response.photoList,
                    shouldPaginate = shouldStartPaginate,
                    lazyGridState = lazyGridState,
                    paddingValues = padding,
                    modifier = Modifier
                        .aspectRatio(1f)
                ) {
                    searchViewModel.fetchNextPage()
                }
            }
            is ApiResult.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ApiResult.Error -> onError(searchState.error)
        }
    }
}

@Composable
private fun FlickrSearchBar(
    modifier: Modifier,
    onSearch: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    SearchBar(
        query = text,
        onQueryChange = { text = it },
        onSearch = {
            isActive = false
            onSearch(it)
        },
        active = isActive,
        onActiveChange = { isActive = it },
        placeholder =  { Text(text = "Search") },
        leadingIcon = { SearchBarIcon(icon = Icons.Default.Search) },
        trailingIcon = {
            if (isActive) {
                SearchBarIcon(
                    icon = Icons.Default.Clear,
                    Modifier.clickable {
                        if (text.isNotEmpty()) {
                            text = ""
                        } else {
                            isActive = false
                        }
                    }
                )
            }
        },
        modifier = modifier
    ) {
        val history = remember { SearchHistory.getHistory() }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                items = history,
                key = { query -> history.indexOf(query)}
            ) { query ->
                Row(
                    modifier = Modifier
                        .clickable {
                            onSearch(query)
                            text = query
                            isActive = false
                        }
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    SearchBarIcon(icon = Icons.Default.History)
                    Text(
                        text = query,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun SearchBarIcon(icon: ImageVector, modifier: Modifier = Modifier) {
    Icon(imageVector = icon, modifier = modifier, contentDescription = null)
}

@Composable
private fun ImageGrid(
    photos: List<Photo>,
    shouldPaginate: Boolean,
    lazyGridState: LazyGridState,
    paddingValues: PaddingValues,
    modifier: Modifier,
    fetchPhotos: () -> Unit
){
    LaunchedEffect(key1 = shouldPaginate) {
        if (shouldPaginate){
            fetchPhotos()
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = lazyGridState,
        contentPadding = paddingValues
    ){
        items(photos.size) {
            FlickrImage(
                photo = photos[it],
                modifier = modifier
                    .padding(2.dp)
            )
        }
    }
}
