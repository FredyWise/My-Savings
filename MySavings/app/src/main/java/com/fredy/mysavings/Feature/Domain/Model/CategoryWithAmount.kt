package com.fredy.mysavings.Feature.Domain.Model

import com.fredy.mysavings.Feature.Domain.Model.Category

data class CategoryWithAmount(
    val category: Category = Category(),
    val amount: Double = 0.0,
    val currency: String = ""
)