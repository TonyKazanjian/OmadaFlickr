@file:OptIn(ExperimentalMaterial3Api::class)

package com.omada.sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omada.sample.domain.ApiResult
import com.omada.sample.domain.Photo
import com.omada.sample.search.SearchHistory
import com.omada.sample.search.SearchViewModel

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val searchViewModel: SearchViewModel = viewModel()
    val searchState = searchViewModel.searchState.collectAsState().value
    Scaffold { padding ->
        FlickrSearchBar(
            modifier = modifier.padding(padding),
            onSearch = {
                searchViewModel.launchSearch(it)
            }
        )

        when(searchState) {
            is ApiResult.Success -> {
                ImageGrid(
                    photos = searchState.response.photoList,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp),
                )
            }
            else -> {}
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
private fun ImageGrid(photos: List<Photo>, modifier: Modifier){
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp)
    ){
        items(photos.size) {
            FlickrImage(
                photo = photos[it],
                modifier = modifier
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SearchPreview() {
//    SearchScreen(modifier = Modifier.fillMaxWidth())
//}
