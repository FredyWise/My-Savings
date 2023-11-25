package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.R

@Entity
data class Account(
    @PrimaryKey(autoGenerate = false)
    var accountId: String = "",
    val userIdFk: String = "",
    val accountName: String = "Account",
    var accountAmount: Double = 0.0,
    var accountCurrency: String = "",
    val accountIcon: Int = R.drawable.ic_wallet_foreground,
    val accountIconDescription: String = "",
)

