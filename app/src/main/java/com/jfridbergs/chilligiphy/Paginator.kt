package com.jfridbergs.chilligiphy

interface Paginator<Item, T> {
    suspend fun loadNextItems(query: String)
    fun reset()
}