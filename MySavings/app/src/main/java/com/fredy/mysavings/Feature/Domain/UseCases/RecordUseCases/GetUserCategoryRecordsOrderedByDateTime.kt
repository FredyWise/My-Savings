package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.RecordMap
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetUserCategoryRecordsOrderedByDateTime(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(categoryId: String, sortType: SortType): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                "getUserCategoryRecordsOrderedByDateTime: $categoryId",

                )

            recordRepository.getUserCategoryRecordsOrderedByDateTime(
                userId, categoryId, sortType
            ).collect { data ->
                Log.i(
                    "getUserCategoryRecordsOrderedByDateTime.Data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserCategoryRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}