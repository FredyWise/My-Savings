package com.fredy.io.domain.useCases

import com.fredy.domain.model.Book
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class GetAllTrueRecordsWithinSelectedBook(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        book: Book,
    ): Flow<Resource<List<TrueRecord>, DataError.Local>> {
        return flow<Resource<List<TrueRecord>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = currentUser?.firebaseUserId ?: ""

            recordRepository.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
                .map { trueRecords -> trueRecords.filter { it.record.bookIdFk == book.bookId } }
                .collect { data ->
                    Timber.i(
                        "getAllTrueRecordsWithinSpecificTime.Data: $data"
                    )
                    emit(Resource.Success(data))
                }

        }.catch { e ->
            Timber.e(
                "getAllTrueRecordsWithinSpecificTime.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}