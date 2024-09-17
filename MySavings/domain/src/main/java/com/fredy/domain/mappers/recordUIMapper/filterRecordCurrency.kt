package com.fredy.domain.mappers.recordUIMapper

import com.fredy.domain.model.Record

fun List<Record>.filterRecordCurrency(currency: List<String>): List<Record> {
    return this.filter {
        currency.contains(
            it.recordCurrency
        ) || currency.isEmpty()
    }
}