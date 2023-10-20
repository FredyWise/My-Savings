package com.fredy.mysavings.Data.User

import com.fredy.mysavings.Data.tempAccounts
import com.fredy.mysavings.Data.tempCategories

object UserData {
    val accounts: MutableList<Account> = tempAccounts
    val categories: MutableList<Category> = tempCategories
    var currency: String = "USD"
    fun addAccount(account: Account) {
        accounts.add(account)
    }
    fun deleteAccount(accountName: String?) {
        val accountToRemove = accounts.find { it.name == accountName }
        if (accountToRemove != null) {
            accounts.remove(accountToRemove)
        }
    }
    fun addCategory(category: Category) {
        categories.add(category)
    }
    fun deleteCategory(categoryName: String?) {
        val categoryToRemove = categories.find { it.name == categoryName }
        if (categoryToRemove != null) {
            categories.remove(categoryToRemove)
        }
    }
    fun getAccount(accountName: String?): Account {
        return accounts.first { it.name == accountName }
    }
    fun getCategory(categoryName: String?): Category {
        return categories.first { it.name == categoryName }
    }
}
