package com.fredy.mysavings.Feature.Data.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Upsert
    suspend fun upsertBookItem(book: Book)

    @Upsert
    suspend fun upsertAllBookItem(books: List<Book>)

    @Delete
    suspend fun deleteBookItem(book: Book)

    @Query("DELETE FROM book")
    suspend fun deleteAllBooks()

    @Query(
        "SELECT * FROM book " +
                "WHERE bookId=:bookId"
    )
    suspend fun getBook(bookId: String): Book

    @Query(
        "SELECT * FROM book " +
                "WHERE userIdFk = :userId " +
                "ORDER BY bookName ASC"
    )
    fun getUserBooksOrderedByName(userId: String): Flow<List<Book>>
}
