package com.fredy.analysis.domain.useCases

import com.fredy.domain.model.Record
import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.currency.domain.useCases.currencyConverter

data class AnalysisUseCases(
//    val upsertRecordItem: UpsertRecordItem,
    val deleteRecordItem: DeleteRecordItem,
    val getUserRecordsFromSpecificTime: GetUserRecordsFromSpecificTime, //analysis flow
    val getUserCategoriesWithAmountFromSpecificTime: GetUserCategoriesWithAmountFromSpecificTime,//analysis overview
    val getUserWalletsWithAmountFromSpecificTime: GetUserWalletsWithAmountFromSpecificTime,//analysis account
    val getUserTotalAmountByTypeFromSpecificTime: GetUserTotalAmountByTypeFromSpecificTime,
    val getUserTotalRecordBalance: GetUserTotalRecordBalance // balance bar total balance
)

