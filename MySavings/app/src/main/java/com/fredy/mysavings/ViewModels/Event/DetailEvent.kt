package com.fredy.mysavings.ViewModels.Event

import com.fredy.mysavings.Data.Database.Entity.Record
import com.fredy.mysavings.Data.Enum.FilterType
import com.fredy.mysavings.Data.Repository.TrueRecord
import com.fredy.mysavings.ViewModels.DetailData
import com.fredy.mysavings.ViewModels.DetailType
import java.time.LocalDate

sealed interface DetailEvent{
    object ShowDialog: DetailEvent
    object HideDialog: DetailEvent
    data class Init(val detailData: DetailData): DetailEvent

}