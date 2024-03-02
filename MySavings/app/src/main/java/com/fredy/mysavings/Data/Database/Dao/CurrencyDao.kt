package com.fredy.mysavings.Data.Database.Dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Currency
import com.fredy.mysavings.Data.Database.Model.CurrencyInfoCache
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Upsert
    suspend fun upsertCurrency(currency: Currency)
    @Upsert
    suspend fun upsertAllCurrencies(currencies: List<Currency>)

    @Delete
    suspend fun deleteCurrency(currency: Currency)

    @Query("SELECT * FROM Currency WHERE name = :name AND userId = :userId")
    suspend fun getCurrency(name: String,userId:String): Currency

    @Query("SELECT * FROM Currency WHERE userId = :userId")
    fun getCurrencies(userId:String): Flow<List<Currency>>

}