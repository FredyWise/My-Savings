package com.fredy.mysavings.Feature.Data.APIs.TextCorrectionModels.Response

data class TextCorrectionResponse(
    val corrected_text: String,
    val language: String,
    val original_text: String,
    val remark: String,
    val tokens: List<Token>
)