package com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource

import com.fredy.mysavings.Feature.Data.Database.Model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Test

class FakeAccountDataSource: AccountDataSource {

    private val accounts = mutableListOf<Account>()

    override suspend fun upsertAccountItem(account: Account) {
        accounts.add(account)
    }

    override suspend fun deleteAccountItem(account: Account) {
        accounts.remove(account)
    }

    override suspend fun getAccount(accountId: String): Account {
       return accounts.first { account -> accountId == account.accountId }
    }

    override suspend fun getUserAccounts(userId: String): Flow<List<Account>> {
        return flow { emit(accounts) }
    }
}