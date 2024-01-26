package com.fredy.mysavings.Data.Database.Dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Data.Database.Model.CurrencyCache
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyCacheDao {
    @Upsert
    suspend fun upsertCurrencyCache(userData: CurrencyCache)

    @Delete
    suspend fun deleteCurrencyCache(userData: CurrencyCache)

    @Query("SELECT * FROM CurrencyCache WHERE cacheId = :id")
    fun getCurrencyCache(id: String): Flow<CurrencyCache>

    @Query("SELECT * FROM CurrencyCache ORDER BY timestamp ASC")
    fun getAllCurrencyCachesOrderedByTime(): Flow<List<CurrencyCache>>

}