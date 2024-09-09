package com.fredy.mysavings.Feature.Domain.Model

import com.fredy.domain.model.TrueRecord
import java.time.LocalDate

data class RecordMap(
    val recordDate: LocalDate,
    val records: List<TrueRecord>
){
    open fun doesMatchSearchQuery(query: String): Boolean {
        return records.any { it.doesMatchAllSearchQuery(query) }
    }

}