package com.fredy.domain.useCases.RecordUseCases

import com.fredy.domain.model.BookMap
import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.mappers.toBookSortedMaps
import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetAllBooks(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<Resource<List<BookMap>, DataError.Local>> {
        return flow<Resource<List<BookMap>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser?.firebaseUserId ?: ""

                val books = bookRepository.getUserBooks(userId).first()

                recordRepository.getRecordMaps(userId).map {
                    it.toBookSortedMaps(books)
                }.collect { data ->
                    Timber.i(
                        "getAllRecords.data: $data"
                    )
                    emit(Resource.Success(data))
                }
            }
        }.catch { e ->
            Timber.e(
                "getAllRecords.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}