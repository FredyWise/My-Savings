package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface AccountRepository {
    suspend fun upsertAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    fun getAccount(accountId: String): Flow<Account>
    fun getUserAccountOrderedByName(): Flow<Resource<List<Account>>>
    fun getUserAccountTotalBalance(): Flow<BalanceItem>
    fun getUserAvailableCurrency(): Flow<List<String>>
}


class AccountRepositoryImpl @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val accountDataSource: AccountDataSource,
    private val accountDao: AccountDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): AccountRepository {
    private val accountCollection = firestore.collection(
        "account"
    )


    override suspend fun upsertAccount(account: Account) {
        val currentUser = firebaseAuth.currentUser
        val tempAccount = if (account.accountId.isEmpty()) {
            val newAccountRef = accountCollection.document()
            account.copy(
                accountId = newAccountRef.id,
                userIdFk = currentUser!!.uid
            )
        } else {
            account.copy(
                userIdFk = currentUser!!.uid
            )
        }

        accountDao.upsertAccountItem(tempAccount)
        accountDataSource.upsertAccountItem(
            tempAccount
        )
    }

    override suspend fun deleteAccount(account: Account) {
        accountDataSource.deleteAccountItem(
            account
        )
        accountDao.deleteAccountItem(account)
    }


    override fun getAccount(accountId: String): Flow<Account> {
        return flow {
            val account = accountDataSource.getAccount(
                accountId
            )
            emit(account)
        }
    }

    override fun getUserAccountOrderedByName(): Flow<Resource<List<Account>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = firebaseAuth.currentUser!!
            val userId = if (currentUser.isNotNull()) currentUser.uid else ""
            val data = accountDataSource.getUserAccounts(
                userId
            )
            emit(Resource.Success(data))
        }.catch { e ->
            Log.i(
                TAG,
                "getUserAccountOrderedByName.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getUserAccountTotalBalance(): Flow<BalanceItem> {
        return flow {
            val currentUser = firebaseAuth.currentUser
            val userId = if (currentUser.isNotNull()) currentUser!!.uid else ""
            val userCurrency = "USD"

            Log.i(
                TAG,
                "getUserAccountTotalBalance: $currentUser"
            )
            val accounts = accountDataSource.getUserAccounts(
                userId
            )
            val totalAccountBalance = accounts.sumOf { account ->
                Log.e(
                    TAG,
                    "getUserAccountTotalBalance2:" + account,
                )
                currencyConverter(
                    account.accountAmount,
                    account.accountCurrency,
                    userCurrency
                )

            }
            val data = BalanceItem(
                "Total Balance",
                totalAccountBalance,
                userCurrency
            )
            Log.i(
                TAG,
                "getUserAccountTotalBalance.data: $data"
            )
            emit(data)
        }
    }

    override fun getUserAvailableCurrency(): Flow<List<String>> {
        return flow {
            val currentUser = firebaseAuth.currentUser
            val userId = if (currentUser.isNotNull()) currentUser!!.uid else ""

            val data = accountDataSource.getUserAvailableCurrency(
                userId
            )
            emit(data)
        }
    }

    private suspend fun currencyConverter(
        amount: Double, from: String, to: String
    ): Double {
        return currencyRepository.convertCurrencyData(
            amount, from, to
        ).amount
    }
}