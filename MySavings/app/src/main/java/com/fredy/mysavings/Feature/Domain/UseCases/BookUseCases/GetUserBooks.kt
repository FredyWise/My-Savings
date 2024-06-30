package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetUserBooks(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<List<Book>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
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