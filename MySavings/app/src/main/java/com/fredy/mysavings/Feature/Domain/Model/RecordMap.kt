package com.fredy.mysavings.Feature.Domain.Model

import java.time.LocalDate

data class RecordMap(
    val recordDate: LocalDate,
    val records: List<TrueRecord>
){
    fun doesMatchSearchQuery(query: String): Boolean {
        return records.any { it.doesMatchAllSearchQuery(query) }
    }

}