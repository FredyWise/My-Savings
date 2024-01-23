package com.fredy.mysavings.Data.Database.Converter

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


object TimestampConverter {
    @TypeConverter
    @JvmStatic
    fun toDateTime(timestamp: Timestamp): LocalDateTime {
        val instant = Instant.ofEpochSecond(
            timestamp.seconds,
            timestamp.nanoseconds.toLong()
        )
        return LocalDateTime.ofInstant(
            instant,
            ZoneOffset.UTC
        )
    }
    @TypeConverter
    @JvmStatic
    fun fromDateTime(dateTime: LocalDateTime): Timestamp {
        return Timestamp(
            dateTime.toEpochSecond(
                ZoneOffset.UTC
            ), 0
        )
    }
}