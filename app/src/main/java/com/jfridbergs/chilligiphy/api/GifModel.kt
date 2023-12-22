package com.jfridbergs.chilligiphy.api

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GiphySearchResponse(
    @SerializedName("data") val data: List<GifData>,
)

@Serializable
data class GifData(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("images") val images: DataImage
)

@Serializable
data class DataImage(
    @SerializedName("original") val original: OriginalImage
)

@Serializable
data class OriginalImage(
    @SerializedName("url") val url: String
)

