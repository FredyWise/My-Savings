package com.fredy.mysavings.DI

import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Feature.Domain.Repository.CSVRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
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
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserWalletRecordsOrderedByDateTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserWalletsWithAmountFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserCategoriesWithAmountFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserCategoryRecordsOrderedByDateTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserRecordsFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTotalAmountByType
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTotalAmountByTypeFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTotalRecordBalance
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.GetUserTrueRecordMapsFromSpecificTime
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpdateRecordItemWithDeletedWallet
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
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        getAllRecords = GetAllRecords(recordRepository, userRepository, bookRepository),
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
    fun provideAuthUseCases(
        firebaseAuth: FirebaseAuth,
        oneTapClient: SignInClient,
        firestore: FirebaseFirestore,
    ): AuthUseCases = AuthUseCases(
        loginUser = LoginUser(firebaseAuth),
        registerUser = RegisterUser(firebaseAuth),
        updateUserInformation = UpdateUserInformation(firebaseAuth),
        googleSignIn = GoogleSignIn(firebaseAuth),
        sendOtp = SendOtp(firebaseAuth),
        verifyPhoneNumber = VerifyPhoneNumber(firebaseAuth),
        signOut = SignOut(firebaseAuth, oneTapClient),
    )

    @Provides
    @Singleton
    fun provideCSVUseCases(
        csvRepository: CSVRepository,
        userRepository: UserRepository,
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
        getDBInfo = GetDBInfo(
            userRepository,
            recordRepository,
            walletRepository,
            categoryRepository
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