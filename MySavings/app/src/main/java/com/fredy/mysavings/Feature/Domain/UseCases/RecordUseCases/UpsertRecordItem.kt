package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordState
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlin.math.absoluteValue

class UpsertRecordItem(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(state: AddRecordState): Flow<Resource<AddRecordState>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            val recordId = state.recordId
            val accountIdFromFk = state.walletIdFromFk
            var accountIdToFk = state.walletIdToFk
            var categoryIdToFk = state.categoryIdFk
            val bookIdFk = state.bookIdFk
            val recordDateTime = state.recordDate.atTime(
                state.recordTime.withNano(
                    (state.recordTime.nano.div(1000000)).times(1000000)
                )
            )
            var calculationResult = state.recordAmount.absoluteValue
            val recordCurrency = state.recordCurrency
            val fromAccountCurrency = state.fromWallet.walletCurrency
            val toAccountCurrency = state.toWallet.walletCurrency
            val recordType = state.recordType
            val recordNotes = state.recordNotes
            val previousAmount = state.previousAmount.absoluteValue
            var difference = 0.0

            if (recordId == "") {
                difference = calculationResult
            } else {
                difference = state.recordAmount.absoluteValue
                difference -= calculationResult
                difference = -difference
            }

            if (isTransfer(recordType)) {
                categoryIdToFk = DefaultData.transferCategory.categoryId
            } else {
                accountIdToFk = accountIdFromFk
            }

            if (recordDateTime == null || calculationResult == 0.0 || recordCurrency.isBlank() || accountIdFromFk == null || accountIdToFk == null || categoryIdToFk == null) {
                throw Exception("Please fill all required information")
            }

            when (recordType) {
                RecordType.Income -> {
                    state.fromWallet.walletAmount += difference
                }

                RecordType.Expense -> {
                    if (state.fromWallet.walletAmount < difference) {
                        throw Exception("Account balance is not enough")

                    }
                    state.fromWallet.walletAmount -= difference
                    calculationResult = -calculationResult
                }

                RecordType.Transfer -> {
                    if (state.fromWallet == state.toWallet) {
                        throw Exception("You Can't transfer into the same account")
                    }

                    if (fromAccountCurrency == toAccountCurrency) {
                        if ((state.fromWallet.walletAmount < difference)) {
                            throw Exception("Account balance is not enough")
                        }
                        state.fromWallet.walletAmount -= difference
                        state.toWallet.walletAmount += difference
                    } else {
                        if (!state.isAgreeToConvert) {
                            if (state.fromWallet.walletAmount < difference) {
                                throw Exception("Account balance is not enough")
                            }
                            emit(Resource.Success(state.copy(isShowWarning = true, previousAmount = calculationResult)))
                            throw Exception("Account Currencies Are not The same!!!, " + "Are you sure want to Transfer from $fromAccountCurrency Currency to ${toAccountCurrency} Currency? \n(Result Will be Converted)")

                        }
                        state.fromWallet.walletAmount -= previousAmount
                        state.toWallet.walletAmount += difference
                    }
                }
            }


            val record = Record(
                recordId = recordId,
                accountIdFromFk = accountIdFromFk,
                accountIdToFk = accountIdToFk,
                categoryIdFk = categoryIdToFk,
                bookId = bookIdFk,
                recordDateTime = recordDateTime,
                recordAmount = calculationResult,
                recordCurrency = recordCurrency,
                recordType = recordType,
                recordNotes = recordNotes,
            )

            recordRepository.upsertRecordItem(
                record.copy(
                    userIdFk = currentUserId,
                    categoryIdFk = if (record.categoryIdFk == DefaultData.transferCategory.categoryId) record.categoryIdFk + currentUserId else record.categoryIdFk
                )
            )
            emit(Resource.Success(state))
        }.catch { e ->
            Log.e(
                "UpsertRecordItem.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}