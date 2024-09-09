package com.fredy.domain.model

data class BookMap(
    val book: Book,
    val recordMaps: List<RecordMap>
){
    fun doesMatchSearchQuery(query: String): Boolean {
        return book.doesMatchSearchQuery(query) ||
                recordMaps.any { it.doesMatchSearchQuery(query) }
    }
}