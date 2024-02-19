package com.fredy.mysavings.Data.Mappers

import com.fredy.mysavings.Data.Database.FirebaseDataSource.RecordDataSourceImpl.TrueRecordComponentResult
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.ViewModels.RecordMap

fun List<Account>.getCurrencies():List<String>{
    return this.map { it.accountCurrency }.distinct()
}