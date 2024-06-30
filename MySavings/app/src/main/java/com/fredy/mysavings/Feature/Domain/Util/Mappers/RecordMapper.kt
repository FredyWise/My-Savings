package com.fredy.mysavings.Feature.Domain.Util.Mappers

import com.fredy.mysavings.Feature.Data.APIs.TabScannerModel.Response.ResultResponse
import com.fredy.mysavings.Feature.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Feature.Data.Database.FirestoreDataSource.RecordDataSourceImpl.TrueRecordComponentResult
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.BookMap
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.RecordMap
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun List<Record>.toTrueRecords(trueRecordComponentResult: TrueRecordComponentResult): List<TrueRecord> {
    return this.map { record ->
        TrueRecord(
            record = record,
            fromWallet = trueRecordComponentResult.fromWallet.single { it.walletId == record.walletIdFromFk },
            toWallet = trueRecordComponentResult.toWallet.single { it.walletId == record.walletIdToFk },
            toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
        )
    }
}

fun List<Record>.filterRecordCurrency(currency: List<String>): List<Record> {
    return this.filter {
        currency.contains(
            it.recordCurrency
        ) || currency.isEmpty()
    }
}

fun List<TrueRecord>.toRecordSortedMaps(sortType: SortType = SortType.DESCENDING): List<RecordMap> {
    return this.groupBy {
        it.record.recordDateTime.toLocalDate()
    }.toSortedMap(when (sortType) {
        SortType.ASCENDING -> compareBy { it }
        SortType.DESCENDING -> compareByDescending { it }
    }).map {
        RecordMap(
            recordDate = it.key,
            records = it.value
        )
    }
}


fun List<TrueRecord>.toBookSortedMaps(
    books: List<Book>,
    sortType: SortType = SortType.DESCENDING
): List<BookMap> {
    return books.map { book ->
        val records = this.filter { it.record.bookIdFk == book.bookId }
        BookMap(
            book = book,
            recordMaps = records.toRecordSortedMaps(sortType)
        )
    }
}


fun List<TrueRecord>.filterTrueRecordCurrency(currency: List<String>): List<TrueRecord> {
    return this.filter {
        currency.contains(
            it.record.recordCurrency
        ) || currency.isEmpty()
    }
}

fun ResultResponse.convertToRecords(): List<Record> {
    val records = mutableListOf<Record>()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val recordTimestamp =
        TimestampConverter.fromDateTime(LocalDateTime.parse(this.result.date, formatter))

    this.result.lineItems.forEachIndexed { index, item ->
        val discount = item.discount.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val recordAmount =
            item.lineTotal.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
        Log.e("${recordAmount}")
        val recordNotes = """
            Address: ${this.result.address}
            Establishment: ${this.result.establishment}
            Document Type: ${this.result.documentType}
            Item Description: ${item.desc}
            Item Quantity: ${item.qty}
            """.trimIndent()

        val record = Record(
            recordId = "$index",
            walletIdFromFk = "",
            walletIdToFk = "",
            categoryIdFk = "",
            userIdFk = "",
            bookIdFk = "",
            recordTimestamp = recordTimestamp,
            recordAmount = recordAmount,
            recordCurrency = this.result.currency,
            recordType = RecordType.Expense,
            recordNotes = recordNotes
        )

        records.add(record)
    }

    this.result.summaryItems.firstOrNull { it.lineType == "Discount" }?.let { item ->
        val recordAmount =
            item.lineTotal.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val record = Record(
            recordId = "Discount",
            walletIdFromFk = "",
            walletIdToFk = "",
            categoryIdFk = "",
            userIdFk = "",
            bookIdFk = "",
            recordTimestamp = recordTimestamp,
            recordAmount = recordAmount,
            recordCurrency = this.result.currency,
            recordType = RecordType.Income,
            recordNotes = "Item Description: Total Discount: \n"
        )

        records.add(record)
    }

    return records
}
