package com.fredy.mysavings.Feature.Data.APIs.TabScannerModel.Response

data class SummaryItem(
    val customFields: CustomFields,
    val desc: String,
    val descClean: String,
    val discount: String,
    val lineTotal: String,
    val lineType: String,
    val price: String,
    val productCode: String,
    val qty: Int,
    val symbols: List<String>,
    val unit: String
)