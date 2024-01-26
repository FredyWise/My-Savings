package com.fredy.mysavings.Data.Database.FirebaseDataSource

import android.util.Log
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Util.TAG
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject

interface RecordDataSource {
    suspend fun upsertRecordItem(record: Record)
    suspend fun deleteRecordItem(record: Record)
    suspend fun getRecordById(recordId: String): TrueRecord

    suspend fun getUserTrueRecordByCurrencyFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): List<TrueRecord>

    suspend fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
    ): List<TrueRecord>

    suspend fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
    ): List<TrueRecord>

    suspend fun getUserRecordsByTypeAndCurrencyFromSpecificTime(
        userId: String,
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): List<Record>

    suspend fun getUserRecords(
        userId: String
    ): List<Record>
    suspend fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Record>

    suspend fun getUserRecordsByType(
        userId: String, recordType: RecordType
    ): List<Record>

    suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Record>
}

class RecordDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): RecordDataSource {
    private val recordCollection = firestore.collection(
        "record"
    )

    override suspend fun upsertRecordItem(//make sure the record already have uid // make sure to create the record id outside instead
        record: Record
    ) {
        recordCollection.document(
            record.recordId
        ).set(
            record
        )
    }

    override suspend fun deleteRecordItem(
        record: Record
    ) {
        recordCollection.document(
            record.recordId
        ).delete()
    }

    override suspend fun getRecordById(recordId: String): TrueRecord {
        return try {
            val recordSnapshot = recordCollection.document(
                recordId
            ).get().await()
            val record = recordSnapshot.toObject<Record>() ?: throw Exception(
                "Record Not Found"
            )
            getTrueRecord(
                record
            )
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to get record: ${e.message}"
            )
            throw e
        }
    }

    override suspend fun getUserTrueRecordByCurrencyFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): List<TrueRecord> {
        val trueRecordComponentResult = getTrueRecordsComponent(
            userId
        )

        return try {
            val querySnapshot = recordCollection.whereGreaterThanOrEqualTo(
                "recordTimestamp",
                TimestampConverter.fromDateTime(
                    startDate
                )
            ).whereLessThanOrEqualTo(
                "recordTimestamp",
                TimestampConverter.fromDateTime(
                    endDate
                )
            ).whereEqualTo(
                "userIdFk", userId
            ).orderBy(
                "recordTimestamp",
                Query.Direction.DESCENDING
            ).get().await()

            val records = querySnapshot.toObjects<Record>().filter {
                currency.contains(
                    it.recordCurrency
                ) || currency.isEmpty()
            }

            records.map { record ->
                TrueRecord(
                    record = record,
                    fromAccount = trueRecordComponentResult.fromAccount.single { it.accountId == record.accountIdFromFk },
                    toAccount = trueRecordComponentResult.toAccount.single { it.accountId == record.accountIdToFk },
                    toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
                )
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "getUserTrueRecordFromSpecificTimeError: ${e.message}"
            )
            throw e
        }
    }


    override suspend fun getUserCategoryRecordsOrderedByDateTime(
        userId: String,
        categoryId: String,
    ): List<TrueRecord> {
        val trueRecordComponentResult = getTrueRecordsComponent(
            userId
        )

        return try {
            val querySnapshot = recordCollection.whereEqualTo(
                "categoryIdFk", categoryId
            ).whereEqualTo(
                "userIdFk", userId
            ).orderBy(
                "recordTimestamp",
                Query.Direction.DESCENDING
            ).get().await()

            val records = querySnapshot.toObjects<Record>()
            records.map { record ->
                TrueRecord(
                    record = record,
                    fromAccount = trueRecordComponentResult.fromAccount.single { it.accountId == record.accountIdFromFk },
                    toAccount = trueRecordComponentResult.toAccount.single { it.accountId == record.accountIdToFk },
                    toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
                )
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "getUserCategoryRecordsOrderedByDateTimeError: ${e.message}"
            )
            throw e
        }
    }

    override suspend fun getUserAccountRecordsOrderedByDateTime(
        userId: String,
        accountId: String,
    ): List<TrueRecord> {
        val trueRecordComponentResult = getTrueRecordsComponent(
            userId
        )

        return try {
            val querySnapshot = recordCollection.where(
                Filter.or(
                    Filter.equalTo(
                        "accountIdFromFk",
                        accountId
                    ), Filter.equalTo(
                        "accountIdToFk", accountId
                    )
                )
            ).whereEqualTo(
                "userIdFk", userId
            ).orderBy(
                "recordTimestamp",
                Query.Direction.DESCENDING
            ).get().await()

            val records = querySnapshot.toObjects<Record>()
            records.map { record ->
                TrueRecord(
                    record = record,
                    fromAccount = trueRecordComponentResult.fromAccount.single { it.accountId == record.accountIdFromFk },
                    toAccount = trueRecordComponentResult.toAccount.single { it.accountId == record.accountIdToFk },
                    toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
                )
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "getUserAccountRecordsOrderedByDateTimeError: ${e.message}"
            )
            throw e
        }
    }


    override suspend fun getUserRecordsByTypeAndCurrencyFromSpecificTime(
        userId: String,
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
    ): List<Record> {
        return try {
            val querySnapshot = recordCollection.whereGreaterThanOrEqualTo(
                "recordTimestamp",
                TimestampConverter.fromDateTime(
                    startDate
                )
            ).whereLessThanOrEqualTo(
                "recordTimestamp",
                TimestampConverter.fromDateTime(
                    endDate
                )
            ).whereEqualTo(
                "recordType", recordType
            ).whereEqualTo(
                "userIdFk", userId
            ).orderBy(
                "recordTimestamp",
                Query.Direction.DESCENDING
            ).get().await()

            querySnapshot.toObjects<Record>().filter {
                currency.contains(
                    it.recordCurrency
                ) || currency.isEmpty()
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "getUserRecordsFromSpecificTimeError: ${e.message}"
            )
            throw e
        }
    }


//    override suspend fun getUserCategoriesWithAmountFromSpecificTime(
//        userId: String,
//        categoryType: RecordType,
//        startDate: LocalDateTime,
//        endDate: LocalDateTime,
//        currency: List<String>,
//    ): List<CategoryWithAmount> {
//        return try {
//            val userCategories = getUserCategory(userId)
//            val querySnapshot = recordCollection
//                .whereGreaterThanOrEqualTo("recordTimestamp", TimestampConverter.fromDateTime(startDate))
//                .whereLessThanOrEqualTo("recordTimestamp", TimestampConverter.fromDateTime(endDate))
//                .whereEqualTo("recordType", categoryType)
//                .whereEqualTo("userIdFk", userId)
//                .orderBy("recordTimestamp", Query.Direction.DESCENDING)
//                .get().await()
//
//            val records = querySnapshot.toObjects<Record>().filter {
//                currency.contains(it.recordCurrency) || currency.isEmpty()
//            }
//
//            val categoryWithAmountMap = mutableMapOf<String, CategoryWithAmount>()
//            records.forEach { record ->
//                val key = record.categoryIdFk + record.recordCurrency
//                val existingCategory = categoryWithAmountMap[key]
//                if (existingCategory != null) {
//                    categoryWithAmountMap[key] = existingCategory.copy(
//                        amount = existingCategory.amount + record.recordAmount
//                    )
//                } else {
//                    val newCategory = CategoryWithAmount(
//                        category = userCategories.first { it.categoryId == record.categoryIdFk },
//                        amount = record.recordAmount,
//                        currency = record.recordCurrency
//                    )
//                    categoryWithAmountMap[key] = newCategory
//                }
//            }
//            categoryWithAmountMap.values.toList().sortedBy { it.amount }
//        } catch (e: Exception) {
//            Log.e(TAG, "getUserCategoriesWithAmountFromSpecificTimeError: ${e.message}")
//            null
//        }
//    }
//
//    override suspend fun getUserAccountsWithAmountFromSpecificTime(
//        userId: String,
//        startDate: LocalDateTime,
//        endDate: LocalDateTime,
//    ): List<AccountWithAmountType> {
//        return try {
//            val userAccounts = getUserAccount(userId)
//            val querySnapshot = recordCollection
//                .whereGreaterThanOrEqualTo("recordTimestamp", TimestampConverter.fromDateTime(startDate))
//                .whereLessThanOrEqualTo("recordTimestamp", TimestampConverter.fromDateTime(endDate))
//                .whereEqualTo("userIdFk", userId)
//                .orderBy("recordTimestamp", Query.Direction.DESCENDING)
//                .get().await()
//
//            val records = querySnapshot.toObjects<Record>()
//            val accountWithAmountMap = mutableMapOf<String, AccountWithAmountType>()
//            userAccounts.forEach { account ->
//                val key = account.accountId
//                val newAccount = AccountWithAmountType(
//                    account = account,
//                    incomeAmount = 0.0,
//                    expenseAmount = 0.0,
//                )
//                accountWithAmountMap[key] = newAccount
//            }
//            records.forEach { record ->
//                val key = record.accountIdFromFk
//                val existingAccount = accountWithAmountMap[key]
//                if (!isTransfer(record.recordType)) {
//                    val incomeAmount = if (isIncome(record.recordType)) record.recordAmount else 0.0
//                    val expenseAmount = if (isExpense(record.recordType)) record.recordAmount else 0.0
//                    if (existingAccount != null) {
//                        accountWithAmountMap[key] = existingAccount.copy(
//                            incomeAmount = existingAccount.incomeAmount + incomeAmount,
//                            expenseAmount = existingAccount.expenseAmount + expenseAmount,
//                        )
//                    } else {
//                        val newAccount = AccountWithAmountType(
//                            account = userAccounts.first { it.accountId == record.accountIdFromFk },
//                            incomeAmount = incomeAmount,
//                            expenseAmount = expenseAmount,
//                        )
//                        accountWithAmountMap[key] = newAccount
//                    }
//                }
//            }
//            accountWithAmountMap.values.toList().sortedBy { it.account.accountName }
//        } catch (e: Exception) {
//            Log.e(TAG, "getUserAccountsWithAmountFromSpecificTimeError: ${e.message}")
//            null
//        }
//    }
override suspend fun getUserRecords(
    userId: String
): List<Record> {
    return try {
        val querySnapshot = recordCollection.whereEqualTo(
            "userIdFk", userId
        ).get().await()

        querySnapshot.toObjects()
    } catch (e: Exception) {
        Log.e(
            TAG,
            "getUserTotalAmountByTypeError: ${e.message}"
        )
        throw e
    }
}
    override suspend fun getUserRecordsFromSpecificTime(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Record> {
        return try {
            val querySnapshot = recordCollection.whereGreaterThanOrEqualTo(
                "recordTimestamp",
                TimestampConverter.fromDateTime(
                    startDate
                )
            ).whereLessThanOrEqualTo(
                "recordTimestamp",
                TimestampConverter.fromDateTime(
                    endDate
                )
            ).whereEqualTo(
                "userIdFk", userId
            ).orderBy(
                "recordTimestamp",
                Query.Direction.DESCENDING
            ).get().await()

            querySnapshot.toObjects<Record>()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "getUserRecordsFromSpecificTimeError: ${e.message}"
            )
            throw e
        }
    }
    override suspend fun getUserRecordsByType(
        userId: String, recordType: RecordType
    ): List<Record> {
        return try {
            val querySnapshot = recordCollection.whereEqualTo(
                "recordType", recordType
            ).whereEqualTo(
                "userIdFk", userId
            ).get().await()

            querySnapshot.toObjects()

        } catch (e: Exception) {
            Log.e(
                TAG,
                "getUserTotalAmountByTypeError: ${e.message}"
            )
            throw e
        }
    }

    override suspend fun getUserRecordsByTypeFromSpecificTime(
        userId: String,
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Record> {
        return try {
            val querySnapshot = recordCollection.whereGreaterThanOrEqualTo(
                "recordTimestamp",
                TimestampConverter.fromDateTime(
                    startDate
                )
            ).whereLessThanOrEqualTo(
                "recordTimestamp",
                TimestampConverter.fromDateTime(
                    endDate
                )
            ).whereEqualTo(
                "recordType", recordType
            ).whereEqualTo(
                "userIdFk", userId
            ).orderBy(
                "recordTimestamp",
                Query.Direction.DESCENDING
            ).get().await()

            querySnapshot.toObjects<Record>()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "getUserTotalAmountByTypeFromSpecificTimeError: ${e.message}"
            )
            throw e
        }
    }


    private suspend fun getTrueRecord(record: Record) = coroutineScope {
        val fromAccountDeferred = async {
            Firebase.firestore.collection("account").document(
                record.accountIdFromFk
            ).get().await()
        }

        val toAccountDeferred = async {
            Firebase.firestore.collection("account").document(
                record.accountIdToFk
            ).get().await()
        }

        val toCategoryDeferred = async {
            Firebase.firestore.collection("category").document(
                record.categoryIdFk
            ).get().await()
        }

        TrueRecord(
            record = record,
            fromAccount = fromAccountDeferred.await().toObject<Account>()!!,
            toAccount = toAccountDeferred.await().toObject<Account>()!!,
            toCategory = toCategoryDeferred.await().toObject<Category>()!!
        )
    }

    private suspend fun getTrueRecordsComponent(
        userId: String
    ) = coroutineScope {

        val fromAccountDeferred = async {
            getUserAccounts(userId)
        }

        val toAccountDeferred = async {
            getUserAccounts(userId)
        }

        val toCategoryDeferred = async {
            getUserCategories(userId)
        }

        TrueRecordComponentResult(
            fromAccount = fromAccountDeferred.await(),
            toAccount = toAccountDeferred.await(),
            toCategory = toCategoryDeferred.await()
        )
    }

    private suspend fun getUserAccounts(
        userId: String
    ) = Firebase.firestore.collection(
        "account"
    ).whereEqualTo(
        "userIdFk", userId
    ).get().await().toObjects<Account>()

    private suspend fun getUserCategories(
        userId: String
    ) = Firebase.firestore.collection(
        "category"
    ).whereEqualTo(
        "userIdFk", userId
    ).get().await().toObjects<Category>()

    data class TrueRecordComponentResult(
        val fromAccount: List<Account>,
        val toAccount: List<Account>,
        val toCategory: List<Category>,
    )
}
