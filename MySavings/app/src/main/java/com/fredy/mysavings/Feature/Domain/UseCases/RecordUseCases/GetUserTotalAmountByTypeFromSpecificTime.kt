package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetUserTotalAmountByTypeFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        book: Book,
    ): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i("getUserTotalAmountByTypeFromSpecificTime: $recordType")

            recordRepository.getUserRecordsByTypeFromSpecificTime(
                userId,
                listOf(recordType),
                startDate,
                endDate
            ).map {
                it.filter { it.bookIdFk == book.bookId }
                    .getTotalRecordBalance(currencyUseCases, userCurrency)
            }.collect { recordTotalAmount ->
                val data = BalanceItem(
                    name = "${recordType.name}: ",
                    amount = recordTotalAmount,
                    currency = userCurrency
                )
                Log.i(
                    "getUserTotalAmountByTypeFromSpecificTime.Data: $data"
                )
                emit(data)
            }
        }.catch { e ->
            Log.e(
                "getUserTotalAmountByTypeFromSpecificTime.Error: $e"
            )
        }
    }
}