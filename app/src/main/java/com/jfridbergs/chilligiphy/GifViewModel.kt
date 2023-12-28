package com.jfridbergs.chilligiphy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jfridbergs.chilligiphy.api.GifData
import kotlinx.coroutines.launch

class GifViewModel(): ViewModel() {

    private val repository = Repository()

    var state by mutableStateOf(ScreenState())

    private val paginator = GiphyPaginator(
        initialKey = state.page,
        onLoadUpdated = {
            state = state.copy(isLoading = it)
        },
        onRequest = {
            query, nextPage -> repository.getGifItems(query,nextPage, 10)
        },
        getNextKey = {
            state.page + 1
        },
        onError = {
            state = state.copy(error = it?.localizedMessage)
        },
        onSuccess = {items, newKey ->
            state = state.copy(
                items = state.items + items,
                page = newKey,
                endReached = items.isEmpty()
            )
        }

    )


    fun newQuery(query: String){
        state.page = 0
        loadNextItems(query)
    }

    fun resetState(newQuery: String){
        state = state.copy(isLoading = false,page = 0, items = emptyList(), endReached = false)
        paginator.reset()
        loadNextItems(newQuery)
    }

    fun loadNextItems(query: String){
        viewModelScope.launch{
            paginator.loadNextItems(query)
        }
    }
}

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<GifData> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    var page: Int = 0
)