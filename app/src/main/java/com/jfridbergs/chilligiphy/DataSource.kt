package com.jfridbergs.chilligiphy

import android.app.LauncherActivity
import android.util.Log
import androidx.compose.runtime.MutableState
import com.jfridbergs.chilligiphy.api.GifData
import com.jfridbergs.chilligiphy.api.GiphyApi
import com.jfridbergs.chilligiphy.api.GiphySearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DataSource {
    fun getItems(page: Int, query: String): Result<List<GifData>> {

        if(query.isEmpty()){
            return Result.success(emptyList())
        }
        val offset = page*5
        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.giphy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(GiphyApi::class.java)

        val apiKey = "X6KFLXMVg82h8zL0sGdVZwEuHXaO2XD1"
        val call: Call<GiphySearchResponse?>? = api.getGifsBySearch(apiKey,5,offset,query);

        call!!.enqueue(object: Callback<GiphySearchResponse?> {
            override fun onResponse(call: Call<GiphySearchResponse?>, response: Response<GiphySearchResponse?>) {
                if(response.isSuccessful) {
                    Log.d("Main", "success!" + response.body().toString())
                    Result.success(
                        response.body()!!.data
                    )
                }
            }

            override fun onFailure(call: Call<GiphySearchResponse?>, t: Throwable) {
                Log.e("Main", "Failed mate " + t.message.toString())
            }
        })
        return Result.success(emptyList())
    }
}

