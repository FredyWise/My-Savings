package com.fredy.book.di

import com.fredy.book.data.RecordSpecificRepositoryImpl
import com.fredy.book.domain.BookSpecificRepository
import com.fredy.book.domain.RecordSpecificRepository
import com.fredy.book.domain.useCases.record.DeleteRecordItem
import com.fredy.book.domain.useCases.record.GetUserTotalAmountByTypeFromSpecificTime
import com.fredy.book.domain.useCases.record.GetUserTotalRecordBalance
import com.fredy.book.domain.useCases.record.GetUserTrueRecordMapsFromSpecificTime
import com.fredy.book.domain.useCases.record.GetWalletsCurrencies
import com.fredy.book.domain.useCases.record.RecordUseCases
import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecordModule {

    @Provides
    @Singleton
    fun provideRecordRepository(
//        firestore: FirebaseFirestore,
//        bookDataSource: RecordDataSource,
//        bookDao: RecordDao,
        recordRepository: RecordRepository
    ): RecordSpecificRepository = RecordSpecificRepositoryImpl(
        recordRepository
//        bookDataSource, bookDao, firestore,
    )


    @Provides
    @Singleton
    fun provideRecordUseCases(
        userRepository: UserRepository,
        recordSpecificRepository: RecordSpecificRepository,
        walletRepository: WalletRepository,
        currencyUseCases: CurrencyUseCases,
        bookSpecificRepository: BookSpecificRepository,
    ): RecordUseCases {
        val recordUseCases = RecordUseCases(
            deleteRecordItem = DeleteRecordItem(recordSpecificRepository),
            getUserTrueRecordMapsFromSpecificTime = GetUserTrueRecordMapsFromSpecificTime(
                recordSpecificRepository, userRepository, currencyUseCases, bookSpecificRepository
            ),
            getUserTotalAmountByTypeFromSpecificTime = GetUserTotalAmountByTypeFromSpecificTime(
                recordSpecificRepository, userRepository, currencyUseCases
            ),
            getUserTotalRecordBalance = GetUserTotalRecordBalance(
                recordSpecificRepository,
                userRepository,
                currencyUseCases
            ),
            getWalletsCurrencies = GetWalletsCurrencies(walletRepository, userRepository),
        )
        return recordUseCases
    }
}