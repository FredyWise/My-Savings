package com.fredy.mysavings.Util

import android.graphics.Color
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Book
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.R
import com.fredy.mysavings.ui.theme.md_theme_dark_primary
import com.fredy.mysavings.ui.theme.md_theme_dark_secondary
import com.fredy.mysavings.ui.theme.md_theme_dark_surface
import com.fredy.mysavings.ui.theme.md_theme_dark_tertiary
import com.fredy.mysavings.ui.theme.md_theme_light_primary
import com.fredy.mysavings.ui.theme.md_theme_light_secondary
import com.fredy.mysavings.ui.theme.md_theme_light_surface
import com.fredy.mysavings.ui.theme.md_theme_light_tertiary
import java.time.LocalDateTime
import androidx.compose.ui.graphics.Color as toColor

//icon
data class SavingsIcon(
    val image: Int,
    val description: String,
)

data class ActionWithName(
    val name: String, val action: () -> Unit
)

data class ValueWithName<T>(
    val name: String, val value: T
)

data class ToggleableInfo(
    val isChecked: Boolean, val text: String
)


object DefaultData {
    // icons

    val appIcon = SavingsIcon(R.drawable.app_icon, "Application Icon")
    val bookInitIcon = SavingsIcon(R.drawable.ic_book, "Book")
    val transferIcon = SavingsIcon(R.drawable.ic_exchange, "Transfer")
    val categoryInitIcon = SavingsIcon(R.drawable.ic_category_foreground, "Category")
    val accountInitIcon = SavingsIcon(R.drawable.ic_wallet_foreground, "Account")

    val accountIcons = listOf(
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


    val accountIconsMap = accountIcons.plus(accountInitIcon).associateBy { it.description }
    val categoryIconsMap =
        categoryIcons.plus(categoryInitIcon).plus(transferIcon).associateBy { it.description }
    val savingsIcons = accountIconsMap.plus(categoryIconsMap)


    val WebClientId = "895326687881-e2kh5jh12kjvpf9se1cehbeias0iuvmq.apps.googleusercontent.com"

    val transferCategory = Category(
        categoryId = "transferCategory",
        userIdFk = "0",
        categoryName = RecordType.Transfer.name,
        categoryType = RecordType.Transfer,
        categoryIcon = transferIcon.image,
        categoryIconDescription = transferIcon.description,
    )

    val deletedCategory = Category(
        categoryId = "deletedCategory",
        userIdFk = "0",
        categoryName = "Deleted Category",
        categoryType = RecordType.Transfer
    )

    val deletedAccount = Account(
        accountId = "deletedAccount",
        userIdFk = "0",
        accountName = "Deleted Account",
        accountCurrency = "USD"
    )

    val defaultBook = Book()
}

//date
val minDate = LocalDateTime.of(2000, 1, 1, 1, 1)
val maxDate = LocalDateTime.of(3000, 1, 1, 1, 1)

//color
val initialDarkThemeDefaultColor = md_theme_dark_surface
val initialLightThemeDefaultColor = md_theme_light_surface

val defaultDarkIncomeColor = md_theme_dark_tertiary
val defaultDarkExpenseColor = md_theme_dark_primary
val defaultDarkTransferColor = md_theme_dark_secondary

val defaultLightIncomeColor = md_theme_light_tertiary
val defaultLightExpenseColor = md_theme_light_primary
val defaultLightTransferColor = md_theme_light_secondary


val defaultColors = listOf(
    toColor(Color.parseColor("#ff0055")),
    toColor(Color.parseColor("#ff9955")),
    toColor(Color.parseColor("#ffcc88")),
    toColor(Color.parseColor("#ff0088")),
    toColor(Color.parseColor("#ff55ff")),
    toColor(Color.parseColor("#cc55ff")),
    toColor(Color.parseColor("#9955ff")),
    toColor(Color.parseColor("#0055ff")),
    toColor(Color.parseColor("#00bbff")),
    toColor(Color.parseColor("#99bbff")),
    toColor(Color.parseColor("#ccbbff")),
    toColor(Color.parseColor("#ffbbff")),
    toColor(Color.parseColor("#bbff00")),
    toColor(Color.parseColor("#bbffff")),
    toColor(Color.parseColor("#55ffff")),
    toColor(Color.parseColor("#55ff00")),
)

// currency
val currencyCodes = listOf(
    "AED",
    "AFN",
    "ALL",
    "AMD",
    "ANG",
    "AOA",
    "ARS",
    "AUD",
    "AWG",
    "AZN",
    "BAM",
    "BBD",
    "BDT",
    "BGN",
    "BHD",
    "BIF",
    "BMD",
    "BND",
    "BOB",
    "BRL",
    "BSD",
    "BTC",
    "BTN",
    "BWP",
    "BYN",
    "BYR",
    "BZD",
    "CAD",
    "CDF",
    "CHF",
    "CLF",
    "CLP",
    "CNY",
    "COP",
    "CRC",
    "CUC",
    "CUP",
    "CVE",
    "CZK",
    "DJF",
    "DKK",
    "DOP",
    "DZD",
    "EGP",
    "ERN",
    "ETB",
    "EUR",
    "FJD",
    "FKP",
    "GBP",
    "GEL",
    "GGP",
    "GHS",
    "GIP",
    "GMD",
    "GNF",
    "GTQ",
    "GYD",
    "HKD",
    "HNL",
    "HRK",
    "HTG",
    "HUF",
    "IDR",
    "ILS",
    "IMP",
    "INR",
    "IQD",
    "IRR",
    "ISK",
    "JEP",
    "JMD",
    "JOD",
    "JPY",
    "KES",
    "KGS",
    "KHR",
    "KMF",
    "KPW",
    "KRW",
    "KWD",
    "KYD",
    "KZT",
    "LAK",
    "LBP",
    "LKR",
    "LRD",
    "LSL",
    "LTL",
    "LVL",
    "LYD",
    "MAD",
    "MDL",
    "MGA",
    "MKD",
    "MMK",
    "MNT",
    "MOP",
    "MRO",
    "MUR",
    "MVR",
    "MWK",
    "MXN",
    "MYR",
    "MZN",
    "NAD",
    "NGN",
    "NIO",
    "NOK",
    "NPR",
    "NZD",
    "OMR",
    "PAB",
    "PEN",
    "PGK",
    "PHP",
    "PKR",
    "PLN",
    "PYG",
    "QAR",
    "RON",
    "RSD",
    "RUB",
    "RWF",
    "SAR",
    "SBD",
    "SCR",
    "SDG",
    "SEK",
    "SGD",
    "SHP",
    "SLE",
    "SLL",
    "SOS",
    "SRD",
    "STD",
    "SYP",
    "SZL",
    "THB",
    "TJS",
    "TMT",
    "TND",
    "TOP",
    "TRY",
    "TTD",
    "TWD",
    "TZS",
    "UAH",
    "UGX",
    "USD",
    "UYU",
    "UZS",
    "VEF",
    "VES",
    "VND",
    "VUV",
    "WST",
    "XAF",
    "XAG",
    "XAU",
    "XCD",
    "XDR",
    "XOF",
    "XPF",
    "YER",
    "ZAR",
    "ZMK",
    "ZMW",
    "ZWL"
)


