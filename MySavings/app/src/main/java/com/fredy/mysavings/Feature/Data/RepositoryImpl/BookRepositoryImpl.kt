package com.fredy.mysavings.Feature.Data.RepositoryImpl

import com.fredy.mysavings.Feature.Data.Database.Dao.BookDao
import com.fredy.mysavings.Feature.Data.Database.FirestoreDataSource.BookDataSource
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDataSource: BookDataSource,
    private val bookDao: BookDao,
    private val firestore: FirebaseFirestore,
) : BookRepository {
    private val bookCollection = firestore.collection(
        "book"
    )

    override suspend fun upsertBook(book: Book): String {
        return withContext(Dispatchers.IO) {
            val tempBook = if (book.bookId.isEmpty()) {
                val newBookRef = bookCollection.document()
                book.copy(
                    bookId = newBookRef.id,
                )
            } else {
                book
            }

            bookDao.upsertBookItem(
                tempBook
            )
            bookDataSource.upsertBookItem(
                tempBook
            )
            tempBook.bookId
        }
    }

    override suspend fun deleteBook(book: Book) {
        withContext(Dispatchers.IO) {
            bookDataSource.deleteBookItem(book)
            bookDao.deleteBookItem(book)
        }
    }


    override fun getBook(bookId: String): Flow<Book> {
        return flow {
            val book = withContext(Dispatchers.IO) {
                bookDataSource.getBook(bookId)
            }
            emit(book)
        }
    }

    override fun getUserBooks(userId: String): Flow<List<Book>> {
        return flow {
            withContext(Dispatchers.IO) {
                bookDataSource.getUserBooksOrderedByName(
                    userId
                )
            }.collect { data ->
                Log.i("getUserBooksRepo.Data: $data")
                emit(data)
            }
        }
    }

}