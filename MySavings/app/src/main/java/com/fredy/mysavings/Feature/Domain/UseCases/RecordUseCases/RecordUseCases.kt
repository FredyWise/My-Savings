package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter

data class RecordUseCases(
    val upsertRecordItem: UpsertRecordItem,
    val deleteRecordItem: DeleteRecordItem,
    val updateRecordItemWithDeletedAccount: UpdateRecordItemWithDeletedAccount,
    val updateRecordItemWithDeletedCategory: UpdateRecordItemWithDeletedCategory,
    val updateRecordItemWithDeletedBook: UpdateRecordItemWithDeletedBook,
    val getRecordById: GetRecordById,
    val getAllTrueRecordsWithinSpecificTime: GetAllTrueRecordsWithinSpecificTime, //io
    val getAllRecords: GetAllRecords, //search
    val getUserCategoryRecordsOrderedByDateTime: GetUserCategoryRecordsOrderedByDateTime, // category
    val getUserAccountRecordsOrderedByDateTime: GetUserAccountRecordsOrderedByDateTime, // account
    val getUserTrueRecordMapsFromSpecificTime: GetUserTrueRecordMapsFromSpecificTime, // record main screen
    val getUserRecordsFromSpecificTime: GetUserRecordsFromSpecificTime, //analysis flow
    val getUserCategoriesWithAmountFromSpecificTime: GetUserCategoriesWithAmountFromSpecificTime,//analysis overview
    val getUserAccountsWithAmountFromSpecificTime: GetUserAccountsWithAmountFromSpecificTime,//analysis account
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



