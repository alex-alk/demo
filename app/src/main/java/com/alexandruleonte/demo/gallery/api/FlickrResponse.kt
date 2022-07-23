package com.alexandruleonte.demo.gallery.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlickrResponse (
    val photos: PhotoResponse
)