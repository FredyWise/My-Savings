package com.fredy.data.enums.checker

import com.fredy.data.enums.RecordType

fun RecordType.isTransfer(): Boolean {
    return this == RecordType.Transfer
}

fun RecordType.isExpense(): Boolean {
    return this == RecordType.Expense
}

fun RecordType.isIncome(): Boolean {
    return this == RecordType.Income
}