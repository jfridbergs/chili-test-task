package com.jfridbergs.chilligiphy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jfridbergs.chilligiphy.api.DataImage
import com.jfridbergs.chilligiphy.api.GifData
import com.jfridbergs.chilligiphy.api.GiphyApi
import com.jfridbergs.chilligiphy.api.GiphySearchResponse
import com.jfridbergs.chilligiphy.api.OriginalImage
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import com.jfridbergs.chilligiphy.ui.theme.ChilliGiphyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChilliGiphyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PaginatorSample()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChilliGiphyTheme {
        Greeting("Android")
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MainScreen() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val id = remember {
            mutableStateOf(TextFieldValue())
        }



        val foundGifs = remember {
            mutableStateOf(emptyList<GifData>())
        }

        val profile = remember {
            mutableStateOf(
                GifData(
                    title = "",
                    url = "",
                    images = DataImage(
                        original = OriginalImage(
                            url=""
                        )
                    )
                )
            )
        }

        Text(
            text="API Sample",
            style= TextStyle(
                fontSize = 40.sp,
                fontFamily = FontFamily.Cursive
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "User ID")},
            value = id.value,
            onValueChange = { id.value = it
            sendRequest(id = id.value.text,
                foundData = foundGifs)
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        GifItemsList(foundGifs.value)

        /*
        GlideImage(
            model = profile.value.images.ogImage.imageUrl,
            contentDescription =profile.value.title,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Crop)
*/
        //Text(text = profile.component1().toString(), fontSize = 40.sp)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun GifItemsList(items: List<GifData>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        items(
            items = items,
            itemContent = { gifItem ->

                    GlideImage(
                        model = gifItem.images.original.url,
                        contentDescription =gifItem.title,
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop)
                }
        )

            }



    }


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PaginatorSample(){
    val viewModel = viewModel<GifViewModel>()
    val state = viewModel.state
    LazyColumn(modifier = Modifier.fillMaxSize()){
        items(state.items.size) {
                i ->
            val item = state.items[i]
            if(i>=state.items.size -1 && !state.endReached && !state.isLoading){
                viewModel.loadNextItems()
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)){
                GlideImage(
                    model = item.images.original.url,
                    contentDescription =item.title,
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop)
            }
        }
        item {
            if(state.isLoading){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ){
                    CircularProgressIndicator()
                }
            }
        }
    }
}


fun sendRequest(
    id: String,
    foundData: MutableState<List<GifData>>
) {
    if(id.isEmpty()){
        return
    }
    val retrofit = Retrofit.Builder()
        .baseUrl("http://api.giphy.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(GiphyApi::class.java)

    val apiKey = "X6KFLXMVg82h8zL0sGdVZwEuHXaO2XD1"
    val call: Call<GiphySearchResponse?>? = api.getGifsBySearch(apiKey,5,0,id);

    call!!.enqueue(object: Callback<GiphySearchResponse?> {
        override fun onResponse(call: Call<GiphySearchResponse?>, response: Response<GiphySearchResponse?>) {
            if(response.isSuccessful) {
                Log.d("Main", "success!" + response.body().toString())
                foundData.value = response.body()!!.data
            }
        }

        override fun onFailure(call: Call<GiphySearchResponse?>, t: Throwable) {
            Log.e("Main", "Failed mate " + t.message.toString())
        }
    })
}