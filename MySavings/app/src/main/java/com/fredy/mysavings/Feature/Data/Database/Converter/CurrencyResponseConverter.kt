package com.fredy.mysavings.Feature.Data.Database.Converter

import androidx.room.TypeConverter
import com.fredy.mysavings.Feature.Data.APIs.CurrencyModels.Response.CurrencyResponse
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