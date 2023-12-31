package com.jfridbergs.chiligiphysearch

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jfridbergs.chiligiphysearch.ui.theme.ChiliGiphyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChiliGiphyTheme {
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


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PaginatorSample(){

    val query = remember {
        mutableStateOf(TextFieldValue())
    }
    val viewModel = viewModel<GifViewModel>()
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            label = { Text(text = "Search for gifs")},
            value = query.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp, start = 40.dp, end = 40.dp)
                .background(
                    colorResource(id = R.color.search_bg),
                    shape = RoundedCornerShape(7.dp)
                ).semantics { testTag="SearchInput" },
            shape = RoundedCornerShape(7.dp),
            leadingIcon = { Icon( imageVector = Icons.Default.Search, contentDescription = "Search") },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = { query.value = it
                viewModel.resetState(query.value.text)
            }
        )

        Spacer(modifier = Modifier.height(15.dp))
        var columnCount = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE )  4 else 2

        LazyVerticalGrid(modifier = Modifier.fillMaxSize().semantics { testTag="SearchOutput" },
        columns = GridCells.Fixed(columnCount)){
            items(state.items.size) {
                    i ->
                val item = state.items[i]
                if(i>=state.items.size -1 && !state.endReached && !state.isLoading){
                    viewModel.loadNextItems(query.value.text)
                }
                    GlideImage(
                        model = item.images.fixed_width.url,
                        contentDescription =item.title,
                        modifier = Modifier.requiredSize(180.dp).padding(5.dp),
                        contentScale = ContentScale.Crop)
                }

            item {
                if(state.isLoading){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .semantics { testTag="waiting" },
                        horizontalArrangement = Arrangement.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }



}

