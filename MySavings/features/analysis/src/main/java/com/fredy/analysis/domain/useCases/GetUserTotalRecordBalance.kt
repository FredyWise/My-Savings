package com.fredy.analysis.domain.useCases


import com.fredy.analysis.domain.AnalysisRepository
import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.currency.domain.useCases.getTotalRecordBalance
import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Book
import com.fredy.domain.modelUI.BalanceItem
import com.fredy.domain.repository.UserRepository

import com.fredy.theme.util.minDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class GetUserTotalRecordBalance(
    private val analysisRepository: AnalysisRepository,
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
                analysisRepository.getUserRecordsByTypeFromSpecificTime(
                    userId,
                    listOf(RecordType.Expense, RecordType.Income),
                    if (isCaryOn) minDate else startDate,
                    endDate
                ).map { records ->
                    currencyUseCases.getTotalRecordBalance(
                        records.filter { it.bookIdFk == book.bookId },
                        userCurrency
                    )
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