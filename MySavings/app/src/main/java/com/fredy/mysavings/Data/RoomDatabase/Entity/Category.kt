package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Data.RoomDatabase.Enum.CategoryType

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val categoryName: String = "",
    val categoryType: CategoryType = CategoryType.Expense,
    val categoryIcon: Int = 0,
    val categoryIconDescription: String = "",
)




