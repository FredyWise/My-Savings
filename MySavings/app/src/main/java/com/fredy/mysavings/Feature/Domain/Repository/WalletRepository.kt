package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.Database.Dao.WalletDao
import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.WalletDataSource
import com.fredy.mysavings.Feature.Data.Database.Model.Wallet

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface WalletRepository {
    suspend fun upsertWallet(wallet: Wallet): String
    suspend fun deleteWallet(wallet: Wallet)
    fun getWallet(accountId: String): Flow<Wallet>
    fun getUserWallets(userId: String): Flow<List<Wallet>>
}


class WalletRepositoryImpl @Inject constructor(
    private val walletDataSource: WalletDataSource,
    private val walletDao: WalletDao,
    private val firestore: FirebaseFirestore,
) : WalletRepository {
    private val accountCollection = firestore.collection(
        "account"
    )

    override suspend fun upsertWallet(wallet: Wallet):String {
        return withContext(Dispatchers.IO) {
            val tempAccount = if (wallet.walletId.isEmpty()) {
                val newAccountRef = accountCollection.document()
                wallet.copy(
                    walletId = newAccountRef.id,
                )
            } else{
                wallet
            }

            walletDao.upsertWalletItem(tempAccount)
            walletDataSource.upsertWalletItem(
                tempAccount
            )
            tempAccount.walletId
        }
    }

    override suspend fun deleteWallet(wallet: Wallet) {
        withContext(Dispatchers.IO) {
            walletDataSource.deleteWalletItem(
                wallet
            )
            walletDao.deleteWalletItem(wallet)
        }
    }


    override fun getWallet(accountId: String): Flow<Wallet> {
        return flow {
            val account = withContext(Dispatchers.IO) {
                walletDataSource.getWallet(
                    accountId
                )
            }
            emit(account)
        }
    }

    override fun getUserWallets(userId: String): Flow<List<Wallet>> {
        return flow {
            withContext(Dispatchers.IO) {
                walletDataSource.getUserWallets(userId)
            }.collect { accounts ->
                emit(accounts)
            }
        }
    }

}