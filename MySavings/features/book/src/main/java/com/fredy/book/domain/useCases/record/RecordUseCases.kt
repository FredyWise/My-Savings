package com.fredy.book.domain.useCases.record

import com.fredy.book.domain.useCases.book.UpdateRecordItemWithDeletedBook
import com.fredy.domain.model.Record


data class RecordUseCases(
//    val upsertRecordItem: UpsertRecordItem,
    val deleteRecordItem: DeleteRecordItem,
//    val getRecordById: GetRecordById,
    val getWalletsCurrencies: GetWalletsCurrencies,
    val getUserTrueRecordMapsFromSpecificTime: GetUserTrueRecordMapsFromSpecificTime, // record main screen
//    val getUserRecordsFromSpecificTime: GetUserRecordsFromSpecificTime, //analysis flow
//    val getUserCategoriesWithAmountFromSpecificTime: GetUserCategoriesWithAmountFromSpecificTime,//analysis overview
//    val getUserWalletsWithAmountFromSpecificTime: GetUserWalletsWithAmountFromSpecificTime,//analysis account
    val getUserTotalAmountByTypeFromSpecificTime: GetUserTotalAmountByTypeFromSpecificTime,
    val getUserTotalRecordBalance: GetUserTotalRecordBalance // balance bar total balance
)





