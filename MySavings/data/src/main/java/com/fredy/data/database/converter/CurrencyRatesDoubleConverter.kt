package com.fredy.data.database.converter

import androidx.room.TypeConverter
import com.fredy.data.api.currencyModels.currencyDTO.Rates
import com.google.gson.Gson

object CurrencyRatesDoubleConverter {

    @TypeConverter
    @JvmStatic
    fun toRates(json: String): Rates {
        val gson = Gson()
        return gson.fromJson(json, Rates::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromRates(rates: Rates): String {
        val gson = Gson()
        return gson.toJson(rates)
    }
}