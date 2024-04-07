package com.fredy.mysavings.Feature.Domain.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Mappers.getCurrencies
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.deletedAccount
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AccountRepository {
    suspend fun upsertAccount(account: Account): String
    suspend fun deleteAccount(account: Account)
    fun getAccount(accountId: String): Flow<Account>
    fun getUserAccounts(userId: String): Flow<List<Account>>
//    fun getAccountOrderedByName(): Flow<Resource<List<Account>>>
//    fun getAccountTotalBalance(): Flow<BalanceItem>
//    fun getAccountsCurrency(): Flow<List<String>>
}


class AccountRepositoryImpl @Inject constructor(
    private val accountDataSource: AccountDataSource,
    private val accountDao: AccountDao,
    private val firestore: FirebaseFirestore,
) : AccountRepository {
    private val accountCollection = firestore.collection(
        "account"
    )

    override suspend fun upsertAccount(account: Account):String {
        return withContext(Dispatchers.IO) {
            val tempAccount = if (account.accountId.isEmpty()) {
                val newAccountRef = accountCollection.document()
                account.copy(
                    accountId = newAccountRef.id,
                )
            } else{
                account
            }

            accountDao.upsertAccountItem(tempAccount)
            accountDataSource.upsertAccountItem(
                tempAccount
            )
            tempAccount.accountId
        }
    }

    override suspend fun deleteAccount(account: Account) {
        withContext(Dispatchers.IO) {
            accountDataSource.deleteAccountItem(
                account
            )
            accountDao.deleteAccountItem(account)
        }
    }


    override fun getAccount(accountId: String): Flow<Account> {
        return flow {
            val account = withContext(Dispatchers.IO) {
                accountDao.getAccount(
                    accountId
                )
            }
            emit(account)
        }
    }

    override fun getUserAccounts(userId: String): Flow<List<Account>> {
        return flow {
            withContext(Dispatchers.IO) {
                accountDataSource.getUserAccounts(userId)
            }.collect { accounts ->
                emit(accounts)
            }
        }
    }

}