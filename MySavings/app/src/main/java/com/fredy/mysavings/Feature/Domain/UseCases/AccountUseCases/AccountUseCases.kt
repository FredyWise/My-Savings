package com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Domain.Repository.AccountRepository
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Feature.Mappers.getCurrencies
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.deletedAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class AccountUseCases(
    val upsertAccount: UpsertAccount,
    val deleteAccount: DeleteAccount,
    val getAccount: GetAccount,
    val getAccountOrderedByName: GetAccounts,
    val getAccountsTotalBalance: GetAccountsTotalBalance,
    val getAccountsCurrencies: GetAccountsCurrencies,
)

class UpsertAccount(
    private val repository: AccountRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(account: Account): String {
        val currentUser = authRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertAccount(account.copy(userIdFk = currentUserId))
    }
}

class DeleteAccount(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(account: Account) {
        repository.deleteAccount(account)
    }
}

class GetAccount(
    private val repository: AccountRepository
) {
    operator fun invoke(accountId: String): Flow<Account> {
        return repository.getAccount(accountId)
    }
}

class GetAccounts(
    private val repository: AccountRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<Resource<List<Account>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            withContext(Dispatchers.IO) {
                repository.getUserAccounts(
                    userId
                )
                    .map { accounts -> accounts.filter { it.accountName != deletedAccount.accountName } }
            }.collect { data ->
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.i(
                TAG,
                "getUserAccountOrderedByName.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}

class GetAccountsCurrencies(
    private val repository: AccountRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<List<String>> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            withContext(Dispatchers.IO) {
                repository.getUserAccounts(
                    userId
                ).map { it.getCurrencies() }
            }.collect { data ->
                emit(data)
            }
        }
    }
}

class GetAccountsTotalBalance(
    private val repository: AccountRepository,
    private val currencyUseCases: CurrencyUseCases,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency

            Log.i(
                TAG,
                "getUserAccountTotalBalance: $currentUser"
            )
            repository.getUserAccounts(userId).collect { accounts ->
                val totalAccountBalance = accounts.getTotalAccountBalance(userCurrency)
                val data = BalanceItem(
                    "Total Balance",
                    totalAccountBalance,
                    userCurrency
                )
                Log.i(
                    TAG,
                    "getUserAccountTotalBalance.data: $data"
                )
                emit(data)
            }
        }
    }

    private suspend fun List<Account>.getTotalAccountBalance(userCurrency: String): Double {
        return this.sumOf { account ->
            currencyUseCases.currencyConverter(
                account.accountAmount,
                account.accountCurrency,
                userCurrency
            )
        }
    }
}