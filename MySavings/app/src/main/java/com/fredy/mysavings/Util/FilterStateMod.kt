package com.fredy.mysavings.Util

import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.ViewModels.FilterState
import java.time.LocalDateTime

fun <T>FilterState.map(target:(start:LocalDateTime,end:LocalDateTime,RecordType,SortType,currencies:List<String>)->T):T{
    return when (filterType) {
        FilterType.Daily -> target(start,end,recordType,sortType,currencies)

        FilterType.Weekly -> target(start,end,recordType,sortType,currencies)

        FilterType.Monthly -> target(start,end,recordType,sortType,currencies)

        FilterType.Per3Months -> target(start,end,recordType,sortType,currencies)

        FilterType.Per6Months -> target(start,end,recordType,sortType,currencies)

        FilterType.Yearly -> target(start,end,recordType,sortType,currencies)
    }
}