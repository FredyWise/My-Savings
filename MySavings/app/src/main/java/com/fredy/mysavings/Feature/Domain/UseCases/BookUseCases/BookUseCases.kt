package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Model.Book
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

data class BookUseCases(
    val upsertBook: UpsertBook,
    val deleteBook: DeleteBook,
    val getBook: GetBook,
    val getUserBooks: GetUserBooks
)

class UpsertBook(
    private val repository: BookRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(book: Book): String {
        val currentUser = authRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertBook(book.copy(userIdFk = currentUserId))
    }
}

class DeleteBook(
    private val repository: BookRepository
) {
    suspend operator fun invoke(book: Book) {
        repository.deleteBook(book)
    }
}

class GetBook(
    private val repository: BookRepository
) {
    operator fun invoke(bookId: String): Flow<Book> {
        return repository.getBook(bookId)
    }
}


class GetUserBooks(
    private val bookRepository: BookRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Resource<List<Book>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            bookRepository.getUserBooks(userId).collect { books ->
                Log.i("getBooksOrderedByName.Data: $books")
                emit(Resource.Success(books))
            }
        }.catch { e ->
            Log.e(
                "getBooksOrderedByName.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}
