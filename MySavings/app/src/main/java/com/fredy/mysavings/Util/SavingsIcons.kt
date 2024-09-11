package com.fredy.mysavings.Util

import com.fredy.mysavings.R
import com.fredy.core.model.SavingsIcon

object SavingsIcons {
    val appIcon = SavingsIcon(R.drawable.app_icon, "Application Icon")
    val bookInitIcon = SavingsIcon(R.drawable.ic_book, "Book")
    val transferIcon = SavingsIcon(R.drawable.ic_exchange, "Transfer")
    val categoryInitIcon = SavingsIcon(R.drawable.ic_category_foreground, "Category")
    val walletInitIcon = SavingsIcon(R.drawable.ic_wallet_foreground, "Wallet")

    val walletIcons = listOf(
        SavingsIcon(R.drawable.ic_mastercard, "Master Card"),
        SavingsIcon(R.drawable.ic_visa, "Visa"),
        SavingsIcon(R.drawable.ic_alipay, "Alipay"),
        SavingsIcon(R.drawable.ic_gpay, "Google pay"),
        SavingsIcon(R.drawable.ic_bit_coin, "Bit Coin"),
        SavingsIcon(R.drawable.ic_line, "Line"),
        SavingsIcon(R.drawable.ic_money_coin, "Coin"),
        SavingsIcon(R.drawable.ic_money_paper, "Paper"),
        SavingsIcon(R.drawable.ic_money_wallet, "Wallet"),
        SavingsIcon(R.drawable.ic_piggy, "Piggy"),
        SavingsIcon(R.drawable.ic_teller, "Teller"),
        SavingsIcon(R.drawable.ic_wechat, "Wechat"),
        SavingsIcon(R.drawable.ic_wallet, "Big Wallet"),
        SavingsIcon(R.drawable.ic_paypal, "Pay Pal"),
        SavingsIcon(R.drawable.ic_withdrawal, "Withdrawal"),
    )

    val categoryIcons = listOf(
        SavingsIcon(R.drawable.ic_fruit, "Fruit"),
        SavingsIcon(R.drawable.ic_beer, "Beer"),
        SavingsIcon(R.drawable.ic_book, "Book"),
        SavingsIcon(R.drawable.ic_jewelery, "Jewelery"),
        SavingsIcon(R.drawable.ic_graduation, "Graduation"),
        SavingsIcon(R.drawable.ic_cake, "Cake"),
        SavingsIcon(R.drawable.ic_junk_food, "Junk Food"),
        SavingsIcon(R.drawable.ic_makeup, "Make Up"),
        SavingsIcon(R.drawable.ic_ramen_soup, "Food"),
        SavingsIcon(R.drawable.ic_shoes, "Shoes"),
        SavingsIcon(R.drawable.ic_sweets, "Sweets"),
        SavingsIcon(R.drawable.ic_voucher, "Voucher"),
    )

    val allSavingsIcons = walletIcons + categoryIcons

    val walletIconsMap = walletIcons.plus(walletInitIcon).associateBy { it.description }

    val categoryIconsMap =
        categoryIcons.plus(categoryInitIcon).plus(transferIcon).associateBy { it.description }

    val savingsIcons = walletIconsMap.plus(categoryIconsMap)

    // DefaultIcons
    val AddCircleOutlineIcon = SavingsIcon(R.drawable.ic_add_foreground, "AddCircleOutline")

}