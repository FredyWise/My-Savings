package com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases

import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Util.Resource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class AccountUseCasesTest : BaseUseCaseTest() {

    private lateinit var upsertAccount: UpsertAccount
    private lateinit var deleteAccount: DeleteAccount
    private lateinit var getAccount: GetAccount
    private lateinit var getAccounts: GetAccounts
    private lateinit var getAccountsCurrencies: GetAccountsCurrencies
    private lateinit var getAccountsTotalBalance: GetAccountsTotalBalance

    @Before
    fun setUp() {
        upsertAccount = UpsertAccount(fakeAccountRepository, fakeAuthRepository)
        deleteAccount = DeleteAccount(fakeAccountRepository)
        getAccount = GetAccount(fakeAccountRepository)
        getAccounts = GetAccounts(fakeAccountRepository, fakeAuthRepository)
        getAccountsCurrencies = GetAccountsCurrencies(fakeAccountRepository, fakeAuthRepository)
        getAccountsTotalBalance = GetAccountsTotalBalance(fakeAccountRepository, mockCurrencyUseCases, fakeAuthRepository)
    }

    @Test
    fun `test upsert New account`() = runBlocking {
        val accountId = "testing"
        val account = Account(
            accountId = accountId,
            userIdFk = currentUserId,
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
            userIdFk = currentUserId,
            accountName = "Account a",
            accountAmount = 100.0,
            accountCurrency = "USD",
            accountIcon = 0,
            accountIconDescription = "Icon a"
        )

        fakeAccountRepository.upsertAccount(oldAccount)

        val account = Account(
            accountId = accountId,
            userIdFk = currentUserId,
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
                userIdFk = currentUserId,
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
                userIdFk = currentUserId,
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
            userIdFk = currentUserId,
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
    fun `test get non-existent account`() {
        runBlocking {
            val accountId = "testing"
            val account = Account(
                accountId = accountId,
                userIdFk = currentUserId,
                accountName = "Account a",
                accountAmount = 100.0,
                accountCurrency = "USD",
                accountIcon = 0,
                accountIconDescription = "Icon a"
            )

            fakeAccountRepository.deleteAccount(account)

            assertFailsWith<NullPointerException> {
                getAccount(accountId = accountId).first()
            }
        }
    }

    @Test
    fun `Retrieve All User Accounts`() = runBlocking {
        val accountsFlow = getAccounts()
        val accountsResource = accountsFlow.last()

        assertTrue(accountsResource is Resource.Success)
        val accounts = (accountsResource as Resource.Success).data!!
        assertEquals(fakeAccountRepository.getUserAccounts(currentUserId).first().size, accounts.size)
    }

    @Test
    fun `Retrieve Distinct Currencies From User Accounts`() = runBlocking {
        val currenciesFlow = getAccountsCurrencies()
        val currencies = currenciesFlow.last()

        val expectedCurrencies = fakeAccountRepository.getUserAccounts(currentUserId).first()
            .map { it.accountCurrency }
            .distinct()

        assertEquals(expectedCurrencies, currencies)
    }

    @Test
    fun `Calculate Total Balance Across All User Accounts`() = runBlocking {
        val balanceItemFlow = getAccountsTotalBalance()
        val balanceItem = balanceItemFlow.last()

        val expectedTotalBalance = fakeAccountRepository.getUserAccounts(currentUserId).first()
            .sumOf { mockCurrencyUseCases.currencyConverter(it.accountAmount, it.accountCurrency, currentUserId) }

        assertEquals(expectedTotalBalance, balanceItem.amount)
    }


}
