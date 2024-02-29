package com.fredy.mysavings.Data.Database.Converter

import androidx.room.TypeConverter
import com.fredy.mysavings.Data.APIs.CountryModels.Response.CurrencyInfoItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CurrencyInfoResponseConverter {

    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromCurrencyInfoItems(items: List<CurrencyInfoItem>): String {
        return gson.toJson(items)
    }

    @TypeConverter
    @JvmStatic
    fun toCurrencyInfoItems(json: String): List<CurrencyInfoItem> {
        return gson.fromJson(json, object : TypeToken<List<CurrencyInfoItem>>() {}.type)
    }
}
