package com.fredy.mysavings.Data.Database.Converter

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

object TimestampConverter {
    fun toDateTime(timestamp: Timestamp): LocalDateTime {
        val instant = timestamp.toDate().toInstant()
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    fun fromDateTime(dateTime: LocalDateTime): Timestamp {
        val zonedDateTime = dateTime.atZone(ZoneId.systemDefault())
        return Timestamp(Date.from(zonedDateTime.toInstant()))
    }
}