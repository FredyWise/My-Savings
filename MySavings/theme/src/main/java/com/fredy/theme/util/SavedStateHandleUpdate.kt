package com.fredy.theme.util

import androidx.lifecycle.SavedStateHandle

fun <T> SavedStateHandle.update(key: String, updateFunction: (T) -> T) {
    this.get<T>(key)?.let { currentState ->
        this[key] = updateFunction(currentState)
    }
}
