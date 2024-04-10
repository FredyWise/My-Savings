package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Repository.AccountRepository
import com.fredy.mysavings.Feature.Domain.Repository.AccountWithAmountType
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryWithAmount
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Feature.Mappers.filterRecordCurrency
import com.fredy.mysavings.Feature.Mappers.filterTrueRecordCurrency
import com.fredy.mysavings.Feature.Mappers.toBookSortedMaps
import com.fredy.mysavings.Feature.Mappers.toRecordSortedMaps
import com.fredy.mysavings.Util.BalanceItem
import com.fredy.mysavings.Util.DefaultData.deletedAccount
import com.fredy.mysavings.Util.DefaultData.deletedCategory
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.isExpense
import com.fredy.mysavings.Util.isIncome
import com.fredy.mysavings.Util.isTransfer
import com.fredy.mysavings.Util.minDate
import com.fredy.mysavings.ViewModels.BookMap
import com.fredy.mysavings.ViewModels.RecordMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

data class RecordUseCases(
    val upsertRecordItem: UpsertRecordItem,
    val deleteRecordItem: DeleteRecordItem,
    val updateRecordItemWithDeletedAccount: UpdateRecordItemWithDeletedAccount,
    val updateRecordItemWithDeletedCategory: UpdateRecordItemWithDeletedCategory,
    val getRecordById: GetRecordById,
    val getAllTrueRecordsWithinSpecificTime: GetAllTrueRecordsWithinSpecificTime, //io
    val getAllRecords: GetAllRecords, //search
    val getUserCategoryRecordsOrderedByDateTime: GetUserCategoryRecordsOrderedByDateTime, // category
    val getUserAccountRecordsOrderedByDateTime: GetUserAccountRecordsOrderedByDateTime, // account
    val getUserTrueRecordMapsFromSpecificTime: GetUserTrueRecordMapsFromSpecificTime, // record main screen
    val getUserRecordsFromSpecificTime: GetUserRecordsFromSpecificTime, //analysis flow
    val getUserCategoriesWithAmountFromSpecificTime: GetUserCategoriesWithAmountFromSpecificTime,//analysis overview
    val getUserAccountsWithAmountFromSpecificTime: GetUserAccountsWithAmountFromSpecificTime,//analysis account
    val getUserTotalAmountByType: GetUserTotalAmountByType,
    val getUserTotalAmountByTypeFromSpecificTime: GetUserTotalAmountByTypeFromSpecificTime,
    val getUserTotalRecordBalance: GetUserTotalRecordBalance // balance bar total balance
)

private suspend fun List<Record>.getTotalRecordBalance(
    currencyUseCases: CurrencyUseCases,
    userCurrency: String
): Double {
    return this.sumOf { record ->
        currencyUseCases.currencyConverter(
            record.recordAmount,
            record.recordCurrency,
            userCurrency
        )
    }
}

class UpsertRecordItem(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(record: Record): String {
        val currentUser = authRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return recordRepository.upsertRecordItem(
            record.copy(
                userIdFk = currentUserId,
                categoryIdFk = if (record.accountIdFromFk != record.accountIdToFk) record.categoryIdFk + currentUserId else record.categoryIdFk
            )
        )
    }
}

class DeleteRecordItem(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(record: Record) {
        recordRepository.deleteRecordItem(record)
    }
}

class UpdateRecordItemWithDeletedAccount(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(account: Account) {
        withContext(Dispatchers.IO) {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val records = recordRepository.getUserRecords(userId).first()
            val tempRecords = records.filter {
                it.accountIdFromFk == account.accountId || it.accountIdToFk == account.accountId
            }.map {
                var record = it
                if (it.accountIdFromFk == account.accountId) {
                    record = record.copy(accountIdFromFk = deletedAccount.accountId + userId)
                }
                if (it.accountIdToFk == account.accountId) {
                    record = record.copy(accountIdToFk = deletedAccount.accountId + userId)
                }
                record
            }
            recordRepository.upsertAllRecordItems(tempRecords)
        }
    }
}

class UpdateRecordItemWithDeletedCategory(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(category: Category) {
        withContext(Dispatchers.IO) {
            Log.d("startDelCategory")
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val records = recordRepository.getUserRecords(userId).first()
            Log.d("$records")
            val tempRecords = records.filter {
                it.categoryIdFk == category.categoryId
            }.map {
                it.copy(categoryIdFk = deletedCategory.categoryId + userId)
            }
            recordRepository.upsertAllRecordItems(tempRecords)
        }
    }
}

class GetRecordById(
    private val recordRepository: RecordRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(recordId: String): Flow<TrueRecord> {
        Log.i("getRecordById: $recordId")
        return flow {
            val trueRecord = recordRepository.getRecordById(
                recordId
            )

            emit(
                trueRecord.copy(
                    record = trueRecord.record.copy(
                        recordAmount = currencyUseCases.currencyConverter(
                            trueRecord.record.recordAmount,
                            trueRecord.toAccount.accountCurrency,
                            trueRecord.fromAccount.accountCurrency
                        )
                    )
                )
            )
        }.catch { e ->
            Log.e(
                "getRecordById.Error: $e"
            )
        }
    }
}

class GetAllTrueRecordsWithinSpecificTime(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<Resource<List<TrueRecord>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            recordRepository.getUserTrueRecordsFromSpecificTime(userId, startDate, endDate)
                .collect { data ->
                    Log.i(
                        "getAllTrueRecordsWithinSpecificTime.Data: $data"
                    )
                    emit(Resource.Success(data))
                }

        }.catch { e ->
            Log.e(
                "getAllTrueRecordsWithinSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}

class GetAllRecords(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<Resource<List<BookMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            val books = bookRepository.getUserBooks(userId).first()

            recordRepository.getRecordMaps(userId).collect { records ->
                val data = books.toBookSortedMaps(records)
                Log.i(
                    "getAllRecords.data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getAllRecords.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}

class GetUserCategoryRecordsOrderedByDateTime(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(categoryId: String, sortType: SortType): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                "getUserCategoryRecordsOrderedByDateTime: $categoryId",

                )

            recordRepository.getUserCategoryRecordsOrderedByDateTime(
                userId, categoryId, sortType
            ).collect { data ->
                Log.i(
                    "getUserCategoryRecordsOrderedByDateTime.Data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserCategoryRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}

class GetUserAccountRecordsOrderedByDateTime(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(accountId: String, sortType: SortType): Flow<Resource<List<RecordMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            Log.i(
                "getUserAccountRecordsOrderedByDateTime: $accountId",

                )
            withContext(Dispatchers.IO) {
                recordRepository.getUserAccountRecordsOrderedByDateTime(
                    userId, accountId, sortType
                )
            }.collect { data ->
                Log.i(
                    "getUserAccountRecordsOrderedByDateTime.data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserAccountRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}

class GetUserTrueRecordMapsFromSpecificTime( // main screen
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases,
    private val bookRepository: BookRepository,
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sortType: SortType,
        currency: List<String>,
        useUserCurrency: Boolean
    ): Flow<Resource<List<BookMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = if (currency.isEmpty()) "" else currentUser.userCurrency
            Log.i(
                "getUserTrueRecordMapsFromSpecificTime: $startDate\n:\n$endDate,\ncurrency: $currency"
            )

            val books = bookRepository.getUserBooks(userId).first()

            recordRepository.getUserTrueRecordsFromSpecificTime(
                userId,
                startDate,
                endDate,
            ).map { records ->
                books.toBookSortedMaps(
                    records.convertRecordCurrency(userCurrency, useUserCurrency)
                        .filterTrueRecordCurrency(currency + userCurrency),
                    sortType
                )
            }.collect { data ->
                emit(Resource.Success(data))
            }

        }.catch { e ->
            Log.e(
                "getUserTrueRecordMapsFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun List<TrueRecord>.convertRecordCurrency(
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<TrueRecord> {
        return if (useUserCurrency) {
            this.map { trueRecord ->
                trueRecord.copy(
                    record = trueRecord.record.copy(
                        recordAmount = currencyUseCases.currencyConverter(
                            trueRecord.record.recordAmount,
                            trueRecord.record.recordCurrency,
                            userCurrency
                        ),
                        recordCurrency = userCurrency
                    )
                )
            }
        } else {
            this
        }
    }
}

class GetUserRecordsFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases,
) {
    operator fun invoke(
        recordType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
        useUserCurrency: Boolean
    ): Flow<Resource<List<Record>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                "getUserRecordsFromSpecificTime: $startDate\n:\n$endDate"
            )

            recordRepository.getUserRecordsByTypeFromSpecificTime(
                userId,
                listOf(recordType),
                startDate,
                endDate,
            ).map { it.filterRecordCurrency(currency) }.collect { records ->
                val data = records.combineSameCurrencyData(sortType, userCurrency, useUserCurrency)

                Log.i(
                    "getUserRecordsFromSpecificTime.Data: $data",

                    )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserRecordsFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun List<Record>.combineSameCurrencyData(
        sortType: SortType = SortType.DESCENDING,
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<Record> {
        val recordsMap = mutableMapOf<String, Record>()
        this.forEach { record ->
            val key = record.recordDateTime.toLocalDate().toString()
            val existingRecord = recordsMap[key]
            val currency = if (useUserCurrency) userCurrency else record.recordCurrency
            val amount = if (useUserCurrency) {
                currencyUseCases.currencyConverter(
                    record.recordAmount,
                    record.recordCurrency,
                    userCurrency
                )
            } else {
                record.recordAmount
            }

            if (existingRecord != null) {
                val tempAmount =
                    if (record.recordCurrency != existingRecord.recordCurrency && !useUserCurrency) {
                        currencyUseCases.currencyConverter(
                            amount,
                            record.recordCurrency,
                            existingRecord.recordCurrency
                        )
                    } else {
                        amount
                    }
                recordsMap[key] = existingRecord.copy(
                    recordAmount = existingRecord.recordAmount + tempAmount,
                )
            } else {
                recordsMap[key] = record.copy(recordAmount = amount, recordCurrency = currency)
            }
        }
        val data = recordsMap.values.toList().let { value ->
            if (sortType == SortType.ASCENDING) {
                value.sortedBy { it.recordAmount }
            } else {
                value.sortedByDescending { it.recordAmount }
            }
        }
        return data
    }


}

class GetUserCategoriesWithAmountFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases,
) {
    operator fun invoke(
        categoryType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
        useUserCurrency: Boolean
    ): Flow<Resource<List<CategoryWithAmount>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                "getUserCategoriesWithAmountFromSpecificTime: $currency\n$categoryType\n$startDate\n:\n$endDate"
            )
            val userCategories = categoryRepository.getUserCategories(
                userId
            ).first()

            recordRepository.getUserRecordsByTypeFromSpecificTime(
                userId,
                listOf(categoryType),
                startDate,
                endDate,
            ).map { it.filterRecordCurrency(currency) }.collect { records ->
                Log.i(
                    "getUserCategoriesWithAmountFromSpecificTime.Result: $records",
                )
                val data =
                    records.combineSameCurrencyCategory(
                        sortType,
                        userCategories,
                        userCurrency,
                        useUserCurrency
                    )
                Log.i(
                    "getUserCategoriesWithAmountFromSpecificTime.Data: $data",

                    )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserCategoriesWithAmountFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun List<Record>.combineSameCurrencyCategory(
        sortType: SortType = SortType.DESCENDING,
        userCategories: List<Category>,
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<CategoryWithAmount> {
        val categoryWithAmountMap = mutableMapOf<String, CategoryWithAmount>()
        this.forEach { record ->
            val currency = if (useUserCurrency) userCurrency else record.recordCurrency
            val key = record.categoryIdFk + currency
            val existingCategory = categoryWithAmountMap[key]
            val amount = if (useUserCurrency) {
                currencyUseCases.currencyConverter(
                    record.recordAmount,
                    record.recordCurrency,
                    userCurrency
                )
            } else {
                record.recordAmount
            }
            if (existingCategory != null) {
                categoryWithAmountMap[key] = existingCategory.copy(
                    amount = existingCategory.amount + amount,
                )
            } else {
                val newCategory = CategoryWithAmount(
                    category = userCategories.first { it.categoryId == record.categoryIdFk },
                    amount = amount,
                    currency = currency
                )
                categoryWithAmountMap[key] = newCategory
            }
        }

        val data = categoryWithAmountMap.values.toList().let { value ->
            if (sortType == SortType.ASCENDING) {
                value.sortedBy { it.amount }
            } else {
                value.sortedByDescending { it.amount }

            }
        }
        return data
    }

}

class GetUserAccountsWithAmountFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases,
) {
    operator fun invoke(
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        useUserCurrency: Boolean
    ): Flow<Resource<List<AccountWithAmountType>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                "getUserAccountsWithAmountFromSpecificTime: \n$startDate\n:\n$endDate"
            )
            val userAccounts = accountRepository.getUserAccounts(
                userId
            ).first()

            recordRepository.getUserRecordsFromSpecificTime(
                userId, startDate, endDate
            ).collect { records ->
                Log.i(
                    "getUserAccountsWithAmountFromSpecificTime.Result: $records",
                )
                val data = records.toAccountWithAmount(
                    sortType,
                    userId,
                    userAccounts,
                    userCurrency,
                    useUserCurrency
                )
                Log.i(
                    "getUserAccountsWithAmountFromSpecificTime.Data: $data",

                    )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserAccountsWithAmountFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun List<Record>.toAccountWithAmount(
        sortType: SortType = SortType.DESCENDING,
        userId: String,
        userAccounts: List<Account>,
        userCurrency: String,
        useUserCurrency: Boolean,
    ): List<AccountWithAmountType> {
        val accountWithAmountMap = mutableMapOf<String, AccountWithAmountType>()
        userAccounts.forEach { account ->
            if (account.accountId == deletedAccount.accountId + userId && account.accountAmount == 0.0) {
                return@forEach
            }
            val currency = if (useUserCurrency) userCurrency else account.accountCurrency
            val key = account.accountId
            val newAccount = AccountWithAmountType(
                account = account.copy(accountCurrency = currency),
                incomeAmount = 0.0,
                expenseAmount = 0.0,
            )
            accountWithAmountMap[key] = newAccount
        }
        this.forEach { record ->
            val account = userAccounts.first { it.accountId == record.accountIdFromFk }
            val key = record.accountIdFromFk

            val existingAccount = accountWithAmountMap[key]
            if (!isTransfer(record.recordType)) {
                val amount = if (useUserCurrency) {
                    currencyUseCases.currencyConverter(
                        record.recordAmount,
                        record.recordCurrency,
                        userCurrency
                    )
                } else {
                    record.recordAmount
                }
                val incomeAmount = if (isIncome(record.recordType)) amount else 0.0
                val expenseAmount = if (isExpense(record.recordType)) amount else 0.0
                if (existingAccount != null) {
                    val currency =
                        if (useUserCurrency) userCurrency else existingAccount.account.accountCurrency
                    accountWithAmountMap[key] = existingAccount.copy(
                        account = existingAccount.account.copy(accountCurrency = currency),
                        incomeAmount = existingAccount.incomeAmount + incomeAmount,
                        expenseAmount = existingAccount.expenseAmount + expenseAmount,
                    )

                } else {
                    val currency = if (useUserCurrency) userCurrency else account.accountCurrency
                    val newAccount = AccountWithAmountType(
                        account = account.copy(accountCurrency = currency),
                        incomeAmount = incomeAmount,
                        expenseAmount = expenseAmount,
                    )
                    accountWithAmountMap[key] = newAccount
                }
            }

        }

        val data = accountWithAmountMap.values.toList().let { value ->
            if (sortType == SortType.ASCENDING) {
                value.sortedBy { it.account.accountName }
            } else {
                value.sortedByDescending { it.account.accountName }
            }
        }
        return data
    }
}

class GetUserTotalAmountByType(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(recordType: RecordType): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                "getUserTotalAmountByType: $recordType",

                )
            val records = recordRepository.getUserRecordsByType(
                userId, recordType
            ).map { it.getTotalRecordBalance(currencyUseCases, userCurrency) }

            records.collect { recordTotalAmount ->
                val data = BalanceItem(
                    name = "${recordType.name}: ",
                    amount = recordTotalAmount,
                    currency = userCurrency
                )
                Log.i(
                    "getUserTotalAmountByType.Result: $data",

                    )
                emit(data)
            }
        }.catch { e ->
            Log.e(
                "getUserTotalAmountByType.Error: $e"
            )
        }
    }
}

class GetUserTotalAmountByTypeFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i("getUserTotalAmountByTypeFromSpecificTime: $recordType")

            recordRepository.getUserRecordsByTypeFromSpecificTime(
                userId,
                listOf(recordType),
                startDate,
                endDate
            ).map { it.getTotalRecordBalance(currencyUseCases, userCurrency) }
                .collect { recordTotalAmount ->
                    val data = BalanceItem(
                        name = "${recordType.name}: ",
                        amount = recordTotalAmount,
                        currency = userCurrency
                    )
                    Log.i(
                        "getUserTotalAmountByTypeFromSpecificTime.Data: $data"
                    )
                    emit(data)
                }
        }.catch { e ->
            Log.e(
                "getUserTotalAmountByTypeFromSpecificTime.Error: $e"
            )
        }
    }
}

class GetUserTotalRecordBalance(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
    private val currencyUseCases: CurrencyUseCases
) {
    operator fun invoke(isCaryOn:Boolean, startDate: LocalDateTime, endDate: LocalDateTime): Flow<BalanceItem> {
        return flow {
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i("getUserTotalRecordBalance: ")
            recordRepository.getUserRecordsByTypeFromSpecificTime(
                userId,
                listOf(RecordType.Expense, RecordType.Income),
                if (isCaryOn) minDate else startDate,
                endDate
            ).map { it.getTotalRecordBalance(currencyUseCases, userCurrency) }
                .collect { recordTotalAmount ->
                    val data = BalanceItem(
                        name = "Balance: ",
                        amount = recordTotalAmount,
                        currency = userCurrency
                    )
                    Log.i(
                        "getUserTotalRecordBalance.Data: $data",

                        )
                    emit(data)
                }
        }.catch { e ->
            Log.e(
                "getUserTotalRecordBalance.Error: $e"
            )
        }
    }
}



