package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Util.Mappers.getCurrencies
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.DefaultData.deletedWallet
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class WalletUseCases(
    val upsertWallet: UpsertWallet,
    val deleteWallet: DeleteWallet,
    val getWallet: GetWallet,
    val getWalletsOrderedByName: GetWallets,
    val getWalletsTotalBalance: GetWalletsTotalBalance,
    val getWalletsCurrencies: GetWalletsCurrencies,
)

class UpsertWallet(
    private val repository: WalletRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(wallet: Wallet): String {
        val currentUser = authRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertWallet(wallet.copy(userIdFk = currentUserId))
    }
}

class DeleteWallet(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(wallet: Wallet) {
        repository.deleteWallet(wallet)
    }
}

class GetWallet(
    private val repository: WalletRepository
) {
    operator fun invoke(accountId: String): Flow<Wallet> {
        return repository.getWallet(accountId)
    }
}

class GetWallets(
    private val repository: WalletRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<Resource<List<Wallet>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            withContext(Dispatchers.IO) {
                repository.getUserWallets(
                    userId
                ).map { accounts -> accounts.filter { it.walletId != deletedWallet.walletId + userId } }
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

class GetWalletsCurrencies(
    private val repository: WalletRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<List<String>> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            withContext(Dispatchers.IO) {
                repository.getUserWallets(
                    userId
                ).map { it.getCurrencies() }
            }.collect { data ->
                emit(data)
            }
        }
    }
}

class GetWalletsTotalBalance(
    private val repository: WalletRepository,
    private val currencyUseCases: CurrencyUseCases,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency

            repository.getUserWallets(userId).collect { accounts ->
                val totalAccountBalance = accounts.getTotalAccountBalance(userCurrency)
                val data = BalanceItem(
                    "Total Balance",
                    totalAccountBalance,
                    userCurrency
                )
                emit(data)
            }
        }
    }

    private suspend fun List<Wallet>.getTotalAccountBalance(userCurrency: String): Double {
        return this.sumOf { account ->
            currencyUseCases.currencyConverter(
                account.walletAmount,
                account.walletCurrency,
                userCurrency
            )
        }
    }
}