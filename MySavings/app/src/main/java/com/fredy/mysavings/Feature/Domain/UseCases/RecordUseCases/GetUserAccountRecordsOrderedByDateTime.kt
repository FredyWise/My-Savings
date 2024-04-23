package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.RecordMap
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GetUserAccountRecordsOrderedByDateTime(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(accountId: String, sortType: SortType): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
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