package com.fredy.addrecord.di

import android.content.Context
import com.fredy.addrecord.data.AddRecordRepositoryImpl
import com.fredy.addrecord.data.TabScannerRepositoryImpl
import com.fredy.addrecord.data.tabScannerModel.TabScannerAPI
import com.fredy.addrecord.domain.AddRecordRepository
import com.fredy.addrecord.domain.TabScannerRepository
import com.fredy.addrecord.domain.usecases.bulkAdd.ProcessImage
import com.fredy.addrecord.domain.usecases.bulkAdd.TabScannerUseCases
import com.fredy.addrecord.domain.usecases.bulkAdd.UpsertRecords
import com.fredy.addrecord.domain.usecases.singleAdd.GetRecordById
import com.fredy.addrecord.domain.usecases.singleAdd.SingleAddUseCases
import com.fredy.addrecord.domain.usecases.singleAdd.UpsertRecordItem
import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.domain.credentials.ApiCredentials
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddRecordModule {
    @Provides
    @Singleton
    fun provideTabScannerApi(okHttpClient: OkHttpClient): TabScannerAPI = Retrofit.Builder()
        .baseUrl(ApiCredentials.TabScanner.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(
            okHttpClient
        )
        .build()
        .create(TabScannerAPI::class.java)

    @Provides
    @Singleton
    fun provideAddRecordRepository(
        recordRepository: RecordRepository
//        recordDataSource: RecordDataSource,
//        recordDao: RecordDao,
//        firestore: FirebaseFirestore,
    ): AddRecordRepository = AddRecordRepositoryImpl(
        recordRepository
//        recordDataSource,
//        recordDao,
//        firestore,
    )

    @Provides
    @Singleton
    fun provideTabScannerRepository(
        tabScannerAPI: TabScannerAPI,
    ): TabScannerRepository = TabScannerRepositoryImpl(
        tabScannerAPI
    )

    @Provides
    @Singleton
    fun provideTabScannerUseCases(
        @ApplicationContext
        context: Context,
        tabScannerRepository: TabScannerRepository,
        userRepository: UserRepository,
        recordRepository: AddRecordRepository
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
        recordRepository: AddRecordRepository,
        currencyUseCases: CurrencyUseCases,
    ): SingleAddUseCases = SingleAddUseCases(
        upsertRecordItem = UpsertRecordItem(recordRepository, userRepository),
        getRecordById = GetRecordById(recordRepository, currencyUseCases),
    )
}