package com.fredy.mysavings.Data.Mappers

import com.fredy.mysavings.Data.Database.FirebaseDataSource.RecordDataSourceImpl.TrueRecordComponentResult
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.ViewModels.RecordMap

fun List<Record>.toTrueRecords(trueRecordComponentResult: TrueRecordComponentResult):List<TrueRecord>{
    return this.map { record->
        TrueRecord(
            record = record,
            fromAccount = trueRecordComponentResult.fromAccount.single { it.accountId == record.accountIdFromFk },
            toAccount = trueRecordComponentResult.toAccount.single { it.accountId == record.accountIdToFk },
            toCategory = trueRecordComponentResult.toCategory.single { it.categoryId == record.categoryIdFk },
        )
    }
}

fun List<Record>.filterRecordCurrency(currency:List<String>):List<Record>{
    return this.filter {
        currency.contains(
            it.recordCurrency
        ) || currency.isEmpty()
    }
}

fun List<TrueRecord>.toRecordSortedMaps(sortType: SortType = SortType.DESCENDING):List<RecordMap>{
    return this.groupBy {
        it.record.recordDateTime.toLocalDate()
    }.toSortedMap(when (sortType) {
        SortType.ASCENDING -> compareBy { it }
        SortType.DESCENDING -> compareByDescending { it }
    } ).map {
        RecordMap(
            recordDate = it.key,
            records = it.value
        )
    }
}
fun List<TrueRecord>.filterTrueRecordCurrency(currency:List<String>):List<TrueRecord>{
    return this.filter {
        currency.contains(
            it.record.recordCurrency
        ) || currency.isEmpty()
    }
}