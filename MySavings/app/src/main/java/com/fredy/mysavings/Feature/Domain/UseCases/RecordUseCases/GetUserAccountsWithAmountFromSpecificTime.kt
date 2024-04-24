package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.AccountWithAmountType
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Presentation.Util.isExpense
import com.fredy.mysavings.Feature.Presentation.Util.isIncome
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetUserAccountsWithAmountFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val walletRepository: WalletRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases,
) {
    operator fun invoke(
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        useUserCurrency: Boolean,
        book: Book
    ): Flow<Resource<List<AccountWithAmountType>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                "getUserAccountsWithAmountFromSpecificTime: \n$startDate\n:\n$endDate"
            )
            val userAccounts = walletRepository.getUserWallets(
                userId
            ).first()

            recordRepository.getUserRecordsFromSpecificTime(
                userId, startDate, endDate
            ).map { records ->
                records.filter { it.bookIdFk == book.bookId }
                    .toAccountWithAmount(
                        sortType,
                        userId,
                        userAccounts,
                        userCurrency,
                        useUserCurrency
                    )
            }.collect { data ->
                Log.i(
                    "getUserAccountsWithAmountFromSpecificTime.Data: $data",

                    )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserAccountsWithAmountFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun List<Record>.toAccountWithAmount(
        sortType: SortType = SortType.DESCENDING,
        userId: String,
        userWallets: List<Wallet>,
        userCurrency: String,
        useUserCurrency: Boolean,
    ): List<AccountWithAmountType> {
        val accountWithAmountMap = mutableMapOf<String, AccountWithAmountType>()
        userWallets.forEach { account ->
            if (account.walletId == DefaultData.deletedWallet.walletId + userId && account.walletAmount == 0.0) {
                return@forEach
            }
            val currency = if (useUserCurrency) userCurrency else account.walletCurrency
            val key = account.walletId
            val newAccount = AccountWithAmountType(
                wallet = account.copy(walletCurrency = currency),
                incomeAmount = 0.0,
                expenseAmount = 0.0,
            )
            accountWithAmountMap[key] = newAccount
        }
        this.forEach { record ->
            val account = userWallets.first { it.walletId == record.walletIdFromFk }
            val key = record.walletIdFromFk

            val existingAccount = accountWithAmountMap[key]
            if (!isTransfer(record.recordType)) {
                val amount = if (useUserCurrency) {
                    currencyUseCases.currencyConverter(
                        record.recordAmount,
                        record.recordCurrency,
                        userCurrency
                    )
                } else {
                    record.recordAmount
                }
                val incomeAmount = if (isIncome(record.recordType)) amount else 0.0
                val expenseAmount = if (isExpense(record.recordType)) amount else 0.0
                if (existingAccount != null) {
                    val currency =
                        if (useUserCurrency) userCurrency else existingAccount.wallet.walletCurrency
                    accountWithAmountMap[key] = existingAccount.copy(
                        wallet = existingAccount.wallet.copy(walletCurrency = currency),
                        incomeAmount = existingAccount.incomeAmount + incomeAmount,
                        expenseAmount = existingAccount.expenseAmount + expenseAmount,
                    )

                } else {
                    val currency = if (useUserCurrency) userCurrency else account.walletCurrency
                    val newAccount = AccountWithAmountType(
                        wallet = account.copy(walletCurrency = currency),
                        incomeAmount = incomeAmount,
                        expenseAmount = expenseAmount,
                    )
                    accountWithAmountMap[key] = newAccount
                }
            }

        }

        val data = accountWithAmountMap.values.toList().let { value ->
            if (sortType == SortType.ASCENDING) {
                value.sortedBy { it.wallet.walletName }
            } else {
                value.sortedByDescending { it.wallet.walletName }
            }
        }
        return data
    }
}