package com.fredy.mysavings.Repository

import com.fredy.mysavings.Data.RoomDatabase.Dao.AccountDao
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.SavingsDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AccountRepository {
    suspend fun upsertAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    fun getAccount(accountId: Int): Flow<Account>
    fun getUserAccountOrderedByName(): Flow<List<Account>>
    fun getUserAccountTotalBalance(): Flow<Double>
}


class AccountRepositoryImpl @Inject constructor(private val savingsDatabase: SavingsDatabase) : AccountRepository {
    override suspend fun upsertAccount(account: Account) {
        savingsDatabase.accountDao.upsertContact(account)
    }

    override suspend fun deleteAccount(account: Account) {
        savingsDatabase.accountDao.deleteContact(account)
    }

    override fun getAccount(accountId: Int): Flow<Account> {
        return savingsDatabase.accountDao.getAccount(accountId)
    }

    override fun getUserAccountOrderedByName(): Flow<List<Account>> {
        return savingsDatabase.accountDao.getUserAccountOrderedByName()
    }

    override fun getUserAccountTotalBalance(): Flow<Double> {
        return savingsDatabase.accountDao.getUserAccountTotalBalance()
    }
}