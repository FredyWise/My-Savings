package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeAccountRepository : AccountRepository {

    private val accounts = mutableListOf<Account>()

    override suspend fun upsertAccount(account: Account): String {
        val existingAccount = accounts.find { it.accountId == account.accountId }
        return if (existingAccount != null) {
            accounts.remove(existingAccount)
            accounts.add(account)
            account.accountId
        } else {
            account.accountId.also { accounts.add(account) }
        }
    }

    override suspend fun deleteAccount(account: Account) {
        accounts.remove(account)
    }

    override fun getAccount(accountId: String): Flow<Account> {
        return flow { emit(accounts.find { it.accountId == accountId }!!) }
    }

    override fun getUserAccounts(userId: String): Flow<List<Account>> {
        return flow { emit(accounts.filter { it.userIdFk == userId }) }
    }
}

