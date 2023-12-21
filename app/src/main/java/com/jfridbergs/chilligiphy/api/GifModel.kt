package com.jfridbergs.chilligiphy.api

import com.google.gson.annotations.SerializedName

data class GiphySearchResponse(
    @SerializedName("data") val foundData: List<GifData>,
)

data class GifData(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("images") val images: DataImage
)

data class DataImage(
    @SerializedName("original") val ogImage: OriginalImage
)

data class OriginalImage(
    @SerializedName("url") val imageUrl: String
)

