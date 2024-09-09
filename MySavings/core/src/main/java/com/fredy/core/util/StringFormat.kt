package com.fredy.core.util


fun String.truncateString(maxLength: Int = 30): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength - 2) + ".."
    } else {
        this
    }
}