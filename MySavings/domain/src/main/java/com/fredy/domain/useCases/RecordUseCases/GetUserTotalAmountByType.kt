package com.fredy.domain.useCases.RecordUseCases

import com.fredy.domain.enums.RecordType
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.getTotalRecordBalance
import com.fredy.theme.model.BalanceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class GetUserTotalAmountByType(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(recordType: RecordType): Flow<BalanceItem> {
        return flow {
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser != null) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Timber.i(
                "getUserTotalAmountByType: $recordType",

                )
            recordRepository.getUserRecordsByType(
                userId, recordType
            ).map {
                it.getTotalRecordBalance(currencyUseCases, userCurrency)
            }.collect { recordTotalAmount ->
                val data = BalanceItem(
                    name = "${recordType.name}: ",
                    amount = recordTotalAmount,
                    currency = userCurrency
                )
                Timber.i(
                    "getUserTotalAmountByType.Result: $data",

                    )
                emit(data)
            }
        }.catch { e ->
            Timber.e(
                "getUserTotalAmountByType.Error: $e"
            )
        }
    }
}