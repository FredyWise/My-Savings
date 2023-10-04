package com.fredy.mysavings.Data.User

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.fredy.mysavings.Data.Balance
import com.fredy.mysavings.R

data class Category(
    var name: String = "Category",
    var balance: Balance = Balance(),
    var icon: CategoryIcons = CategoryIcons.DEFAULT,
    var iconDescription: String = "",
    var iconColor: Color = Color.Unspecified,
)

enum class CategoryIcons {
    DEFAULT,
    MASTER_CARD,
    CREDIT_CARD,
//    MONEY,
}

@Composable
fun CategoryIcons(categoryIcon: CategoryIcons): Painter {
    return when (categoryIcon) {
        CategoryIcons.DEFAULT -> painterResource(
            id = R.drawable.ic_tag
        )

        CategoryIcons.MASTER_CARD -> painterResource(
            id = R.drawable.ic_mastercard
        )

        CategoryIcons.CREDIT_CARD -> painterResource(
            id = R.drawable.ic_visa
        )
//        CategoryIcons.MONEY ->
    }
}


