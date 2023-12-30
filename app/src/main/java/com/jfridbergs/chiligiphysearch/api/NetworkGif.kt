package com.jfridbergs.chiligiphysearch.api

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
    @SerializedName("fixed_width") val fixed_width: FwImage
)

@Serializable
data class FwImage(
    @SerializedName("url") val url: String
)

