package com.omada.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.omada.sample.domain.Photo

@Composable
fun FlickrImage(
    photo: Photo,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photo.url)
            .crossfade(true)
            .build(),
        imageLoader = ImageLoader(LocalContext.current),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}