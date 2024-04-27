package com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource

import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Model.Wallet

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface WalletDataSource {
    suspend fun upsertWalletItem(wallet: Wallet)
    suspend fun deleteWalletItem(wallet: Wallet)
    suspend fun getWallet(walletId: String): Wallet
    suspend fun getUserWallets(userId: String): Flow<List<Wallet>>
}


class WalletDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : WalletDataSource {
    private val walletCollection = firestore.collection(
        "wallet"
    )

    override suspend fun upsertWalletItem(
        wallet: Wallet
    ) {
        walletCollection.document(
            wallet.walletId
        ).set(
            wallet
        )
    }

    override suspend fun deleteWalletItem(wallet: Wallet) {
        walletCollection.document(wallet.walletId).delete()
    }

    override suspend fun getWallet(walletId: String): Wallet {
        return try {
            walletCollection.document(walletId).get().await().toObject<Wallet>()
                ?: throw Exception(
                    "Account Not Found"
                )
        } catch (e: Exception) {
            Log.e("Failed to get wallet: ${e.message}")
            throw e
        }
    }

    override suspend fun getUserWallets(userId: String): Flow<List<Wallet>> {
        return try {
            val querySnapshot = walletCollection
                .whereEqualTo("userIdFk", userId)
                .snapshots()

            querySnapshot.map { it.toObjects()}
        } catch (e: Exception) {
            Log.e("Failed to get user wallets: $e")
            throw e
        }
    }

}