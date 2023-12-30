package com.jfridbergs.chiligiphysearch

interface Paginator<Item, T> {
    suspend fun loadNextItems(query: String)
    fun reset()
}