package com.fredy.wallet.data

import com.fredy.data.database.dao.WalletDao
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.data.database.firestoreDataSource.WalletDataSource
import com.fredy.data.mappers.toDomainRecords
import com.fredy.data.mappers.toDomainTrueRecords
import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Record
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.model.Wallet
import com.fredy.wallet.domain.WalletRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val recordDataSource: RecordDataSource,
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

    override fun getUserRecordsByType(
        userId: String,
        recordType: RecordType,
    ): Flow<List<Record>> {
        Timber.i("getUserRecordsByTypeRepo: $userId")
        return flow {
            recordDataSource.getUserRecordsByType(userId, recordType).collect { records ->
                Timber.i("getUserRecordsByTypeRepo.Data: $records")
                emit(records.toDomainRecords())
            }
        }
    }

    override fun getUserWalletRecordsOrderedByDateTime(
        userId: String,
        walletId: String,
        sortType: com.fredy.domain.enums.SortType,
    ): Flow<List<TrueRecord>> {
        Timber.i("getUserAccountRecordsOrderedByDateTimeRepo: $walletId")
        return flow {
            recordDataSource.getUserWalletRecordsOrderedByDateTime(userId, walletId)
                .collect { records ->
                    Timber.i("getUserAccountRecordsOrderedByDateTimeRepo.Data: $records")
                    emit(records.toDomainTrueRecords())
                }
        }
    }

}