package com.fredy.domain.useCases.RecordUseCases

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.mappers.recordUIMapper.toBookSortedMaps
import com.fredy.domain.model.BookMap
import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetAllRecords(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<Resource<List<BookMap>, DataError.Local>> {
        return flow<Resource<List<BookMap>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
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
        }.catch { e ->
            Timber.e(
                "getAllRecords.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}