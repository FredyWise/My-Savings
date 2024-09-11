package com.fredy.domain.useCases

import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Book
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.theme.model.BalanceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetUserTotalAmountByTypeFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        book: Book,
    ): Flow<BalanceItem> {
        return flow {
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
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
            }
        }.catch { e ->
            Log.e(
                "getUserTotalAmountByTypeFromSpecificTime.Error: $e"
            )
        }
    }
}