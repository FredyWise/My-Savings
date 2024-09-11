package com.fredy.addrecord.domain.usecases.singleAdd

import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.currencyConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetRecordById(
    private val recordRepository: RecordRepository,
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