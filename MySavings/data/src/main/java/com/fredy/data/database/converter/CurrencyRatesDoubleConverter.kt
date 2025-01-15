package com.fredy.data.database.converter

import androidx.room.TypeConverter
import com.fredy.domain.model.Rate
import com.google.gson.Gson
import timber.log.Timber

data class CurrencyRatesWrapper(val rates: List<Rate>)

object CurrencyRatesDoubleConverter {
    @TypeConverter
    @JvmStatic
    fun toRates(json: String): List<Rate>? {
        val gson = Gson()
        val wrapper = gson.fromJson(json, CurrencyRatesWrapper::class.java)
        Timber.d("toRates: $wrapper")
        return wrapper?.rates
    }

    @TypeConverter
    @JvmStatic
    fun fromRates(rates: List<Rate>): String {
        val gson = Gson()
        val wrapper = CurrencyRatesWrapper(rates)
        Timber.d("fromRates: $wrapper")
        return gson.toJson(wrapper)
    }
}