package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.domain.model.Book
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Presentation.Util.BalanceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i("getUserTotalRecordBalance: ")
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
                Log.i(
                    "getUserTotalRecordBalance.Data: $data",

                    )
                emit(data)
            }
        }.catch { e ->
            Log.e(
                "getUserTotalRecordBalance.Error: $e"
            )
        }
    }
}