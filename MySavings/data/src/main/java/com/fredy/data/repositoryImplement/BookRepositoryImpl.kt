package com.fredy.data.repositoryImplement

import com.fredy.data.database.dao.BookDao
import com.fredy.data.database.firestoreDataSource.BookDataSource
import com.fredy.data.mappers.toDataBook
import com.fredy.data.mappers.toDomainBook
import com.fredy.domain.model.Book
import com.fredy.domain.repository.BookRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
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
            val dataBook = if (book.bookId.isEmpty()) {
                val newBookRef = bookCollection.document()
                book.copy(
                    bookId = newBookRef.id,
                )
            } else {
                book
            }.toDataBook()

            bookDao.upsertBookItem(
                dataBook
            )
            bookDataSource.upsertBookItem(
                dataBook
            )
            dataBook.bookId
        }
    }

    override suspend fun deleteBook(book: Book) {
        withContext(Dispatchers.IO) {
            val dataBook = book.toDataBook()
            bookDataSource.deleteBookItem(dataBook)
            bookDao.deleteBookItem(dataBook)
        }
    }


    override fun getBook(bookId: String): Flow<Book> {
        return flow {
            val domainBook = withContext(Dispatchers.IO) {
                bookDataSource.getBook(bookId)
            }.toDomainBook()
            emit(domainBook)
        }
    }

    override fun getUserBooks(userId: String): Flow<List<Book>> {
        return flow {
            withContext(Dispatchers.IO) {
                bookDataSource.getUserBooksOrderedByName(
                    userId
                )
            }.collect { data ->
                Timber.i("getUserBooksRepo.Data: $data")
                val domainBooks = data.map {
                    it.toDomainBook()
                }
                emit(domainBooks)
            }
        }
    }

}