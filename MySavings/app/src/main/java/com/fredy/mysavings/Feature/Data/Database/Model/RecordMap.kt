package com.fredy.mysavings.Feature.Data.Database.Model

import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import java.time.LocalDate

data class RecordMap(
    val recordDate: LocalDate,
    val records: List<TrueRecord>
){
    fun doesMatchSearchQuery(query: String): Boolean {
        return records.any { it.doesMatchSearchQuery(query) }
    }

}