package com.fredy.addrecord.domain

import com.fredy.addrecord.viewModel.AddRecordState
import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource
import com.fredy.domain.enums.RecordType
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class UpsertRecords(
    val userRepository: UserRepository,
    val recordRepository: RecordRepository,
) {
    operator fun invoke(
        state: AddRecordState,
    ): Flow<Resource<AddRecordState, DataError.Local>> {
        return flow<Resource<AddRecordState, DataError.Local>> {
            Timber.i("UpsertRecords: start")
            emit(Resource.Loading())
            val currentUserId = userRepository.getCurrentUser()!!.firebaseUserId
            val tempRecords = state.records

            if (tempRecords.isNullOrEmpty()) {
                throw Exception("Records is Empty")
            }
            val records = tempRecords.map {
                val recordId = state.recordId
                val walletIdFromFk = state.walletIdFromFk
                val walletIdToFk = state.walletIdToFk
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

                if (recordDateTime == null || calculationResult == 0.0 || recordCurrency.isBlank() || walletIdFromFk == null || walletIdToFk == null || categoryExpenseIdToFk == null || categoryIncomeIdToFk == null) {
                    Timber.e(
                        "UpsertRecords.Error: Please fill all required information"
                    )
                    throw Exception("Please fill all required information")
                } else {
                    when (recordType) {
                        RecordType.Income -> {
                            Timber.i("UpsertRecords: income: $it")
                            state.fromWallet.walletAmount += calculationResult
                            categoryIdFk = categoryIncomeIdToFk
                        }

                        RecordType.Expense -> {
                            Timber.i("UpsertRecords: expense: $it")
                            if (state.fromWallet.walletAmount < calculationResult) {
                                Timber.e(
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
                        recordDateTime = recordDateTime,
                        recordAmount = calculationResult,
                        recordCurrency = recordCurrency,
                        recordType = recordType,
                        recordNotes = it.recordNotes,
                    )
                }
            }

            recordRepository.upsertAllRecordItems(records.map { it.copy(userIdFk = currentUserId) })
            Timber.i("UpsertRecords: finish")
            emit(Resource.Success(state))
        }.catch { e ->
            Timber.e(
                "UpsertRecords.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}