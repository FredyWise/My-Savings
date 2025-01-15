package com.fredy.wallet.di

import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.data.database.dao.WalletDao
import com.fredy.data.database.firestoreDataSource.RecordDataSource
import com.fredy.data.database.firestoreDataSource.WalletDataSource
import com.fredy.domain.repository.RecordRepository

import com.fredy.domain.repository.UserRepository

import com.fredy.wallet.data.WalletRepositoryImpl
import com.fredy.wallet.domain.WalletRepository
import com.fredy.wallet.domain.useCases.DeleteWallet
import com.fredy.wallet.domain.useCases.GetUserTotalAmountByType

import com.fredy.wallet.domain.useCases.GetUserWalletRecordsOrderedByDateTime
import com.fredy.wallet.domain.useCases.GetWallet
import com.fredy.wallet.domain.useCases.GetWallets
import com.fredy.wallet.domain.useCases.GetWalletsTotalBalance
import com.fredy.wallet.domain.useCases.UpdateRecordItemWithDeletedWallet
import com.fredy.wallet.domain.useCases.UpsertWallet
import com.fredy.wallet.domain.useCases.WalletUseCases

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WalletModule {


    @Provides
    @Singleton
    fun provideWalletRepository(
        recordDataSource: RecordDataSource,
        walletDataSource: WalletDataSource,
        walletDao: WalletDao,
        firestore: FirebaseFirestore,
    ): WalletRepository = WalletRepositoryImpl(
        recordDataSource,
        walletDataSource,
        walletDao,
        firestore,
    )

    @Provides
    @Singleton
    fun provideWalletUseCases(
        recordRepository: RecordRepository,
        currencyUseCases: CurrencyUseCases,
        walletRepository: WalletRepository,
        userRepository: UserRepository,
    ): WalletUseCases = WalletUseCases(
        upsertWallet = UpsertWallet(walletRepository, userRepository),
        deleteWallet = DeleteWallet(walletRepository),
        getWallet = GetWallet(walletRepository),
        getWalletsOrderedByName = GetWallets(walletRepository, userRepository),
        getWalletsTotalBalance = GetWalletsTotalBalance(
            walletRepository,
            currencyUseCases,
            userRepository
        ),
        getUserTotalAmountByType = GetUserTotalAmountByType(walletRepository, userRepository,currencyUseCases),
        getUserWalletRecordsOrderedByDateTime = GetUserWalletRecordsOrderedByDateTime(walletRepository,userRepository),
        updateRecordItemWithDeletedWallet = UpdateRecordItemWithDeletedWallet(recordRepository,userRepository)
    )


}