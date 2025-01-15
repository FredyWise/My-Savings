package com.fredy.wallet.domain.useCases

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.enums.SortType
import com.fredy.domain.mappers.recordUIMapper.toRecordSortedMaps
import com.fredy.domain.model.RecordMap
import com.fredy.domain.repository.UserRepository
import com.fredy.wallet.domain.WalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class GetUserWalletRecordsOrderedByDateTime(
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(
        accountId: String,
        sortType: SortType
    ): Flow<Resource<List<RecordMap>, DataError.Local>> {
        return flow<Resource<List<RecordMap>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = currentUser?.firebaseUserId ?: ""
            Timber.i(
                "getUserAccountRecordsOrderedByDateTime: $accountId",

                )
            withContext(Dispatchers.IO) {
                walletRepository.getUserWalletRecordsOrderedByDateTime(
                    userId, accountId, sortType
                )
            }.collect { data ->
                Timber.i(
                    "getUserAccountRecordsOrderedByDateTime.data: $data"
                )
                emit(Resource.Success(data.toRecordSortedMaps()))
            }
        }.catch { e ->
            Timber.e(
                "getUserAccountRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}