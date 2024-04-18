package com.fredy.mysavings.Util.Mappers

import com.fredy.mysavings.Feature.Data.Database.FirebaseDataSource.RecordDataSourceImpl.TrueRecordComponentResult
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.BookMap
import com.fredy.mysavings.Feature.Domain.Model.RecordMap

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

fun List<Book>.toBookSortedMaps(
    trueRecords: List<TrueRecord>,
    sortType: SortType = SortType.DESCENDING
): List<BookMap> {
    return this.map { book ->
        val records = trueRecords.filter { it.record.bookIdFk == book.bookId }
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