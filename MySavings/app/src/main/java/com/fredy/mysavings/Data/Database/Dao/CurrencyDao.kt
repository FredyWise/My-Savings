package com.fredy.mysavings.Data.Database.Dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Database.Model.CurrencyInfoCache

@Dao
interface CurrencyDao {
    @Upsert
    suspend fun upsertCurrency(userData: Currency)

    @Delete
    suspend fun deleteCurrency(userData: Currency)

    @Query("SELECT * FROM Currency WHERE name = :name")
    suspend fun getCurrency(name: String): Currency

    @Query("SELECT * FROM Currency")
    suspend fun getCurrencies(): List<Currency>

}