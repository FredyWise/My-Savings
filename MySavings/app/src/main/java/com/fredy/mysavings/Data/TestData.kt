package com.fredy.mysavings.Data

import com.fredy.mysavings.Data.Records.Item
import com.fredy.mysavings.Data.Records.Record
import com.fredy.mysavings.Data.User.Account
import com.fredy.mysavings.Data.User.AccountIcons
import com.fredy.mysavings.Data.User.Category
import com.fredy.mysavings.Data.User.CategoryIcons
import java.time.LocalDate
import java.time.LocalTime

val tempCategories: List<Category> = listOf(
    Category(
        name = "Savings toCategory",
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
        balance = Balance(
            amount = 1000f, currency = "USD"
        )
    ), Account(
        name = "Credit Card",
        icon = AccountIcons.MASTER_CARD,
        balance = Balance(
            amount = 1000f, currency = "USD"
        )
    ),Account(
        name = "Bitch Account",
        icon = AccountIcons.CREDIT_CARD,
        balance = Balance(
            amount = 1000f, currency = "USD"
        )
    ), Account(
        name = "Ass Card",
        icon = AccountIcons.MASTER_CARD,
        balance = Balance(
            amount = -500f, currency = "USD"
        )
    ),Account(
        name = "tai Account",
        icon = AccountIcons.CREDIT_CARD,
        balance = Balance(
            amount = -500f, currency = "USD"
        )
    ), Account(
        name = "babi Card",
        icon = AccountIcons.MASTER_CARD,
        balance = Balance(
            amount = -500f, currency = "USD"
        )
    )
)
val tempRecords = listOf(
    Record(
        date = LocalDate.now(), items = listOf(
            Item(
                balance = Balance(
                    amount = 1000000f,
                    isTransfer = true,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Savings",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = Account(
                    name = "Groceries",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toCategory = null,
                notes = "Grocery shopping"
            ),
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    ), Record(
        date = LocalDate.of(2020, 12, 23),
        items = listOf(
            Item(
                balance = Balance(
                    amount = -50f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Credit Card",
                    icon = AccountIcons.CREDIT_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = CategoryIcons.CREDIT_CARD
                ),
                notes = "Restaurant"
            ), Item(
                balance = Balance(
                    amount = 500f,
                    currency = "USD"
                ),
                time = LocalTime.now(),
                account = Account(
                    name = "Salary",
                    icon = AccountIcons.MASTER_CARD,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = CategoryIcons.MASTER_CARD
                ),
                notes = "Monthly salary"
            )
        )
    )
)