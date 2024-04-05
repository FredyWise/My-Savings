package com.fredy.mysavings.Feature.Data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fredy.mysavings.Feature.Data.Database.Converter.CurrencyInfoResponseConverter
import com.fredy.mysavings.Feature.Data.Database.Converter.CurrencyRatesConverter
import com.fredy.mysavings.Feature.Data.Database.Converter.CurrencyResponseConverter
import com.fredy.mysavings.Feature.Data.Database.Converter.DateTimeConverter
import com.fredy.mysavings.Feature.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Feature.Data.Database.Dao.AccountDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CategoryDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyCacheDao
import com.fredy.mysavings.Feature.Data.Database.Dao.CurrencyDao
import com.fredy.mysavings.Feature.Data.Database.Dao.RecordDao
import com.fredy.mysavings.Feature.Data.Database.Dao.UserDao
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.Currency
import com.fredy.mysavings.Feature.Data.Database.Model.RatesCache
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.UserData

@TypeConverters(value = [DateTimeConverter::class, TimestampConverter::class, CurrencyRatesConverter::class, CurrencyResponseConverter::class, CurrencyInfoResponseConverter::class])
@Database(
    entities = [Record::class, Account::class, Category::class, UserData::class, RatesCache::class, Currency::class],
    version = 1,
//    exportSchema = true,
//    autoMigrations = [
//        AutoMigration (
//            from = 1,
//            to = 2,
//        )
//    ]
)
abstract class SavingsDatabase : RoomDatabase() {
    abstract val recordDao: RecordDao
    abstract val accountDao: AccountDao
    abstract val categoryDao: CategoryDao
    abstract val userDao: UserDao
    abstract val currencyCache: CurrencyCacheDao
    abstract val currency: CurrencyDao
}

