package com.fredy.domain.mappers.recordUIMapper

import com.fredy.domain.enums.SortType
import com.fredy.domain.model.RecordMap
import com.fredy.domain.model.TrueRecord

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

