package com.fredy.mysavings.Feature.Data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fredy.data.database.converter.CurrencyRatesDoubleConverter
import com.fredy.data.database.converter.CurrencyResponseConverter
import com.fredy.data.database.converter.DateTimeConverter
import com.fredy.data.database.converter.TimestampConverter
import com.fredy.data.database.dao.WalletDao
import com.fredy.data.database.dao.BookDao
import com.fredy.data.database.dao.CategoryDao
import com.fredy.data.database.dao.CurrencyCacheDao
import com.fredy.data.database.dao.CurrencyDao
import com.fredy.data.database.dao.RecordDao
import com.fredy.data.database.dao.UserDao
import com.fredy.domain.model.Wallet
import com.fredy.domain.model.Book
import com.fredy.domain.model.Category
import com.fredy.domain.model.Currency
import com.fredy.domain.model.RatesCache
import com.fredy.domain.model.Record
import com.fredy.domain.model.UserData

@TypeConverters(value = [DateTimeConverter::class, TimestampConverter::class, CurrencyRatesDoubleConverter::class, CurrencyResponseConverter::class])
@Database(
    entities = [Record::class, Wallet::class, Category::class, UserData::class, RatesCache::class, Currency::class, Book::class],
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
    abstract val walletDao: WalletDao
    abstract val categoryDao: CategoryDao
    abstract val bookDao: BookDao
    abstract val userDao: UserDao
    abstract val currencyCache: CurrencyCacheDao
    abstract val currency: CurrencyDao
}

