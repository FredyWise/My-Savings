package com.fredy.mysavings

import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.Currency
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Repository.FakeAccountRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeAuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeCategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.FakeCurrencyRepository
import com.fredy.mysavings.Util.DefaultData
import com.fredy.mysavings.Util.DefaultData.TAG
import com.fredy.mysavings.Util.DefaultData.deletedAccount
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before

abstract class BaseTest {

    protected lateinit var fakeAccountRepository: FakeAccountRepository
    protected lateinit var fakeCategoryRepository: FakeCategoryRepository
    protected lateinit var fakeCurrencyRepository: FakeCurrencyRepository
    protected lateinit var fakeAuthRepository: FakeAuthRepository
    protected val currentUser = "User1"

    @Before
    fun init() {
        DefaultData
        MockKAnnotations.init(this)
    }

    @Before
    fun fakeAccountRepositorySetUp() {
        fakeAccountRepository = FakeAccountRepository()

        val accountsToInsert = mutableListOf<Account>()
        ('a'..'z').forEachIndexed { index, c ->
            accountsToInsert.add(
                Account(
                    accountId = "Account${index + 1}",
                    userIdFk = currentUser,
                    accountName = "Account $c",
                    accountAmount = (index + 1) * 100.0,
                    accountCurrency = if (index % 2 == 0) "USD" else "EUR",
                    accountIcon = index,
                    accountIconDescription = "Icon $c"
                )
            )
        }
//        accountsToInsert.add(deletedAccount)
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
                    userIdFk = currentUser,
                    categoryName = "Category $c",
                    categoryType = if (index % 2 == 0) RecordType.Expense else RecordType.Income,
                    categoryIcon = index,
                    categoryIconDescription = "Icon $c"
                )
            )
        }
//        categoriesToInsert.add(deletedCategory)
//        categoriesToInsert.add(transferCategory)
//        categoriesToInsert.shuffle()
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
                    userIdFk = currentUser,
                    name = "Currency $c",
                    symbol = "$c",
                    value = (index + 1) * 10.0,
                    url = "",
                    alt = "Alt $c"
                )
            )
        }
//        currenciesToInsert.shuffle()
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
                firebaseUserId = currentUser,
                username = "Username 1",
                emailOrPhone = "user1@example.com",
                profilePictureUrl = "null",
                userCurrency = "USD",
            )
        )
//        usersToInsert.shuffle()
        runBlocking {
            usersToInsert.forEach { fakeAuthRepository.users.add(it) }
        }
    }

    @After
    fun cleanup() {
        clearAllMocks()
    }
}

