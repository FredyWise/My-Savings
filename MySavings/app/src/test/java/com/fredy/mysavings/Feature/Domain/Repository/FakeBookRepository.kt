package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Domain.Model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeBookRepository : BookRepository {

    private val books = mutableListOf<Book>()

    override suspend fun upsertBook(book: Book): String {
        val existingBook = books.find { it.bookId == book.bookId }
        return if (existingBook != null) {
            books.remove(existingBook)
            books.add(book)
            book.bookId
        } else {
            book.bookId.also { books.add(book) }
        }
    }

    override suspend fun deleteBook(book: Book) {
        books.remove(book)
    }

    override fun getBook(bookId: String): Flow<Book> {
        return flow { emit(books.find { it.bookId == bookId }!!) }
    }

    override fun getUserBooks(userId: String): Flow<List<Book>> {
        return flow { emit(books.filter { it.userIdFk == userId }) }
    }
}


