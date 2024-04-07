package com.fredy.mysavings.Feature.Data.Database.Dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Feature.Data.Database.Model.RatesCache

@Dao
interface CurrencyCacheDao {
    @Upsert
    suspend fun upsertCurrencyCache(userData: RatesCache)

    @Delete
    suspend fun deleteCurrencyCache(userData: RatesCache)

    @Query("SELECT * FROM RatesCache WHERE cacheId = :cacheId")
    suspend fun getCurrencyCache(cacheId: String): RatesCache


}