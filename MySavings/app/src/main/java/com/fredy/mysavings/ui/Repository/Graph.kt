package com.fredy.mysavings.ui.Repository

import android.content.Context
import com.fredy.mysavings.Data.RoomDatabase.SavingsDatabase

object Graph {
    private lateinit var savingsDatabase: SavingsDatabase

    val recordRepository by lazy {
        RecordRepositoryImpl(savingsDatabase.recordDao())
    }

    val accountRepository by lazy {
        AccountRepositoryImpl(savingsDatabase.accountDao())
    }

    val categoryRepository by lazy {
        CategoryRepositoryImpl(savingsDatabase.categoryDao())
    }

    fun provideAppContext(appContext: Context) {
        savingsDatabase = SavingsDatabase.getDatabase(appContext.applicationContext)
    }
}

