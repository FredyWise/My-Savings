package com.fredy.mysavings.Feature.Domain.Model

import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import java.time.LocalDate

data class RecordMap(
    val recordDate: LocalDate,
    val records: List<TrueRecord>
){
    fun doesMatchSearchQuery(query: String): Boolean {
        return records.any { it.doesMatchSearchQuery(query) }
    }

}