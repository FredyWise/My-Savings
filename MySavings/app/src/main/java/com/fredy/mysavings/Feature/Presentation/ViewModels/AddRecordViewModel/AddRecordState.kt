package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel

import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import java.time.LocalDate
import java.time.LocalTime

data class AddRecordState(
    val recordId: String = "",
    val accountIdFromFk: String? = null,
    val fromWallet: Wallet = Wallet(),
    val accountIdToFk: String? = null,
    val toWallet: Wallet = Wallet(),
    val categoryIdFk: String? = null,
    val bookIdFk: String = "",
    val toCategory: Category = Category(),
    val recordDate: LocalDate = LocalDate.now(),
    val recordTime: LocalTime = LocalTime.now(),
    val recordAmount: Double = 0.0,
    val previousAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordNotes: String = "",
    val recordType: RecordType = RecordType.Expense,
    val isShowWarning: Boolean = false,
    val isAgreeToConvert: Boolean = false,
)