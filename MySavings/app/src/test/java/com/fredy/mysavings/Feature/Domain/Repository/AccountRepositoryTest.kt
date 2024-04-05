package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AccountRepositoryImplTest {

    @Mock
    private lateinit var currencyRepository: CurrencyRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var accountDataSource: AccountDataSource

    @Mock
    private lateinit var accountDao: AccountDao

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Test
    fun testUpsertAccount() = runBlocking {
        val accountRepository = AccountRepositoryImpl(currencyRepository, authRepository, accountDataSource, accountDao, firestore)
        val account = Account()

        accountRepository.upsertAccount(account)

        verify(accountDataSource, times(1)).upsertAccountItem(account)
        verify(accountDao, times(1)).upsertAccountItem(account)
    }

    @Test
    fun testDeleteAccount() = runBlocking {
        val accountRepository = AccountRepositoryImpl(currencyRepository, authRepository, accountDataSource, accountDao, firestore)
        val account = Account()

        accountRepository.deleteAccount(account)

        verify(accountDataSource, times(1)).deleteAccountItem(account)
        verify(accountDao, times(1)).deleteAccountItem(account)
    }

    @Test
    fun testGetAccount() = runTest {
        val accountRepository = AccountRepositoryImpl(currencyRepository, authRepository, accountDataSource, accountDao, firestore)
        val accountId = "testAccountId"
        val expectedAccount = Account()
        `when`(accountDao.getAccount(accountId)).thenReturn(expectedAccount)

        val actualAccount = accountRepository.getAccount(accountId).first()

        assertEquals(expectedAccount, actualAccount)
    }

    @Test
    fun testGetUserAccounts() = runBlocking {
        val accountRepository = AccountRepositoryImpl(currencyRepository, authRepository, accountDataSource, accountDao, firestore)
        val userId = "testUserId"
        val expectedAccounts = flowOf(listOf(Account()))
        `when`(accountDataSource.getUserAccounts(userId)).thenReturn(expectedAccounts)

        val actualAccounts = accountRepository.getUserAccounts(userId).first()

        assertEquals(expectedAccounts, actualAccounts)
    }

    @Test
    fun testGetUserAccountOrderedByName() = runBlocking {
        val accountRepository = AccountRepositoryImpl(currencyRepository, authRepository, accountDataSource, accountDao, firestore)
        val expectedAccounts = flowOf(listOf(Account()))
        `when`(authRepository.getCurrentUser()).thenReturn(UserData())
        `when`(accountDataSource.getUserAccounts(any())).thenReturn(expectedAccounts)

        val actualAccounts = accountRepository.getAccountOrderedByName().first()

        assertEquals(Resource.Success(expectedAccounts), actualAccounts)
    }

    @Test
    fun testGetUserAccountTotalBalance() = runBlocking {
        val accountRepository = AccountRepositoryImpl(currencyRepository, authRepository, accountDataSource, accountDao, firestore)
        val expectedBalance = BalanceItem("Total Balance", 100.0, "USD")
        val fakeAccounts = flowOf(listOf(Account()))
        `when`(authRepository.getCurrentUser()).thenReturn(UserData())
        `when`(accountDataSource.getUserAccounts(any())).thenReturn(fakeAccounts)
        `when`(currencyRepository.convertCurrencyData(any(), any(), any())).thenReturn(BalanceItem(amount = 100.0))

        val actualBalance = accountRepository.getAccountTotalBalance().first()

        assertEquals(expectedBalance, actualBalance)
    }

    @Test
    fun testGetUserAvailableCurrency() = runBlocking {
        val accountRepository = AccountRepositoryImpl(currencyRepository, authRepository, accountDataSource, accountDao, firestore)
        val fakeAccounts = flowOf(listOf(Account()))
        val expectedCurrencies = listOf("USD", "EUR")
        `when`(authRepository.getCurrentUser()).thenReturn(UserData())
        `when`(accountDataSource.getUserAccounts(any())).thenReturn(fakeAccounts)

        val actualCurrencies = accountRepository.getAvailableCurrency().first()

        assertEquals(expectedCurrencies, actualCurrencies)
    }

}
