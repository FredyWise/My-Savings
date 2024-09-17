package com.fredy.domain.useCases.RecordUseCases

import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Book
import com.fredy.domain.modelUI.BalanceItem
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.theme.util.minDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class GetUserTotalRecordBalance(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(
        isCaryOn: Boolean,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        book: Book,
    ): Flow<BalanceItem> {
        return flow {
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
                val userCurrency = currentUser.userCurrency
                Timber.i("getUserTotalRecordBalance: ")
                recordRepository.getUserRecordsByTypeFromSpecificTime(
                    userId,
                    listOf(RecordType.Expense, RecordType.Income),
                    if (isCaryOn) minDate else startDate,
                    endDate
                ).map { records ->
                    records.filter { it.bookIdFk == book.bookId }
                        .getTotalRecordBalance(currencyUseCases, userCurrency)
                }.collect { recordTotalAmount ->
                    val data = BalanceItem(
                        name = "Balance: ",
                        amount = recordTotalAmount,
                        currency = userCurrency
                    )
                    Timber.i(
                        "getUserTotalRecordBalance.Data: $data",

                        )
                    emit(data)
                }
            }
        }.catch { e ->
            Timber.e(
                "getUserTotalRecordBalance.Error: $e"
            )
        }
    }
}