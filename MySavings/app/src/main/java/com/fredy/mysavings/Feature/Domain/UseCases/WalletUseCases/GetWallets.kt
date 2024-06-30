package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetWallets(
    private val repository: WalletRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Resource<List<Wallet>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            withContext(Dispatchers.IO) {
                repository.getUserWallets(
                    userId
                )
                    .map { accounts -> accounts.filter { it.walletId != DefaultData.deletedWallet.walletId + userId } }
            }.collect { data ->
                Log.i("getUserAccountOrderedByName.Data: $data")
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e("getUserAccountOrderedByName.Error: $e")
            emit(Resource.Error(e.message.toString()))
        }
    }
}