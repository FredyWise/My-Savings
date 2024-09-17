package com.fredy.domain.mappers.recordUIMapper

import com.fredy.domain.model.TrueRecord

fun List<TrueRecord>.filterTrueRecordCurrency(currency: List<String>): List<TrueRecord> {
    return this.filter {
        currency.contains(
            it.record.recordCurrency
        ) || currency.isEmpty()
    }
}