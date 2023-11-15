package com.fredy.mysavings.Data.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fredy.mysavings.Data.RoomDatabase.Converter.DateTimeConverter
import com.fredy.mysavings.Data.RoomDatabase.Dao.AccountDao
import com.fredy.mysavings.Data.RoomDatabase.Dao.CategoryDao
import com.fredy.mysavings.Data.RoomDatabase.Dao.RecordDao
import com.fredy.mysavings.Data.RoomDatabase.Entity.Account
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Entity.Record

@TypeConverters(value = [DateTimeConverter::class])
@Database(
    entities = [ Record::class, Account::class, Category::class],
    version = 1
)
abstract class SavingsDatabase: RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao

    companion object{
        @Volatile
        var INSTANCE: SavingsDatabase? = null
        fun getDatabase(context: Context): SavingsDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    SavingsDatabase::class.java,
                    "savings_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }

}
