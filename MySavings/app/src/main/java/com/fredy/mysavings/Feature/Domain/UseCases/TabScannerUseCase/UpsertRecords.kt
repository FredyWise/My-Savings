package com.fredy.mysavings.Feature.Domain.UseCases.TabScannerUseCase

import com.fredy.mysavings.Feature.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordState
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpsertRecords(
    val userRepository: UserRepository,
    val recordRepository: RecordRepository,
) {
    operator fun invoke(
        state: AddRecordState,
    ): Flow<Resource<AddRecordState>> {
        return flow {
            Log.i("UpsertRecords: start")
            emit(Resource.Loading())
            val currentUserId = userRepository.getCurrentUser()!!.firebaseUserId

            if (state.records.isNullOrEmpty()) {
                throw Exception("Records is Empty")
            }
            val records = state.records.map {
                val recordId = state.recordId
                val walletIdFromFk = state.walletIdFromFk
                val categoryExpenseIdToFk = state.categoryIdFk
                val categoryIncomeIdToFk = state.categoryIncomeIdFk
                var categoryIdFk = categoryExpenseIdToFk
                val bookIdFk = state.bookIdFk
                val recordDateTime = state.recordDate.atTime(
                    state.recordTime.withNano(
                        (state.recordTime.nano.div(1000000)).times(1000000)
                    )
                )
                var calculationResult = it.recordAmount
                val recordCurrency = state.recordCurrency
                val recordType = it.recordType

                if (recordDateTime == null || calculationResult == 0.0 || recordCurrency.isBlank() || walletIdFromFk == null || walletIdFromFk == null || categoryExpenseIdToFk == null || categoryIncomeIdToFk == null) {
                    Log.e(
                        "UpsertRecords.Error: Please fill all required information"
                    )
                    throw Exception("Please fill all required information")
                } else {
                    when (recordType) {
                        RecordType.Income -> {
                            Log.i("UpsertRecords: income: $it")
                            state.fromWallet.walletAmount += calculationResult
                            categoryIdFk = categoryIncomeIdToFk
                        }

                        RecordType.Expense -> {
                            Log.i("UpsertRecords: expense: $it")
                            if (state.fromWallet.walletAmount < calculationResult) {
                                Log.e(
                                    "UpsertRecords.Error: Account balance is not enough"
                                )
                                throw Exception("Account balance is not enough")
                            }
                            state.fromWallet.walletAmount -= calculationResult
                            calculationResult = -calculationResult
                            categoryIdFk = categoryExpenseIdToFk
                        }

                        RecordType.Transfer -> {}
                    }

                    it.copy(
                        recordId = recordId,
                        walletIdFromFk = walletIdFromFk,
                        walletIdToFk = walletIdFromFk,
                        categoryIdFk = categoryIdFk!!,
                        bookIdFk = bookIdFk,
                        recordTimestamp = TimestampConverter.fromDateTime(recordDateTime),
                        recordAmount = calculationResult,
                        recordCurrency = recordCurrency,
                        recordType = recordType,
                        recordNotes = it.recordNotes,
                    )
                }
            }

            recordRepository.upsertAllRecordItems(records.map { it.copy(userIdFk = currentUserId) })
            Log.i("UpsertRecords: finish")
            emit(Resource.Success(state))
        }.catch { e ->
            Log.e(
                "UpsertRecords.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}