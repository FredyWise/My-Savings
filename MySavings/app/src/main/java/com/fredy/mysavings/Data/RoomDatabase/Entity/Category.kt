package com.fredy.mysavings.Data.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.R

@Entity
data class Category(
    @PrimaryKey(autoGenerate = false)
    var categoryId: String = "",
    val userIdFk: String = "",
    val categoryName: String = "Category",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = R.drawable.ic_category_foreground,
    val categoryIconDescription: String = "",
)




