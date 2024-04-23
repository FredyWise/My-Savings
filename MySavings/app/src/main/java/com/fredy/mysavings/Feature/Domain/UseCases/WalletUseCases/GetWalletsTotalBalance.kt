package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Util.BalanceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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