package com.fredy.mysavings.Util

import android.graphics.Color
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.R
import com.fredy.mysavings.ui.theme.md_theme_dark_primary
import com.fredy.mysavings.ui.theme.md_theme_dark_secondary
import com.fredy.mysavings.ui.theme.md_theme_dark_surface
import com.fredy.mysavings.ui.theme.md_theme_dark_tertiary
import com.fredy.mysavings.ui.theme.md_theme_light_surface
import androidx.compose.ui.graphics.Color as toColor

//icon
data class SavingsIcon(
    val image: Int,
    val description: String,
)

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
val categoryIconsMap = categoryIcons.plus(categoryInitIcon).plus(transferIcon).associateBy { it.description }
val savingsIcons = accountIconsMap.plus(categoryIconsMap)



val appIcon = SavingsIcon(R.drawable.ic_wallet_foreground, "Application Icon")
val TAG = "BABI"
val WebClientId = "895326687881-e2kh5jh12kjvpf9se1cehbeias0iuvmq.apps.googleusercontent.com"


val transferCategory = Category(
    categoryId = "transferCategory",
    categoryName = RecordType.Transfer.name,
    categoryType = RecordType.Transfer,
    categoryIcon = transferIcon.image,
    categoryIconDescription = transferIcon.description,
)
val deletedCategory = Category(categoryId = "deletedCategory", categoryName = "Deleted Category", categoryType = RecordType.Transfer)
val deletedAccount = Account(accountId = "deletedAccount", accountName = "Deleted Account", accountCurrency = "USD")


//color
val initialDarkThemeDefaultColor = md_theme_dark_surface
val initialLightThemeDefaultColor = md_theme_light_surface

val defaultIncomeColor = md_theme_dark_tertiary
val defaultExpenseColor = md_theme_dark_primary
val defaultTransferColor = md_theme_dark_secondary


val defaultColors = listOf(
    toColor(Color.parseColor("#fff3cf")),
    toColor(Color.parseColor("#f49acf")),
    toColor(Color.parseColor("#ff4d7f")),
    toColor(Color.parseColor("#ff527a")),
    toColor(Color.parseColor("#ff5253")),
    toColor(Color.parseColor("#cc52c3")),
    toColor(Color.parseColor("#aa52e9")),
    toColor(Color.parseColor("#5452f5")),
    toColor(Color.parseColor("#5201fa")),
    toColor(Color.parseColor("#8239fb")),
    toColor(Color.parseColor("#5269fc")),
    toColor(Color.parseColor("#5298fd")),
    toColor(Color.parseColor("#52c6ff")),
    toColor(Color.parseColor("#52fffb")),
    toColor(Color.parseColor("#52ff8f")),
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

data class ActionWithName(
    val name: String, val action: () -> Unit
)

data class ValueWithName<T>(
    val name: String, val value: T
)

data class ToggleableInfo(
    val isChecked: Boolean, val text: String
)

fun <T, U> listConverter(
    items: List<T>, convertFunction: (T) -> U
): List<U> {
    return items.map { item ->
        convertFunction(item)
    }
}

