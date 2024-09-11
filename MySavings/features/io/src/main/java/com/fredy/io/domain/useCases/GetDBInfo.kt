package com.fredy.io.domain.useCases

import com.fredy.domain.enumsChecker.isExpense
import com.fredy.domain.enumsChecker.isIncome
import com.fredy.domain.enumsChecker.isTransfer
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.io.domain.model.DBInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetDBInfo(
    private val userRepository: UserRepository,
    private val recordRepository: RecordRepository,
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository

) {
    suspend operator fun invoke(): Flow<DBInfo> {
        return flow {
            val currentUserId = userRepository.getCurrentUser()!!.firebaseUserId
            val sumOfRecord = recordRepository.getUserRecords(currentUserId).first()
            val sumOfAccount = walletRepository.getUserWallets(currentUserId).first().size
            val sumOfCategory = categoryRepository.getUserCategories(currentUserId).first().size
            val sumOfExpense =
                sumOfRecord.sumOf { (if (it.recordType.isExpense()) 1 else 0).toInt() }
            val sumOfIncome = sumOfRecord.sumOf { (if (it.recordType.isIncome()) 1 else 0).toInt() }
            val sumOfTransfer =
                sumOfRecord.sumOf { (if (it.recordType.isTransfer()) 1 else 0).toInt() }

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