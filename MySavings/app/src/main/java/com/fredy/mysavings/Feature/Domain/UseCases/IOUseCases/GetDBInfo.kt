package com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases

import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Presentation.ViewModels.IOViewModel.DBInfo
import com.fredy.mysavings.Feature.Presentation.Util.isExpense
import com.fredy.mysavings.Feature.Presentation.Util.isIncome
import com.fredy.mysavings.Feature.Presentation.Util.isTransfer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetDBInfo(
    private val authRepository: AuthRepository,
    private val recordRepository: RecordRepository,
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository

) {
    suspend operator fun invoke(): Flow<DBInfo> {
        return flow {
            val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
            val sumOfRecord = recordRepository.getUserRecords(currentUserId).first()
            val sumOfAccount = walletRepository.getUserWallets(currentUserId).first().size
            val sumOfCategory = categoryRepository.getUserCategories(currentUserId).first().size
            val sumOfExpense =
                sumOfRecord.sumOf { (if (isExpense(it.recordType)) 1 else 0).toInt() }
            val sumOfIncome = sumOfRecord.sumOf { (if (isIncome(it.recordType)) 1 else 0).toInt() }
            val sumOfTransfer =
                sumOfRecord.sumOf { (if (isTransfer(it.recordType)) 1 else 0).toInt() }

            emit(
                DBInfo(
                    sumOfRecords = sumOfRecord.size,
                    sumOfAccounts = sumOfAccount,
                    sumOfCategories = sumOfCategory,
                    sumOfExpense = sumOfExpense,
                    sumOfIncome = sumOfIncome,
                    sumOfTransfer = sumOfTransfer
                )
            )
        }
    }
}