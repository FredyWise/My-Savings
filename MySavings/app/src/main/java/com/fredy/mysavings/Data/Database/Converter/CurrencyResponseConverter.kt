package com.fredy.mysavings.Data.Database.Converter

import androidx.room.TypeConverter
import com.fredy.mysavings.Data.APIs.CurrencyModels.Response.CurrencyResponse
import com.fredy.mysavings.Data.Database.Model.CurrencyCache
import com.google.gson.Gson

object CurrencyResponseConverter {
    @TypeConverter
    @JvmStatic
    fun toCurrencyResponse(json: String): CurrencyResponse {
        val gson = Gson()
        return gson.fromJson(json, CurrencyResponse::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromCurrencyResponse(response: CurrencyResponse): String {
        val gson = Gson()
        return gson.toJson(response)
    }
}