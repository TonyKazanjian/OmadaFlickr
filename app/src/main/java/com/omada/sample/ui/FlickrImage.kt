package com.omada.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.omada.sample.data.PhotoData

private const val IMAGE_FORMAT = "https://live.staticflickr.com/%1\$s/%2\$s_%3\$s_%4\$s.jpg"

enum class SizeSuffix(val value: String) {
    Thumbnail("w"),
    Detail("b")
}

@Composable
fun FlickrImage(
    photo: PhotoData,
    sizeSuffix: SizeSuffix,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(String.format(IMAGE_FORMAT, photo.server, photo.id, photo.secret, sizeSuffix.value))
            .crossfade(true)
            .build(),
        imageLoader = ImageLoader(LocalContext.current),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}