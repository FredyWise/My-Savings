package com.fredy.mysavings.Feature.Domain.UseCases.CSVUseCases

import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Repository.AccountRepository
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.ViewModels.DBInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

data class CSVUseCases(
    val outputToCSV: OutputToCSV,
    val inputFromCSV: InputFromCSV,
    val getDBInfo: GetDBInfo
)

class OutputToCSV(
    private val csvRepository: CSVRepository
) {
    suspend operator fun invoke(
        directory: String, filename: String, trueRecords: List<TrueRecord>,
        delimiter: String = ","
    ) {
        csvRepository.outputToCSV(directory, filename, trueRecords, delimiter)
    }
}

class InputFromCSV(
    private val csvRepository: CSVRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        directory: String,
        delimiter: String = ","
    ): List<TrueRecord> {
        val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
        return csvRepository.inputFromCSV(currentUserId, directory, delimiter)
    }

}

class GetDBInfo(
    private val authRepository: AuthRepository,
    private val recordRepository: RecordRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository

) {
    suspend operator fun invoke(): Flow<DBInfo> {
        return flow {
            val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
            val sumOfRecord = recordRepository.getUserRecords(currentUserId).first()
            val sumOfAccount = accountRepository.getUserAccounts(currentUserId).first().size
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
