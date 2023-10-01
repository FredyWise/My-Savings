package com.fredy.mysavings.Data

import com.fredy.mysavings.Data.Records.Item
import com.fredy.mysavings.Data.Records.Record
import java.time.LocalDate
import java.time.LocalTime

val tempCategories: List<Category> = listOf(
    Category(
        name = "Savings Category",
        icon = CategoryIcons.CREDIT_CARD,
    ), Category(
        name = "Credit Card",
        icon = CategoryIcons.CREDIT_CARD,
    )
)

val tempAccounts: List<Account> = listOf(
    Account(
        name = "Savings Account",
        icon = AccountIcons.CREDIT_CARD,
        amount = 1000.0,
        currency = "USD"
    ), Account(
        name = "Credit Card",
        icon = AccountIcons.MASTER_CARD,
        amount = -500.0,
        currency = "USD"
    )
)

val tempRecords = listOf(
    Record(
        date = LocalDate.now(), items = listOf(
            Item(
                amount = 100.0,
                currency = "USD",
                time = LocalTime.now(),
                account = Account(
                    name = "Savings",
                    icon = AccountIcons.MASTER_CARD,
                    currency = "USD"
                ),
                toAccount = null,
                category = Category(
                    name = "Groceries",
                    icon = CategoryIcons.MASTERCARD
                ),
                notes = "Grocery shopping"
            ),
        )
    ), Record(
        date = LocalDate.of(2020,12,23), items = listOf(
            Item(
                amount = -50.0,
                currency = "USD",
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    currency = "USD"
                ),
                toAccount = null,
                category = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                amount = 500.0,
                currency = "USD",
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    currency = "USD"
                ),
                toAccount = null,
                category = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTERCARD
                ),
                notes = "Monthly salary"
            )
        )
    )
)