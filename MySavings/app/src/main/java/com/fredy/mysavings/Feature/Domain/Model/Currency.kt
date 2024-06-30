package com.fredy.mysavings.Feature.Domain.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency(
    @PrimaryKey
    val currencyId:String="",
    val code:String = "",
    val userIdFk:String = "",
    val name:String = "",
    val symbol:String = "",
    val value:Double = 0.0,
    val url: String = "",
    val alt: String = ""
)
