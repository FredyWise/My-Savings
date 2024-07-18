package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetAllTrueRecordsWithinSpecificTime(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        book: Book,
    ): Flow<Resource<List<TrueRecord>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            recordRepository.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
                .map { trueRecords -> trueRecords.filter { it.record.bookIdFk == book.bookId } }
                .collect { data ->
                    Log.i(
                        "getAllTrueRecordsWithinSpecificTime.Data: $data"
                    )
                    emit(Resource.Success(data))
                }

        }.catch { e ->
            Log.e(
                "getAllTrueRecordsWithinSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}