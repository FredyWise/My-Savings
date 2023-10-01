package com.fredy.mysavings.Data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.fredy.mysavings.R

data class Category(
    var name: String,
    var icon: CategoryIcons,
    var iconDescription: String = "",
    var iconColor: Color = Color.Unspecified,
)

enum class CategoryIcons {
    MASTERCARD,
    CREDIT_CARD,
//    MONEY,
}

@Composable
fun CategoryIcons(categoryIcon: CategoryIcons): Painter {
    return when (categoryIcon) {
        CategoryIcons.MASTERCARD -> painterResource(
            id = R.drawable.ic_mastercard
        )

        CategoryIcons.CREDIT_CARD -> painterResource(
            id = R.drawable.ic_visa
        )
//        CategoryIcons.MONEY ->
    }
}


