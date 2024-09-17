package com.fredy.domain.useCases.RecordUseCases

import com.fredy.domain.model.Record
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.currencyConverter

data class RecordUseCases(
    val upsertRecordItem: UpsertRecordItem,
    val deleteRecordItem: DeleteRecordItem,
    val updateRecordItemWithDeletedWallet: UpdateRecordItemWithDeletedWallet,
    val updateRecordItemWithDeletedCategory: UpdateRecordItemWithDeletedCategory,
    val updateRecordItemWithDeletedBook: UpdateRecordItemWithDeletedBook,
    val getRecordById: GetRecordById,
    val getAllTrueRecordsWithinSpecificTime: GetAllTrueRecordsWithinSpecificTime, //io
    val getAllBooks: GetAllRecords, //search
    val getUserCategoryRecordsOrderedByDateTime: GetUserCategoryRecordsOrderedByDateTime, // category
    val getUserWalletRecordsOrderedByDateTime: GetUserWalletRecordsOrderedByDateTime, // account
    val getUserTrueRecordMapsFromSpecificTime: GetUserTrueRecordMapsFromSpecificTime, // record main screen
    val getUserRecordsFromSpecificTime: GetUserRecordsFromSpecificTime, //analysis flow
    val getUserCategoriesWithAmountFromSpecificTime: GetUserCategoriesWithAmountFromSpecificTime,//analysis overview
    val getUserWalletsWithAmountFromSpecificTime: GetUserWalletsWithAmountFromSpecificTime,//analysis account
    val getUserTotalAmountByType: GetUserTotalAmountByType,
    val getUserTotalAmountByTypeFromSpecificTime: GetUserTotalAmountByTypeFromSpecificTime,
    val getUserTotalRecordBalance: GetUserTotalRecordBalance // balance bar total balance
)

suspend fun List<Record>.getTotalRecordBalance(
    currencyUseCases: CurrencyUseCases,
    userCurrency: String
): Double {
    return this.sumOf { record ->
        currencyUseCases.currencyConverter(
            record.recordAmount,
            record.recordCurrency,
            userCurrency
        )
    }
}



