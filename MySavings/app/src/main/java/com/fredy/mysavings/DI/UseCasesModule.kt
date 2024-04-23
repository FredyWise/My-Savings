package com.fredy.mysavings.DI

import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.WalletRepository
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.AuthUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.GoogleSignIn
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.LoginUser
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.RegisterUser
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.SendOtp
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.SignOut
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.UpdateUserInformation
import com.fredy.mysavings.Feature.Domain.UseCases.AuthUseCases.VerifyPhoneNumber
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.BookUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.DeleteBook
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.GetBook
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.GetUserBooks
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.UpsertBook
import com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases.CategoryUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases.DeleteCategory
import com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases.GetCategory
import com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases.GetCategoryMapOrderedByName
import com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases.UpsertCategory
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.ConvertCurrencyData
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.GetCurrencies
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.GetCurrencyRates
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.UpdateCurrency
import com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases.GetDBInfo
import com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases.IOUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases.InputFromCSV
import com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases.OutputToCSV
import com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases.UpsertTrueRecords
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.DeleteRecordItem
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetAllRecords
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetAllTrueRecordsWithinSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetRecordById
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserAccountRecordsOrderedByDateTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserAccountsWithAmountFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserCategoriesWithAmountFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserCategoryRecordsOrderedByDateTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserRecordsFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTotalAmountByType
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTotalAmountByTypeFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTotalRecordBalance
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTrueRecordMapsFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpdateRecordItemWithDeletedAccount
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpdateRecordItemWithDeletedBook
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpdateRecordItemWithDeletedCategory
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpsertRecordItem
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.DeleteUser
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.GetAllUsersOrderedByName
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.GetCurrentUser
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.GetUser
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.InsertUser
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.SearchUsers
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.UpdateUser
import com.fredy.mysavings.Feature.Domain.UseCases.UserUseCases.UserUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.DeleteWallet
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.GetWallet
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.GetWallets
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.GetWalletsCurrencies
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.GetWalletsTotalBalance
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.UpsertWallet
import com.fredy.mysavings.Feature.Domain.UseCases.WalletUseCases.WalletUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    @Singleton
    fun provideAccountUseCases(
        currencyUseCases: CurrencyUseCases,
        walletRepository: WalletRepository,
        authRepository: AuthRepository,
    ): WalletUseCases = WalletUseCases(
        upsertWallet = UpsertWallet(walletRepository, authRepository),
        deleteWallet = DeleteWallet(walletRepository),
        getWallet = GetWallet(walletRepository),
        getWalletsOrderedByName = GetWallets(walletRepository, authRepository),
        getWalletsTotalBalance = GetWalletsTotalBalance(
            walletRepository,
            currencyUseCases,
            authRepository
        ),
        getWalletsCurrencies = GetWalletsCurrencies(walletRepository, authRepository)
    )

    @Provides
    @Singleton
    fun provideRecordUseCases(
        authRepository: AuthRepository,
        recordRepository: RecordRepository,
        walletRepository: WalletRepository,
        categoryRepository: CategoryRepository,
        currencyUseCases: CurrencyUseCases,
        bookRepository: BookRepository,
    ): RecordUseCases = RecordUseCases(
        upsertRecordItem = UpsertRecordItem(recordRepository, authRepository),
        deleteRecordItem = DeleteRecordItem(recordRepository),
        updateRecordItemWithDeletedAccount = UpdateRecordItemWithDeletedAccount(
            recordRepository,
            authRepository
        ),
        updateRecordItemWithDeletedCategory = UpdateRecordItemWithDeletedCategory(
            recordRepository,
            authRepository
        ),
        updateRecordItemWithDeletedBook = UpdateRecordItemWithDeletedBook(
            recordRepository,
            authRepository
        ),
        getRecordById = GetRecordById(recordRepository, currencyUseCases),
        getAllTrueRecordsWithinSpecificTime = GetAllTrueRecordsWithinSpecificTime(
            recordRepository,
            authRepository
        ),
        getAllRecords = GetAllRecords(recordRepository, authRepository, bookRepository),
        getUserCategoryRecordsOrderedByDateTime = GetUserCategoryRecordsOrderedByDateTime(
            recordRepository, authRepository
        ),
        getUserAccountRecordsOrderedByDateTime = GetUserAccountRecordsOrderedByDateTime(
            recordRepository, authRepository
        ),
        getUserTrueRecordMapsFromSpecificTime = GetUserTrueRecordMapsFromSpecificTime(
            recordRepository, authRepository, currencyUseCases, bookRepository
        ),
        getUserRecordsFromSpecificTime = GetUserRecordsFromSpecificTime(
            recordRepository,
            authRepository,
            currencyUseCases
        ),
        getUserCategoriesWithAmountFromSpecificTime = GetUserCategoriesWithAmountFromSpecificTime(
            recordRepository, categoryRepository, authRepository, currencyUseCases
        ),
        getUserAccountsWithAmountFromSpecificTime = GetUserAccountsWithAmountFromSpecificTime(
            recordRepository, walletRepository, authRepository, currencyUseCases
        ),
        getUserTotalAmountByType = GetUserTotalAmountByType(
            recordRepository,
            authRepository,
            currencyUseCases
        ),
        getUserTotalAmountByTypeFromSpecificTime = GetUserTotalAmountByTypeFromSpecificTime(
            recordRepository, authRepository, currencyUseCases
        ),
        getUserTotalRecordBalance = GetUserTotalRecordBalance(
            recordRepository,
            authRepository,
            currencyUseCases
        )
    )

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
        searchUsers = SearchUsers(userRepository)
    )


    @Provides
    @Singleton
    fun provideCategoryUseCases(
        authRepository: AuthRepository,
        categoryRepository: CategoryRepository
    ): CategoryUseCases = CategoryUseCases(
        upsertCategory = UpsertCategory(categoryRepository, authRepository),
        deleteCategory = DeleteCategory(categoryRepository),
        getCategory = GetCategory(categoryRepository),
        getCategoryMapOrderedByName = GetCategoryMapOrderedByName(
            categoryRepository,
            authRepository
        )
    )


    @Provides
    @Singleton
    fun provideBookUseCases(
        authRepository: AuthRepository,
        bookRepository: BookRepository
    ): BookUseCases = BookUseCases(
        upsertBook = UpsertBook(bookRepository, authRepository),
        deleteBook = DeleteBook(bookRepository),
        getBook = GetBook(bookRepository),
        getUserBooks = GetUserBooks(
            bookRepository,
            authRepository
        )
    )

    @Provides
    @Singleton
    fun provideAuthUseCases(
        authRepository: AuthRepository,
    ): AuthUseCases = AuthUseCases(
        loginUser = LoginUser(authRepository),
        registerUser = RegisterUser(authRepository),
        updateUserInformation = UpdateUserInformation(authRepository),
        googleSignIn = GoogleSignIn(authRepository),
        sendOtp = SendOtp(authRepository),
        verifyPhoneNumber = VerifyPhoneNumber(authRepository),
        signOut = SignOut(authRepository),
    )

    @Provides
    @Singleton
    fun provideCSVUseCases(
        csvRepository: CSVRepository,
        authRepository: AuthRepository,
        recordRepository: RecordRepository,
        walletRepository: WalletRepository,
        categoryRepository: CategoryRepository,
    ): IOUseCases = IOUseCases(
        outputToCSV = OutputToCSV(csvRepository),
        inputFromCSV = InputFromCSV(
            csvRepository,
            authRepository,
            recordRepository,
            walletRepository,
            categoryRepository
        ),
        upsertTrueRecords = UpsertTrueRecords(
            authRepository,
            recordRepository,
            walletRepository,
            categoryRepository
        ),
        getDBInfo = GetDBInfo(
            authRepository,
            recordRepository,
            walletRepository,
            categoryRepository
        )
    )

    @Provides
    @Singleton
    fun provideCurrencyUseCases(
        authRepository: AuthRepository,
        currencyRepository: CurrencyRepository,
    ): CurrencyUseCases = CurrencyUseCases(
        updateCurrency = UpdateCurrency(currencyRepository),
        getCurrencyRates = GetCurrencyRates(currencyRepository),
        convertCurrencyData = ConvertCurrencyData(currencyRepository),
        getCurrencies = GetCurrencies(currencyRepository, authRepository)
    )

}