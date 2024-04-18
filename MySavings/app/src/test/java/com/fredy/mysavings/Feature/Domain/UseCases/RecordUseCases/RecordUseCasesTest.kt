package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Util.Mappers.filterRecordCurrency
import com.fredy.mysavings.Util.Mappers.filterTrueRecordCurrency
import com.fredy.mysavings.Util.Resource
import com.google.firebase.Timestamp
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertFailsWith

class RecordUseCasesTest : BaseUseCaseTest() {

    private lateinit var upsertRecordItem: UpsertRecordItem
    private lateinit var deleteRecordItem: DeleteRecordItem
    private lateinit var updateRecordItemWithDeletedAccount: UpdateRecordItemWithDeletedAccount
    private lateinit var updateRecordItemWithDeletedCategory: UpdateRecordItemWithDeletedCategory
    private lateinit var getRecordById: GetRecordById
    private lateinit var getAllTrueRecordsWithinSpecificTime: GetAllTrueRecordsWithinSpecificTime
    private lateinit var getAllRecords: GetAllRecords
    private lateinit var getUserCategoryRecordsOrderedByDateTime: GetUserCategoryRecordsOrderedByDateTime
    private lateinit var getUserAccountRecordsOrderedByDateTime: GetUserAccountRecordsOrderedByDateTime
    private lateinit var getUserTrueRecordMapsFromSpecificTime: GetUserTrueRecordMapsFromSpecificTime
    private lateinit var getUserRecordsFromSpecificTime: GetUserRecordsFromSpecificTime
    private lateinit var getUserCategoriesWithAmountFromSpecificTime: GetUserCategoriesWithAmountFromSpecificTime
    private lateinit var getUserAccountsWithAmountFromSpecificTime: GetUserAccountsWithAmountFromSpecificTime
    private lateinit var getUserTotalAmountByType: GetUserTotalAmountByType
    private lateinit var getUserTotalAmountByTypeFromSpecificTime: GetUserTotalAmountByTypeFromSpecificTime
    private lateinit var getUserTotalRecordBalance: GetUserTotalRecordBalance

    @Before
    fun setUp() {
        upsertRecordItem = UpsertRecordItem(fakeRecordRepository, fakeAuthRepository)
        deleteRecordItem = DeleteRecordItem(fakeRecordRepository)
        updateRecordItemWithDeletedAccount =
            UpdateRecordItemWithDeletedAccount(fakeRecordRepository, fakeAuthRepository)
        updateRecordItemWithDeletedCategory =
            UpdateRecordItemWithDeletedCategory(fakeRecordRepository, fakeAuthRepository)
        getRecordById = GetRecordById(fakeRecordRepository, mockCurrencyUseCases)
        getAllTrueRecordsWithinSpecificTime =
            GetAllTrueRecordsWithinSpecificTime(fakeRecordRepository, fakeAuthRepository)
        getAllRecords = GetAllRecords(fakeRecordRepository, fakeAuthRepository,fakeBookRepository)
        getUserCategoryRecordsOrderedByDateTime =
            GetUserCategoryRecordsOrderedByDateTime(fakeRecordRepository, fakeAuthRepository)
        getUserAccountRecordsOrderedByDateTime =
            GetUserAccountRecordsOrderedByDateTime(fakeRecordRepository, fakeAuthRepository)
        getUserTrueRecordMapsFromSpecificTime = GetUserTrueRecordMapsFromSpecificTime(
            fakeRecordRepository,
            fakeAuthRepository,
            mockCurrencyUseCases,
            fakeBookRepository
        )
        getUserRecordsFromSpecificTime = GetUserRecordsFromSpecificTime(
            fakeRecordRepository,
            fakeAuthRepository,
            mockCurrencyUseCases
        )
        getUserCategoriesWithAmountFromSpecificTime = GetUserCategoriesWithAmountFromSpecificTime(
            fakeRecordRepository,
            fakeCategoryRepository,
            fakeAuthRepository,
            mockCurrencyUseCases
        )
        getUserAccountsWithAmountFromSpecificTime = GetUserAccountsWithAmountFromSpecificTime(
            fakeRecordRepository,
            fakeAccountRepository,
            fakeAuthRepository,
            mockCurrencyUseCases
        )
        getUserTotalAmountByType =
            GetUserTotalAmountByType(fakeRecordRepository, fakeAuthRepository, mockCurrencyUseCases)
        getUserTotalAmountByTypeFromSpecificTime = GetUserTotalAmountByTypeFromSpecificTime(
            fakeRecordRepository,
            fakeAuthRepository,
            mockCurrencyUseCases
        )
        getUserTotalRecordBalance = GetUserTotalRecordBalance(
            fakeRecordRepository,
            fakeAuthRepository,
            mockCurrencyUseCases
        )
    }

    @Test
    fun `Upsert New Record Item`() = runBlocking {
        val recordId = "testing"
        val record = Record(
            recordId = recordId,
            walletIdFromFk = "testAccountId",
            walletIdToFk = "testAccountId",
            categoryIdFk = "testCategoryId",
            userIdFk = currentUserId,
            recordTimestamp = Timestamp.now(),
            recordAmount = 100.0,
            recordCurrency = "USD",
            recordType = RecordType.Expense,
            recordNotes = "Test record notes"
        )

        val result = upsertRecordItem(record)

        assertEquals(recordId, result)
        val insertedRecord = fakeRecordRepository.getRecordById(recordId = recordId).record
        assertEquals(record, insertedRecord)
    }

    @Test
    fun `Upsert Existing Record Item`() = runBlocking {
        val recordId = "testing"
        val oldRecord = Record(
            recordId = recordId,
            walletIdFromFk = "testAccountId",
            walletIdToFk = "testAccountId",
            categoryIdFk = "testCategoryId",
            userIdFk = currentUserId,
            recordTimestamp = Timestamp.now(),
            recordAmount = 100.0,
            recordCurrency = "USD",
            recordType = RecordType.Expense,
            recordNotes = "Test record notes"
        )

        fakeRecordRepository.upsertRecordItem(oldRecord)

        val record = Record(
            recordId = recordId,
            walletIdFromFk = "testAccountId",
            walletIdToFk = "testAccountId",
            categoryIdFk = "testCategoryId",
            userIdFk = currentUserId,
            recordTimestamp = Timestamp.now(),
            recordAmount = 200.0,
            recordCurrency = "USD",
            recordType = RecordType.Expense,
            recordNotes = "Test record notes updated"
        )

        val result = upsertRecordItem(record)

        assertEquals(recordId, result)
        val insertedRecord = fakeRecordRepository.getRecordById(recordId = recordId).record
        assertNotEquals(oldRecord, insertedRecord)
        assertEquals(record, insertedRecord)
    }

    @Test
    fun `Delete Existing Record Item`() {
        runBlocking {
            val recordId = "testing"
            val record = Record(
                recordId = recordId,
                walletIdFromFk = "testAccountIdFrom",
                walletIdToFk = "testAccountIdTo",
                categoryIdFk = "testCategoryId",
                userIdFk = currentUserId,
                recordTimestamp = Timestamp.now(),
                recordAmount = 100.0,
                recordCurrency = "USD",
                recordType = RecordType.Expense,
                recordNotes = "Test record notes"
            )

            fakeRecordRepository.upsertRecordItem(record)

            deleteRecordItem(record)
            assertFailsWith<NullPointerException> {
                fakeRecordRepository.getRecordById(recordId = recordId)
            }
        }
    }

    @Test
    fun `Delete Non-Existent Record Item`() {
        runBlocking {
            val recordId = "testing"
            val record = Record(
                recordId = recordId,
                walletIdFromFk = "testAccountIdFrom",
                walletIdToFk = "testAccountIdTo",
                categoryIdFk = "testCategoryId",
                userIdFk = currentUserId,
                recordTimestamp = Timestamp.now(),
                recordAmount = 100.0,
                recordCurrency = "USD",
                recordType = RecordType.Expense,
                recordNotes = "Test record notes"
            )

            deleteRecordItem(record)
            assertFailsWith<NullPointerException> {
                fakeRecordRepository.getRecordById(recordId = recordId)
            }
        }
    }

    @Test
    fun `Retrieve Existing Record Item`() = runBlocking {
        val recordId = "testing"
        val record = Record(
            recordId = recordId,
            walletIdFromFk = "testAccountIdFrom",
            walletIdToFk = "testAccountIdTo",
            categoryIdFk = "testCategoryId",
            userIdFk = currentUserId,
            recordTimestamp = Timestamp.now(),
            recordAmount = 100.0,
            recordCurrency = "USD",
            recordType = RecordType.Expense,
            recordNotes = "Test record notes"
        )

        fakeRecordRepository.upsertRecordItem(record)

        val retrievedRecord = getRecordById(recordId = recordId).last().record

        assertEquals(record, retrievedRecord)
    }

    @Test
    fun `Retrieve Non-Existent Record Item`() {
        runBlocking {
            val nonExistentRecordId = "nonExistent"

            assertFailsWith<Exception> {
                getRecordById(nonExistentRecordId).first()
            }
        }
    }

    @Test
    fun `Retrieve All True Records Within Specific Time`() = runBlocking {
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()

        val recordsFlow = getAllTrueRecordsWithinSpecificTime(startDate, endDate)
        val recordsResource = recordsFlow.last()

        assertTrue(recordsResource is Resource.Success)
        val records = (recordsResource as Resource.Success).data!!
        assertEquals(
            fakeRecordRepository.getUserTrueRecordsFromSpecificTime(
                currentUserId,
                startDate,
                endDate
            ).first().size, records.size
        )
    }

    @Test
    fun `Retrieve All Records`() = runBlocking {
        val recordsFlow = getAllRecords()
        val recordsResource = recordsFlow.last()

        assertTrue(recordsResource is Resource.Success)
        val records = (recordsResource as Resource.Success).data!!
        assertEquals(
            fakeRecordRepository.getRecordMaps(currentUserId).first().size,
            records.sumOf { it.recordMaps.sumOf { recordMap -> recordMap.records.size } })
    }


    @Test
    fun `Retrieve User Category Records Ordered By DateTime`() = runBlocking {
        val categoryId = "testCategoryId"
        val sortType = SortType.ASCENDING

        val recordsFlow = getUserCategoryRecordsOrderedByDateTime(categoryId, sortType)
        val recordsResource = recordsFlow.last()

        assertTrue(recordsResource is Resource.Success)
        val records = (recordsResource as Resource.Success).data!!
        assertEquals(
            fakeRecordRepository.getUserCategoryRecordsOrderedByDateTime(
                currentUserId,
                categoryId,
                sortType
            ).first().size, records.size
        )
    }

    @Test
    fun `Retrieve User Account Records Ordered By DateTime`() = runBlocking {
        val accountId = "testAccountId"
        val sortType = SortType.ASCENDING

        val recordsFlow = getUserAccountRecordsOrderedByDateTime(accountId, sortType)
        val recordsResource = recordsFlow.last()

        assertTrue(recordsResource is Resource.Success)
        val records = (recordsResource as Resource.Success).data!!
        assertEquals(
            fakeRecordRepository.getUserAccountRecordsOrderedByDateTime(
                currentUserId,
                accountId,
                sortType
            ).first().size, records.sumOf { it.records.size }
        )
    }

    @Test
    fun `Retrieve User True Record Maps From Specific Time`() = runBlocking {
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val sortType = SortType.ASCENDING
        val currency = listOf(currentUserCurrency)
        val useUserCurrency = false
        val book = fakeBookRepository.getBook(bookTestId).first()

        val recordsFlow = getUserTrueRecordMapsFromSpecificTime(
            startDate,
            endDate,
            sortType,
            currency,
            useUserCurrency,
            book
        )
        val recordsResource = recordsFlow.last()

        assertTrue(recordsResource is Resource.Success)
        val recordMaps = (recordsResource as Resource.Success).data!!
        assertEquals(
            fakeRecordRepository.getUserTrueRecordsFromSpecificTime(
                currentUserId,
                startDate,
                endDate
            ).first().filterTrueRecordCurrency(currency + currentUserCurrency).size,
            recordMaps.sumOf { it.recordMaps.size }

        )
    }

    @Test
    fun `Retrieve User Records From Specific Time`() = runBlocking {
        val recordType = RecordType.Expense
        val sortType = SortType.ASCENDING
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val currency = emptyList<String>()
        val useUserCurrency = true
        val book = fakeBookRepository.getBook(bookTestId).first()

        val recordsFlow = getUserRecordsFromSpecificTime(
            recordType,
            sortType,
            startDate,
            endDate,
            currency,
            useUserCurrency,
            book
        )

        val recordsResource = recordsFlow.last()

        assertTrue(recordsResource is Resource.Success)
        val records = (recordsResource as Resource.Success).data!!
        assertEquals(
            fakeRecordRepository.getUserRecordsByTypeFromSpecificTime(
                currentUserId,
                listOf(recordType),
                startDate,
                endDate
            ).map { it.filterRecordCurrency(currency) }.last()
                .distinctBy { it.recordDateTime.toLocalDate() }.size,
            records.size
        )
    }

    @Test
    fun `Retrieve User Categories With Amount From Specific Time`() = runBlocking {
        val categoryType = RecordType.Expense
        val sortType = SortType.ASCENDING
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val currency = listOf("USD")
        val useUserCurrency = true
        val book = fakeBookRepository.getBook(bookTestId).first()

        val categoriesFlow = getUserCategoriesWithAmountFromSpecificTime(
            categoryType,
            sortType,
            startDate,
            endDate,
            currency,
            useUserCurrency,
            book
        )
        val categoriesResource = categoriesFlow.last()

        assertTrue(categoriesResource is Resource.Success)
        val categories = (categoriesResource as Resource.Success).data!!
        assertEquals(
            fakeRecordRepository.getUserRecordsByTypeFromSpecificTime(
                currentUserId,
                listOf(categoryType),
                startDate,
                endDate
            ).first().size, categories.size
        )
    }

    @Test
    fun `Retrieve User Accounts With Amount From Specific Time`() = runBlocking {
        val sortType = SortType.ASCENDING
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val useUserCurrency = false
        val book = fakeBookRepository.getBook(bookTestId).first()

        val accountsFlow =
            getUserAccountsWithAmountFromSpecificTime(
                sortType,
                startDate,
                endDate,
                useUserCurrency,
                book
            )
        val accountsResource = accountsFlow.last()

        assertTrue(accountsResource is Resource.Success)
        val accounts = (accountsResource as Resource.Success).data!!
        assertEquals(
            fakeAccountRepository.getUserWallets(
                currentUserId,
            ).last().size, accounts.size
        )
    }

    @Test
    fun `Retrieve User Total Amount By Type`() = runBlocking {
        val recordType = RecordType.Expense

        val balanceFlow = getUserTotalAmountByType(recordType)
        val balance = balanceFlow.last()

        assertEquals(
            fakeRecordRepository.getUserRecordsByType(currentUserId, recordType).first()
                .sumOf { it.recordAmount }, balance.amount
        )
    }

    @Test
    fun `Retrieve User Total Amount By Type From Specific Time`() = runBlocking {
        val recordType = RecordType.Expense
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val book = fakeBookRepository.getBook(bookTestId).first()

        val balanceFlow =
            getUserTotalAmountByTypeFromSpecificTime(recordType, startDate, endDate, book)
        val balance = balanceFlow.last()

        assertEquals(
            fakeRecordRepository.getUserRecordsByTypeFromSpecificTime(
                currentUserId,
                listOf(recordType),
                startDate,
                endDate
            ).first().sumOf { it.recordAmount }, balance.amount
        )
    }

    @Test
    fun `Retrieve User Total Record Balance`() =
        runBlocking {
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val book = fakeBookRepository.getBook(bookTestId).first()

            val balanceFlow = getUserTotalRecordBalance(false, startDate, endDate, book)
            val balance = balanceFlow.last()

            assertEquals(
                fakeRecordRepository.getUserRecordsByTypeFromSpecificTime(
                    currentUserId,
                    listOf(RecordType.Expense, RecordType.Income),
                    startDate,
                    endDate
                ).first().sumOf { it.recordAmount }, balance.amount
            )

        }
}


