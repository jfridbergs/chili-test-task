package com.jfridbergs.chilligiphy

interface Paginator<Item, T> {
    suspend fun loadNextItems()
    fun reset()
}