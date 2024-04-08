package com.fredy.mysavings

import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.Currency
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Repository.FakeAccountRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeAuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeCSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeCategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeCurrencyRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeRecordRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.DefaultData
import com.fredy.mysavings.Util.DefaultData.deletedAccount
import com.fredy.mysavings.Util.DefaultData.deletedCategory
import com.fredy.mysavings.Util.DefaultData.transferCategory
import com.fredy.mysavings.Util.Log
import com.google.firebase.Timestamp
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.util.Date
import kotlin.random.Random

abstract class BaseUseCaseTest {

    protected lateinit var fakeAccountRepository: FakeAccountRepository
    protected lateinit var fakeCategoryRepository: FakeCategoryRepository
    protected lateinit var fakeCurrencyRepository: FakeCurrencyRepository
    protected lateinit var fakeAuthRepository: FakeAuthRepository
    protected lateinit var fakeRecordRepository: FakeRecordRepository
    protected lateinit var fakeCSVRepository: FakeCSVRepository
    protected val currentUserId = "User1"
    protected val currentUserCurrency = "USD"
    protected var mockCurrencyUseCases = mockkClass(CurrencyUseCases::class)

    private fun randomTimestamp(): Timestamp {
        val now = Date()
        val sevenDaysInMillis: Long = 7 * 24 * 60 * 60 * 1000
        val randomMillis = Random.nextLong(-sevenDaysInMillis, sevenDaysInMillis)
        return Timestamp(Date(now.time + randomMillis))
    }

    @Before
    fun init() {
        Log
        DefaultData
        MockKAnnotations.init(this)
        coEvery { mockCurrencyUseCases.convertCurrencyData(any(), any(), any()) } answers {
            val amount = firstArg<Double>()
            val currency = thirdArg<String>()
            BalanceItem(amount = amount, currency = currency)
        }
    }

    @Before
    fun fakeAccountRepositorySetUp() {
        fakeAccountRepository = FakeAccountRepository()

        val accountsToInsert = mutableListOf<Account>()
        ('a'..'z').forEachIndexed { index, c ->
            accountsToInsert.add(
                Account(
                    accountId = "Account${index + 1}",
                    userIdFk = currentUserId,
                    accountName = "Account $c",
                    accountAmount = (index + 1) * 10000.0,
                    accountCurrency = if (index % 2 == 0) "USD" else "EUR",
                    accountIcon = index,
                    accountIconDescription = "Icon $c"
                )
            )
        }
        accountsToInsert.add(deletedAccount)
        accountsToInsert.shuffle()
        runBlocking {
            accountsToInsert.forEach { fakeAccountRepository.upsertAccount(it) }
        }
    }

    @Before
    fun fakeCategoryRepositorySetUp() {
        fakeCategoryRepository = FakeCategoryRepository()

        val categoriesToInsert = mutableListOf<Category>()
        ('a'..'z').forEachIndexed { index, c ->
            categoriesToInsert.add(
                Category(
                    categoryId = "Category${index + 1}",
                    userIdFk = currentUserId,
                    categoryName = "Category $c",
                    categoryType = if (index % 2 == 0) RecordType.Expense else RecordType.Income,
                    categoryIcon = index,
                    categoryIconDescription = "Icon $c"
                )
            )
        }
        categoriesToInsert.add(deletedCategory)
        categoriesToInsert.add(transferCategory)
        categoriesToInsert.shuffle()
        runBlocking {
            categoriesToInsert.forEach { fakeCategoryRepository.upsertCategory(it) }
        }
    }

    @Before
    fun fakeCurrencyRepositorySetUp() {
        fakeCurrencyRepository = FakeCurrencyRepository()

        val currenciesToInsert = mutableListOf<Currency>()
        ('a'..'z').forEachIndexed { index, c ->
            currenciesToInsert.add(
                Currency(
                    currencyId = "Currency${index + 1}",
                    userIdFk = currentUserId,
                    name = "Currency $c",
                    symbol = "$c",
                    value = (index + 1) * 10.0,
                    url = "",
                    alt = "Alt $c"
                )
            )
        }
        currenciesToInsert.shuffle()
        runBlocking {
            currenciesToInsert.forEach { fakeCurrencyRepository.updateCurrency(it) }
        }
    }

    @Before
    fun fakeAuthRepositorySetUp() {
        fakeAuthRepository = FakeAuthRepository()

        val usersToInsert = mutableListOf<UserData>()

        usersToInsert.add(
            UserData(
                firebaseUserId = currentUserId,
                username = "Username 1",
                emailOrPhone = "user1@example.com",
                profilePictureUrl = "null",
                userCurrency = currentUserCurrency,
            )
        )
        runBlocking {
            usersToInsert.forEach { fakeAuthRepository.users.add(it) }
        }
    }

    @Before
    fun fakeRecordRepositorySetUp() {
        runBlocking {
            fakeRecordRepository = FakeRecordRepository()
            val accounts = fakeAccountRepository.getUserAccounts(currentUserId).first()
            val categories = fakeCategoryRepository.getUserCategories(currentUserId).first()
            val recordsToInsert = mutableListOf<Record>()
            val trueRecordToInsert = mutableListOf<TrueRecord>()
            ('a'..'y').forEachIndexed { index, c ->
                val record = Record(
                    recordId = "Record${index + 1}",
                    accountIdFromFk = accounts[index].accountId,
                    accountIdToFk = accounts[index + 1].accountId,
                    categoryIdFk = categories[index + 1].categoryId,
                    userIdFk = currentUserId,
                    recordTimestamp = randomTimestamp(),
                    recordAmount = (index + 1) * 1000.0,
                    recordCurrency = if (index % 2 == 0) "USD" else "EUR",
                    recordType = if (index % 2 == 0) RecordType.Expense else RecordType.Income,
                    recordNotes = "Record $c"
                )
                recordsToInsert.add(
                    record
                )
                trueRecordToInsert.add(
                    TrueRecord(
                        record,
                        accounts.find { it.accountId == record.accountIdFromFk }!!,
                        accounts.find { it.accountId == record.accountIdToFk }!!,
                        categories.find { it.categoryId == record.categoryIdFk }!!
                    )
                )
            }
            recordsToInsert.forEach { fakeRecordRepository.upsertRecordItem(it) }
        }
    }

    @Before
    fun fakeCSVRepositorySetUp() {
        runBlocking {
            fakeCSVRepository = FakeCSVRepository()
            val accounts = fakeAccountRepository.getUserAccounts(currentUserId).first()
            val categories = fakeCategoryRepository.getUserCategories(currentUserId).first()
            val trueRecordToInsert = mutableListOf<TrueRecord>()
            ('a'..'y').forEachIndexed { index, c ->
                val record = Record(
                    recordId = "Record${index + 1}",
                    accountIdFromFk = accounts[index].accountId,
                    accountIdToFk = accounts[index + 1].accountId,
                    categoryIdFk = categories[index + 1].categoryId,
                    userIdFk = currentUserId,
                    recordTimestamp = randomTimestamp(),
                    recordAmount = (index + 1) * 1000.0,
                    recordCurrency = if (index % 2 == 0) "USD" else "EUR",
                    recordType = if (index % 2 == 0) RecordType.Expense else RecordType.Income,
                    recordNotes = "Record $c"
                )
                trueRecordToInsert.add(
                    TrueRecord(
                        record,
                        accounts.find { it.accountId == record.accountIdFromFk }!!,
                        accounts.find { it.accountId == record.accountIdToFk }!!,
                        categories.find { it.categoryId == record.categoryIdFk }!!
                    )
                )
            }
            runBlocking {
                fakeCSVRepository.outputToCSV(
                    "directory",
                    "filename",
                    trueRecordToInsert,
                    ","
                )
            }
        }
    }

    @After
    fun cleanup() {
        clearAllMocks()
    }
}