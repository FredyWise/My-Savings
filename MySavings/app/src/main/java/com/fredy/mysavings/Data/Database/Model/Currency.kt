package com.fredy.mysavings.Data.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency(
    @PrimaryKey
    val code:String,
    val name:String,
    val symbol:String,
    val value:Double,
    val url: String,
    val alt: String
)
