package com.fredy.data.api.tabScannerModel.tabScannerDTO



data class ResultResponse(
    val code: Int,
    val message: String,
    val result: Result,
    val status: String,
    val status_code: Int,
    val success: Boolean
)