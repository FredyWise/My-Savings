package com.fredy.mysavings.Feature.Data.APIs.TextCorrectionModels.Response

import com.fredy.data.api.textCorrectionModels.textCorrectionDTO.Token

data class TextCorrectionResponse(
    val corrected_text: String,
    val language: String,
    val original_text: String,
    val remark: String,
    val tokens: List<Token>
)