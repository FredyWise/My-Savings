package com.fredy.mysavings.Data.User

import com.fredy.mysavings.Data.tempAccounts
import com.fredy.mysavings.Data.tempCategories

object UserData {
    val accounts: List<Account> = tempAccounts
    val categories: List<Category> = tempCategories
    fun getAccount(accountName: String?): Account {
        return accounts.first { it.name == accountName }
    }
    fun getCategory(categoryName: String?): Category {
        return categories.first { it.name == categoryName }
    }
}
fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}