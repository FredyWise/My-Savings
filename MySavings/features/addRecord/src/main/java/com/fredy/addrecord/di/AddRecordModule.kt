package com.fredy.addrecord.di

import android.content.Context
import com.fredy.addrecord.domain.usecases.bulkAdd.ProcessImage
import com.fredy.addrecord.domain.usecases.bulkAdd.TabScannerUseCases
import com.fredy.addrecord.domain.usecases.bulkAdd.UpsertRecords
import com.fredy.addrecord.domain.usecases.singleAdd.GetRecordById
import com.fredy.addrecord.domain.usecases.singleAdd.SingleAddUseCases
import com.fredy.addrecord.domain.usecases.singleAdd.UpsertRecordItem
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.TabScannerRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddRecordModule {

    @Provides
    @Singleton
    fun provideTabScannerUseCases(
        @ApplicationContext
        context: Context,
        tabScannerRepository: TabScannerRepository,
        userRepository: UserRepository,
        recordRepository: RecordRepository
    ): TabScannerUseCases =
        TabScannerUseCases(
            processImage = ProcessImage(
                context,
                tabScannerRepository
            ),
            upsertRecords = UpsertRecords(
                userRepository,
                recordRepository
            )
        )
    @Provides
    @Singleton
    fun provideSingleAddRecordUseCases(
        userRepository: UserRepository,
        recordRepository: RecordRepository,
        currencyUseCases: CurrencyUseCases,
    ): SingleAddUseCases = SingleAddUseCases(
        upsertRecordItem = UpsertRecordItem(recordRepository, userRepository),
        getRecordById = GetRecordById(recordRepository, currencyUseCases),
    )
}