package com.fredy.data.model

import com.fredy.domain.enums.BudgetPeriodType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Budget(
    val budgetId: String = "",
    val categoryId: String = "",
    var amount: Double = 0.0,
    val allocated: Double = 0.0, // needs to add allocated balance on wallet
    var spent: Double = 0.0,
    val budgetCurrency: String = "",
    var period: BudgetPeriodType = BudgetPeriodType.MONTHLY,
    val startPeriod: LocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
    val endPeriod: LocalDateTime = LocalDateTime.of(LocalDate.now().plusMonths(1), LocalTime.MAX),
)