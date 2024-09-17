package com.fredy.domain.useCases.WalletUseCases

import com.fredy.domain.model.Wallet
import com.fredy.domain.modelUI.BalanceItem
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.currencyConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetWalletsTotalBalance(
    private val repository: WalletRepository,
    private val currencyUseCases: CurrencyUseCases,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<BalanceItem> {
        return flow {
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
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