package com.fredy.data.mappers


import com.fredy.data.database.converter.TimestampConverter
import com.fredy.data.database.firestoreDataSource.RecordDataSourceImpl
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Book
import com.fredy.domain.model.BookMap
import com.fredy.domain.model.RecordMap
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.fredy.data.database.dto.Record as DataRecord
import com.fredy.data.database.dto.TrueRecord as DataTrueRecord

import com.fredy.domain.model.Record as DomainRecord
import com.fredy.domain.model.TrueRecord as DomainTrueRecord

fun DataRecord.toDomainRecord(): DomainRecord {
    return DomainRecord(
        recordId,
        walletIdFromFk,
        walletIdToFk,
        categoryIdFk,
        userIdFk,
        bookIdFk,
        recordDateTime,
        recordAmount,
        recordCurrency,
        recordType,
        recordNotes,
    )
}

fun DomainRecord.toDataRecord(): DataRecord {
    return DataRecord(
        recordId,
        walletIdFromFk,
        walletIdToFk,
        categoryIdFk,
        userIdFk,
        bookIdFk,
        TimestampConverter.fromDateTime(recordDateTime),
        recordAmount,
        recordCurrency,
        recordType,
        recordNotes,
    )
}

fun List<DataRecord>.toDomainRecords(): List<DomainRecord> {
    return this.map { it.toDomainRecord() }
}

fun List<DomainRecord>.toDataRecords(): List<DataRecord> {
    return this.map { it.toDataRecord() }
}

fun DataTrueRecord.toDomainTrueRecord(): DomainTrueRecord {
    return DomainTrueRecord(
        record = record.toDomainRecord(),
        fromWallet = fromWallet.toDomainWallet(),
        toWallet = toWallet.toDomainWallet(),
        toCategory = toCategory.toDomainCategory(),
    )
}

fun List<DataTrueRecord>.toDomainTrueRecords(): List<DomainTrueRecord> {
    return this.map { it.toDomainTrueRecord() }
}


fun List<DataRecord>.toTrueRecords(trueRecordComponentResult: RecordDataSourceImpl.TrueRecordComponentResult): List<DataTrueRecord> {
    return this.map { record ->
        DataTrueRecord(
            record = record,
            fromWallet = trueRecordComponentResult.fromWallet.single { it.walletId == record.walletIdFromFk },
            toWallet = trueRecordComponentResult.toWallet.single { it.walletId == record.walletIdToFk },
            toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
        )
    }
}



//fun List<DomainTrueRecord>.toRecordSortedMaps(sortType: SortType = SortType.DESCENDING): List<RecordMap> {
//    return this.groupBy {
//        it.record.recordDateTime.toLocalDate()
//    }.toSortedMap(when (sortType) {
//        SortType.ASCENDING -> compareBy { it }
//        SortType.DESCENDING -> compareByDescending { it }
//    }).map {
//        RecordMap(
//            recordDate = it.key,
//            records = it.value
//        )
//    }
//}

//
//fun List<DomainTrueRecord>.toBookSortedMaps(
//    books: List<Book>,
//    sortType: SortType = SortType.DESCENDING
//): List<BookMap> {
//    return books.map { book ->
//        val records = this.filter { it.record.bookIdFk == book.bookId }
//        BookMap(
//            book = book,
//            recordMaps = records.toRecordSortedMaps(sortType)
//        )
//    }
//}
//

//
//fun ResultResponse.convertToDataRecords(): List<DataRecord> {
//    val records = mutableListOf<DataRecord>()
//    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//    val recordTimestamp =
//        TimestampConverter.fromDateTime(LocalDateTime.parse(this.result.date, formatter))
//
//    this.result.lineItems.forEachIndexed { index, item ->
//        val discount = item.discount.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
//        val recordAmount =
//            item.lineTotal.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
//        Timber.e("${recordAmount}")
//        val recordNotes = """
//            Address: ${this.result.address}
//            Establishment: ${this.result.establishment}
//            Document Type: ${this.result.documentType}
//            Item Description: ${item.desc}
//            Item Quantity: ${item.qty}
//            """.trimIndent()
//
//        val record = DataRecord(
//            recordId = "$index",
//            walletIdFromFk = "",
//            walletIdToFk = "",
//            categoryIdFk = "",
//            userIdFk = "",
//            bookIdFk = "",
//            recordTimestamp = recordTimestamp,
//            recordAmount = recordAmount,
//            recordCurrency = this.result.currency,
//            recordType = RecordType.Expense,
//            recordNotes = recordNotes
//        )
//
//        records.add(record)
//    }
//
//    this.result.summaryItems.firstOrNull { it.lineType == "Discount" }?.let { item ->
//        val recordAmount =
//            item.lineTotal.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
//        val record = DataRecord(
//            recordId = "Discount",
//            walletIdFromFk = "",
//            walletIdToFk = "",
//            categoryIdFk = "",
//            userIdFk = "",
//            bookIdFk = "",
//            recordTimestamp = recordTimestamp,
//            recordAmount = recordAmount,
//            recordCurrency = this.result.currency,
//            recordType = RecordType.Income,
//            recordNotes = "Item Description: Total Discount: \n"
//        )
//
//        records.add(record)
//    }
//
//    return records
//}
