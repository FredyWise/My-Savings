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
    suspend fun upsertCategoryItem(category: Category)

    @Upsert
    suspend fun upsertAllCategoryItem(categorys: List<Category>)

    @Delete
    suspend fun deleteCategoryItem(category: Category)

    @Query("DELETE FROM category")
    suspend fun deleteAllCategories()

    @Query(
        "SELECT * FROM category " +
                "WHERE categoryId=:categoryId"
    )
    suspend fun getCategory(categoryId: String):Category

    @Query(
        "SELECT * FROM category " +
                "WHERE userIdFk = :userId " +
                "ORDER BY categoryName ASC"
    )
    suspend fun getUserCategoriesOrderedByName(userId: String):List<Category>

    @Query(
        "SELECT * FROM category " +
                "WHERE categoryType =:type AND userIdFk = :userId " +
                "ORDER BY categoryName ASC"
    )
    suspend fun getCategoriesUsingTypeOrderedByName(
        userId: String,
        type: RecordType = RecordType.Expense
    ):List<Category>
}
