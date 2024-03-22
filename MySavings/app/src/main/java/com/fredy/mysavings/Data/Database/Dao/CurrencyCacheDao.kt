package com.fredy.mysavings.Data.Database.Dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Data.Database.Model.RatesCache

@Dao
interface CurrencyCacheDao {
    @Upsert
    suspend fun upsertCurrencyCache(userData: RatesCache)

    @Delete
    suspend fun deleteCurrencyCache(userData: RatesCache)

    @Query("SELECT * FROM RatesCache WHERE base = :base")
    suspend fun getCurrencyCache(base: String): RatesCache


}