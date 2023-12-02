package com.fredy.mysavings.Repository

import android.util.Log
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Data.Database.Enum.RecordType
import com.fredy.mysavings.Util.TAG
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject


interface RecordRepository {
    suspend fun upsertRecordItem(record: Record)
    suspend fun deleteRecordItem(record: Record)
    fun getRecordById(recordId: String): Flow<TrueRecord>
    fun getUserRecordsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<TrueRecord>>

    fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String
    ): Flow<List<TrueRecord>>

    fun getUserAccountRecordsOrderedByDateTime(
        accountId: String
    ): Flow<List<TrueRecord>>

    fun getUserTotalAmountByType(recordType: RecordType): Flow<Double>
    fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<Double>

    fun getUserTotalRecordBalance(): Flow<Double>
}

class RecordRepositoryImpl(): RecordRepository {

    override suspend fun upsertRecordItem(
        record: Record
    ) {
        val currentUser = Firebase.auth.currentUser
        Log.e(TAG, "upsertRecordItem: " + record)
        val recordCollection = Firebase.firestore.collection(
            "record"
        )
        if (record.recordId.isEmpty()) {
            recordCollection.add(
                record
            ).addOnSuccessListener { document ->
                recordCollection.document(
                    document.id
                ).set(
                    record.copy(
                        recordId = document.id,
                        userIdFk = currentUser!!.uid
                    )
                )
            }
        } else {
            recordCollection.document(
                record.recordId
            ).set(
                record.copy(
                    userIdFk = currentUser!!.uid
                )
            )
        }
    }

    override suspend fun deleteRecordItem(
        record: Record
    ) {
        Log.e(TAG, "deleteRecordItem: " + record)
        Firebase.firestore.collection("record").document(
            record.recordId
        ).delete()
    }

    override fun getRecordById(recordId: String): Flow<TrueRecord> {
        Log.e(TAG, "getRecordById: " + recordId)
        return flow {
            val record = Firebase.firestore.collection(
                "record"
            ).document(
                recordId
            ).get().await().toObject<Record>()!!
            val result = getDocuments(
                record
            )
            emit(
                TrueRecord(
                    record = record,
                    fromAccount = result.fromAccount.await().toObject<Account>()!!,
                    toAccount = result.toAccount.await().toObject<Account>()!!,
                    toCategory = result.toCategory.await().toObject<Category>()!!
                )
            )

        }
    }

    override fun getUserRecordsFromSpecificTime(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = callbackFlow<List<TrueRecord>> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserRecordsFromSpecificTime: $startDate\n:\n$endDate"
        )

        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordDateTime", startDate
        ).whereLessThanOrEqualTo(
            "recordDateTime", endDate
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.uid
        ).orderBy(
            "recordDateTime",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    TAG,
                    "getUserRecordsFromSpecificTimeError: ${it.message}"
                )
                close(it)
                return@addSnapshotListener
            }

            value?.let { it ->
                val recordDocuments = it.documents

                launch {
                    val data = recordDocuments.map { document ->
                        Log.e(
                            TAG,
                            "getUserRecordsFromSpecificTime0.1: $document"
                        )
                        val record = document.toObject<Record>()!!
                        Log.e(
                            TAG,
                            "getUserRecordsFromSpecificTime0.2: $record"
                        )
                        val result = getDocuments(
                            record
                        )
                        val fromAccount =  result.fromAccount.await().toObject<Account>()!!
                        val toAccount =result.toAccount.await().toObject<Account>()!!
                        val toCategory =  result.toCategory.await().toObject<Category>()!!

                        TrueRecord(
                            record = record,
                            fromAccount = fromAccount,
                            toAccount = toAccount,
                            toCategory = toCategory
                        )
                    }
                    Log.e(
                        TAG,
                        "getUserRecordsFromSpecificTime0.0: $data"
                    )
                    trySend(data)
                }
            }
        }

        Log.e(
            TAG,
            "getUserRecordsFromSpecificTime0.0: babi"
        )

        awaitClose {
            listener.remove()
        }
    }


    override fun getUserCategoryRecordsOrderedByDateTime(
        categoryId: String,
    ) = callbackFlow<List<TrueRecord>> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserCategoryRecordsOrderedByDateTime: " + categoryId,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "categoryIdFk", categoryId
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.uid
        ).orderBy(
            "recordDateTime",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let { result ->
                val recordDocuments = result.documents
                launch {
                    val data = recordDocuments.map { document ->
                        Log.e(
                            TAG,
                            "getUserRecordsFromSpecificTime0.1: $document"
                        )
                        val record = document.toObject<Record>()!!
                        Log.e(
                            TAG,
                            "getUserRecordsFromSpecificTime0.2: $record"
                        )
                        val result = getDocuments(
                            record
                        )

                        val fromAccount = result.fromAccount.await().toObject<Account>()!!
                        val toAccount = result.toAccount.await().toObject<Account>()!!
                        val toCategory = result.toCategory.await().toObject<Category>()!!

                        TrueRecord(
                            record = record,
                            fromAccount = fromAccount,
                            toAccount = toAccount,
                            toCategory = toCategory
                        )
                    }
                    Log.e(
                        TAG,
                        "getUserRecordsFromSpecificTime0.0: $data"
                    )
                    trySend(data)
                }
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserAccountRecordsOrderedByDateTime(
        accountId: String,
    ) = callbackFlow<List<TrueRecord>> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserAccountRecordsOrderedByDateTime: " + accountId,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "accountIdFk", accountId
        ).whereIn(
            "accountfk", listOf(accountId)
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.uid
        ).orderBy(
            "recordDateTime",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                close(it)
            }
            value?.let { result ->
                val recordDocuments = result.documents
                launch {
                    val data = recordDocuments.map { document ->
                        Log.e(
                            TAG,
                            "getUserRecordsFromSpecificTime0.1: $document"
                        )
                        val record = document.toObject<Record>()!!
                        Log.e(
                            TAG,
                            "getUserRecordsFromSpecificTime0.2: $record"
                        )
                        val result = getDocuments(
                            record
                        )

                        val fromAccount = result.fromAccount.await().toObject<Account>()!!
                        val toAccount = result.toAccount.await().toObject<Account>()!!
                        val toCategory = result.toCategory.await().toObject<Category>()!!

                        TrueRecord(
                            record = record,
                            fromAccount = fromAccount,
                            toAccount = toAccount,
                            toCategory = toCategory
                        )
                    }
                    Log.e(
                        TAG,
                        "getUserRecordsFromSpecificTime0.0: $data"
                    )
                    trySend(data)
                }
            }
        }
        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalAmountByType(
        recordType: RecordType
    ) = callbackFlow<Double> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserTotalAmountByType: " + recordType,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "recordType", recordType
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.uid
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.sumOf { document ->
                    document.toObject<Record>().recordAmount
                }
                Log.e(
                    TAG,
                    "getUserTotalAmountByTypeResult: " + data,

                    )
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalAmountByTypeFromSpecificTime(
        recordType: RecordType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) = callbackFlow<Double> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserTotalAmountByTypeFromSpecificTime: " + recordType,

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereGreaterThanOrEqualTo(
            "recordDateTime", startDate
        ).whereLessThanOrEqualTo(
            "recordDateTime", endDate
        ).whereEqualTo(
            "recordType", recordType
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.uid
        ).orderBy(
            "recordDateTime",
            Query.Direction.DESCENDING
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.sumOf { document ->
                    document.toObject<Record>().recordAmount
                }
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    override fun getUserTotalRecordBalance() = callbackFlow<Double> {
        val currentUser = Firebase.auth.currentUser
        Log.e(
            TAG,
            "getUserTotalRecordBalance: ",

            )
        val listener = Firebase.firestore.collection(
            "record"
        ).whereEqualTo(
            "userIdFk",
            currentUser!!.uid
        ).addSnapshotListener { value, error ->
            error?.let {
                Log.e(
                    "BABI",
                    "getUserAccountTotalBalance2: " + it.message,
                )
            }
            value?.let {
                val data = it.sumOf { document ->
                    document.toObject<Record>().recordAmount
                }
                Log.e(
                    TAG,
                    "getUserTotalRecordBalanceResult: " + data,

                    )
                trySend(data)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun getDocuments(record: Record): DocumentResults = coroutineScope {
        val fromAccountDeferred = async {
            Firebase.firestore.collection("account").document(record.accountIdFromFk).get()
        }

        val toAccountDeferred = async {
            Firebase.firestore.collection("account").document(record.accountIdToFk).get()
        }

        val toCategoryDeferred = async {
            Firebase.firestore.collection("category").document(record.categoryIdFk).get()
        }

        DocumentResults(
            fromAccount = fromAccountDeferred.await(),
            toAccount = toAccountDeferred.await(),
            toCategory = toCategoryDeferred.await()
        )
    }

}

data class DocumentResults(
    val fromAccount: Task<DocumentSnapshot>,
    val toAccount: Task<DocumentSnapshot>,
    val toCategory: Task<DocumentSnapshot>,
)

data class TrueRecord(
    val record: Record = Record(),
    val fromAccount: Account = Account(),
    val toAccount: Account = Account(),
    val toCategory: Category = Category(),
)
