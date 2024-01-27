package com.fredy.mysavings.Data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fredy.mysavings.Data.Database.Converter.CurrencyRatesConverter
import com.fredy.mysavings.Data.Database.Converter.CurrencyResponseConverter
import com.fredy.mysavings.Data.Database.Converter.DateTimeConverter
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Data.Database.Dao.UserDao
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.CurrencyCache
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.UserData

@TypeConverters(value = [DateTimeConverter::class,TimestampConverter::class, CurrencyRatesConverter::class, CurrencyResponseConverter::class])
@Database(
    entities = [Record::class, Account::class, Category::class, UserData::class, CurrencyCache::class],
    version = 1
)
abstract class SavingsDatabase: RoomDatabase() {
    abstract val recordDao: RecordDao
    abstract val accountDao: AccountDao
    abstract val categoryDao: CategoryDao
    abstract val userDao: UserDao
    abstract val currencyCache: CurrencyCacheDao
}

