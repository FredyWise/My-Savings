package com.fredy.domain.di

import android.content.Context
import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.CurrencyRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.TabScannerRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository
import com.fredy.domain.useCases.BookUseCases.BookUseCases
import com.fredy.domain.useCases.BookUseCases.DeleteBook
import com.fredy.domain.useCases.BookUseCases.GetBook
import com.fredy.domain.useCases.BookUseCases.GetUserBooks
import com.fredy.domain.useCases.BookUseCases.UpsertBook
import com.fredy.domain.useCases.CategoryUseCases.CategoryUseCases
import com.fredy.domain.useCases.CategoryUseCases.DeleteCategory
import com.fredy.domain.useCases.CategoryUseCases.GetCategory
import com.fredy.domain.useCases.CategoryUseCases.GetCategoryMapOrderedByName
import com.fredy.domain.useCases.CategoryUseCases.UpsertCategory
import com.fredy.domain.useCases.CurrencyUseCases.ConvertCurrencyData
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.GetCurrencies
import com.fredy.domain.useCases.CurrencyUseCases.GetCurrencyRates
import com.fredy.domain.useCases.CurrencyUseCases.UpdateCurrency
import com.fredy.domain.useCases.RecordUseCases.DeleteRecordItem
import com.fredy.domain.useCases.RecordUseCases.GetAllRecords
import com.fredy.domain.useCases.RecordUseCases.GetAllTrueRecordsWithinSpecificTime
import com.fredy.domain.useCases.RecordUseCases.GetRecordById
import com.fredy.domain.useCases.RecordUseCases.GetUserCategoriesWithAmountFromSpecificTime
import com.fredy.domain.useCases.RecordUseCases.GetUserCategoryRecordsOrderedByDateTime
import com.fredy.domain.useCases.RecordUseCases.GetUserRecordsFromSpecificTime
import com.fredy.domain.useCases.RecordUseCases.GetUserTotalAmountByType
import com.fredy.domain.useCases.RecordUseCases.GetUserTotalAmountByTypeFromSpecificTime
import com.fredy.domain.useCases.RecordUseCases.GetUserTotalRecordBalance
import com.fredy.domain.useCases.RecordUseCases.GetUserTrueRecordMapsFromSpecificTime
import com.fredy.domain.useCases.RecordUseCases.GetUserWalletRecordsOrderedByDateTime
import com.fredy.domain.useCases.RecordUseCases.GetUserWalletsWithAmountFromSpecificTime
import com.fredy.domain.useCases.RecordUseCases.RecordUseCases
import com.fredy.domain.useCases.RecordUseCases.UpdateRecordItemWithDeletedBook
import com.fredy.domain.useCases.RecordUseCases.UpdateRecordItemWithDeletedCategory
import com.fredy.domain.useCases.RecordUseCases.UpdateRecordItemWithDeletedWallet
import com.fredy.domain.useCases.UserUseCases.DeleteUser
import com.fredy.domain.useCases.UserUseCases.GetAllUsersOrderedByName
import com.fredy.domain.useCases.UserUseCases.GetCurrentUser
import com.fredy.domain.useCases.UserUseCases.GetUser
import com.fredy.domain.useCases.UserUseCases.InsertUser
import com.fredy.domain.useCases.UserUseCases.SearchUsers
import com.fredy.domain.useCases.UserUseCases.UpdateUser
import com.fredy.domain.useCases.UserUseCases.UploadProfilePicture
import com.fredy.domain.useCases.UserUseCases.UserUseCases
import com.fredy.domain.useCases.WalletUseCases.DeleteWallet
import com.fredy.domain.useCases.WalletUseCases.GetWallet
import com.fredy.domain.useCases.WalletUseCases.GetWallets
import com.fredy.domain.useCases.WalletUseCases.GetWalletsCurrencies
import com.fredy.domain.useCases.WalletUseCases.GetWalletsTotalBalance
import com.fredy.domain.useCases.WalletUseCases.UpsertWallet
import com.fredy.domain.useCases.WalletUseCases.WalletUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    @Singleton
    fun provideWalletUseCases(
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
        getWalletsCurrencies = GetWalletsCurrencies(walletRepository, userRepository)
    )

    @Provides
    @Singleton
    fun provideRecordUseCases(
        userRepository: UserRepository,
        recordRepository: RecordRepository,
        walletRepository: WalletRepository,
        categoryRepository: CategoryRepository,
        currencyUseCases: CurrencyUseCases,
        bookRepository: BookRepository,
    ): RecordUseCases {
        val recordUseCases = RecordUseCases(
//            upsertRecordItem = UpsertRecordItem(recordRepository, userRepository),
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
            getAllBooks = GetAllRecords(recordRepository, userRepository, bookRepository),
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
        return recordUseCases
    }

    @Provides
    @Singleton
    fun provideUserUseCases(
        userRepository: UserRepository
    ): UserUseCases = UserUseCases(
        insertUser = InsertUser(userRepository),
        updateUser = UpdateUser(userRepository),
        deleteUser = DeleteUser(userRepository),
        getUser = GetUser(userRepository),
        getCurrentUser = GetCurrentUser(userRepository),
        getAllUsersOrderedByName = GetAllUsersOrderedByName(userRepository),
        searchUsers = SearchUsers(userRepository),
        uploadProfilePicture = UploadProfilePicture()
    )


    @Provides
    @Singleton
    fun provideCategoryUseCases(
        userRepository: UserRepository,
        categoryRepository: CategoryRepository
    ): CategoryUseCases = CategoryUseCases(
        upsertCategory = UpsertCategory(categoryRepository, userRepository),
        deleteCategory = DeleteCategory(categoryRepository),
        getCategory = GetCategory(categoryRepository),
        getCategoryMapOrderedByName = GetCategoryMapOrderedByName(
            categoryRepository,
            userRepository
        )
    )


    @Provides
    @Singleton
    fun provideBookUseCases(
        userRepository: UserRepository,
        bookRepository: BookRepository
    ): BookUseCases = BookUseCases(
        upsertBook = UpsertBook(bookRepository, userRepository),
        deleteBook = DeleteBook(bookRepository),
        getBook = GetBook(bookRepository),
        getUserBooks = GetUserBooks(
            bookRepository,
            userRepository
        )
    )



    @Provides
    @Singleton
    fun provideCurrencyUseCases(
        userRepository: UserRepository,
        currencyRepository: CurrencyRepository,
    ): CurrencyUseCases = CurrencyUseCases(
        updateCurrency = UpdateCurrency(currencyRepository),
        getCurrencyRates = GetCurrencyRates(currencyRepository),
        convertCurrencyData = ConvertCurrencyData(currencyRepository),
        getCurrencies = GetCurrencies(currencyRepository, userRepository)
    )


}