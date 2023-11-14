package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.R

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val categoryName: String = "Category",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = R.drawable.ic_category_foreground,
    val categoryIconDescription: String = "",
)




