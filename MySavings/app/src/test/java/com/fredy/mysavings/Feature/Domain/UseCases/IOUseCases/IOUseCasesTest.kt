package com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases

import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Util.isTransfer
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class IOUseCasesTest:BaseUseCaseTest() {

    private lateinit var outputToCSV: OutputToCSV
    private lateinit var inputFromCSV: InputFromCSV
    private lateinit var getDBInfo: GetDBInfo

    @Before
    fun setUp() {
        outputToCSV = OutputToCSV(fakeCSVRepository)
        inputFromCSV = InputFromCSV(fakeCSVRepository, fakeAuthRepository)
        getDBInfo = GetDBInfo(fakeAuthRepository, fakeRecordRepository, fakeAccountRepository, fakeCategoryRepository)
    }

    @Test
    fun `test output to CSV`() = runBlocking {
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val directory = "testDirectory"
        val filename = "testFile"
        val trueRecords = fakeRecordRepository.getUserTrueRecordsFromSpecificTime(currentUserId,startDate,endDate).last()
        val delimiter = ","

        outputToCSV(directory, filename, trueRecords, delimiter)

        assertEquals(trueRecords,fakeCSVRepository.inputFromCSV(currentUserId,directory+filename,delimiter))
    }

    @Test
    fun `test input from CSV`() = runBlocking {
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val filename = "testFile"
        val directory = "testDirectory"
        val delimiter = ","

        val trueRecords = fakeRecordRepository.getUserTrueRecordsFromSpecificTime(currentUserId,startDate,endDate).last()
        fakeCSVRepository.outputToCSV(directory, filename, trueRecords, delimiter)

        val result = inputFromCSV(directory+filename, delimiter, Book(bookTestId))

        assertEquals(trueRecords, result)
    }

    @Test
    fun `test get DB info`() = runBlocking {
        val dbInfo = getDBInfo().first()

        val sumOfRecord = fakeRecordRepository.getUserRecords(currentUserId).first()
        val sumOfAccount = fakeAccountRepository.getUserWallets(currentUserId).first().size
        val sumOfCategory = fakeCategoryRepository.getUserCategories(currentUserId).first().size
        val sumOfExpense =
            sumOfRecord.sumOf { (if (isExpense(it.recordType)) 1 else 0).toInt() }
        val sumOfIncome = sumOfRecord.sumOf { (if (isIncome(it.recordType)) 1 else 0).toInt() }
        val sumOfTransfer =
            sumOfRecord.sumOf { (if (isTransfer(it.recordType)) 1 else 0).toInt() }

        assertEquals(sumOfRecord.size, dbInfo.sumOfRecords)
        assertEquals(sumOfAccount, dbInfo.sumOfAccounts)
        assertEquals(sumOfCategory, dbInfo.sumOfCategories)
        assertEquals(sumOfExpense, dbInfo.sumOfExpense)
        assertEquals(sumOfIncome, dbInfo.sumOfIncome)
        assertEquals(sumOfTransfer, dbInfo.sumOfTransfer)
    }
}
