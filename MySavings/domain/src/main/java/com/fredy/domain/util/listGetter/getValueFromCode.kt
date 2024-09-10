package com.fredy.domain.util.listGetter

import com.fredy.domain.model.Rate

fun List<Rate>.getValueFromCode(code: String): Double? {
    return this.find { it.code == code }?.value
}

fun List<Rate>.updateRatesUsingCode(code: String, value: Double): List<Rate> {
    val index = this.indexOfFirst { it.code == code }
    val updatedList = this.toMutableList()
    updatedList[index] = updatedList[index].copy(value = value)
    return updatedList
}