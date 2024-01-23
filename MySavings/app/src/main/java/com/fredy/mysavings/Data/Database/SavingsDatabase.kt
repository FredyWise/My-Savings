package com.fredy.mysavings.Data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fredy.mysavings.Data.Database.Converter.DateTimeConverter
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Data.Database.Dao.UserDao
import com.fredy.mysavings.Data.Database.Entity.Account
import com.fredy.mysavings.Data.Database.Entity.Category
import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Database.Entity.UserData

@TypeConverters(value = [DateTimeConverter::class,TimestampConverter::class])
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

