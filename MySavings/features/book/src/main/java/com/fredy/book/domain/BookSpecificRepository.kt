package com.fredy.book.domain

import com.fredy.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookSpecificRepository {
    suspend fun upsertBook(book: Book): String
    suspend fun deleteBook(book: Book)
    fun getBook(bookId: String): Flow<Book>
    fun getUserBooks(userId: String): Flow<List<Book>>
}

