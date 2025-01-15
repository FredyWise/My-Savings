package com.fredy.addrecord.domain.usecases.singleAdd


import com.fredy.addrecord.domain.AddRecordRepository
import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.currency.domain.useCases.currencyConverter
import com.fredy.domain.model.TrueRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetRecordById(
    private val recordRepository: AddRecordRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(recordId: String): Flow<TrueRecord> {
        Timber.i("getRecordById: $recordId")
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
            Timber.e(
                "getRecordById.Error: $e"
            )
        }
    }
}