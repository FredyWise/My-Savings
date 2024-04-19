package com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases

import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.mysavings.Feature.Domain.Model.Wallet
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

class WalletUseCasesTest : BaseUseCaseTest() {

    private lateinit var upsertWallet: UpsertWallet
    private lateinit var deleteWallet: DeleteWallet
    private lateinit var getWallet: GetWallet
    private lateinit var getWallets: GetWallets
    private lateinit var getWalletsCurrencies: GetWalletsCurrencies
    private lateinit var getWalletsTotalBalance: GetWalletsTotalBalance

    @Before
    fun setUp() {
        upsertWallet = UpsertWallet(fakeAccountRepository, fakeAuthRepository)
        deleteWallet = DeleteWallet(fakeAccountRepository)
        getWallet = GetWallet(fakeAccountRepository)
        getWallets = GetWallets(fakeAccountRepository, fakeAuthRepository)
        getWalletsCurrencies = GetWalletsCurrencies(fakeAccountRepository, fakeAuthRepository)
        getWalletsTotalBalance = GetWalletsTotalBalance(fakeAccountRepository, mockCurrencyUseCases, fakeAuthRepository)
    }

    @Test
    fun `test upsert New account`() = runBlocking {
        val accountId = "testing"
        val wallet = Wallet(
            walletId = accountId,
            walletName = "Account a",
            walletAmount = 100.0,
            walletCurrency = "USD",
            walletIcon = 0,
            walletIconDescription = "Icon a"
        )

        val result = upsertWallet(wallet)

        assertEquals(accountId, result)
        val insertedAccount = fakeAccountRepository.getWallet(accountId = accountId).lastOrNull()
        assertEquals(wallet.copy(userIdFk = currentUserId), insertedAccount)
    }


    @Test
    fun `test upsert existing account`() = runBlocking {
        val accountId = "testing"
        val oldWallet = Wallet(
            walletId = accountId,
            userIdFk = currentUserId,
            walletName = "Account a",
            walletAmount = 100.0,
            walletCurrency = "USD",
            walletIcon = 0,
            walletIconDescription = "Icon a"
        )

        fakeAccountRepository.upsertWallet(oldWallet)

        val wallet = oldWallet.copy(
            walletName = "Account b",
            walletAmount = 100.0,
            walletCurrency = "IDR",
            walletIcon = 0,
            walletIconDescription = "Icon b"
        )

        val result = upsertWallet(wallet)

        assertEquals(accountId, result)
        val insertedAccount = fakeAccountRepository.getWallet(accountId = accountId).lastOrNull()
        assertNotEquals(oldWallet, insertedAccount)
        assertEquals(wallet, insertedAccount)
    }


    @Test
    fun `test delete existing account`() {
        runBlocking {
            val accountId = "testing"
            val wallet = Wallet(
                walletId = accountId,
                userIdFk = currentUserId,
                walletName = "Account a",
                walletAmount = 100.0,
                walletCurrency = "USD",
                walletIcon = 0,
                walletIconDescription = "Icon a"
            )

            fakeAccountRepository.upsertWallet(wallet)

            deleteWallet(wallet)
            assertFailsWith<NullPointerException> {
                fakeAccountRepository.getWallet(accountId = accountId).first()
            }
        }
    }


    @Test
    fun `test delete non-existent account`() {
        runBlocking {
            val accountId = "testing"
            val wallet = Wallet(
                walletId = accountId,
                userIdFk = currentUserId,
                walletName = "Account a",
                walletAmount = 100.0,
                walletCurrency = "USD",
                walletIcon = 0,
                walletIconDescription = "Icon a"
            )

            deleteWallet(wallet)
            assertFailsWith<NullPointerException> {
                fakeAccountRepository.getWallet(accountId = accountId).first()
            }
        }
    }

    @Test
    fun `test get account`() = runBlocking {
        val accountId = "testing"
        val wallet = Wallet(
            walletId = accountId,
            userIdFk = currentUserId,
            walletName = "Account a",
            walletAmount = 100.0,
            walletCurrency = "USD",
            walletIcon = 0,
            walletIconDescription = "Icon a"
        )

        fakeAccountRepository.upsertWallet(wallet)

        val retrievedAccount = getWallet(accountId = accountId).first()

        assertEquals(wallet, retrievedAccount)
    }

    @Test
    fun `test get non-existent account`() {
        runBlocking {
            val accountId = "testing"
            val wallet = Wallet(
                walletId = accountId,
                userIdFk = currentUserId,
                walletName = "Account a",
                walletAmount = 100.0,
                walletCurrency = "USD",
                walletIcon = 0,
                walletIconDescription = "Icon a"
            )

            fakeAccountRepository.deleteWallet(wallet)

            assertFailsWith<NullPointerException> {
                getWallet(accountId = accountId).first()
            }
        }
    }

    @Test
    fun `Retrieve All User Accounts`() = runBlocking {
        val accountsFlow = getWallets()
        val accountsResource = accountsFlow.last()

        assertTrue(accountsResource is Resource.Success)
        val accounts = (accountsResource as Resource.Success).data!!
        assertEquals(fakeAccountRepository.getUserWallets(currentUserId).first().size, accounts.size)
    }

    @Test
    fun `Retrieve Distinct Currencies From User Accounts`() = runBlocking {
        val currenciesFlow = getWalletsCurrencies()
        val currencies = currenciesFlow.last()

        val expectedCurrencies = fakeAccountRepository.getUserWallets(currentUserId).first()
            .map { it.walletCurrency }
            .distinct()

        assertEquals(expectedCurrencies, currencies)
    }

    @Test
    fun `Calculate Total Balance Across All User Accounts`() = runBlocking {
        val balanceItemFlow = getWalletsTotalBalance()
        val balanceItem = balanceItemFlow.last()

        val expectedTotalBalance = fakeAccountRepository.getUserWallets(currentUserId).first()
            .sumOf { mockCurrencyUseCases.currencyConverter(it.walletAmount, it.walletCurrency, currentUserId) }

        assertEquals(expectedTotalBalance, balanceItem.amount)
    }


}
