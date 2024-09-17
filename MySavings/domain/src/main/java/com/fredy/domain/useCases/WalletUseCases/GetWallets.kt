package com.fredy.domain.useCases.WalletUseCases

import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.domain.util.DefaultData
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

class GetWallets(
    private val repository: WalletRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Resource<List<Wallet>, DataError.Local>> {
        return flow<Resource<List<Wallet>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
                withContext(Dispatchers.IO) {
                    repository.getUserWallets(
                        userId
                    )
                        .map { accounts -> accounts.filter { it.walletId != DefaultData.deletedWallet.walletId + userId } }
                }.collect { data ->
                    Timber.i("getUserAccountOrderedByName.Data: $data")
                    emit(Resource.Success(data))
                }
            }
        }.catch { e ->
            Timber.e("getUserAccountOrderedByName.Error: $e")
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}