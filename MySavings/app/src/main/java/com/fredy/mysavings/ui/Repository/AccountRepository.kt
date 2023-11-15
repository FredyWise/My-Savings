package com.fredy.mysavings.ui.Repository

import com.fredy.mysavings.Data.RoomDatabase.Dao.AccountDao
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun upsertAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    fun getAccount(accountId: Int): Flow<Account>
    fun getUserAccountOrderedByName(): Flow<List<Account>>
    fun getUserAccountTotalBalance(): Flow<Double>
}


class AccountRepositoryImpl(private val accountDao: AccountDao) : AccountRepository {
    override suspend fun upsertAccount(account: Account) {
        accountDao.upsertContact(account)
    }

    override suspend fun deleteAccount(account: Account) {
        accountDao.deleteContact(account)
    }

    override fun getAccount(accountId: Int): Flow<Account> {
        return accountDao.getAccount(accountId)
    }

    override fun getUserAccountOrderedByName(): Flow<List<Account>> {
        return accountDao.getUserAccountOrderedByName()
    }

    override fun getUserAccountTotalBalance(): Flow<Double> {
        return accountDao.getUserAccountTotalBalance()
    }
}