package com.fredy.mytest.APIs.TextCorrectionModule.Response

data class TextCorrectionResponse(
    val corrected_text: String,
    val language: String,
    val original_text: String,
    val remark: String,
    val tokens: List<Token>
)