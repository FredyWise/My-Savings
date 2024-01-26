package com.fredy.mysavings.Data.Database.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Enum.RecordType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun upsertCategory(category: Category)
    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM category " +
            "WHERE categoryId=:categoryId")
    fun getCategory(categoryId:Int): Flow<Category>

    @Query("SELECT * FROM category " +
            "ORDER BY categoryName ASC")
    fun getUserCategoriesOrderedByName(): Flow<List<Category>>
    @Query("SELECT * FROM category " +
            "WHERE categoryType =:type " +
            "ORDER BY categoryName ASC")
    fun getCategoriesUsingTypeOrderedByName(type: RecordType = RecordType.Expense): Flow<List<Category>>
}

//remember to change the query bro