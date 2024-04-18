package com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource

import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Model.Book

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface BookDataSource {
    suspend fun upsertBookItem(book: Book)
    suspend fun deleteBookItem(book: Book)
    suspend fun getBook(bookId: String): Book
    suspend fun getUserBooksOrderedByName(userId: String): Flow<List<Book>>
}


class BookDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : BookDataSource {
    private val bookCollection = firestore.collection(
        "book"
    )

    override suspend fun upsertBookItem(
        book: Book
    ) {
        bookCollection.document(
            book.bookId
        ).set(
            book
        )
    }

    override suspend fun deleteBookItem(
        book: Book
    ) {
        bookCollection.document(book.bookId).delete()
    }

    override suspend fun getBook(bookId: String): Book {
        return withContext(Dispatchers.IO) {
            try {
                bookCollection.document(bookId).get().await().toObject<Book>()
                    ?: throw Exception(
                        "Book Not Found"
                    )
            } catch (e: Exception) {
                Log.e(
                    "Failed to get book: ${e.message}"
                )
                throw e
            }
        }
    }

    override suspend fun getUserBooksOrderedByName(userId: String): Flow<List<Book>> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = bookCollection.whereEqualTo(
                    "userIdFk",
                    userId
                ).orderBy("bookName").snapshots()

                querySnapshot.map { it.toObjects()}
            } catch (e: Exception) {
                Log.e(
                    "Failed to get user books: $e"
                )
                throw e
            }
        }
    }
}