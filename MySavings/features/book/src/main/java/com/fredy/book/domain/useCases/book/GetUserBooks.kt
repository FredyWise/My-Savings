package com.fredy.book.domain.useCases.book

import com.fredy.book.domain.BookSpecificRepository
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.model.Book
import com.fredy.domain.repository.UserRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetUserBooks(
    private val bookSpecificRepository: BookSpecificRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<List<Book>, DataError.Local>> {
        return flow<Resource<List<Book>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId

                bookSpecificRepository.getUserBooks(userId).collect { books ->
                    Timber.i("getBooksOrderedByName.Data: $books")
                    emit(Resource.Success(books))
                }
            }
        }.catch { e ->
            Timber.e(
                "getBooksOrderedByName.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}