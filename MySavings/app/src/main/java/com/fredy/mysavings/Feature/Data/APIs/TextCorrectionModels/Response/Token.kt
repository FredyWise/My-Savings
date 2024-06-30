package com.fredy.mysavings.Feature.Data.APIs.TextCorrectionModels.Response

data class Token(
    val chars_to_replace: Int,
    val correctionType: String,
    val is_in_dictionary: Boolean,
    val original_word: String,
    val start_index: Int,
    val suggestions: List<Suggestion>,
    val underline_choice: String
)