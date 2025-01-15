package com.fredy.data.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.domain.model.Rate
import com.google.firebase.Timestamp

@Entity
data class RatesCache(
    @PrimaryKey
    val cacheId: String = "",
    val base: String = "",
    val date: String = "",
    val rates: List<Rate> = emptyList(),
    val success: Boolean = false,
    val timestamp: Int = 0,
    val cachedTime: Timestamp = Timestamp.now()
)