package com.fredy.addrecord.di

import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.RecordUseCases.GetAllBooks
import com.fredy.domain.useCases.RecordUseCases.GetAllTrueRecordsWithinSpecificTime
import com.fredy.domain.useCases.RecordUseCases.GetUserTotalAmountByType
import com.fredy.domain.useCases.RecordUseCases.GetUserWalletRecordsOrderedByDateTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.DeleteRecordItem
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetRecordById
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserCategoriesWithAmountFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserCategoryRecordsOrderedByDateTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserRecordsFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTotalAmountByTypeFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTotalRecordBalance
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTrueRecordMapsFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserWalletsWithAmountFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpdateRecordItemWithDeletedBook
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpdateRecordItemWithDeletedCategory
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpdateRecordItemWithDeletedWallet
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpsertRecordItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object recordModule {

    @Provides
    @Singleton
    fun provideSingleAddRecordUseCases(
        userRepository: UserRepository,
        recordRepository: RecordRepository,
        walletRepository: WalletRepository,
        categoryRepository: CategoryRepository,
        currencyUseCases: CurrencyUseCases,
        bookRepository: BookRepository,
    ): RecordUseCases = RecordUseCases(
        upsertRecordItem = UpsertRecordItem(recordRepository, userRepository),
        deleteRecordItem = DeleteRecordItem(recordRepository),
        updateRecordItemWithDeletedWallet = UpdateRecordItemWithDeletedWallet(
            recordRepository,
            userRepository
        ),
        updateRecordItemWithDeletedCategory = UpdateRecordItemWithDeletedCategory(
            recordRepository,
            userRepository
        ),
        updateRecordItemWithDeletedBook = UpdateRecordItemWithDeletedBook(
            recordRepository,
            userRepository
        ),
        getRecordById = GetRecordById(recordRepository, currencyUseCases),
        getAllTrueRecordsWithinSpecificTime = GetAllTrueRecordsWithinSpecificTime(
            recordRepository,
            userRepository
        ),
        getAllBooks = GetAllBooks(recordRepository, userRepository, bookRepository),
        getUserCategoryRecordsOrderedByDateTime = GetUserCategoryRecordsOrderedByDateTime(
            recordRepository, userRepository
        ),
        getUserWalletRecordsOrderedByDateTime = GetUserWalletRecordsOrderedByDateTime(
            recordRepository, userRepository
        ),
        getUserTrueRecordMapsFromSpecificTime = GetUserTrueRecordMapsFromSpecificTime(
            recordRepository, userRepository, currencyUseCases, bookRepository
        ),
        getUserRecordsFromSpecificTime = GetUserRecordsFromSpecificTime(
            recordRepository,
            userRepository,
            currencyUseCases
        ),
        getUserCategoriesWithAmountFromSpecificTime = GetUserCategoriesWithAmountFromSpecificTime(
            recordRepository, categoryRepository, userRepository, currencyUseCases
        ),
        getUserWalletsWithAmountFromSpecificTime = GetUserWalletsWithAmountFromSpecificTime(
            recordRepository, walletRepository, userRepository, currencyUseCases
        ),
        getUserTotalAmountByType = GetUserTotalAmountByType(
            recordRepository,
            userRepository,
            currencyUseCases
        ),
        getUserTotalAmountByTypeFromSpecificTime = GetUserTotalAmountByTypeFromSpecificTime(
            recordRepository, userRepository, currencyUseCases
        ),
        getUserTotalRecordBalance = GetUserTotalRecordBalance(
            recordRepository,
            userRepository,
            currencyUseCases
        )
    )
}