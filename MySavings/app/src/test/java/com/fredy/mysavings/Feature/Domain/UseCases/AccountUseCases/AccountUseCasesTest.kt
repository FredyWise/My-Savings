package com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases

import com.fredy.mysavings.BaseTest
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkClass
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertFailsWith

class AccountUseCasesTest : BaseTest() {

    private lateinit var upsertAccount: UpsertAccount
    private lateinit var deleteAccount: DeleteAccount
    private lateinit var getAccount: GetAccount
    private lateinit var getAccounts: GetAccounts
    private lateinit var getAccountsCurrencies: GetAccountsCurrencies
    private lateinit var getAccountsTotalBalance: GetAccountsTotalBalance
    private var mockCurrencyUseCases = mockkClass(CurrencyUseCases::class)

    @Before
    fun setUp() {
        coEvery { mockCurrencyUseCases.convertCurrencyData(any(), any(), any()) } answers {
            val amount = firstArg<Double>()
            val currency = thirdArg<String>()
            BalanceItem(amount = amount, currency = currency)
        }

        upsertAccount = UpsertAccount(fakeAccountRepository, fakeAuthRepository)
        deleteAccount = DeleteAccount(fakeAccountRepository)
        getAccount = GetAccount(fakeAccountRepository)
        getAccounts = GetAccounts(fakeAccountRepository, fakeAuthRepository)
        getAccountsCurrencies = GetAccountsCurrencies(fakeAccountRepository, fakeAuthRepository)
        getAccountsTotalBalance = GetAccountsTotalBalance(fakeAccountRepository, mockCurrencyUseCases, fakeAuthRepository)
    }

    @Test
    fun `test upsert non-existent account`() = runBlocking {
        val accountId = "testing"
        val account = Account(
            accountId = accountId,
            userIdFk = currentUser,
            accountName = "Account a",
            accountAmount = 100.0,
            accountCurrency = "USD",
            accountIcon = 0,
            accountIconDescription = "Icon a"
        )

        val result = upsertAccount(account)

        assertEquals(accountId, result)
        val insertedAccount = fakeAccountRepository.getAccount(accountId = accountId).lastOrNull()
        assertEquals(account, insertedAccount)
    }


    @Test
    fun `test upsert existing account`() = runBlocking {
        val accountId = "testing"
        val oldAccount = Account(
            accountId = accountId,
            userIdFk = currentUser,
            accountName = "Account a",
            accountAmount = 100.0,
            accountCurrency = "USD",
            accountIcon = 0,
            accountIconDescription = "Icon a"
        )

        fakeAccountRepository.upsertAccount(oldAccount)

        val account = Account(
            accountId = accountId,
            userIdFk = currentUser,
            accountName = "Account b",
            accountAmount = 100.0,
            accountCurrency = "IDR",
            accountIcon = 0,
            accountIconDescription = "Icon b"
        )

        val result = upsertAccount(account)

        assertEquals(accountId, result)
        val insertedAccount = fakeAccountRepository.getAccount(accountId = accountId).lastOrNull()
        assertNotEquals(oldAccount, insertedAccount)
        assertEquals(account, insertedAccount)
    }


    @Test
    fun `test delete existing account`() {
        runBlocking {
            val accountId = "testing"
            val account = Account(
                accountId = accountId,
                userIdFk = currentUser,
                accountName = "Account a",
                accountAmount = 100.0,
                accountCurrency = "USD",
                accountIcon = 0,
                accountIconDescription = "Icon a"
            )

            fakeAccountRepository.upsertAccount(account)

            deleteAccount(account)
            assertFailsWith<NullPointerException> {
                fakeAccountRepository.getAccount(accountId = accountId).first()
            }
        }
    }


    @Test
    fun `test delete non-existent account`() {
        runBlocking {
            val accountId = "testing"
            val account = Account(
                accountId = accountId,
                userIdFk = currentUser,
                accountName = "Account a",
                accountAmount = 100.0,
                accountCurrency = "USD",
                accountIcon = 0,
                accountIconDescription = "Icon a"
            )

            deleteAccount(account)
            assertFailsWith<NullPointerException> {
                fakeAccountRepository.getAccount(accountId = accountId).first()
            }
        }
    }

    @Test
    fun `test get account`() = runBlocking {
        val accountId = "testing"
        val account = Account(
            accountId = accountId,
            userIdFk = currentUser,
            accountName = "Account a",
            accountAmount = 100.0,
            accountCurrency = "USD",
            accountIcon = 0,
            accountIconDescription = "Icon a"
        )

        fakeAccountRepository.upsertAccount(account)
        val retrievedAccount = getAccount(accountId = accountId).first()

        assertEquals(account, retrievedAccount)
    }

    @Test
    fun `test GetAccounts`() = runBlocking {
        val accountsFlow = getAccounts()
        val accountsResource = accountsFlow.last()

        assertTrue(accountsResource is Resource.Success)
        val accounts = (accountsResource as Resource.Success).data!!
        assertEquals(fakeAccountRepository.getUserAccounts(currentUser).first().size, accounts.size)
    }

    @Test
    fun `test GetAccountsCurrencies`() = runBlocking {
        val currenciesFlow = getAccountsCurrencies()
        val currencies = currenciesFlow.last()

        val expectedCurrencies = fakeAccountRepository.getUserAccounts(currentUser).first()
            .map { it.accountCurrency }
            .distinct()

        assertEquals(expectedCurrencies, currencies)
    }

    @Test
    fun `test GetAccountsTotalBalance`() = runBlocking {
        val balanceItemFlow = getAccountsTotalBalance()
        val balanceItem = balanceItemFlow.last()

        val expectedTotalBalance = fakeAccountRepository.getUserAccounts(currentUser).first()
            .sumOf { mockCurrencyUseCases.currencyConverter(it.accountAmount, it.accountCurrency, currentUser) }

        assertEquals(expectedTotalBalance, balanceItem.amount)
    }

}
