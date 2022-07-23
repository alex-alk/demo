package com.alexandruleonte.demo.gallery.api

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GalleryItem(
    var title: String,
    var id: String,
    @Json(name = "url_s") var url: String,
    var owner: String
) {
    val photoPageUri: Uri
        get() {
            return Uri.parse("https://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build()
        }
}