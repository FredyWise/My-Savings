package com.fredy.search.domain

import com.fredy.domain.model.BookMap
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun getBookMaps(userId: String): Flow<List<BookMap>>
}