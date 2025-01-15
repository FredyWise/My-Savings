package com.fredy.currency.util

import com.fredy.domain.model.Rate
import timber.log.Timber

fun List<Rate>.getValueFromCode(code: String): Double {
    Timber.d("Getting value from code: $this")
    return this.firstOrNull { it.code.contains(code) }?.value?: throw IllegalArgumentException(
        "Currency '$code' not found in rates."
    )
}
