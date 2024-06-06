package com.fredy.mysavings.Feature.Presentation.Util

fun String.truncateString( maxLength: Int): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength - 2) + ".."
    } else {
        this
    }
}