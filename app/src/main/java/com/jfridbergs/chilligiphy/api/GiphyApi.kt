package com.jfridbergs.chilligiphy.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


public interface GiphyApi {
    @Headers(
        "Accept: application/json"
    )
    @GET("v1/gifs/search")
    abstract fun getGifsBySearch(@Query("api_key") apiKey: String,
                                 @Query("limit") limit: Int,
                                 @Query("q") search: String): Call<GiphySearchResponse?>?
}