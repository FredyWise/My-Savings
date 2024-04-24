package com.fredy.mysavings

import com.fredy.mysavings.Feature.Domain.Model.Wallet
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Currency
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Model.UserData
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Repository.FakeWalletRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeAuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeBookRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeCSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeCategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeCurrencyRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeRecordRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Presentation.Util.BalanceItem
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.deletedWallet
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.deletedCategory
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.transferCategory
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

    protected lateinit var fakeAccountRepository: FakeWalletRepository
    protected lateinit var fakeCategoryRepository: FakeCategoryRepository
    protected lateinit var fakeBookRepository: FakeBookRepository
    protected lateinit var fakeCurrencyRepository: FakeCurrencyRepository
    protected lateinit var fakeAuthRepository: FakeAuthRepository
    protected lateinit var fakeRecordRepository: FakeRecordRepository
    protected lateinit var fakeCSVRepository: FakeCSVRepository
    protected val currentUserId = "User1"
    protected val bookTestId = "Book1"
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
        Log.setDebuggable(false)
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
        fakeAccountRepository = FakeWalletRepository()

        val accountsToInsert = mutableListOf<Wallet>()
        ('a'..'z').forEachIndexed { index, c ->
            accountsToInsert.add(
                Wallet(
                    walletId = "Account${index + 1}",
                    userIdFk = currentUserId,
                    walletName = "Account $c",
                    walletAmount = (index + 1) * 10000.0,
                    walletCurrency = if (index % 2 == 0) "USD" else "EUR",
                    walletIcon = index,
                    walletIconDescription = "Icon $c"
                )
            )
        }
        accountsToInsert.add(deletedWallet)
        accountsToInsert.shuffle()
        runBlocking {
            accountsToInsert.forEach { fakeAccountRepository.upsertWallet(it) }
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
    fun fakeBookRepositorySetUp() {
        fakeBookRepository = FakeBookRepository()

        val categoriesToInsert = mutableListOf<Book>()
        ('a'..'z').forEachIndexed { index, c ->
            categoriesToInsert.add(
                Book(
                    bookId = "Book${index + 1}",
                    userIdFk = currentUserId,
                    bookName = "Book $c",
                    bookIcon = index,
                    bookIconDescription = "Icon $c"
                )
            )
        }
        categoriesToInsert.shuffle()
        runBlocking {
            categoriesToInsert.forEach { fakeBookRepository.upsertBook(it) }
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
                email = "user1@example.com",
                phone = "+621234567890",
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
            val accounts = fakeAccountRepository.getUserWallets(currentUserId).first()
            val categories = fakeCategoryRepository.getUserCategories(currentUserId).first()
            val recordsToInsert = mutableListOf<Record>()
            val trueRecordToInsert = mutableListOf<TrueRecord>()
            ('a'..'y').forEachIndexed { index, c ->
                val record = Record(
                    recordId = "Record${index + 1}",
                    walletIdFromFk = accounts[index].walletId,
                    walletIdToFk = accounts[index + 1].walletId,
                    categoryIdFk = categories[index + 1].categoryId,
                    userIdFk = currentUserId,
                    bookIdFk = bookTestId,
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
                        accounts.find { it.walletId == record.walletIdFromFk }!!,
                        accounts.find { it.walletId == record.walletIdToFk }!!,
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
            val accounts = fakeAccountRepository.getUserWallets(currentUserId).first()
            val categories = fakeCategoryRepository.getUserCategories(currentUserId).first()
            val trueRecordToInsert = mutableListOf<TrueRecord>()
            ('a'..'y').forEachIndexed { index, c ->
                val record = Record(
                    recordId = "Record${index + 1}",
                    walletIdFromFk = accounts[index].walletId,
                    walletIdToFk = accounts[index + 1].walletId,
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
                        accounts.find { it.walletId == record.walletIdFromFk }!!,
                        accounts.find { it.walletId == record.walletIdToFk }!!,
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