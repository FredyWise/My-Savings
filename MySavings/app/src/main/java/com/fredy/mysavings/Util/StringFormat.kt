package com.fredy.mysavings.Util

fun truncateString(inputString: String, maxLength: Int): String {
    return if (inputString.length > maxLength) {
        inputString.substring(0, maxLength - 2) + ".."
    } else {
        inputString
    }
}