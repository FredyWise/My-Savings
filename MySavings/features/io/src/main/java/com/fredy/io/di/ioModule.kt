package com.fredy.io.di

import android.content.Context
import com.fredy.data.CSV.CSVDao
import com.fredy.data.CSV.CSVDaoImpl
import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.io.data.CSVRepositoryImpl
import com.fredy.io.domain.CSVRepository
import com.fredy.io.domain.useCases.GetAllTrueRecordsWithinSelectedBook
import com.fredy.io.domain.useCases.GetDBInfo
import com.fredy.io.domain.useCases.GetUserBooks
import com.fredy.io.domain.useCases.IOUseCases
import com.fredy.io.domain.useCases.InputFromCSV
import com.fredy.io.domain.useCases.OutputToCSV
import com.fredy.io.domain.useCases.UpsertTrueRecords
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ioModule {
    @Provides
    @Singleton
    fun provideCSVDao(@ApplicationContext context: Context): CSVDao = CSVDaoImpl(context)

    @Provides
    @Singleton
    fun provideCSVRepository(
        csvDao: CSVDao,
    ): CSVRepository = CSVRepositoryImpl(
        csvDao
    )


    @Provides
    @Singleton
    fun provideCSVUseCases(
        csvRepository: CSVRepository,
        userRepository: UserRepository,
        bookRepository: BookRepository,
        recordRepository: RecordRepository,
        walletRepository: WalletRepository,
        categoryRepository: CategoryRepository,
    ): IOUseCases = IOUseCases(
        outputToCSV = OutputToCSV(csvRepository),
        inputFromCSV = InputFromCSV(
            csvRepository,
            userRepository,
            recordRepository,
            walletRepository,
            categoryRepository
        ),
        upsertTrueRecords = UpsertTrueRecords(
            userRepository,
            recordRepository,
            walletRepository,
            categoryRepository
        ),
        getAllTrueRecordsWithinSelectedBook = GetAllTrueRecordsWithinSelectedBook(
            recordRepository,
            userRepository
        ),
        getDBInfo = GetDBInfo(
            userRepository,
            recordRepository,
            walletRepository,
            categoryRepository
        ),
        getUserBooks = GetUserBooks(bookRepository, userRepository)
    )
}