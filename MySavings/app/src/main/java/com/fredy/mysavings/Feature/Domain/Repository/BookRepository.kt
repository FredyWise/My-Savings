package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Domain.Model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun upsertBook(book: Book): String
    suspend fun deleteBook(book: Book)
    fun getBook(bookId: String): Flow<Book>
    fun getUserBooks(userId: String): Flow<List<Book>>
}

