package com.jfridbergs.chiligiphysearch.api

import android.app.LauncherActivity
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

class Repository {

    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json{ ignoreUnknownKeys = true },contentType = ContentType("application", "json"))
        }
    }

    suspend fun getGifItems(query: String, page: Int, pageSize: Int): Result<List<GifData>>{
        Log.d("REPOSITORY", "Search query: $query")
        Log.d("REPOSITORY", "Page number: $page")
        val offset = page*pageSize
        val response: GiphySearchResponse = httpClient.get("http://api.giphy.com/v1/gifs/search"){
            url {
                parameters.append("api_key", "X6KFLXMVg82h8zL0sGdVZwEuHXaO2XD1")
                parameters.append("q", query)
                parameters.append("limit",pageSize.toString())
                parameters.append("offset", offset.toString())
            }
        }.body()
        return Result.success(response.data)
    }
}

