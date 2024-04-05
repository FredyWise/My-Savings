package com.fredy.mysavings.DI

import com.fredy.mysavings.Feature.Domain.Repository.AccountRepository
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.CurrencyRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.AccountUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.CategoryUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.DeleteAccount
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.DeleteCategory
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.DeleteUser
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetAccount
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetAccounts
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetAccountsCurrencies
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetAccountsTotalBalance
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetAllUsersOrderedByName
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetCategory
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetCategoryMapOrderedByName
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetCurrentUser
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.GetUser
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.SearchUsers
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.UpsertAccount
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.UpsertCategory
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.UpsertUser
import com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases.UserUseCases
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
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpdateRecordItemWithDeletedCategory
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.UpsertRecordItem
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
        currencyRepository: CurrencyRepository,
        accountRepository: AccountRepository,
        authRepository: AuthRepository,
    ): AccountUseCases = AccountUseCases(
        upsertAccount = UpsertAccount(accountRepository, authRepository),
        deleteAccount = DeleteAccount(accountRepository),
        getAccount = GetAccount(accountRepository),
        getAccountOrderedByName = GetAccounts(accountRepository, authRepository),
        getAccountsTotalBalance = GetAccountsTotalBalance(
            accountRepository,
            currencyRepository,
            authRepository
        ),
        getAccountsCurrencies = GetAccountsCurrencies(accountRepository, authRepository)
    )

    @Provides
    @Singleton
    fun provideRecordUseCases(
        recordRepository: RecordRepository
    ): RecordUseCases = RecordUseCases(
        upsertRecordItem = UpsertRecordItem(recordRepository),
        deleteRecordItem = DeleteRecordItem(recordRepository),
        updateRecordItemWithDeletedAccount = UpdateRecordItemWithDeletedAccount(recordRepository),
        updateRecordItemWithDeletedCategory = UpdateRecordItemWithDeletedCategory(recordRepository),
        getRecordById = GetRecordById(recordRepository),
        getAllTrueRecordsWithinSpecificTime = GetAllTrueRecordsWithinSpecificTime(recordRepository),
        getAllRecords = GetAllRecords(recordRepository),
        getUserCategoryRecordsOrderedByDateTime = GetUserCategoryRecordsOrderedByDateTime(
            recordRepository
        ),
        getUserAccountRecordsOrderedByDateTime = GetUserAccountRecordsOrderedByDateTime(
            recordRepository
        ),
        getUserTrueRecordMapsFromSpecificTime = GetUserTrueRecordMapsFromSpecificTime(
            recordRepository
        ),
        getUserRecordsFromSpecificTime = GetUserRecordsFromSpecificTime(recordRepository),
        getUserCategoriesWithAmountFromSpecificTime = GetUserCategoriesWithAmountFromSpecificTime(
            recordRepository
        ),
        getUserAccountsWithAmountFromSpecificTime = GetUserAccountsWithAmountFromSpecificTime(
            recordRepository
        ),
        getUserTotalAmountByType = GetUserTotalAmountByType(recordRepository),
        getUserTotalAmountByTypeFromSpecificTime = GetUserTotalAmountByTypeFromSpecificTime(
            recordRepository
        ),
        getUserTotalRecordBalance = GetUserTotalRecordBalance(recordRepository)
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
    fun provideUserUseCases(
        userRepository: UserRepository
    ): UserUseCases = UserUseCases(
        upsertUser = UpsertUser(userRepository),
        deleteUser = DeleteUser(userRepository),
        getUser = GetUser(userRepository),
        getCurrentUser = GetCurrentUser(userRepository),
        getAllUsersOrderedByName = GetAllUsersOrderedByName(userRepository),
        searchUsers = SearchUsers(userRepository)
    )

}