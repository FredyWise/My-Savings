package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
    @PrimaryKey(autoGenerate = true)
    val accountId: Int = 0,
    val accountName: String = "",
    var accountAmount: Double = 0.0,
    var accountCurrency: String = "",
    val accountIcon: Int = 0,
    val accountIconDescription: String = "",
)

