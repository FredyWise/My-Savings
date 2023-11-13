package com.fredy.mysavings.Data.RoomDatabase.Converter

import androidx.room.TypeConverter
import java.time.LocalDateTime

object DateTimeConverter {
    @TypeConverter
    @JvmStatic
    fun toDateTime(dateTimeInt: Int): LocalDateTime {
        val dateTimeString = dateTimeInt.toString()
        val year = dateTimeString.substring(0, 4).toInt()
        val month = dateTimeString.substring(4, 6).toInt()
        val day = dateTimeString.substring(6, 8).toInt()
        return LocalDateTime.of(year, month, day, 0, 0)
    }

    @TypeConverter
    @JvmStatic
    fun fromDateTime(dateTime: LocalDateTime): Int {
        val year = dateTime.year
        val month = dateTime.monthValue
        val day = dateTime.dayOfMonth
        return "%04d%02d%02d".format(year, month, day).toInt()
    }
}



