package com.fredy.data.api.textCorrectionModels.textCorrectionDTO

data class TextCorrectionRequest(
    val token: String,
    val languages: List<String>,
    val text: String,
    val keyboard: String,
    val remove_low_prob_tokens: Boolean
)