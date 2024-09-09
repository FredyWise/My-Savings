package com.fredy.domain.repository

interface SyncRepository {
    suspend fun syncBooks(withDelete: Boolean = true)
    suspend fun syncAccounts(withDelete: Boolean = true)
    suspend fun syncRecords(withDelete: Boolean = true)
    suspend fun syncCategory(withDelete: Boolean = true)
    suspend fun syncAll(withDelete: Boolean = true)
}

