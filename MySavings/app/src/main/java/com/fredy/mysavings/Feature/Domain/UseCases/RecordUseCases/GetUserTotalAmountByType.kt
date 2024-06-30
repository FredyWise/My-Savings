package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Presentation.Util.BalanceItem
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetUserTotalAmountByType(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(recordType: RecordType): Flow<BalanceItem> {
        return flow {
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
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
                Log.i(
                    "getUserTotalAmountByType.Result: $data",

                    )
                emit(data)
            }
        }.catch { e ->
            Log.e(
                "getUserTotalAmountByType.Error: $e"
            )
        }
    }
}