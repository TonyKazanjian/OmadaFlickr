package com.omada.sample.domain

import com.omada.sample.data.PhotosResponse

sealed class ApiResult {
    data class Success(val response: PhotoPage): ApiResult()
    data class Error(val error: String): ApiResult()
//    data class Paginating(val response: PhotosResponse): ApiResult()
    object Loading: ApiResult()
}

private const val IMAGE_FORMAT = "https://live.staticflickr.com/%1\$s/%2\$s_%3\$s_%4\$s.jpg"

enum class SizeSuffix(val value: String) {
    Thumbnail("w"),
    Detail("b")
}

fun PhotosResponse.map(sizeSuffix: SizeSuffix): PhotoPage =
    PhotoPage(
        page = photos.page,
        pages = photos.pages,
        photoList = photos.photo.map { data ->
            Photo(
                id = data.id,
                url = String.format(IMAGE_FORMAT, data.server, data.id, data.secret, sizeSuffix.value)
            )
        }
    )
