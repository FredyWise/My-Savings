package com.fredy.mysavings.Feature.Data.Database.Dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Feature.Domain.Model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Upsert
    suspend fun upsertCurrency(currency: Currency)
    @Upsert
    suspend fun upsertAllCurrencies(currencies: List<Currency>)

    @Delete
    suspend fun deleteCurrency(currency: Currency)

    @Query("SELECT * FROM Currency WHERE code = :code AND userIdFk = :userId")
    suspend fun getCurrencyByCode(code: String,userId:String): Currency

    @Query("SELECT * FROM Currency WHERE userIdFk = :userId")
    fun getCurrencies(userId:String): Flow<List<Currency>>

}