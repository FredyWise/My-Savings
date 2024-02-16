package com.fredy.mysavings.Data.Repository

import android.util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Data.Database.FirebaseDataSource.AccountDataSource
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.deletedAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
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
    private val authRepository: AuthRepository,
    private val accountDataSource: AccountDataSource,
    private val accountDao: AccountDao,
    private val firestore: FirebaseFirestore,
) : AccountRepository {
    private val accountCollection = firestore.collection(
        "account"
    )


    override suspend fun upsertAccount(account: Account) {
        withContext(Dispatchers.IO) {
            val currentUserId = authRepository.getCurrentUser()!!.firebaseUserId
            val tempAccount = if (account.accountId.isEmpty()) {
                val newAccountRef = accountCollection.document()
                account.copy(
                    accountId = newAccountRef.id,
                    userIdFk = currentUserId
                )
            } else {
                account.copy(
                    userIdFk = currentUserId
                )
            }

            accountDao.upsertAccountItem(tempAccount)
            accountDataSource.upsertAccountItem(
                tempAccount
            )
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

    override fun getUserAccountOrderedByName(): Flow<Resource<List<Account>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val data = withContext(Dispatchers.IO) {
                accountDao.getUserAccounts(
                    userId
                ).filter { it.accountName != deletedAccount.accountName }
            }
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
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency

            Log.i(
                TAG,
                "getUserAccountTotalBalance: $currentUser"
            )
            val accounts = withContext(Dispatchers.IO) {
                accountDao.getUserAccounts(
                    userId
                )
            }
            val totalAccountBalance = accounts.sumOf { account ->
                Log.i(
                    TAG,
                    "getUserAccountTotalBalance.foreach:$account",
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
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            val data = withContext(Dispatchers.IO) {
                accountDao.getUserAvailableCurrency(
                    userId
                )
            }
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