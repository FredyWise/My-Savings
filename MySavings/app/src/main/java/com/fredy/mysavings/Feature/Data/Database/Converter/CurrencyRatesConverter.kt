package com.fredy.mysavings.Feature.Data.Database.Converter

import androidx.room.TypeConverter
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.Rates
import com.google.gson.Gson

object CurrencyRatesConverter {
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