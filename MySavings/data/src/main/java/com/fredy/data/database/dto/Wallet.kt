package com.fredy.data.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.data.util.DefaultData
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData.walletInitIcon

@Entity
data class Wallet(
    @PrimaryKey
    var walletId: String = "",
    val userIdFk: String = "",
    val walletName: String = "Wallet",
    var walletAmount: Double = 0.0,
    var walletCurrency: String = "",
    val walletIcon: Int = DefaultData.walletInitIcon.image,
    val walletIconDescription: String = DefaultData.walletInitIcon.description,
)

