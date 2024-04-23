package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetRecordById(
    private val recordRepository: RecordRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(recordId: String): Flow<TrueRecord> {
        Log.i("getRecordById: $recordId")
        return flow {
            val trueRecord = recordRepository.getRecordById(
                recordId
            )

            emit(
                trueRecord.copy(
                    record = trueRecord.record.copy(
                        recordAmount = currencyUseCases.currencyConverter(
                            trueRecord.record.recordAmount,
                            trueRecord.toWallet.walletCurrency,
                            trueRecord.fromWallet.walletCurrency
                        )
                    )
                )
            )
        }.catch { e ->
            Log.e(
                "getRecordById.Error: $e"
            )
        }
    }
}