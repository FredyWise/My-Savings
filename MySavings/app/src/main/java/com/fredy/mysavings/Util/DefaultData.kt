package com.fredy.mysavings.Util

import android.graphics.Color
import androidx.compose.ui.graphics.Color as toColor

val CURRENCY_CONVERTER_URL = "https://api.apilayer.com/"
val CURRENCY_API_KEY = "ENXX0mTPZmDfoYwXTFQff6UN8ruMJTOH"
val TAG = "BABI"


val defaultColors = listOf(
    toColor(Color.parseColor("#4CAF50")), // Green
    toColor(Color.parseColor("#2196F3")), // Blue
    toColor(Color.parseColor("#FFC107")), // Amber
    toColor(Color.parseColor("#FF5722")), // Deep Orange
    toColor(Color.parseColor("#673AB7")), // Deep Purple
    toColor(Color.parseColor("#FF9800")), // Orange
    toColor(Color.parseColor("#3F51B5")), // Indigo
    toColor(Color.parseColor("#FFEB3B")), // Yellow
    toColor(Color.parseColor("#FF5252")), // Red
    toColor(Color.parseColor("#9C27B0")), // Purple
    toColor(Color.parseColor("#607D8B")), // Blue Gray
    toColor(Color.parseColor("#FFD740")), // Yellow Accent
    toColor(Color.parseColor("#03A9F4")), // Light Blue
    toColor(Color.parseColor("#E91E63")), // Pink
    toColor(Color.parseColor("#9E9E9E")), // Gray
    toColor(Color.parseColor("#FF6D00")), // Deep Orange Accent
    toColor(Color.parseColor("#00BCD4")), // Cyan
    toColor(Color.parseColor("#795548")), // Brown
    toColor(Color.parseColor("#8BC34A")), // Light Green
    toColor(Color.parseColor("#F44336"))  // Red
)

val currencyCodes = listOf(
    "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN",
    "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD",
    "BTC", "BTN", "BWP", "BYN", "BYR", "BZD", "CAD", "CDF", "CHF", "CLF", "CLP",
    "CNY", "COP", "CRC", "CUC", "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD",
    "EGP", "ERN", "ETB", "EUR", "FJD", "FKP", "GBP", "GEL", "GGP", "GHS", "GIP",
    "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS",
    "IMP", "INR", "IQD", "IRR", "ISK", "JEP", "JMD", "JOD", "JPY", "KES", "KGS",
    "KHR", "KMF", "KPW", "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD",
    "LSL", "LTL", "LVL", "LYD", "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP",
    "MRO", "MUR", "MVR", "MWK", "MXN", "MYR", "MZN", "NAD", "NGN", "NIO", "NOK",
    "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR",
    "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG", "SEK", "SGD", "SHP",
    "SLE", "SLL", "SOS", "SRD", "STD", "SYP", "SZL", "THB", "TJS", "TMT", "TND",
    "TOP", "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VEF",
    "VES", "VND", "VUV", "WST", "XAF", "XAG", "XAU", "XCD", "XDR", "XOF", "XPF",
    "YER", "ZAR", "ZMK", "ZMW", "ZWL"
)
