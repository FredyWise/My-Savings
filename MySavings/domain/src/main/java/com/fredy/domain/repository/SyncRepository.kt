package com.fredy.domain.repository

import com.fredy.domain.modelUI.FilterState
import kotlinx.coroutines.flow.StateFlow

interface SyncRepository {
    suspend fun syncBooks(withDelete: Boolean = true)
    suspend fun syncAccounts(withDelete: Boolean = true)
    suspend fun syncRecords(withDelete: Boolean = true)
    suspend fun syncCategory(withDelete: Boolean = true)
    suspend fun syncAll(withDelete: Boolean = true)


    val filterSettings: StateFlow<FilterState>
     fun saveFilterSettings(filterState: FilterState)
//     fun getFilterSettings(): StateFlow<FilterState>

}

