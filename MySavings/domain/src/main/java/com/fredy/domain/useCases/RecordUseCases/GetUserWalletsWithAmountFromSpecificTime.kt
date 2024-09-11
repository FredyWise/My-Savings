package com.fredy.domain.useCases.RecordUseCases

import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.AccountWithAmountType
import com.fredy.domain.model.Book
import com.fredy.domain.model.Record
import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import com.fredy.mysavings.Feature.Presentation.Util.isExpense
import com.fredy.mysavings.Feature.Presentation.Util.isIncome
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class GetUserWalletsWithAmountFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases,
) {
    operator fun invoke(
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        useUserCurrency: Boolean,
        book: Book
    ): Flow<Resource<List<AccountWithAmountType>, DataError.Local>> {
        return flow<Resource<List<AccountWithAmountType>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
                val userCurrency = currentUser.userCurrency
                Timber.i(
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
                    Timber.i(
                        "getUserAccountsWithAmountFromSpecificTime.Data: $data",

                        )
                    emit(Resource.Success(data))
                }

            }
        }.catch { e ->
            Timber.e(
                "getUserAccountsWithAmountFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
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