package com.fredy.mysavings.Data.RoomDatabase.Converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeConverter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd, HH:mm:ss")

    @TypeConverter
    @JvmStatic
    fun toDateTime(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    @TypeConverter
    @JvmStatic
    fun fromDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(formatter)
    }
}


