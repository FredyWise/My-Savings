package com.fredy.mysavings.Data.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Data.Database.Model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Upsert
    suspend fun upsertAccountItem(account: Account)
    @Delete
    suspend fun deleteAccountItem(account: Account)
    @Query("SELECT * FROM account WHERE accountId = :accountId")
    suspend fun getAccount(accountId: String): Account
    @Query("SELECT * FROM account WHERE userIdFk = :userId")
    suspend fun getUserAccounts(userId: String): List<Account>
    @Query("SELECT DISTINCT accountCurrency FROM account WHERE userIdFk = :userId")
    suspend fun getUserAvailableCurrency(userId: String): List<String>

}