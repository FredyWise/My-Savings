package com.fredy.data.repositoryImplement

import com.fredy.data.database.dao.WalletDao
import com.fredy.data.database.firestoreDataSource.WalletDataSource
import com.fredy.data.mappers.toDataWallet
import com.fredy.data.mappers.toDomainWallet
import com.fredy.domain.model.Wallet
import com.fredy.domain.repository.WalletRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val walletDataSource: WalletDataSource,
    private val walletDao: WalletDao,
    private val firestore: FirebaseFirestore,
) : WalletRepository {
    private val walletCollection = firestore.collection(
        "account"
    )

    override suspend fun upsertWallet(wallet: Wallet):String {
        return withContext(Dispatchers.IO) {
            val dataWallet = if (wallet.walletId.isEmpty()) {
                val newAccountRef = walletCollection.document()
                wallet.copy(
                    walletId = newAccountRef.id,
                )
            } else {
                wallet
            }.toDataWallet()

            walletDao.upsertWalletItem(dataWallet)
            walletDataSource.upsertWalletItem(
                dataWallet
            )
            dataWallet.walletId
        }
    }

    override suspend fun deleteWallet(wallet: Wallet) {
        withContext(Dispatchers.IO) {
            val dataWallet = wallet.toDataWallet()
            walletDataSource.deleteWalletItem(
                dataWallet
            )
            walletDao.deleteWalletItem(dataWallet)
        }
    }


    override fun getWallet(walletId: String): Flow<Wallet> {
        return flow {
            val wallet = withContext(Dispatchers.IO) {
                walletDataSource.getWallet(
                    walletId
                )
            }.toDomainWallet()
            emit(wallet)
        }
    }

    override fun getUserWallets(userId: String): Flow<List<Wallet>> {
        return flow {
            withContext(Dispatchers.IO) {
                walletDataSource.getUserWallets(userId)
            }.collect { wallets ->
                emit(wallets.toDomainWallet())
            }
        }
    }

}