package com.fredy.mysavings.Data

import com.fredy.mysavings.Data.Records.Item
import com.fredy.mysavings.Data.Records.Record
import com.fredy.mysavings.Data.User.Account
import com.fredy.mysavings.Data.User.Category
import com.fredy.mysavings.R
import java.time.LocalDate
import java.time.LocalTime

val tempCategories: MutableList<Category> = mutableListOf(
    Category(
        name = "Savings toCategory",
        icon = R.drawable.ic_mastercard,
    ), Category(
        name = "Credit Card",
        icon = R.drawable.ic_mastercard,
    )
)
val tempAccounts: MutableList<Account> = mutableListOf(
    Account(
        name = "Savings Account",
        icon = R.drawable.ic_mastercard,
        balance = Balance(
            amount = 1000f, currency = "USD"
        )
    ), Account(
        name = "Credit Card",
        icon = R.drawable.ic_visa,
        balance = Balance(
            amount = 1000f, currency = "USD"
        )
    ), Account(
        name = "Bitch Account",
        icon = R.drawable.ic_mastercard,
        balance = Balance(
            amount = 1000f, currency = "USD"
        )
    ), Account(
        name = "Ass Card",
        icon = R.drawable.ic_visa,
        balance = Balance(
            amount = -500f, currency = "USD"
        )
    ), Account(
        name = "tai Account",
        icon = R.drawable.ic_mastercard,
        balance = Balance(
            amount = -500f, currency = "USD"
        )
    ), Account(
        name = "babi Card",
        icon = R.drawable.ic_visa,
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = Account(
                    name = "Groceries",
                    icon = R.drawable.ic_visa,
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
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
                    icon = R.drawable.ic_mastercard,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Dining",
                    icon = R.drawable.ic_mastercard
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
                    icon = R.drawable.ic_visa,
                    balance = Balance()
                ),
                toAccount = null,
                toCategory = Category(
                    name = "Income",
                    icon = R.drawable.ic_visa
                ),
                notes = "Monthly salary"
            )
        )
    )
)