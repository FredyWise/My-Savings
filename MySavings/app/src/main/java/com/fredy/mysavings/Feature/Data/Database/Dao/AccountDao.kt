package com.fredy.mysavings.Feature.Data.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Upsert
    suspend fun upsertAccountItem(account: Account)

    @Upsert
    suspend fun upsertAllAccountItem(accounts: List<Account>)

    @Delete
    suspend fun deleteAccountItem(account: Account)

    @Query("DELETE FROM account")
    suspend fun deleteAllAccounts()

    @Query("SELECT * FROM account WHERE accountId = :accountId")
    suspend fun getAccount(accountId: String): Account

    @Query("SELECT * FROM account WHERE userIdFk = :userId")
    fun getUserAccounts(userId: String): Flow<List<Account>>

    @Query("SELECT DISTINCT accountCurrency FROM account WHERE userIdFk = :userId")
    fun getUserAvailableCurrency(userId: String): Flow<List<String>>

}