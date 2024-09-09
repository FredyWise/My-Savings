package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.data.enums.SortType
import com.fredy.domain.model.RecordMap
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GetUserWalletRecordsOrderedByDateTime(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(accountId: String, sortType: SortType): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                "getUserAccountRecordsOrderedByDateTime: $accountId",

                )
            withContext(Dispatchers.IO) {
                recordRepository.getUserAccountRecordsOrderedByDateTime(
                    userId, accountId, sortType
                )
            }.collect { data ->
                Log.i(
                    "getUserAccountRecordsOrderedByDateTime.data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserAccountRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}