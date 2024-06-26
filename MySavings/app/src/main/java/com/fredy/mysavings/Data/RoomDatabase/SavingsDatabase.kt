package com.fredy.mysavings.Data.RoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fredy.mysavings.Data.RoomDatabase.Converter.DateTimeConverter
import com.fredy.mysavings.Data.RoomDatabase.Dao.AccountDao
import com.fredy.mysavings.Data.RoomDatabase.Dao.CategoryDao
import com.fredy.mysavings.Data.RoomDatabase.Dao.RecordDao
import com.fredy.mysavings.Data.RoomDatabase.Dao.UserDao
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record
import com.fredy.mysavings.Data.RoomDatabase.Entity.UserData

@TypeConverters(value = [DateTimeConverter::class])
@Database(
    entities = [Record::class, Account::class, Category::class, UserData::class],
    version = 1
)
abstract class SavingsDatabase: RoomDatabase() {
    abstract val recordDao: RecordDao
    abstract val accountDao: AccountDao
    abstract val categoryDao: CategoryDao
    abstract val userDao: UserDao
}

