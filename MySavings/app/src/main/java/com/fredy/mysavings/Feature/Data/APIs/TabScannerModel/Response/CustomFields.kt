package com.fredy.mysavings.Feature.Data.APIs.TabScannerModel.Response

data class CustomFields(
    val CardLast4Digits: String,
    val Country: String,
    val Currency: String,
    val ExpenseType: String,
    val PaymentMethod: String,
    val StoreID: String,
    val URL: String,
    val VATNumber: String
)