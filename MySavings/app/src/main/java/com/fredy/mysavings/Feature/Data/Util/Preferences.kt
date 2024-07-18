package com.fredy.mysavings.Feature.Data.Util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class Preferences(val context: Context) {
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            "Settings"
        )
    }
    fun <T> getPreference(key: Key<T>): Flow<T?> {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    fun <T> getPreference(key: Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun <T> savePreference(
        key: Key<T>,
        value: T?
    ) {
        context.dataStore.edit { preferences ->
            value?.let {
                preferences[key] = value
            } ?: preferences.remove(key)
        }
    }
}