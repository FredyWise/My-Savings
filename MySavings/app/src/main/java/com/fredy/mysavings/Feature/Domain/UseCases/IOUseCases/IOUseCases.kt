package com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases

import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.Feature.Presentation.ViewModels.DBInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

data class IOUseCases(
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
        delimiter: String = ",",
        book: Book
    ): List<TrueRecord> {
        val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
        return csvRepository.inputFromCSV(currentUserId, directory, delimiter, book)
    }

}

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
