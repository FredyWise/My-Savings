package com.fredy.mysavings.Feature.Domain.Model

import com.fredy.domain.model.Book
import com.fredy.domain.model.RecordMap

data class BookMap(
    val book: Book,
    val recordMaps: List<RecordMap>
){
    fun doesMatchSearchQuery(query: String): Boolean {
        return book.doesMatchSearchQuery(query) ||
                recordMaps.any { it.doesMatchSearchQuery(query) }
    }
}