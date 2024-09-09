package com.fredy.domain.model

data class CategoryWithAmount(
    val category: Category = Category(),
    val amount: Double = 0.0,
    val currency: String = ""
)